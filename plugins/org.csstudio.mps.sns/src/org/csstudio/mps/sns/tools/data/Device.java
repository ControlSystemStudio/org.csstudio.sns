package org.csstudio.mps.sns.tools.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.mps.sns.view.MPSBrowserView;

/**
 * Provides a class to hold all of the data associated with a 
 * <CODE>Device</CODE>. This class holds the data from a record in the DVC table 
 * in the database.
 * 
 * @author Chris Fowlkes
 */
public class Device extends RDBData implements Cloneable, Comparable
{
  /**
   * Holds the value of the id database field.
   */
  private String id;
  /**
   * Holds the value of the device type id database field.
   * @attribute 
   */
  private DeviceType type;
  /**
   * Holds the value of the description database field.
   */
  private String description;
  /**
   * Holds the <CODE>EpicsSystem</CODE> for the <CODE>Device</CODE>.
   * @attribute 
   */
  private EpicsSystem system;
  /**
   * Holds the <CODE>EpicsSubsystem</CODE> for the <CODE>Device</CODE>.
   * @attribute 
   */
  private EpicsSubsystem subsystem;
  /**
   * Holds the signals that have been associated with this <CODE>Device</CODE>, 
   * mainly through the SGNL table.
   */
  private ArrayList signals = new ArrayList();
  /**
   * Provides a flag to easily determine if the <CODE>Device</CODE> is already 
   * in the database. <CODE>true</CODE> by default.
   */
  private boolean inDatabase = true;
  /**
   * Holds the instance for the <CODE>Device</CODE>. This property holds the 
   * value in the DVC_INST field in the DVC table.
   */
  private String instance;
  /**
   * Holds the invalid device ID indicator.
   */
  private String invalidIDIndicator = "N";
//  /**
//   * Holds the name of the file the <CODE>Device</CODE> was imported from. This
//   * is only used by the import intercafes.
//   */
//  private String sourceFileName;
  /**
   * Holds the active device indicator. Defaults to <CODE>"Y"</CODE>.
   */
  private String activeDeviceIndicator = "Y";
  /**
   * Holds the instances of <CODE>Equipment</CODE> associated with this 
   * <CODE>Device</CODE>.
   */
  private ArrayList equipment = new ArrayList();
  /**
   * Holds the RDB column in which the value of the ID property is stored.
   */
  public final static String DEVICE_ID_COLUMN = "DVC_ID";
  /**
   * Holds the RDB table for the class.
   */
  public final static String DEVICE_TABLE_NAME = "DVC";
  /**
   * Holds the instances of <CODE>Device</CODE> that are children to thie 
   * current <CODE>Device</CODE>.
   */
  private ArrayList childDevices = new ArrayList();
  /**
   * Holds the <CODE>Device</CODE> that is the parent of the current 
   * <CODE>Device</CODE>.
   */
  private Device parent;
  /**
   * Holds the instances of <CODE>Cable</CODE> for the <CODE>Device</CODE>.
   */
  private ArrayList<Cable> cables = new ArrayList();
  
  /**
   * Creates a new <CODE>Device</CODE>.
   */
  public Device()
  {
  }
  
  /**
   * Creates a new <CODE>Device</CODE> with the ID in.
   *
   * @param id The ID for the <CODE>Device</CODE>.
   */
  public Device(String id)
  {
    this();
    setID(id);
  }
  
  /**
   * Creates a new <CODE>Device</CODE> with the ID and description passed in.
   *
   * @param id The ID for the <CODE>Device</CODE>.
   * @param description the description for the <CODE>Device</CODE>.
   */
  public Device(String id, String description)
  {
    this(id);
    setDescription(description);
  }

  /**
   * Creates the <CODE>Device</CODE> from the given <CODE>ResultSet</CODE>.
   * 
   * @param result The <CODE>ResultSet</CODE> with which to create the <CODE>IOC</CODE>.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public Device(ResultSet result) throws java.sql.SQLException
  {
    super(result);
  }

  /**
   * Creates the <CODE>Device</CODE> from the given <CODE>ResultSet</CODE>.
   * 
   * @param result The <CODE>ResultSet</CODE> with which to create the <CODE>IOC</CODE>.
   * @param typeMap a map of data types to column types.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public Device(ResultSet result, Map typeMap) throws java.sql.SQLException
  {
    super(result, typeMap);
  }

  /**
   * Sets the ID for the <CODE>Device</CODE>.
   *
   * @param id The ID of the <CODE>Device</CODE>.
   */
  public void setID(String id)
  {
    String oldID = id;
    this.id = id;
    firePropertyChange("id", oldID, id);
  }

