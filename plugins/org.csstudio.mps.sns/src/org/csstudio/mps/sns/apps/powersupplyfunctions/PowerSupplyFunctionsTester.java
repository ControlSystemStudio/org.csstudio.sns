package org.csstudio.mps.sns.apps.powersupplyfunctions;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.IOC;
import org.csstudio.mps.sns.tools.data.Magnet;
import org.csstudio.mps.sns.tools.data.PowerSupply;
import org.csstudio.mps.sns.tools.data.PowerSupplyController;
import org.csstudio.mps.sns.tools.data.PowerSupplyInterface;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.csstudio.mps.sns.test.JDBCTestFixture;
import org.dbunit.Assertion;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

public class PowerSupplyFunctionsTester extends TestCase 
{
  private DatabaseDataSourceConnection connection;
  private JDBCTestFixture jdbcFixture;
  private PowerSupplyFunctions functions;
  private FlatXmlDataSet allData;

  public PowerSupplyFunctionsTester(String sTestName)
  {
    super(sTestName);
  }

  public void setUp() throws Exception
  {
    jdbcFixture = JDBCTestFixture.getInstance();
    jdbcFixture.setUp();
    functions = new PowerSupplyFunctions();
    functions.setConnection(jdbcFixture.getConnection());
  }

  public void tearDown() throws Exception
  {
    jdbcFixture.tearDown();
  }

  /**
   * PowerSupply[] loadPowerSupplyData()
   */
  public void testloadPowerSupplyDataCheckIOCs() throws Exception
  {
    //Check number of IOCs loaded.
    IOC[] iocs = functions.loadPowerSupplyData();
    QueryDataSet query = new QueryDataSet(getConnection());
    StringBuffer sql = new StringBuffer("SELECT DISTINCT IOC_DVC.DVC_ID FROM ");
    sql.append(MPSBrowserView.SCHEMA);
    sql.append(".IOC_DVC, ");
    sql.append(MPSBrowserView.SCHEMA);
    sql.append(".PSC_DVC WHERE IOC_DVC.DVC_ID = PSC_DVC.IOC_DVC_ID");
    query.addTable("IOC_DVC", sql.toString());
    ITable iocTable = query.getTable("IOC_DVC");
    int expectedIOCCount = iocTable.getRowCount();
    assertEquals("Wrong number of IOC records loaded.", expectedIOCCount, iocs.length);
    for(int iocIndex=0;iocIndex<expectedIOCCount;iocIndex++) 
    {
      //Look for each IOC.
      StringBuffer message = new StringBuffer("IOC '");
      message.append(iocs[iocIndex]);
      message.append("' not loaded from RDB.");
      String iocID = iocTable.getValue(iocIndex, "DVC_ID").toString();
      IOC currentIOC = findIOC(iocs, iocID);
      assertNotNull(message.toString(), currentIOC);
    }
  }

