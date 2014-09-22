package org.csstudio.mps.sns.apps.devicepicklist;
import org.csstudio.mps.sns.application.JeriDialog;
import java.awt.*;
import javax.sql.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import javax.swing.event.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.EpicsGroup;
import org.csstudio.mps.sns.view.MPSBrowserView;

/**
 * Provides an interface that allows the user to select instances of 
 * <CODE>Device</CODE>. This is used to allow the user to add devices to an 
 * <CODE>EpicsGroup</CODE> in the group window.
 * 
 * @author Chris Fowlkes
 */
public class DevicePickListDialog extends JeriDialog 
{
  private JPanel outerButtonPanel = new JPanel();
  private FlowLayout outerButtonPanelLayout = new FlowLayout();
  private JPanel innerButtonPanel = new JPanel();
  private GridLayout innerButtonPanelLayout = new GridLayout();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private JScrollPane scrollPane = new JScrollPane();
  private JList deviceList = new JList();
  /**
   * Holds the <CODE>DataSource</CODE> for the window. This is used to make 
   * database connections.
   */
  private DataSource connectionPool;
  private DefaultListModel deviceListModel = new DefaultListModel();
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
  private BorderLayout dialogLayout = new BorderLayout();
  private JPanel findPanel = new JPanel();
  private BorderLayout findPanelLayout = new BorderLayout();
  private JLabel groupLabel = new JLabel();
  private JComboBox groupCombo = new JComboBox();
  private JButton findButton = new JButton();
  private JSeparator separator = new JSeparator();

  /**
   * Creates a new, non-modal  <CODE>DevicePickListDialog</CODE>.
   */
  public DevicePickListDialog()
  {
    this(null, "", false);
  }

