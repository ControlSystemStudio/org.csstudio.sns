package org.csstudio.mps.sns.apps.filter;

import org.csstudio.mps.sns.application.JeriDialog;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * Provides an interface that allows the user to select a filter.
 * 
 * @author Chris Fowlkes
 */
public class FilterOpenDialog extends JeriDialog 
{
  private JPanel outerButtonPanel = new JPanel();
  private JPanel innerButtonPanel = new JPanel();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private BorderLayout outerButtonPanelLayout = new BorderLayout();
  private GridLayout innerButtonPanelaLayout = new GridLayout();
  private JPanel mainPanel = new JPanel();
  private GridLayout mainPanelLayout = new GridLayout();
  private JComboBox nameComboBox = new JComboBox();
  private JCheckBox publicCheckBox = new JCheckBox();
  private JPanel comboBoxPanel = new JPanel();
  private BorderLayout comboBoxPanelLayout = new BorderLayout();
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
   * Holds the names of all of the filters.
   */
  private String[] allFilterNames;
  /**
   * Holds the names of the user filters.
   */
  private String[] userFilterNames;

  /**
   * Creates a new <CODE>FilterOpenDialog</CODE>.
   * 
   * @param parent The parent of the dialog.
   * @param title The title of the dialog.
   * @param modal Pass as <CODE>true</CODE> for a modal dialog.
   */
  public FilterOpenDialog(Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
    try
    {
      jbInit();
      pack();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

  }

  /**
   * Constructs a new <CODE>FilterOpenDialog</CODE>.
   * 
   * @param parent The parent window for the dialog.
   * @param title The title that will appear in the title bar of the dialog.
   * @param modal Pass as <CODE>true</CODE> to make the dialog modal, <CODE>false</CODE> otherwise.
   */
  public FilterOpenDialog(Dialog parent, String title, boolean modal)
  {
    super(parent, title, modal);
    try
    {
      jbInit();
      pack();
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
    this.setSize(new Dimension(400, 300));
    this.addWindowListener(new java.awt.event.WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          this_windowClosing(e);
        }
      });
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    outerButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    innerButtonPanel.setLayout(innerButtonPanelaLayout);
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
    innerButtonPanelaLayout.setHgap(5);
    mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
    mainPanel.setLayout(mainPanelLayout);
    mainPanelLayout.setRows(2);
    publicCheckBox.setText("Include Public Filters");
    publicCheckBox.setMnemonic('I');
    publicCheckBox.setSelected(true);
    publicCheckBox.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          publicCheckBox_stateChanged(e);
        }
      });
    comboBoxPanel.setLayout(comboBoxPanelLayout);
    innerButtonPanel.add(okButton, null);
    innerButtonPanel.add(cancelButton, null);
    outerButtonPanel.add(innerButtonPanel, BorderLayout.EAST);
    this.getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);
    comboBoxPanel.add(nameComboBox, BorderLayout.SOUTH);
    mainPanel.add(comboBoxPanel, null);
    mainPanel.add(publicCheckBox, null);
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
  }

  /**
   * Called when the OK button is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
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
   * Called when the window is closed with the close button in the title bar.
   * 
   * @param e The <CODE>WindowEvent</CODE> that caused the invocation of this method.
   */
  private void this_windowClosing(WindowEvent e)
  {
    result = CANCEL;
  }

  /**
   * Returns a value used to determine the button clicked to close the dialog.
   * 
   * @return <CODE>OK</CODE> if the user closed the dialog by clicking the ok button, <CODE>CANCEL</CODE> otherwise.
   */
  public int getResult()
  {
    return result;
  }
  
  /**
   * Gets the item selected in the combo box.
   * 
   * @return The name selected in the combo box.
   */
  public String getSelectedFilterName()
  {
    Object name = nameComboBox.getSelectedItem();
    if(name == null)
      return null;
    else
      return name.toString();
  }

  /**
   * Sets the filter names displayed when the include public filters check box
   * is selected. This should include public and user filter names.
   * 
   * @param allFilterNames The names of all public and user filters.
   */
  public void setAllFilterNames(String[] allFilterNames)
  {
    this.allFilterNames = allFilterNames;
    if(publicCheckBox.isSelected())
      populateComboBox();
  }
  
  /**
   * Sets the filter names displayed when the include public filters check box
   * is not selected. This should include only user filter names.
   * 
   * @param userFilterNames The names of all user filters.
   */
  public void setUserFilterNames(String[] userFilterNames)
  {
    this.userFilterNames = userFilterNames;
    if(! publicCheckBox.isSelected())
      populateComboBox();
  }
  
  /**
   * Populates the combo box with the appropriate values.
   */
  private void populateComboBox()
  {
    String oldSelectedName = getSelectedFilterName();
    nameComboBox.removeAllItems();
    String[] values;
    if(publicCheckBox.isSelected())
      values = allFilterNames;
    else
      values = userFilterNames;
    if(values != null)
    {
      int indexToSelect = -1;
      for(int i=0;i<values.length;i++)
      {
        if(indexToSelect == -1)
          indexToSelect = i;
        nameComboBox.addItem(values[i]);
        if(oldSelectedName != null && oldSelectedName.equals(values[i]))
          indexToSelect = i;
      }
      nameComboBox.setSelectedIndex(indexToSelect);
    }
  }

  /**
   * Called when the state of the check box changes. This method calls 
   * <CODE>populateComboBox()</CODE>.
   * 
   * @param e The <CODE>ChangeEvent</CODE> that caused the invocatino of this method.
   */
  private void publicCheckBox_stateChanged(ChangeEvent e)
  {
    populateComboBox();
  }
}