package org.csstudio.mps.sns.apps.results;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import java.awt.*;

/**
 * Provides a cell renderer that denotes rows with errors in red text and with 
 * a special icon.
 *
 * @author Chris Fowlkes
 */
public class ResultTableRenderer extends DefaultTableCellRenderer 
{
  /**
   * Holds the row errors for the data displayed in the table. Rows have errors
   * if the value in the array at the index of the row is not <CODE>null</CODE>.
   */
  private String[] rowErrors;
  /**
   * Holds the icon used to denote a row with an error.
   */
  private ImageIcon redIcon;
  /**
   * Holds the icon used to denote a row without an error.
   */
  private ImageIcon greenIcon;

  /**
   * Creates a <CODE>ResultTableRenderer</CODE>.
   */
  public ResultTableRenderer()
  {
    Class thisClass = getClass();
    try
    {
      redIcon = new ImageIcon(thisClass.getResource("../../resources/images/RedFlag.gif"));
      greenIcon = new ImageIcon(thisClass.getResource("../../resources/images/GreenFlag.gif"));
    }//try
    catch(Exception ex)
    {
      ex.printStackTrace();
    }//catch(Exception ex)
  }

  /**
   * Creates a <CODE>ResultTableRenderer</CODE>.
   *
   * @param rowErrors Error descriptions for those rows with errors.
   */
  public ResultTableRenderer(String[] rowErrors)
  {
    this();
    setRowErrors(rowErrors);
  }

  /**
   * Gets the renderer component for the given cell. This method returns a 
   * renderer with red text for lines with errors.
   *
   * @param table The table the cell will appear in.
   * @param value The value of the cell.
   * @param isSelected <CODE>true</CODE> if the cell is selected, <CODE>false</CODE> otherwise.
   * @param hasFocus <CODE>true</CODE> if the cell has focus, <CODE>false</CODE> otherwise.
   * @param row The row number of the cell.
   * @param column The column number of the cell.
   */
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
  {
    Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if(rowErrors == null || rowErrors[row] == null)
    {
      renderer.setForeground(Color.black);
      if(column == 0)
        ((JLabel)renderer).setIcon(greenIcon);
      else
        ((JLabel)renderer).setIcon(null);
    }//if(rowErrors == null || rowErrors[row] == null)
    else
    {
      renderer.setForeground(Color.red);
      if(column == 0)
        ((JLabel)renderer).setIcon(redIcon);
      else
        ((JLabel)renderer).setIcon(null);
    }//else
    return renderer;
  }

  /**
   * Sets the errors for the rows. Rows with errors will have an error message,
   * rows without errors will have a <CODE>null</CODE> at the array index 
   * matching the row number.
   *
   * @param rowErrors Error messages for the rows.
   */
  public void setRowErrors(String[] rowErrors)
  {
    this.rowErrors = rowErrors;
  }
}