package org.csstudio.mps.sns.tools.database.swing;

import org.csstudio.mps.sns.tools.database.CachingDatabaseAdaptor;
import java.awt.*;

import java.math.*;

import java.sql.*;

import java.text.*;

import java.util.*;

import javax.sql.*;

import javax.swing.*;
import javax.swing.table.*;
import org.csstudio.mps.sns.tools.database.swing.DatabaseTableModel;

/**
 * Provides a generic model for editing a table in the database.
 * 
 * @author Chris Fowlkes
 */
public class DatabaseEditTableModel extends AbstractTableModel implements DatabaseTableModel
{
  /**
   * Holds the number of rows in the table. This is set when the data is loaded.
   */
  private int rowCount;
  /**
   * Holds the data in the row being edited. If no row is being edited this is 
   * set to <CODE>null</CODE>.
   */
  private Object[] editRowData;
  /**
   * Holds the row number of the row being edited.
   */
  private int editRowIndex = -1;
  /**
   * Holds the data in the row being inserted. If no row is geing inserted this 
   * is <CODE>null</CODE>.
   */
  private Object[] insertRowData;
  /**
   * Holds the database connection used by the model.
   */
  private Connection oracleConnection;
  /**
   * Holds the query used to retrieve the data contained in the model.
   */
  private PreparedStatement dataQuery;
  /**
   * Holds the query used to retrieve the number of rows contained in the model.
   */
  private PreparedStatement rowCountQuery;
  /**
   * Holds the data being displayed in the model.
   */
  private ResultSet result;
  /**
   * Holds the name of the database table being displayed.
   */
  private String tableName;
  /**
   * Holds the names of the columns in the database table being displayed. This 
   * is populated when the data is retrieved.
   */
  private ArrayList columnNames = new ArrayList();
  /**
   * Holds the filter for the table. The filter is a where clause appended to
   * the query used to retrieve the data from the database.
   */
  private String filter = "";
  /**
   * Holds the name of the primary keys of the database table being displayed. 
   * This is set when the key data is retrieved. The primary key is not 
   * editable.
   */
  private ArrayList primaryKeys = new ArrayList();
  /**
   * Holds the names of the foreign keys of the database table being displayed. 
   * This is set when the key data is retrieved. The primary key is editable on 
   * inserted rows if isn't is a foreign key.
   */
  private ArrayList foreignKeys = new ArrayList();
  /**
   * If set to <CODE>true</CODE>, primary key should always be editable. If 
   * <CODE>false</CODE>, primary key is only editable if it is a foreign key.
   * <CODE>false</CODE> by default.
   */
  private boolean primaryKeyEditable = false;
  /**
   * Flag that determines if any action has been taken that requires a commit.
   */
  private boolean commitNeeded;
  /**
   * Holds the <CODE>DatabaseAdaptor</CODE> for the application.
   */
  private CachingDatabaseAdaptor databaseAdaptor;
  /**
   * Indicates that the data is in read mode, or has not been edited, when 
   * returned by the <CODE>getMode()</CODE> method.
   */
  public static final int READ_MODE = 0;
  /**
   * Indicates that the data has been edited, when returned by the 
   * <CODE>getMode()</CODE> method.
   */
  public static final int EDIT_MODE = 1;
  /**
   * Indicates that the data has been inserted into the model, when returned by 
   * the <CODE>getMode()</CODE> method.
   */
  public static final int INSERT_MODE = 2;
  /**
   * Holds the fetch size used by the queries.
   */
  private int fetchSize = 80;
  /**
   * Flag that determines of the data is editable or not.
   */
  private boolean editable = true;
  /**
   * Holds the name of the table that the key data last loaded belongs to.
   */
  private String keyTableName;
  /**
   * Holds the names of the columns which have been removed from the view via
   * the <CODE>hideColumn</CODE> method.
   */
  private ArrayList hiddenColumnNames = new ArrayList();
  /**
   * Holds the name of the table that the column names in the 
   * <CODE>hiddenColumnNames ArrayList</CODE> belong to.
   */
  private String hiddenColumnTableName;
  /**
   * Holds the user's saved properties for the application.
   */
  private Properties applicationProperties;
  /**
   * Holds the schema in which the tables reside.
   */
  private String schema;
  /**
   * Holds the names of database columns that look like they should be 
   * checkboxes, but should be text fields.
   */
  private ArrayList exceptions = new ArrayList();
  /**
   * Variable that determines if the key field should be posted by the 
   * <CODE>postEditRow()</CODE> method. This is used if a key field change needs 
   * to be posted via a stored procedure. If that is the case then the stored
   * procedure call has to be implemented by a subclass. <CODE>true</CODE> by 
   * default.
   */
  private boolean postKeyField = true;
   
  /**
   * Creates a new <CODE>DVCTableModel</CODE>.
   */
  public DatabaseEditTableModel()
  {
  }

  /**
   * Gets the name of the given column. This method returns the name of the
   * column in the database.
   *
   * @param i The number of the column to return the name of.
   * @return The name of the column to display in the table header.
   */
  public final String getColumnName(int i) 
  {  
    return columnNames.get(i).toString();
  }

  /**
   * Gets the number of columns in the table. This is the number of columns in 
   * the database table being displayed.
   *
   * @return The number of columns in the table.
   */
  public int getColumnCount()
  {
    return columnNames.size();
  }

