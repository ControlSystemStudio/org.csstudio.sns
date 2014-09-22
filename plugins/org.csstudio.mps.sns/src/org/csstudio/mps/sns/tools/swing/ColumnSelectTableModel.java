package org.csstudio.mps.sns.tools.swing;
import javax.swing.table.*;
import java.util.*;

/**
 * Provides a model for the tables in the <CODE>ColumnSelectDialog</CODE>. The 
 * <CODE>ColumnSelectDialog</CODE> is used to select the fields visible in the 
 * <CODE>SignalTableBrowserFrame</CODE>.
 * 
 * @author Chris Fowlkes
 */
public class ColumnSelectTableModel extends AbstractTableModel
{
  /**
   * Holds the objects that represent the columns in the table.
   */
  private ArrayList columns = new ArrayList();
  
  /**
   * Creates a new <CODE>ColumnSelectTableModel</CODE>.
   */
  public ColumnSelectTableModel()
  {
  }

  /**
   * Determines if the cell is editable. This model is not editable so this
   * method always returns <CODE>false</CODE>.
   * 
   * @param rowIndex The row index of the cell in question.
   * @param columnIndex The column index of the cell in question.
   * @return <CODE>false</CODE>.
   */
  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return false;
  }

  /**
   * Returns the number of rows in the model.
   * 
   * @return The number of rows of data in the model.
   */
  public int getRowCount()
  {
    return columns.size();
  }

  /**
   * Gets the data for the column represented at the given row in the table.
   * 
   * @param row The row of the column data to return.
   * @return The data represented at the given row.
   */
  public Object getColumnAtRow(int row)
  {
    return columns.get(row);
  }
  
  /**
   * Sets the data for the columns displayed in the model.
   * 
   * @param columns The objects that represent the columns (usually the column names).
   */
  public void setColumns(ArrayList newColumns)
  {
    columns.clear();
    columns.addAll(newColumns);
    fireTableDataChanged();
  }

  /**
   * Returns the data that represents the columns in the table.
   * 
   * @return The columns displayed in the model.
   */
  public ArrayList getColumns()
  {
    return columns;
  }

  /**
   * Removes the rows specified from the table.
   * 
   * @param rows The indexes of the rows to remove.
   * @return The values removed from the table.
   */
  public Object[] removeRows(int[] rows)
  {
    Object[] values = new Object[rows.length];
    Arrays.sort(rows);
    for(int i=rows.length-1;i>=0;i--)
      values[i] = columns.remove(rows[i]);
    fireTableDataChanged();
    return values;
  }

  /**
   * Deletes the rows from the table that match the given column data.
   * 
   * @param rowData The rows to delete.
   * @return The rows removed.
   */
  public Object[] removeRows(Object[] rowData)
  {
    ArrayList removedRows = new ArrayList();
    for(int i=0;i<rowData.length;i++)
      if(columns.remove(rowData[i]))
        removedRows.add(rowData[i]);
    fireTableDataChanged();
    return removedRows.toArray();
  }
  
  /**
   * Adds the given data to the model. If any of the given rows are already in 
   * the model, they are ignored.
   * 
   * @param rows An array of the objects that represent the data in a row.
   */
  public void addRows(Object[] rows)
  {
    for(int i=0;i<rows.length;i++)
      if(! columns.contains(rows[i]))
        columns.add(rows[i]);
    fireTableDataChanged();
  }

  /**
   * Finds the row index of an item in the table.
   * 
   * @param columnData The data that makes up the row to find.
   * @return The row index of the given data or -1 if it is not found.
   */
  public int findColumnData(Object columnData)
  {
    return columns.indexOf(columnData);
  }

  /**
   * Returns the number of columns in the table. This is always one.
   * 
   * @return The number of columns in the table, one.
   */
  public int getColumnCount()
  {
    return 1;
  }

  /**
   * Gets the value at the given row and column.
   * 
   * @param row The row of the cell to get the value of.
   * @param column The column of the cell to get the value of.
   * @return The value of the given cell.
   */
  public Object getValueAt(int row, int column)
  {
    return columns.get(row);
  }

  /**
   * Gets the name of the given column.
   * 
   * @param column The index of the column to return the name of.
   * @return The name of the given column.
   */
  public String getColumnName(int column)
  {
    return "Name";
  }

  /**
   * Removes all data from the model.
   */
  public void removeAllRows()
  {
    columns.clear();
  }
}