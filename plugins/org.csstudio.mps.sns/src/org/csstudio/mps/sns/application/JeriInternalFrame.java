package org.csstudio.mps.sns.application;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.util.Properties;

import javax.sql.DataSource;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.csstudio.mps.sns.MainFrame;

/**
 * Provides a super class to all of the internal frames in the application for 
 * placing common functions and properties.
 *
 * @author Chris Fowlkes
 */
public class JeriInternalFrame extends JInternalFrame 
{
  /**
   * Holds the user's saved properties for the application.
   */
  private Properties applicationProperties;
  /**
   * Holds the <CODE>DataSource</CODE> for the window. This is used to make 
   * database connections.
   */
  private DataSource connectionPool;
  /**
   * Holds a reference to the main window of the application.
   */
  private MainFrame mainWindow;

  /**
   * Creates a new <CODE>JeriInternalFrame</CODE>. This constructor creates an 
   * instance of the window that has no title and is resizable, closeable, 
   * maximizable, and iconifiable.
   */
  public JeriInternalFrame()
  {
    super("MPS Frame", true, true, true, true);
    try
    {
      System.out.println("JeriInternalFrame");
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
   * @throws java.lang.Exception Thrown on error.
   */
  private void jbInit() throws Exception
  {
    this.addComponentListener(new java.awt.event.ComponentAdapter()
      {
        public void componentResized(ComponentEvent e)
        {
          this_componentResized(e);
        }
      });
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
   * Sets the <CODE>DataSource</CODE> used by the window to connect to the 
   * database.
   *
   * @param connectionPool The <CODE>DataSource</CODE> to use to connect to the database.
   * @throws java.sql.SQLException Not thrown in this method, but can be thrown in subclasses.
   */
  public void setDataSource(DataSource connectionPool) throws java.sql.SQLException
  {
    this.connectionPool = connectionPool;
  }
  
  /**
   * Sets the properties stored in the applications properties file. These are 
   * read in before the user logs in by the main application class and passed to 
   * this class as an instance of <CODE>Properties</CODE>. This method also 
   * restores the size of the window to the sizestored in the 
   * <CODE>Properties</CODE>.
   * 
   * @param applicationProperties The applicationProperties application settings.
   */
  public void setApplicationProperties(Properties applicationProperties)
  {
    this.applicationProperties = applicationProperties;
    String windowName = getClass().getName();
    String width = String.valueOf(getWidth());
    width = applicationProperties.getProperty(windowName + ".width", width);
    String height = String.valueOf(getHeight());
    height = applicationProperties.getProperty(windowName + ".height", height);
    setSize(Integer.parseInt(width), Integer.parseInt(height));
  }

  /**
   * Gets the properties stored in the applications properties file.
   *
   * @return The settings for the application.
   */
  public Properties getApplicationProperties()
  {
    return applicationProperties;
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
    String className = getClass().getName();
    if(settings != null)
    {
      settings.setProperty(className + ".width", String.valueOf(newSize.width));
      settings.setProperty(className + ".height", String.valueOf(newSize.height));
    }//if(settings != null)
  }

  /**
   * Gives the window a reference to the main window. This is needed to launch 
   * the signal table browser with the generated signals. If this is not 
   * provided it will be located.
   * 
   * @param mainWindow The main window of the application.
   */
  public void setMainWindow(MainFrame mainWindow)
  {
    this.mainWindow = mainWindow;
  }

  /**
   * Gets a reference to the main window of the application.
   * 
   * @return The main window of the application.
   */
  public MainFrame getMainWindow()
  {
    Container parent = this;
    while(mainWindow == null && parent != null)
    {
      parent = parent.getParent();
      if(parent instanceof MainFrame)
        mainWindow = (MainFrame)parent;
    }//while(mainFrame == null)
    return mainWindow;
  }

  /**
   * Shows the given error message in a thread safe manner.
   * 
   * @param title The title of the message.
   * @param prefix The prefix text of the message.
   * @param error The <CODE>Exception</CODE>.
   */
  protected void showErrorMessage(final String title, final String prefix, final Exception error)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        Application.displayError(title, prefix, error);
      }
    });
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
        JOptionPane.showMessageDialog(JeriInternalFrame.this, message, title, messageType);
      }
    });
  }
}