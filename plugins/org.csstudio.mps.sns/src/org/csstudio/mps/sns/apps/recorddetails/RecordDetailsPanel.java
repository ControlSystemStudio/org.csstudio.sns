package org.csstudio.mps.sns.apps.recorddetails;

import org.csstudio.mps.sns.tools.swing.ImageDialog;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.Document;
import oracle.sql.BLOB;
import org.csstudio.mps.sns.view.MPSBrowserView;
/**
 * Provides an interface for looking at a single record.
 * 
 * @author Chris Fowlkes
 */
public class RecordDetailsPanel extends JPanel 
{
  private JPanel labelPanel = new JPanel();
  private GridLayout labelPanelLayout = new GridLayout();
  private JPanel fieldPanel = new JPanel();
  private GridLayout fieldPanelLayout = new GridLayout();
  private BorderLayout layout = new BorderLayout();
  /**
   * Holds the display names for each column in the table. The keys are the 
   * column names
   */
  private HashMap displayNames;
  /**
   * Holds the flag used to determine if a commit is needed. This is only 
   * updated when the interface is linked to a <CODE>ResultSet</CODE>.
   */
  private boolean changed = false;
  /**
   * Holds the instances of <CODE>ChangeListener</CODE> that have been added to 
   * the <CODE>RecordDetailsPanel</CODE>.
   */
  private ArrayList changeListeners = new ArrayList();
  /**
   * Holds the instances of <CODE>DocumentListener</CODE> that have been added 
   * to the <CODE>RecordDetailsPanel</CODE>.
   */
  private ArrayList documentListeners = new ArrayList();
  /**
   * Holds the dialog used to open an image.
   */
  private JFileChooser fileDialog;
  /**
   * Holds the dialog that displays any images in the database.
   */
  private ImageDialog imageDialog;
  /**
   * Holds the <CODE>ResultSet</CODE> that contains the data.
   */
  private ResultSet data;

  /**
   * Creates a new <CODE>RedordDetailsPanel</CODE>.
   */
  public RecordDetailsPanel()
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

  /**
   * Component initialization.
   * 
   * @throws java.lang.Exception Thrown on SQL error.
   */
  private void jbInit() throws Exception
  {
    this.setLayout(layout);
    labelPanel.setLayout(labelPanelLayout);
    labelPanelLayout.setColumns(1);
    labelPanelLayout.setRows(0);
    labelPanelLayout.setVgap(5);
    fieldPanel.setLayout(fieldPanelLayout);
    fieldPanelLayout.setColumns(1);
    fieldPanelLayout.setRows(0);
    fieldPanelLayout.setVgap(5);
    layout.setHgap(5);
    this.add(fieldPanel, BorderLayout.CENTER);
    this.add(labelPanel, BorderLayout.WEST);
  }

  /**
   * Adds the <CODE>ChangeListener</CODE> to the interface. This listener will
   * be notified when the state of any check boxes in the interface change.
   * 
   * @param listener The <CODE>ChangeListener</CODE> to add to the interface.
   */
  public void addChangeListener(ChangeListener listener)
  {
    changeListeners.add(listener);
  }

  /**
   * Removess the <CODE>ChangeListener</CODE> from the interface. 
   * 
   * @param listener The <CODE>ChangeListener</CODE> to remove from the interface.
   */
  public void removeChangeListener(ChangeListener listener)
  {
    changeListeners.remove(listener);
  }

  /**
   * Adds the <CODE>DocumentListener</CODE> to the interface. This listener will
   * be notified when the text of any text fields in the interface change.
   * 
   * @param listener The <CODE>DocumentListener</CODE> to add to the interface.
   */
  public void addDocumentListener(DocumentListener listener)
  {
    documentListeners.add(listener);
  }

  /**
   * Removes the <CODE>DocumentListener</CODE> from the interface. 
   * 
   * @param listener The <CODE>DocumentListener</CODE> to remove from the interface.
   */
  public void removeDocumentListener(DocumentListener listener)
  {
    documentListeners.remove(listener);
  }
  
