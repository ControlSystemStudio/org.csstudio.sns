package org.csstudio.mps.sns.application;
import java.awt.*;
import javax.swing.JDialog;
import java.util.*;
import java.awt.event.*;

/**
 * This class holds functionality shared by all dialogs in the application. This
 * includes saving the size of the dialog in the application's property file, so
 * that the next time they are shown the user's size will remain.
 * 
 * @author Chris Fowlkes
 */
public class JeriDialog extends JDialog 
{
  /**
   * Holds the user's saved properties for the application.
   */
  private Properties applicationProperties;

  /**
   * Constructs a new, non-modal <CODE>JeriDialog</CODE>.
   */
  public JeriDialog()
  {
    this((Frame)null, "", false);
  }

  /**
   * Constructs a new <CODE>JeriDialog</CODE>.
   * 
   * @param parent The parent window for the dialog.
   * @param title The title that will appear in the title bar of the dialog.
   * @param modal Pass as <CODE>true</CODE> to make the dialog modal, <CODE>false</CODE> otherwise.
   */
  public JeriDialog(Frame parent, String title, boolean modal)
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
   * Constructs a new <CODE>JeriDialog</CODE>.
   * 
   * @param parent The parent window for the dialog.
   * @param title The title that will appear in the title bar of the dialog.
   * @param modal Pass as <CODE>true</CODE> to make the dialog modal, <CODE>false</CODE> otherwise.
   */
  public JeriDialog(Dialog parent, String title, boolean modal)
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
   * @throws java.lang.Exception Thron on initialization error.
   */
  private void jbInit() throws Exception
  {
    this.addComponentListener(new ComponentAdapter()
      {
        public void componentResized(ComponentEvent e)
        {
          this_componentResized(e);
        }
      });
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
    }//if(dialogSize.width > screenSize.width || dialogSize.height > screenSize.height)
    int x = (screenSize.width - dialogSize.width) / 2;
    int y = (screenSize.height - dialogSize.height) / 2;
    setLocation(x, y);
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
    if(settings != null)
    {
      String windowName = getClass().getName();
      settings.setProperty(windowName + ".width", String.valueOf(newSize.width));
      settings.setProperty(windowName + ".height", String.valueOf(newSize.height));
    }//if(settings != null)
  }
}