  /**
   * Gets the number of rows in the table. This is the number of rows in the 
   * database table being displayed, plus one if a row is being inserted.
   *
   * @return The number of rows in the table.
   */
  final public int getRowCount()
  { 
    if(isInsertRowVisible())
      return rowCount + 1;
    else
      return rowCount;
  }

  /**
   * Gets the value for a given cell. The value returned is the value in the 
   * result set at the given row and column unless the row is being edited. If 
   * the row is being edited or inserted, the value is retrieved from the 
   * appropriate variable.
   * 
   * @param row The row of the cell to get the value of.
   * @param col The column of the cell to get the value of.
   * @return The value in the given cell.
   */
  public Object getValueAt(int row, int col)
  {
    if(row < 0 || row >= getRowCount())
      throw new java.lang.IllegalArgumentException(row + " is not a valid row number.");
    if(col < 0 || col >= getColumnCount())
      throw new java.lang.IllegalArgumentException(col + " is not a valid column number.");
    Object value;
    if(isInsertRowVisible() && row == getRowCount() - 1)
      value = insertRowData[col];
    else
    {
      if(row == getEditRowIndex())
        value = editRowData[col];
      else
      {
        try
        {
          result.absolute(row + 1);
          Class columnType = getColumnClass(col);
          if(columnType.equals(java.sql.Date.class))
            value = result.getDate(getColumnName(col));
          else
            if(columnType.equals(Byte.class))
            {
              byte[] binaryData = result.getBytes(getColumnName(col));
              if(binaryData == null)
                value = null;
              else
                value = new BigInteger(binaryData);
            }
            else
              if(columnType.equals(Boolean.class))
              {
                String stringData = result.getString(getColumnName(col));
                if(stringData != null && stringData.equals("Y"))
                  value = Boolean.TRUE;
                else
                  value = Boolean.FALSE;
              }
              else
                value = result.getString(getColumnName(col));
        }
        catch(java.sql.SQLException ex)
        {
          ex.printStackTrace();
          value = ex.getMessage();
        }
      }
    }
    return value;
  }

  /**
   * Sets the <CODE>CachingDatabaseAdaptor</CODE> the model is to use to connect 
   * to the database. If the tablename property has already been set, this 
   * method connects to the database and retrieves the data.
   *
   * @param connectionPool The <CODE>CachingDatabaseAdaptor</CODE> to use to get connections to the database.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void setDatabaseAdaptor(CachingDatabaseAdaptor databaseAdaptor) throws java.sql.SQLException
  {
    this.databaseAdaptor = databaseAdaptor;
    if(oracleConnection != null)
      oracleConnection.close();
    if(databaseAdaptor == null)
      oracleConnection =  null;
    else
    {
      oracleConnection = databaseAdaptor.getConnection();
      oracleConnection.setAutoCommit(false);
      String tableName = getTableName();
      if(tableName != null && ! tableName.trim().equals(""))
        setupDataQuery();
    }
  }

  /**
   * Gets the <CODE>DatabaseAdaptor</CODE> used by the application to obtain 
   * database connections.
   *
   * @return The <CODE>DataSource</CODE> for the application.
   */
  public CachingDatabaseAdaptor getDatabaseAdaptor()
  {
    return databaseAdaptor;
  }

  /**
   * Sets up the query that retrieves the data.
   *
   * throws java.sql.SQLException Thrown on sql error.
   */
  private void setupDataQuery() throws java.sql.SQLException
  {
    String table = getTableName();
    StringBuffer sql = new StringBuffer("SELECT ");
    String schema = getSchema();
    if(schema != null)
    {
      sql.append(schema);
      sql.append(".");
    }
    sql.append(table);
    sql.append(".* FROM ");
    if(schema != null)
    {
      sql.append(schema);
      sql.append(".");
    }
    sql.append(table);
    String whereClause = getFilter();
    if(! (whereClause == null || whereClause.trim().equals("")))
    {
      sql.append(" WHERE ");
      sql.append(whereClause);
    }
    dataQuery = oracleConnection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
    dataQuery.setFetchSize(fetchSize);
    sql = new StringBuffer("SELECT COUNT(*) FROM ");
    if(schema != null)
    {
      sql.append(schema);
      sql.append(".");
    }
    sql.append(table);
    if(! (whereClause == null || whereClause.trim().equals("")))
    {
      sql.append(" WHERE ");
      sql.append(whereClause);
    }
    rowCountQuery = oracleConnection.prepareStatement(sql.toString());
  }

  /**
   * Sets the name of the database table to be displayed. This method does not 
   * reload the data for the table.
   *
   * @param tableName The name of the database table to display the data from.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void setTableName(String tableName) throws java.sql.SQLException
  {
    this.tableName = tableName;
    if(! (tableName == null || tableName.trim().equals("") || oracleConnection == null))
    {
      setupDataQuery();
      setFilter(null);
    }
  }

  /**
   * Gets the name of the database table used by the model.
   *
   * @return The name of the database table in the model.
   */
  public String getTableName()
  {
    return tableName;
  }

  /**
   * Determines if the given cell should be editable or not. All cells except 
   * those in the primary key column of the database table are editable.
   *
   * @param row The row number of the cell to check.
   * @param col The column number of the cell to check.
   * @return <CODE>false</CODE> if the column is the primary key of the database table, <CODE>true</CODE> otherwise.
   */   
  public boolean isCellEditable(int row, int col) 
  {
    boolean cellEditable;
    if(! isEditable())
      cellEditable = false;
    else
    {
      String columnName = getColumnName(col);
      if(getPrimaryKeys().contains(columnName))
        if(isInsertRowVisible() && row == getRowCount() - 1)
          if(getForeignKeys().contains(columnName))//Dealing with insert row, check for FK.
            cellEditable = true;//Primary key editable in insert row if foreign key.
          else
            cellEditable = false;
        else
          cellEditable = isPrimaryKeyEditable();//Property does not apply to insert row.
      else 
        cellEditable = true;//Fields that are not primary key always editable.
    }
    return cellEditable;
  }

