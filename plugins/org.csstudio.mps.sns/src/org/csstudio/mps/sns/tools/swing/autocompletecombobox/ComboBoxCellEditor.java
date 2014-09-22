package org.csstudio.mps.sns.tools.swing.autocompletecombobox;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * Fixes several problems with <code>JComboBox</code> cell edtors. This class is
 * used instead of <code>DefaultCellEditor</code> to fix some problems with
 * using editable <code>JComboBox</code> objects as editors in a
 * <code>JTable</code>. One problem is that when a <code>JComboBox</code> is
 * shown in a <code>JTable</code> with focus, if it is editable, the
 * <code>JComboBox</code> objects editor does not have focus.
 *
 * @author Chris Fowlkes
 * @version 1.0
 */

public class ComboBoxCellEditor extends AbstractCellEditor implements TableCellEditor
{
  private JComboBox editor;
  /**
   * Holds the items always displayed in the <CODE>JComboBox</CODE>.
   */
  private ArrayList defaultItems;

  /**
   * Default constructor. Creates a <code>ComboBoxCellEditor</code> with an
   * <code>AutoCompleteComboBox</code> as the editing component.
   */
  public ComboBoxCellEditor()
  {
    this(new AutoCompleteComboBox());
  }

  /**
   * Default constructor. Creates a <code>ComboBoxCellEditor</code> with the
   * <code>JComboBox</code> passed in as the editing component.
   *
   * @param newEditor The <code>JComboBox</code> to use as the editor
   */
  public ComboBoxCellEditor(JComboBox newEditor)
  {
    editor = newEditor;
//    editor.addItemListener(new ItemListener()
//    {
//      public void itemStateChanged(ItemEvent e)
//      {
//        SwingUtilities.invokeLater(new Runnable()
//        {
//          public void run()
//          {
//            if(! editor.isPopupVisible())
//            {
//              JTable table = (JTable)editor.getParent();
//              if(table == null)
//                fireEditingStopped();
//              else
//              {
//                int row = table.getEditingRow(), column = table.getEditingColumn();
//                fireEditingStopped();
//                table.editCellAt(row, column);
//              }//else
//            }//if(! editor.isPopupVisible())
//          }
//        });
//      }
//    });
//    editor.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        JTable table = (JTable)editor.getParent();
//        if(table == null)
//          fireEditingStopped();
//        else
//        {
//          int row = table.getEditingRow(), column = table.getEditingColumn();
//          fireEditingStopped();
//          table.editCellAt(row, column);
//        }//else
//      }
//    });
  }

  /**
   * Gets the value of the editor. This method is provided as an implementation
   * of the abstract method in the <code>AbstractCellEditor</code> class. It
   * gets the value of the editor.
   *
   * @return the value of the editor
   */
  public Object getCellEditorValue()
  {
    return editor.getSelectedItem();
  }

  /**
   * Gets the editor component. This method is provided as part of the
   * <code>TableCellEditor</code> interface. It returns the
   * <code>Component</code> to be used to edit the giuven cell.
   *
   * @param table the <code>JTable</code> the component will be used in
   * @param value the value of the cell before editing
   * @param isSelected <code>true</code> if the editor is selected, <code>false</code> otherwise
   * @param row the row being edited
   * @param column the column being edited
   */
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
  {
    ArrayList choices = getDefaultItems();
    if(choices != null)
    {
      editor.removeAllItems();
      int itemCount = choices.size();
      for(int i=0;i<itemCount;i++)
        editor.addItem(choices.get(i));
      if(! choices.contains(value))
        editor.addItem(value);
    }//if(choices != null)
    editor.setSelectedItem(value);
    final boolean editableCombo = editor.isEditable();
    if(editableCombo)
      if(value == null)
        editor.getEditor().setItem("");
      else
        editor.getEditor().setItem(value.toString());
    else
      editor.setSelectedItem(value);
    if(isSelected)
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          if(editableCombo)
            editor.getEditor().getEditorComponent().requestFocus();
          else
            editor.requestFocus();
        }
      });
    return editor;
  }

  /**
   * Sets the items displayed in the <CODE>JComboBox</CODE> by default. If this 
   * property is set to a non <CODE>null</CODE> value, when the 
   * <CODE>JComboBox</CODE> is shown, the items passed into this method are used 
   * in the list, along with the current value of the cell. This means that the
   * contents of the list change each time the list is shown and always includes
   * the current value of the cell.
   * 
   * @param defaultItems The items to show in the <CODE>JComboBox</CODE> each time it is shown.
   */
  public void setDefaultItems(ArrayList defaultItems)
  {
    this.defaultItems = defaultItems;
  }

  /**
   * Gets the items displayed in the <CODE>JComboBox</CODE> by default.
   * 
   * @return The items to show in the <CODE>JComboBox</CODE> each time it is shown.
   */
  public ArrayList getDefaultItems()
  {
    return defaultItems;
  }
}