  /**
   * Gets the ID for the <CODE>Device</CODE>.
   *
   * @return The ID of the <CODE>Device</CODE>.
   */
  public String getID()
  {
    return id;
  }

  /**
   * Sets the <CODE>DeviceType</CODE> for the <CODE>Device</CODE>.
   *
   * @param type Instance of <CODE>DeviceType</CODE> holding atleast the device typ ID.
   */
  public void setType(DeviceType type)
  {
    this.type = type;
  }

  /**
   * Gets the <CODE>DeviceType</CODE> for the <CODE>Device</CODE>.
   *
   * @return Instance of <CODE>DeviceType</CODE> holding atleast the device typ ID.
   */
  public DeviceType getType()
  {
    return type;
  }

  /**
   * Provides a <CODE>String</CODE> representation of the <CODE>Device</CODE>. 
   * This description consists solely of the device ID.
   *
   * @return The value of the ID property.
   */
  public String toString()
  {
    String name = getID();
    if(name == null)
      name = "";
    return name;
  }

  /**
   * Sets the description for the <CODE>Device</CODE>.
   * 
   * @param description The description for the <CODE>Device</CODE>.
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * Gets the description for the <CODE>Device</CODE>.
   * 
   * @return The description for the <CODE>Device</CODE>.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Creates a copy of the <CODE>Device</CODE>.
   * 
   * @return A copy of the <CODE>Device</CODE>.
   */
  public Object clone()
  {
    Device clone = new Device(getID(), getDescription());
    DeviceType type = getType();
    if(type != null)
      clone.setType((DeviceType)type.clone());
    EpicsSystem system = getSystem();
    if(system != null)
      clone.setSystem((EpicsSystem)system.clone());
    EpicsSubsystem subsystem = getSubsystem();
    if(subsystem != null)
      clone.setSubsystem((EpicsSubsystem)subsystem.clone());
    String instance = getInstance();
    if(instance != null)
      clone.setInstance(instance);
    clone.setInvalidIDIndicator(getInvalidIDIndicator());
    clone.setInDatabase(isInDatabase());
    return clone;
  }

  /**
   * Tests the given <CODE>Device</CODE> to see if it is equal to the current 
   * one.
   * 
   * @param obj The <CODE>Device</CODE> to compare to.
   * @return <CODE>true</CODE> if the instances of <CODE>Device</CODE> are equal, <CODE>false</CODE> if they are not equal.
   */
  public boolean equals(Object obj)
  {
    //Check for null
    if(obj == null)
      return false;
    //Check class type
    if(! (obj instanceof Device))
      return false;
    //Check ID
    Device compareTo = (Device)obj;
    if(! MPSBrowserView.compare(getID(), compareTo.getID()))
      return false;
    //Check device type
    if(! MPSBrowserView.compare(getType(), compareTo.getType()))
      return false;
    //Check description
    if(! MPSBrowserView.compare(getDescription(), compareTo.getDescription()))
      return false;
    //Check system
    if(! MPSBrowserView.compare(getSystem(), compareTo.getSystem()))
      return false;
    //Check subsystem
    if(! MPSBrowserView.compare(getSubsystem(), compareTo.getSubsystem()))
      return false;
    if(! MPSBrowserView.compare(getInstance(), compareTo.getInstance()))
      return false;
    if(! MPSBrowserView.compare(getInvalidIDIndicator(), compareTo.getInvalidIDIndicator()))
      return false;
    return true;
  }

