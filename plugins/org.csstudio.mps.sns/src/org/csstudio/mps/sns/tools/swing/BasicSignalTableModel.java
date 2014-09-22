package org.csstudio.mps.sns.tools.swing;
import org.csstudio.mps.sns.tools.swing.AbstractSignalTableModel;
import javax.swing.table.TableModel;

/**
 * Provides a read only model for displaying signal IDs.
 * 
 * @author Chris Fowlkes
 */
public class BasicSignalTableModel extends AbstractSignalTableModel
{
  /**
   * Creates a new <CODE>BasicSignalTableModel</CODE>.
   */
  public BasicSignalTableModel()
  {
  }

  /**
   * Gets the number of columns in the model. This method always returns 
   * <CODE>1</CODE> since the only column in the model is the signal ID column.
   * 
   * @return The number of columns in the model.
   */
  public int getColumnCount()
  {
    return 1;
  }

  /**
   * Gets the value for the given cell.
   * 
   * @param rowIndex The row index of the cell for which to return the value.
   * @param columnIndex The column index of the cell for which to return the value.
   * @return The value of the given cell.
   */
  public Object getValueAt(int rowIndex, int columnIndex)
  {
    return getValueAt(rowIndex, SIGNAL_ID_FIELD_NAME);
  }

  /**
   * Gets the name of the given column.
   * 
   * @param column The index of the column for which to return the name.
   * @return The name of the column at the given index.
   */
  public String getColumnName(int column)
  {
    return "Signal ID";
  }
}