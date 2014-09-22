package org.csstudio.mps.sns.apps.templateimport;

import org.csstudio.mps.sns.tools.swing.TemplateTableModel;
import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.csstudio.mps.sns.tools.data.Template;
import org.csstudio.mps.sns.application.JeriDialog;

/**
 * Provides an interface that allows the user to enter information about the 
 * templates created from an import operation.
 * 
 * @author Chris Fowlkes
 */
public class TemplateImportDialog extends JeriDialog 
{
  private JTextArea instructionsText = new JTextArea();
  private JScrollPane scrollPane = new JScrollPane();
  private JTable table = new JTable();
  private JPanel outerButtonPanel = new JPanel();
  private JPanel innerButtonPanel = new JPanel();
  private BorderLayout outerButtonPanelLayout = new BorderLayout();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private GridLayout innerButtonPanelLayout = new GridLayout();
  /**
   * Constant that denotes the OK button was clicked.
   */
  public final static int OK = 1;
  /**
   * Constant that denotes the OK button was not clicked.
   */
  public final static int CANCEL = 0;
  /**
   * Holds and indicator to determine if the OK button was used to exit the 
   * dialog.
   */
  private int result = CANCEL;
  /**
   * Provides the model for the table.
   */
  private TemplateTableModel model = new TemplateTableModel();
  /**
   * Holds the key used in the property file to store settings for the table.
   */
  private String key = "TemplateImportDialog";
  
  /**
   * Creates a new instance of <CODE>TemplateImportDialog</CODE>.
   */
  public TemplateImportDialog()
  {
    this(null, "", false);
  }