  /**
   * Returns a hash code for the <CODE>Device</CODE>. If the 
   * <CODE>equals</CODE> method for a class returns <CODE>true</CODE>, the 
   * <CODE>hashCode</CODE> methods for those instances of <CODE>Object</CODE> 
   * must also return the same value. The reverse is not the case, meaning that
   * just because the value returned by the <CODE>hashCode()</CODE> methods of 
   * two instances of a class is the same equality can not be assumed.
   * 
   * @return A hash code for the <CODE>Device</CODE>.
   */
  public int hashCode()
  {
    int hashCode = 1;
    hashCode = hashCode * 37 + findPropertyHashCode(getID());
    hashCode = hashCode * 37 + findPropertyHashCode(getType());
    hashCode = hashCode * 37 + findPropertyHashCode(getDescription());
    hashCode = hashCode * 37 + findPropertyHashCode(getSystem());
    hashCode = hashCode * 37 + findPropertyHashCode(getSubsystem());
    hashCode = hashCode * 37 + findPropertyHashCode(getInstance());
    hashCode = hashCode * 37 + findPropertyHashCode(getInvalidIDIndicator());
    return hashCode;
  }
  
  /**
   * Checks the given <CODE>Object</CODE> for <CODE>null</CODE> before invoking 
   * <CODE>hashCode()</CODE> on it. If <CODE>null</CODE> is passed in, 
   * <CODE>0</CODE> is returned, otherwise the value returned by the 
   * <CODE>hashCode</CODE> method is returned.
   * 
   * @param propertyValue The <CODE>Object</CODE> of which to return the hash code.
   * @return The hash code for the given <CODE>Object</CODE>.
   */
  private int findPropertyHashCode(Object propertyValue)
  {
    if(propertyValue == null)
      return 0;
    else
      return propertyValue.hashCode();
  }

  /**
   * Sets the <CODE>EpicsSystem</CODE> to which the <CODE>Device</CODE> belongs.
   * 
   * @param system The <CODE>EpicsSystem</CODE> for the <CODE>Device</CODE>.
   */
  public void setSystem(EpicsSystem system)
  {
    EpicsSystem oldSystem = this.getSystem();
    if(system != oldSystem)
    {
      this.system = system;
      if(system != null && system.getDevice(getID()) == null)
        system.addDevice(this);
      if(oldSystem != null && oldSystem.getDevice(getID()) != null)
        oldSystem.removeDevice(this);
    }
  }

  /**
   * Gets the <CODE>EpicsSystem</CODE> to which the <CODE>Device</CODE> belongs.
   * 
   * @return The <CODE>EpicsSystem</CODE> for the <CODE>Device</CODE>.
   */
  public EpicsSystem getSystem()
  {
    return system;
  }

  /**
   * Sets the <CODE>EpicsSubsystem</CODE> to which the <CODE>Device</CODE> 
   * belongs.
   * 
   * @param subsystem The <CODE>EpicsSubsystem</CODE> for the <CODE>Device</CODE>.
   */
  public void setSubsystem(EpicsSubsystem subsystem)
  {
    this.subsystem = subsystem;
  }

  /**
   * Gets the <CODE>EpicsSubsystem</CODE> to which the <CODE>Device</CODE> 
   * belongs.
   * 
   * @return The <CODE>EpicsSubsystem</CODE> for the <CODE>Device</CODE>.
   */
  public EpicsSubsystem getSubsystem()
  {
    return subsystem;
  }

  /**
   * Associates the given <CODE>Signal</CODE> with the <CODE>Device</CODE>. This 
   * method is called by the <CODE>setDevice</CODE> method in the 
   * <CODE>Signal</CODE> class.
   * 
   * @param signalToAdd The <CODE>Signal</CODE> to associate with the <CODE>Device</CODE>.
   */
  public void addSignal(Signal signalToAdd)
  {
    signals.add(signalToAdd);
  }

  /**
   * Gets the number of signals that are associated with the <CODE>Device</CODE>.
   * 
   * @return The number of instances of <CODE>Signal</CODE> that have been added to the <CODE>Device</CODE>.
   */
  public int getSignalCount()
  {
    return signals.size();
  }

  /**
   * Gets a perticular <CODE>Signal</CODE> in the device.
   * 
   * @param signalIndex The index of the <CODE>Signal</CODE> to return.
   * @return The <CODE>Signal</CODE> at the given index.
   */
  public Signal getSignalAt(int signalIndex)
  {
    return (Signal)signals.get(signalIndex);
  }