  /**
   * Sets the value at the given cell. This method sets the value at the given 
   * cell to the value given. It does not commit or post the value to the
   * database.
   *
   * @param aValue The value to set at the given cell.
   * @param rowIndex The row number of the cell to set the value of.
   * @param columnIndex The column number of the cell to set the value of.
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    if(rowIndex < 0 || rowIndex >= getRowCount())
      throw new java.lang.IllegalArgumentException(rowIndex + " is not a valid row number.");
    if(columnIndex < 0 || columnIndex >= getColumnCount())
      throw new java.lang.IllegalArgumentException(columnIndex + " is not a valid column number.");
    if(! compare(aValue, getValueAt(rowIndex, columnIndex)))//Make sure value changed.
    {
      boolean insertVisible = isInsertRowVisible();
      if(insertVisible && rowIndex == getRowCount() - 1)
        updateArrayValue(columnIndex, aValue, insertRowData);
      else
      {
        try
        {
          if(insertVisible)
          {
            if(isChanged())
              post();
            insertRowData = null;//Should not be in insert mode if editing another row.  
          }
          if(rowIndex != getEditRowIndex())
          {
            if(isChanged())
              post();
            //Load the current values for the row being edited.
            reloadEditRow(rowIndex);
            editRowIndex = rowIndex;
          }
        }
        catch(java.sql.SQLException ex)
        {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(null, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
        //Set the value in the edit row.
        updateArrayValue(columnIndex, aValue, editRowData);
      }
      fireTableCellUpdated(rowIndex, columnIndex);
    }
  }

  /**
   * Compares two objects with the <CODE>equals</CODE> method. This method also 
   * considers two <CODE>null</CODE> objects equal as well.
   * 
   * @param object1 The first <CODE>Object</CODE> to compare.
   * @param object2 The <CODE>Object</CODE> to compare to.
   * @return <CODE>true</CODE> if the two items are equal.
   */
  private boolean compare(Object object1, Object object2)
  {
    //Sometimes if a null is passed in as a string, we end up here...
    if((object1 == null || object2 == null) && (object1 instanceof String || object2 instanceof String))
      return compare((String)object1, (String)object2);
    if(object1 == null)
    {
      if(object2 != null)
        return false;
    }
    else
      if(! object1.equals(object2))
        return false;
    return true;
  }

  /**
   * Compares two instances of <CODE>String</CODE>. This method considers 
   * <CODE>null</CODE> and empty string to be equal and does not consider 
   * leading or trailing whitespace.
   * 
   * @param string1 The first <CODE>String</CODE> to compare.
   * @param string2 The <CODE>String</CODE> to compare to.
   * @return <CODE>true</CODE> if the two items are equal.
   */
  private boolean compare(String string1, String string2)
  {
    if(string1 == null)
      string1 = "";
    if(string2 == null)
      string2 = "";
    return string1.trim().equals(string2.trim());
  }

  /**
   * Takes the value in the given array at the given position and inserts it
   * into the result set. This method does not change the row of the result set. 
   * That must be done before invoking this method.
   * 
   * @param columnIndex The index of the column to change.
   * @param data
   * @throws java.sql.SQLException
   */
  private void updateResultSetValue(int columnIndex, Object[] data) throws java.sql.SQLException
  {
    Class columnType = getColumnClass(columnIndex);
    if(columnType.equals(java.sql.Date.class))
      result.updateDate(getColumnName(columnIndex), (java.sql.Date)data[columnIndex]);
    else
      if(columnType.equals(Byte.class))
        if(data[columnIndex] == null)
          result.updateBytes(getColumnName(columnIndex), null);
        else
          result.updateBytes(getColumnName(columnIndex), ((BigInteger)data[columnIndex]).toByteArray());
      else
        if(columnType.equals(Boolean.class))
          if(data[columnIndex] != null && ((Boolean)data[columnIndex]).booleanValue())
            result.updateString(getColumnName(columnIndex), "Y");
          else
            result.updateString(getColumnName(columnIndex), "N");
        else
          if(data[columnIndex] == null)
            result.updateString(getColumnName(columnIndex), null);
          else
          {
            String stringValue = data[columnIndex].toString();
            if(stringValue.length() == 0)
              result.updateString(getColumnName(columnIndex), null);
            else
              result.updateString(getColumnName(columnIndex), stringValue);
          }
  }
  
  /**
   * Sets the value for a cell. This method works with either the insert or edit
   * row.
   * 
   * @param columnIndex The index of the column of which to set the value.
   * @param newValue The value for the cell.
   * @param rowData The row of which to set the value. Can be either <CODE>editRowData</CODE> or <CODE>insertRowData</CODE>.
   */
  private void updateArrayValue(int columnIndex, Object newValue, Object[] rowData)
  {
    Class columnType = getColumnClass(columnIndex);
    if(columnType.equals(java.sql.Date.class))
    {
      java.sql.Date newDateValue = convertDate(newValue.toString());
      if(newDateValue == null)
        JOptionPane.showMessageDialog(null, "Invalid date format. Enter the date as 'MM-dd-yyyy HH:mm:ss'.", "Invalid Date Format", JOptionPane.ERROR_MESSAGE);
      else
        rowData[columnIndex] = newDateValue;
    }
    else
      if(columnType.equals(Byte.class))
        rowData[columnIndex] = new BigInteger(newValue.toString());
      else
        if(columnType.equals(Boolean.class))
          rowData[columnIndex] = newValue;
        else
          rowData[columnIndex] = newValue.toString();
  }
  
