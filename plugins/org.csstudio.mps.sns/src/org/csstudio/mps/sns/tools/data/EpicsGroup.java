package org.csstudio.mps.sns.tools.data;
import java.util.*;

import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.tools.data.Device;

/**
 * Contains the data associated with an <CODE>EpicsGroup</CODE>. This class 
 * represents the data in the EPICS_GRP table.
 * 
 * @author Chris Fowlkes
 */
public class EpicsGroup implements Cloneable 
{
  /**
   * Holds the ID of the <CODE>EpicsGroup</CODE>.
   */
  private String id;
  /**
   * Holds the description of the <CODE>EpicsGroup</CODE>.
   */
  private String description;
  /**
   * Holds the instances of <CODE>Device</CODE> that belong to the 
   * <CODE>EpicsGroup</CODE>.
   */
  private Vector devices = new Vector();

  /**
   * Creates a new <CODE>EpicsGroup</CODE>.
   */
  public EpicsGroup()
  {
  }

  /**
   * Creates a new <CODE>EpicsGroup</CODE> with the given ID.
   * 
   * @param id The id for the new group.
   */
  public EpicsGroup(String id)
  {
    this();
    setID(id);
  }

  /**
   * Creates a new <CODE>EpicsGroup</CODE> with the given ID and description.
   * 
   * @param id The ID of the new group.
   * @param description The description of the new group.
   */
  public EpicsGroup(String id, String description)
  {
    this(id);
    setDescription(description);
  }

  /**
   * Sets the value of the ID for the <CODE>EpicsGroup</CODE>.
   * 
   * @param id The ID of the <CODE>EpicsGroup</CODE>.
   */
  public void setID(String id)
  {
    this.id = id;
  }

  /**
   * Gets the value of the ID for the <CODE>EpicsGroup</CODE>.
   * 
   * @return The ID of the <CODE>EpicsGroup</CODE>.
   */
  public String getID()
  {
    return id;
  }

  /**
   * Sets the value of the description for the <CODE>EpicsGroup</CODE>.
   * 
   * @param description The description of the <CODE>EpicsGroup</CODE>.
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * Gets the description of the <CODE>EpicsGroup</CODE>.
   * 
   * @return The description of the <CODE>EpicsGroup</CODE>.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Adds the <CODE>Device</CODE> to the <CODE>EpicsGroup</CODE>.
   * 
   * @param newDevice The <CODE>Device</CODE> to add to the <CODE>EpicsGroup</CODE>.
   */
  public void addDevice(Device newDevice)
  {
    this.devices.addElement(newDevice);
  }

  /**
   * Gets a <CODE>Vector</CODE> conataining all of the instances of 
   * <CODE>Device</CODE> in the <CODE>EpicsGroup</CODE>. This is all of the 
   * instances of <CODE>Device</CODE> that have been added to the 
   * <CODE>EpicsGroup</CODE> via the <CODE>addDevice</CODE> method.
   * 
   * @return The instances of <CODE>Device</CODE> that have been added to the <CODE>EpicsGroup</CODE>.
   */
  public Vector getDevices()
  {
    return devices;
  }

  /**
   * Compares two instances of <CODE>EpicsGroup</CODE> to see if they are equal.
   * 
   * @param obj The <CODE>EpicsGroup</CODE> to compare to.
   * @return <CODE>true</CODE> if the groups are equal, <CODE>false</CODE> if not.
   */
  public boolean equals(Object obj)
  {
    //Check for null
    if(obj == null)
      return false;
    //Check class type
    if(! (obj instanceof EpicsGroup))
      return false;
    //Check ID
    EpicsGroup compareTo = (EpicsGroup)obj;
    if(! MPSBrowserView.compare(getID(), compareTo.getID()))
      return false;
    //Compare description
    if(! MPSBrowserView.compare(getDescription(), compareTo.getDescription()))
      return false;
    //Compare devices
    if(! MPSBrowserView.compare(getDevices(), compareTo.getDevices()))
      return false;
    return true;
  }

  /**
   * Returns a hash code for the <CODE>EpicsGroup</CODE>. If the 
   * <CODE>equals</CODE> method for a class returns <CODE>true</CODE>, the 
   * <CODE>hashCode</CODE> methods for those instances of <CODE>Object</CODE> 
   * must also return the same value. The reverse is not the case, meaning that
   * just because the value returned by the <CODE>hashCode()</CODE> methods of 
   * two instances of a class is the same equality can not be assumed.
   * 
   * @return A hash code for the <CODE>EpicsGroup</CODE>.
   */
  public int hashCode()
  {
    int hashCode = 1;
    hashCode = hashCode * 37 + findPropertyHashCode(getID());
    hashCode = hashCode * 37 + findPropertyHashCode(getDescription());
    hashCode = hashCode * 37 + findPropertyHashCode(getDevices());
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

  /**
   * Creates and returns a copy of the <CODE>EpicsGroup</CODE>.
   * 
   * @return A clone of the <CODE>EpicsGroup</CODE>.
   */
  public Object clone()
  {
    EpicsGroup clone = new EpicsGroup(getID(), getDescription());
    Vector devices = getDevices();
    int deviceCount = devices.size();
    for(int i=0;i<deviceCount;i++)
    {
      Device currentDevice = (Device)devices.elementAt(i);
      clone.addDevice((Device)currentDevice.clone());
    }//for(int i=0;i<deviceCount;i++)
    return clone;
  }

  /**
   * Removes the <CODE>Device</CODE> from the group.
   * 
   * @param deviceToRemove The <CODE>Device</CODE> to remove from the group.
   */
  public void removeDevice(Device deviceToRemove)
  {
    devices.removeElement(deviceToRemove);
  }

  /**
   * Returns a <CODE>String</CODE> representation of the 
   * <CODE>EpicsGroup</CODE>. The <CODE>String</CODE> returned is the group ID.
   * 
   * @return The ID of the group.
   */
  public String toString()
  {
    String name = getID();
    if(name == null)
      name = "";
    return getID();
  }
}