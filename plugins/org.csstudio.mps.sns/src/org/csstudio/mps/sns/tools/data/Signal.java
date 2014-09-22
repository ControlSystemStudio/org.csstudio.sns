package org.csstudio.mps.sns.tools.data;
import java.math.*;
import java.sql.*;
import java.util.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.awt.datatransfer.Transferable;
import java.sql.Date;

import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.EpicsGroup;
import org.csstudio.mps.sns.tools.data.SignalField;
import org.csstudio.mps.sns.tools.data.SignalFieldType;
import org.csstudio.mps.sns.view.MPSBrowserView;

/**
 * Provides a class to hold data for a signal. This comprises the data held in 
 * the SGNL_REC table.
 *
 * @author Chris Fowlkes
 */
public class Signal extends RDBData implements Cloneable, Comparable, Transferable 
{
  /**
   * Holds the ID for the signal.
   */
  private String id;
  /**
   * Holds the <CODE>Device</CODE> used to create the <CODE>Signal</CODE>.
   * @attribute 
   */
  private Device device;
  /**
   * Holds the <CODE>SignalType</CODE> for the <CODE>Signal</CODE>.
   * @attribute 
   */
  private SignalType type;
  /**
   * Holds the values in the sgnl_fld table associated with the 
   * <CODE>Signal</CODE> as instances of <CODE>SignalFiled</CODE>.
   */
  private ArrayList signalFields = new ArrayList();
  /**
   * Provides a flag to easily determine if the <CODE>Signal</CODE> is already 
   * in the database. <CODE>true</CODE> by default.
   */
  private boolean inDatabase = true;
  /**
   * Holds the ID alias for the <CODE>Signal</CODE>.
   */
  private String idAlias;
  /**
   * Holds the <CODE>EpicsGroup</CODE> the <CODE>Signal</CODE> belongs to.
   * @attribute 
   */
  private EpicsGroup group;
  /**
   * Used to hold a file name for cases where the <CODE>Signal</CODE> was 
   * imported from a spreadsheet or graphical design tool.
   */
  private String externalSource;
  /**
   * Indicator used to indicate an invalid ID in the database.
   */
  private String invalidID = "N";
  /**
   * Holds the name of the <CODE>Signal</CODE>.
   */
  private String name;
//  /**
//   * Holds the name of the file this <CODE>Signal</CODE> was generated from.
//   */
//  private String externalSourceFileName;
//  /**
//   * Holds the modified date of the file this <CODE>Signal</CODE> was generated 
//   * from.
//   */
//  private java.sql.Date externalSourceFileModifiedDate;
  /**
   * Holds the indicator that determines if the <CODE>Signal</CODE> gets 
   * archived or not. This reflects the data in the ARCH_IND database field and
   * defaults to <CODE>"Y"</CODE>.
   */
  private String archiveIndicator = "N";
  /**
   * Holds the indicator that determines if the <CODE>Signal</CODE> gets 
   * included in the alarm file or not. This reflects the data in the ALARM_IND 
   * database field and defaults to <CODE>"Y"</CODE>.
   */
  private String alarmIndicator = "Y";
  /**
   * Holds the frequency with which this <CODE>Signal</CODE> is archived. This 
   * reflects the value of the ARCH_FREQ database field.
   */
  private BigDecimal archiveFrequency;
  /**
   * Holds the archive type. This field is either <CODE>"periodic"</CODE>, 
   * <CODE>"moitor"</CODE>, or <CODE>null</CODE>, depending on the type of 
   * archiving required. This property reflects the value of the ARCH_TYPE 
   * database field.
   */
  private String archiveType;
  /**
   * Holds the frequency with which this <CODE>Signal</CODE> is archived. This 
   * reflects the value of the EXT_ARCH_FREQ database field.
   */
  private BigDecimal externalArchiveFrequency;
  /**
   * Holds the archive type. This field is either <CODE>"periodic"</CODE>, 
   * <CODE>"moitor"</CODE>, or <CODE>null</CODE>, depending on the type of 
   * archiving required. This property reflects the value of the EXT_ARCH_TYPE 
   * database field.
   */
  private String externalArchiveType;
  /**
   * Holds the indicator that determines if the cable for the 
   * <CODE>Signal</CODE> is complete. This reflects the data in the 
   * CBL_CMPLT_IND database field and defaults to <CODE>"N"</CODE>.
   */
  private String cableCompleteIndicator = "N";
  /**
   * Holds the <CODE>DataFlavor</CODE> used to transfer a single instance of 
   * <CODE>Signal</CODE> via drag and drop.
   */
  final static public DataFlavor SIGNAL_FLAVOR = new DataFlavor(org.csstudio.mps.sns.tools.data.Signal.class, "Signal");
  /**
   * Holds the indicator ised by the archiver to determine if the channel is 
   * disabled. This reflects the data in the SGNL_ARCH_REQ.DISABLE_CHAN_IND 
   * field and defaults to <CODE>"N"</CODE>.
   */
  private String disableChannelIndicator = "N";
  /**
   * Holds the <CODE>EpicsGroup</CODE> for the <CODE>Signal</CODE>.
   * @attribute 
   */
  private EpicsGroup epicsGroup;
//  /**
//   * Holds the value for the machine protection (MPS) indicator. Defaults to 
//   * <CODE>"N"</CODE>.
//   */
//  private String machineProtectionIndicator = "N";
//  /**
//   * Holds the value for the multimode indicator. Defaults to <CODE>"N"</CODE>.
//   */
//  private String multimodeIndicator = "N";
//  /**
//   * Holds the value for the bulk indicator. Defaults to <CODE>"N"</CODE>.
//   */
//  private String bulkIndicator = "N";
  /**
   * Gets the ID of the last user to modify this <CODE>Signal</CODE>.
   */
  private String modifiedByUID;
  /**
   * Gets the <CODE>Date</CODE> this <CODE>Signal</CODE> was last modified.
   */
  private Date modifiedDate;
  /**
   * Holds the instances of <CODE>DBFile</CODE> from which the 
   * <CODE>Signal</CODE> was imported. Idealy this would be one.
   */
  private ArrayList dbFiles = new ArrayList();
  /**
   * Holds the name of the RDB column for the ID property.
   */
  public final static String ID_COLUMN_NAME = "SGNL_ID";
  /**
   * Holds the name of the RDB column for the archive frequency property
   */
  public final static String ARCHIVE_FREQUENCY_COLUMN_NAME = "ARCH_FREQ";
  /**
   * Holds the name of the RDB column for the external archive frequency 
   * property.
   */
  public final static String EXTERNAL_ARCHIVE_FREQUENCY_COLUMN_NAME = "EXT_ARCH_FREQ";
  /**
   * Holds the name of the RDB column for the archive type property
   */
  public final static String ARCHIVE_TYPE_COLUMN_NAME = "ARCH_TYPE";
  /**
   * Holds the name of the RDB column for the external archive type column.
   */
  public final static String EXTERNAL_ARCHIVE_TYPE_COLUMN_NAME = "EXT_ARCH_TYPE";
  /**
   * Holds the name of the RDB column for the disable channel indicator 
   * property.
   */
  public final static String DISABLE_CHANNEL_INDICATOR_COLUMN_NAME = "DISABLE_CHAN_IND";
  /**
   * Holds the name of the RDB column for the type property.
   */
  public final static String TYPE_COLUMN_NAME = "REC_TYPE_ID";
  /**
   * Holds the name of the RDB column for the archive indicator property.
   */
  public final static String ARCHIVE_INDICATOR_COLUMN_NAME = "ARCH_IND";
  /**
   * Holds the name of the RDB table for the class.
   */
  public final static String SIGNAL_TABLE_NAME = "SGNL_REC";
  /**
   * Holds the name of the schema for the RDB table for the class.
   */
  public final static String SIGNAL_SCHEMA_NAME = "EPICS";
  
