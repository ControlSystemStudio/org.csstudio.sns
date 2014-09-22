package org.csstudio.mps.sns.tools.data;
import java.sql.ResultSet;

import java.util.*;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.tools.data.EpicsRecordType;

/**
 * Provides a class to hold information about signal fields. This class will 
 * hold the data contained in the sgnl_fld_def and sgnl_fld_menu tables.
 * 
 * @author Chris Fowlkes
 */
public class SignalFieldType implements Cloneable 
{
  /**
   * Holds the value of the fld_id field in the table.
   */
  private String id;
  /**
   * Holds the menu items associated with the editor.
   * @attribute 
   */
  private SignalFieldMenu menu;
  /**
   * Holds the EPICS record type for the <CODE>SignalType</CODE>.
   * @attribute 
   */
  private EpicsRecordType recordType;
  /**
   * Holds the EPICS record description for the <CODE>SignalType</CODE>.
   */
  private String description;
  /**
   * Holds the value of the fld_prmt_grp field in the database.
   */
  private String promptGroup;
  private int promptOrder;
  private String epicsFieldTypeID;
  private String initial;
  private boolean inDatabase = true;
  /**
   * Holds the RDB column name for the ID property.
   */
  public final static String ID_COLUMN_NAME = "FLD_ID";
  
  /**
   * Creates a new <CODE>SignalFieldType</CODE>.
   */
  public SignalFieldType()
  {
  }

  /**
   * Creates and initializes a new <CODE>SignalFieldType</CODE>.
   * 
   * @param id The ID for the <CODE>SignalFieldType</CODE>.
   */
  public SignalFieldType(String id)
  {
    setID(id);
  }

  /**
   * Creates and initializes a new <CODE>SignalFieldType</CODE>.
   * 
   * @param id The ID for the <CODE>SignalFieldType</CODE>.
   * @param recordType The Epics record type for the <CODE>SignalFieldType</CODE>.
   */
  public SignalFieldType(String id, EpicsRecordType recordType)
  {
    setID(id);
    setRecordType(recordType);
  }

  /**
   * Creates and initializes a new <CODE>SignalFieldType</CODE>.
   * 
   * @param id The ID for the <CODE>SignalFieldType</CODE>.
   * @param recordType The EPICS record type for the <CODE>SignalFieldType</CODE>.
   * @param description The description for the field.
   */
  public SignalFieldType(String id, EpicsRecordType recordType, String description)
  {
    setID(id);
    setRecordType(recordType);
    setDescription(description);
  }
  
  /**
   * Creates a <CODE>SignalFieldType</CODE> from the given RDB data.
   * 
   * @param data The data with which to create the <CODE>SignalFieldType</CODE>.
   * @throws java.sql.SQLException Throw on SQL error.
   */
  public SignalFieldType(ResultSet data) throws java.sql.SQLException
  {
    this();
    String[] columnNames = RDBData.findColumnNames(data);
    if(Arrays.binarySearch(columnNames, ID_COLUMN_NAME) >= 0)
      setID(data.getString(ID_COLUMN_NAME));
  }

  /**
   * Sets the ID of the <CODE>SignalFieldType</CODE>. The ID is used to identify 
   * the field in the database.
   * 
   * @param id The new value for the id property.
   */
  public void setID(String id)
  {
    this.id = id;
  }

