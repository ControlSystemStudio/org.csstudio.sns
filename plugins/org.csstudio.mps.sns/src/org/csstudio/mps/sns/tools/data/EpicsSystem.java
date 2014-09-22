package org.csstudio.mps.sns.tools.data;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import org.csstudio.mps.sns.view.MPSBrowserView;
/**
 * This class holds the data stored in the SYS table in the database.
 * 
 * @author Chris Fowlkes
 */
public class EpicsSystem extends RDBData implements Cloneable 
{
  /**
   * Holds the ID of the <CODE>EpicsSystem</CODE>.
   */
  private String id;
  /**
   * Holds the name of the <CODE>EpicsSystem</CODE>.
   */
  private String name;
  /**
   * Holds the instances of <CODE>Device</CODE> that have been added to the
   * <CODE>EpicsSystem</CODE>.
   */
  private ArrayList devices = new ArrayList();
  /**
   * Holds the name of the table for the class.
   */
  public static final String EPICS_SYSTEM_TABLE_NAME = "SYST";
  
  /**
   * Creates a new <CODE>EpicsSystem</CODE>.
   */
  public EpicsSystem()
  {
  }

  /**
   * Creates and initializes a new <CODE>EpicsSystem</CODE>.
   * 
   * @param id The ID of the <CODE>EpicsSystem</CODE>.
   */
  public EpicsSystem(String id)
  {
    this();
    setID(id);
  }

  /**
   * Creates and initializes a new <CODE>EpicsSystem</CODE>.
   * 
   * @param id The ID of the <CODE>EpicsSystem</CODE>.
   * @param name The name of the <CODE>EpicsSystem</CODE>.
   */
  public EpicsSystem(String id, String name)
  {
    this(id);
    setName(name);
  }
  
  /**
   * Sets the ID of the <CODE>EpicsSystem</CODE>.
   * 
   * @param id The value of the SYS_ID field in the database.
   */
  public void setID(String id)
  {
    this.id = id;
  }

  /**
   * Gets the ID of the <CODE>EpicsSystem</CODE>.
   * 
   * @return The value of the SYS_ID field in the database.
   */
  public String getID()
  {
    return id;
  }

  /**
   * Sets the name of the <CODE>EpicsSystem</CODE>.
   * 
   * @param name The value of the SYS_NM field in the database.
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Gets the name of the <CODE>EpicsSystem</CODE>.
   * 
   * @return The value of the SYS_NM field in the database.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Compares two instances of <CODE>EpicsSystem</CODE> for equality.
   * 
   * @param obj The <CODE>EpicsSystem</CODE> to compare to.
   * @return <CODE>true</CODE> if the instances of <CODE>EpicsSystem</CODE> are equal, <CODE>false</CODE> if not.
   */
  public boolean equals(Object obj)
  {
    //Check for null
    if(obj == null)
      return false;
    //Check class type
    if(! (obj instanceof EpicsSystem))
      return false;
    //Check ID
    EpicsSystem compareTo = (EpicsSystem)obj;
    if(! MPSBrowserView.compare(getID(), compareTo.getID()))
      return false;
    //Check name
    if(! MPSBrowserView.compare(getName(), compareTo.getName()))
      return false;
    return true;
  }

