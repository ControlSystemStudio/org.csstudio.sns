package org.csstudio.mps.sns.tools.swing;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.*;

import org.csstudio.mps.sns.tools.data.Device;

/**
 * Provides a model for displaying the ID and description of entries in the 
 * device table. This class is used in the signal wizard to display devices.
 * 
 * @author Chris Fowlkes
 */
public class DeviceTableModel extends AbstractTableModel 
{
  /**
   * Holds the instances of <CODE>Device</CODE> that comprise the data held in 
   * the model.
   */
  private ArrayList data = new ArrayList();
  
  /**
   * Creates a new <CODE>DeviceTableModel</CODE>.
   */
  public DeviceTableModel()
  {
  }

  /**
   * Gets the number of rows in the model.
   * 
   * @return The number of rows in the model.
   */
  public int getRowCount()
  {
    return data.size();
  }

  /**
   * Gets the number of columns in the table (2).
   * 
   * @return The number of columns in the table.
   */
  public int getColumnCount()
  {
    return 2;
  }

  /**
   * Gets the value of the given cell.
   * 
   * @param rowIndex The row index of the cell to return the value of.
   * @return The value of the given cell.
   */
  public Object getValueAt(int rowIndex, int columnIndex)
  {
    Device selectedDevice = getDeviceAt(rowIndex);
    if(columnIndex == 0)
      return selectedDevice.getID();
    else
      return selectedDevice.getDescription();
  }

  /**
   * Gets the <CODE>Device</CODE> represented at the given row in the model.
   * 
   * @param rowIndex The row of the <CODE>Device</CODE> to return.
   * @return The <CODE>Device</CODE> at the given index.
   */
  public Device getDeviceAt(int rowIndex)
  {
    return (Device)data.get(rowIndex);
  }

  /**
   * Gets the instances of <CODE>Device</CODE> at the given row indexes.
   * 
   * @param rows The rows to return the instances of <CODE>Device</CODE> at.
   * @return The instances of <CODE>Device</CODE> at the given rows.
   */
  public Device[] getDeviceAt(int[] rows)
  {
    Device[] devices = new Device[rows.length];
    for(int i=0;i<devices.length;i++)
      devices[i] = getDeviceAt(rows[i]);
    return devices;
  }
  
  /**
   * Gets the name of the given column.
   * 
   * @param column The index of the column to return the name of.
   * @return The name to appear in the header of the given column.
   */
  public String getColumnName(int column)
  {
    if(column == 0)
      return "Device ID";
    else
      return "Description";
  }

  /**
   * Removes all instances of <CODE>Device</CODE> from the model.
   */
  public void clear()
  {
    int lastRow = getRowCount() - 1;
    if(lastRow >= 0)
    {
      data.clear();
      fireTableRowsDeleted(0, lastRow);
    }//if(lastRow >= 0)
  }

  /**
   * Adds the given <CODE>Device</CODE> to the model.
   * 
   * @param device The <CODE>Device</CODE> to add to the model.
   */
  public void addDevice(Device device)
  {
    int newRow = getRowCount();
    data.add(device);
    fireTableRowsInserted(newRow, newRow);
  }
}