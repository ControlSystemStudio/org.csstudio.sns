package org.csstudio.mps.sns.tools.swing;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import javax.swing.JTable;

import org.csstudio.mps.sns.tools.data.Signal;

/**
 * Provides a renderer for displaying the ID field of a <CODE>Signal</CODE> in 
 * a table. This renderer displays the ID in red if it is not in the database.
 * 
 * @author Chris Fowlkes
 */
public class SignalIDRenderer extends DefaultTableCellRenderer 
{
  /**
   * Creates a new <CODE>SignalIDRenderer</CODE>
   */
  public SignalIDRenderer()
  {
  }

  /**
   * Returns the cell renderer. The renderer will display the signal ID in red
   * if it is not in the database, black otherwise.
   * 
   * @param table The <CODE>JTable</CODE> the renderer is for.
   * @param value The value being renderd.
   * @param isSelected <CODE>true</CODE> if the cell is selected, <CODE>false</CODE> otherwise.
   * @param row The row of the cell the renderer will appear in.
   * @param column The column of the cell the renderer will appear in.
   * @return A renderer for the given cell and data.
   */
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
  {
    DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if(((Signal)value).isInDatabase())
      renderer.setForeground(Color.black);
    else
      renderer.setForeground(Color.red);
    return renderer;
  }
}