  /**
   * Makes sure the dialog has all the correct filed types and lays out the 
   * fields.
   * 
   * @param table The <CODE>JTable</CODE> containing the rows for which data is being displayed.
   */
  private void layoutFields(final JTable table)
  {
    int[] selectedRows = table.getSelectedRows();
    if(selectedRows.length <= 0)
      return;//Nothing selected.
    clear();
    int rowIndex = selectedRows[0];
    int currentRow = 0;
    int columnCount = table.getColumnCount();
    for(int i=0;i<columnCount;i++)
    {
      Component currentEditor;
      if(table.isCellEditable(rowIndex, i))
      {
        Object currentValue = table.getValueAt(rowIndex, i);
        currentEditor = table.getCellEditor(rowIndex, i).getTableCellEditorComponent(table, currentValue, true, rowIndex, i);
      }
      else
        currentEditor = null;
      if(currentEditor == null)
        currentEditor = new JLabel();
      else
      {
        final int editColumn = i;
        if(currentEditor instanceof JTextField)
        {
          final JTextField editor = new JTextField();
          editor.getDocument().addDocumentListener(new DocumentListener()
          {
            public void changedUpdate(DocumentEvent e)
            {
              table.setValueAt(editor.getText(), table.getSelectedRows()[0], editColumn);
            }
            
            public void	insertUpdate(DocumentEvent e)
            { 
              table.setValueAt(editor.getText(), table.getSelectedRows()[0], editColumn);
            }
            
            public void removeUpdate(DocumentEvent e)
            {
              table.setValueAt(editor.getText(), table.getSelectedRows()[0], editColumn);
            }
          });
          currentEditor = editor;
        }//if(currentEditor instanceof JTextField)
        else
          if(currentEditor instanceof JCheckBox)
          {
            final JCheckBox editor = new JCheckBox();
            editor.addChangeListener(new ChangeListener()
            {
              public void stateChanged(ChangeEvent e)
              {
                if(editor.isSelected())
                  table.setValueAt(Boolean.TRUE, table.getSelectedRows()[0], editColumn);
                else
                  table.setValueAt(Boolean.FALSE, table.getSelectedRows()[0], editColumn);
              }
            });
            currentEditor = editor;
          }//if(currentEditor instanceof JTextField)
      }//else
      labelPanelLayout.setRows(++currentRow);
      fieldPanelLayout.setRows(currentRow);
      addLabel(table.getColumnName(i));
      fieldPanel.add(currentEditor);
    }//for(int i=0;i<columnCount;i++)
    labelPanel.doLayout();
    fieldPanel.doLayout();
  }

  /**
   * Returns the editor at the given index.
   * 
   * @param i The index of the editor to return.
   * @return The editor at the given index.
   */
  public Component editorAt(int i)
  {
    return fieldPanel.getComponent(i);
  }

