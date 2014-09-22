package org.csstudio.mps.sns.tools.data;
import com.cosylab.gui.components.ProgressEvent;
import com.cosylab.gui.components.ProgressListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Provides a super class for all classes that contain the data for a record in 
 * the RDB.
 * 
 * @author Chris Fowlkes
 */
public abstract class RDBData 
{
  /**
   * A flag used to tell if the <CODE>DBFile</CODE> is in the database.
   */
  private Boolean inDatabase = Boolean.FALSE;
  private transient PropertyChangeSupport propertyChangeSupport;
  private transient ArrayList progressListeners = new ArrayList(2);
  /**
   * Contains the names of the primary key columns for the table as defined in
   * the database.
   */
  private static TreeMap primaryKeys = new TreeMap();
  /**
   * Flag used to determine if a commit is needed on the 
   * <CODE>Connection</CODE>.
   */
  private Boolean commitNeeded = Boolean.FALSE;

  /**
   * Holds the names of the RDB fields that have been changed.
   */
  private ArrayList changedRDBFields = new ArrayList();

  /**
   * Creates a new <CODE>RDBData</CODE> instance.
   */
  public RDBData()
  {
    propertyChangeSupport = new PropertyChangeSupport(this);
  }
  
  /**
   * Creates an instance of <CODE>RDBData</CODE> and initializes it with the 
   * given data from the RDB.
   * 
   * @param data The data from the RDB for the instance of <CODE>RDBData</CODE>.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public RDBData(ResultSet data) throws java.sql.SQLException
  {
    this();
    ResultSetMetaData metaData = data.getMetaData();
    int columnCount = data.getMetaData().getColumnCount();
    for(int i=1;i<=columnCount;i++)
      setValue(metaData.getColumnName(i), data.getObject(i));
    resetChangedFlag();
    setInDatabase(true);
  }
  
  /**
   * Creates an instance of <CODE>RDBData</CODE> and initializes it with the 
   * given data from the RDB.
   * 
   * @param data The data from the RDB for the instance of <CODE>RDBData</CODE>.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  protected RDBData(ResultSet data, Map typeMap) throws java.sql.SQLException
  {
    this();
    ResultSetMetaData metaData = data.getMetaData();
    int columnCount = data.getMetaData().getColumnCount();
    for(int i=1;i<=columnCount;i++)
    {
      String columnType = metaData.getColumnTypeName(i);
      Object columnClass = typeMap.get(columnType);
      Object value;
      if(columnClass != null && columnClass instanceof Class)
        if(columnClass == Timestamp.class)
          value = data.getTimestamp(i);
        else
          value = data.getObject(i);
      else
        value = data.getObject(i);
      setValue(metaData.getColumnName(i), value);
    }
    resetChangedFlag();
    setInDatabase(true);
  }
  
  /**
   * Needs to be overridden by the subclass to return the name of the table to 
   * which the data belongs. If no schema is needed, <CODE>null</CODE> should be 
   * returned.
   * 
   * @return The name of the RDB schema that corresponds to the class, or <CODE>null</CODE> if none applies.
   */
  abstract protected String getSchemaName();
  
  /**
   * Needs to be overridden by the subclass to return the name of the table to 
   * which the data belongs. When upadates are done, they will be done in the 
   * order that the tables are returned.
   * 
   * @return The name of the RDB table that corresponds to the class.
   */
  abstract protected String getTableName();
  
  /**
   * Should return the value of the RDB field as stored in the 
   * <CODE>RDBData</CODE> instance.
   * 
   * @return The value of the property that corresponds to the given RDB field.
   */
  abstract protected Object getValue(String rdbFieldName);
  
  /**
   * Should set the value of the property to the value of the RDB field. If the 
   * field name passed in does not correspond to a property in the class, it 
   * should just be ignored.
   * 
   * @param rdbFieldName The name of the field for the data.
   * @param value The value of the RDB field.
   * @return The value of the property that corresponds to the given RDB field.
   */
  abstract protected void setValue(String rdbFieldName, Object value);
  
//  /**
//   * Should return a descriptive name for a record in the table to be used in 
//   * messages sent to the user for feedback. For example, <CODE>"Signal"</CODE>
//   * should be returned by the class for the SGNL_REC table and 
//   * <CODE>Device</CODE> for the class that representsa the DVC table.
//   * 
//   * @return A descriptive name of the table.
//   */
//  abstract public String getDescriptiveName();
  
