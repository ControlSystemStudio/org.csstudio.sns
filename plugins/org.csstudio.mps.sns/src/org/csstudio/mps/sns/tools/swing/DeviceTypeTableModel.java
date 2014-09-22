package org.csstudio.mps.sns.tools.swing;
import javax.swing.table.AbstractTableModel;
import java.util.*;

import org.csstudio.mps.sns.tools.data.DeviceType;

/**
 * Provides a model for displaying instances of <CODE>DeviceType</CODE> in a 
 * table. It is used in the device type tables in the signal and device wizards.
 * 
 * @author Chris Fowlkes.
 */
public class DeviceTypeTableModel extends AbstractTableModel 
{
  /**
   * Holds the instances of <CODE>DeviceType</CODE> displayed in the table.
   */
  private ArrayList deviceTypes = new ArrayList();

  /**
   * Creates a new <CODE>DeviceTypeTableModel</CODE>.
   */
  public DeviceTypeTableModel()
  {
  }

  /**
   * Determines if the given cell is editable. This model is read only so the 
   * method always returns <CODE>false</CODE>.
   * 
   * @param rowIndex The row index of the cell to check.
   * @param columnIndex The column index of the cell to check.
   * @return Always returns <CODE>false</CODE> as this model is read only.
   */
  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return false;
  }

  /**
   * Removes all of the instances of <CODE>DeviceType</CODE> from the model.
   */
  public void removeAllDeviceTypes()
  {
    int rowCount = getRowCount();
    if(rowCount > 0)
    {
      deviceTypes.clear();
      fireTableRowsDeleted(0, rowCount - 1);
    }//if(rowCount > 0)
  }

  /**
   * Adds the given <CODE>DeviceType</CODE> to the model.
   * 
   * @param newType The <CODE>DeviceType</CODE> to add to the model.
   */
  public void addDeviceType(DeviceType newType)
  {
    int rowCount = getRowCount();
    deviceTypes.add(newType);
    fireTableRowsInserted(rowCount, rowCount);
  }

  /**
   * Gets the <CODE>DeviceType</CODE> represented by the given row.
   * 
   * @param row The row index of the <CODE>DeviceType</CODE> to return.
   * @return The <CODE>DeviceType</CODE> at the given row.
   */
  public DeviceType getDeviceTypeAt(int row)
  {
    return (DeviceType)deviceTypes.get(row);
  }

  /**
   * Returns the number of rows in the model. The number of rows is equal to the
   * number of instances of <CODE>DeviceType</CODE> that have been added to the 
   * model via the <CODE>addDeviceType</CODE> method.
   * 
   * @return The number of rows in the model.
   */
  public int getRowCount()
  {
    return deviceTypes.size();
  }

  /**
   * Returns the number of columns in the model. This model contains two 
   * columns, one for the description of the <CODE>DeviceType</CODE> and one for
   * it's ID.
   * 
   * @return The number of columns in the model, two.
   */
  public int getColumnCount()
  {
    return 2;
  }

  /**
   * Gets the value for the given cell.
   * 
   * @param row The row index of the cell to get the value of.
   * @param column The column index of the cell to get the value of.
   * @return The value for the given cell.
   */
  public Object getValueAt(int row, int column)
  {
    if(column < 0 || column >= getColumnCount())
      throw new java.lang.IllegalArgumentException(column + " is not a valid column number.");
    if(column == 0)
      return getDeviceTypeAt(row).getID();
    else
      return getDeviceTypeAt(row).getDescription();
  }

  /**
   * Gets the name of the given column. This model contains a column for the 
   * description and ID of each <CODE>DeviceType</CODE>.
   * 
   * @param column The column to get the name of.
   * @return The name of the given column.
   */
  public String getColumnName(int column)
  {
    if(column < 0 || column >= getColumnCount())
      throw new java.lang.IllegalArgumentException(column + " is not a valid column number.");
    if(column == 0)
      return "Device Type ID";
    else
      return "Description";
  }
}