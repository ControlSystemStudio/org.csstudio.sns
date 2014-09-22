package org.csstudio.mps.sns.apps.signalfieldcolumnselect;
import org.csstudio.mps.sns.tools.data.SignalFieldType;
import org.csstudio.mps.sns.tools.swing.ColumnSelectTableModel;

/**
 * Provides a model for displaying instances of <CODE>SignalFieldType</CODE> in 
 * the column select dialog.
 * 
 * @author Chris Fowlkes
 */
public class SignalFieldColumnSelectTableModel extends ColumnSelectTableModel 
{

  /**
   * Creates a new <CODE>SignalFieldColumnSelectTableModel</CODE>
   */
  public SignalFieldColumnSelectTableModel()
  {
  }

  /**
   * Gets the name of the column at the given index.
   * 
   * @param column The index of the column to return the name of.
   * @return The name of the column at the given index.
   */
  public String getColumnName(int column)
  {
    String name;
    switch(column)
    {
      case 0:
        name = "Field Group";
        break;
      case 1:
        name = "Field Name";
        break;
      default:
        throw new IllegalArgumentException(column + " is not a valid column index.");
    }//switch(column)
    return name;
  }

  /**
   * The number of columns in the model. This model contains 2 columns.
   * 
   * @return 2.
   */
  public int getColumnCount()
  {
    return 2;
  }

  /**
   * Gets the value for the cell at the given rowe and column.
   * 
   * @param rowIndex The row of the cell to get the value of.
   * @param columnIndex The column of the cell tro get the value of.
   * @return The value of the cell at the given row and column.
   */
  public Object getValueAt(int rowIndex, int columnIndex)
  {
    String value;
    switch(columnIndex)
    {
      case 0:
        value = getSignalFieldTypeAt(rowIndex).getPromptGroup();
        break;
      case 1:
        value = getSignalFieldTypeAt(rowIndex).getID();
        break;
      default:
        throw new java.lang.IllegalArgumentException(columnIndex + " is not a vlaid column number.");
    }//switch(columnIndex)
    return value;
  }
  
  /**
   * Returns the <CODE>SignalFieldType</CODE> represented at the given row.
   * 
   * @param row The row index of the <CODE>SignalFieldType</CODE> to return.
   * @return The <CODE>SignalFieldType</CODE> at the given index.
   */
  public SignalFieldType getSignalFieldTypeAt(int row)
  {
    return (SignalFieldType)super.getColumnAtRow(row);
  }
}