package org.csstudio.mps.sns.tools.swing;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.*;

import org.csstudio.mps.sns.tools.data.SignalField;
import org.csstudio.mps.sns.tools.data.SignalFieldType;

/**
 * Provides a table cell renderer for signal fields.  This renderer uses the 
 * fld_desc field in the sgnl_fld_def table, and "grays out" cells that are not
 * applicable for the record type.
 * 
 * @author Chris Fowlkes
 */
public class SignalFieldRenderer extends DefaultTableCellRenderer 
{

  /**
   * Creates a new <CODE>SignalFieldRenderer</CODE>.
   */
  public SignalFieldRenderer()
  {
  }

  /**
   * Gets a renderer for the cell. The renderer uses the fld_desc field in the 
   * sgnl_fld_def table, and "grays out" cells that are not applicable for the 
   * record type.
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
    if(table.isCellEditable(row, column))
    {
      if(! isSelected)
        renderer.setBackground(Color.white);
      if(value == null)
        renderer.setToolTipText("");
      else
      {
        SignalField field = (SignalField)value;
        renderer.setToolTipText(field.getType().getDescription());
      }
    }
    else
    {
      renderer.setOpaque(true);
      renderer.setToolTipText("N/A for Type");
      if(isSelected)
      {
        //Average default selected color and gray...
        int selectColor = (Color.lightGray.getRGB() + renderer.getBackground().getRGB()) / 2;
        renderer.setBackground(new Color(selectColor));
      }
      else
        renderer.setBackground(Color.lightGray);
    }
    return renderer;
  }
}