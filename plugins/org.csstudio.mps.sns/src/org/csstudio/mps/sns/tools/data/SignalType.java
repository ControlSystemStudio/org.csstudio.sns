package org.csstudio.mps.sns.tools.data;

import java.sql.ResultSet;
import java.util.Arrays;
import org.csstudio.mps.sns.view.MPSBrowserView;

/**
 * Provides a class to hold the data in the DVC_TYPE_SGNL table. This 
 * information defines a signal type.
 *
 * @author Chris Fowlkes
 */
public class SignalType implements Cloneable 
{ 
  /**
   * Holds the type of device the signal is valid for.
   * @attribute 
   */
  private DeviceType deviceType;
  /**
   * Holds the name of the <CODE>SignalType</CODE>.
   */
  private String name;
  /**
   * Holds the description of the <CODE>SignalType</CODE>.
   */
  private String description;
  /**
   * Holds the EPICS record type for the <CODE>SignalType</CODE>.
   * @attribute 
   */
  private EpicsRecordType recordType;
  /**
   * Holds the value of the multimode indicator.
   */
  private String multimode;
  /**
   * Holds the value of the machine protection indicator.
   */
  private String machineProtection;
  /**
   * Holds the value of the bulk indicator.
   */
  private String bulk;
  /**
   * Holds the name of the RDB column for the record type property.
   */
  public final static String RECORD_TYPE_COLUMN_NAME = "REC_TYPE_ID";
  
  /**
   * Constructs a new <CODE>SignalType</CODE>.
   */
  public SignalType()
  {
  }

  /**
   * Creates and initializes a <CODE>SignalType</CODE>.
   *
   * @param deviceType Describes the type of device the signal is suitable for.
   * @param name The name to give the <CODE>SignalType</CODE>.
   * @param description A description of the <CODE>SignalType</CODE>.
   */
  public SignalType(DeviceType deviceType, String name, String description, String multimode, String machineProtection, String bulk, EpicsRecordType recordType)
  {
    this();
    setDeviceType(deviceType);
    setName(name);
    setDescription(description);
    setMultimode(multimode);
    setMachineProtection(machineProtection);
    setBulk(bulk);
    setRecordType(recordType);
  }

  /**
   * Creates a <CODE>SignalType</CODE> from the given data.
   * 
   * @param data The data with which to initialize the <CODE>SignalType</CODE>
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public SignalType(ResultSet data) throws java.sql.SQLException
  {
    this();
    String[] columnNames = RDBData.findColumnNames(data);
    if(Arrays.binarySearch(columnNames, RECORD_TYPE_COLUMN_NAME) >= 0)
      setRecordType(new EpicsRecordType(data));
  }

  /**
   * Sets the type of device the signal is suitable for.
   *
   * @param deviceType Describes the type of device the signal is suitable for.
   */
  public void setDeviceType(DeviceType deviceType)
  {
    this.deviceType = deviceType;
  }

  /**
   * Gets the type of device the signal is suitable for.
   *
   * @return Description of the type of device the signal is suitable for.
   */
  public DeviceType getDeviceType()
  {
    return deviceType;
  }

  /**
   * Sets the name of the <CODE>SignalType</CODE>.
   *
   * @param name The name to give the <CODE>SignalType</CODE>.
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Gets the name of the <CODE>SignalType</CODE>.
   *
   * @return The descriptive name for the <CODE>SignalType</CODE>.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Sets the description of the <CODE>SignalType</CODE>.
   *
   * @param description The description to give the <CODE>SignalType</CODE>.
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * Gets the description of the <CODE>SignalType</CODE>.
   *
   * @return The description for the <CODE>SignalType</CODE>.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Returns the descriptive name of the <CODE>SignalType</CODE>.
   * 
   * @return The name of the <CODE>SignalType</CODE>.
   */
  public String toString()
  {
    String name = getName();
    if(name == null)
      name = "";
    return name;
  }

  /**
   * Sets the EPICS record type of the <CODE>SignalType</CODE>.
   *
   * @param recordType The EPICS record type for the <CODE>SignalType</CODE>.
   */
  public void setRecordType(EpicsRecordType recordType)
  {
    this.recordType = recordType;
  }

  /**
   * Gets the EPICS record type of the <CODE>SignalType</CODE>.
   *
   * @return The EPICS record type for the <CODE>SignalType</CODE>.
   */
  public EpicsRecordType getRecordType()
  {
    return recordType;
  }

