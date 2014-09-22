package org.csstudio.mps.sns.application;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.Properties;
import javax.swing.SwingUtilities;

/**
 * Provides an interface for displaying a <CODE>JeriModule</CODE>. Subclasses 
 * should only provide a user interface, leaving all logic and data in the 
 * instance of <CODE>JeriModule</CODE> being displayed.
 * 
 * @author Chris Fowlkes
 */
public class JeriUserInterface extends JPanel 
{
  /**
   * Holds the <CODE>JeriModule</CODE> this interface is responsible for 
   * displaying.
   */
  private JeriModule module;
  /**
   * Holds the instance of <CODE>Properties</CODE> used to store settings.
   */
  private Properties applicationProperties;
  /**
   * The parent window of the interface as set by the developer, or as 
   * determined through a recursive search. This is either an instance of 
   * <CODE>Frame</CODE> or <CODE>Dialog</CODE>.
   */
  private Window parentWindow;
  
  /**
   * Creates a <CODE>JeriUserInterface</CODE> without an associated 
   * <CODE>JeriModule</CODE>. Subclasses electing to use this constructor need
   * to override <CODE>getModule()</CODE>, and store the instance of 
   * <CODE>JeriModule</CODE> for themselves.
   */
  public JeriUserInterface()
  {  
    this(null);
  }
  
  /**
   * Creates a <CODE>JeriUserInterface</CODE> for the given 
   * <CODE>JeriModule</CODE>. Subclasses should only provide a constructor for
   * the specific types of modules that the interface is capable of displaying.
   * 
   * @param module The <CODE>JeriModule</CODE> to be displayed in the interface.
   */
  public JeriUserInterface(JeriModule module)
  {
    this.module = module;
  }
  
  /**
   * Gets the <CODE>JeriModule</CODE> the interface is responsible for 
   * displaying.
   * 
   * @return The <CODE>JeriModule</CODE> for the interface.
   */
  public JeriModule getModule()
  {
    return module;
  }
  
  /**
   * Provides a default size for the window displaying the interface. Subclasses
   * should override this method to provide the preferred window size. By
   * default this method returns the preferred size of the interface.
   * 
   * @return The Preferred size of the window displaying the component.
   */
  public Dimension getPreferredWindowSize()
  {
    return getPreferredSize();
  }

  /**
   * Gets the instance of <CODE>Properties</CODE> used to store the settings for
   * the interface. This method will create a new, but empty, instance of 
   * <CODE>Properties</CODE> and return that if the property is not set when
   * this method is called.
   * 
   * @return The instance of <CODE>Properties</CODE> used to store settings.
   */
  public Properties getApplicationProperties()
  {
    if(applicationProperties == null)
      applicationProperties = new Properties();
    return applicationProperties;
  }

  /**
   * Sets the instance of <CODE>Properties</CODE> used to store settings for the 
   * interface. It is the responsibility of the code displaing the interface to
   * initialize this property and save the settings.
   * 
   * @param applicationProperties The <CODE>Properties</CODE> used to store the settings.
   */
  public void setApplicationProperties(Properties applicationProperties)
  {
    this.applicationProperties = applicationProperties;
  }
  
  /**
   * Sets the parent of the interface. If this property is not set, the 
   * interface will try to determine the parent, but setting the property will
   * save some time. This is used to show modal dialogs.
   * 
   * @param parentWindow The parent of the interface.
   */
  public void setParentWindow(Dialog parentWindow)
  {
    this.parentWindow = parentWindow;
  }
  
  /**
   * Sets the parent of the interface. If this property is not set, the 
   * interface will try to determine the parent, but setting the property will
   * save some time. This is used to show modal dialogs.
   * 
   * @param parentWindow The parent of the interface.
   */
  public void setParentWindow(Frame parentWindow)
  {
    this.parentWindow = parentWindow;
  }
  
  /**
   * This method returns the instance of <CODE>Frame</CODE> holding the 
   * interface. This will be an instance of <CODE>Frame</CODE>, 
   * <CODE>Dialog</CODE>, or <CODE>null</CODE> if no parent is given and one can 
   * not be located.
   * 
   * @return The parent to the interface.
   */
  public Window getParentWindow()
  {
    if(parentWindow == null)
      parentWindow = findParentWindow(this);
    return parentWindow;
  }
  
  /**
   * This method returns the instance of <CODE>Frame</CODE> holding the 
   * interface. This will be an instance of <CODE>Frame</CODE> or 
   * <CODE>null</CODE> if no parent is given and one can not be located. This 
   * method will not return a <CODE>Dialog</CODE>.
   * 
   * @return The parent to the interface.
   */
  public Frame getParentFrame()
  {
    Container parentWindow = getParentWindow();
    if(parentWindow != null && parentWindow instanceof Dialog)
      parentWindow = ((Dialog)parentWindow).getParent();
    if(parentWindow != null && parentWindow instanceof Frame)
      return (Frame)parentWindow;
    else      
      return null;
  }

  /**
   * This method searches for the instance of <CODE>Frame</CODE> holding the 
   * interface. This will be an instance of <CODE>Frame</CODE>, 
   * <CODE>Dialog</CODE>, or <CODE>null</CODE> if no parent is given and one can 
   * not be located.
   * 
   * @return The container for which to find the parent window.
   */
  protected Window findParentWindow(Container child)
  {
    if(child == null)
      return JOptionPane.getRootFrame();
    if(child instanceof Frame || child instanceof Dialog)
      return (Window)child;
    return findParentWindow(child.getParent());
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
        JOptionPane.showMessageDialog(org.csstudio.mps.sns.application.JeriUserInterface.this, message, title, messageType);
      }
    });
  }
}