  /**
   * Copies the values in the given row of the table into the edit row.
   * 
   * @param rowIndex The index of the row in the table to copy into the edit row array.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  private void reloadEditRow(int rowIndex) throws java.sql.SQLException
  {
    editRowData = new Object[getColumnCount()];
    result.absolute(rowIndex + 1);
    for(int column=0;column<editRowData.length;column++)
    {
      Class columnType = getColumnClass(column);
      if(columnType.equals(java.sql.Date.class))
        editRowData[column] = result.getDate(getColumnName(column));
      else
        if(columnType.equals(Byte.class))
        {
          byte[] binaryValue = result.getBytes(getColumnName(column));
          if(binaryValue == null)
            editRowData[column] = null;
          else
            editRowData[column] = new BigInteger(binaryValue);
        }
        else
          if(columnType.equals(Boolean.class))
          {
            String currentValue = result.getString(getColumnName(column));
            if(currentValue != null && currentValue.equals("Y"))
              editRowData[column] = Boolean.TRUE;
            else
              editRowData[column] = Boolean.FALSE;
          }
          else
            editRowData[column] = result.getString(getColumnName(column));
    }
  }
  
  /**
   * Called when the post button is clicked. This method posts all local changes 
   * that have been made.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void post() throws java.sql.SQLException
  {
    if(getMode() == INSERT_MODE)
      postInsertRow();
    else
      postEditRow();
  }

  /**
   * Posts the row being edited to the database. This method does not commit the 
   * changes.
   *
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void postEditRow() throws java.sql.SQLException
  {
    if(isEditRowChanged())
    {
      result.absolute(editRowIndex + 1);
      int columnCount = getColumnCount();
      boolean postPrimaryKey = isPostKeyField();
      ArrayList keyFields = getPrimaryKeys(), foreignKeys = getForeignKeys();
      for(int column=0;column<columnCount;column++)
        if(isCellEditable(editRowIndex, column))
        {
          String columnName = getColumnName(column);
          if((! postPrimaryKey) && keyFields.contains(columnName) && (! foreignKeys.contains(columnName)))
            continue;//Skip primary keys if post key field property is false.
          updateResultSetValue(column, editRowData);
        }
      result.updateRow();
      commitNeeded = true;
    }
    editRowData = null;
    editRowIndex = -1;
  }

  /**
   * Compares to values for equality. This method returns <CODE>true</CODE> if 
   * both values are <CODE>null</CODE> or determined to be equal with the 
   * <CODE>equals()</CODE> method of the <CODE>Object</CODE>.
   *
   * @param value1 One of the values to compare.
   * @param value2 One of the values to compare.
   * @return <CODE>true</CODE> if the values or equal, <CODE>false</CODE> if they are not equal.
   */
  private boolean areValuesEqual(Object value1, Object value2)
  {
    if(value1 == null)
      return value2 == null;
    else
      return value1.equals(value2);
  }

  /**
   * Called when the cancel toolbar button is clicked. This method cancels any 
   * local changes that have been made to the data.
   */
  public void cancel()
  {
    if(getMode() == INSERT_MODE)
      cancelInsertRow();
    else
      cancelEditRow();
  }

  /**
   * Cancels the changes made to the edit row since the last post.
   */
  public void cancelEditRow()
  {
    editRowData = null;
    fireTableRowsUpdated(editRowIndex, editRowIndex);
    editRowIndex = -1;
  }

  /**
   * Cancels the changes to the insert row.
   */
  public void cancelInsertRow()
  {
    if(isInsertRowVisible())
    {
      insertRowData = null;
      //fireTableRowsDeleted(rowNumber, rowNumber);
      fireTableDataChanged();//Much faster than row deleted. Who knows why...
    }
  }

  /**
   * Refreshes the data in the model. This method is called automatically when 
   * the data source and table name properties are both set, but can be used to
   * reload the data at other times.
   *
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void refresh() throws java.sql.SQLException
  {
    if(keyTableName == null || ! keyTableName.equals(getTableName()))
      refreshPrimaryKeyData();//Key data needs to be reloaded.
    result = dataQuery.executeQuery();
    if(hiddenColumnTableName == null || ! hiddenColumnTableName.equals(getTableName()))
      loadVisibleColumns();
    ResultSet rowCountResult = rowCountQuery.executeQuery();
    try
    {
      if(rowCountResult.next())
        rowCount = rowCountResult.getInt(1);
      else
        rowCount = 0;
    }
    finally
    {
      rowCountResult.close();
    }
    editRowData = null;
    editRowIndex = -1;
    insertRowData = null;
    fireTableStructureChanged();
  }

  /**
   * Loads the primary key information for the table.
   *
   * @throws java.sql.SQLException Thrown on sql error.
   */
  private void refreshPrimaryKeyData() throws java.sql.SQLException
  {
    //Get the keys for the table.
    foreignKeys.clear();
    primaryKeys.clear();
    DatabaseMetaData metaData = oracleConnection.getMetaData();
    ResultSet keys = metaData.getPrimaryKeys(null, getSchema(), keyTableName);
    while(keys.next())
      primaryKeys.add(keys.getString("COLUMN_NAME"));
    keys = metaData.getImportedKeys(null, getSchema(), keyTableName);
    while(keys.next())
      foreignKeys.add(keys.getString("FKCOLUMN_NAME"));
    keys.close();
    keyTableName = getTableName();
  }