  /**
   * Sets the multimode indicator of the <CODE>SignalType</CODE>.
   *
   * @param multimode The value of the multimode indicator of the <CODE>SignalType</CODE>.
   */
  public void setMultimode(String multimode)
  {
    this.multimode = multimode;
  }

  /**
   * Sets the multimode indicator of the <CODE>SignalType</CODE>.
   *
   * @return The value of the multimode indicator of the <CODE>SignalType</CODE>.
   */
  public String getMultimode()
  {
    return multimode;
  }

  /**
   * Sets the machine protection indicator of the <CODE>SignalType</CODE>.
   *
   * @param machineProtection The value of the machine protection indicator of the <CODE>SignalType</CODE>.
   */
  public void setMachineProtection(String machineProtection)
  {
    this.machineProtection = machineProtection;
  }

  /**
   * Sets the machine protection indicator of the <CODE>SignalType</CODE>.
   *
   * @return The value of the machine protection indicator of the <CODE>SignalType</CODE>.
   */
  public String getMachineProtection()
  {
    return machineProtection;
  }

  /**
   * Sets the bulk indicator of the <CODE>SignalType</CODE>.
   *
   * @param bulk The value of the bulk indicator of the <CODE>SignalType</CODE>.
   */
  public void setBulk(String bulk)
  {
    this.bulk = bulk;
  }

  /**
   * Sets the bulk indicator of the <CODE>SignalType</CODE>.
   *
   * @return The value of the bulk indicator of the <CODE>SignalType</CODE>.
   */
  public String getBulk()
  {
    return bulk;
  }

  /**
   * Creates and returns a copy of the <CODE>SignalType</CODE>.
   * 
   * @return A copy of the <CODE>SignalType</CODE>
   */
  protected Object clone()
  {
    SignalType clone = new SignalType();
    DeviceType deviceType = getDeviceType();
    if(deviceType != null)
      clone.setDeviceType((DeviceType)deviceType.clone());
    clone.setName(getName());
    clone.setDescription(getDescription());
    clone.setMultimode(getMultimode());
    clone.setMachineProtection(getMachineProtection());
    clone.setBulk(getBulk());
    clone.setRecordType(getRecordType());
    return clone;
  }

  /**
   * Tests the given <CODE>SignalType</CODE> to see if it is equal.
   * 
   * @param obj The <CODE>SignalType</CODE> to compare.
   */
  public boolean equals(Object obj)
  {
    //Check for null
    if(obj == null)
      return false;
    //Check class type
    if(! (obj instanceof SignalType))
      return false;
    //Check device type
    SignalType compareTo = (SignalType)obj;
    if(! MPSBrowserView.compare(getDeviceType(), compareTo.getDeviceType()))
      return false;
    //Check name
    if(! MPSBrowserView.compare(getName(), compareTo.getName()))
      return false;
    //Check description
    if(! MPSBrowserView.compare(getDescription(), compareTo.getDescription()))
      return false;
    //Check multimode
    if(! MPSBrowserView.compare(getMultimode(), compareTo.getMultimode()))
      return false;
    //Check machine protection
    if(! MPSBrowserView.compare(getMachineProtection(), compareTo.getMachineProtection()))
      return false;
    //Check bulk
    if(! MPSBrowserView.compare(getBulk(), compareTo.getBulk()))
      return false;
    //Check record type
    if(! MPSBrowserView.compare(getRecordType(), compareTo.getRecordType()))
      return false;
    return true;
  }

  /**
   * Returns a hash code for the <CODE>SignalType</CODE>. If the 
   * <CODE>equals</CODE> method for a class returns <CODE>true</CODE>, the 
   * <CODE>hashCode</CODE> methods for those instances of <CODE>Object</CODE> 
   * must also return the same value. The reverse is not the case, meaning that
   * just because the value returned by the <CODE>hashCode()</CODE> methods of 
   * two instances of a class is the same equality can not be assumed.
   * 
   * @return A hash code for the <CODE>SignalType</CODE>.
   */
  public int hashCode()
  {
    int hashCode = 1;
    hashCode = hashCode * 37 + findPropertyHashCode(getDeviceType());
    hashCode = hashCode * 37 + findPropertyHashCode(getDescription());
    hashCode = hashCode * 37 + findPropertyHashCode(getMultimode());
    hashCode = hashCode * 37 + findPropertyHashCode(getMachineProtection());
    hashCode = hashCode * 37 + findPropertyHashCode(getBulk());
    hashCode = hashCode * 37 + findPropertyHashCode(getRecordType());
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
    int hashValue = 0;
    if(propertyValue == null)
      return 0;
    else
      return propertyValue.hashCode();
  }
}