  /**
   * Gets the ID of the <CODE>SignalFieldType</CODE>. The ID is used to identify 
   * the field in the database.
   * 
   * @return The ID of the field.
   */
  public String getID()
  {
    return id;
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
   * Sets the description for the record. This property reflects the value of 
   * the fld_desc field in the sgnl_fld_def table.
   * 
   * @param description The description of the signal field.
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * Gets the description for the record. This property reflects the value of 
   * the fld_desc field in the sgnl_fld_def table.
   * 
   * @return The description of the signal field.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Sets the value of the prompt group property. This is used to categorize the 
   * field types.
   * 
   * @param promptGroup The value of the fld_prmt_grp field from the sgnl_fld_def table.
   */
  public void setPromptGroup(String promptGroup)
  {
    this.promptGroup = promptGroup;
  }

  /**
   * Sets the value of the prompt group property. This is used to categorize the 
   * field types.
   * 
   * @return The value of the fld_prmt_grp field from the sgnl_fld_def table.
   */
  public String getPromptGroup()
  {
    return promptGroup;
  }

  /**
   * Compares two instances of <CODE>SignalFieldType</CODE>. They are considered
   * equal of the ID and record type match. This method does not compare the 
   * menu items of the instances of <CODE>SignalFieldType</CODE>.
   * 
   * @param obj The <CODE>SignalFieldType</CODE> to compare to.
   */
  public boolean equals(Object obj)
  {
    //Check for null
    if(obj == null)
      return false;
    //Check class type
    if(! (obj instanceof SignalFieldType))
      return false;
    //Check the ID
    SignalFieldType compareTo = (SignalFieldType)obj;
    if(! getID().equals(compareTo.getID()))
      return false;
    //Check the record type.
    if(! MPSBrowserView.compare(getRecordType(), compareTo.getRecordType()))
      return false;
    //Check the prompt group.
    if(! MPSBrowserView.compare(getPromptGroup(), compareTo.getPromptGroup()))
      return false;
    return true;
  }

  /**
   * Returns a hash code for the <CODE>SignalFieldType</CODE>. If the 
   * <CODE>equals</CODE> method for a class returns <CODE>true</CODE>, the 
   * <CODE>hashCode</CODE> methods for those instances of <CODE>Object</CODE> 
   * must also return the same value. The reverse is not the case, meaning that
   * just because the value returned by the <CODE>hashCode()</CODE> methods of 
   * two instances of a class is the same equality can not be assumed.
   * 
   * @return A hash code for the <CODE>SignalFieldType</CODE>.
   */
  public int hashCode()
  {
    int hashCode = 1;
    hashCode = hashCode * 37 + findPropertyHashCode(getID());
    hashCode = hashCode * 37 + findPropertyHashCode(getRecordType());
    hashCode = hashCode * 37 + findPropertyHashCode(getPromptGroup());
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
   * Creates and returns a copy of the <CODE>SignalFieldType</CODE>.
   * 
   * @return A copy of the <CODE>SignlaFieldType</CODE>.
   */
  public Object clone()
  {
    SignalFieldType clone = new SignalFieldType(getID(), getRecordType(), getDescription());
    clone.setPromptGroup(getPromptGroup());
    SignalFieldMenu menu = getMenu();
    if(menu == null)
      clone.setMenu(null);
    else
      clone.setMenu((SignalFieldMenu)menu.clone());
    return clone;
  }

  /**
   * Returns the <CODE>String</CODE> representation of the 
   * <CODE>SignalFieldType</CODE>. This value is the value of the ID property.
   * 
   * @return The <CODE>String</CODE> representation of the <CODE>SignalFieldType</CODE>, which is the value of the ID property.
   */
  public String toString()
  {
    String value = getID();
    if(value == null)
      value = "";
    return value;
  }

  public void setMenu(SignalFieldMenu menu)
  {
    this.menu = menu;
  }

  public SignalFieldMenu getMenu()
  {
    return menu;
  }

  public void setPromptOrder(int promptOrder)
  {
    this.promptOrder = promptOrder;
  }

  public int getPromptOrder()
  {
    return promptOrder;
  }

  public void setEpicsFieldTypeID(String epicsFieldTypeID)
  {
    this.epicsFieldTypeID = epicsFieldTypeID;
  }

  public String getEpicsFieldTypeID()
  {
    return epicsFieldTypeID;
  }

  public void setInitial(String initial)
  {
    this.initial = initial;
  }

  public String getInitial()
  {
    return initial;
  }

  public void setInDatabase(boolean inDatabase)
  {
    this.inDatabase = inDatabase;
  }

  public boolean isInDatabase()
  {
    return inDatabase;
  }
}