  /**
   * Marks a field as being changed.
   * 
   * @param fieldName The name of the field changed.
   */
  protected void markFieldChanged(String fieldName)
  {
    PropertyChangeEvent event = null;
    synchronized(changedRDBFields)
    {
      if(! changedRDBFields.contains(fieldName))
      {
        Boolean oldValue = Boolean.valueOf(changedRDBFields.size() > 0);
        changedRDBFields.add(fieldName);
        event = new PropertyChangeEvent(this, "changed", oldValue, Boolean.TRUE);
      }
    }
    if(event != null)
      firePropertyChange(event);
  }

  /**
   * Determines if the <CODE>DBFile</CODE> has changes that need to be saved to
   * the RDB.
   * 
   * @return <CODE>true</CODE> if properties of the <CODE>DBFile</CODE> from the database have changed, <CODE>false</CODE> otherwise.
   */
  public boolean isChanged()
  {
    synchronized(changedRDBFields)
    {
      if(changedRDBFields.size() > 0)
        return true;
      return false;
    }
  }
    
  /**
   * Manually clears the changed flag.
   */
  public void resetChangedFlag()
  {
    PropertyChangeEvent event;
    synchronized(changedRDBFields)
    {
      if(isChanged())
      {
        changedRDBFields.clear();
        event = new PropertyChangeEvent(this, "changed", Boolean.TRUE, Boolean.FALSE);
        firePropertyChange(event);
      }
    }
  }
  
  /**
   * Resets the changed flag for the given fields.
   * 
   * @param fieldNames The names of the fields for which to reset the changed flag.
   */
  public void resetChangedFlag(String[] fieldNames)
  {
    PropertyChangeEvent event;
    synchronized(changedRDBFields)
    {
      Boolean oldValue = Boolean.valueOf(isChanged());
      changedRDBFields.removeAll(Arrays.asList(fieldNames));
      Boolean newValue = Boolean.valueOf(isChanged());
      event = new PropertyChangeEvent(this, "changed", oldValue, newValue);
    }
    firePropertyChange(event);
  }
  
  /**
   * Sets the value of the in database flag. Defaults to <CODE>false</CODE>.
   * 
   * @param inDatabase The new value of the in database flag.
   */
  public void setInDatabase(boolean inDatabase)
  {
    PropertyChangeEvent event;
    synchronized(this.inDatabase)
    {
      Boolean oldValue = this.inDatabase;
      this.inDatabase = Boolean.valueOf(inDatabase);
      event = new PropertyChangeEvent(this, "inDatabase", oldValue, this.inDatabase);
    }
    firePropertyChange(event);
  }

  /**
   * Gets the value of the in database flag.
   * 
   * @return <CODE>true</CODE> if the <CODE>DBFile</CODE> is in the database, <CODE>false</CODE> if it is not in the database.
   */
  public boolean isInDatabase()
  {
    synchronized(inDatabase)
    {
      return inDatabase.booleanValue();
    }
  }

  /**
   * Fires a progress change event. 
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   */
  protected void fireProgress(final ProgressEvent e)
  {
    fireProgress(e, progressListeners);
  }

  /**
   * Fires a progress change event. 
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> to notify.
   */
  static protected void fireProgress(final ProgressEvent e, ArrayList progressListeners)
  {
    if(progressListeners != null)
      synchronized(progressListeners)
      {
        int count = progressListeners.size();
        for (int i=0;i<count;i++)
          ((ProgressListener)progressListeners.get(i)).progress(e);
      }
  }

  /**
   * Fires a task started event. This is done in a thread safe manner using
   * <CODE>SwingUtilities.invokeLater</CODE>.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   */
  protected void fireTaskStarted(final ProgressEvent e)
  {
    fireTaskStarted(e, progressListeners);
  }

  /**
   * Fires a task started event. This is done in a thread safe manner using
   * <CODE>SwingUtilities.invokeLater</CODE>.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> to notify.
   */
  static protected void fireTaskStarted(final ProgressEvent e, ArrayList progressListeners)
  {
    if(progressListeners != null)
      synchronized(progressListeners)
      {
        int count = progressListeners.size();
        for (int i=0;i<count;i++)
          ((ProgressListener)progressListeners.get(i)).taskStarted(e);
      }
  }

