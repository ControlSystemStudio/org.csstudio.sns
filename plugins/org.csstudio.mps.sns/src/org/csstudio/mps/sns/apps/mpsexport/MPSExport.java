package org.csstudio.mps.sns.apps.mpsexport;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.application.JeriDataModule;
import org.csstudio.mps.sns.tools.data.MPSBoard;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Types;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import javax.sql.DataSource;

/**
 * Holds the functionality of the MPS export interface.
 * 
 * @author Chris Fowlkes
 */
public class MPSExport extends JeriDataModule 
{

  /**
   * Creates a new <CODE>MPSExport</CODE>.
   */
  public MPSExport()
  {
  }

  /**
   * Creates the contents for the db to contain the data given.
   * 
   * @param chassisBoards The data to include in the db file.
   * @param allChannels Pass as <CODE>true</CODE> if the user has selected all channels in the interface.
   * @return The contents of the .db files.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public String createDBFile(MPSBoard[] chassisBoards, boolean allChannels) throws java.sql.SQLException//, java.io.IOException
  {
    String fileContents;
    Connection oracleConnection = getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("{? = call ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".EPICS_MPS_PKG.EXPAND_MPS_INPUT_TEMPLATE(?, ?)}");
      CallableStatement expandTemplateStatement = oracleConnection.prepareCall(sql.toString());
      try
      {
        expandTemplateStatement.registerOutParameter(1, Types.CLOB);
        String[] deviceIDs = new String[chassisBoards.length];
        for(int i=0;i<deviceIDs.length;i++)
          deviceIDs[i] = chassisBoards[i].getID();
        ArrayDescriptor arrayDescription = ArrayDescriptor.createDescriptor(MPSBrowserView.SCHEMA + ".DVC_ID_TAB_TYP", oracleConnection);
        ARRAY deviceIDArray = new ARRAY(arrayDescription, oracleConnection, deviceIDs);
        expandTemplateStatement.setArray(2, deviceIDArray);
        if(allChannels)
          expandTemplateStatement.setString(3, "A");
        else
          expandTemplateStatement.setString(3, "I");
        expandTemplateStatement.execute();
        Clob returnValue = expandTemplateStatement.getClob(1);
        fileContents = returnValue.getSubString(1, (int)returnValue.length());
      }//try
      finally
      {
        expandTemplateStatement.close();
      }//finally
    }//try
    finally
    {
      oracleConnection.close();
    }//finally
    return fileContents;
  }

  /**
   * Exposing the datasource property.
   * 
   * @param newDataSource The <CODE>DataSource</CODE> to use to connect to the database.
   */
  public void setDataSource(DataSource newDataSource)
  {
    super.setDataSource(newDataSource);
  }
}