  /**
   * Gets the <CODE>Signal</CODE> with the given signal ID.
   * 
   * @param signalID The ID of the <CODE>Signal</CODE> to return.
   * @return The <CODE>Signal</CODE> with the given signal ID, or <CODE>null</CODE> if the <CODE>Device</CODE> does not contain a <CODE>Signal</CODE> with the given ID.
   */
  public Signal getSignal(String signalID)
  {
    int count = getSignalCount();
    Signal value = null;
    for(int i=0;i<count;i++)
    {
      Signal currentSignal = getSignalAt(i);
      if(currentSignal.getID().equals(signalID))
      {
        value = currentSignal;
        break;
      }//if(currentSignal.getID().equals(signalID))
    }//for(int i=0;i<count;i++)
    return value;
  }

  /**
   * Allows the in database flag to be set for the <CODE>Device</CODE>. This is 
   * needed to determine if the query to save the data should be an insert or 
   * update statement. The default value is <CODE>false</CODE>.
   * 
   * @param inDatabase Pass as <CODE>true</CODE> if the <CODE>Device</CODE> is already in the database, <CODE>false</CODE> if not.
   */
  public void setInDatabase(boolean inDatabase)
  {
    this.inDatabase = inDatabase;
  }

  /**
   * Gets the in database flag for the <CODE>Device</CODE>. This is needed to 
   * determine if the query to save the data should be an insert or update 
   * statement. The default value is <CODE>true</CODE>.
   * 
   * @return <CODE>true</CODE> if the <CODE>Device</CODE> is already in the database, <CODE>false</CODE> if not.
   */
  public boolean isInDatabase()
  {
    return inDatabase;
  }

  /**
   * Sets the instance for the <CODE>Device</CODE>. This property holds the 
   * value in the DVC_INST field in the DVC table.
   * 
   * @param instance The instance for the <CODE>Device</CODE>.
   */
  public void setInstance(String instance)
  {
    this.instance = instance;
  }

  /**
   * Gets the instance for the <CODE>Device</CODE>. This property holds the 
   * value in the DVC_INST field in the DVC table.
   * 
   * @return The instance for the <CODE>Device</CODE>.
   */
  public String getInstance()
  {
    return instance;
  }

  /**
   * Gets the invalid ID indicator.
   * 
   * @return The invalid ID indicator.
   */
  public String getInvalidIDIndicator()
  {
    return invalidIDIndicator;
  }

  /**
   * Sets the invalid ID indicator.
   * 
   * @param invalidIDIndicator <CODE>"Y"</CODE> if the ID is invalid, <CODE>"N"</CODE> if not.
   */
  public void setInvalidIDIndicator(String invalidIDIndicator)
  {
    this.invalidIDIndicator = invalidIDIndicator;
  }

//  public String getSourceFileName()
//  {
//    return sourceFileName;
//  }
//
//  public void setSourceFileName(String sourceFileName)
//  {
//    this.sourceFileName = sourceFileName;
//  }

  /**
   * Takes a device and breaks up the device ID to set other properties. This 
   * method extracts the device type ID, system ID, and subsystem ID embedded in 
   * the device ID and sets the appropriate properties of the 
   * <CODE>Device</CODE> with that information.
   */
  public void expandDeviceID()
  {
    //Device ID format: system_subsystem:deviceType_instance
    //System: required at begining of device ID till _ or :  ([^\s_:]+)
    //Subsystem: optional, will be after _ before :  ([^\s:]*)
    //deviceType: required, from : to _ or digit  ([^\s\d_]+)
    //instance: optional, everything after device type  (\S*)
    Pattern deviceIDPattern = Pattern.compile("([^\\s_:]+)_?([^\\s:]*):([^\\s\\d_]+)(\\S*)");
    Matcher deviceIDMatcher = deviceIDPattern.matcher(getID());
    if(deviceIDMatcher.matches())
    {
      //System
      String currentSystemID = deviceIDMatcher.group(1);
      EpicsSystem currentSystem = new EpicsSystem(currentSystemID);
      setSystem(currentSystem);
      //Subsystem
      String currentSubsystemID = deviceIDMatcher.group(2);
      EpicsSubsystem currentSubsystem = new EpicsSubsystem(currentSubsystemID);
      setSubsystem(currentSubsystem);
      //Device type
      DeviceType currentDeviceType = new DeviceType();
      currentDeviceType.setID(deviceIDMatcher.group(3));
      setType(currentDeviceType);
      //Instance
      String currentInstance = deviceIDMatcher.group(4);
      Pattern instancePattern = Pattern.compile("_(\\D\\S*)");
      Matcher instanceMatcher = instancePattern.matcher(currentInstance);
      if(instanceMatcher.matches())
        currentInstance = instanceMatcher.group(1);//Remove _ if next digit is numeric.
      setInstance(currentInstance);
    }
    else
    {
      setInvalidIDIndicator("Y");
      setSystem(new EpicsSystem());
      setSubsystem(new EpicsSubsystem());
      setType(new DeviceType());
    }//else
  }

