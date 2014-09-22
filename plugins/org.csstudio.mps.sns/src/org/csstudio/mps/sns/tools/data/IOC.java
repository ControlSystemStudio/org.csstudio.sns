package org.csstudio.mps.sns.tools.data;
import com.cosylab.gui.components.ProgressEvent;
import com.cosylab.gui.components.ProgressListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

import org.csstudio.mps.sns.tools.data.Device;

import java.sql.Date;
import java.sql.SQLException;
import org.csstudio.mps.sns.view.MPSBrowserView;

/**
 * This class holds the data contained in the IOC_DVC table. Each instance 
 * represents a single IOC.
 * 
 * @author Chris Fowlkes
 */
public class IOC extends Device 
{
//  /**
//   * Holds the external source files associated with the <CODE>IOC</CODE>.
//   */
//  private ArrayList externalSourceFileNames = new ArrayList();
//  /**
//   * Holds the external directory names associated with the <CODE>IOC</CODE>.
//   * There should be a 1 to 1 relationship between the items in the 
//   * <CODE>externalSourceFileNames ArrayList</CODE> and this one.
//   */
//  private ArrayList externalSourceDirectoryNames = new ArrayList();
  private ArrayList dbFiles = new ArrayList();
  /**
   * Holds the instances of <CODE>PowerSupplyController</CODE> added to the 
   * <CODE>IOC</CODE>.
   */
  private ArrayList powerSupplyControllers = new ArrayList();
  /**
   * Holds the boot information for the <CODE>IOC</CODE>.
   */
  private HashMap boots = new HashMap();
  private String netName;
  private Timestamp bootDate;
  private String bootFile;
  private String bootServer;
  private String ipAddress;
  private String macAddress;
  /**
   * Comment here
   * @attribute 
   */
  private Device switchDevice;
  private int switchPortNumber;
  /**
   * Comment here
   * @attribute 
   */
  private Device terminalServer;
  private String terminalServerPortNumber;
  /**
   * Comment here
   * @attribute 
   */
  private Device rpc;
  private String rpcAddress;
  private String netDomain;
  private String netMask;
  private String vlanNumber;
  private String cpuType;
  private String serialNumber;
  private String ttyName;
  private String epicsVersion;
  private String vxWorksBSPVersion;
  private String gatewayAddress;
  private String loginName;
  /**
   * Holds the value of the PARSE_IND RDB field.
   */
  private String parseIndicator = "Y";
  public final static String NET_NAME_COLUMN = "IOC_NET_NM";
  public final static String BOOT_DATE_COLUMN = "BOOT_DTE";
  public final static String BOOT_FILE_COLUMN = "BOOT_FILE";
  public final static String BOOT_SERVER_COLUMN = "BOOT_SRV";
  public final static String IP_ADDRESS_COLUMN = "IP_ADDR";
  public final static String MAC_ADDRESS_COLUMN = "ETHER_ADDR";
  public final static String SWITCH_DEVICE_COLUMN = "SW_DVC_ID";
  public final static String SWITCH_PORT_NUMBER_COLUMN = "SW_PORT_NBR";
  public final static String TERMINAL_SERVER_COLUMN = "TSRV_DVC_ID";
  public final static String TERMINAL_SERVER_PORT_NUMBER_COLUMN = "TSRV_PORT_NBR";
  public final static String RPC_COLUMN = "RPC_DVC_ID";
  public final static String RPC_ADDRESS_COLUMN = "RPC_ADDR";
  public final static String NET_DOMAIN_COLUMN = "NET_DOMAIN";
  public final static String NET_MASK_COLUMN = "NET_MASK";
  public final static String VLAN_NUMBER_COLUMN = "VLAN_NBR";
  public final static String CPU_TYPE_COLUMN = "CPU_TYPE";
  public final static String SERIAL_NUMBER_COLUMN = "IOC_SER_NBR";
  public final static String TTY_NAME_COLUMN = "TTY_NM";
  public final static String EPICS_VERSION_COLUMN = "EPICS_VERSION";
  public final static String VX_WORKS_BSP_VERSION_COLUMN = "VXWORKS_BSP_VER";
  public final static String GATEWAY_ADDRESS_COLUMN = "GATEWAY_ADR";
  public final static String LOGIN_NAME_COLUMN = "LOGIN_NM";
  /**
   * Holds the name of the RDB column to which the parse indicator property 
   * refers.
   */
  public final static String PARSE_INDICATOR_COLUMN = "PARSE_IND";
  /**
   * Holds the name of the RDB column to which the parse indicator property
   * refers.
   */
  public final static String VX_WORKS_OS_VERSION_COLUMN = "VXWORKS_OS_VER";
  /**
   * The name of the table for the class.
   */
  public final static String IOC_TABLE_NAME = "IOC_DVC";
  /**
   * Holds the value of the vx works OS version property.
   */
  private String vxWorksOSVersion;

  /**
   * Creates a new <CODE>IOC</CODE>.
   */
  public IOC()
  {
  }

  /**
   * Creates the <CODE>IOC</CODE> from the given <CODE>ResultSet</CODE>.
   * 
   * @param result The <CODE>ResultSet</CODE> with which to create the <CODE>IOC</CODE>.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public IOC(ResultSet result) throws java.sql.SQLException
  {
    super(result, createTypeMap());
  }
  
  /**
   * Creates a new <CODE>IOC</CODE> with the given ID.
   * 
   * @param id The ID for the new <CODE>IOC</CODE>.
   */
  public IOC(String id)
  {
    this();
    setID(id);
  }

