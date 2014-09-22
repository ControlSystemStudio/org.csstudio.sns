package org.csstudio.mps.sns.tools.swing;
import org.csstudio.mps.sns.application.JeriDialog;
import javax.swing.*;
import java.util.*;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.CardLayout;
import javax.swing.JCheckBox;

/**
 * Provides an interface that allows the user to set the value of a column for 
 * multiple rows.
 * 
 * @author Chris Fowlkes
 */
public class FillColumnDialog extends JeriDialog 
{
  private JPanel fieldPanel = new JPanel();
  private JPanel labelPanel = new JPanel();
  private JComboBox fieldComboBox = new JComboBox();
  private JTextField valueTextField = new JTextField();
  private GridLayout fieldPanelLayout = new GridLayout();
  private GridLayout labelPanelLayout = new GridLayout();
  private JLabel fieldLabel = new JLabel();
  private JLabel valueLabel = new JLabel();
  private JPanel outerButtonPanel = new JPanel();
  private JPanel innerButtonPanel = new JPanel();
  private FlowLayout outerButtonPanelLayout = new FlowLayout();
  private GridLayout innerButtonPanelLayout = new GridLayout();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private JPanel mainPanel = new JPanel();
  private BorderLayout mainPanelLayout = new BorderLayout();
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
   * Holds the classes for the given columns.
   */
  private HashMap columnClasses = new HashMap();
  private JPanel valuePanel = new JPanel();
  private CardLayout valuePanelLayout = new CardLayout();
  private JCheckBox valueCheckBox = new JCheckBox();

  /**
   * Creates a new, non-modal, <CODE>FillColumnDialog</CODE>.
   */
  public FillColumnDialog()
  {
    this(null, "", false);
  }