  /**
   * Gets the active device indicator.
   * 
   * @return The active device indicator.
   */
  public String getActiveDeviceIndicator()
  {
    return activeDeviceIndicator;
  }

  /**
   * Sets the active device indicator. Default os <CODE>"Y"</CODE>.
   * 
   * @param activeDeviceIndicator <CODE>"Y"</CODE> if the <CODE>Device</CODE> is active, <CODE>"N"</CODE> if not.
   */
  public void setActiveDeviceIndicator(String activeDeviceIndicator)
  {
    this.activeDeviceIndicator = activeDeviceIndicator;
  }

  /**
   * Compares two instances of <CODE>Device</CODE>. This method will throw a 
   * <CODE>NullPointerException</CODE> if the <CODE>Object</CODE> passed in is
   * <CODE>null</CODE> and a <CODE>ClassCastException</CODE> if it is not an 
   * instance of <CODE>Device</CODE>.
   * 
   * @param o The <CODE>Device</CODE> to which to compare this <CODE>Device</CODE>.
   * @return The value returned by comparing the IDs of the instances of <CODE>Device</CODE>.
   */
  public int compareTo(Object o)
  {
    return getID().compareTo(((Device)o).getID());
  }
  
  /**
   * Adds the given instance of <CODE>Equipment</CODE> to the 
   * <CODE>Device</CODE>.
   * 
   * @param id The instance of <CODE>Equipment</CODE> to add to the <CODE>Device</CODE>.
   */
  public void addEquipment(Equipment equipment)
  {
    addEquipment(new DeviceEquipmentAssociation(equipment));
  }
  
  /**
   * Adds the given instance of <CODE>Equipment</CODE> to the 
   * <CODE>Device</CODE>.
   * 
   * @param id The instance of <CODE>Equipment</CODE> to add to the <CODE>Device</CODE>.
   * @param beginDate The begin <CODE>Date</CODE> for the association.
   * @param endDate The end <CODE>Date</CODE> for the association.
   */
  public void addEquipment(Equipment equipment, java.sql.Date beginDate, java.sql.Date endDate)
  {
    addEquipment(new DeviceEquipmentAssociation(equipment, beginDate, endDate));
  }

  /**
   * Adds the given instance of <CODE>Equipment</CODE> to the 
   * <CODE>Device</CODE>.
   * 
   * @param id The <CODE>EquipmentAssociation</CODE> to add to the <CODE>Device</CODE>.
   */
  private void addEquipment(DeviceEquipmentAssociation association)
  {
    int oldEquipmentCount = getEquipmentCount();
    equipment.add(association);
    firePropertyChange("equipmentCount", oldEquipmentCount, getEquipmentCount());
    Equipment newEquipment = association.getEquipment();
    if(newEquipment.getDevice() != this)
      newEquipment.setDevice(this);
  }
  
  /**
   * Gets the number of instances of <CODE>Equipment</CODE> added to the 
   * <CODE>Device</CODE>.
   * 
   * @return The number of instances of <CODE>Equipment</CODE> added to the <CODE>Device</CODE>.
   */
  public int getEquipmentCount()
  {
    return equipment.size();
  }
  
  /**
   * Returns the instance of <CODE>Equipment</CODE> at the given index.
   * 
   * @param index The index of the equipment ID to return.
   * @return The equipment ID at the given index.
   */
  public Equipment getEquipmentAt(int index)
  {
    return getEquipmentAssociationAt(index).getEquipment();
  }
    
