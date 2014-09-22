package org.csstudio.mps.sns.apps.dbexport;

import org.csstudio.mps.sns.apps.dbexport.DBExport;
import org.csstudio.mps.sns.tools.data.DBFile;
import org.csstudio.mps.sns.tools.data.Signal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.csstudio.mps.sns.test.JDBCTestFixture;
import org.csstudio.mps.sns.view.MPSBrowserView;
public class DBExportTester extends TestCase 
{
  private JDBCTestFixture jdbcFixture;
  private DBExport dbExport;

  public DBExportTester(String sTestName)
  {
    super(sTestName);
  }

  public void setUp() throws Exception
  {
    jdbcFixture = JDBCTestFixture.getInstance();
    jdbcFixture.setUp();
    dbExport = new DBExport();
    dbExport.setDataSource(jdbcFixture.getDataSource());
  }

  public void tearDown() throws Exception
  {
    jdbcFixture.tearDown();
  }

  /**
   * String[] loadDBFileNames()
   */
  public void testloadDBFileNames() throws java.sql.SQLException
  {
    String[] actualFileNamesArray = dbExport.loadDBFileNames();
    ArrayList expectedFileNames = loadDBFileNames();
    StringBuffer message = new StringBuffer("Incorrect number of DB files loaded.");
    assertEquals(message.toString(), expectedFileNames.size(), actualFileNamesArray.length);
    List actualFileNames = Arrays.asList(actualFileNamesArray);
    for(int i=0;i<actualFileNamesArray.length;i++) 
    {
      String fileName = expectedFileNames.get(i).toString();
      message = new StringBuffer("File name '");
      message.append(fileName);
      message.append("' not loaded from database.");
      assertTrue(message.toString(), actualFileNames.contains(fileName));
    }
  }

  /**
   *  Signal[][] loadSignals(File[] dbFileNames)
   */
  public void testloadSignals() throws java.sql.SQLException
  {
    ArrayList dbFileNames = jdbcFixture.findDBFilesWithSignals(1);
    int dbFileCount = dbFileNames.size();
    ArrayList signalsToDelete = null;
    try
    {
      if(dbFileCount == 0)
      {
        //Need to create some data.
        dbFileNames = jdbcFixture.findDBFileNames(1);
        dbFileCount = dbFileNames.size();
        signalsToDelete = jdbcFixture.findValidSignalIDs(2);
        jdbcFixture.insertSignalsIntoDBFile(signalsToDelete, dbFileNames);
      }
      DBFile[] dbFiles = new DBFile[dbFileCount];
      for(int i=0;i<dbFileCount;i++) 
        dbFiles[i] = new DBFile(dbFileNames.get(i).toString());
      dbExport.loadSignals(dbFiles);
      for(int i=0;i<dbFiles.length;i++) 
      {
        ArrayList expectedSignalIDs = jdbcFixture.findSignalIDsForDBFile(dbFiles[i].getFileName());
        StringBuffer message = new StringBuffer("Incorrect number of signals returned for DB file '");
        message.append(dbFiles[i]);
        message.append("'.");
        int expectedSignalCount = expectedSignalIDs.size();
        int actualSignalCount = dbFiles[i].getSignalCount();
        assertEquals(message.toString(), expectedSignalCount, actualSignalCount);
        for(int j=0;j<expectedSignalCount;j++) 
        {
          String signalID = expectedSignalIDs.get(j).toString();
          message = new StringBuffer("Signal '");
          message.append(signalID);
          message.append("' not loaded for file '");
          message.append(dbFiles[i]);
          message.append("'.");
          assertNotNull(message.toString(), dbFiles[i].getSignal(signalID));
        }
      }
    }
    finally
    {
      if(signalsToDelete != null)
      {
//        for(int i=0;i<dbFileCount;i++) 
//        {
//          String dbFileName = dbFileNames.get(i).toString();
//          jdbcFixture.deleteSignalsFromDBFile(signalsToDelete, dbFileName);
//        }
      }
    }
  }
  
  public ArrayList loadDBFileNames() throws java.sql.SQLException
  {
    Connection oracle = jdbcFixture.getConnection();
    try
    {
      Statement query = oracle.createStatement();
      try
      {
        //Find a DB file with atleast one signal and field.
        StringBuffer sql = new StringBuffer("SELECT DISTINCT EXT_SRC_FILE_NM FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".IOC_DB_FILE_ASGN");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          ArrayList fileNames = new ArrayList();
          while(result.next())
          {
            String fileName = result.getString("EXT_SRC_FILE_NM");
            fileNames.add(fileName);
          }
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
      oracle.close();
    }
  }
}