  /**
   * Creates a new <CODE>FillColumnDialog</CODE>.
   * 
   * @param parent The parent window of the dialog.
   * @param title The title that appears in the title bar of the dialog.
   * @param modal Pass as <CODE>true</CODE> for a modal dialog, <CODE>false</CODE> otherwise.
   */
  public FillColumnDialog(Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
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
   * @throws java.lang.Exception Thrown on initialization error.
   */
  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(264, 150));
    this.addWindowListener(new java.awt.event.WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          this_windowClosing(e);
        }
      });
    fieldPanel.setLayout(fieldPanelLayout);
    labelPanel.setLayout(labelPanelLayout);
    fieldComboBox.addItemListener(new ItemListener()
      {
        public void itemStateChanged(ItemEvent e)
        {
          fieldComboBox_itemStateChanged(e);
        }
      });
    fieldPanelLayout.setRows(2);
    fieldPanelLayout.setColumns(1);
    fieldPanelLayout.setVgap(5);
    labelPanelLayout.setRows(2);
    labelPanelLayout.setColumns(1);
    labelPanelLayout.setVgap(5);
    fieldLabel.setText("Field:");
    fieldLabel.setDisplayedMnemonic('F');
    fieldLabel.setLabelFor(fieldComboBox);
    valueLabel.setText("Value:");
    valueLabel.setDisplayedMnemonic('V');
    valueLabel.setLabelFor(valueTextField);
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    innerButtonPanel.setLayout(innerButtonPanelLayout);
    outerButtonPanelLayout.setAlignment(2);
    innerButtonPanelLayout.setHgap(5);
    okButton.setText("OK");
    okButton.setMnemonic('O');
    okButton.setEnabled(false);
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
    mainPanel.setLayout(mainPanelLayout);
    mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
    mainPanelLayout.setHgap(5);
    valuePanel.setLayout(valuePanelLayout);
    fieldPanel.add(fieldComboBox, null);
    valuePanel.add(valueTextField, "valueTextField");
    valuePanel.add(valueCheckBox, "jCheckBox1");
    fieldPanel.add(valuePanel, null);
    labelPanel.add(fieldLabel, null);
    labelPanel.add(valueLabel, null);
    innerButtonPanel.add(okButton, null);
    innerButtonPanel.add(cancelButton, null);
    outerButtonPanel.add(innerButtonPanel, null);
    this.getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);
    mainPanel.add(labelPanel, BorderLayout.WEST);
    mainPanel.add(fieldPanel, BorderLayout.CENTER);
    this.getContentPane().add(mainPanel, BorderLayout.NORTH);
  }

  /**
   * Sets the fields available to the user.
   * 
   * @param fields A <CODE>ArrayList</CODE> containing the names of the fields to present to the user.
   */
  public void setFields(String[] fields)
  {
    fieldComboBox.removeAllItems();
    for(int i=0;i<fields.length;i++)
      fieldComboBox.addItem(fields[i]);
    okButton.setEnabled(fields.length > 0);
  }

  /**
   * Called when the ok buton is clicked. This method set the value of the
   * result property to <CODE>OK</CODE> and closes the dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void okButton_actionPerformed(ActionEvent e)
  {
    result = OK;
    setVisible(false);
  }

  /**
   * Called when the cancel buton is clicked. This method set the value of the
   * result property to <CODE>CANCEL</CODE> and closes the dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void cancelButton_actionPerformed(ActionEvent e)
  {
    result = CANCEL;
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
  private void this_windowClosing(WindowEvent e)
  {
    result = CANCEL;
  }

  /**
   * Returns the name of the field selected in the field combo.
   * 
   * @return The name of the selected field.
   */
  public String getSelectedField()
  {
    Object selectedItem = fieldComboBox.getSelectedItem();
    if(selectedItem == null)
      return "";
    else
      return selectedItem.toString().trim();
  }

  /**
   * Gets the value to assign to the selected field. If the class of the column
   * was set to <CODE>Boolean.class</CODE>, the value will be an instance of 
   * <CODE>Boolean</CODE>, otherwise is will be an instance of 
   * <CODE>String</CODE>.
   * 
   * @return The value in the text field.
   */
  public Object getValue()
  {
    Object value;
    Object columnClass = columnClasses.get(getSelectedField());
    if(columnClass != null && columnClass.equals(Boolean.class))
      value = Boolean.valueOf(valueCheckBox.isSelected());
    else
      value = valueTextField.getText();
    return value;
  }

  /**
   * Selects the given <CODE>String</CODE> in the field combo box. If the field 
   * is not in the combo box, nothing is done.
   * 
   * @param selectedField The field to select by default.
   */
  public void setSelectedField(String selectedField)
  {
    DefaultComboBoxModel model = (DefaultComboBoxModel)fieldComboBox.getModel();
    if(model.getIndexOf(selectedField) >= 0)
      fieldComboBox.setSelectedItem(selectedField);
  }

  /**
   * Sets the <CODE>Class</CODE> for the given column. By default the 
   * <CODE>Class</CODE> is <CODE>String</CODE>. To make the editor for a column
   * a <CODE>JCheckBox</CODE>, pass <CODE>Boolean.class</CODE> for the column.
   * 
   * @param columnName The name of the column to which to assign the <CODE>Class</CODE>.
   * @param columnClass The <CODE>Class</CODE> that represents the given column.
   */
  public void setColumnClass(String columnName, Class columnClass)
  {
    columnClasses.put(columnName, columnClass);
  }

  /**
   * Called when the state of the field combo box changes. If a new field has 
   * been selected, this method makes sure the appropriate editor is showing.
   * 
   * @param e The <CODE>ItemEvent</CODE> that caused the invocation of this method.
   */
  private void fieldComboBox_itemStateChanged(ItemEvent e)
  {
    if(e.getStateChange() == ItemEvent.SELECTED)
    {
      Object columnClass = columnClasses.get(e.getItem());
      if(columnClass != null && columnClass.equals(Boolean.class))
        valuePanelLayout.last(valuePanel);
      else
        valuePanelLayout.first(valuePanel);
    }//if(e.getStateChange() == ItemEvent.SELECTED)
  }
}