  /**
   * Sets the <CODE>JTable</CODE> used to display the data in the main 
   * interface.
   * 
   * @param newTable The <CODE>JTable</CODE> used to display the data in the main interface.
   */
  public void setTable(final JTable newTable)
  {
    newTable.getModel().addTableModelListener(new TableModelListener()
    {
      public void tableChanged(TableModelEvent e)
      {
        if(e.getLastRow() == TableModelEvent.HEADER_ROW)
          layoutFields(newTable);
      }
    });
    newTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged(ListSelectionEvent e)
      {
        layoutFields(newTable);
        populateFields(newTable);
      }
    });
    layoutFields(newTable);
    populateFields(newTable);
  }

  /**
   * Populates the fields of the dialog.
   * 
   * @param table The <CODE>JTable</CODE> with which to populate the interface.
   */
  private void populateFields(final JTable table)
  {
    int rowIndex = table.getSelectedRow();
    if(rowIndex >= 0)
    {
      int columnCount = table.getColumnCount();
      for(int i=0;i<columnCount;i++)
      {
        Object currentValue = table.getValueAt(rowIndex, i);
        Component currentEditor = fieldPanel.getComponent(i);
        if(currentEditor instanceof JLabel)
        {
          String currentStringValue;
          if(currentValue == null)
            currentStringValue = "";
          else
            currentStringValue = currentValue.toString();
          ((JLabel)currentEditor).setText(currentStringValue);
        }//if(currentEditor == null)
        else
          if(currentEditor instanceof JTextField)
          {
            //Need to create a copy of the field.
            String currentStringValue;
            if(currentValue == null)
              currentStringValue = "";
            else
              currentStringValue = currentValue.toString();
            ((JTextField)currentEditor).setText(currentStringValue);
          }//if(currentEditor instanceof JTextField)
          else
            if(currentEditor instanceof JCheckBox)
            {
              //Need to create a copy of the field.
              if(currentValue == null)
                ((JCheckBox)currentEditor).setSelected(false);
              else
                ((JCheckBox)currentEditor).setSelected(((Boolean)currentValue).booleanValue());
            }//if(currentEditor instanceof JTextField)
        }//for(int i=0;i<columnCount;i++)
    }//if(rowIndex >= 0)
  }

  /**
   * Populates the fields of the dialog.
   * 
   * @param table The <CODE>JTable</CODE> with which to populate the interface.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  private void populateFields(final ResultSet data) throws java.sql.SQLException
  {
    ResultSetMetaData metaData = data.getMetaData();
    int columnCount = metaData.getColumnCount();
    int currentRow = 0;
    for(int i=0;i<columnCount;i++)
    {
      final String currentValue = data.getString(i + 1);
      final int editorIndex = i;
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          Component currentEditor = fieldPanel.getComponent(editorIndex);
          if(currentEditor instanceof JLabel)
            ((JLabel)currentEditor).setText(currentValue);
          else
            if(currentEditor instanceof JTextField)
              ((JTextField)currentEditor).setText(currentValue);
            else
              if(currentEditor instanceof JCheckBox)
              {
                //Need to create a copy of the field.
                if(currentValue == null)
                  ((JCheckBox)currentEditor).setSelected(false);
                else
                  ((JCheckBox)currentEditor).setSelected(currentValue.equals("Y"));
              }
        }
      });
    }
  }

  /**
   * Sets display names for the fields in the dialog box. This method should not
   * be called until after the <CODE>setTable</CODE> method. Doing so will 
   * result in an exception.
   * 
   * @param displayNames The user friendly names for the fields in the dialog.
   */
  public void setDisplayNames(HashMap displayNames)
  {
    int labelCount = labelPanel.getComponentCount();
    for(int i=0;i<labelCount;i++)
    {
      JLabel currentLabel = (JLabel)labelPanel.getComponent(i);
      Object displayLabel = displayNames.get(currentLabel.getText());
      if(displayLabel != null)
        currentLabel.setText(displayLabel.toString());
    }//for(int i=0;i<labelCount;i++)
    this.displayNames = displayNames;
  }
  
  /**
   * Sets the fields and data using the current row in the 
   * <CODE>ResultSet</CODE>.
   * 
   * @param result The <CODE>ResultSet</CODE> containing the record to display.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void setData(ResultSet result) throws java.sql.SQLException
  {
    layoutFields(result);
    populateFields(result);
    this.data = result;
  }
  
  /**
   * Gets the <CODE>ResultSet</CODE> for the <CODE>RecordDetailsPanel</CODE>.
   * 
   * @return The <CODE>ResultSet</CODE> that holds the data for the <CODE>RecordDetailsPanel</CODE>.
   */
  public ResultSet getData()
  {
    return this.data;
  }

  /**
   * Clears the panels.
   */
  private void clear()
  {
    labelPanel.removeAll();
    fieldPanel.removeAll();
    labelPanelLayout.setRows(0);
    fieldPanelLayout.setRows(0);
  }


  /**
   * Makes sure the dialog has all the correct filed types and lays out the 
   * fields.
   * 
   * @param data The <CODE>ResultSet</CODE> containing the rows for which data is being displayed.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  private void layoutFields(final ResultSet data) throws java.sql.SQLException
  {
    clear();
    ResultSetMetaData metaData = data.getMetaData();
    int columnCount = metaData.getColumnCount();
    int currentRow = 0;
    for(int i=1;i<=columnCount;i++)
    {
      //Determine the type of column we're dealing with.
      final Component currentEditor;
      if(metaData.getColumnType(i) == Types.BLOB)
      {
        //Image. Add panel for changing image.
        JPanel panel = new JPanel(new BorderLayout());
        final JButton viewButton = new JButton("View");
        boolean nullValue = data.getBlob(i) == null;
        viewButton.setEnabled(! nullValue);
        final int columnIndex = i;
        viewButton.addActionListener(new ActionListener()
        {
          public void	actionPerformed(ActionEvent e)
          {
            showImage(data, columnIndex);
          }
        });
//        JButton changeButton = new JButton("Change");
//        changeButton.addActionListener(new ActionListener()
//        {
//          public void	actionPerformed(ActionEvent e)
//          {
//            changeImage(data, columnIndex);
//          }
//        });
//        final JButton removeButton = new JButton("Remove");
//        removeButton.setEnabled(! nullValue);
//        changeButton.addActionListener(new ActionListener()
//        {
//          public void	actionPerformed(ActionEvent e)
//          {
//            removeImage(data, columnIndex, new JButton[]{viewButton, removeButton});
//          }
//        });
        JPanel innerPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        innerPanel.add(viewButton, null);
//        innerPanel.add(changeButton, null);
//        innerPanel.add(removeButton, null);
        panel.add(innerPanel, BorderLayout.WEST);
        currentEditor = panel;
      }
      else
        if(! metaData.isWritable(i))
          currentEditor = new JLabel();
        else
        {
          int columnType = metaData.getColumnType(i);
          final int columnIndex = i;
          if(columnType == Types.CHAR && metaData.getPrecision(i) == 1 && metaData.getColumnName(i).endsWith("_IND"))
          {
            final JCheckBox editor = new JCheckBox();
            editor.addChangeListener(new ChangeListener()
            {
              public void stateChanged(ChangeEvent e)
              {
                if(editor.isSelected())
                  updateTextData(data, columnIndex, "Y", editor);
                else
                  updateTextData(data, columnIndex, "N", editor);
              }
            });
            int listenerCount = changeListeners.size();
            for(int j=0;j<listenerCount;j++) 
              editor.addChangeListener((ChangeListener)changeListeners.get(j));
            currentEditor = editor;
          }
          else
          {
            final JTextField editor = new JTextField();
            Document document = editor.getDocument();
            document.addDocumentListener(new DocumentListener()
            {
              public void changedUpdate(DocumentEvent e)
              {
                updateTextData(data, columnIndex, editor.getText(), editor);
              }
              
              public void	insertUpdate(DocumentEvent e)
              { 
                updateTextData(data, columnIndex, editor.getText(), editor);
              }
              
              public void removeUpdate(DocumentEvent e)
              {
                updateTextData(data, columnIndex, editor.getText(), editor);
              }
            });
            int listenerCount = documentListeners.size();
            for(int j=0;j<listenerCount;j++) 
              document.addDocumentListener((DocumentListener)documentListeners.get(j));
            currentEditor = editor;
          }//if(currentEditor instanceof JTextField)
        }//else
      final int layoutRow = ++currentRow, columnIndex = i;
      final String columnName = data.getMetaData().getColumnName(columnIndex);
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          labelPanelLayout.setRows(layoutRow);
          fieldPanelLayout.setRows(layoutRow);
          fieldPanel.add(currentEditor);
          addLabel(columnName);
        }
      });
    }//for(int i=0;i<columnCount;i++)
//    labelPanel.doLayout();
//    fieldPanel.doLayout();
//    doLayout();
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        labelPanel.validate();
        fieldPanel.validate();
        validate();
      }
    });
  }

  /**
   * Shows the image stored in the <CODE>ResultSet</CODE> at the given column in 
   * a dialog.
   * 
   * @param columnIndex The index of the blob column holding the image.
   * @param data The <CODE>ResultSet</CODE> containing the image.
   */
  private void showImage(final ResultSet data, final int columnIndex)
  {
    Thread blobLoadThread = new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          BLOB image = (BLOB)data.getBlob(columnIndex);
          byte[] imageData = new byte[(int)image.length()];
          byte[] buffer = new byte[image.getBufferSize()];
          InputStream iStream = image.getBinaryStream();
          try
          {
            int bytesRead = iStream.read(buffer);
            int progress = 0;
            while(bytesRead != -1)
            {
              System.arraycopy(buffer, 0, imageData, progress, bytesRead);
              progress += bytesRead;
              bytesRead = iStream.read(buffer);
            }
            if(imageDialog == null)
              imageDialog = new ImageDialog(null, "Image", false);
            imageDialog.setImage(new ImageIcon(imageData));
            SwingUtilities.invokeLater(new Runnable()
            {
              public void run()
              {
                imageDialog.center();
                imageDialog.setVisible(true);
              }
            });
          }
          finally
          {
            iStream.close();
          }
        }
        catch(java.sql.SQLException ex)
        {
          ex.printStackTrace();
          showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
        catch(java.io.IOException ex)
        {
          ex.printStackTrace();
          showMessage(ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    blobLoadThread.start();
  }

  /**
   * Uses <CODE>SwingUtilities.invokeLater</CODE> to show a message in a 
   * <CODE>JOptionPane</CODE>.
   * 
   * @param message The message to display.
   * @param title The title for the message.
   * @param messageType The message type.
   */
  private void showMessage(final Object message, final String title, final int messageType)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        JOptionPane.showMessageDialog(RecordDetailsPanel.this, message, title, messageType);
      }
    });
  }
  