  /**
   * Returns a <CODE>ArrayList</CODE> containing the names of the foreign keys in 
   * the table.
   * 
   * @return The names of the columns that are foreign keys.
   */
  final public ArrayList getForeignKeys()
  {
    return foreignKeys;
  }

  /**
   * Returns a <CODE>ArrayList</CODE> containing the names of the primary keys of 
   * the table.
   * 
   * @return The names of the columns that are foreign keys.
   */
  public ArrayList getPrimaryKeys()
  {
    return primaryKeys;
  }
  
  /**
   * Determines if changes have been made to the edit row. This is done by 
   * comparing the values in the edit row to thoser in the result set.
   *
   * @return <CODE>true</CODE> if changes have been made to the edit row, <CODE>false</CODE> otherwise.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public boolean isEditRowChanged() throws java.sql.SQLException
  {
    boolean changed = false;
    if(isEditing())
    {
      int columnCount = getColumnCount();
      result.absolute(editRowIndex + 1);
      for(int column=0;column<columnCount;column++)
      {
        Class columnType = getColumnClass(column);
        if(columnType.equals(java.sql.Date.class))
          changed = ! areValuesEqual(editRowData[column], result.getDate(getColumnName(column)));
        else
          if(columnType.equals(Byte.class))
          {
            byte[] binaryValue = result.getBytes(getColumnName(column));
            if(binaryValue == null)
              changed = editRowData[column] != null;
            else
              changed = ! areValuesEqual(editRowData[column], new BigInteger(binaryValue));
          }
          else
            if(columnType.equals(Boolean.class))
            {
              String stringValue = result.getString(getColumnName(column));
              if(stringValue != null && stringValue.equals("Y"))//Should be true. Changed if false
                changed = ! ((Boolean)editRowData[column]).booleanValue();
              else
                changed = ((Boolean)editRowData[column]).booleanValue();
            }
            else
              changed = ! areValuesEqual(editRowData[column], result.getString(getColumnName(column)));
        if(changed)
          break;//Atleast one value changed, no need to continue.
      }
    }
    return changed;
  }

  /**
   * Checks to see if the data has been changed locally.
   * 
   * @return <CODE>true</CODE> if the data has been changed locally, <CODE>false</CODE> if not.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public boolean isChanged() throws java.sql.SQLException
  {
    return isEditRowChanged() || isInsertRowChanged();
  }

  /**
   * Determines if the insert row has been changed. This method checks the
   * inset row to see if any values have been set.
   *
   * @return <CODE>true</CODE> if the insert row contains any values that are not <CODE>null</CODE>, <CODE>false</CODE> if it contains all <CODE>null</CODE> values.
   */
  public boolean isInsertRowChanged()
  {
    boolean changed = false;
    if(isInsertRowVisible())
      for(int column=0;column<insertRowData.length;column++)
        if(insertRowData[column] != null)
        {
          changed = true;
          break;
        }
    return changed;
  }

  /**
   * Adds the empty insert row visible to the table.
   */
  public void insert()
  {
    insert(new Object[getColumnCount()]);
  }

  /**
   * Inserts a new row to the table. The row is inistialized with the given 
   * data.
   * 
   * @param newInsertRowData The values with which to initialize the columns in the new row.
   */
  public void insert(Object[] newInsertRowData)
  {
    insertRowData = newInsertRowData;
    int rowCount = getRowCount();
    fireTableRowsInserted(rowCount - 1, rowCount - 1);
  }

  /**
   * Deletes the given rows from the database. This method deletes the rows, but
   * does not do a commit.
   *
   * @param rowNumbers The row numbers of the rows to delete.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void delete(int[] rowNumbers) throws java.sql.SQLException
  {
    Arrays.sort(rowNumbers);
    for(int i=rowNumbers.length-1;i>=0;i--)
    {
      if(rowNumbers[i] == rowCount && isInsertRowVisible())
        insertRowData = null;//Deleted the insert row. Take care of that first.
      else
      {
        result.absolute(rowNumbers[i] + 1);
        result.deleteRow();
        rowCount--;
        commitNeeded = true;
      }
    }
    fireTableDataChanged();//Much faster than row deleted. Who knows why...
  }

  /**
   * Posts the changes to the insert row. This method posts the changes made to
   * the insert row but does not commit them.
   *
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void postInsertRow() throws java.sql.SQLException
  {
    if(isInsertRowChanged())
    {
      result.moveToInsertRow();
      int columnCount = getColumnCount();
      int insertRow = getRowCount() - 1;
      for(int column=0;column<columnCount;column++)
        if(isCellEditable(insertRow, column))
          updateResultSetValue(column, insertRowData);
      result.insertRow();
      rowCount++;
      commitNeeded = true;
      refresh();
    }
  }

  /**
   * Determines if the insert row is visible or not. The insert row is added
   * to the model with the <CODE>insertRow</CODE> method .
   *
   * @return <CODE>true</CODE> if the insert row is part of the model, <CODE>false</CODE> if not.
   */
  public boolean isInsertRowVisible()
  {
    return insertRowData != null;
  }