  /**
   * Creates a new <CODE>Signal</CODE>.
   */
  public Signal()
  {
  }

  /**
   * Creates and initializes a new <CODE>Signal</CODE>. This constructor 
   * initializes the properties of the class with the values passed in.
   *
   * @param id The value to initialize the ID property with.
   */
  public Signal(String id)
  {
    this(id, null, null);
  }

  /**
   * Creates and initializes a new <CODE>Signal</CODE>. This constructor 
   * initializes the properties of the class with the values passed in.
   *
   * @param id The value to initialize the ID property with.
   * @param device The device that was used to create the <CODE>Signal</CODE>.
   * @param type The <CODE>SignalType</CODE> for the <CODE>Signal</CODE>.
   */
  public Signal(String id, Device device, SignalType type)
  {
    setID(id);
    setDevice(device);
    setType(type);
  }
  
  /**
   * Creates an instance of <CODE>Signal</CODE> from the data in the given 
   * <CODE>resultSet</CODE>.
   * 
   * @param data The data from which to create the <CODE>Signal</CODE>.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public Signal(ResultSet data) throws java.sql.SQLException
  {
    super(data);
    String[] columnNames = RDBData.findColumnNames(data);
    if(Arrays.binarySearch(columnNames, TYPE_COLUMN_NAME) >= 0)
      setType(new SignalType(data));
    resetChangedFlag();
  }

  /**
   * Sets the ID of the <CODE>Device</CODE>.
   *
   * @param id The ID for the <CODE>Signal</CODE>.
   */
  public void setID(String id)
  {
    String oldValue = this.id;
    this.id = id;
    firePropertyChange("id", oldValue, id);
    markFieldChanged(ID_COLUMN_NAME);
  }

  /**
   * Gets the ID of the <CODE>Device</CODE>.
   *
   * @return The ID of the <CODE>Signal</CODE>.
   */
  public String getID()
  {
    return id;
  }

  /**
   * Sets the <CODE>Device</CODE> for which the <CODE>Signal</CODE> was created.
   * This method also adds the current <CODE>Signal</CODE> to the 
   * <CODE>Device</CODE> passed in by calling the <CODE>addSignal</CODE> in the 
   * <CODE>Device</CODE> class.
   *
   * @param device The <CODE>Device</CODE> used to generate the <CODE>Signal</CODE>.
   */
  public void setDevice(Device device)
  {
    this.device = device;
    if(device != null && device.getSignal(getID()) == null)
      device.addSignal(this);
  }

  /**
   * Gets the <CODE>Device</CODE> the <CODE>Signal</CODE> was created with.
   *
   * @return The <CODE>Device</CODE> used to generate the <CODE>Signal</CODE>.
   */
  public Device getDevice()
  {
    return device;
  }

  /**
   * Sets the type of the <CODE>Signal</CODE>.
   *
   * @param type The type of the <CODE>Signal</CODE>.
   */
  public void setType(SignalType type)
  {
    SignalType oldValue = this.type;
    this.type = type;
    firePropertyChange("type", oldValue, type);
    markFieldChanged(TYPE_COLUMN_NAME);
  }

  /**
   * Gets the <CODE>Signal</CODE> type.
   *
   * @return The <CODE>Signal</CODE> type.
   */
  public SignalType getType()
  {
    return type;
  }