  /**
   * PowerSupply[] loadPowerSupplyData()
   */
  public void testloadPowerSupplyDataCheckPSCs() throws Exception
  {
    IOC[] iocs = functions.loadPowerSupplyData();
    for(int iocIndex=0;iocIndex<iocs.length;iocIndex++) 
    {
      //Check number of PSC records loaded for each IOC.
      StringBuffer sql = new StringBuffer("SELECT DVC_ID FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".PSC_DVC WHERE IOC_DVC_ID = '");
      sql.append(iocs[iocIndex].getID());
      sql.append("'");
      QueryDataSet query = new QueryDataSet(getConnection());
      query.addTable("PSC_DVC", sql.toString());
      ITable pscTable = query.getTable("PSC_DVC");
      StringBuffer message = new StringBuffer("Wrong number of PSC records loaded for IOC '");
      message.append(iocs[iocIndex]);
      message.append("'.");
      int expectedPSCCount = pscTable.getRowCount();
      int actual = iocs[iocIndex].getPowerSupplyControllerCount();
      assertEquals(message.toString(), expectedPSCCount, actual);
      for(int pscIndex=0;pscIndex<expectedPSCCount;pscIndex++) 
      {
        //Look for each PSC in each IOC.
        String pscID = pscTable.getValue(pscIndex, "DVC_ID").toString();
        message = new StringBuffer("PSC '");
        message.append(pscID);
        message.append("' for IOC '");
        message.append(iocs[iocIndex]);
        message.append("' not loaded from RDB.");
        PowerSupplyController currentPSC = iocs[iocIndex].getPowerSupplyController(pscID);
        assertNotNull(message.toString(), currentPSC);
      }
    }
  }

  /**
   * PowerSupply[] loadPowerSupplyData()
   */
  public void testloadPowerSupplyDataCheckPSIs() throws Exception
  {
    IOC[] iocs = functions.loadPowerSupplyData();
    for(int iocIndex=0;iocIndex<iocs.length;iocIndex++) 
    {
      int pscCount = iocs[iocIndex].getPowerSupplyControllerCount();
      for(int pscIndex=0;pscIndex<pscCount;pscIndex++) 
      {
        PowerSupplyController psc = iocs[iocIndex].getPowerSupplyControllerAt(pscIndex);
        //Check the number of PSI records in each PSC.
        StringBuffer sql = new StringBuffer("SELECT DVC_ID FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".PSI_DVC WHERE PSC_DVC_ID = '");
        sql.append(psc.getID());
        sql.append("'");
      QueryDataSet query = new QueryDataSet(getConnection());
        query.addTable("PSI_DVC", sql.toString());
        ITable psiTable = query.getTable("PSI_DVC");
        StringBuffer message = new StringBuffer("Wrong number of PSI records loaded for PSC '");
        message.append(psc);
        message.append("'.");
        int expectedPSICount = psiTable.getRowCount();
        int actual = psc.getPowerSupplyInterfaceCount();
        assertEquals(message.toString(), expectedPSICount, actual);
        for(int psiIndex=0;psiIndex<expectedPSICount;psiIndex++) 
        {
          //Look for each PSI in each PSC.
          String psiID = psiTable.getValue(psiIndex, "DVC_ID").toString();
          message = new StringBuffer("PSI '");
          message.append(psiID);
          message.append("' for PSC '");
          message.append(psc);
          message.append("' not loaded from RDB.");
          PowerSupplyInterface currentPSI = psc.getPowerSupplyInterface(psiID);
          assertNotNull(message.toString(), currentPSI);
        }
      }
    }
  }

  /**
   * PowerSupply[] loadPowerSupplyData()
   */
  public void testloadPowerSupplyDataCheckPSs() throws Exception
  {
    IOC[] iocs = functions.loadPowerSupplyData();
    for(int iocIndex=0;iocIndex<iocs.length;iocIndex++) 
    {
      int pscCount = iocs[iocIndex].getPowerSupplyControllerCount();
      for(int pscIndex=0;pscIndex<pscCount;pscIndex++) 
      {
        PowerSupplyController psc = iocs[iocIndex].getPowerSupplyControllerAt(pscIndex);
        int psiCount = psc.getPowerSupplyInterfaceCount();
        for(int psiIndex=0;psiIndex<psiCount;psiIndex++) 
        {
          //Check the number of PS records in each PSI.
          PowerSupplyInterface psi = psc.getPowerSupplyInterfaceAt(psiIndex);
          StringBuffer sql = new StringBuffer("SELECT DVC_ID FROM ");
          sql.append(MPSBrowserView.SCHEMA);
          sql.append(".PS_DVC WHERE PSI_DVC_ID = '");
          sql.append(psi.getID());
          sql.append("'");
          QueryDataSet query = new QueryDataSet(getConnection());
          query.addTable("PS_DVC", sql.toString());
          ITable psTable = query.getTable("PS_DVC");
          StringBuffer message = new StringBuffer("Wrong number of PS records loaded for PSI '");
          message.append(psi);
          message.append("'.");
          int expectedPSCount = psTable.getRowCount();
          int actual = psi.getPowerSupplyCount();
          assertEquals(message.toString(), expectedPSCount, actual);
          for(int psIndex=0;psIndex<expectedPSCount;psIndex++) 
          {
            //Look for each PS in each PSI.
            String psID = psTable.getValue(psIndex, "DVC_ID").toString();
            message = new StringBuffer("PS '");
            message.append(psID);
            message.append("' for PSI '");
            message.append(psID);
            message.append("' not loaded from RDB.");
            PowerSupply currentPS = psi.getPowerSupply(psID);
            assertNotNull(message.toString(), currentPS);
          }
        }
      }
    }
  }

  /**
   * PowerSupply[] loadPowerSupplyData()
   */
  public void testloadPowerSupplyDataCheckMagnets() throws Exception
  {
    IOC[] iocs = functions.loadPowerSupplyData();
    for(int iocIndex=0;iocIndex<iocs.length;iocIndex++) 
    {
      int pscCount = iocs[iocIndex].getPowerSupplyControllerCount();
      for(int pscIndex=0;pscIndex<pscCount;pscIndex++) 
      {
        PowerSupplyController psc = iocs[iocIndex].getPowerSupplyControllerAt(pscIndex);
        int psiCount = psc.getPowerSupplyInterfaceCount();
        for(int psiIndex=0;psiIndex<psiCount;psiIndex++) 
        {
          PowerSupplyInterface psi = psc.getPowerSupplyInterfaceAt(psiIndex);
          int psCount = psi.getPowerSupplyCount();
          for(int psIndex=0;psIndex<psCount;psIndex++) 
          {
            PowerSupply ps = psi.getPowerSupplyAt(psIndex);
            //Check the number of Magnet records in each PS.
            StringBuffer sql = new StringBuffer("SELECT MAG_DVC.DVC_ID FROM ");
            sql.append(MPSBrowserView.SCHEMA);
            sql.append(".MAG_DVC, DVC WHERE DVC.PARENT_DVC_ID = '");
            sql.append(ps.getID());
            sql.append("' AND DVC.DVC_ID = MAG_DVC.DVC_ID");
            QueryDataSet query = new QueryDataSet(getConnection());
            query.addTable("MAG_DVC", sql.toString());
            ITable magnetTable = query.getTable("MAG_DVC");
            StringBuffer message = new StringBuffer("Wrong number of Magnet records loaded for PS '");
            message.append(ps);
            message.append("'.");
            int expectedMagnetCount = magnetTable.getRowCount();
            int actual = ps.getMagnetCount();
            assertEquals(message.toString(), expectedMagnetCount, actual);
            for(int magnetIndex=0;magnetIndex<expectedMagnetCount;magnetIndex++) 
            {
              //Look for each magnet in each PS.
              String magnetID = magnetTable.getValue(magnetIndex, "DVC_ID").toString();
              message = new StringBuffer("Magnet '");
              message.append(magnetID);
              message.append("' for PS '");
              message.append(ps);
              message.append("' not loaded from RDB.");
              Magnet currentMagnet = ps.getMagnet(magnetID);
              assertNotNull(message.toString(), currentMagnet);
            }
          }
        }
      }
    }
  }

  private IOC findIOC(IOC[] iocs, String id)
  {
    for(int i=0;i<iocs.length;i++) 
      if(id.equals(iocs[i].getID()))
        return iocs[i];
    return null;
  }
  
  protected IDatabaseConnection getConnection() throws java.sql.SQLException
  {
    if(connection == null)
      connection = new DatabaseDataSourceConnection(jdbcFixture.getDataSource(), MPSBrowserView.SCHEMA);
    return connection;
  }
  
//  /**
//   * ResultSet loadData(Object)
//   */
//  public void testloadDataIOC() throws Exception
//  {
//    testTableLoad("IOC_DVC");
//  }
//  
//  /**
//   * ResultSet loadData(Object)
//   */
//  public void testloadDataPSC() throws Exception
//  {
//    testTableLoad("PSC_DVC");
//  }
//  
//  /**
//   * ResultSet loadData(Object)
//   */
//  public void testloadDataPSI() throws Exception
//  {
//    testTableLoad("PSI_DVC");
//  }
//  
//  /**
//   * ResultSet loadData(Object)
//   */
//  public void testloadDataPS() throws Exception
//  {
//    testTableLoad("PS_DVC");
//  }
//  
//  /**
//   * ResultSet loadData(Object)
//   */
//  public void testloadDataMAG() throws Exception
//  {
//    testTableLoad("MAG_DVC");
//  }
  
  private void testTableLoad(String tableName) throws Exception
  {
    IDataSet allData = getDataSet();
    DatabaseOperation.DELETE.execute(getConnection(), allData);
    DefaultDataSet insertData = new DefaultDataSet(allData.getTable("DVC"));
    try
    {
      ITable deviceTable = allData.getTable(tableName);
      insertData.addTable(deviceTable);
      DatabaseOperation.INSERT.execute(getConnection(), insertData);
      int deviceCount = deviceTable.getRowCount();
      for(int i=0;i<deviceCount;i++) 
      {
        String deviceID = deviceTable.getValue(i, "DVC_ID").toString();
        Device currentDevice = createDeviceForTable(tableName, deviceID);
        ResultSet data = functions.loadData(currentDevice);
        try
        {
          StringBuffer message = new StringBuffer("No record loaded from table '");
          message.append(tableName);
          message.append("' for device '");
          message.append(deviceID);
          message.append("'.");
          assertTrue(message.toString(), data.next());
        }
        finally
        {
          data.close();
        }
      }
    }
    finally
    {
      DatabaseOperation.DELETE.execute(getConnection(), insertData);
    }
  }
  
  private Device createDeviceForTable(String tableName, String deviceID)
  {
    if(tableName.equals("IOC_DVC"))
      return new IOC(deviceID);
    else
      if(tableName.equals("PSC_DVC"))
        return new PowerSupplyController(deviceID);
      else
        if(tableName.equals("PSI_DVC"))
          return new PowerSupplyInterface(deviceID);
        else
          if(tableName.equals("PS_DVC"))
            return new PowerSupply(deviceID);
          else
            return new Magnet(deviceID);
  }

  protected IDataSet getDataSet() throws Exception
  {
    if(allData == null)
    {
      URL file = getClass().getResource("/gov/sns/apps/jeri/apps/PowerSupplyFunctionsTesterData.xml");
      allData = new FlatXmlDataSet(file);
    }
    return allData;
  }
}