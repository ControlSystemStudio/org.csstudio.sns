package org.csstudio.mps.sns.tools.swing;
import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import org.csstudio.mps.sns.apps.devicepicklist.DevicePickListDialog;

public class DialogCellEditor extends AbstractCellEditor implements TableCellEditor
{
  private JButton button = new JButton();
  private DevicePickListDialog dialog;
  
  public DialogCellEditor()
  {
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

  }

  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
  {
    if(value == null)
      button.setText("");
    else
      button.setText(value.toString());
    return button;
  }

  public Object getCellEditorValue()
  {
    return dialog.getDevice();
  }

  public void setDialog(DevicePickListDialog dialog)
  {
    this.dialog = dialog;
  }

  private void jbInit() throws Exception
  {
    button.setHorizontalAlignment(SwingConstants.LEFT);
    button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          button_actionPerformed(e);
        }
      });
  }

  private void button_actionPerformed(ActionEvent e)
  {
    dialog.center();
    dialog.setVisible(true);
    if(dialog.getResult() == DevicePickListDialog.CANCEL)
      fireEditingCanceled();
    else
      fireEditingStopped();
  }

  public void cancelCellEditing()
  {
    // TODO:  Override this javax.swing.AbstractCellEditor method
    super.cancelCellEditing();
  }

  public boolean stopCellEditing()
  {
    // TODO:  Override this javax.swing.AbstractCellEditor method
    return super.stopCellEditing();
  }
}