  /**
   * Returns a <CODE>String</CODE> representation of the <CODE>Signal</CODE>.
   * This is the value of the ID property.
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
   * Called to determine equality. This method compares the ID property of the 
   * two instances of <CODE>Signal</CODE> and all of the fields associated with 
   * it.
   *
   * @param obj The <CODE>Signal</CODE> to compare this instance to.
   * @return <CODE>true</CODE> if the ID property is equal, <CODE>false</CODE> otherwise.
   */
  public boolean equals(Object obj)
  {
    //Check for null
    if(obj == null)
      return false;
    //Check class
    if(! (obj instanceof Signal))
      return false;
    //Check signal ID
    Signal compareTo = (Signal)obj;
    if(! MPSBrowserView.compare(getID(), compareTo.getID()))
      return false;
    //Check device
    if(! MPSBrowserView.compare(getDevice(), compareTo.getDevice()))
      return false;
    //Check signal type
    if(! MPSBrowserView.compare(getType(), compareTo.getType()))
      return false;
    //Check all signal fields
    int fieldCount = getFieldCount();        
    for(int i=0;i<fieldCount;i++)
    {
      SignalField currentField = getFieldAt(i);
      SignalFieldType currentType = currentField.getType();
      //fields with null types are not in the database, and not editable. No need
      //to try to compare these.
      if(currentType != null)
      {
        String currentFieldID = currentType.getID();
        if(! currentField.equals(compareTo.getField(currentFieldID)))
          return false;
      }//if(currentType != null)
    }//for(int i=0;i<fieldCount;i++)
    //Check ID alias
    if(! MPSBrowserView.compare(getIDAlias(), compareTo.getIDAlias()))
      return false;
    //Check the epics group
    if(! MPSBrowserView.compare(getGroup(), compareTo.getGroup()))
      return false;
    //Check the external source
    if(! MPSBrowserView.compare(getExternalSource(), compareTo.getExternalSource()))
      return false;
    //Check the invalid ID indicator
    if(! MPSBrowserView.compare(getInvalidID(), compareTo.getInvalidID()))
      return false;
    //Check the archive indicator
    if(! MPSBrowserView.compare(getArchiveIndicator(), compareTo.getArchiveIndicator()))
      return false;
    //Check the archive frequency
    if(! MPSBrowserView.compare(getArchiveFrequency(), compareTo.getArchiveFrequency()))
      return false;
    //Check archive type
    if(! MPSBrowserView.compare(getArchiveType(), compareTo.getArchiveType()))
      return false;
    return true;
  }

