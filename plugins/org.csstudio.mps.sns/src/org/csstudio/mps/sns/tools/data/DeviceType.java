package org.csstudio.mps.sns.tools.data;

import org.csstudio.mps.sns.view.MPSBrowserView;

/**
 * Provides a class to hold a device type. This class represents the data in the 
 * rocords of the DVC_TYPE table.
 *
 * @author Chris Fowlkes
 */
public class DeviceType implements Cloneable 
{
  /**
   * Holds the value of the id database field.
   */
  private String id;
  /**
   * Holds the value of the description database field.
   */
  private String description;
  /**
   * Holds the value returned by the DEVICE_IS_NETWORKED stored procedure for 
   * the device type.
   */
  private String networked;

  /**
   * Creates a new <CODE>DeviceType</CODE>.
   */
  public DeviceType()
  {
  }
  
  /**
   * Creates a new <CODE>DeviceType</CODE> with the given value for ID.
   *
   * @param id The ID for the <CODE>DeviceType</CODE>.
   */
  public DeviceType(String id)
  {
    this();
    setID(id);
  }

  /**
   * Creates a new <CODE>DeviceType</CODE> with the given values for ID and 
   * description.
   *
   * @param id The ID for the <CODE>DeviceType</CODE>.
   * @param description The description of the <CODE>DeviceType</CODE>.
   */
  public DeviceType(String id, String description)
  {
    this(id);
    setDescription(description);
  }

  /**
   * Sets the ID for the <CODE>DeviceType</CODE>.
   * 
   * @param id The ID for the <CODE>DeviceType</CODE>.
   */
  public void setID(String id)
  {
    this.id = id;
  }

  /**
   * Gets the ID for the <CODE>DeviceType</CODE>.
   * 
   * @return The ID for the <CODE>DeviceType</CODE>.
   */
  public String getID()
  {
    return id;
  }

  /**
   * Sets the description for the <CODE>DeviceType</CODE>.
   * 
   * @param description The description for the <CODE>DeviceType</CODE>.
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * Gets the description for the <CODE>DeviceType</CODE>.
   * 
   * @return The description for the <CODE>DeviceType</CODE>.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Gets the <CODE>String</CODE> representation of the <CODE>DeviceType</CODE>.
   * This method returns the value of the description property.
   *
   * @return The value of the description property.
   */
  public String toString()
  {
    String name = getID();
    if(name == null)
      name = "";
    return name;
  }

  /**
   * Creates a copy of the <CODE>DeviceType</CODE>.
   * 
   * @return A copy of the <CODE>DeviceType</CODE>.
   */
  public Object clone()
  {
    return new DeviceType(getID(), getDescription());
  }

  /**
   * Tests the given <CODE>DeviceType</CODE> for equality.
   * 
   * @param obj The <CODE>Object</CODE> with which to compare.
   * @return <CODE>true</CODE> if the instances of <CODE>DeviceType</CODE> are equal, <CODE>false</CODE> if not.
   */
  public boolean equals(Object obj)
  {
    //Test for null
    if(obj == null)
      return false;
    //Test class type
    if(! (obj instanceof DeviceType))
      return false;
    //Test ID
    DeviceType compareTo = (DeviceType)obj;
    if(! MPSBrowserView.compare(getID(), compareTo.getID()))
      return false;
    //Test description
    if(! MPSBrowserView.compare(getDescription(), compareTo.getDescription()))
      return false;
    return true;
  }

  /**
   * Returns a hash code for the <CODE>DeviceType</CODE>. If the 
   * <CODE>equals</CODE> method for a class returns <CODE>true</CODE>, the 
   * <CODE>hashCode</CODE> methods for those instances of <CODE>Object</CODE> 
   * must also return the same value. The reverse is not the case, meaning that
   * just because the value returned by the <CODE>hashCode()</CODE> methods of 
   * two instances of a class is the same equality can not be assumed.
   * 
   * @return A hash code for the <CODE>DeviceType</CODE>.
   */
  public int hashCode()
  {
    int hashCode = 1;
    hashCode = hashCode * 37 + findPropertyHashCode(getID());
    hashCode = hashCode * 37 + findPropertyHashCode(getDescription());
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
   * Returns the value returned by the DEVICE_IS_NETWORKED stored procedure for
   * the <CODE>DeviceType</CODE>. <CODE>null</CODE> is returned if the value has 
   * not been set.
   * 
   * @return The value returned by the DEVICE_IS_NETWORKED stored procedure for this <CODE>DeviceType</CODE>.
   */
  public String getNetworked()
  {
    return networked;
  }

  /**
   * Sets the networked value for the <CODE>DeviceType</CODE>. This should 
   * reflect the value returned by the DEVICE_IS_NETWORKED stored procedure for
   * the <CODE>DeviceType</CODE>.
   * 
   * @param networked The new value for the property.
   */
  public void setNetworked(String networked)
  {
    this.networked = networked;
  }
}