  /**
   * Sets a filter for the data query. This method refreshes the data if the 
   * data source and table name properties have been set.
   *
   * @param filter A where clause for the data query to use.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void setFilter(String filter) throws java.sql.SQLException
  {
    if(filter == null)
      this.filter = "";
    else
      this.filter = filter;
    String table = getTableName();
    if(! (table == null || table.trim().equals("") || oracleConnection == null))
      setupDataQuery();
  }

  /**
   * Called by the super class to determine if the data is filtered or not.
   * 
   * @return <CODE>true</CODE> if the data in the model is filtered, <CODE>false</CODE> if not.
   */
  public boolean isFiltered()
  {
    String filter = getFilter();
    return ! (filter == null || filter.trim().equals(""));
  }

  /**
   * Gets the filter being used by the data query. 
   *
   * @return The where clause for the data query to use.
   */
  public String getFilter()
  {
    return filter;
  }

  /**
   * Commits all of the current changes to the database. This does not include 
   * unposted  edits to the edit and insert rows.
   *
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void commit() throws java.sql.SQLException
  {
    if(isInsertRowChanged())
      postInsertRow();
    else
      if(isEditRowChanged())
        postEditRow();
    oracleConnection.commit();
    commitNeeded = false;
  }

  /**
   * Does a rollback on the current changes to the database. This does not 
   * include unposted edits to the edit and insert rows. This method does not
   * reload the data.
   *
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void rollback() throws java.sql.SQLException
  {
    oracleConnection.rollback();
    commitNeeded = false;
  }

  /**
   * Makes the primary key editable or read only. If set to <CODE>true</CODE>, 
   * the primary key field is always editable. If set to <CODE>false</CODE>, 
   * the primary key is only editable if it is a foreign key and only for the 
   * insert row.
   * 
   * @param primaryKeyEditable Pass as <CODE>true</CODE> to force the primary key to always be editable.
   */
  public void setPrimaryKeyEditable(boolean primaryKeyEditable)
  {
    this.primaryKeyEditable = primaryKeyEditable;
  }

  /**
   * Determines if the primary key is always editable.
   *
   * @return <CODE>true</CODE> if the primary key is always editable, <CODE>false</CODE> if it is only editable for foreign keys in the insert row.
   */
  public boolean isPrimaryKeyEditable()
  {
    return primaryKeyEditable;
  }

  /**
   * Returns the row being edited. If no row is being edited, -1 is returned.
   *
   * @return The row being edited, or -1 if not in edit mode.
   */
  public int getEditRowIndex()
  {
    if(isEditing())
      return editRowIndex;
    else
      return -1;
  }

  /**
   * Used to see if changes have been made but not committed or rolledback.
   *
   * @return <CODE>true</CODE> if changes are pending, <CODE>false</CODE> if not.
   * @throws java.sql.SQLException Thrown on error.
   */
  public boolean isCommitNeeded() throws java.sql.SQLException
  {
    return commitNeeded || isInsertRowChanged() || isEditRowChanged();
  }

  /**
   * Used to determine if a row in the table is being edited.
   *
   * @return <CODE>true</CODE> if a row is being edited, <CODE>false</CODE> otherwise.
   */
  public boolean isEditing()
  {
    if(editRowData == null)
      return false;
    else
      return true;
  }

  /**
   * Closes the database connection.
   *
   * @throws java.sql.SQLException Thrown on database error.
   */
  public void closeConnection() throws java.sql.SQLException
  {
    oracleConnection.close();
  }

  /**
   * Gets the mode the data model is in. This is 
   * <CODE>DatabaseEditModel.INSERT_MODE</CODE>, 
   * <CODE>DatabaseEditModel.EDIT_MODE</CODE>, or 
   * <CODE>DatabaseEditModel.READ_MODE</CODE>, depending on the state of the 
   * data.
   *
   * @return Returns the mode of the data model.
   */
  public int getMode()
  {
    if(isInsertRowVisible())
      return org.csstudio.mps.sns.tools.database.swing.DatabaseEditTableModel.INSERT_MODE;
    else
      if(editRowData != null)
        return org.csstudio.mps.sns.tools.database.swing.DatabaseEditTableModel.EDIT_MODE;
      else
        return org.csstudio.mps.sns.tools.database.swing.DatabaseEditTableModel.READ_MODE;
  }

  /**
   * Determines if the data should be editable or not.
   * 
   * @param editable Pass as <CODE>false</CODE> to make read only, <CODE>true</CODE> by default.
   */
  public void setEditable(boolean editable)
  {
    this.editable = editable;
  }

  /**
   * Determines if the data should be editable or not.
   * 
   * @return Pass as <CODE>false</CODE> to make read only, <CODE>true</CODE> by default.
   */
  public boolean isEditable()
  {
    return editable;
  }