  /**
   * Creates a <CODE>Map</CODE> that contains the type mappings for the class.
   */
  static private Map createTypeMap()
  {
    HashMap map = new HashMap();
    map.put("DATE", Timestamp.class);
    return map;
  }
  
  /**
   * Adds the given <CODE>DBFile</CODE> to the <CODE>IOC</CODE>.
   * 
   * @param dbFile The <CODE>DBFile</CODE> to add to the <CODE>IOC</CODE>.
   */
  public void addDBFile(DBFile dbFile)
  {
    DBFile[] oldDBFiles = getDBFiles();
    if(getDBFile(dbFile.getFileName()) == null)
      dbFiles.add(dbFile);
    if(dbFile.getIOC() != this)
      dbFile.setIOC(this);
    firePropertyChange("dbFiles", oldDBFiles, getDBFiles());
  }
  
  /**
   * Returns the number of instances of <CODE>DBFile</CODE> that have been added
   * to the <CODE>IOC</CODE>.
   * 
   * @return The number of instances of <CODE>DBFile</CODE> that have been added to the <CODE>IOC</CODE>.
   */
  public int getDBFileCount()
  {
    return dbFiles.size();
  }
  
  /**
   * Gets the instance of <CODE>DBFile</CODE> at the given index.
   * 
   * @param index The index of the <CODE>DBFile</CODE> to return.
   * @return The <CODE>DBFile</CODE> at the given index.
   */
  public DBFile getDBFileAt(int index)
  {
    return (DBFile)dbFiles.get(index);
  }

  /**
   * Gets the <CODE>DBFile</CODE> with the given file name. If a 
   * <CODE>DBFile</CODE> with the name has not been added to the 
   * <CODE>IOC</CODE>, <CODE>null</CODE> is returned.
   * 
   * @param name The name of the <CODE>DBFile</CODE> to return.
   * @return The <CODE>DBFile</CODE> with the given name, <CODE>null</CODE> if one is not found.
   */
  public DBFile getDBFile(String name)
  {
    int index = getIndexOfDBFile(name);
    if(index == -1)
      return null;
    else
      return getDBFileAt(index);
  }

  /**
   * Gets the index of the <CODE>DBFile</CODE> with the given name.
   * 
   * @param name The name of the <CODE>DBFile</CODE> of which to find the index.
   * @return The index of the <CODE>DBFile</CODE> with the given name. <CODE>-1</CODE> if it is not found.
   */
  public int getIndexOfDBFile(String name)
  {
    int dbFileCount = getDBFileCount();
    for(int i=0;i<dbFileCount;i++) 
    {
      DBFile file = getDBFileAt(i);
      if(MPSBrowserView.compare(file.getFileName(), name))
        return i;
    }
    return -1;
  }

  /**
   * Gets the instances of <CODE>DBFile</CODE> associated with the 
   * <CODE>IOC</CODE>.
   * 
   * @return The instances of <CODE>DBFile</CODE> associated with the <CODE>IOC</CODE>.
   */
  public DBFile[] getDBFiles()
  {
    return (DBFile[])dbFiles.toArray(new DBFile[dbFiles.size()]);
  }

  /**
   * Removes the first <CODE>DBFile</CODE> with the given name.
   * 
   * @param name The name of the <CODE>DBFile</CODE> to remove.
   */
  public void removeDBFile(String name)
  {
    int index = getIndexOfDBFile(name);
    if(index >= 0)
      removeDBFileAt(index);
  }

  /**
   * Removes the <CODE>DBFile</CODE> at the given index.
   * 
   * @param index The index of the <CODE>DBFile</CODE> to remove.
   */
  public void removeDBFileAt(int index)
  {
    DBFile[] oldDBFiles = getDBFiles();
    DBFile file = (DBFile)dbFiles.remove(index);
    if(file.getIOC() == this)
      file.setIOC(null);
    firePropertyChange("dbFiles", oldDBFiles, getDBFiles());
  }

  /**
   * Adds a <CODE>PowerSupplyController</CODE> to the <CODE>IOC</CODE>.
   * 
   * @param newPowerSupplyController The <CODE>PowerSupplyController</CODE> to add to the <CODE>IOC</CODE>.
   */
  public void addPowerSupplyController(PowerSupplyController newPowerSupplyController)
  {
    powerSupplyControllers.add(newPowerSupplyController);
  }
  
  /**
   * Gets the number of instances of <CODE>PowerSupplyController</CODE> that 
   * have been added to the <CODE>IOC</CODE>.
   * 
   * @return The number of instances of <CODE>PowerSupplyController</CODE> added to the <CODE>IOC</CODE>.
   */
  public int getPowerSupplyControllerCount()
  {
    return powerSupplyControllers.size();
  }
  
  /**
   * Gets the <CODE>PowerSupplyController</CODE> at the given index.
   * 
   * @param index The index of the <CODE>PowerSupplyController</CODE> to return.
   * @return The <CODE>PowerSupplyCOntroller</CODE> at the given index.
   */
  public PowerSupplyController getPowerSupplyControllerAt(int index)
  {
    return (PowerSupplyController)powerSupplyControllers.get(index);
  }
  