  /**
   * Fires a task started event. This is done in a thread safe manner using
   * <CODE>SwingUtilities.invokeLater</CODE>.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   */
  protected void fireTaskInterrupted(final ProgressEvent e)
  {
    fireTaskInterrupted(e, progressListeners);
  }

  /**
   * Fires a task started event. This is done in a thread safe manner using
   * <CODE>SwingUtilities.invokeLater</CODE>.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> to notify.
   */
  static protected void fireTaskInterrupted(final ProgressEvent e, ArrayList progressListeners)
  {
    if(progressListeners != null)
      synchronized(progressListeners)
      {
        int count = progressListeners.size();
        for (int i=0;i<count;i++)
          ((ProgressListener)progressListeners.get(i)).taskInterruped(e);
      }
  }

  /**
   * Fires a task started event. This is done in a thread safe manner using
   * <CODE>SwingUtilities.invokeLater</CODE>.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   */
  protected void fireTaskComplete(final ProgressEvent e)
  {
    fireTaskComplete(e, progressListeners);
  }

  /**
   * Fires a task started event. This is done in a thread safe manner using
   * <CODE>SwingUtilities.invokeLater</CODE>.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   */
  static protected void fireTaskComplete(final ProgressEvent e, ArrayList progressListeners)
  {
    if(progressListeners != null)
      synchronized(progressListeners)
      {
        int count = progressListeners.size();
        for (int i=0;i<count;i++)
          ((ProgressListener)progressListeners.get(i)).taskComplete(e);
      }
  }

  /**
   * Adds the given <CODE>ProgressListener</CODE> to the instance of 
   * <CODE>RDBObject</CODE>.
   * 
   * @param l The <CODE>ProgressListener</CODE> to add to the <CODE>RDBObject</CODE>.
   */
  public void addProgressListener(ProgressListener l)
  {
    addProgressListeners(new ProgressListener[]{l});
  }

  /**
   * Adds the given instances of <CODE>ProgressListener</CODE> to the instance 
   * of <CODE>RDBObject</CODE>.
   * 
   * @param l The <CODE>ProgressListener</CODE> to add to the module.
   */
  public void addProgressListeners(ProgressListener[] l)
  {
    if(progressListeners != null)
      synchronized(progressListeners)
      {
        for(int i=0;i<l.length;i++) 
          if(! progressListeners.contains(l[i]))
            progressListeners.add(l[i]);
      }
  }

  /**
   * Removes the given <CODE>ProgressListener</CODE> from the <CODE>RDBObject</CODE>.
   * 
   * @param l The <CODE>ProgressListener</CODE> to remove from the <CODE>RDBObject</CODE>.
   */
  public void removeProgressListener(ProgressListener l)
  {
    removeProgressListeners(new ProgressListener[]{l});
  }

  /**
   * Removes the given instances of <CODE>ProgressListener</CODE> from the <CODE>RDBObject</CODE>.
   * 
   * @param l The instances of <CODE>ProgressListener</CODE> to remove from the <CODE>RDBObject</CODE>.
   */
  public void removeProgressListeners(ProgressListener[] l)
  {
    synchronized(progressListeners)
    {
      for(int i=0;i<l.length;i++) 
        progressListeners.remove(l[i]);
    }
  }
  
