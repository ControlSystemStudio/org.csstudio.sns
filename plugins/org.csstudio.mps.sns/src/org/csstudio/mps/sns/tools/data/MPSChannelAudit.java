package org.csstudio.mps.sns.tools.data;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * Holds the data for a single audit of a <CODE>MPSChannel</CODE>.
 * 
 * @author Chris Fowlkes
 */
public class MPSChannelAudit extends RDBData
{
  /**
   * Holds the date that the channel was last audited.
   */
  private Timestamp date;
  /**
   * Indicates if the <CODE>MPSChannel</CODE> passed the last audit.
   */
  private String passIndicator;
  /**
   * Holds the <CODE>MPSChannel</CODE> to which the audit belongs.
   */
  private MPSChannel channel;
  /**
   * Holds the name of the database column for the pass indicator property.
   */
  public final static String PASS_INDICATOR_COLUMN_NAME = "PASS_IND";
  /**
   * Holds the name of the database column for the audit date property.
   */
  public final static String DATE_COLUMN_NAME = "AUDIT_DTE";
  

  /**
   * Creates a new <CODE>MPSChannelAudit</CODE>
   */
  public MPSChannelAudit()
  {
  }

  /**
   * Creates a <CODE>MPSChannelAudit</CODE> from the given data.
   * 
   * @param data The data with which to initialize the <CODE>MPSChannelAudit</CODE>.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public MPSChannelAudit(ResultSet data) throws java.sql.SQLException
  {
    super(data);
  }
  
  /**
   * Gets the name of the schema for the class.
   * 
   * @return The schema name for the table where the class data is stored.
   */
  protected String getSchemaName()
  {
    return "EPICS";
  }

  /**
   * Gets the name of the database table for the class.
   * 
   * @return The database table name where the class data is stored.
   */
  protected String getTableName()
  {
    return "MPS_CHAN_AUDIT";
  }

  /**
   * Gets the value for the given database field.
   * 
   * @param rdbFieldName The name of the field of which to return the value.
   * @return The value for the given database field.
   */
  protected Object getValue(String rdbFieldName)
  {
    if(rdbFieldName.equals(PASS_INDICATOR_COLUMN_NAME))
      return getPassIndicator();
    else
      if(rdbFieldName.equals(DATE_COLUMN_NAME))
        return getDate();
    return null;
  }

  /**
   * Sets the value for the given database field.
   * 
   * @param rdbFieldName The name of the RDB field of which to set the value.
   * @param value The value for the given database field.
   */
  protected void setValue(String rdbFieldName, Object value)
  {
    if(rdbFieldName.equals(PASS_INDICATOR_COLUMN_NAME))
      setPassIndicator((String)(value));
    else
      if(rdbFieldName.equals(DATE_COLUMN_NAME)) {
    	  setDate((Timestamp)value);
      }
  }
  
  /**
   * Sets the pass indicator for the audit.
   * 
   * The value for the pass indicator.
   */
  public void setPassIndicator(String passIndicator)
  {
    this.passIndicator = passIndicator;
  }
  
  /**
   * Gets the value of the pass indicator.
   * 
   * @return The pass indicator for the audit.
   */
  public String getPassIndicator()
  {
    return passIndicator;
  }
  
  /**
   * Sets the <CODE>Date</CODE> of the audit.
   * 
   * @param date The <CODE>Date</CODE> of the audit.
   */
  public void setDate(Timestamp date)
  {
    this.date = date;
  }
  
  /**
   * Gets the <CODE>Date</CODE> of the audit.
   * 
   * @return The <CODE>Date</CODE> of the audit.
   */
  public Timestamp getDate()
  {
    return date;
  }
  
  /**
   * Sets the channel for the audit.
   * 
   * @param channel The <CODE>MPSChannel</CODE> for the audit.
   */
  public void setChannel(MPSChannel channel)
  {
    this.channel = channel;
    if(channel.getAudit(getDate()) == null)
      channel.addAudit(this);
  }
  
  /**
   * Gets the <CODE>MPSChannel</CODE> to which the <CODE>MPSChannelAudit</CODE>
   * belongs.
   * 
   * @return The <CODE>MPSChannel</CODE> to which the <CODE>MPSChannelAudit</CODE> belongs.
   */
  public MPSChannel getChannel()
  {
    return channel;
  }
}