  /**
   * Creates a new <CODE>DevicePickListDialog</CODE>.
   * 
   * @param parent The parent window of the dialog.
   * @param title The <CODE>String</CODE> to appear in the title bar of the dialog.
   * @param modal Pass as <CODE>true</CODE> to make the dialog modal, <CODE>false</CODE> to make it non-modal.
   */
  public DevicePickListDialog(Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
    try
    {
      jbInit();
      deviceList.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              //Enable OK button if atleast one device is selected.
              okButton.setEnabled(deviceList.getSelectedIndices().length > 0);
            }
          });
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
   * @throws java.lang.Exception Thrown on initialization error.
   */
  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(400, 300));
    this.getContentPane().setLayout(dialogLayout);
    this.addWindowListener(new java.awt.event.WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          this_windowClosing(e);
        }
      });
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    outerButtonPanelLayout.setAlignment(2);
    innerButtonPanel.setLayout(innerButtonPanelLayout);
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
    deviceList.setModel(deviceListModel);
    deviceList.addKeyListener(new java.awt.event.KeyAdapter()
      {
        public void keyTyped(KeyEvent e)
        {
          deviceList_keyTyped(e);
        }
      });
    deviceList.addMouseListener(new java.awt.event.MouseAdapter()
      {
        public void mouseClicked(MouseEvent e)
        {
          deviceList_mouseClicked(e);
        }
      });
    dialogLayout.setVgap(5);
    findPanel.setLayout(findPanelLayout);
    findPanelLayout.setHgap(5);
    findPanelLayout.setVgap(5);
    groupLabel.setText("Epics Group:");
    groupLabel.setDisplayedMnemonic('E');
    groupLabel.setLabelFor(groupCombo);
    groupCombo.addItemListener(new ItemListener()
      {
        public void itemStateChanged(ItemEvent e)
        {
          groupCombo_itemStateChanged(e);
        }
      });
    findButton.setText("Find Devices");
    findButton.setMnemonic('F');
    findButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          findButton_actionPerformed(e);
        }
      });
    innerButtonPanel.add(okButton, null);
    innerButtonPanel.add(cancelButton, null);
    outerButtonPanel.add(innerButtonPanel, null);
    this.getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);
    scrollPane.getViewport().add(deviceList, null);
    this.getContentPane().add(scrollPane, BorderLayout.CENTER);
    findPanel.add(groupLabel, BorderLayout.WEST);
    findPanel.add(groupCombo, BorderLayout.CENTER);
    findPanel.add(findButton, BorderLayout.EAST);
    findPanel.add(separator, BorderLayout.SOUTH);
    this.getContentPane().add(findPanel, BorderLayout.NORTH);
  }

  /**
   * Sets the <CODE>DataSource</CODE> for the window.
   *
   * @param connectionPool The <CODE>DataSource</CODE> the application uses to get it's connection to the database.
   */
  public void setDataSource(DataSource connectionPool)
  {
    this.connectionPool = connectionPool;
  }

  /**
   * Gets the <CODE>DataSource</CODE> used by the window to connect to the 
   * database.
   *
   * @return The <CODE>DataSource</CODE> used to connect to the database.
   */
  public DataSource getDataSource()
  {
    return connectionPool;
  }

  /**
   * Refreshes all of the data in the interface including the items in the drop
   * down list and the table.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void refreshData() throws java.sql.SQLException
  {
    Connection oracleConnection = getDataSource().getConnection();
    try
    {
      Statement query = oracleConnection.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT DISTINCT epics_grp_id FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".epics_grp ORDER BY epics_grp_id");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          groupCombo.removeAllItems();
          groupCombo.addItem("All");
          while(result.next())
            groupCombo.addItem(new EpicsGroup(result.getString("epics_grp_id")));
        }//try
        finally
        {
          result.close();
        }//finally
      }//try
      finally
      {
        query.close();
      }//finally
    }//try
    finally
    {
      oracleConnection.close();
    }//finally
  }

  /**
   * Called when the OK button is clicked. This method sets the value of the
   * result property to <CODE>OK</CODE> and closes the window.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void okButton_actionPerformed(ActionEvent e)
  {
    result = OK;
    setVisible(false);
  }

  /**
   * Called when the cancel button is clicked. This method sets the value of the
   * result property to <CODE>CANCEL</CODE> and closes the window.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void cancelButton_actionPerformed(ActionEvent e)
  {
    result = CANCEL;
    setVisible(false);
  }

  /**
   * Called when the dialog is closed by clicking the close button in the title 
   * bar. This method sets the value of the result property to 
   * <CODE>CANCEL</CODE> and closes the window.
   * 
   * @param e The <CODE>WindowEvent</CODE> that caused the invocation of this method.
   */
  void this_windowClosing(WindowEvent e)
  {
    result = CANCEL;
  }

  /**
   * Called when the mouse is clicked on the list. This method checks to see if 
   * the list was double clicked. Double clicks are treated like the user 
   * selected the item in the list and clicked the ok button by setting the 
   * value of the result property to <CODE>OK</CODE> and closing the window.
   * 
   * @param e The <CODE>MouseEvent</CODE> that caused the invocation of this method.
   */
  void deviceList_mouseClicked(MouseEvent e)
  {
    if(e.getClickCount() >= 2)
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          if(deviceList.getSelectedIndices().length > 0)
          {
            result = OK;
            setVisible(false);
          }//if(deviceList.getSelectedIndices().length > 0)
        }
      });
  }

  /**
   * Gets the <CODE>int</CODE> representing the button used to close the dialog.
   * If the OK button was clicked, <CODE>OK</CODE> is returned, otherwise 
   * <CODE>CANCEL</CODE> is returned.
   *
   * @return If the dialog was closed by clicking the OK buttton <CODE>OK</CODE> is returned, otherwise <CODE>CANCEL</CODE> is returned.
   */
  public int getResult()
  {
    return result;
  }

  /**
   * Gets the instances of <CODE>Device</CODE> that are in the list.
   * 
   * @return The instances of <CODE>Device</CODE> in the list.
   */
  public Object[] getDevices()
  {
    return deviceList.getSelectedValues();
  }

  /**
   * Gets the single <CODE>Device</CODE> selected.
   */
  public Device getDevice()
  {
    return (Device)deviceList.getSelectedValue();
  }
  
  /**
   * Sets the property file used by the application to store settings.
   * 
   * @param applicationProperties Settings for the application.
   */
  public void setApplicationProperties(Properties applicationProperties)
  {
    super.setApplicationProperties(applicationProperties);
  }

  /**
   * Called when the find button is clicked. This method Gets the matching 
   * devices from the database and displays them in the list.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void findButton_actionPerformed(ActionEvent e)
  {
    loadDevices();
  }

  /**
   * Sets the select mode for the list. <CODE>false</CODE> by default.
   * 
   * @param singleSelect Pass as <CODE>true</CODE> to force the user to select a single <CODE>Device</CODE>, <CODE>false</CODE> otherwise..
   */
  public void setSingleSelect(boolean singleSelect)
  {
    if(singleSelect)
      deviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    else
      deviceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
  }

  /**
   * Called when the group combo box changes. This method clears and reloads the 
   * list if the dialog is visible.
   * 
   * @param e The <CODE>ItemEvent</CODE> that caused the invocation of this method.
   */
  private void groupCombo_itemStateChanged(ItemEvent e)
  {
    if(isShowing())
    {
      deviceListModel.removeAllElements();
      okButton.setEnabled(false);
      loadDevices();
    }//if(isShowing())
  }

  /**
   * This method loads the devices for the selected group into the list.
   */
  private void loadDevices()
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      Connection oracleConnection = getDataSource().getConnection();
      try
      {
        Statement query = oracleConnection.createStatement();
        try
        {
          StringBuffer sql;
          if(groupCombo.getSelectedIndex() == 0)
          {
            //Selecting all device IDs.
            sql = new StringBuffer("SELECT DVC_ID FROM ");
            sql.append(MPSBrowserView.SCHEMA);
            sql.append(".DVC");
          }//if(groupCombo.getSelectedIndex() == 0)
          else
          {
            //Selecting device IDs in a group.
            sql = new StringBuffer("SELECT IOC_DVC_ID FROM ");
            sql.append(MPSBrowserView.SCHEMA);
            sql.append(".EPICS_GRP WHERE EPICS_GRP_ID = '");
            sql.append(groupCombo.getSelectedItem());
            sql.append("'");
          }//else
          ResultSet result = query.executeQuery(sql.toString());
          try
          {
            deviceListModel.removeAllElements();
            while(result.next())
              deviceListModel.addElement(new Device(result.getString(1)));
          }//try
          finally
          {
            result.close();
          }//finally
        }//try
        finally
        {
          query.close();
        }//finally
      }//try
      finally
      {
        oracleConnection.close();
      }//finally
    }//try
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }//catch(java.sql.SQLException ex)
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }//finally
  }

  /**
   * Called when a key is typed in the list. This method looks for the first 
   * entry in the list that begins with the character represented by the key
   * typed.
   * 
   * @param e The <CODE>KeyEvent</CODE> that caused the invocation of this method.
   */
  private void deviceList_keyTyped(KeyEvent e)
  {
    int rowCount = deviceListModel.size();
    String charTyped = String.valueOf(e.getKeyChar()).toUpperCase();
    for(int i=0;i<rowCount;i++)
    {
      if(deviceListModel.getElementAt(i).toString().toUpperCase().startsWith(charTyped))
      {
        deviceList.getSelectionModel().setSelectionInterval(i, i);
        Rectangle selectedRow = deviceList.getCellBounds(i, i);
        deviceList.scrollRectToVisible(selectedRow);
        break;//No need to continue.
      }//if(currentModel.getValueAt(i, 0).toString().toUpperCase().startsWith(charTyped))
    }//for(int i=0;i<rowCount;i++)
  }
}