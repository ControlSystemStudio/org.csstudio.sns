package org.csstudio.mps.sns.tools.database;


import org.csstudio.mps.sns.tools.database.CachingDatabaseAdaptor;
import org.csstudio.mps.sns.tools.database.DatabaseAdaptor;
import org.csstudio.mps.sns.tools.swing.SwingWorker;
import org.csstudio.mps.sns.view.MPSBrowserView;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;


/**
 * Provides an interface to allow the user to log into the application.
 *
 * @author Chris Fowlkes
 */
public class LoginDialog extends JDialog
{
  private JPanel bottomPanel = new JPanel();
  private FlowLayout bottomPanelLayout = new FlowLayout();
  private JButton loginButton = new JButton();
  private JButton cancelButton = new JButton();
  private JPanel buttonPanel = new JPanel();
  private GridLayout buttonPanelLayout = new GridLayout();
  private JPanel centerPanel = new JPanel();
  private BorderLayout centerPanelLayout = new BorderLayout();
  private JPanel labelPanel = new JPanel();
  private GridLayout labelPanelLayout = new GridLayout();
  private JPanel controlPanel = new JPanel();
  private GridLayout controlPanelLayout = new GridLayout();
  private JLabel userIDLabel = new JLabel();
  private JLabel passwordLabel = new JLabel();
  private JTextField userIDField = new JTextField();
  private JPasswordField passwordField = new JPasswordField();
  private JLabel databaseLabel = new JLabel();
  private JComboBox databaseCombo = new JComboBox();
  /**
   * returned by the <CODE>getResult()</CODE> method when the dialog was closed 
   * by clicking the OK button.
   */
  public static final int OK = 0;
  /**
   * returned by the <CODE>getResult()</CODE> method when the dialog was not 
   * closed by clicking the OK button.
   */
  public static final int CANCEL = 1;
  /**
   * Used to determine how the dialog was closed.
   */
  private int result = CANCEL;