  /**
   * Returns a hash code for the <CODE>Signal</CODE>. If the 
   * <CODE>equals</CODE> method for a class returns <CODE>true</CODE>, the 
   * <CODE>hashCode</CODE> methods for those instances of <CODE>Object</CODE> 
   * must also return the same value. The reverse is not the case, meaning that
   * just because the value returned by the <CODE>hashCode()</CODE> methods of 
   * two instances of a class is the same equality can not be assumed.
   * 
   * @return A hash code for the <CODE>Signal</CODE>.
   */
  public int hashCode()
  {
    int hashCode = 1;
    hashCode = hashCode * 37 + findPropertyHashCode(getID());
    hashCode = hashCode * 37 + findPropertyHashCode(getDevice());
    hashCode = hashCode * 37 + findPropertyHashCode(getType());
    int fieldCount = getFieldCount();        
    for(int i=0;i<fieldCount;i++)
    {
      SignalField currentField = getFieldAt(i);
      SignalFieldType currentType = currentField.getType();
      if(currentType != null)
        hashCode = hashCode * 37 + findPropertyHashCode(currentType.getID());
    }//for(int i=0;i<fieldCount;i++)
    hashCode = hashCode * 37 + findPropertyHashCode(getIDAlias());
    hashCode = hashCode * 37 + findPropertyHashCode(getGroup());
    hashCode = hashCode * 37 + findPropertyHashCode(getExternalSource());
    hashCode = hashCode * 37 + findPropertyHashCode(getInvalidID());
    hashCode = hashCode * 37 + findPropertyHashCode(getArchiveIndicator());
    hashCode = hashCode * 37 + findPropertyHashCode(getArchiveFrequency());
    hashCode = hashCode * 37 + findPropertyHashCode(getArchiveType());
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
   * Adds a field to the <CODE>Signal</CODE>.
   * 
   * @param field The <CODE>SignalField</CODE> to add to the <CODE>Signal</CODE>. Can not be <CODE>null</CODE>.
   */
  public void addField(SignalField field)
  {
    SignalField[] oldValue = getFields();
    signalFields.add(field);
    field.setSignal(this);
    firePropertyChange("fields", oldValue, getFields());
  }

  /**
   * Gets the instances of <CODE>SignalField</CODE> that have been added to the 
   * <CODE>Signal</CODE>.
   * 
   * @return The instances of <CODE>SignalField</CODE> for the <CODE>Signal</CODE>.
   */
  public SignalField[] getFields()
  {
    return (SignalField[])signalFields.toArray(new SignalField[signalFields.size()]);
  }
  
  /**
   * Returns the <CODE>SignalField</CODE> at the given index.
   * 
   * @param index The index of the <CODE>SignalField</CODE> to return.
   * @return A The <CODE>SignalField</CODE> at the given index.
   */
  public SignalField getFieldAt(int index)
  {
    return (SignalField)signalFields.get(index);
  }

  /**
   * Finds the field with the ID that matches the given ID.
   * 
   * @param fieldID The ID of the field to return.
   * @return The <CODE>SignalField</CODE> with the given ID.
   */
  public SignalField getField(String fieldID)
  {
    int index = getFieldIndex(fieldID);
    if(index == -1)
      return null;
    else
      return (SignalField)signalFields.get(index);
  }

  /**
   * Finds the field with the ID that matches the given ID and returns the 
   * index of the field.
   * 
   * @param fieldID The ID of the field to return.
   * @return The index of the <CODE>SignalField</CODE> with the given ID.
   */
  public int getFieldIndex(String fieldID)
  {
    int fieldCount = signalFields.size(), index = -1;
    for(int i=0;i<fieldCount;i++)
    {
      SignalField currentField = getFieldAt(i);
      SignalFieldType currentFieldType = currentField.getType();
      if(currentFieldType != null && currentFieldType.getID().equals(fieldID))
      {
        index = i;
        break;
      }//if(currentFieldType != null && currentFieldType.getID().equals(fieldID))
    }//for(int i=0;i<fieldCount;i++)
    return index;
  }

  /**
   * Removes the field with the ID that matches the given ID.
   * 
   * @param fieldID The ID of the field to return.
   */
  public void removeField(String fieldID)
  {
    SignalField[] oldValue = getFields();
    int index = getFieldIndex(fieldID);
    if(index != -1)
      signalFields.remove(index);
    firePropertyChange("fields", oldValue, getFields());
  }

  /**
   * Returns the number of fields associated with the <CODE>Signal</CODE>.
   * 
   * @return The number of fields The <CODE>Signal</CODE> has.
   */
  public int getFieldCount()
  {
    return signalFields.size();
  }

  /**
   * Creates and returns a clone of the <CODE>Signal</CODE>.
   * 
   * @return A clone of the <CODE>Signal</CODE>.
   */
  public Object clone()
  {
    Signal clone = new Signal(getID());
    Device device = getDevice();
    if(device != null)
      clone.setDevice((Device)device.clone());
    SignalType type = getType();
    if(type != null)
      clone.setType((SignalType)type.clone());
    int fieldCount = getFieldCount();
    for(int i=0;i<fieldCount;i++)
      clone.addField((SignalField)getFieldAt(i).clone());
    clone.setIDAlias(getIDAlias());
    clone.setGroup(getGroup());
    clone.setExternalSource(getExternalSource());
    clone.setInvalidID(getInvalidID());
    clone.setAlarmIndicator(getAlarmIndicator());
    clone.setArchiveIndicator(getArchiveIndicator());
    clone.setArchiveFrequency(getArchiveFrequency());
    clone.setArchiveType(getArchiveType());
    clone.setCableCompleteIndicator(getCableCompleteIndicator());
    return clone;
  }

  /**
   * Allows the in database flag to be set for the field. This is needed to 
   * determine if the query to save the data should be an insert or update 
   * statement. The default value is <CODE>true</CODE>.
   * 
   * @param inDatabase Pass as <CODE>true</CODE> if the field is already in the database, <CODE>false</CODE> if not.
   */
  public void setInDatabase(boolean inDatabase)
  {
    this.inDatabase = inDatabase;
  }

  /**
   * Gets the in database flag for the field. This is needed to determine if the 
   * query to save the data should be an insert or update statement. The default 
   * value is <CODE>true</CODE>.
   * 
   * @return <CODE>true</CODE> if the field is already in the database, <CODE>false</CODE> if not.
   */
  public boolean isInDatabase()
  {
    return inDatabase;
  }

  /**
   * Convenience method that sets the in database flag for all fields in the 
   * <CODE>Signal</CODE>.
   * 
   * @param fieldsInDatabase Value for the in database flag to be applied to all fields in the <CODE>Signal</CODE>
   */
  public void setFieldsInDatabase(boolean fieldsInDatabase)
  {
    int count = getFieldCount();
    for(int i=0;i<count;i++)
      getFieldAt(i).setInDatabase(fieldsInDatabase);
  }
  
  /**
   * Sets the ID alias for the <CODE>Signal</CODE>.
   * 
   * @param idAlias The ID alias for the <CODE>Signal</CODE>.
   */
  public void setIDAlias(String idAlias)
  {
    this.idAlias = idAlias;
  }

  /**
   * Gets the value of the ID alias for the <CODE>Signal</CODE>.
   * 
   * @return The ID alias for the <CODE>Signal</CODE>.
   */
  public String getIDAlias()
  {
    return idAlias;
  }

  /**
   * Sets the <CODE>EpicsGroup</CODE> the <CODE>Signal</CODE> is associated 
   * with.
   * 
   * @param group The <CODE>EpicsGroup</CODE> the <CODE>Signal</CODE> belongs to.
   */
  public void setGroup(EpicsGroup group)
  {
    this.group = group;
  }

  /**
   * Gets the <CODE>EpicsGroup</CODE> the <CODE>Signal</CODE> is associated 
   * with.
   * 
   * @return The <CODE>EpicsGroup</CODE> the <CODE>Signal</CODE> belongs to.
   */
  public EpicsGroup getGroup()
  {
    return group;
  }

  /**
   * Sets the external source for the <CODE>Signal</CODE>.
   * 
   * @param externalSource The name of the external source used to create the <CODE>Signal</CODE>.
   */
  public void setExternalSource(String externalSource)
  {
    this.externalSource = externalSource;
  }

  /**
   * Gets the external source for the <CODE>Signal</CODE>.
   * 
   * @return The name of the external source used to create the <CODE>Signal</CODE>.
   */
  public String getExternalSource()
  {
    return externalSource;
  }

  /**
   * Sets the invalid ID indicator flag.
   * 
   * @param invalidID The new value for the invalid ID indicator flag.
   */
  public void setInvalidID(String invalidID)
  {
    this.invalidID = invalidID;
  }

  /**
   * Gets the value of the invalid ID indicator flag.
   * 
   * @return The invalid ID indicator flag.
   */
  public String getInvalidID()
  {
    return invalidID;
  }

  /**
   * Removes all of the fields from the <CODE>Signal</CODE>.
   */
  public void removeAllFields()
  {
    SignalField[] oldValue = getFields();
    signalFields.clear();
    firePropertyChange("fields", oldValue, getFields());
  }

  /**
   * Sets the name of the <CODE>Signal</CODE>.
   * 
   * @param name The name of the <CODE>Signal</CODE>.
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Gets the name of the <CODE>Signal</CODE>.
   * 
   * @return The name of the <CODE>Signal</CODE>.
   */
  public String getName()
  {
    return name;
  }

//  /**
//   * Sets the name of the file the <CODE>Signal was created from.
//   * 
//   * @param externalSourceFileName The name of the file the <CODE>Signal</CODE> was created from.
//   */
//  public void setExternalSourceFileName(String externalSourceFileName)
//  {
//    this.externalSourceFileName = externalSourceFileName;
//  }
//
//  /**
//   * Gets the name of the file the <CODE>Signal was created from.
//   * 
//   * @return The name of the file the <CODE>Signal</CODE> was created from.
//   */
//  public String getExternalSourceFileName()
//  {
//    return externalSourceFileName;
//  }
//
//  /**
//   * Sets the modified date of the file the <CODE>Signal was created from.
//   * 
//   * @param externalSourceFileModifiedDate The modified date of the file the <CODE>Signal</CODE> was created from.
//   */
//  public void setExternalSourceFileModifiedDate(long externalSourceFileModifiedDate)
//  {
//    this.externalSourceFileModifiedDate = new java.sql.Date(externalSourceFileModifiedDate);
//  }
//
//  /**
//   * Sets the modified date of the file the <CODE>Signal was created from.
//   * 
//   * @param externalSourceFileModifiedDate The modified date of the file the <CODE>Signal</CODE> was created from.
//   */
//  public void setExternalSourceFileModifiedDate(java.sql.Date externalSourceFileModifiedDate)
//  {
//    this.externalSourceFileModifiedDate = externalSourceFileModifiedDate;
//  }
//
//  /**
//   * Gets the modified date of the file the <CODE>Signal was created from.
//   * 
//   * @return The modified date of the file the <CODE>Signal</CODE> was created from.
//   */
//  public java.sql.Date getExternalSourceFileModifiedDate()
//  {
//    return externalSourceFileModifiedDate;
//  }

  /**
   * Compares the <CODE>String</CODE> representations of the objects.
   * 
   * @param o The <CODE>Object</CODE> to compare to.
   * @return the value returned by comparing the <CODE>Strig</CODE> values.
   */
  public int compareTo(Object o)
  {
    return toString().compareTo(String.valueOf(o));
  }

  /**
   * Sets the archive indicator for the <CODE>Signal</CODE>. Pass either 
   * <CODE>"Y"</CODE> or <CODE>"N"</CODE>. <CODE>"Y"</CODE> by default.
   * 
   * @param archiveIndicator Pass as <CODE>"Y"</CODE> to indicate a <CODE>Signal</CODE> that should be archived, <CODE>"N"</CODE> to indicate a <CODE>Signal</CODE> that should not be archived.
   */
  public void setArchiveIndicator(String archiveIndicator)
  {
    String oldValue = this.archiveIndicator;
    this.archiveIndicator = archiveIndicator;
    firePropertyChange("archiveIndicator", oldValue, archiveIndicator);
    markFieldChanged(ARCHIVE_INDICATOR_COLUMN_NAME);
  }

  /**
   * Gets the archive indicator for the <CODE>Signal</CODE>. <CODE>"Y"</CODE> 
   * denotes a <CODE>Signal</CODE> that should be archived.
   * 
   * @return <CODE>"Y"</CODE> if the <CODE>Signal</CODE> should be archived.
   */
  public String getArchiveIndicator()
  {
    return archiveIndicator;
  }

  /**
   * Sets the alarm indicator for the <CODE>Signal</CODE>. Pass either 
   * <CODE>"Y"</CODE> or <CODE>"N"</CODE>. <CODE>"Y"</CODE> by default.
   * 
   * @param alarmIndicator Pass as <CODE>"Y"</CODE> to indicate a <CODE>Signal</CODE> that should be included in the alarm handler file., <CODE>"N"</CODE> to indicate a <CODE>Signal</CODE> that should not be included in the alarm handler file.
   */
  public void setAlarmIndicator(String alarmIndicator)
  {
    this.alarmIndicator = alarmIndicator;
  }

  /**
   * Gets the alarm indicator for the <CODE>Signal</CODE>. <CODE>"Y"</CODE> 
   * denotes a <CODE>Signal</CODE> that should be included in the alarm handler 
   * file.
   * 
   * @return <CODE>"Y"</CODE> if the <CODE>Signal</CODE> should be included in the alarm handler file.
   */
  public String getAlarmIndicator()
  {
    return alarmIndicator;
  }

  /**
   * Sets the frequency at which the <CODE>Signal</CODE> should be archived. The 
   * database field of which this property reflects the value is defined as a 
   * number (6,2).
   * 
   * @param archiveFrequency The frequency at which the <CODE>Signal</CODE> should be archived.
   */
  public void setArchiveFrequency(BigDecimal archiveFrequency)
  {
    BigDecimal oldValue = this.archiveFrequency;
    this.archiveFrequency = archiveFrequency;
    firePropertyChange("archiveFrequency", oldValue, archiveFrequency);
    markFieldChanged(ARCHIVE_FREQUENCY_COLUMN_NAME);
  }

  /**
   * Gets the frequency at which the <CODE>Signal</CODE> should be archived.
   * 
   * @return The frequency at which the <CODE>Signal</CODE> should be archived.
   */
  public BigDecimal getArchiveFrequency()
  {
    return archiveFrequency;
  }

  /**
   * Sets the archive type for the <CODE>Signal</CODE>. The valid values for 
   * this property are <CODE>"periodic"</CODE>, <CODE>"moitor"</CODE>, and 
   * <CODE>null</CODE>.
   * 
   * @param archiveType <CODE>"periodic"</CODE> to monitor at the given archive frequency, <CODE>"monitor"</CODE> to archive when changed, or <CODE>null</CODE> if archive indicator is "N".
   */
  public void setArchiveType(String archiveType)
  {
    String oldValue = this.archiveType;
    this.archiveType = archiveType;
    firePropertyChange("archiveType", this.archiveType, archiveType);
    markFieldChanged(ARCHIVE_TYPE_COLUMN_NAME);
  }

  /**
   * Gets the archive type for the <CODE>Signal</CODE>. 
   * 
   * @return <CODE>"periodic"</CODE>, <CODE>"monitor"</CODE>, or <CODE>null</CODE>.
   */
  public String getArchiveType()
  {
    return archiveType;
  }

  /**
   * Sets the external frequency at which the <CODE>Signal</CODE> should be 
   * archived. The database field of which this property reflects the value is 
   * defined as a number (6,2).
   * 
   * @param archiveFrequency The frequency at which the <CODE>Signal</CODE> should be archived.
   */
  public void setExternalArchiveFrequency(BigDecimal archiveFrequency)
  {
    BigDecimal oldValue = this.externalArchiveFrequency;
    this.externalArchiveFrequency = archiveFrequency;
    firePropertyChange("externalArchiveFrequency", oldValue, archiveFrequency);
    markFieldChanged(EXTERNAL_ARCHIVE_FREQUENCY_COLUMN_NAME);
  }

  /**
   * Gets the external frequency at which the <CODE>Signal</CODE> should be 
   * archived. This value came from the original file where the external archive 
   * frequency property was set in the application.
   * 
   * @return The frequency at which the <CODE>Signal</CODE> should be archived.
   */
  public BigDecimal getExternalArchiveFrequency()
  {
    return externalArchiveFrequency;
  }

  /**
   * Sets the external archive type for the <CODE>Signal</CODE>. The valid 
   * values for this property are <CODE>"periodic"</CODE>, 
   * <CODE>"moitor"</CODE>, and <CODE>null</CODE>.
   * 
   * @param archiveType <CODE>"periodic"</CODE> to monitor at the given archive frequency, <CODE>"monitor"</CODE> to archive when changed, or <CODE>null</CODE> if archive indicator is "N".
   */
  public void setExternalArchiveType(String archiveType)
  {
    String oldValue = this.externalArchiveType;
    this.externalArchiveType = archiveType;
    firePropertyChange("externalArchiveType", oldValue, archiveType);
    markFieldChanged(EXTERNAL_ARCHIVE_TYPE_COLUMN_NAME);
  }

  /**
   * Gets the archive type for the <CODE>Signal</CODE>. This value came from the 
   * original file where the external archive frequency property was set in the 
   * application.
   * 
   * @return <CODE>"periodic"</CODE>, <CODE>"monitor"</CODE>, or <CODE>null</CODE>.
   */
  public String getExternalArchiveType()
  {
    return externalArchiveType;
  }

  /**
   * Sets the cable complete indicator for the <CODE>Signal</CODE>. Pass either 
   * <CODE>"Y"</CODE> or <CODE>"N"</CODE>. <CODE>"N"</CODE> by default.
   * 
   * @param cableCompleteIndicator Pass as <CODE>"Y"</CODE> to indicate a complete cable, <CODE>"N"</CODE> otherwise.
   */
  public void setCableCompleteIndicator(String cableCompleteIndicator)
  {
    this.cableCompleteIndicator = cableCompleteIndicator;
  }

  /**
   * Gets the cable complete indicator for the <CODE>Signal</CODE>.
   * 
   * @return <CODE>"Y"</CODE> to indicate a complete cable, <CODE>"N"</CODE> otherwise.
   */
  public String getCableCompleteIndicator()
  {
    return cableCompleteIndicator;
  }

  /**
   * Gets the instances of <CODE>DataFlavor</CODE> supported by the 
   * <CODE>Signal</CODE>.
   * 
   * @return The instances of <CODE>DataFlavor</CODE> that represent the ways a <CODE>Signal</CODE> can be transferred.
   */
  public DataFlavor[] getTransferDataFlavors()
  {
    DataFlavor[] flavors = new DataFlavor[2];
    flavors[0] = SIGNAL_FLAVOR;
    flavors[1] = DataFlavor.stringFlavor;
    return flavors;
  }

  /**
   * Tests to see if a given <CODE>DataFlavor</CODE> is supported.
   * 
   * @param flavor The flavor to test for support.
   * @return <CODE>true</CODE> if the flavor is supported, <CODE>false</CODE> if it is not supported.
   */
  public boolean isDataFlavorSupported(DataFlavor flavor)
  {
    if(flavor.getRepresentationClass().equals(getClass()))
      return true;
    else
      return flavor.equals(DataFlavor.stringFlavor);
  }

  /**
   * Gets the transfer data.
   * 
   * @param flavor The <CODE>DataFlavor</CODE> to be transferred.
   * @return The data to be transferred.
   * @throws UnsupportedFlavorException Thrown if the given <CODE>DataFlavor</CODE> is not supported.
   */
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
  {
    if(flavor.getRepresentationClass().equals(getClass()))
      return this;
    else
      if(flavor.equals(DataFlavor.stringFlavor))
        return getID();
      else
        throw new UnsupportedFlavorException(flavor);
  }

  /**
   * Sets the channel disabled indicator for the <CODE>Signal</CODE>. 
   * Pass either <CODE>"Y"</CODE> or <CODE>"N"</CODE>. <CODE>"N"</CODE> by 
   * default.
   * 
   * @param newDisableChannelIndicator Pass as <CODE>"Y"</CODE> to indicate a <CODE>Signal</CODE> that is enabled, <CODE>"N"</CODE> to indicate a <CODE>Signal</CODE> that is disabled.
   */
  public void setDisableChannelIndicator(String newDisableChannelIndicator)
  {
    String oldValue = this.disableChannelIndicator;
    this.disableChannelIndicator = newDisableChannelIndicator;
    firePropertyChange("disableChannelIndicator", oldValue, newDisableChannelIndicator);
    markFieldChanged(DISABLE_CHANNEL_INDICATOR_COLUMN_NAME);
  }

  /**
   * Gets the disabled indicator for the <CODE>Signal</CODE>. <CODE>"Y"</CODE> 
   * denotes a <CODE>Signal</CODE> that is disabled in the archive.
   * 
   * @return <CODE>"Y"</CODE> if the <CODE>Signal</CODE> is disabled in archives.
   */
  public String getDisableChannelIndicator()
  {
    return disableChannelIndicator;
  }

  /**
   * Gets the <CODE>EpicsGroup</CODE> for the <CODE>Signal</CODE>.
   * 
   * @return The <CODE>EpicsGroup</CODE> for the <CODE>Signal</CODE>.
   */
  public EpicsGroup getEpicsGroup()
  {
    return epicsGroup;
  }

  /**
   * Sets the <CODE>EpicsGroup</CODE> for the <CODE>Signal</CODE>.
   * 
   * @param newEpicsGroup The mew <CODE>EpicsGroup</CODE> for the <CODE>Signal</CODE>.
   */
  public void setEpicsGroup(EpicsGroup newEpicsGroup)
  {
    epicsGroup = newEpicsGroup;
  }
//
//  /**
//   * Gets the value for the machine protection indicator.
//   * 
//   * @return The value of the machine protection indicator.
//   */
//  public String getMachineProtectionIndicator()
//  {
//    return machineProtectionIndicator;
//  }
//
//  /**
//   * Sets the value of the machine protection indicator. The default value is 
//   * <CODE>"N"</CODE>.
//   * 
//   * @param newMachineProtectionIndicator The new value for the machine protection indicator.
//   */
//  public void setMachineProtectionIndicator(String newMachineProtectionIndicator)
//  {
//    machineProtectionIndicator = newMachineProtectionIndicator;
//  }
//
//  /**
//   * Gets the value for the multimode indicator.
//   * 
//   * @return The value of the multimode indicator.
//   */
//  public String getMultimodeIndicator()
//  {
//    return multimodeIndicator;
//  }
//
//  /**
//   * Sets the value of the multimode indicator. The default value is 
//   * <CODE>"N"</CODE>.
//   * 
//   * @param newMultimodeIndicator The new value for the multimode indicator.
//   */
//  public void setMultimodeIndicator(String newMultimodeIndicator)
//  {
//    multimodeIndicator = newMultimodeIndicator;
//  }
//
//  /**
//   * Gets the value for the bulk indicator.
//   * 
//   * @return The value of the bulk indicator.
//   */
//  public String getBulkIndicator()
//  {
//    return bulkIndicator;
//  }
//
//  /**
//   * Sets the value of the bulk indicator. The default value is 
//   * <CODE>"N"</CODE>.
//   * 
//   * @param newBulkIndicator The new value for the multimode indicator.
//   */
//  public void setBulkIndicator(String newBulkIndicator)
//  {
//    bulkIndicator = newBulkIndicator;
//  }

  /**
   * Gets the ID of the last user to modify this <CODE>Signal</CODE>.
   * 
   * @return The ID of the last user to modify this <CODE>Signal</CODE>.
   */
  public String getModifiedByUID()
  {
    return modifiedByUID;
  }

  /**
   * Sets the ID of the last user to modify this <CODE>Signal</CODE>. This field
   * in the database is set by a trigger.
   * 
   * @param newModifiedByUID The ID of the last user to modify this <CODE>Signal</CODE>.
   */
  public void setModifiedByUID(String newModifiedByUID)
  {
    modifiedByUID = newModifiedByUID;
  }

  /**
   * Gets the <CODE>Date</CODE> that this <CODE>Signal</CODE> was last modified.
   * 
   * @return The <CODE>Date</CODE> this <CODE>Signal</CODE> was last modified.
   */
  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  /**
   * Sets the <CODE>Date</CODE> that this <CODE>Signal</CODE> was last modified.
   * 
   * @param newModifiedDate The <CODE>Date</CODE> this <CODE>Signal</CODE> was last modified.
   */
  public void setModifiedDate(Date newModifiedDate)
  {
    modifiedDate = newModifiedDate;
  }

  /**
   * Gets the <CODE>DBFile</CODE> to which this <CODE>Signal</CODE> belongs.
   * 
   * @param index The index of the <CODE>DBFile</CODE> to return.
   * @return The <CODE>DBFile</CODE> to which the <CODE>Signal</CODE> belongs.
   */
  public DBFile getDBFileAt(int index)
  {
    return (DBFile)dbFiles.get(index);
  }

  /**
   * Returns the number of instances of <CODE>DBFile</CODE> that have been added 
   * to the <CODE>Signal</CODE>.
   * 
   * @return The number of <CODE>DBFiles</CODE> that have been added.
   */
  public int getDBFileCount()
  {
    return dbFiles.size();
  }
  
  /**
   * Adds the <CODE>DBFile</CODE> to those to which this <CODE>Signal</CODE> 
   * belongs. This method calls the <CODE>addSignal</CODE> method in 
   * <CODE>DBFile</CODE>, so there is no need to call both.
   * 
   * @param dbFile dbFile The <CODE>DBFile</CODE> from which the <CODE>Signal</CODE> was imported.
   */
  public void addDBFile(DBFile dbFile)
  {
    dbFiles.add(dbFile);
    if(dbFile.getIndexOfSignal(getID()) == -1)
      dbFile.addSignal(this);
  }

  /**
   * Removes all instances of <CODE>DBFile</CODE> from the <CODE>Signal</CODE>.
   */
  public void removeAllDBFiles()
  {
    dbFiles.clear();
  }

  /**
   * Finds the index of the <CODE>DBFile</CODE> with the given file name.
   * 
   * @param fileName The file name of the <CODE>DBFile</CODE> ofwhich to return the index.
   * @return The index of the <CODE>DBFile</CODE> with the given name, <CODE>-1</CODE> if it is not found.
   */
  public int getIndexOfDBFile(String fileName)
  {
    int count = getDBFileCount();
    for(int i=0;i<count;i++) 
    {
      DBFile file = getDBFileAt(i);
      if(file.getFileName().equals(fileName))
        return i;
    }
    return -1;
  }

  /**
   * Returns the <CODE>DBFile</CODE> with the given name.
   * 
   * @param fileName The name of the <CODE>DBFile</CODE> to return.
   * @return The <CODE>DBFile</CODE> with the given name, or <CODE>null</CODE> if it is not found.
   */
  public DBFile getDBFile(String fileName)
  {
    int index = getIndexOfDBFile(fileName);
    if(index >= 0)
      return getDBFileAt(index);
    else
      return null;
  }
  
  /**
   * Removes the <CODE>DBFile</CODE> with the given name from the 
   * <CODE>Signal</CODE>. This method also calls the <CODE>removeSignal</CODE>
   * method in <CODE>DBFile</CODE>, so it is not necessary to call both.
   * 
   * @param fileName The name of the <CODE>DBFile</CODE> to remove.
   */
  public void removeDBFile(String fileName)
  {
    int index = getIndexOfDBFile(fileName);
    if(index >= 0)
    {
      DBFile file = (DBFile)dbFiles.remove(index);
      file.removeSignal(getID());
    }
  }

  /**
   * Returns the RDB schema to which the SGNL_REC table belongs.
   * 
   * @return The name of the RDB schema that corresponds to the class.
   */
  protected String getSchemaName()
  {
    return "EPICS";
  }

  /**
   * Returns the name of the RDB table which the class represents.
   * 
   * @return The name of the RDB table that corresponds to the class.
   */
  protected String getTableName()
  {
    return SIGNAL_TABLE_NAME;
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
    if(rdbFieldName.equals(ID_COLUMN_NAME))
      return getID();
    if(rdbFieldName.equals(ARCHIVE_FREQUENCY_COLUMN_NAME))
      return getArchiveFrequency();
    if(rdbFieldName.equals(EXTERNAL_ARCHIVE_FREQUENCY_COLUMN_NAME))
      return getExternalArchiveFrequency();
    if(rdbFieldName.equals(ARCHIVE_TYPE_COLUMN_NAME))
      return getArchiveType();
    if(rdbFieldName.equals(EXTERNAL_ARCHIVE_TYPE_COLUMN_NAME))
      return getExternalArchiveType();
    if(rdbFieldName.equals(DISABLE_CHANNEL_INDICATOR_COLUMN_NAME))
      return getDisableChannelIndicator();
    if(rdbFieldName.equals(TYPE_COLUMN_NAME))
      return getType().getRecordType().getID();
    if(rdbFieldName.equalsIgnoreCase(ARCHIVE_INDICATOR_COLUMN_NAME))
      return getArchiveIndicator();
    return null;
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
    if(rdbFieldName.equalsIgnoreCase(ID_COLUMN_NAME))
      setID((String)value);
    if(rdbFieldName.equalsIgnoreCase(ARCHIVE_FREQUENCY_COLUMN_NAME))
      setArchiveFrequency((BigDecimal)value);
    if(rdbFieldName.equalsIgnoreCase(EXTERNAL_ARCHIVE_FREQUENCY_COLUMN_NAME))
      setExternalArchiveFrequency((BigDecimal)value);
    if(rdbFieldName.equalsIgnoreCase(ARCHIVE_TYPE_COLUMN_NAME))
      setArchiveType((String)value);
    if(rdbFieldName.equalsIgnoreCase(EXTERNAL_ARCHIVE_TYPE_COLUMN_NAME))
      setExternalArchiveType((String)value);
    if(rdbFieldName.equalsIgnoreCase(DISABLE_CHANNEL_INDICATOR_COLUMN_NAME))
      setDisableChannelIndicator((String)value);
    if(rdbFieldName.equalsIgnoreCase(ARCHIVE_INDICATOR_COLUMN_NAME))
      setArchiveIndicator((String)value);
  }
}