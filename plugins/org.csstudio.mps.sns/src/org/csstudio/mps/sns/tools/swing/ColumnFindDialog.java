package org.csstudio.mps.sns.tools.swing;
import org.csstudio.mps.sns.application.JeriDialog;
import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;
import java.io.IOException;
import javax.swing.JPanel;
import java.awt.CardLayout;
import javax.swing.JCheckBox;

/**
 * Provides an interface for search and replace within a column of the table 
 * browser.
 * 
 * @author Chris Fowlkes
 */
public class ColumnFindDialog extends JeriDialog 
{
  private JPanel outerButtonPanel = new JPanel();
  private JPanel innerButtonPanel = new JPanel();
  private BorderLayout outerButtonPanelLayout = new BorderLayout();
  private GridLayout innerButtonPanelLayout = new GridLayout();
  private JPanel searchPanel = new JPanel();
  private JPanel fieldPanel = new JPanel();
  private JPanel labelPanel = new JPanel();
  private BorderLayout searchPanelLayout = new BorderLayout();
  private GridLayout labelPanelLayout = new GridLayout();
  private GridLayout fieldPanelLayout = new GridLayout();
  private JLabel searchLabel = new JLabel();
  private JCheckBox replaceCheckBox = new JCheckBox();
  private JLabel columnLabel = new JLabel();
  private JTextField searchFieldTextBox = new JTextField();
  private JComboBox columnField = new JComboBox();
  private JTextField replaceFieldTextBox = new JTextField();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private JPanel replaceOptionPanel = new JPanel();
  private GridLayout replaceOptionPanelLayout = new GridLayout();
  private JRadioButton singleRadioButton = new JRadioButton();
  private JRadioButton allRadioButton = new JRadioButton();
  private JRadioButton promptRadioButton = new JRadioButton();
  /**
   * Holds the names of the read only columns, if any.
   */
  private String[] readOnlyColumns;
  /**
   * Holds the <CODE>ButtonGroup</CODE> used for the replace options.
   */
  private ButtonGroup radioButtonGroup = new ButtonGroup();
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
   * Constant that denotes the replace single option was selected.
   */
  public final static int SINGLE = 2;
  /**
   * Constant that denotes the replace all option was selected.
   */
  public final static int ALL = 3;
  /**
   * Constant that denotes the prompt for replace option was selected.
   */
  public final static int PROMPT = 4;
  /**
   * Holds the names of the <CODE>Boolean</CODE> columns in the table.
   */
  private String[] booleanColumns;
  private JPanel searchFieldPanel = new JPanel();
  private JPanel replaceFieldPanel = new JPanel();
  private CardLayout searchFieldPanelLayout = new CardLayout();
  private CardLayout replaceFieldPanelLayout = new CardLayout();
  private JCheckBox searchFieldCheckBox = new JCheckBox();
  private JCheckBox replaceFieldCheckBox = new JCheckBox();

  /**
   * Creates a new <CODE>ColumnFindDialog</CODE>.
   */
  public ColumnFindDialog()
  {
    this((Frame)null, "", false);
  }