  /**
   * Saves any changes to the <CODE>Object</CODE>.
   * 
   * @param connection The <CODE>Connection</CODE> used to connect to the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void update(Connection connection) throws java.sql.SQLException
  {
    synchronized(changedRDBFields)
    {
      String[] changedFields = new String[changedRDBFields.size()];
      for(int i=0;i<changedFields.length;i++) 
        changedFields[i] = changedRDBFields.get(i).toString();
      ArrayList primaryKeys = getPrimaryKeys(connection);
      update(connection, changedFields, primaryKeys, getTableName());
    }
  }
  
  /**
   * Saves any changes to the <CODE>Object</CODE>.
   * 
   * @param connection The <CODE>Connection</CODE> used to connect to the database.
   * @param changedFields The names of the fields to update.
   * @param keyFields The names of the primary keys.
   * @param tableName The name of the table to update.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  protected void update(Connection connection, String[] changedFields, ArrayList keyFields, String tableName) throws java.sql.SQLException
  {
    synchronized(changedRDBFields)
    {
      if(changedFields.length > 0)
      {
        StringBuffer sql = new StringBuffer("UPDATE ");
        sql.append(buildTableReference(tableName));
        sql.append(" SET ");
        for(int i=0;i<changedFields.length;i++) 
        {
          if(i > 0)
            sql.append(", ");
          sql.append(changedFields[i]);
          sql.append(" = ?");
        }
        sql.append(" WHERE ");
        int keyCount = keyFields.size();
        for(int i=0;i<keyCount;i++) 
        {
          if(i > 0)
            sql.append(" AND ");
          sql.append(keyFields.get(i));
          sql.append(" = ?");
        }
//        String sqlString = sql.toString();
        PreparedStatement query = connection.prepareStatement(sql.toString());
        try
        {
          for(int i=0;i<changedFields.length;i++) 
          {
            Object value = getValue(changedFields[i]);
            String stringValue;
            if(value == null)
              stringValue = null;
            else
              stringValue = value.toString();
            query.setString(i + 1, stringValue);
//            sqlString = sqlString.replaceFirst("\\?", "'" + value + "'");
          }
          for(int i=0;i<keyCount;i++) 
          {
            Object value = getValue(keyFields.get(i).toString());
            String stringValue;
            if(value == null)
              stringValue = null;
            else
              stringValue = value.toString();
            query.setString(changedFields.length + i + 1, stringValue);
//            sqlString = sqlString.replaceFirst("\\?", "'" + stringValue + "'");
          }
//          System.out.println(sqlString);
          query.execute();
          setCommitNeeded(true);
          resetChangedFlag(changedFields);
        }
        finally
        {
          query.close();
        }
      }
    }
  }
  
  /**
   * Inserts any changes to the <CODE>Object</CODE>.
   * 
   * @param connection The <CODE>Connection</CODE> used to connect to the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void insert(Connection connection) throws java.sql.SQLException
  {
    synchronized(changedRDBFields)
    {
      int changedFieldCount = changedRDBFields.size();
      String[] queryFields = new String[changedFieldCount];
      StringBuffer sql = new StringBuffer("INSERT INTO ");
      sql.append(buildTableReference(getTableName()));
      sql.append(" (");
      for(int i=0;i<changedFieldCount;i++) 
      {
        queryFields[i] = changedRDBFields.get(i).toString();
        if(i > 0)
          sql.append(", ");
        sql.append(queryFields[i]);
      }
      sql.append(") VALUES (");
      for(int i=0;i<changedFieldCount;i++) 
      {
        if(i > 0)
          sql.append(", ");
        sql.append("?");
      }
      sql.append(")");
      PreparedStatement query = connection.prepareStatement(sql.toString());
      try
      {
        for(int i=0;i<queryFields.length;i++) 
        {
          Object value = getValue(queryFields[i]);
          String stringValue;
          if(value == null)
            stringValue = null;
          else
            stringValue = value.toString();
          query.setString(i + 1, stringValue);
        }
        query.execute();
        setCommitNeeded(true);
        resetChangedFlag();
        setInDatabase(true);
      }
      finally
      {
        query.close();
      }
    }
  }
  
  /**
   * Deletes the data for the instance of <CODE>RDBData</CODE> from the RDB.
   * 
   * @param connection The <CODE>Connection</CODE> used to connect to the RDB.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void delete(Connection connection) throws java.sql.SQLException
  {
    synchronized(changedRDBFields)
    {
      ArrayList primaryKeys = getPrimaryKeys(connection);
      int primaryKeyCount = primaryKeys.size();
      String[] queryFields = new String[primaryKeyCount];
      StringBuffer sql = new StringBuffer("DELETE FROM ");
      sql.append(buildTableReference(getTableName()));
      sql.append(" WHERE ");
      for(int i=0;i<primaryKeyCount;i++) 
      {
        queryFields[i] = primaryKeys.get(i).toString();
        if(i > 0)
          sql.append(" AND ");
        sql.append(queryFields[i]);
        sql.append(" = ?");
      }
      PreparedStatement query = connection.prepareStatement(sql.toString());
      try
      {
        for(int i=0;i<queryFields.length;i++) 
        {
          Object value = getValue(queryFields[i]);
          String stringValue;
          if(value == null)
            stringValue = null;
          else
            stringValue = value.toString();
          query.setString(i + 1, stringValue);
        }
        query.execute();
        setCommitNeeded(true);
        setInDatabase(false);
      }
      finally
      {
        query.close();
      }
    }
  }
  
  /**
   * Saves the changes to the <CODE>RDBData</CODE>.
   * 
   * @param connection The <CODE>Connection/CODE> to use to connect to the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void save(Connection connection) throws java.sql.SQLException
  {
    if(isInDatabase())
      update(connection);
    else
      insert(connection);
  }
  
  /**
   * Overiding this method to return hard coded values will improve performance.
   * By default the names are loaded from the RDB once.
   * 
   * @return The names of the RDB columns that are keys for the table.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  protected ArrayList getPrimaryKeys(Connection connection) throws java.sql.SQLException
  {
    synchronized(primaryKeys)
    {
      String tableName = getTableName();
      String tableReference = buildTableReference(tableName);
      ArrayList keys = (ArrayList)primaryKeys.get(tableReference);
      if(keys == null)
      {
        keys = new ArrayList();
        String schemaName = getSchemaName();
        ResultSet result = connection.getMetaData().getPrimaryKeys(null, schemaName, tableName);
        try
        {
          while(result.next())
            keys.add(result.getString("COLUMN_NAME"));
          primaryKeys.put(tableReference, keys);
        }
        finally
        {
          result.close();
        }
      }
      return keys;
    }
  }
  
  /**
   * Builds the <CODE>String</CODE> used to refer to the table in queries from
   * the schema and table names provided by the subclass.
   * 
   * @param tableName The name of the table for which to build a reference to a table.
   * @return The <CODE>String</CODE> used to refer to the table.
   */
  private String buildTableReference(String tableName)
  {
    String schemaName = getSchemaName();
    if(schemaName == null)
      return tableName;
    else
    {
      StringBuffer reference = new StringBuffer(schemaName);
      reference.append(".");
      reference.append(tableName);
      return reference.toString();
    }
  }
  
