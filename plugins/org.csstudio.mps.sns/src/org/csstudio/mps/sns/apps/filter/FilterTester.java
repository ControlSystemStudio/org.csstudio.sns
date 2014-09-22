package org.csstudio.mps.sns.apps.filter;
import org.csstudio.mps.sns.view.MPSBrowserView;

import org.csstudio.mps.sns.test.JDBCTestFixture;
import org.csstudio.mps.sns.tools.data.DeviceType;
import org.csstudio.mps.sns.tools.data.EpicsRecordType;
import org.csstudio.mps.sns.tools.data.EpicsSubsystem;
import org.csstudio.mps.sns.tools.data.EpicsSystem;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

public class FilterTester extends DatabaseTestCase 
{
  private JDBCTestFixture jdbcFixture;
  private Filter filter;
  private DatabaseDataSourceConnection connection;
  private FlatXmlDataSet allData;

  public FilterTester(String sTestName)
  {
    super(sTestName);
  }

  public void setUp() throws Exception
  {
    jdbcFixture = JDBCTestFixture.getInstance();
    jdbcFixture.setUp();
    filter = new Filter();
    filter.setDataSource(jdbcFixture.getDataSource());
    filter.setSchema(MPSBrowserView.SCHEMA);
    super.setUp();
    deleteTestData();
  }