  /**
   * Returns the instance of <CODE>EquipmentAssociation</CODE> at the given 
   * index.
   * 
   * @param index The index of the equipment ID to return.
   * @return The equipment ID at the given index.
   */
  private DeviceEquipmentAssociation getEquipmentAssociationAt(int index)
  {
    return (DeviceEquipmentAssociation)equipment.get(index);
  }
  
  /**
   * Looks for an instance of <CODE>Equipment</CODE> with the given ID.
   * 
   * @param id The ID of the instance of <CODE>Equipment</CODE> for which to look.
   * @return The instance of <CODE>Equipment</CODE> at the given index.
   */
  public boolean containsEquipment(String id)
  {
    int count = getEquipmentCount();
    for(int i=0;i<count;i++) 
      if(getEquipmentAt(i).getID().equals(id))
        return true;
    return false;
  }
    
  /**
   * Returns the begin <CODE>Date</CODE> for the association of the instance
   * of <CODE>Equipment</CODE> at the given index.
   * 
   * @param equipmentIndex The index of the instance of <CODE>Equipment</CODE> of which to return the begin <CODE>Date</CODE>.
   * @return The begin <CODE>Date</CODE> for the association with the instance of <CODE>Equipment</CODE> at the given index.
   */
  public java.sql.Date getEquipmentBeginDate(int equipmentIndex)
  {
    return getEquipmentAssociationAt(equipmentIndex).getBeginDate();
  }
  
  /**
   * Returns the end <CODE>Date</CODE> for the association of the instance
   * of <CODE>Equipment</CODE> at the given index.
   * 
   * @param equipmentIndex The index of the instance of <CODE>Equipment</CODE> of which to return the end <CODE>Date</CODE>.
   * @return The end <CODE>Date</CODE> for the association with the instance of <CODE>Equipment</CODE> at the given index.
   */
  public java.sql.Date getEquipmentEndDate(int equipmentIndex)
  {
    return getEquipmentAssociationAt(equipmentIndex).getEndDate();
  }

  /**
   * Sets the parent of the <CODE>Device</CODE>.
   * 
   * @param parent The parent of the <CODE>Device</CODE>.
   */
  public void setParent(Device parent)
  {
    Device oldParent = this.parent;
    this.parent = parent;
    if(parent.getIndexOfChild(this) < 0)
      parent.addChild(this);
    firePropertyChange("parent", oldParent, parent);
    markFieldChanged("parent");
  }

  /**
   * Gets the parent of the <CODE>Device</CODE>.
   * 
   * @return The parent of the <CODE>Device</CODE>.
   */
  public Device getParent()
  {
    return parent;
  }
  
  /**
   * Gets the number of instances of <CODE>Device</CODE> that are children of
   * the <CODE>Device</CODE>.
   * 
   * @return The number of children of the <CODE>Device</CODE>.
   */
  public int getChildCount()
  {
    return childDevices.size();
  }

  /**
   * Gets the instance of <CODE>Device</CODE> for the child at the given index.
   * 
   * @param index The index of the <CODE>Device</CODE> to return.
   * @return The child at the given index.
   */
  public Device getChildAt(int index)
  {
    return (Device)childDevices.get(index);
  }

  /**
   * Gets the index of the given <CODE>Device</CODE>.
   * 
   * @param child The child <CODE>Device</CODE> of which to return the index.
   * @return The index of the child <CODE>Device</CODE>.
   */
  public int getIndexOfChild(Device child)
  {
    return childDevices.indexOf(child);
  }
  
  /**
   * Gets the index of the given <CODE>Device</CODE> with the given ID.
   * 
   * @param childDeviceID The ID of the child <CODE>Device</CODE> of which to return the index.
   * @return The index of the child <CODE>Device</CODE>.
   */
  public int getIndexOfChild(String childDeviceID)
  {
    int childCount = getChildCount();
    for(int i=0;i<childCount;i++) 
      if(childDeviceID.equals(getChildAt(i).getID()))
        return i;
    return -1;
  }