  /**
   * Creates a new <CODE>ColumnFindDialog</CODE>.
   * 
   * @param parent The parent window to the dialog.
   * @param title The title to appear in the title bar.
   * @param modal Pass as <CODE>true</CODE> for a modal dialog, <CODE>false</CODE> for non-modal.
   */
  public ColumnFindDialog(Dialog parent, String title, boolean modal)
  {
    super(parent, title, modal);
    try
    {
      jbInit();
      setup();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Creates a new <CODE>ColumnFindDialog</CODE>.
   * 
   * @param parent The parent window to the dialog.
   * @param title The title to appear in the title bar.
   * @param modal Pass as <CODE>true</CODE> for a modal dialog, <CODE>false</CODE> for non-modal.
   */
  public ColumnFindDialog(Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
    try
    {
      jbInit();
      setup();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Does additional initialization not included in <CODE>jbInit</CODE>.
   */
  private void setup()
  {
    radioButtonGroup.add(singleRadioButton);
    radioButtonGroup.add(allRadioButton);
    radioButtonGroup.add(promptRadioButton);
    pack();
    setSize(400, getHeight());
  }

  /**
   * Component initialization.
   * 
   * @throws java.lang.Exception Thrown on initialization error.
   */
  private void jbInit() throws Exception
  {
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    this.addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          this_windowClosing(e);
        }
      });
    outerButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
    innerButtonPanel.setLayout(innerButtonPanelLayout);
    innerButtonPanelLayout.setHgap(5);
    searchPanel.setLayout(searchPanelLayout);
    searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
    fieldPanel.setLayout(fieldPanelLayout);
    labelPanel.setLayout(labelPanelLayout);
    labelPanelLayout.setColumns(1);
    labelPanelLayout.setRows(3);
    fieldPanelLayout.setRows(3);
    fieldPanelLayout.setColumns(1);
    fieldPanelLayout.setVgap(5);
    searchLabel.setText("Search For:");
    searchLabel.setDisplayedMnemonic('S');
    searchLabel.setLabelFor(searchFieldTextBox);
    replaceCheckBox.setText("Replace With:");
    replaceCheckBox.setMnemonic('R');
    replaceCheckBox.addItemListener(new ItemListener()
      {
        public void itemStateChanged(ItemEvent e)
        {
          replaceCheckBox_itemStateChanged(e);
        }
      });
    columnLabel.setText("In Column:");
    columnLabel.setDisplayedMnemonic('I');
    columnLabel.setLabelFor(columnField);
    columnField.addItemListener(new ItemListener()
      {
        public void itemStateChanged(ItemEvent e)
        {
          columnField_itemStateChanged(e);
        }
      });
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
    replaceOptionPanel.setBorder(BorderFactory.createTitledBorder("Replace Options"));
    replaceOptionPanel.setLayout(replaceOptionPanelLayout);
    replaceOptionPanelLayout.setRows(3);
    singleRadioButton.setText("Single Occurance");
    singleRadioButton.setMnemonic('g');
    allRadioButton.setText("All Occurances");
    allRadioButton.setMnemonic('A');
    promptRadioButton.setText("Prompt");
    promptRadioButton.setMnemonic('P');
    promptRadioButton.setSelected(true);
    searchFieldPanel.setLayout(searchFieldPanelLayout);
    replaceFieldPanel.setLayout(replaceFieldPanelLayout);
    innerButtonPanel.add(okButton, null);
    innerButtonPanel.add(cancelButton, null);
    outerButtonPanel.add(innerButtonPanel, BorderLayout.EAST);
    this.getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);
    labelPanel.add(searchLabel, null);
    labelPanel.add(columnLabel, null);
    labelPanel.add(replaceCheckBox, null);
    searchPanel.add(labelPanel, BorderLayout.WEST);
    searchFieldPanel.add(searchFieldTextBox, "searchField");
    searchFieldPanel.add(searchFieldCheckBox, "jCheckBox1");
    fieldPanel.add(searchFieldPanel, null);
    fieldPanel.add(columnField, null);
    replaceFieldPanel.add(replaceFieldTextBox, "replaceField");
    replaceFieldPanel.add(replaceFieldCheckBox, "jCheckBox2");
    fieldPanel.add(replaceFieldPanel, null);
    searchPanel.add(fieldPanel, BorderLayout.CENTER);
    this.getContentPane().add(searchPanel, BorderLayout.NORTH);
    replaceOptionPanel.add(promptRadioButton, null);
    replaceOptionPanel.add(allRadioButton, null);
    replaceOptionPanel.add(singleRadioButton, null);
    this.getContentPane().add(replaceOptionPanel, BorderLayout.CENTER);
  }

  /**
   * Called when the ok button is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocatin of this method.
   */
  private void okButton_actionPerformed(ActionEvent e)
  {
    result = OK;
    setVisible(false);
  }