  /**
   * ArrayList loadColumnNames()
   */
  public void testloadColumnNames() throws java.sql.SQLException
  {
    filter.setTableName("DVC");
    ArrayList names = filter.loadColumnNames();
    Connection oracleConnection = jdbcFixture.getConnection();
    try
    {
      Statement query = oracleConnection.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT * FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".DVC WHERE DVC_ID = 'A' AND DVC_ID = 'B'");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          ResultSetMetaData columnInfo = result.getMetaData();
          int columnCount = columnInfo.getColumnCount();
          assertNotNull("Null returned.", names);
          assertEquals("Wrong number of column names returned.", columnCount, names.size());
          for(int i=1;i<=columnCount;i++)
          {
            String currentName = columnInfo.getColumnName(i);
            assertTrue("Column '" + currentName + "' not returned.", names.contains(currentName));
            names.remove(currentName);
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

  public void testdeleteFilter() throws Exception
  {
    OracleDataSource dataSource = (OracleDataSource)jdbcFixture.getDataSource();
    String userID = dataSource.getUser().toUpperCase();
    insertTestData(userID);
    ITable expectedData = getDataSet().getTable("JERI_USR_PROP");
    int rowCount = expectedData.getRowCount();
    for(int i=0;i<rowCount;i++) 
    {
      String name = expectedData.getValue(i, "FLTR_NM").toString();
      filter.deleteFilter(name);
      QueryDataSet databaseData = new QueryDataSet(getConnection());
      StringBuffer sql = new StringBuffer("SELECT * FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".JERI_USR_PROP WHERE FLTR_NM = '");
      sql.append(name);
      sql.append("' AND USERID = '");
      sql.append(userID);
      sql.append("'");
      databaseData.addTable("JERI_USR_PROP", sql.toString());
      ITable actualData = databaseData.getTable("JERI_USR_PROP");
      assertEquals("Filter not deleted.", 0, actualData.getRowCount());
    }
  }
  
  private void insertTestData(String userID) throws Exception
  {
    boolean useTestFileUserID = userID == null;
    Connection connection = jdbcFixture.getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("INSERT INTO ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".JERI_USR_PROP (FLTR_CONT, PROP_CD, FLTR_NM, USERID, FLTR_TBL) VALUES (?, ?, ?, ?, ?)");
      PreparedStatement insertStatement = connection.prepareStatement(sql.toString());
      try
      {
        ITable propertiesTable = getDataSet().getTable("JERI_USR_PROP");    
        int rowCount = propertiesTable.getRowCount();
        for(int i=0;i<rowCount;i++) 
        {
          String contents = propertiesTable.getValue(i, "FLTR_CONT").toString();
          insertStatement.setString(1, contents);
          String code = propertiesTable.getValue(i, "PROP_CD").toString();
          insertStatement.setString(2, code);
          String name = propertiesTable.getValue(i, "FLTR_NM").toString();
          insertStatement.setString(3, name);
          if(useTestFileUserID)//Use value in test data file if null.
            userID = propertiesTable.getValue(i, "USERID").toString().toUpperCase();
          insertStatement.setString(4, userID);
          String table = propertiesTable.getValue(i, "FLTR_TBL").toString();
          insertStatement.setString(5, table);
          insertStatement.execute();
        }
      }
      finally
      {
        insertStatement.close();
      }
    }
    finally
    {
      connection.close();
    }
  }
  
  public void testloadFilter() throws Exception
  {
    insertTestData(null);
    ITable expectedData = getDataSet().getTable("JERI_USR_PROP");
    int rowCount = expectedData.getRowCount();
    for(int i=0;i<rowCount;i++) 
    {
      String name = expectedData.getValue(i, "FLTR_NM").toString();
      String[] newFilter = filter.loadFilter(name);
      StringBuffer message = new StringBuffer("Null returned for filter '");
      message.append(name);
      message.append("'.");
      assertNotNull(message.toString(), newFilter);
      String type = expectedData.getValue(i, "PROP_CD").toString();
      assertEquals("Filter type wrong.", type, newFilter[0]);
      String contents = expectedData.getValue(i, "FLTR_CONT").toString();
      assertEquals("Filter contents wrong.", contents.trim(), newFilter[1].trim());
    }
  }
  
  /**
   * getUserFilterNames()
   */
  public void testgetUserFilterNames() throws Exception
  {
    insertTestData(null);
    filter.setTableName("DVC");
    filter.loadFilterNames();
    String[] actual = filter.getUserFilterNames();
    OracleDataSource dataSource = (OracleDataSource)jdbcFixture.getDataSource();
    String userID = dataSource.getUser().toUpperCase();
    ArrayList expected = getUserFilterNames("DVC", userID);
    int expectedCount = expected.size();
    assertEquals("Wrong number of filter names loaded.", expectedCount, actual.length);
    for(int i=0;i<actual.length;i++) 
    {
      StringBuffer message = new StringBuffer("Returned value '");
      message.append(actual[i]);
      message.append("' not expected.");
      assertTrue(message.toString(), expected.contains(actual[i]));
    }
  }
  
  /**
   * loadFilterNames(boolean)
   */
  public void testloadFilterNames() throws Exception
  {
    filter.setTableName("DVC");
    filter.loadFilterNames();
    String[] actual = filter.getAllFilterNames();
    OracleDataSource dataSource = (OracleDataSource)jdbcFixture.getDataSource();
    String userID = dataSource.getUser().toUpperCase();
    ArrayList expected = getAllFilterNames("DVC", userID);
    int expectedCount = expected.size();
    assertEquals("Wrong number of filter names loaded.", expectedCount, actual.length);
    for(int i=0;i<actual.length;i++) 
    {
      StringBuffer message = new StringBuffer("Returned value '");
      message.append(actual[i]);
      message.append("' not expected.");
      assertTrue(message.toString(), expected.contains(actual[i]));
    }
  }
  
  public ArrayList getAllFilterNames(String tableName, String userID) throws Exception
  {
    QueryDataSet data = new QueryDataSet(getConnection());
    StringBuffer sql = new StringBuffer("SELECT FLTR_NM FROM ");
    sql.append(MPSBrowserView.SCHEMA);
    sql.append(".JERI_USR_PROP WHERE FLTR_TBL = '");
    sql.append(tableName);
    sql.append("' AND (PROP_CD = 'FP' OR PROP_CD = 'BP' OR (USERID = '");
    sql.append(userID);
    sql.append("' AND (PROP_CD = 'FU' OR PROP_CD = 'BU' OR PROP_CD = 'F')))");
    data.addTable("JERI_USR_PROP", sql.toString());
    ITable table = data.getTable("JERI_USR_PROP");
    int rowCount = table.getRowCount();
    ArrayList names = new ArrayList(rowCount);
    for(int i=0;i<rowCount;i++) 
      names.add(table.getValue(i, "FLTR_NM"));
    return names;
  }
  
  public ArrayList getUserFilterNames(String tableName, String userID) throws Exception
  {
    QueryDataSet data = new QueryDataSet(getConnection());
    StringBuffer sql = new StringBuffer("SELECT FLTR_NM FROM ");
    sql.append(MPSBrowserView.SCHEMA);
    sql.append(".JERI_USR_PROP WHERE FLTR_TBL = '");
    sql.append(tableName);
    sql.append("' AND USERID = '");
    sql.append(userID);
    sql.append("' AND (PROP_CD = 'FP' OR PROP_CD = 'BP' OR PROP_CD = 'FU' OR PROP_CD = 'BU' OR PROP_CD = 'F')");
    data.addTable("JERI_USR_PROP", sql.toString());
    ITable table = data.getTable("JERI_USR_PROP");
    int rowCount = table.getRowCount();
    ArrayList names = new ArrayList(rowCount);
    for(int i=0;i<rowCount;i++) 
      names.add(table.getValue(i, "FLTR_NM"));
    return names;
  }
  
  /**
   * ArrayList saveFilter(boolean, String, String, String)
   */
  public void testsaveFilter() throws Exception
  {
    ITable data = getDataSet().getTable("JERI_USR_PROP");
    int rowCount = data.getRowCount();
    OracleDataSource dataSource = (OracleDataSource)jdbcFixture.getDataSource();
    String userID = dataSource.getUser().toUpperCase();
    for(int i=0;i<rowCount;i++) 
    {
      String contents = data.getValue(i, "FLTR_CONT").toString();
      String type = data.getValue(i, "PROP_CD").toString();
      String name = data.getValue(i, "FLTR_NM").toString();
      filter.saveFilter(true, contents, type, name);
      QueryDataSet databaseData = new QueryDataSet(getConnection());
      StringBuffer sql = new StringBuffer("SELECT * FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".JERI_USR_PROP WHERE FLTR_NM = '");
      sql.append(name);
      sql.append("' AND USERID = '");
      sql.append(userID);
      sql.append("'");
      databaseData.addTable("JERI_USR_PROP", sql.toString());
      ITable actualData = databaseData.getTable("JERI_USR_PROP");
      assertEquals("Filter not saved correctly.", 1, actualData.getRowCount());
    }
  }

  /**
   * boolean checkOtherUsersFilterNames(boolean)
   */
  public void testcheckUsersFilterNamesOtherUsersPublicAndUser() throws Exception
  {
    insertTestData(null);
    ITable allData = getDataSet().getTable("JERI_USR_PROP");
    int rowCount = allData.getRowCount();
    for(int i=0;i<rowCount;i++) 
    {
      String tableName = allData.getValue(i, "FLTR_TBL").toString();
      String filterName = allData.getValue(i, "FLTR_NM").toString();
      boolean actual = filter.checkUsersFilterNames(tableName, filterName, false, false);
      StringBuffer message = new StringBuffer("Filter '");
      message.append(filterName);
      message.append("' was not found in the database for table '");
      message.append(tableName);
      message.append("'.");
      assertTrue(message.toString(), actual);
    }
  }

  /**
   * boolean checkOtherUsersFilterNames(boolean)
   */
  public void testcheckUsersFilterNamesCurrentUserPublicAndUser() throws Exception
  {
    OracleDataSource dataSource = (OracleDataSource)jdbcFixture.getDataSource();
    String userID = dataSource.getUser().toUpperCase();
    insertTestData(userID);
    ITable allData = getDataSet().getTable("JERI_USR_PROP");
    int rowCount = allData.getRowCount();
    for(int i=0;i<rowCount;i++) 
    {
      String tableName = allData.getValue(i, "FLTR_TBL").toString();
      String filterName = allData.getValue(i, "FLTR_NM").toString();
      boolean actual = filter.checkUsersFilterNames(tableName, filterName, false, true);
      StringBuffer message = new StringBuffer("Filter '");
      message.append(filterName);
      message.append("' was not found in the database for table '");
      message.append(tableName);
      message.append("'.");
      assertTrue(message.toString(), actual);
    }
  }

  /**
   * boolean checkOtherUsersFilterNames(boolean)
   */
  public void testcheckUsersFilterNamesOtherUsersPublicOnly() throws Exception
  {
    insertTestData(null);
    ITable allData = getDataSet().getTable("JERI_USR_PROP");
    int rowCount = allData.getRowCount();
    for(int i=0;i<rowCount;i++) 
    {
      String tableName = allData.getValue(i, "FLTR_TBL").toString();
      String filterName = allData.getValue(i, "FLTR_NM").toString();
      boolean actual = filter.checkUsersFilterNames(tableName, filterName, true, false);
      //Possible values for the PROP_CD field:
      //'F'  - Legacy user defined editor filter (treated as 'FU' type).
      //'FU' - User defined editor filter.
      //'FP' - Public editor filter.
      //'BU' - User defined builder filter.
      //'BP' - Public builder filter.
      Object type = allData.getValue(i, "PROP_CD").toString();
      if(type.equals("FP") || type.equals("BP"))
      {
        StringBuffer message = new StringBuffer("Filter '");
        message.append(filterName);
        message.append("' was not found in the database for table '");
        message.append(tableName);
        message.append("'.");
        assertTrue(message.toString(), actual);
      }
      else
      {
        StringBuffer message = new StringBuffer("Filter '");
        message.append(filterName);
        message.append("' was found in the database for table '");
        message.append(tableName);
        message.append("'.");
        assertFalse(message.toString(), actual);
      }
    }
  }

  /**
   * boolean checkOtherUsersFilterNames(boolean)
   */
  public void testcheckUsersFilterNamesCurrentUserPublicOnly() throws Exception
  {
    OracleDataSource dataSource = (OracleDataSource)jdbcFixture.getDataSource();
    String userID = dataSource.getUser().toUpperCase();
    insertTestData(userID);
    ITable allData = getDataSet().getTable("JERI_USR_PROP");
    int rowCount = allData.getRowCount();
    for(int i=0;i<rowCount;i++) 
    {
      String tableName = allData.getValue(i, "FLTR_TBL").toString();
      String filterName = allData.getValue(i, "FLTR_NM").toString();
      boolean actual = filter.checkUsersFilterNames(tableName, filterName, true, true);
      //Possible values for the PROP_CD field:
      //'F'  - Legacy user defined editor filter (treated as 'FU' type).
      //'FU' - User defined editor filter.
      //'FP' - Public editor filter.
      //'BU' - User defined builder filter.
      //'BP' - Public builder filter.
      Object type = allData.getValue(i, "PROP_CD").toString();
      if(type.equals("FP") || type.equals("BP"))
      {
        StringBuffer message = new StringBuffer("Filter '");
        message.append(filterName);
        message.append("' was not found in the database for table '");
        message.append(tableName);
        message.append("'.");
        assertTrue(message.toString(), actual);
      }
      else
      {
        StringBuffer message = new StringBuffer("Filter '");
        message.append(filterName);
        message.append("' was found in the database for table '");
        message.append(tableName);
        message.append("'.");
        assertFalse(message.toString(), actual);
      }
    }
  }
  
  protected IDatabaseConnection getConnection() throws java.sql.SQLException
  {
    if(connection == null)
      connection = new DatabaseDataSourceConnection(jdbcFixture.getDataSource(), MPSBrowserView.SCHEMA);
    return connection;
  }

  protected IDataSet getDataSet() throws Exception
  {
    if(allData == null)
    {
      URL file = getClass().getResource("/gov/sns/apps/jeri/apps/filter/FilterTesterData.xml");
      allData = new FlatXmlDataSet(file);
    }
    return allData;
  }

  private void deleteTestData() throws Exception
  {
    ITable allData = getDataSet().getTable("JERI_USR_PROP");
    int rowCount = allData.getRowCount();
    Connection connection = jdbcFixture.getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("DELETE FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".JERI_USR_PROP WHERE FLTR_NM = ? AND FLTR_TBL = ?");
      PreparedStatement deleteStatement = connection.prepareStatement(sql.toString());
      try
      {
        for(int i=0;i<rowCount;i++) 
        {
          String name = allData.getValue(i, "FLTR_NM").toString();
          deleteStatement.setString(1, name);
          String table = allData.getValue(i, "FLTR_TBL").toString();
          deleteStatement.setString(2, table);
          deleteStatement.execute();
        }
        connection.commit();
      }
      finally
      {
        deleteStatement.close();
      }
    }
    finally
    {
      connection.close();
    }
  }

  protected void tearDown() throws Exception
  {
    super.tearDown();
    deleteTestData();
  }

  protected DatabaseOperation getSetUpOperation() throws Exception
  {
    return DatabaseOperation.NONE;
  }

  protected DatabaseOperation getTearDownOperation() throws Exception
  {
    return DatabaseOperation.NONE;
  }
}