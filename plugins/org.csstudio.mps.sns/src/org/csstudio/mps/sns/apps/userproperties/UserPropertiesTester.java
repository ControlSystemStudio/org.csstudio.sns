package org.csstudio.mps.sns.apps.userproperties;
import org.csstudio.mps.sns.view.MPSBrowserView;
import java.util.ArrayList;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.csstudio.mps.sns.test.JDBCTestFixture;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.ITable;

public class UserPropertiesTester extends TestCase 
{
  private JDBCTestFixture jdbcFixture;
  private UserProperties properties;
  private DatabaseDataSourceConnection connection;

  public UserPropertiesTester(String sTestName)
  {
    super(sTestName);
  }

  public void setUp() throws Exception
  {
    jdbcFixture = JDBCTestFixture.getInstance();
    jdbcFixture.setUp();
    properties = new UserProperties();
    properties.setDataSource(jdbcFixture.getDataSource());
  }

  public void tearDown() throws Exception
  {
    jdbcFixture.tearDown();
  }

  /**
   * ArrayList loadUserIDs()
   */
  public void testloadUserIDs() throws Exception
  {
    QueryDataSet query = new QueryDataSet(getConnection());
    StringBuffer sql = new StringBuffer("SELECT USERID FROM ");
    sql.append(MPSBrowserView.SCHEMA);
    sql.append(".JERI_USR_PROP WHERE PROP_CD = 'P' ORDER BY USERID");
    query.addTable("JERI_USR_PROP", sql.toString());
    ITable expectedData = query.getTable("JERI_USR_PROP");
    ArrayList actualData = properties.loadUserIDs();
    int expectedCount = expectedData.getRowCount();
    int actualCount = actualData.size();
    assertEquals("Wrong number of user records loaded.", expectedCount, actualCount);
    for(int i=0;i<expectedCount;i++) 
    {
      Object expectedRecord = expectedData.getValue(i, "USERID");
      StringBuffer message = new StringBuffer("User ID '");
      message.append(expectedRecord);
      message.append("' not loaded from RDB.");
      assertTrue(message.toString(), actualData.contains(expectedRecord));
    }
  }

  /**
   * 
   * @return 
   * @throws java.sql.SQLException
   */
  protected IDatabaseConnection getConnection() throws java.sql.SQLException
  {
    if(connection == null)
      connection = new DatabaseDataSourceConnection(jdbcFixture.getDataSource(), MPSBrowserView.SCHEMA);
    return connection;
  }
}