  /**
   * Called when the cancel button is clicked. 
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void cancelButton_actionPerformed(ActionEvent e)
  {
    result = CANCEL;
    setVisible(false);
  }

  /**
   * Sets the items available in the column pick list.
   * 
   * @param columnNames The names of the columns available to search or replace.
   */
  final public void setColumnNames(String[] columnNames)
  {
    columnField.removeAllItems();
    for(int i=0;i<columnNames.length;i++) 
      columnField.addItem(columnNames[i]);
  }

  /**
   * Sets the initial value of the column pick list.
   * 
   * @param selectedColumn The name of the column to select in the pick list.
   */
  public void setSelectedColumn(String selectedColumn)
  {
    columnField.setSelectedItem(selectedColumn);
  }

  /**
   * Gets the name of the column selected in the pick list.
   * 
   * @return The name of the selected column.
   */
  public String getSelectedColumn()
  {
    Object selectedColumn = columnField.getSelectedItem();
    if(selectedColumn == null)
      return "";
    else
      return selectedColumn.toString();
  }

  /**
   * Sets the names of the columns that are considered read only. Replace will 
   * not be available as an option if one of these columns is selected.
   * 
   * @param readOnlyColumns The names of the columns for which replace can not be used.
   */
  public void setReadOnlyColumns(String[] readOnlyColumns)
  {
    if(readOnlyColumns == null)
      this.readOnlyColumns = null;
    else
    {
      this.readOnlyColumns = readOnlyColumns;
      Arrays.sort(this.readOnlyColumns);
    }
    enableControls();
  }

  /**
   * Gets the names of the columns for which replace is not a valid function.
   * 
   * @return The names of the read only columns.
   */
  public String[] getReadOnlyColumns()
  {
    return readOnlyColumns;
  }

  /**
   * Called when the item selected in the column pick list changes. This method
   * disables all controls dealing with replace if the column name has been
   * specified as belonging to a read only column with the 
   * <CODE>setReadOnlyColumns</CODE> method.
   * 
   * @param e The <CODE>ItemEvent</CODE> that caused the invocation of this method.
   */
  private void columnField_itemStateChanged(ItemEvent e)
  {
    if(e.getStateChange() == ItemEvent.SELECTED)
      enableControls();
  }