  /**
   * Creates a new <CODE>LoginDialog</CODE>.
   * 
   * @param modal Pass as <CODE>true</CODE> for a modal dialog.
   * @param title The text to appear in the title bar of the dialog.
   * @param parent The parent of the dialog.
   */
  public LoginDialog(Frame parent, String title, boolean modal)
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
   * Creates a new <CODE>LoginDialog</CODE>.
   * 
   * @param modal Pass as <CODE>true</CODE> for a modal dialog.
   * @param title The text to appear in the title bar of the dialog.
   * @param parent The parent of the dialog.
   */
  public LoginDialog(Dialog parent, String title, boolean modal)
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
   * Creates a new <CODE>LoginDialog</CODE>. This method creates a non-modal 
   * dialog, with no title or parent window.
   */
  public LoginDialog()
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
   * @throws java.lang.Exception thrown on initialization error.
   */
  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(400, 300));
    this.setTitle("MPS Login");
    this.addComponentListener(new java.awt.event.ComponentAdapter()
      {
        public void componentShown(ComponentEvent e)
        {
          this_componentShown(e);
        }
      });
    this.addWindowListener(new java.awt.event.WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          this_windowClosing(e);
        }
      });
    bottomPanel.setLayout(bottomPanelLayout);
    bottomPanelLayout.setAlignment(2);
    loginButton.setText("Login");
    loginButton.setActionCommand("loginButton");
    loginButton.setMnemonic('L');
    loginButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          loginButton_actionPerformed(e);
        }
      });
    cancelButton.setText("Cancel");
    cancelButton.setActionCommand("cancelButton");
    cancelButton.setMnemonic('C');
    cancelButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          cancelButton_actionPerformed(e);
        }
      });
    buttonPanel.setLayout(buttonPanelLayout);
    buttonPanelLayout.setHgap(5);
    centerPanel.setLayout(centerPanelLayout);
    centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
    centerPanelLayout.setHgap(5);
    labelPanel.setLayout(labelPanelLayout);
    labelPanelLayout.setRows(3);
    labelPanelLayout.setColumns(1);
    controlPanel.setLayout(controlPanelLayout);
    controlPanelLayout.setColumns(1);
    controlPanelLayout.setRows(3);
    controlPanelLayout.setVgap(5);
    userIDLabel.setText("User ID");
    userIDLabel.setLabelFor(userIDField);
    userIDLabel.setDisplayedMnemonic('U');
    passwordLabel.setText("Password");
    passwordLabel.setLabelFor(passwordField);
    passwordLabel.setDisplayedMnemonic('P');
    databaseLabel.setText("RDB");
    databaseLabel.setDisplayedMnemonic('D');
    databaseLabel.setLabelFor(databaseCombo);
    buttonPanel.add(loginButton, null);
    buttonPanel.add(cancelButton, null);
    bottomPanel.add(buttonPanel, null);
    this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    labelPanel.add(userIDLabel, null);
    labelPanel.add(passwordLabel, null);
    labelPanel.add(databaseLabel, null);
    centerPanel.add(labelPanel, BorderLayout.WEST);
    controlPanel.add(userIDField, null);
    controlPanel.add(passwordField, null);
    controlPanel.add(databaseCombo, null);
    centerPanel.add(controlPanel, BorderLayout.CENTER);
    this.getContentPane().add(centerPanel, BorderLayout.CENTER);
    pack();
    getRootPane().setDefaultButton(loginButton);
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
    MPSBrowserView.LOGINRESULT = CANCEL; 
  }

  /**
   * Called when the login button is clicked. This method sets the result 
   * property to <CODE>OK</CODE> and exits the dialog.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void loginButton_actionPerformed(ActionEvent e)
  {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    userIDField.setEnabled(false);
    passwordField.setEnabled(false);
    databaseCombo.setEnabled(false);
    loginButton.setEnabled(false);
    cancelButton.setEnabled(false);
    SwingWorker thread = new SwingWorker()
    {
      public Object construct()
      {
        try
        {
          CachingDatabaseAdaptor adaptor = getDatabaseAdaptor();
          synchronized(adaptor)
          {
            try
            {
              adaptor.getConnection(getUserID(), getPassword()).close();
              result = OK;
              MPSBrowserView.LOGINRESULT = OK;
              setVisible(false);
            }
            catch(java.sql.SQLException ex)
            {
              Logger.getLogger("global").log( Level.SEVERE, "Error closing the connection to the database.", ex);
              throw new org.csstudio.mps.sns.tools.database.DatabaseException("Exception closing the connection to the database.", adaptor, ex);
            }
            catch(org.csstudio.mps.sns.tools.database.DatabaseException ex)
            {
              //Invalid login. Inform user.
              showMessage(ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
              ex.printStackTrace();
            }
          }
        }
        finally
        {
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              setCursor(Cursor.getDefaultCursor());
              userIDField.setEnabled(true);
              passwordField.setEnabled(true);
              databaseCombo.setEnabled(true);
              loginButton.setEnabled(true);
              cancelButton.setEnabled(true);
            }
          });
        }
        return null;
      }
    };
    thread.start();
  }
  
  /**
   * Called when the cancel button is clicked. This method sets the result 
   * property to <CODE>CANCEL</CODE> and exits the dialog.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void cancelButton_actionPerformed(ActionEvent e)
  {
	  result = CANCEL;
      MPSBrowserView.LOGINRESULT = CANCEL;
	  setVisible(false);
    //System.exit(0);
  }

  /**
   * Gets the value in the user ID field.
   *
   * @return The value entered in the userID field.
   */
  public String getUserID()
  {
    return userIDField.getText().trim();
  }

  /**
   * Sets the value in the user ID field.
   *
   * @param userID The value to display in the user ID field.
   */
  public void setUserID(String userID)
  {
    userIDField.setText(userID);
  }

  /**
   * Gets the value in the password field.
   *
   * @return The value entered in the password field.
   */
  public String getPassword()
  {
    return String.valueOf(passwordField.getPassword()).trim();
  }

  /**
   * Called when the dialog is shown. This method sets the focus for the dialog.
   * if there is no value in the user ID field it is given the focus. If there
   * is a value in the user ID field, the password field is given the focus.
   *
   * @param e The <CODE>ComponentEvent</CODE> that caused the invocation of this method.
   */
  void this_componentShown(ComponentEvent e)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        if(userIDField.getText().equals(""))
          userIDField.requestFocus();
        else
          passwordField.requestFocus();
      }
    });
  }

  /**
   * Returns the <CODE>CachingDatabaseAdapto</CODE> selected in the database 
   * drop down list.
   *
   * @return the <CODE>CachingDatabaseAdaptor</CODE> selected by the user.
   */
  public String getDatabase()
  {
    return databaseCombo.getSelectedItem().toString();
  }

  /**
   * Sets the item selected in the database combo.
   *
   * @param database The <CODE>DatabaseAdaptor</CODE> to select in the database combo box.
   */
  public void setDatabase(DatabaseAdaptor database)
  {
    databaseCombo.setSelectedItem(database);
  }

  /**
   * Uses <CODE>SwingUtilities.invokeLater</CODE> to safely display a message in
   * a <CODE>JOptionPane</CODE>.
   * 
   * @param message The message to display.
   * @param title The title for the <CODE>JOptionPane</CODE>.
   * @param messageType The type of message, such as <CODE>JOptionPane.ERROR_MESSAGE</CODE>.
   */
  protected void showMessage(final Object message, final String title, final int messageType)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        JOptionPane.showMessageDialog(LoginDialog.this, message, title, messageType);
      }
    });
  }

  /**
   * Used to determine if the user closed the dialog by clicking the login 
   * button, or by canceling.
   * 
   * @return <CODE>OK</CODE> if the user logged in, <CODE>CANCEL</CODE> if the user canceled the login.
   */
  public int getResult()
  {
    return result;
  }
  
  /**
   * Gets the <CODE>CachingDatabaseAdaptor</CODE> to use to connect to the database.
   * 
   * @return The <CODE>CachingDatabaseAdaptor</CODE> to use to connect to the database.
   */
  public CachingDatabaseAdaptor getDatabaseAdaptor()
  {
    return (CachingDatabaseAdaptor)databaseCombo.getSelectedItem();
  }

  /**
   * Centers the dialog on the screen.
   */
  public void center()
  {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension dialogSize = getSize();
    if(dialogSize.width > screenSize.width || dialogSize.height > screenSize.height)
    {
      if(dialogSize.width > screenSize.width)
        dialogSize.width = screenSize.width;
      if(dialogSize.height > screenSize.height)
        dialogSize.height = screenSize.height;
      setSize(dialogSize);
    }
    int x = (screenSize.width - dialogSize.width) / 2;
    int y = (screenSize.height - dialogSize.height) / 2;
    setLocation(x, y);
  }
  
  /**
   * Adds the given <CODE>CachingDatabaseAdaptor</CODE> to the database combo box. This 
   * adaptor must already be set up and ready to connect.
   * 
   * @param databaseAdaptor The preconfigured <CODE>CachingDatabaseAdaptor</CODE> to use to login.
   */
  public void addDatabaseAdaptor(CachingDatabaseAdaptor databaseAdaptor)
  {
    databaseCombo.addItem(databaseAdaptor);
  }
}