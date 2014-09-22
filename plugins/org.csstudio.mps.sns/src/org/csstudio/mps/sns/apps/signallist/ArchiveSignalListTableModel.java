package org.csstudio.mps.sns.apps.signallist;
import org.csstudio.mps.sns.tools.swing.AbstractSignalTableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.csstudio.mps.sns.tools.data.Signal;

/**
 * Provides a model for displaying instances of <CODE>Signal</CODE> and thier 
 * data relating to archives in a <CODE>JTable</CODE>.
 * 
 * @author Chris Fowlkes
 */
public class ArchiveSignalListTableModel extends AbstractSignalTableModel
{
  /**
   * Creates a new <CODE>ArchiveSignalListTableModel</CODE>.
   */
  public ArchiveSignalListTableModel()
  {
  }

  /**
   * Gets the number of columns in the model. This model contains three columns:
   * signal ID, archive frequency, and archive type.
   * 
   * @return The number of columns in the model.
   */
  public int getColumnCount()
  {
    return 3;
  }

  /**
   * Gets the value for the given cell.
   * 
   * @param rowIndex The index of the row for the cell of which to return the value.
   * @param columnIndex The index of the column for the cell of which to return the value.
   * @return The value of the given cell.
   */
  public Object getValueAt(int rowIndex, int columnIndex)
  {
    Signal currentSignal = getSignalAt(rowIndex);
    Object value;
    switch(columnIndex)
    {
      case 0:
        value = currentSignal.getID();
        break;
      case 1:
        value = currentSignal.getArchiveFrequency();
        break;
      case 2:
        value = currentSignal.getArchiveType();
        break;
      default:
        throw new IllegalArgumentException(columnIndex + " is not a valid column index.");
    }//switch(columnIndex)
    return value;
  }

  /**
   * Gets the name of the given column.
   * 
   * @param column The index of the column for which to return the name.
   * @return The name of the given column.
   */
  public String getColumnName(int column)
  {
    String columnName;
    switch(column)
    {
      case 0:
        columnName = "Signal ID";
        break;
      case 1:
        columnName = "Archive Frequency";
        break;
      case 2:
        columnName = "Archive Type";
        break;
      default:
        throw new IllegalArgumentException(column + " is not a valid column index.");
    }//switch(column)
    return columnName;
  }
}