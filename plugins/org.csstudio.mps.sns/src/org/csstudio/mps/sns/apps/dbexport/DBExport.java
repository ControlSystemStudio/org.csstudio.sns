package org.csstudio.mps.sns.apps.dbexport;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.application.JeriDataModule;
import org.csstudio.mps.sns.tools.data.DBFile;
import org.csstudio.mps.sns.tools.data.EpicsRecordType;
import org.csstudio.mps.sns.tools.data.Signal;
import org.csstudio.mps.sns.tools.data.SignalField;
import org.csstudio.mps.sns.tools.data.SignalFieldType;
import org.csstudio.mps.sns.tools.data.SignalType;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import javax.sql.DataSource;

/**
 * Provides the functionality and data access for the Db export interface.
 * 
 * @author Chris Fowlkes
 */
public class DBExport extends JeriDataModule 
{
  /**
   * Creqates a new <CODE>DBExport</CODE>.
   */
  public DBExport()
  {
  }

  /**
   * Loads the names of the DB files from the database.
   * 
   * @return The DB fie names from the database.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public String[] loadDBFileNames() throws java.sql.SQLException
  {
    setMessage("Loading DB file list from RDB");
    setProgressIndeterminate(true);
    Connection oracleConnection = getConnection();
    try
    {
      Statement query = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      try
      {
        StringBuffer sql = new StringBuffer("SELECT EXT_SRC_FILE_NM FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".IOC_DB_FILE_ASGN GROUP BY EXT_SRC_FILE_NM ORDER BY EXT_SRC_FILE_NM");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          if(result.last())
            setProgressMaximum(result.getRow());
          else
            setProgressMaximum(0);
          result.beforeFirst();
          ArrayList fileNameData = new ArrayList();
          int progress = 0;
          setProgressValue(0);
          setProgressIndeterminate(false);
          while(result.next())
          {
            fileNameData.add(result.getString("EXT_SRC_FILE_NM"));
            setProgressValue(++progress);
            if(isCanceled())
              break;
          }
          String[] fileNames = new String[fileNameData.size()];
          fileNameData.toArray(fileNames);
          return fileNames;
        }
        finally
        {
          result.close();
        }
      }
      finally
      {
        query.close();
      }
    }
    finally
    {
      clearProgress();
      oracleConnection.close();
    }
  }

  /**
   * Exposing the data source property.
   * 
   * @param dataSource The <CODE>DataSource</CODE> to use to connect to the database.
   */
  public void setDataSource(DataSource dataSource)
  {
    super.setDataSource(dataSource);
  }
  
  /**
   * Exposing the data source property.
   * 
   * @return The <CODE>DataSource</CODE> to use to connect to the database.
   */
  public DataSource getDataSource()
  {
    return super.getDataSource();
  }

  /**
   * Loads and returns the instances of <CODE>Signal</CODE> associated with the
   * given DB file in the database. The instances of <CODE>Signal</CODE> will be 
   * loaded into the instances of <CODE>DBFile</CODE> passed into the method.
   * All <CODE>Signal</CODE> references in the instances of <CODE>DBFile</CODE>
   * when the method is invoked will be removed.
   * 
   * @param dbFiles The instances of  <CODE>DBFile</CODE> for which to load the signal associations.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void loadSignals(DBFile[] dbFiles) throws java.sql.SQLException
  {
    try
    {
      setProgressIndeterminate(true);
      setMessage("Loading signal data from RDB");
      Connection oracleConnection = getDataSource().getConnection();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        StringBuffer whereClause = new StringBuffer(MPSBrowserView.SCHEMA);
        whereClause.append(".IOC_DB_FILE_ASGN_SGNL, ");
        whereClause.append(MPSBrowserView.SCHEMA);
        whereClause.append(".SGNL_REC, ");
        whereClause.append(MPSBrowserView.SCHEMA);
        whereClause.append(".SGNL_FLD WHERE IOC_DB_FILE_ASGN_SGNL.SGNL_ID = SGNL_REC.SGNL_ID AND SGNL_REC.SGNL_ID = SGNL_FLD.SGNL_ID(+) AND SGNL_REC.REC_TYPE_ID = SGNL_FLD.REC_TYPE_ID(+) AND (");
        for(int i=0;i<dbFiles.length;i++)
        {
          if(i > 0)
            whereClause.append(" OR ");
          whereClause.append("IOC_DB_FILE_ASGN_SGNL.EXT_SRC_FILE_NM = ?");
        }
        whereClause.append(")");
        sql.append(whereClause);
        PreparedStatement countQuery = oracleConnection.prepareStatement(sql.toString());
        try
        {
          for(int i=0;i<dbFiles.length;i++)
            countQuery.setString(i + 1, dbFiles[i].getFileName());
          ResultSet result = countQuery.executeQuery();
          try
          {
            result.next();
            setProgressMaximum(result.getInt(1));
          }
          finally
          {
            result.close();
          }
        }
        finally
        {
          countQuery.close();
        }
        sql = new StringBuffer("SELECT IOC_DB_FILE_ASGN_SGNL.EXT_SRC_FILE_NM, SGNL_REC.SGNL_ID, SGNL_REC.REC_TYPE_ID, FLD_ID, FLD_VAL FROM ");
        sql.append(whereClause);
        sql.append(" ORDER BY IOC_DB_FILE_ASGN_SGNL.EXT_SRC_FILE_NM");
        PreparedStatement query = oracleConnection.prepareStatement(sql.toString());
        try
        {
          for(int i=0;i<dbFiles.length;i++)
          {
            query.setString(i + 1, dbFiles[i].getFileName());
            dbFiles[i].removeAllSignals();
          }
          ResultSet result = query.executeQuery();
          try
          {
            DBFile currentDBFile = null;
            String currentDBFileName = null;
            setProgressValue(0);
            int progress = 0;
            setProgressIndeterminate(false);
            while(result.next())
            {
              String newDBFileName = result.getString("EXT_SRC_FILE_NM");
              if(currentDBFileName == null || ! currentDBFileName.equals(newDBFileName))
                for(int i=0;i<dbFiles.length;i++) 
                  if(dbFiles[i].getFileName().equals(newDBFileName))
                  {
                    currentDBFile = dbFiles[i];
                    currentDBFileName = newDBFileName;
                    break;
                  }
              String currentSignalID = result.getString("SGNL_ID");
              Signal currentSignal = currentDBFile.getSignal(currentSignalID);
              if(currentSignal == null)
              {
                currentSignal = new Signal(currentSignalID);
                //Set epics record type.
                String currentRecordTypeID = result.getString("REC_TYPE_ID");
                EpicsRecordType currentEpicsRecordType = new EpicsRecordType(currentRecordTypeID);
                SignalType currentSignalType = new SignalType();
                currentSignalType.setRecordType(currentEpicsRecordType);
                currentSignal.setType(currentSignalType);
                //Add the signal to the current DB file.
                currentDBFile.addSignal(currentSignal);
                //Make note of the current Signal ID.
              }
              String currentValue = result.getString("FLD_VAL");
              SignalField currentField = new SignalField(currentValue);
              String currentFieldID = result.getString("FLD_ID");
              SignalFieldType currentFieldType = new SignalFieldType(currentFieldID);
              currentField.setType(currentFieldType);
              currentSignal.addField(currentField);
              setProgressValue(++progress);
              if(isCanceled())
                break;
            }
          }
          finally
          {
            result.close();
          }
        }
        finally
        {
          query.close();
        }
      }
      finally
      {
        oracleConnection.close();
      }
    }
    finally
    {
      clearProgress();
    }
  }
}