  /**
   * Adds the instance of <CODE>Device</CODE> to the current <CODE>Device</CODE>
   * as a child.
   * 
   * @param child The <CODE>Device</CODE> to add as a child.
   */
  public void addChild(Device child)
  {
    Integer oldValue = new Integer(getChildCount());
    childDevices.add(child);
    if(child.getParent() != this)
      child.setParent(this);
    firePropertyChange("childCount", oldValue, new Integer(getChildCount()));
  }

  /**
   * Gets the unique instances of <CODE>DeviceType</CODE> for the children of
   * the <CODE>Device</CODE>.
   * 
   * @return The instances of <CODE>DeviceType</CODE> to which the children of the <CODE>Device</CODE> belong.
   */
  public DeviceType[] getChildTypes()
  {
    int childCount = getChildCount();
    ArrayList types = new ArrayList();
    for(int i=0;i<childCount;i++) 
    {
      DeviceType childType = getChildAt(i).getType();
      if(! types.contains(childType))
        types.add(childType);
    }
    return (DeviceType[])types.toArray(new DeviceType[types.size()]);
  }

  /**
   * Returns all of the children that have the given <CODE>DeviceType</CODE>.
   * 
   * @param type The <CODE>DeviceType</CODE> of the children to return.
   * @return The child instances of <CODE>Device</CODE> that match the given <CODE>DeviceType</CODE>.
   */
  public Device[] getChildrenByType(DeviceType type)
  {
    int childCount = getChildCount();
    ArrayList children = new ArrayList();
    for(int i=0;i<childCount;i++) 
    {
      Device child = getChildAt(i);
      if(type.equals(child.getType()))
        children.add(child);
    }
    return (Device[])children.toArray(new Device[children.size()]);
  }
  
  /**
   * Provides an associatin between an instance of <CODE>Device</CODE> and an 
   * instance of <CODE>Equipment</CODE>.
   * 
   * @author Chris Fowlkes
   */
  private class DeviceEquipmentAssociation
  {
    /**
     * Holds the begin date of the association.
     */
    private java.sql.Date beginDate;
    /**
     * Holds the end date of the association.
     */
    private java.sql.Date endDate;
    /**
     * Holds the instance of <CODE>Equipment</CODE> being associated.
     */
    private Equipment equipment;

    /**
     * Creates a new instance of <CODE>EquipmentAssociation</CODE>.
     * 
     * @param equipment The instance of <CODE>Equipment</CODE> to associate with the <CODE>Device</CODE>.
     */
    public DeviceEquipmentAssociation(Equipment equipment)
    {
      setEquipment(equipment);
    }

    /**
     * Creates a new instance of <CODE>EquipmentAssociation</CODE>.
     * 
     * @param equipment The instance of <CODE>Equipment</CODE> to associate with the <CODE>Device</CODE>.
     * @param beginDate The <CODE>Date</CODE> the association began.
     * @param endDate The <CODE>Date</CODE> the association ended.
     */
    public DeviceEquipmentAssociation(Equipment equipment, java.sql.Date beginDate, java.sql.Date endDate)
    {
      this(equipment);
      setBeginDate(beginDate);
      setEndDate(endDate);
    }
    
    /**
     * Sets the instance of <CODE>Equipment</CODE> to associate with the 
     * <CODE>Device</CODE>.
     * 
     * @param equipment The instance of <CODE>Equipment</CODE> to associate with the <CODE>Device</CODE>.
     */
    public void setEquipment(Equipment equipment)
    {
      this.equipment = equipment;
    }

    /**
     * Gets the instance of <CODE>Equipment</CODE> associated with the <CODE>Device</CODE>.
     * 
     * @return The instance of <CODE>Equipment</CODE> associated with the <CODE>Device</CODE>.
     */
    public Equipment getEquipment()
    {
      return equipment;
    }
    
    /**
     * Sets the <CODE>Date</CODE> the association began.
     * 
     * @param beginDate The <CODE>Date</CODE> the association began.
     */
    public void setBeginDate(java.sql.Date beginDate)
    {
      this.beginDate = beginDate;
    }
    
    /**
     * Gets the <CODE>Date</CODE> the associatin began.
     * 
     * @return The <CODE>Date</CODE> the association began.
     */
    public java.sql.Date getBeginDate()
    {
      return beginDate;
    }
    