//  /**
//   * Allows the user to select a new image to store at the given column.
//   * 
//   * @param columnIndex The column of the field in which to store the image.
//   * @param data The <CODE>ResultSet</CODE> in which to store the image.
//   */
//  private void changeImage(final ResultSet data, final int columnIndex)
//  {
//    if(fileDialog == null)
//      fileDialog = new JFileChooser();
//    if(fileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
//    {
//      Thread blobLoadThread = new Thread(new Runnable()
//      {
//        public void run()
//        {
//          try
//          {
//            BLOB contents = (BLOB)data.getBlob(columnIndex);
//            byte[] buffer = new byte[contents.getBufferSize()];
//            File imageFile = fileDialog.getSelectedFile();
//            BufferedInputStream iStream = new BufferedInputStream(new FileInputStream(imageFile));
//            try
//            {
//              OutputStream oStream = contents.setBinaryStream(0);
//              try
//              {
//                int bytesRead = iStream.read(buffer);
//                while(bytesRead != -1)
//                {
//                  oStream.write(buffer, 0, bytesRead);
//                  bytesRead = iStream.read(buffer);
//                }//while(bytesRead != -1)
//                oStream.flush();
//              }//try
//              finally
//              {
//                oStream.close();
//              }//finally
//            }//try
//            finally
//            {
//              iStream.close();
//            }//finally
//            changed = true;
//          }
//          catch(java.sql.SQLException ex)
//          {
//            ex.printStackTrace();
//            showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
//          }
//          catch(java.io.IOException ex)
//          {
//            ex.printStackTrace();
//            showMessage(ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
//          }
//        }
//      });
//      blobLoadThread.start();
//    }
//  }
//
//  /**
//   * Removes the image stored in the given column from the 
//   * <CODE>ResultSet</CODE>.
//   * 
//   * @param columnIndex The index of the blob field to clear.
//   * @param data The <CODE>ResultSet</CODE> containing the field to clear.
//   * @param buttons The instances of <CODE>JButton</CODE> to disable after the operation.
//   */
//  private void removeImage(final ResultSet data, final int columnIndex, final JButton[] buttons)
//  {
//    Thread blobLoadThread = new Thread(new Runnable()
//    {
//      public void run()
//      {
//        try
//        {
//          data.updateNull(columnIndex);
//          SwingUtilities.invokeLater(new Runnable()
//          {
//            public void run()
//            {
//              for(int i=0;i<buttons.length;i++) 
//                buttons[i].setEnabled(false);
//            }
//          });
//        }
//        catch(java.sql.SQLException ex)
//        {
//          ex.printStackTrace();
//          showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
//        }
//      }
//    });
//    blobLoadThread.start();
//  }
  
  /**
   * Updates the text in the <CODE>ResultSet</CODE> if needed to match the value 
   * in the editor.
   * 
   * @param data The <CODE>ResultSet</CODE> containing the data.
   * @param columnIndex The index of the column in the <CODE>ResultSet</CODE> to update.
   * @param textValue The value in the component that is to be put in the <CODE>ResultSet</CODE> if needed.
   * @param editor The <CODE>Component</CODE> containing the new value.
   */
  private void updateTextData(ResultSet data, int columnIndex, String textValue, Component editor)
  {
    try
    {
      if(! MPSBrowserView.compare(data.getString(columnIndex), textValue))
      {
        if(textValue == null || textValue.equals(""))
          data.updateNull(columnIndex);
        else
          data.updateString(columnIndex, textValue);
        setChanged(true);
      }
    }
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(editor, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  /**
   * Adds the label for the given text to the label panel. If there is a display
   * name corresponding to the text passed in, that is used for the label text. 
   * Otherwise the text passed in is used.
   * @param text
   */
  private void addLabel(String text)
  {
    JLabel newLabel = new JLabel();
    if(displayNames == null)
      newLabel.setText(text);
    else
    {
      Object displayName = displayNames.get(text);
      if(displayName == null)
        newLabel.setText(text);
      else
        newLabel.setText(displayName.toString());
    }//else
    labelPanel.add(newLabel);
  }
  
  /**
   * Determine's if the data in the <CODE>ResultSet</CODE> the interface is using has been 
   * changed.
   * 
   * @return <CODE>true</CODE> if the <CODE>ResultSet</CODE> has been changed, <CODE>false</CODE> otherwise.
   */
  public boolean isChanged()
  {
    return changed;
  }
  
  /**
   * Used to reset the commit needed flag.
   * 
   * @param newCommitNeeded The new value for the commit needed property.
   */
  public void setChanged(boolean newChanged)
  {
    changed = newChanged;
  }
}