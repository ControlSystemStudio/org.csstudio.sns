package org.csstudio.mps.sns.tools.data;
import java.sql.ResultSet;
import java.util.Arrays;

/**
 * Provides a class to hold the data in the SGNL_REC_TYPE table.
 * 
 * @author Chris Fowlkes
 */
public class EpicsRecordType 
{
  /**
   * Holds the data in the REC_TYPE_ID field of the SGNL_REC_TYPE table.
   */
  private String id;
  /**
   * Holds the data in the REC_TYPE_CODE field of the SGNL_REC_TYPE table.
   */
  private String code;
  /**
   * Holds the data in the TYPE_DESC field of the SGNL_REC_TYPE table.
   */
  private String description;
  /**
   * A flag used to determine if the data is in the database. <CODE>true</CODE>
   * by default.
   */
  private boolean inDatabase = true;
  /**
   * Holds the name of the RDB column for the ID property.
   */
  public final static String ID_COLUMN_NAME = "REC_TYPE_ID";

  /**
   * Creates a new <CODE>EpicsRecordType</CODE>.
   */
  public EpicsRecordType()
  {
  }

  /**
   * Creates a new <CODE>EpicsRecordType</CODE> and initializes the id property.
   * 
   * @param id The value of the id property.
   */
  public EpicsRecordType(String id)
  {
    this();
    setID(id);
  }

  /**
   * Creates a new <CODE>EpicsRecordType</CODE> and initializes the id, code, 
   * and description properties.
   * 
   * @param id The value of the id property.
   * @param code The value of the code property.
   * @param description The value of the description property.
   */
  public EpicsRecordType(String id, String code, String description)
  {
    this(id);
    setCode(code);
    setDescription(description);
  }
  
  /**
   * Creates an <CODE>EpicsRecordtype</CODE> from the RDB data.
   * 
   * @param data The data with which to create the <CODE>EpicsRecordType</CODE>.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public EpicsRecordType(ResultSet data) throws java.sql.SQLException
  {
    this();
    String[] columnNames = RDBData.findColumnNames(data);
    if(Arrays.binarySearch(columnNames, ID_COLUMN_NAME) >= 0)
      setID(data.getString(ID_COLUMN_NAME));
  }

  /**
   * Sets the value of the id property. This property reflects the value of the 
   * REC_TYPE_ID field in the database.
   * 
   * @param id The new value of the id field.
   */
  public void setID(String id)
  {
    this.id = id;
  }

  /**
   * Gets the value of the id property. This property reflects the value of the 
   * REC_TYPE_ID field in the database.
   * 
   * @return The value of the id field.
   */
  public String getID()
  {
    return id;
  }

  /**
   * Sets the value of the code property. This property reflects the value of 
   * the REC_TYPE_CODE field in the database.
   * 
   * @param code The new value of the code field.
   */
  public void setCode(String code)
  {
    this.code = code;
  }

  /**
   * Gets the value of the code property. This property reflects the value of 
   * the REC_TYPE_CODE field in the database.
   * 
   * @return The value of the code field.
   */
  public String getCode()
  {
    return code;
  }

  /**
   * Sets the value of the description property. This property reflects the value of 
   * the TYPE_DESC field in the database.
   * 
   * @param description The new value of the description field.
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * Gets the value of the description property. This property reflects the value of 
   * the TYPE_DESC field in the database.
   * 
   * @return The value of the description field.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Sets the value of the in database flag. This flag is used to determine if 
   * the data is in the database, or needs to be inserted when saving.
   * 
   * @param inDatabase The new value of the in database flag.
   */
  public void setInDatabase(boolean inDatabase)
  {
    this.inDatabase = inDatabase;
  }

  /**
   * Gets the value of the in database flag. This flag is used to determine if 
   * the data is in the database, or needs to be inserted when saving.
   * 
   * @return The value of the in database flag.
   */
  public boolean isInDatabase()
  {
    return inDatabase;
  }

  /**
   * Provides a <CODE>String</CODE> representation of the 
   * <CODE>EpicsRecordType</CODE>. The value returned is the value of the id 
   * property.
   * 
   * @return A <CODE>String</CODE> representation of the <CODE>EpicsRecordType</CODE>.
   */
  public String toString()
  {
    String id = getID();
    if(id == null)
      id = "";
    return id;
  }
}