  /**
   * Returns a hash code for the <CODE>EpicsSystem</CODE>. If the 
   * <CODE>equals</CODE> method for a class returns <CODE>true</CODE>, the 
   * <CODE>hashCode</CODE> methods for those instances of <CODE>Object</CODE> 
   * must also return the same value. The reverse is not the case, meaning that
   * just because the value returned by the <CODE>hashCode()</CODE> methods of 
   * two instances of a class is the same equality can not be assumed.
   * 
   * @return A hash code for the <CODE>EpicsSystem</CODE>.
   */
  public int hashCode()
  {
    int hashCode = 1;
    hashCode = hashCode * 37 + findPropertyHashCode(getID());
    hashCode = hashCode * 37 + findPropertyHashCode(getName());
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
   * Creates and returns a copy of the <CODE>EpicsSystem</CODE>.
   * 
   * @return A copy of the <CODE>EpicsSystem</CODE>.
   */
  public Object clone()
  {
    return new EpicsSystem(getID(), getName());
  }

  /**
   * Returns the <CODE>String</CODE> representation of the 
   * <CODE>EpicsSystem</CODE>. This is the value of the ID property.
   * 
   * @return The ID of the subsystem.
   */
  public String toString()
  {
    StringBuffer stringValue = new StringBuffer(getID());
    String name = getName();
    if(name != null)
    {
      stringValue.append(" - ");
      stringValue.append(name);
    }
    return stringValue.toString();
  }

  /**
   * Adds the given <CODE>Device</CODE> to the <CODE>EpicsSystem</CODE>.
   * 
   * @param device The <CODE>Device</CODE> to add to the system.
   */
  public void addDevice(Device device)
  {
    Device[] oldDevices = getDevices();
    devices.add(device);
    if(device.getSystem() != this)
      device.setSystem(this);
    firePropertyChange("devices", oldDevices, getDevices());
  }
  
  /**
   * Removes the <CODE>Device</CODE> from the <CODE>EpicsSystem</CODE>.
   * 
   * @param device The <CODE>Device</CODE> to remove from the <CODE>EpicsSystem</CODE>.
   */
  public void removeDevice(Device device)
  {
    Device[] oldDevices = getDevices();
    devices.remove(device);
    if(device.getSystem() == this)
      device.setSystem(null);
    firePropertyChange("devices", oldDevices, getDevices());
  }
  
  /**
   * Gets the number of instances of <CODE>Device</CODE> that have been added to 
   * the <CODE>EpicsSystem</CODE>.
   * 
   * @return The number of instances of <CODE>Device</CODE> that have been added to the <CODE>EpicsSystem</CODE>.
   */
  public int getDeviceCount()
  {
    return devices.size();
  }

  /**
   * Returns the <CODE>Device</CODE> at the given index.
   * 
   * @param index The index of the <CODE>Device</CODE> to return.
   * @return The <CODE>Device</CODE> at the given index.
   */
  public Device getDeviceAt(int index)
  {
    return (Device)devices.get(index);
  }
  
  /**
   * Gets the index of the <CODE>Device</CODE> with the given ID. 
   * <CODE>-1</CODE> is returned if no <CODE>Device</CODE> with the given ID 
   * is found.
   * 
   * @param deviceID The ID of the <CODE>Device</CODE> to return.
   * @return The index of the <CODE>Device</CODE> with the given ID.
   */
  public int getDeviceIndex(String deviceID)
  {
    int count = getDeviceCount();
    for(int i=0;i<count;i++) 
      if(deviceID.equals(getDeviceAt(i).getID()))
        return i;
    return -1;
  }
  
  /**
   * Gets the <CODE>Device</CODE> with the given ID. <CODE>null</CODE> is 
   * returned if no <CODE>Device</CODE> with the given ID is found.
   * 
   * @param deviceID The ID of the <CODE>Device</CODE> to return.
   * @return The index of the <CODE>Device</CODE> with the given ID.
   */
  public Device getDevice(String deviceID)
  {
    int index = getDeviceIndex(deviceID);
    if(index == -1)
      return null;
    else
      return getDeviceAt(index);
  }

  /**
   * Gets the instances of <CODE>Device</CODE> associated with the <CODE>IOC</CODE>.
   * 
   * @return The instances of <CODE>Device</CODE> associated with the <CODE>EpicsSystem</CODE>.
   */
  public Device[] getDevices()
  {
    return (Device[])devices.toArray(new Device[devices.size()]);
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
    return EPICS_SYSTEM_TABLE_NAME;
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
  }
}