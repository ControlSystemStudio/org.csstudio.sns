package org.csstudio.mps.sns.tools.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Holds a record from the CABLE.TBL_CABLE RDB table.
 * 
 * @author Chris Fowlkes
 */
public class Cable
{
  /**
   * Handles property change events for the <CODE>Cable</CODE>.
   */
  private transient PropertyChangeSupport propertyChangeSupport;
  /**
   * Holds the <CODE>Device</CODE> from which the <CODE>Cable</CODE> comes.
   */
  private Device deviceFrom;
  /**
   * Holds the label for the <CODE>Device</CODE> from which the 
   * <CODE>Cable</CODE> comes.
   */
  private String fromLabel;
  /**
   * Holds the <CODE>Device</CODE> to which the <CODE>Cable</CODE> goes.
   */
  private Device deviceTo;
  /**
   * Holds the label for the <CODE>Device</CODE> to which the <CODE>Cable</CODE> 
   * goes.
   */
  private String toLabel;
  /**
   * Holds the number of the <CODE>Cable</CODE>.
   */
  private String number;
  
  /**
   * Creates a new <CODE>Cable</CODE>.
   */
  public Cable()
  {
    propertyChangeSupport = new PropertyChangeSupport(this);
  }

  /**
   * Sets the 'from' <CODE>Device</CODE> for the <CODE>Cable</CODE>.
   * 
   * @param deviceFrom The <CODE>Device</CODE> from which the <CODE>Cable</CODE> comes.
   */
  public void setDeviceFrom(Device deviceFrom)
  {
    Device oldValue = getDeviceFrom();
    this.deviceFrom = deviceFrom;
    firePropertyChange("deviceFrom", oldValue, deviceFrom);
    if(deviceFrom.getCable(getNumber()) == null)
      deviceFrom.addCableFrom(this);
  }

  /**
   * Gets the 'from' <CODE>Device</CODE> for the <CODE>Cable</CODE>.
   * 
   * @return The <CODE>Device</CODE> from which the cable comes.
   */
  public Device getDeviceFrom()
  {
    return deviceFrom;
  }

  /**
   * Sets the label for the 'from' <CODE>Device</CODE> for the <CODE>Cable</CODE>.
   * 
   * @param fromLabel The label for the <CODE>Device</CODE> from which the cable comes.
   */
  public void setFromLabel(String fromLabel)
  {
    String oldValue = getFromLabel();
    this.fromLabel = fromLabel;
    firePropertyChange("fromLabel", oldValue, fromLabel);
  }

  /**
   * Gets the label for the 'from' <CODE>Device</CODE> for the <CODE>Cable</CODE>.
   * 
   * @return The label for the <CODE>Device</CODE> from which the cable comes.
   */
  public String getFromLabel()
  {
    return fromLabel;
  }

  /**
   * Sets the 'to' <CODE>Device</CODE> for the <CODE>Cable</CODE>.
   * 
   * @param deviceTo The <CODE>Device</CODE> to which the <CODE>Cable</CODE> goes.
   */
  public void setDeviceTo(Device deviceTo)
  {
    Device oldValue = getDeviceTo();
    this.deviceTo = deviceTo;
    if(deviceTo.getCable(getNumber()) == null)
      deviceTo.addCableTo(this);
    firePropertyChange("deviceTo", oldValue, deviceTo);
  }

  /**
   * Gets the 'to' <CODE>Device</CODE> for the <CODE>Cable</CODE>.
   * 
   * @return The <CODE>Device</CODE> to which the <CODE>Cable</CODE> goes.
   */
  public Device getDeviceTo()
  {
    return deviceTo;
  }

  /**
   * Sets the label for the 'to' <CODE>Device</CODE> for the <CODE>Cable</CODE>.
   * 
   * @param toLabel The label for the <CODE>Device</CODE> to which the <CODE>Cable</CODE> goes.
   */
  public void setToLabel(String toLabel)
  {
    String oldValue = getToLabel();
    this.toLabel = toLabel;
    firePropertyChange("toLabel", oldValue, toLabel);
  }

  /**
   * Gets the label for the 'to' <CODE>Device</CODE> for the <CODE>Cable</CODE>.
   * 
   * @return The label for the <CODE>Device</CODE> to which the <CODE>Cable</CODE> goes.
   */
  public String getToLabel()
  {
    return toLabel;
  }

  /**
   * Sets the number of the <CODE>Cable</CODE>.
   * 
   * @param number The number of the <CODE>Cable</CODE>.
   */
  public void setNumber(String number)
  {
    String oldValue = getNumber();
    this.number = number;
    firePropertyChange("number", oldValue, number);
  }

  /**
   * Gets the number of the <CODE>Cable</CODE>.
   * 
   * @return The number of the <CODE>Cable</CODE>.
   */
  public String getNumber()
  {
    return number;
  }

  /**
   * Adds the given <CODE>PropertyChangeListener</CODE> to the module.
   * 
   * @param l The <CODE>PropertyChangeListener</CODE> to add to the module.
   */
  public void addPropertyChangeListener(PropertyChangeListener l)
  {
    propertyChangeSupport.addPropertyChangeListener(l);
  }

  /**
   * Removes the given <CODE>PropertyChangeListener</CODE> from the module.
   * 
   * @param l The <CODE>PropertyChangeListener</CODE> to remove from the module.
   */
  public void removePropertyChangeListener(PropertyChangeListener l)
  {
    propertyChangeSupport.removePropertyChangeListener(l);
  }

  /**
   * Creates a <CODE>PropertyChangeEvent</CODE> and fires it.
   * 
   * @param propertyName The name of the property changed.
   * @param oldValue The value of the property before the change.
   * @param newValue The new value of the property.
   */
  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
  {
    propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }
  
  /**
   * Fires the given property change event. This method should be called if the 
   * title property changes.
   * 
   * @param e The <CODE>PropertyChangeEvent</CODE> to fire.
   */
  protected void firePropertyChange(PropertyChangeEvent e)
  {
    propertyChangeSupport.firePropertyChange(e);
  }
  
  /**
   * Fires a <CODE>PropertyChangeEvent</CODE> for the given indexed property.
   * 
   * @param propertyName The name of the property changed.
   * @param index The index of the property changed.
   * @param oldValue The old value of the property.
   * @param newValue The new value of the property.
   */
  protected void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue)
  {
    propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
  }
  
  /**
   * Fires a <CODE>PropertyChangeEvent</CODE> for the given indexed property.
   * 
   * @param propertyName The name of the property changed.
   * @param index The index of the property changed.
   * @param oldValue The old value of the property.
   * @param newValue The new value of the property.
   */
  protected void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue)
  {
    propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
  }
  
  /**
   * Fires a <CODE>PropertyChangeEvent</CODE> for the given indexed property.
   * 
   * @param propertyName The name of the property changed.
   * @param index The index of the property changed.
   * @param oldValue The old value of the property.
   * @param newValue The new value of the property.
   */
  protected void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue)
  {
    propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
  }
}