  /**
   * Gets the instance of <CODE>PowerSupplyContoller</CODE> with the given ID.
   * 
   * @param id The ID of the <CODE>PowerSupplyController</CODE> to return.
   * @return The <CODE>PowerSupplyController</CODE> with the given ID, <CODE>null</CODE> if no mach is found.
   */
  public PowerSupplyController getPowerSupplyController(String id)
  {
    int controllerCount = getPowerSupplyControllerCount();
    for(int i=0;i<controllerCount;i++) 
    {
      PowerSupplyController currentController = getPowerSupplyControllerAt(i);
      if(id.equals(currentController.getID()))
        return currentController;
    }
    return null;
  }

  /**
   * Adds the given boot information to the <CODE>IOC</CODE>.
   * 
   * @param bootDate The <CODE>Date</CODE> of the boot.
   */
  public void addBootDate(Timestamp bootDate)
  {
    Timestamp[] oldValue = getBootDates();
    boots.put(bootDate, new ArrayList());
    Timestamp[] newValue = getBootDates();
    firePropertyChange("bootDates", oldValue, newValue);
  }
  
  /**
   * Adds the given <CODE>IOCSoftware</CODE> to the boot.
   * 
   * @param bootDate The boot date to which to add the software.
   * @param software The software to add.
   */
  public void addSoftware(Timestamp bootDate, IOCSoftware software)
  {
    IOCSoftware[] oldValue = getSoftware(bootDate);
    ArrayList softwareForDate = (ArrayList)boots.get(bootDate);
    if(softwareForDate == null)
    {
      addBootDate(bootDate);
      softwareForDate = (ArrayList)boots.get(bootDate);
    }
    softwareForDate.add(software);
    if(software.getIOC() != this)
      software.setIOC(this, bootDate);
    IOCSoftware[] newValue = getSoftware(bootDate);
    firePropertyChange("software", oldValue, newValue);
  }

  /**
   * Gets the <CODE>IOCSoftware</CODE> for a specific boot date.
   * 
   * @param bootDate The boot date for which to return the <CODE>IOCSoftware</CODE>.
   * @return The <CODE>IOCSoftware</CODE> for a boot date.
   */
  public IOCSoftware[] getSoftware(Timestamp bootDate)
  {
    ArrayList software = (ArrayList)boots.get(bootDate);
    if(software == null)
      return new IOCSoftware[0];
    return (IOCSoftware[])software.toArray(new IOCSoftware[software.size()]);
  }
  
  /**
   * Gets the boot dates for the <CODE>IOC</CODE>.
   * 
   * @return The boot dates for the <CODE>IOC</CODE>.
   */
  public Timestamp[] getBootDates()
  {
    Set bootDates = boots.keySet();
    return (Timestamp[])bootDates.toArray(new Timestamp[bootDates.size()]);
  }

  /**
   * Gets the <CODE>IOCSoftware</CODE> for the given boot date.
   * 
   * @param bootDate The boot date for which to return the software.
   * @return The software for the <CODE>IOC</CODE> on the given <CODE>Date</CODE> or <CODE>null</CODE>.
   */
  public IOCSoftware[] findSoftware(Timestamp bootDate)
  {
    ArrayList software = (ArrayList)boots.get(bootDate);
    return (IOCSoftware[])software.toArray(new IOCSoftware[software.size()]);
  }
  
  /**
   * Looks for and returns the <CODE>IOCSoftware</CODE> that matches the given name and boot date.
   * 
   * @param bootDate The boot date of the <CODE>IOCSoftware</CODE> to find.
   * @param softwareName The name of the <CODE>IOCSoftware</CODE> to find.
   * @return The matching <CODE>IOCSoftware</CODE>, or <CODE>null</CODE> of no matchi is found.
   */
  public IOCSoftware findSoftware(Timestamp bootDate, String softwareName)
  {
    IOCSoftware[] software = findSoftware(bootDate);
    if(software != null)
      for(int i=0;i<software.length;i++) 
        if(software[i].getName().equals(softwareName))
          return software[i];
    return null;
  }

  public String getNetName()
  {
    return netName;
  }

  public void setNetName(String netName)
  {
    String oldNetName = netName;
    this.netName = netName;
    firePropertyChange("netName", oldNetName, netName);
  }

  public Timestamp getBootDate()
  {
    return bootDate;
  }

  public void setBootDate(Timestamp bootDate)
  {
    Timestamp oldBootDate = bootDate;
    this.bootDate = bootDate;
    firePropertyChange("bootDate", oldBootDate, bootDate);
  }

  public String getBootFile()
  {
    return bootFile;
  }

  public void setBootFile(String bootFile)
  {
    String oldBootFile = bootFile;
    this.bootFile = bootFile;
    firePropertyChange("bootFile", oldBootFile, bootFile);
  }

  public String getBootServer()
  {
    return bootServer;
  }

  public void setBootServer(String bootServer)
  {
    String oldBootServer = bootServer;
    this.bootServer = bootServer;
    firePropertyChange("bootServer", oldBootServer, bootServer);
  }

  public String getIPAddress()
  {
    return ipAddress;
  }

  public void setIPAddress(String ipAddress)
  {
    String oldIPAddress = ipAddress;
    this.ipAddress = ipAddress;
    firePropertyChange("ipAddress", oldIPAddress, ipAddress);
  }

  public String getMACAddress()
  {
    return macAddress;
  }

