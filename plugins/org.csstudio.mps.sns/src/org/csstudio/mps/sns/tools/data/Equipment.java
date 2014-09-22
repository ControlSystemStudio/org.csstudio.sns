package org.csstudio.mps.sns.tools.data;

/**
 * Provides a class to hold data in the equipment table.
 * 
 * @author Chris Fowlkes
 */
public class Equipment 
{
  /**
   * The ID of the <CODE>Equipment</CODE>.
   */
  private String id;
  /**
   * The name of the <CODE>Equipment</CODE>.
   */
  private String name;
  private Device device;
  /**
   * Holds the serial number for the equipment.
   */
  private String serialNumber;

  /**
   * Creates a new <CODE>Equipment</CODE>.
   */
  public Equipment()
  {
  }

  /**
   * Creates a new <CODE>Equipment</CODE>.
   * 
   * @param id The ID of the <CODE>Equipment</CODE>.
   */
  public Equipment(String id)
  {
    setID(id);
  }

  /**
   * Creates a new <CODE>Equipment</CODE>.
   * 
   * @param id The ID of the <CODE>Equipment</CODE>.
   * @param name The name of the <CODE>Equipment</CODE>.
   */
  public Equipment(String id, String name)
  {
    setID(id);
    setName(name);
  }

  /**
   * Gets the ID of the <CODE>Equipment</CODE>.
   * 
   * @return The ID of the equipment.
   */
  public String getID()
  {
    return id;
  }

  /**
   * Sets the ID of the <CODE>Equipment</CODE>.
   * 
   * @param id The ID of the equipment.
   */
  public void setID(String id)
  {
    this.id = id;
  }

  /**
   * Gets the name of the <CODE>Equipment</CODE>.
   * 
   * @return The name of the equipment.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Sets the name of the <CODE>Equipment</CODE>.
   * 
   * @param name The name of the equipment.
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Returns the <CODE>String</CODE> representation of the 
   * <CODE>Equipment</CODE>. The value returned is the value of the ID property.
   * 
   * @return The value of the ID property.
   */
  public String toString()
  {
    return getID();
  }

  /**
   * Gets the <CODE>Device</CODE> with which this instance of 
   * <CODE>Equipment</CODE> is associated.
   * 
   * @return The <CODE>Device</CODE> with which this instance of <CODE>Equipment</CODE> is associated.
   */
  public Device getDevice()
  {
    return device;
  }

  /**
   * Sets the <CODE>Device</CODE> with which this instance of 
   * <CODE>Equipment</CODE> is associated.
   * 
   * @return device The <CODE>Device</CODE> with which this instance of <CODE>Equipment</CODE> is associated.
   */
  public void setDevice(Device device)
  {
    this.device = device;
    if(device != null && ! device.containsEquipment(getID()))
      device.addEquipment(this);
  }

  /**
   * Sets the serial number for the <CODE>Equipment</CODE>.
   * 
   * @param serialNumber The serial number for the <CODE>Equipment</CODE>.
   */
  public void setSerialNumber(String serialNumber)
  {
    this.serialNumber = serialNumber;
  }

  /**
   * Gets the serial number for the <CODE>Equipment</CODE>.
   * 
   * @return The serial number for the <CODE>Equipment</CODE>.
   */
  public String getSerialNumber()
  {
    return serialNumber;
  }
}