  /**
   * Sets the commit needed flag for the class.
   * 
   * @param commitNeeded Sets the value of the commit needed flag for the class.
   */
  protected void setCommitNeeded(boolean commitNeeded)
  {
    PropertyChangeEvent event;
    synchronized(this.commitNeeded)
    {
      Boolean oldValue = this.commitNeeded;
      this.commitNeeded = Boolean.valueOf(commitNeeded);
      event = new PropertyChangeEvent(this, "commitNeeded", oldValue, this.commitNeeded);
    }
    firePropertyChange(event);
  }

  /**
   * Returns <CODE>true</CODE> if there are pending RDB changes that need to be 
   * committed.
   * 
   * @return <CODE>true</CODE> if cahnges have been submitted to the RDB, <CODE>false</CODE> if not.
   */
  public boolean isCommitNeeded()
  {
    synchronized(this.commitNeeded)
    {
      return commitNeeded.booleanValue();
    }
  }
  
  /**
   * Clears the commit needed flag.
   */
  public void resetCommitNeededFlag()
  {
    setCommitNeeded(false);
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
  
  /**
   * This method finds and returns the names of the columns in the given 
   * <CODE>ResultSet</CODE>. The column names are returned in an array that is 
   * sorted so that it is ready to be used with the 
   * <CODE>Arrays.binarySearch</CODE> method.
   * 
   * @param result The <CODE>ResultSet</CODE> for which to find the names of the columns.
   * @return The names of the columns in the <CODE>ResultSet</CODE>.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public static String[] findColumnNames(ResultSet result) throws java.sql.SQLException
  {
    ResultSetMetaData metaData = result.getMetaData();
    String[] columnNames = new String[metaData.getColumnCount()];
    for(int i=0;i<columnNames.length;i++) 
      columnNames[i] = metaData.getColumnName(i + 1);
    Arrays.sort(columnNames);
    return columnNames;
  }
  
  /**
   * Checks to see if the given field has been flagged as changed.
   * 
   * @param fieldName The name of the field to change.
   * @return Returns <CODE>true</CODE> if the given field has been flagged as changed, <CODE>false</CODE> otherwise.
   */
  public boolean isFieldChanged(String fieldName)
  {
    return changedRDBFields.contains(fieldName);
  }
}