  public void setMACAddress(String macAddress)
  {
    String oldMACAddress = macAddress;
    this.macAddress = macAddress;
    firePropertyChange("macAddress", oldMACAddress, macAddress);
  }

  public Device getSwitchDevice()
  {
    return switchDevice;
  }

  public void setSwitchDevice(Device switchDevice)
  {
    Device oldSwitchDevice = switchDevice;
    this.switchDevice = switchDevice;
    firePropertyChange("switchDevice", oldSwitchDevice, switchDevice);
  }

  public int getSwitchPortNumber()
  {
    return switchPortNumber;
  }

  public void setSwitchPortNumber(int switchPortNumber)
  {
    int oldSwitchPortNumber = switchPortNumber;
    this.switchPortNumber = switchPortNumber;
    firePropertyChange("switchPortNumber", new Integer(oldSwitchPortNumber), new Integer(switchPortNumber));
  }

  public Device getTerminalServer()
  {
    return terminalServer;
  }

  public void setTerminalServer(Device terminalServer)
  {
    Device oldTerminalServer = terminalServer;
    this.terminalServer = terminalServer;
    firePropertyChange("terminalServer", oldTerminalServer, terminalServer);
  }

  public String getTerminalServerPortNumber()
  {
    return terminalServerPortNumber;
  }

  public void setTerminalServerPortNumber(String terminalServerPortNumber)
  {
    String oldTerminalServerPortNumber = terminalServerPortNumber;
    this.terminalServerPortNumber = terminalServerPortNumber;
    firePropertyChange("terminalServerPortNumber", oldTerminalServerPortNumber, terminalServerPortNumber);
  }

  public Device getRPC()
  {
    return rpc;
  }

  public void setRPC(Device rpc)
  {
    Device oldRPC = rpc;
    this.rpc = rpc;
    firePropertyChange("rpc", oldRPC, rpc);
  }

  public String getRPCAddress()
  {
    return rpcAddress;
  }

  public void setRPCAddress(String rpcAddress)
  {
    String oldRPCAddress = rpcAddress;
    this.rpcAddress = rpcAddress;
    firePropertyChange("rpcAddress", oldRPCAddress, rpcAddress);
  }

  public String getNetDomain()
  {
    return netDomain;
  }

  public void setNetDomain(String netDomain)
  {
    String oldNetDomain = netDomain;
    this.netDomain = netDomain;
    firePropertyChange("netDomain", oldNetDomain, netDomain);
  }

  public String getNetMask()
  {
    return netMask;
  }

  public void setNetMask(String netMask)
  {
    String oldNetMask = netMask;
    this.netMask = netMask;
    firePropertyChange("netMask", oldNetMask, netMask);
  }

  public String getVLANNumber()
  {
    return vlanNumber;
  }

  public void setVLANNumber(String vlanNumber)
  {
    String oldVLANNumber = vlanNumber;
    this.vlanNumber = vlanNumber;
    firePropertyChange("vlanNumber", oldVLANNumber, vlanNumber);
  }

  public String getCPUType()
  {
    return cpuType;
  }

  public void setCPUType(String cpuType)
  {
    String oldCPUType = cpuType;
    this.cpuType = cpuType;
    firePropertyChange("cpuType", oldCPUType, cpuType);
  }

  public String getSerialNumber()
  {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber)
  {
    String oldSerialNumber = serialNumber;
    this.serialNumber = serialNumber;
    firePropertyChange("serialNumber", oldSerialNumber, serialNumber);
  }

  public String getTTYName()
  {
    return ttyName;
  }

  public void setTTYName(String ttyName)
  {
    String oldTTYName = ttyName;
    this.ttyName = ttyName;
    firePropertyChange("ttyName", oldTTYName, ttyName);
  }

  public String getEpicsVersion()
  {
    return epicsVersion;
  }

  public void setEpicsVersion(String epicsVersion)
  {
    String oldEpicsVersion = epicsVersion;
    this.epicsVersion = epicsVersion;
    firePropertyChange("epicsVersion", oldEpicsVersion, epicsVersion);
  }

  public String getVxWorksBSPVersion()
  {
    return vxWorksBSPVersion;
  }

  public void setVxWorksBSPVersion(String vxWorksBSPVersion)
  {
    String oldVxWorksBSPVersion = vxWorksBSPVersion;
    this.vxWorksBSPVersion = vxWorksBSPVersion;
    firePropertyChange("vxWorksBSPVersion", oldVxWorksBSPVersion, vxWorksBSPVersion);
  }

  public String getGatewayAddress()
  {
    return gatewayAddress;
  }

  public void setGatewayAddress(String gatewayAddress)
  {
    String oldGatewayAddress = gatewayAddress;
    this.gatewayAddress = gatewayAddress;
    firePropertyChange("gatewayAddress", oldGatewayAddress, gatewayAddress);
  }

  public String getLoginName()
  {
    return loginName;
  }

  public void setLoginName(String loginName)
  {
    String oldLoginName = loginName;
    this.loginName = loginName;
    firePropertyChange("loginName", oldLoginName, loginName);
  }
  
