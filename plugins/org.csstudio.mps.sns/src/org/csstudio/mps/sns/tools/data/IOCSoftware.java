package org.csstudio.mps.sns.tools.data;
import java.sql.Date;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;

/**
 * Provides a class to hold a row of data from the IOC_SFTW table.
 * 
 * @author Chris Fowlkes
 */
public class IOCSoftware 
{
  /**
   * Holds the <CODE>IOC</CODE> on which the <CODE>IOCSoftware</CODE> was 
   * installed.
   * @attribute 
   */
  private IOC ioc;
  private transient PropertyChangeSupport propertyChangeSupport;
  /**
   * Holds the <CODE>Date</CODE> that the <CODE>IOC</CODE> was booted with the 
   * software.
   */
  private Timestamp bootDate;
  /**
   * Holds the name of the software.
   */
  private String name;
  /**
   * Holds the version of the software.
   */
  private String version;
  /**
   * Holds the location of the software.
   */
  private String location;
  
  /**
   * Creates a new <CODE>IOCSoftware</CODE>.
   */
  public IOCSoftware()
  {
    propertyChangeSupport = new PropertyChangeSupport(this);
  }

  /**
   * Creates and initializes the <CODE>IOCSoftware</CODE>.
   * 
   * @param ioc The <CODE>IOC</CODE> on which the <CODE>IOCSoftware</CODE> was running.
   * @param bootDate The boot date of the <CODE>IOC</CODE>.
   */
  public IOCSoftware(IOC ioc, Timestamp bootDate)
  {
    this();
    setIOC(ioc, bootDate);
  }

  /**
   * Creates and initializes the <CODE>IOCSoftware</CODE>.
   * 
   * @param name The name of the software.
   */
  public IOCSoftware(String name)
  {
    this();
    setName(name);
  }
  /**
   * Gets the <CODE>IOC</CODE> on which the software was running.
   * 
   * @return The <CODE>IOC</CODE> on which the software was running.
   */
  public IOC getIOC()
  {
    return ioc;
  }

  /**
   * Adds the listener to the <CODE>IOCSoftware</CODE>. The bound properties are
   * ioc, name, version, location, and bootDate.
   * 
   * @param l The <CODE>PropertyChangeListener</CODE> to add to the <CODE>IOCSoftware</CODE>.
   */
  public void addPropertyChangeListener(PropertyChangeListener l)
  {
    propertyChangeSupport.addPropertyChangeListener(l);
  }

  /**
   * Removes the listener from the <CODE>IOCSoftware</CODE>.
   * 
   * @param l The <CODE>PropertyChangeListener</CODE> to remove from the <CODE>IOCSoftware</CODE>.
   */
  public void removePropertyChangeListener(PropertyChangeListener l)
  {
    propertyChangeSupport.removePropertyChangeListener(l);
  }

  /**
   * Sets the <CODE>IOC</CODE> on which the <CODE>IOCSoftware</CODE> was running.
   * 
   * @param ioc The <CODE>IOC</CODE> on which the <CODE>IOCSoftware</CODE> was running.
   */
  public void setIOC(IOC ioc, Timestamp bootDate)
  {
    IOC oldIOC = ioc;
    this.ioc = ioc;
    if(ioc.findSoftware(bootDate, getName()) == null)
      ioc.addSoftware(bootDate, this);
    propertyChangeSupport.firePropertyChange("ioc", oldIOC, ioc);
    setBootDate(bootDate);
  }

  /**
   * Gets the value of the name property.
   * 
   * @return The value of the name property.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Sets the name of the software. This is a bound property.
   * 
   * @param name The name of the software.
   */
  public void setName(String name)
  {
    String oldName = name;
    this.name = name;
    propertyChangeSupport.firePropertyChange("name", oldName, name);
  }

  /**
   * Gets the version of the software. This is a bound property.
   * 
   * @return The version of the software.
   */
  public String getVersion()
  {
    return version;
  }

  /**
   * Sets the version of the software.
   * 
   * @param version The version of the software.
   */
  public void setVersion(String version)
  {
    String oldVersion = version;
    this.version = version;
    propertyChangeSupport.firePropertyChange("version", oldVersion, version);
  }

  /**
   * Gets the location of the software.
   * 
   * @return The location of the software.
   */
  public String getLocation()
  {
    return location;
  }

  /**
   * Sets the location of the software.
   * 
   * @param location The location of the software.
   */
  public void setLocation(String location)
  {
    String oldLocation = location;
    this.location = location;
    propertyChangeSupport.firePropertyChange("location", oldLocation, location);
  }

  /**
   * Sets the location of the software.
   * 
   * @param location The location of the software.
   */
  public void setBootDate(Timestamp bootDate)
  {
    Timestamp oldBootDate = bootDate;
    this.bootDate = bootDate;
    propertyChangeSupport.firePropertyChange("bootDate", oldBootDate, bootDate);
  }

  /**
   * Gets the boot date for the <CODE>IOC</CODE> with which the <CODE>IOCSoftware</CODE> is associated.
   */
  public Timestamp getBootDate()
  {
    return bootDate;
  }
}