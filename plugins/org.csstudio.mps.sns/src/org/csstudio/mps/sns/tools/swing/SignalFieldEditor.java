package org.csstudio.mps.sns.tools.swing;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import java.awt.*;

import org.csstudio.mps.sns.tools.data.SignalField;
import org.csstudio.mps.sns.tools.data.SignalFieldMenu;
import org.csstudio.mps.sns.tools.data.SignalFieldType;

/**
 * This class provides an editor for editing EPICS Signal fields in a table. If
 * the field has values in the SGNL_FLD_MENU table, the editor will be a 
 * <CODE>JComboBox</CODE> with those values in the drop down. Otherwise it will
 * be a <CODE>JTextField</CODE>.
 * 
 * @author Chris Fowlkes
 */
public class SignalFieldEditor extends AbstractCellEditor implements TableCellEditor
{
  /**
   * Holds the text field editor <CODE>Component</CODE>.
   */
  private JTextField textFieldEditor = new JTextField();
  /**
   * Holds the combo box editor <CODE>Component</CODE>.
   */
  private JComboBox comboBoxEditor = new JComboBox();
  /**
   * Holds the last editor used. This is needed tro be able to get the value.
   */
  private Component lastEditor;

  /**
   * Creates a new <CODE>SignalFieldEditor</CODE>.
   */
  public SignalFieldEditor()
  {
  }

  /**
   * Gets the <CODE>SignalFieldEditor</CODE> for the given cell. This is either
   * a <CODE>JComboBox</CODE> or a <CODE>JTextField</CODE>, depending on the 
   * state of the parameters passed in.
   * 
   * @param table The <CODE>JTable</CODE> the editor is for.
   * @param value A valid (not null) instance of <CODE>SignalField</CODE>, with a valid <CODE>SignalFieldType</CODE> for the value of the type property.
   * @param isSelected <CODE>true</CODE> if the cell is selected, <CODE>false</CODE> otherwise.
   * @param row The row of the cell the editor will appear in.
   * @param column The column of the cell the editor will appear in.
   * @return An editor for the given cell and data.
   */
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
  {
    TableModel model = table.getModel();
    SignalFieldType type = ((SignalField)value).getType();
    SignalFieldMenu fieldMenu = type.getMenu();
    int itemCount;
    if(fieldMenu == null)
      itemCount = 0;
    else
      itemCount = fieldMenu.getSize();
    if(itemCount > 0)
    {
      lastEditor = comboBoxEditor;
      DefaultComboBoxModel comboModel = (DefaultComboBoxModel)comboBoxEditor.getModel();
      comboModel.removeAllElements();
      for(int i=0;i<itemCount;i++)
      {
        String currentMenuItem = fieldMenu.getMenuItemAt(i);
        comboModel.addElement(currentMenuItem);
        if(value != null && value.toString().equals(currentMenuItem))
          comboBoxEditor.setSelectedItem(currentMenuItem);
      }//for(int i=0;i<itemCount;i++)
    }//if(itemCount > 0)
    else
      lastEditor = textFieldEditor;
    if(lastEditor == textFieldEditor)
      if(value == null)
        textFieldEditor.setText("");
      else
        textFieldEditor.setText(value.toString());
    return lastEditor;
  }

  /**
   * Gets the value of the editor.
   * 
   * @return The item selected or entered by the user.
   */
  public Object getCellEditorValue()
  {   
    if(lastEditor == comboBoxEditor)
      return comboBoxEditor.getSelectedItem();
    else
      return textFieldEditor.getText();
  }
}