  /**
   * Converts the given <CODE>String</CODE> to a <CODE>Date</CODE>. This method 
   * uses the following formats to convert the <CODE>String</CODE> to a 
   * <CODE>Date</CODE>:
   * 
   * <UL>
   * <LI>yyyy-MM-dd HH:mm:ss (Database format)
   * <LI>MM-dd-yyyy HH:mm:ss (Display format)
   * <LI>MM-dd-yyyy (User Input Format)
   * </UL>
   * 
   * @param dateString The <CODE>String</CODE> to convert to a <CODE>Date</CODE>.
   * @return The <CODE>Date</CODE> representation of the <CODE>String</CODE>.
   */
  private java.sql.Date convertDate(String dateString)
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//Database format.
    java.util.Date parsedDate;
    try
    {
      parsedDate = format.parse(dateString);
    }
    catch(java.text.ParseException e)
    {
      format.applyPattern("MM-dd-yyyy HH:mm:ss");//Display format
      try
      {
        parsedDate = format.parse(dateString);
      }
      catch(java.text.ParseException ex)
      {
        format.applyPattern("MM-dd-yyyy");//User format
        try
        {
          parsedDate = format.parse(dateString);
        }
        catch(java.text.ParseException exc)
        {
          parsedDate = null;//Giving up!
        }
      }
    }
    if(parsedDate == null)
      return null;
    else
      return new java.sql.Date(parsedDate.getTime());
  }

  /**
   * Gets the type of data represented by the column. 
   * 
   * @param columnIndex The index of the column to return the <CODE>Class</CODE> for.
   * @return The <CODE>Class</CODE> of the column represented.
   */
  public Class getColumnClass(int columnIndex)
  {
    try
    {
      ResultSetMetaData metaData = result.getMetaData();
      String columnName = getColumnName(columnIndex);
      int resultSetColumnCount = metaData.getColumnCount();
      //Need to get column index by comparing column name. Could be hidden columns.
      int databaseColumnIndex = columnIndex + 1;
      for(int i=1;i<=resultSetColumnCount;i++)
        if(metaData.getColumnName(i).equals(columnName))
        {
          databaseColumnIndex = i;
          break;
        }
      int columnType = metaData.getColumnType(databaseColumnIndex);
      if(columnType == Types.TIMESTAMP || columnType == Types.DATE)
        return java.sql.Date.class;
      else
        if(columnType == Types.VARBINARY)
          return Byte.class;
        else
        {
          if(columnType == Types.CHAR && metaData.getPrecision(databaseColumnIndex) == 1 && columnName.endsWith("_IND") && ! exceptions.contains(columnName))
            return Boolean.class;
          else
            return super.getColumnClass(columnIndex);
        }
    }
    catch(java.sql.SQLException e)
    {
      e.printStackTrace();
      return super.getColumnClass(columnIndex);
    }
  }

  /**
   * Gets the names of all of the columns in the database table being viewed.
   * 
   * @return An <CODE>ArrayList</CODE> containing the names of the columns being displayed.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public ArrayList getAvailableColumns() throws java.sql.SQLException
  {
    ResultSetMetaData metaData = result.getMetaData();
    int columnCount = metaData.getColumnCount();
    ArrayList columns = new ArrayList(columnCount);
    for(int i=0;i<columnCount;i++)
      columns.add(metaData.getColumnName(i));
    return columns;
  }

  /**
   * Gets the names of the columns that are visible.
   * 
   * @return An <CODE>ArrayList</CODE> containing the names of the visible columns.
   */
  public ArrayList getVisibleColumns()
  {
    return columnNames;
  }

  /**
   * Shows only the given columns in the view. If the data has been changed this 
   * method posts those changes to the result set to avoid conflicts with the
   * column numbering scheme. This method does not throw an exception if a given
   * column name is not in the result set or is already hidden, it just ignores 
   * it.
   * 
   * @param columnNamesToShow The names of the columns to show.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public final void setVisibleColumns(ArrayList columnNamesToShow) throws java.sql.SQLException
  {
    if(isInsertRowChanged())
      postInsertRow();
    else
      if(isEditRowChanged())
        postEditRow();
    showColumns(columnNamesToShow);
    saveVisibleColumns();
  }

  /**
   * Shows only the given columns in the view. This method does not check for
   * changes to the current data and does not save the columns in the property 
   * file.
   * 
   * @param columnNamesToShow The names of the columns to show.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  private void showColumns(ArrayList columnNamesToShow) throws java.sql.SQLException
  {
    hiddenColumnNames.clear();
    columnNames.clear();
    hiddenColumnTableName = getTableName();
    ResultSetMetaData metaData = result.getMetaData();
    int columnCount = metaData.getColumnCount();
    for(int i=1;i<=columnCount;i++)
    {
      String currentColumnName = metaData.getColumnName(i);
      if(columnNamesToShow.contains(currentColumnName))
        columnNames.add(currentColumnName);
      else
        hiddenColumnNames.add(currentColumnName);
    }
    fireTableStructureChanged();
  }

  /**
   * Gets the names of the columns in the table that are not visible.
   * 
   * @return The names of the columns that have been hidden.
   */
  public ArrayList getHiddenColumns()
  {
    return hiddenColumnNames;
  }

  /**
   * Loads the visible columns last saved for the current table from the 
   * property file.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  private void loadVisibleColumns() throws java.sql.SQLException
  {
    Properties settings = getApplicationProperties();
    ArrayList selectedColumns = new ArrayList();
    if(settings != null)
    {
      String currentTableName = getTableName();
      String columnCountProperty = settings.getProperty("DBEditModel." + currentTableName + ".ColumnCount");
      if(columnCountProperty != null)
      {
        int columnCount = Integer.parseInt(columnCountProperty);
        for(int i=0;i<columnCount;i++)
        {
          String columnName = applicationProperties.getProperty("DBEditModel." + currentTableName + ".Column" + i);
          if(columnName != null)
            selectedColumns.add(columnName);
        }
      }
    }
    if(selectedColumns.size() <= 0)
    {
      //Need to load all columns by default.      
      ResultSetMetaData metaData = result.getMetaData();
      int columnCount = metaData.getColumnCount();
      for(int i=1;i<=columnCount;i++)
        selectedColumns.add(metaData.getColumnName(i));
    }
    showColumns(selectedColumns);
  }

  /**
   * Saves the visible columns in the property file. This method does not save
   * anything if the application properties property has not been set or if no
   * columns are visible.
   */
  private void saveVisibleColumns()
  {
    Properties settings = getApplicationProperties();
    if(settings != null)
    {
      int columnCount = columnNames.size();
      if(columnCount > 0)
      {
        String currentTableName = getTableName();
        String columnCountProperty = String.valueOf(columnCount);
        settings.setProperty("DBEditModel." + currentTableName + ".ColumnCount", columnCountProperty);
        for(int i=0;i<columnCount;i++)
        {
          String currentColumnName = columnNames.get(i).toString();
          applicationProperties.setProperty("DBEditModel." + currentTableName + ".Column" + i, currentColumnName);
        }
      }
    }
  }
  
  /**
   * Sets the properties stored in the applications properties file. These are 
   * read in before the user logs in by the main application class and passed to 
   * this class as an instance of <CODE>Properties</CODE>. This method also 
   * restores the size of the window to the sizestored in the 
   * <CODE>Properties</CODE>.
   * 
   * @param applicationProperties The applicationProperties application settings.
   */
  public void setApplicationProperties(Properties applicationProperties)
  {
    this.applicationProperties = applicationProperties;
  }

  /**
   * Gets the properties stored in the applications properties file.
   *
   * @return The settings for the application.
   */
  public Properties getApplicationProperties()
  {
    return applicationProperties;
  }

  /**
   * Provided so that subclasses can use the transaction to do data work.
   * 
   * @return The <CODE>Connection</CODE> used to connect to the database.
   */
  final protected Connection getConnection()
  {
    return oracleConnection;
  }

  /**
   * Allows subclasses to set the commit needed flag.
   * 
   * @param commitNeeded Pass as <CODE>true</CODE> to set the flag as data changed.
   */
  final protected void setCommitNeeded(boolean commitNeeded)
  {
    this.commitNeeded = commitNeeded;
  }

  /**
   * Returns the value of the given field. This method can be used to retrieve
   * the value of any field, visible or not, in the table being edited.
   * 
   * @param row The <CODE>JTable</CODE> row index of the field to return the value of.
   * @param columnName The name of the field to return the value of.
   * @return The value of the given field.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public String getStringValue(int row, String columnName) throws java.sql.SQLException
  {
    result.absolute(row + 1);
    return result.getString(columnName);
  }

  /**
   * Checks to see if the current database table contains the given column name.
   * 
   * @param fieldName The name of the field to check the database table for.
   * @return <CODE>true</CODE> if the database table contains a field with the given name, <CODE>false</CODE> if not.
   */
  public boolean tableContainsField(String fieldName)
  {
    if(result == null)
      return false;
    try
    {
      result.findColumn(fieldName);
      return true;
    }
    catch(java.sql.SQLException ex)
    {
      //Does not contain column.
      return false;
    }
  }

  /**
   * Finds the first row in the table that has the given value for the given 
   * database field. The database field does not have to be visible in the
   * <CODE>JTable</CODE>, but it does have to exist in the database table.
   * 
   * @param fieldName The name of the database field to search the value of.
   * @param value The value to find in the given database field.
   * @return The <CODE>JTable</CODE> row index of the first row with the given value for the given field, or -1 if no match is found.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public int findFieldValue(String fieldName, String value) throws java.sql.SQLException
  {
    result.beforeFirst();
    while(result.next())
    {
      if(compare(value, result.getString(fieldName)))
        return result.getRow() - 1;
    }
    return -1;
  }

  /**
   * Forces the model to use tables in the given schema instead of the schema
   * defined by <CODE>Jeri.SCHEMA</CODE>.
   * 
   * @param schema The schema to which the database table belongs. Defaults to <CODE>Jeri.SCHEMA</CODE>.
   */
  final public void setSchema(String schema)
  {
    this.schema = schema;
  }

  /**
   * Returns the schema used in the data queries.
   * 
   * @return The schema used.
   */
  final public String getSchema()
  {
    return schema;
  }

  /**
   * Makes sure a column will not have a checkbox editor. By default only one
   * character text fields that end with "_IND" will be checkboxes. To force a
   * field that falls into this criteria to be a text field, pass the column
   * name to this method.
   * 
   * @param columnName The name of the indicator column that is to be a text field instead of a checkbox.
   */
  final public void addNoCheckBoxField(String columnName)
  {
    exceptions.add(columnName);
  }

  /**
   * Determines if the key field should be posted by the 
   * <CODE>postEditRow()</CODE> method. This is used if a key field change needs 
   * to be posted via a stored procedure. If that is the case then the stored
   * procedure call has to be implemented by a subclass. <CODE>true</CODE> by 
   * default.
   * 
   * @return <CODE>true</CODE> if the key field changes are to be posted by this class, <CODE>false</CODE> if they are handled by a subclass.
   */
  public boolean isPostKeyField()
  {
    return postKeyField;
  }

  /**
   * Determines if the key field should be posted by the 
   * <CODE>postEditRow()</CODE> method. This is used if a key field change needs 
   * to be posted via a stored procedure. If that is the case then the stored
   * procedure call has to be implemented by a subclass. <CODE>true</CODE> by 
   * default.
   * 
   * @param newPostKeyField Pass as <CODE>true</CODE> if the key field changes are to be posted by this class, <CODE>false</CODE> if they are handled by a subclass.
   */
  public void setPostKeyField(boolean newPostKeyField)
  {
    postKeyField = newPostKeyField;
  }
}