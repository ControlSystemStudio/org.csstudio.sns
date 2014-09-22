package org.csstudio.mps.sns.tools.data;
import org.csstudio.mps.sns.view.MPSBrowserView;

/**
 * This class holds the data stored in the SUBSYS table in the database.
 * 
 * @author Chris Fowlkes
 */
public class EpicsSubsystem 
{
  /**
   * Holds the ID of the <CODE>EpicsSubsystem</CODE>.
   */
  private String id;
  /**
   * Holds the name of the <CODE>EpicsSubsystem</CODE>.
   */
  private String name;
  
  /**
   * Creates a new <CODE>EpicsSubsystem</CODE>.
   */
  public EpicsSubsystem()
  {
  }

  /**
   * Creates and initializes a new <CODE>EpicsSubsystem</CODE>.
   * 
   * @param id The ID of the <CODE>EpicsSubsystem</CODE>.
   */
  public EpicsSubsystem(String id)
  {
    setID(id);
  }

  /**
   * Creates and initializes a new <CODE>EpicsSubsystem</CODE>.
   * 
   * @param id The ID of the <CODE>EpicsSubsystem</CODE>.
   * @param name The name of the <CODE>EpicsSubsystem</CODE>.
   */
  public EpicsSubsystem(String id, String name)
  {
    this(id);
    setName(name);
  }
  
  /**
   * Sets the ID of the <CODE>EpicsSubsystem</CODE>.
   * 
   * @param id The value of the SUBSYS_ID field in the database.
   */
  public void setID(String id)
  {
    this.id = id;
  }

  /**
   * Gets the ID of the <CODE>EpicsSubsystem</CODE>.
   * 
   * @return The value of the SUBSYS_ID field in the database.
   */
  public String getID()
  {
    return id;
  }

  /**
   * Sets the name of the <CODE>EpicsSubsystem</CODE>.
   * 
   * @param name The value of the SUBSYS_NM field in the database.
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Gets the name of the <CODE>EpicsSubsystem</CODE>.
   * 
   * @return The value of the SUBSYS_NM field in the database.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Compares two instances of <CODE>EpicsSubsystem</CODE> for equality.
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
    if(! (obj instanceof EpicsSubsystem))
      return false;
    //Check ID
    EpicsSubsystem compareTo = (EpicsSubsystem)obj;
    if(! MPSBrowserView.compare(getID(), compareTo.getID()))
      return false;
    //Check name
    if(! MPSBrowserView.compare(getName(), compareTo.getName()))
      return false;
    return true;
  }

  /**
   * Returns a hash code for the <CODE>EpicsSubsystem</CODE>. If the 
   * <CODE>equals</CODE> method for a class returns <CODE>true</CODE>, the 
   * <CODE>hashCode</CODE> methods for those instances of <CODE>Object</CODE> 
   * must also return the same value. The reverse is not the case, meaning that
   * just because the value returned by the <CODE>hashCode()</CODE> methods of 
   * two instances of a class is the same equality can not be assumed.
   * 
   * @return A hash code for the <CODE>EpicsSubsystem</CODE>.
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
    int hashValue = 0;
    if(propertyValue == null)
      return 0;
    else
      return propertyValue.hashCode();
  }

  /**
   * Creates and returns a copy of the <CODE>EpicsSubsystem</CODE>.
   * 
   * @return A copy of the <CODE>EpicsSubsystem</CODE>.
   */
  public Object clone()
  {
    return new EpicsSubsystem(getID(), getName());
  }

  /**
   * Returns the <CODE>String</CODE> representation of the 
   * <CODE>EpicsSubsystem</CODE>.
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
}