  /**
   * Determines if the controls dealing with text replace should be available
   * and either enables or disables them.
   */
  private void enableControls()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        boolean enable;
        Object selectedColumn = getSelectedColumn();
        String[] readOnlyColumns = getReadOnlyColumns();
        if(readOnlyColumns == null)
          enable = true;
        else
          enable = Arrays.binarySearch(readOnlyColumns, selectedColumn) < 0;
        replaceCheckBox.setEnabled(enable);
        enableReplaceOptions();
        String[] booleanColumns = getBooleanColumns();
        if(booleanColumns == null || Arrays.binarySearch(booleanColumns, selectedColumn) < 0)
        {
          searchFieldPanelLayout.first(searchFieldPanel);
          replaceFieldPanelLayout.first(replaceFieldPanel);
        }
        else
        {
          searchFieldPanelLayout.last(searchFieldPanel);
          replaceFieldPanelLayout.last(replaceFieldPanel);
        }
      }
    });
  }

  /**
   * Enales or disables the replace options radio buttons. The radio buttons are 
   * enabled if the replace checkbox is enabled and selected.
   */
  private void enableReplaceOptions()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        //Options enabled if checkbox enabled and selected.
        boolean enable = replaceCheckBox.isEnabled() && replaceCheckBox.isSelected();
        singleRadioButton.setEnabled(enable);
        allRadioButton.setEnabled(enable);
        promptRadioButton.setEnabled(enable);
        replaceFieldTextBox.setEnabled(enable);
        replaceFieldCheckBox.setEnabled(enable);
      }
    });
  }

  /**
   * Called when the state of the replace checkbox is changed.
   * 
   * @param e The <CODE>ItemEvent</CODE> that caused the invocation of this method.
   */
  private void replaceCheckBox_itemStateChanged(ItemEvent e)
  {
    enableReplaceOptions();
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
   * Gets the optons selected for the replace option. This method returns an 
   * <CODE>int</CODE> reflecting the selected replace option.
   * 
   * @return <CODE>SINGLE</CODE>, <CODE>ALL</CODE>, or <CODE>PROMPT</CODE>, depending on the selected radio button.
   */
  public int getReplaceOption()
  {
    if(singleRadioButton.isSelected())
      return SINGLE;
    else
      if(allRadioButton.isSelected())
        return ALL;
      else
        return PROMPT;
  }

  /**
   * Gets the value to search for in the column. If the selected column is a
   * boolean column, the value will be an instance of <CODE>Boolean</CODE>, 
   * otherwise a <CODE>String</CODE> will be returned.
   * 
   * @return The <CODE>String</CODE> or <CODE>Boolean</CODE> entered by the user to search for in the selected column.
   */
  public Object getSearchValue()
  {
    if(booleanColumns != null && Arrays.binarySearch(booleanColumns, getSelectedColumn()) >= 0)
      return Boolean.valueOf(searchFieldCheckBox.isSelected());
    else
      return searchFieldTextBox.getText().trim();
  }

  /**
   * Determines if the replace option is selected.
   * 
   * @return <CODE>true</CODE> if the replace check box is enabled and selected, <CODE>false</CODE> otherwise.
   */
  public boolean isReplace()
  {
    return replaceCheckBox.isEnabled() && replaceCheckBox.isSelected();
  }

  /**
   * Sets the state of the replace checkbox. 
   * 
   * @param replace Pass as <CODE>true</CODE> to make the peplace checkbox checked, <CODE>false</CODE> to make it unchecked.
   */
  public void setReplace(boolean replace)
  {
    replaceCheckBox.setSelected(replace);
  }
  
  /**
   * Gets the value in the replace field. This method only returns the value in 
   * the replace field, it does not check to see if the replace check box is 
   * checked. That should be done before invoking this method by calling 
   * <CODE>isReplace()</CODE>. If the column selected in a boolean column the
   * value returned will be an instance of <CODE>Boolean</CODE>, otherwise a 
   * <CODE>String</CODE> will be returned.
   * 
   * @return The text in the replace field.
   */
  public Object getReplaceValue()
  {
    if(booleanColumns != null && Arrays.binarySearch(booleanColumns, getSelectedColumn()) >= 0)
      return Boolean.valueOf(replaceCheckBox.isSelected());
    else
      return replaceFieldTextBox.getText();
  }

  /**
   * Gets the names of the <CODE>Boolean</CODE> columns. These will have check
   * boxes instead of text fields.
   * 
   * @return The names of the columns that are of type <CODE>Boolean</CODE>.
   */
  public String[] getBooleanColumns()
  {
    return booleanColumns;
  }

  /**
   * Sets the names of the columns that contain <CODE>Boolean</CODE> values.
   * 
   * @param newBooleanColumns The names of the <CODE>Boolean</CODE> columns in the table.
   */
  public void setBooleanColumns(String[] newBooleanColumns)
  {
    booleanColumns = newBooleanColumns;
    if(booleanColumns != null)
      Arrays.sort(booleanColumns);
    enableControls();
  }
  
  /**
   * Hides or shows the replace controls on the search dialog. By default they
   * are visible.
   * 
   * @param replaceVisible Pass as <CODE>false</CODE> to hide the replace controls entirely.
   */
  public void setReplaceVisible(boolean replaceVisible)
  {
    replaceCheckBox.setVisible(replaceVisible);
    replaceFieldPanel.setVisible(replaceVisible);
    replaceOptionPanel.setVisible(replaceVisible);
    pack();
    setSize(400, getHeight());
  }
}