  /**
   * Creates a new instance of <CODE>TemplateImportDialog</CODE>.
   * 
   * @param parent The parent window.
   * @param title The text to appear in the title bar of the window.
   * @param modal Pass as <CODE>true</CODE> for a modal dialog.
   */
  public TemplateImportDialog(Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
    try
    {
      jbInit();
      model.addTableModelListener(new TableModelListener()
      {
        public void tableChanged(TableModelEvent e)
        {
          tableModel_tableChanged(e);
        }
      });
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

  }

  /**
   * Component initialization.
   * 
   * @throws java.lang.Exception Thrown on intialization error.
   */
  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(400, 300));
    this.addWindowListener(new java.awt.event.WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          this_windowClosing(e);
        }
      });
    instructionsText.setText("Enter a name for each template.");
    instructionsText.setEditable(false);
    instructionsText.setOpaque(false);
    table.setModel(model);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    outerButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    innerButtonPanel.setLayout(innerButtonPanelLayout);
    okButton.setText("OK");
    okButton.setMnemonic('O');
    okButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          okButton_actionPerformed(e);
        }
      });
    cancelButton.setText("Cancel");
    cancelButton.setMnemonic('C');
    cancelButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          cancelButton_actionPerformed(e);
        }
      });
    innerButtonPanelLayout.setHgap(5);
    this.getContentPane().add(instructionsText, BorderLayout.NORTH);
    scrollPane.getViewport().add(table, null);
    this.getContentPane().add(scrollPane, BorderLayout.CENTER);
    innerButtonPanel.add(okButton, null);
    innerButtonPanel.add(cancelButton, null);
    outerButtonPanel.add(innerButtonPanel, BorderLayout.EAST);
    this.getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);
  }

  /**
   * Called when the ok button is clicked. This method makes sure a name has 
   * been given to each imported template, then closes the dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void okButton_actionPerformed(ActionEvent e)
  {
    if(table.isEditing())
      table.getCellEditor().stopCellEditing();
    int templateCount = model.getRowCount();
    for(int i=0;i<templateCount;i++)
    {
      String currentID = model.getTemplateAt(i).getID();
      if(currentID == null || currentID.trim().equals(""))
      {
        JOptionPane.showMessageDialog(this, "Please enter an ID for each imported template.");
        return;//Aborting close.
      }//if(currentID == null || currentID.trim().equals(""))
    }//for(int i=0;i<templateCount;i++)
    result = OK;
    setVisible(false);
  }

  /**
   * Called when the cancel button is clicked. This method closes the dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void cancelButton_actionPerformed(ActionEvent e)
  {
    result = CANCEL;
    setVisible(false);
  }

  /**
   * Returns an <CODE>int</CODE> reflecting the button clicked to exit the 
   * dialog.
   * 
   * @return <CODE>OK</CODE> if the ok button was clicked to exit the dialog. <CODE>CANCEL</CODE> otherwise.
   */
  public int getResult()
  {
    return result;
  }

  /**
   * Called when the window is closed without clicking the OK or cancel button.
   * 
   * @param e The <CODE>WindowEvent</CODE> that caused the invocation of this method.
   */
  private void this_windowClosing(WindowEvent e)
  {
    result = CANCEL;
  }

  /**
   * Sets the instances of <CODE>Template</CODE> displayed in the dialog.
   * 
   * @param newTemplates The instances of <CODE>Template</CODE> to display in the dialog.
   */
  public void setTemplates(Template[] newTemplates)
  {
    model.setTemplates(newTemplates);
  }

  /**
   * Gets the instances of <CODE>Template</CODE> displayed in the dialog.
   * 
   * @return The instances of <CODE>Template</CODE> displayed in the dialog.
   */
  public Template[] getTemplates()
  {
    return model.getTemplates();
  }

  /**
   * Restores the widths of the columns. The column widths are saved in and 
   * restored from the application properties for each table.
   *
   * @param e The <CODE>TableModelEvent</CODE> that caused the invocation of this method.
   */
  private void tableModel_tableChanged(final TableModelEvent e)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        if(e.getFirstRow() == TableModelEvent.HEADER_ROW)
          tableStructureChanged();
      }
    });
  }

  /**
   * Called when the table structure is changed. This method is called by the 
   * <CODE>tableModel_tableChanged</CODE> method whenever that method is invoked
   * to handle a table structure change.
   */
  private void tableStructureChanged()
  {
    restoreColumnWidths();
    arrangeColumns();
    int columnCount = table.getColumnCount();
    TableColumnModel allColumns = table.getColumnModel();
    for(int i=0;i<columnCount;i++)
    {
      final String propertyName = key + "." + table.getColumnName(i) + ".width";
      allColumns.getColumn(i).addPropertyChangeListener(new java.beans.PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent e)
        {
          if(e.getPropertyName().equals("preferredWidth"))
            getApplicationProperties().setProperty(propertyName, e.getNewValue().toString());
        }
      });
    }//for(int i=0;i<columnCount;i++)
    //Need to save column positions when moved.
    allColumns.addColumnModelListener(new TableColumnModelListener()
    {
      public void columnAdded(TableColumnModelEvent e)
      {
      }

      public void columnMarginChanged(ChangeEvent e)
      {
      }

      public void columnMoved(final TableColumnModelEvent e)
      {
        saveColumnPositions();
      }

      public void columnRemoved(TableColumnModelEvent e)
      {
      }

      public void columnSelectionChanged(ListSelectionEvent e)
      {
      }
    });
  }
  
  /**
   * This method puts the columns in the order the user left them in.
   */
  private void arrangeColumns()
  {
    Properties settings = getApplicationProperties();
    String columnCountProperty = settings.getProperty(key + ".ColumnCount");
    if(columnCountProperty != null)
    {
      int columnCount = Integer.parseInt(columnCountProperty), moveTo = 0;
      int tableColumnCount = table.getColumnCount();
      ArrayList modelColumns = new ArrayList(tableColumnCount);
      for(int i=0;i<tableColumnCount;i++)
        modelColumns.add(model.getColumnName(i));
      for(int i=0;i<columnCount;i++)
      {
        String currentColumnName = settings.getProperty(key + ".Column" + i);
        if(currentColumnName != null)
        {
          int modelIndex = modelColumns.indexOf(currentColumnName);
          if(modelIndex >= 0)//Column is in the model, move it...
          {
            int moveFrom = table.convertColumnIndexToView(modelIndex);//Convert to view index.
            if(moveTo != moveFrom)
              table.moveColumn(moveFrom, moveTo);
            moveTo++;//Next position.
          }//if(modelIndex >= 0)
        }//if(currentColumnName != null)
      }//for(int i=0;i<columnCount;i++)
    }//if(columnCountProperty != null)
  }

  /**
   * This method saves the positions of the table's columns in the application's
   * properties file.
   */
  private void saveColumnPositions()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        String propertyName = "TemplateImportDialog", column;
        int columnCount = table.getColumnCount();
        Properties settings = getApplicationProperties();
        settings.setProperty(propertyName + ".ColumnCount", String.valueOf(columnCount));
        for(int position=0;position<columnCount;position++)
        {
          column = table.getColumnName(position);
          settings.setProperty(propertyName + ".Column" + position, column);
        }//for(int position=0;position<columnCount;position++)
      }
    });
  }

  /**
   * This method restores the column widths to the widths saved in the 
   * applicaiton's properties file.
   */
  private void restoreColumnWidths()
  {
    Properties applicationSettings = getApplicationProperties();
    if(applicationSettings != null)
    {
      TableColumnModel allColumns = table.getColumnModel();
      int columnCount = allColumns.getColumnCount();
      TableColumn currentColumn;
      String widthProperty;
      for(int i=0;i<columnCount;i++)
      {
        currentColumn = allColumns.getColumn(i);
        final String propertyName = key + "." + table.getColumnName(i) + ".width";
        widthProperty = applicationSettings.getProperty(propertyName, "100");
        currentColumn.setPreferredWidth(Integer.parseInt(widthProperty));
        currentColumn.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {
          public void propertyChange(PropertyChangeEvent e)
          {
            if(e.getPropertyName().equals("preferredWidth"))
            {
              TableColumn resizeColumn = (TableColumn)e.getSource();
              getApplicationProperties().setProperty(propertyName, e.getNewValue().toString());
            }//if(e.getPropertyName().equals("preferredWidth"))
          }
        });
      }//for(int i=0;i<columnCount;i++)
    }//if(applicationSettings != null)
  }

  /**
   * Sets the application property file.
   * 
   * @param applicationProperties The instance of <CODE>Properties</CODE> that holds the settings for the application.
   */
  public void setApplicationProperties(Properties applicationProperties)
  {
    super.setApplicationProperties(applicationProperties);
    tableStructureChanged();//Sets column widths, etc.
  }
}