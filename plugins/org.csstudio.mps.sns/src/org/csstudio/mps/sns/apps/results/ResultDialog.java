package org.csstudio.mps.sns.apps.results;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import org.csstudio.mps.sns.application.JeriDialog;

/**
 * Provides an interface for displaying the results of a group of queries. This 
 * is useful for displaying results for a batch of insert queries.
 *
 * @author Chris Fowlkes
 */
public class ResultDialog extends JeriDialog 
{
  private JPanel mainPanel = new JPanel();
  private BorderLayout mainPanelLayout = new BorderLayout();
  private JScrollPane scrollPane = new JScrollPane();
  private JTable table = new JTable();
  private JPanel outerButtonPanel = new JPanel();
  private JTextArea messageText = new JTextArea();
  private JTextArea errorText = new JTextArea();
  /**
   * Holds the errors associated with the data.
   */
  private String[] rowErrors;
  private FlowLayout outerButtonPanelLayout = new FlowLayout();
  private JPanel innerButtonPanel = new JPanel();
  private GridLayout innerButtonPanelLayout = new GridLayout();
  private JButton commitButton = new JButton();
  private JButton cancelButton = new JButton();
  /**
   * <CODE>int</CODE> returned by the <CODE>getResult</CODE> method if commit 
   * was clicked to close the dialog.
   */
  public final static int COMMIT = 1;
  /**
   * <CODE>int</CODE> returned by the <CODE>getResult</CODE> method if cancel 
   * was clicked to close the dialog.
   */
  public final static int CANCEL = 0;
  /**
   * Holds the value of the result property.
   */
  private int result = CANCEL;

  /**
   * Creates a new, non-modal <CODE>ResultDialog</CODE>.
   */
  public ResultDialog()
  {
    this(null, "", false);
  }

  /**
   * Creates a new <CODE>ResultDialog</CODE> as specified by the parameters.
   *
   * @param parent The parent window of the dialog.
   * @param title The title that appears in the title bar of the dialog.
   * @param modal Specifies the modality of the dialog.
   */
  public ResultDialog(Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
    try
    {
      jbInit();
      ListSelectionModel tableSelectionModel = table.getSelectionModel();
      tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      tableSelectionModel.addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              errorText.setText(rowErrors[table.getSelectedRow()]);
            }
          });
        }
      });
      //Making table read only.
      table.setModel(new DefaultTableModel()
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
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
   * @throws java.lang.Exception Thrown on error.
   */
  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(500, 300));
    this.addWindowListener(new java.awt.event.WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          this_windowClosing(e);
        }
      });
    mainPanel.setLayout(mainPanelLayout);
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    messageText.setOpaque(false);
    messageText.setLineWrap(true);
    messageText.setWrapStyleWord(true);
    errorText.setOpaque(false);
    errorText.setLineWrap(true);
    errorText.setWrapStyleWord(true);
    outerButtonPanelLayout.setAlignment(2);
    innerButtonPanel.setLayout(innerButtonPanelLayout);
    innerButtonPanelLayout.setHgap(5);
    commitButton.setText("Commit");
    commitButton.setMnemonic('O');
    commitButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          commitButton_actionPerformed(e);
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
    scrollPane.getViewport().add(table, null);
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    mainPanel.add(messageText, BorderLayout.NORTH);
    mainPanel.add(errorText, BorderLayout.SOUTH);
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    innerButtonPanel.add(commitButton, null);
    innerButtonPanel.add(cancelButton, null);
    outerButtonPanel.add(innerButtonPanel, null);
    this.getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);
  }

  /**
   * Sets the data displayed in the dialog.
   *
   * @param data The data to display in the dialog.
   * @param columnNames The column names to display in the table header.
   * @param rowErrors The errors associated with the rows.
   */
  public void setData(Object[][] data, String[] columnNames, String[] rowErrors)
  {
    DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
    tableModel.setDataVector(data, columnNames);
    this.rowErrors = rowErrors;
    ResultTableRenderer renderer = new ResultTableRenderer(rowErrors);
    table.setDefaultRenderer(Object.class, renderer);
    tableModel.fireTableStructureChanged();
    tableModel.fireTableDataChanged();
    if(data.length > 0 && data[0].length > 0)//Data was provided...
    {
      int columnCount = table.getColumnCount();
      TableColumn currentColumn;
      TableColumnModel allColumns = table.getColumnModel();
      final Properties settings = getApplicationProperties();
      String defaultSize;
      int newSize;
      for(int i=0;i<columnCount;i++)
      {
        //Restore the width of each column...
        final String propertyName = "ResultDialog.Column." + columnNames[i] + ".width";
        currentColumn = allColumns.getColumn(i);
        defaultSize = String.valueOf(currentColumn.getPreferredWidth());
        newSize = Integer.parseInt(settings.getProperty(propertyName, defaultSize));
        currentColumn.setPreferredWidth(newSize);
        //Add a listener to each column to save the width.
        currentColumn.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {
          public void propertyChange(PropertyChangeEvent e)
          {
            if(e.getPropertyName().equals("preferredWidth"))
              settings.setProperty(propertyName, e.getNewValue().toString());
          }
        });
      }//for(int i=0;i<columnCount;i++)
    }//if(data.length > 0 && data[0].length > 0)
  }

  /**
   * Sets the message that appears at the top of the dialog.
   *
   * @param message The message to display at the top of the dialog.
   */
  public void setMessage(String message)
  {
    messageText.setText(message);
  }

  /**
   * Called when the cancel button is clicked. This method sets the result 
   * property of the dialog and hides it.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void cancelButton_actionPerformed(ActionEvent e)
  {
    result = CANCEL;
    setVisible(false);
  }

  /**
   * Called when the commit button is clicked. This method sets the result 
   * property of the dialog and hides it.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void commitButton_actionPerformed(ActionEvent e)
  {
    result = COMMIT;
    setVisible(false);
  }

  /**
   * Returns an <CODE>int</CODE> used to determine which button the dialog was
   * closed with.
   *
   * @return Returns <CODE>COMMIT</CODE> if the commit button was clicked, returns <CODE>CANCEL</CODE> otherwise.
   */
  public int getResult()
  {
    return result;
  }

  /**
   * Called when the window is closed by clicking the X button in the title bar.
   * This method sets the value of the result property to equate closing the 
   * dialog with canceling it.
   *
   * @param e The <CODE>WindowEvent</CODE> that caused the invocation of this method.
   */
  void this_windowClosing(WindowEvent e)
  {
    result = CANCEL;
  }

  /**
   * Called when the window is resized. This method records the new size in the 
   * properties file.
   *
   * @param e The <CODE>ComponentEvent</CODE> that caused the invocation of this method.
   */
  void this_componentResized(ComponentEvent e)
  {
    Dimension newSize = getSize();
    Properties settings = getApplicationProperties();
    if(settings != null)
    {
      settings.setProperty("ResultDialog.width", String.valueOf(newSize.width));
      settings.setProperty("ResultDialog.height", String.valueOf(newSize.height));
    }//if(settings != null)
  }

  /**
   * Sets the text that appears on the commit button.
   * 
   * @param commitText The text to put on the commit button.
   */
  public void setCommitText(String commitText)
  {
    commitButton.setText(commitText);
  }
}