  /**
   * Loads the counts for the signal fields from the RDB.
   * 
   * @param connection The <CODE>Connection</CODE> to use to connect to the RDB.
   * @return A <CODE>LinkedHashMap</CODE> containing the field IDs as keys and the counts as values.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public LinkedHashMap loadFieldCounts(Connection connection) throws java.sql.SQLException
  {
    String message = "Loading Field Counts";
    try
    {
      fireTaskStarted(new ProgressEvent(this, message, 0, -1));
      LinkedHashMap allFieldIDCounts = new LinkedHashMap();
      StringBuffer sql = new StringBuffer("SELECT COUNT(*)");
      String whereClause = " from EPICS.ioc_db_file_asgn_sgnl a, EPICS.sgnl_fld b where a.sgnl_id = b.sgnl_id and a.dvc_id = ? group by fld_id";
      sql.append(whereClause);
      int recordCount;
      PreparedStatement countQuery = connection.prepareStatement(sql.toString());
      try
      {
        countQuery.setString(1, getID());
        recordCount = findRecordCount(countQuery);
      }
      finally
      {
        countQuery.close();
      }
      sql = new StringBuffer("select fld_id, count(fld_id)");
      sql.append(whereClause);
      sql.append(" order by 2 desc");
      PreparedStatement query = connection.prepareStatement(sql.toString());
      try
      {
        query.setString(1, getID());
        ResultSet result = query.executeQuery();
        try
        {
          int progress = 0;
          fireProgress(new ProgressEvent(this, message, progress, recordCount));
          while(result.next())
          {
            String fieldID = result.getString("FLD_ID");
            int count = result.getInt("COUNT(FLD_ID)");
            Integer fieldCount = new Integer(count);
            allFieldIDCounts.put(fieldID, fieldCount);
            fireProgress(new ProgressEvent(this, message, ++progress, recordCount));
          }
          return allFieldIDCounts;
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
    catch(java.sql.SQLException ex)
    {
      fireTaskInterrupted(new ProgressEvent(this, ex.getMessage(), 0, 0));
      throw ex;
    }
    finally
    {
      fireTaskComplete(new ProgressEvent(this, null, 0, 0));
    }
  }
  
  /**
   * Loads the counts for the signal record types from the RDB.
   * 
   * @param connection The <CODE>Connection</CODE> to use to connect to the RDB.
   * @return A <CODE>LinkedHashMap</CODE> containing the record type IDs as keys and the counts as values.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public LinkedHashMap loadRecordTypeCounts(Connection connection) throws java.sql.SQLException
  {
    String message = "Loading Record Counts";
    fireTaskStarted(new ProgressEvent(this, message, 0, -1));
    try
    {
      LinkedHashMap allRecordTypeCounts = new LinkedHashMap();
      StringBuffer sql = new StringBuffer("SELECT COUNT(*)");
      String whereClause = " from EPICS.ioc_db_file_asgn_sgnl a, EPICS.sgnl_rec b where a.sgnl_id = b.sgnl_id and a.dvc_id = ? group by rec_type_id";
      sql.append(whereClause);
      int recordCount;
      PreparedStatement countQuery = connection.prepareStatement(sql.toString());
      try
      {
        countQuery.setString(1, getID());
        recordCount = findRecordCount(countQuery);
      }
      finally
      {
        countQuery.close();
      }
      sql = new StringBuffer("select rec_type_id, count(rec_type_id)");
      sql.append(whereClause);
      sql.append(" order by 2 desc");
      PreparedStatement query = connection.prepareStatement(sql.toString());
      try
      {
        query.setString(1, getID());
        ResultSet result = query.executeQuery();
        try
        {
          int progress = 0;
          fireProgress(new ProgressEvent(this, message, progress, recordCount));
          while(result.next())
          {
            String fieldID = result.getString("REC_TYPE_ID");
            int count = result.getInt("COUNT(REC_TYPE_ID)");
            Integer fieldCount = new Integer(count);
            allRecordTypeCounts.put(fieldID, fieldCount);
            fireProgress(new ProgressEvent(this, message, ++progress, recordCount));
          }
          return allRecordTypeCounts;
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
    catch(java.sql.SQLException ex)
    {
      fireTaskInterrupted(new ProgressEvent(this, ex.getMessage(), 0, 0));
      throw ex;
    }
    finally
    {
      fireTaskComplete(new ProgressEvent(this, null, 0, 0));
    }
  }
  
  /**
   * Deletes the <CODE>IOC</CODE> from the database.
   * 
   * @param connection The <CODE>Connection</CODE> to use to connect to the RDB.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void delete(Connection connection) throws java.sql.SQLException
  {
    String message = "Deleting IOC from the RDB";
    fireTaskStarted(new ProgressEvent(this, message, 0, -1));
    try
    {
      String iocID = getID();
      PreparedStatement dbFileAssociationDeleteQuery = connection.prepareStatement("DELETE FROM EPICS.IOC_DB_FILE_ASGN WHERE DVC_ID = ?");
      try
      {
        dbFileAssociationDeleteQuery.setString(1, iocID);
        dbFileAssociationDeleteQuery.execute();
        setCommitNeeded(true);
        PreparedStatement iocDeleteQuery = connection.prepareStatement("DELETE FROM EPICS.IOC_DVC WHERE DVC_ID = ?");
        try
        {
          iocDeleteQuery.setString(1, iocID);
          iocDeleteQuery.execute();
        }
        finally
        {
          iocDeleteQuery.close();
        }
      }
      finally
      {
        dbFileAssociationDeleteQuery.close();
      }
    }
    catch(java.sql.SQLException ex)
    {
      fireTaskInterrupted(new ProgressEvent(this, ex.getMessage(), 0, 0));
      throw ex;
    }
    finally
    {
      fireTaskComplete(new ProgressEvent(this, null, 0, 0));
    }
  }

  /**
   * Associates the <CODE>DBFile</CODE> with the <CODE>IOC</CODE> in the RDB.
   * 
   * @param connection The <CODE>Connection</CODE> to the RDB.
   * @param dbFile The <CODE>DBFile</CODE> to associate with the <CODE>IOC</CODE>.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void insertDBFile(Connection connection, DBFile dbFile) throws java.sql.SQLException
  {
    String iocID = getID();
    PreparedStatement query = connection.prepareStatement("INSERT INTO EPICS.IOC_DB_FILE_ASGN (DVC_ID, EXT_SRC_FILE_NM, EXT_SRC_DIR_NM) VALUES (?, ?, ?)");
    try
    {
      query.setString(1, iocID);
      query.setString(2, dbFile.getFileName());
      query.setString(3, dbFile.getDirectoryName());
      query.execute();
      setCommitNeeded(true);
    }
    finally
    {
      query.close();
    }
  }
  
  /**
   * Delets the associaction between the <CODE>IOC</CODE> and the 
   * <CODE>DBFile</CODE>. This method does not remove the <CODE>DBFile</CODE> 
   * from the RDB or the instance of <CODE>IOC</CODE>.
   * 
   * @param connection The <CODE>Connection</CODE> to use to connect to the RDB.
   * @param dbFile The <CODE>DBFile</CODE> to remove from the <CODE>IOC</CODE> in the RDB.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void deleteDBFile(Connection connection, String dbFileName) throws java.sql.SQLException
  {
    String iocID = getID();
    PreparedStatement signalQuery = connection.prepareStatement("DELETE FROM EPICS.IOC_DB_FILE_ASGN_SGNL WHERE DVC_ID = ? AND EXT_SRC_FILE_NM = ?");
    try
    {
      signalQuery.setString(1, iocID);
      signalQuery.setString(2, dbFileName);
      signalQuery.execute();    
      setCommitNeeded(true);
    }
    finally
    {
      signalQuery.close();
    }
    PreparedStatement fileQuery = connection.prepareStatement("DELETE FROM EPICS.IOC_DB_FILE_ASGN WHERE DVC_ID = ? AND EXT_SRC_FILE_NM = ?");
    try
    {
      fileQuery.setString(1, iocID);
      fileQuery.setString(2, dbFileName);
      fileQuery.execute();    
    }
    finally
    {
      fileQuery.close();
    }
  }

  /**
   * Loads the boot data for the <CODE>IOC</CODE> from the RDB.
   * 
   * @param connection The <CODE>Connection</CODE> through which to load the boot data.
   */
  public void loadBootData(Connection connection) throws java.sql.SQLException
  {
    String message = "Loading IOC Boot Data";
    try
    {
      fireTaskStarted(new ProgressEvent(this, message, 0, -1));
      StringBuffer sql = new StringBuffer("SELECT COUNT(*)");
      String whereClause = " FROM EPICS.IOC_BOOT_HIST, EPICS.IOC_SFTW WHERE IOC_BOOT_HIST.DVC_ID = ? AND IOC_BOOT_HIST.DVC_ID = IOC_SFTW.DVC_ID (+) AND IOC_BOOT_HIST.BOOT_DTE = IOC_SFTW.BOOT_DTE (+)";
      sql.append(whereClause);
      int recordCount;
      String id = getID();
      PreparedStatement countQuery = connection.prepareStatement(sql.toString());
      try
      {
        countQuery.setString(1, id);
        ResultSet result = countQuery.executeQuery();
        try
        {
          result.next();
          recordCount = result.getInt(1);
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
      sql = new StringBuffer("SELECT IOC_BOOT_HIST.BOOT_DTE, SFTW_NM, SFTW_VER, SFTW_LOC");
      sql.append(whereClause);
      sql.append(" ORDER BY IOC_BOOT_HIST.BOOT_DTE");
      PreparedStatement query = connection.prepareStatement(sql.toString());
      try
      {
        query.setString(1, id);
        ResultSet result = query.executeQuery();
        try
        {
          int progress = 0;
          fireProgress(new ProgressEvent(this, message, progress, recordCount));
          while(result.next())
          {
            Timestamp bootDate = result.getTimestamp("BOOT_DTE");
            String softwareName = result.getString("SFTW_NM");
            if(softwareName != null)
            {
              IOCSoftware software = new IOCSoftware(softwareName);
              software.setVersion(result.getString("SFTW_VER"));
              software.setLocation(result.getString("SFTW_LOC"));
              addSoftware(bootDate, software);
            }
            else
              addBootDate(bootDate);
            fireProgress(new ProgressEvent(this, message, ++progress, recordCount));
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
    catch(java.sql.SQLException ex)
    {
      fireTaskInterrupted(new ProgressEvent(this, ex.getMessage(), 0, 0));
      throw ex;
    }
    finally
    {
      fireTaskComplete(new ProgressEvent(this, null, 0, 0));
    }
  }

  /**
   * Should return the value of the RDB field as stored in the 
   * <CODE>RDBData</CODE> instance.
   * 
   * @param rdbFieldName The name of the field of which to return the value.
   * @return The value of the property that corresponds to the given RDB field.
   */
  protected Object getValue(String rdbFieldName)
  {
    if(rdbFieldName.equals(NET_NAME_COLUMN))
      return getNetName();
    if(rdbFieldName.equals(BOOT_DATE_COLUMN))
      return getBootDate();
    if(rdbFieldName.equals(BOOT_FILE_COLUMN))
      return getBootFile();
    if(rdbFieldName.equals(BOOT_SERVER_COLUMN))
      return getBootServer();
    if(rdbFieldName.equals(IP_ADDRESS_COLUMN))
      return getIPAddress();
    if(rdbFieldName.equals(MAC_ADDRESS_COLUMN))
      return getMACAddress();
    if(rdbFieldName.equals(SWITCH_DEVICE_COLUMN))
      return getSwitchDevice();
    if(rdbFieldName.equals(SWITCH_PORT_NUMBER_COLUMN))
      return new Integer(getSwitchPortNumber());
    if(rdbFieldName.equals(TERMINAL_SERVER_COLUMN))
      return getTerminalServer();
    if(rdbFieldName.equals(TERMINAL_SERVER_PORT_NUMBER_COLUMN))
      return getTerminalServerPortNumber();
    if(rdbFieldName.equals(RPC_COLUMN))
      return getRPC();
    if(rdbFieldName.equals(RPC_ADDRESS_COLUMN))
      return getRPCAddress();
    if(rdbFieldName.equals(NET_DOMAIN_COLUMN))
      return getNetDomain();
    if(rdbFieldName.equals(NET_MASK_COLUMN))
      return getNetMask();
    if(rdbFieldName.equals(VLAN_NUMBER_COLUMN))
      return getVLANNumber();
    if(rdbFieldName.equals(CPU_TYPE_COLUMN))
      return getCPUType();
    if(rdbFieldName.equals(SERIAL_NUMBER_COLUMN))
      return getSerialNumber();
    if(rdbFieldName.equals(TTY_NAME_COLUMN))
      return getTTYName();
    if(rdbFieldName.equals(EPICS_VERSION_COLUMN))
      return getEpicsVersion();
    if(rdbFieldName.equals(VX_WORKS_BSP_VERSION_COLUMN))
      return getVxWorksBSPVersion();
    if(rdbFieldName.equals(VX_WORKS_OS_VERSION_COLUMN))
      return getVxWorksOSVersion();
    if(rdbFieldName.equals(GATEWAY_ADDRESS_COLUMN))
      return getGatewayAddress();
    if(rdbFieldName.equals(LOGIN_NAME_COLUMN))
      return getLoginName();
    if(rdbFieldName.equals(PARSE_INDICATOR_COLUMN))
      return getParseIndicator();
    return super.getValue(rdbFieldName);
  }

  /**
   * Should set the value of the property to the value of the RDB field. If the 
   * field name passed in does not correspond to a property in the class, it 
   * should just be ignored.
   * 
   * @param rdbFieldName The name of the field for the data.
   * @param value The value of the RDB field.
   * @return The value of the property that corresponds to the given RDB field.
   */
  protected void setValue(String rdbFieldName, Object value)
  {
    if(rdbFieldName.equalsIgnoreCase(NET_NAME_COLUMN))
      setNetName((String)value);
    else if(rdbFieldName.equalsIgnoreCase(BOOT_DATE_COLUMN))
      setBootDate((Timestamp)value);
    else if(rdbFieldName.equalsIgnoreCase(BOOT_FILE_COLUMN))
      setBootFile((String)value);
    else if(rdbFieldName.equalsIgnoreCase(BOOT_SERVER_COLUMN))
      setBootServer((String)value);
    else if(rdbFieldName.equalsIgnoreCase(IP_ADDRESS_COLUMN))
      setIPAddress((String)value);
    else if(rdbFieldName.equalsIgnoreCase(MAC_ADDRESS_COLUMN))
      setMACAddress((String)value);
    else if(rdbFieldName.equalsIgnoreCase(SWITCH_PORT_NUMBER_COLUMN))
      if(value == null)
        setSwitchPortNumber(0);
      else
        setSwitchPortNumber(((BigDecimal)value).intValue());
    else if(rdbFieldName.equalsIgnoreCase(TERMINAL_SERVER_PORT_NUMBER_COLUMN))
      setTerminalServerPortNumber((String)value);
    else if(rdbFieldName.equalsIgnoreCase(RPC_ADDRESS_COLUMN))
      setRPCAddress((String)value);
    else if(rdbFieldName.equalsIgnoreCase(NET_DOMAIN_COLUMN))
      setNetDomain((String)value);
    else if(rdbFieldName.equalsIgnoreCase(NET_MASK_COLUMN))
      setNetMask((String)value);
    else if(rdbFieldName.equalsIgnoreCase(VLAN_NUMBER_COLUMN))
      setVLANNumber((String)value);
    else if(rdbFieldName.equalsIgnoreCase(CPU_TYPE_COLUMN))
      setCPUType((String)value);
    else if(rdbFieldName.equalsIgnoreCase(SERIAL_NUMBER_COLUMN))
      setSerialNumber((String)value);
    else if(rdbFieldName.equalsIgnoreCase(TTY_NAME_COLUMN))
      setTTYName((String)value);
    else if(rdbFieldName.equalsIgnoreCase(EPICS_VERSION_COLUMN))
      setEpicsVersion((String)value);
    else if(rdbFieldName.equalsIgnoreCase(VX_WORKS_BSP_VERSION_COLUMN))
      setVxWorksBSPVersion((String)value);
    else if(rdbFieldName.equalsIgnoreCase(VX_WORKS_OS_VERSION_COLUMN))
      setVxWorksOSVersion((String)value);
    else if(rdbFieldName.equalsIgnoreCase(GATEWAY_ADDRESS_COLUMN))
      setGatewayAddress((String)value);
    else if(rdbFieldName.equalsIgnoreCase(LOGIN_NAME_COLUMN))
      setLoginName((String)value);
    else if(rdbFieldName.equalsIgnoreCase(PARSE_INDICATOR_COLUMN))
      setParseIndicator((String)value);
    else if(rdbFieldName.equalsIgnoreCase(SWITCH_DEVICE_COLUMN))
      setSwitchDevice(new Device((String)value));
    else if(rdbFieldName.equalsIgnoreCase(TERMINAL_SERVER_COLUMN))
      setTerminalServer(new Device((String)value));
    else 
      if(rdbFieldName.equalsIgnoreCase(RPC_COLUMN))
        setRPC(new Device((String)value));
      else 
        super.setValue(rdbFieldName, value);
  }
  
  /**
   * Gets the value of the parse indicator property.
   * 
   * @return The value of the parse indicator property.
   */
  public String getParseIndicator()
  {
    return parseIndicator;
  }
  
  /**
   * Returns a <CODE>boolean</CODE> value for the parse indicator flag.
   * 
   * @return <CODE>false</CODE> if the parse indicator is set to <CODE>"N"</CODE>, <CODE>true</CODE> otherwise.
   */
  public boolean isParseIndicator()
  {
    String indicator = getParseIndicator();
    if(indicator == null)
      return true;
    return ! indicator.equals("N");
  }

  /**
   * Sets the value of the parse indicator property.
   * 
   * @param parseIndicator The value of the parse indicator field.
   */
  public void setParseIndicator(String parseIndicator)
  {
    String oldValue = parseIndicator;
    this.parseIndicator = parseIndicator;
    markFieldChanged(PARSE_INDICATOR_COLUMN);
    firePropertyChange("parseIndicator", oldValue, parseIndicator);
  }

  /**
   * Saves any changes to the <CODE>Object</CODE>.
   * 
   * @param connection The <CODE>Connection</CODE> used to connect to the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void update(Connection connection) throws SQLException
  {
    String[] fieldNames = new String[25];
    fieldNames[0] = NET_NAME_COLUMN;
    fieldNames[1] = BOOT_DATE_COLUMN;
    fieldNames[2] = BOOT_FILE_COLUMN;
    fieldNames[3] = BOOT_SERVER_COLUMN;
    fieldNames[4] = IP_ADDRESS_COLUMN;
    fieldNames[5] = MAC_ADDRESS_COLUMN;
    fieldNames[6] = SWITCH_DEVICE_COLUMN;
    fieldNames[7] = SWITCH_PORT_NUMBER_COLUMN;
    fieldNames[8] = TERMINAL_SERVER_COLUMN;
    fieldNames[9] = TERMINAL_SERVER_PORT_NUMBER_COLUMN;
    fieldNames[10] = RPC_COLUMN;
    fieldNames[11] = RPC_ADDRESS_COLUMN;
    fieldNames[12] = NET_DOMAIN_COLUMN;
    fieldNames[13] = NET_MASK_COLUMN;
    fieldNames[14] = VLAN_NUMBER_COLUMN;
    fieldNames[15] = CPU_TYPE_COLUMN;
    fieldNames[16] = SERIAL_NUMBER_COLUMN;
    fieldNames[17] = TTY_NAME_COLUMN;
    fieldNames[18] = EPICS_VERSION_COLUMN;
    fieldNames[19] = VX_WORKS_BSP_VERSION_COLUMN;
    fieldNames[20] = GATEWAY_ADDRESS_COLUMN;
    fieldNames[21] = LOGIN_NAME_COLUMN;
    fieldNames[22] = PARSE_INDICATOR_COLUMN;
    fieldNames[23] = IOC_TABLE_NAME;
    fieldNames[24] = VX_WORKS_OS_VERSION_COLUMN;
    ArrayList changedFields = new ArrayList();
    for(int i=0;i<fieldNames.length;i++) 
      if(isFieldChanged(fieldNames[i]))
        changedFields.add(fieldNames[i]);
    resetChangedFlag(fieldNames);
    super.update(connection);
    int changedFieldCount = changedFields.size();
    if(changedFieldCount > 0)
      for(int i=0;i<changedFieldCount;i++) 
      {
        String[] fieldArray = (String[])changedFields.toArray(new String[changedFieldCount]);
        update(connection, fieldArray, getPrimaryKeys(connection), "IOC_DVC");
      }
  }

  /**
   * Returns the value of the vxworks_os_ver RDB column.
   * 
   * @return The value of the vxworks_os_ver RDB column.
   */
  public String getVxWorksOSVersion()
  {
    return vxWorksOSVersion;
  }

  /**
   * Sets the value of the VX works OS version.
   * 
   * @param vxWorksOSVersion The value of the VX works OS version property.
   */
  public void setVxWorksOSVersion(String vxWorksOSVersion)
  {
    String oldVxWorksOSVersion = vxWorksOSVersion;
    this.vxWorksOSVersion = vxWorksOSVersion;
    firePropertyChange("vxWorksOSVersion", oldVxWorksOSVersion, vxWorksOSVersion);
  }
}