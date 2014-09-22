package org.csstudio.mps.sns.apps.mpsbrowser;

import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.MPSChannel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.csstudio.mps.sns.test.JDBCTestFixture;
import org.csstudio.mps.sns.view.MPSBrowserView;


public class MPSBrowserTester extends TestCase 
{
  JDBCTestFixture jdbcFixture;

  public MPSBrowserTester(String sTestName)
  {
    super(sTestName);
  }

  public void setUp() throws Exception
  {
    jdbcFixture = JDBCTestFixture.getInstance();
    jdbcFixture.setUp();
  }

  public void tearDown() throws Exception
  {
    jdbcFixture.tearDown();
  }

  /**
   * void loadDefaults(MPSChannel)
   */
  public void testloadDefaults() throws java.sql.SQLException
  {
    MPSBrowser browser = new MPSBrowser();
    Connection connection = jdbcFixture.getConnection();
    try
    {
      browser.setConnection(connection);
      MPSChannel channel = new MPSChannel();
      Statement query = connection.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT MACHINE_MODE.DVC_ID FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".MACHINE_MODE, ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".DVC, ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".MACHINE_MODE_DEF_MASK WHERE MACHINE_MODE.DVC_ID = DVC.DVC_ID AND DVC.DVC_TYPE_ID = MACHINE_MODE_DEF_MASK.DVC_TYPE_ID");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          if(result.next())
          {
            //method was closing connection.
            channel.setDevice(new Device(result.getString("DVC_ID")));
            browser.loadDefaults(channel, null);
            assertFalse("Connection closed.", connection.isClosed());
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
      connection.close();
    }
  }
}