    /**
     * Sets the <CODE>Date</CODE> the association ended.
     * 
     * @param endDate The <CODE>Date</CODE> the association ended.
     */
    public void setEndDate(java.sql.Date endDate)
    {
      this.endDate = endDate;
    }
    
    /**
     * Gets the <CODE>Date</CODE> the associatin ended.
     * 
     * @return The <CODE>Date</CODE> the association ended.
     */
    public java.sql.Date getEndDate()
    {
      return endDate;
    }
  }

  /**
   * Needs to be overridden by the subclass to return the name of the table to 
   * which the data belongs. If no schema is needed, <CODE>null</CODE> should be 
   * returned.
   * 
   * @return The name of the RDB schema that corresponds to the class, or <CODE>null</CODE> if none applies.
   */
  protected String getSchemaName()
  {
    return "EPICS";
  }

  /**
   * Needs to be overridden by the subclass to return the name of the table to 
   * which the data belongs.
   * 
   * @return The name of the RDB table that corresponds to the class.
   */
  protected String getTableName()
  {
    return DEVICE_TABLE_NAME;
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
    if(rdbFieldName.equals(DEVICE_ID_COLUMN))
      return getID();
    throw new java.lang.IllegalArgumentException(rdbFieldName + " is not a valid field name.");
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
    if(rdbFieldName.equalsIgnoreCase(DEVICE_ID_COLUMN))
      setID((String)value);
  }
  
  /**
   * Runs a sql statement that returns a single <CODE>int</CODE> value, such as
   * a count query.
   * 
   * @param query The <CODE>PreparedStatement</CODE> to run to get the record count.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  protected static int findRecordCount(PreparedStatement query) throws java.sql.SQLException
  {
    int count;
    ResultSet result = query.executeQuery();
    try
    {
      if(result.next())
        count = result.getInt(1);
      else
        count = 0;
    }
    finally
    {
      result.close();
    }
    return count;
  }

  /**
   * Gets the number of instances of <CODE>Cable</CODE>.
   * 
   * @return The number of instances of <CODE>Cable</CODE> in the <CODE>Device</CODE>.
   */
  public int getCableCount()
  {
    return cables.size();
  }

  /**
   * Gets the <CODE>Cable</CODE> at the given index.
   * 
   * @param index The index of the <CODE>Cable</CODE> to return.
   * @return The <CODE>Cable</CODE> at the given index.
   */
  public Cable getCableAt(int index)
  {
    return cables.get(index);
  }

  /**
   * Adds the <CODE>Cable</CODE> to the <CODE>Device</CODE>.
   * 
   * @param cable The <CODE>Cable</CODE> to add to the <CODE>Device</CODE>.
   */
  public void addCableFrom(Cable cable)
  {
    cables.add(cable);
    if(cable.getDeviceFrom() != this)
      cable.setDeviceFrom(this);
    int cableCount = getCableCount();
    fireIndexedPropertyChange("cables", cableCount - 1, null, cable);
    firePropertyChange("cableCount", cableCount - 1, cableCount);
  }

  /**
   * Adds the <CODE>Cable</CODE> to the <CODE>Device</CODE>.
   * 
   * @param cable The <CODE>Cable</CODE> to add to the <CODE>Device</CODE>.
   */
  public void addCableTo(Cable cable)
  {
    cables.add(cable);
    if(cable.getDeviceTo() != this)
      cable.setDeviceTo(this);
    int cableCount = getCableCount();
    fireIndexedPropertyChange("cables", cableCount - 1, null, cable);
    firePropertyChange("cableCount", cableCount - 1, cableCount);
  }
  
  /**
   * Gets the <CODE>Cable</CODE> with the given number.
   * 
   * @param cableNumber The number of the <CODE>Cable</CODE> to return.
   * @return The <CODE>Cable</CODE> with the given value for the number property or <CODE>null</CODE> if a matching instance of <CODE>Cable</CODE> is not found.
   */
  public Cable getCable(String cableNumber) 
  {
    int cableCount = getCableCount();
    for(int i=0;i<cableCount;i++) 
    {
      Cable cable = getCableAt(i);
      if(cableNumber.equals(cable.getNumber()))
        return cable;
    }
    return null;
  }
}