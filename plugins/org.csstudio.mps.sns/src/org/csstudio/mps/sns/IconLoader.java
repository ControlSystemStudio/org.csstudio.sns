package org.csstudio.mps.sns;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

public class IconLoader 
{
  private static ImageIcon doubleLeftIcon = new ImageIcon();
  private static ImageIcon leftIcon = new ImageIcon();
  private static ImageIcon rightIcon = new ImageIcon();
  private static ImageIcon doubleRightIcon = new ImageIcon();
  private static ImageIcon tableSearchIcon = new ImageIcon();
  private static ImageIcon largeTableSearchIcon = new ImageIcon();
  /**
   * Holds the double checkmark icon.
   */
  private static ImageIcon checkAllIcon = new ImageIcon();
  /**
   * Holds the red traffic light icon.
   */
  private static ImageIcon redTrafficLightIcon = new ImageIcon();

  /**
   * This class is static and can not be instantiated.
   */
  private IconLoader()
  {
	  System.out.println("In IconLoader");
  }

  /**
   * Loads the images for the icons used on the buttons from the image files in 
   * the resources directory.
   */
  static
  {
    try
    {
      Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
      Class thisClass = Class.forName("org.csstudio.mps.sns.IconLoader");
      doubleLeftIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Begin.gif")));
      leftIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Left.gif")));
      rightIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Right.gif")));
      doubleRightIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/End.gif")));
      tableSearchIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/SearchRow.gif")));
      largeTableSearchIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/RowSearch.gif")));
      checkAllIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/CheckAll.gif")));
      redTrafficLightIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/TrafficRed.gif")));
    }
    catch(java.lang.Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  /**
   * Gets the double left icon.
   * 
   * @return The double left icon.
   */
  public static ImageIcon getDoubleLeftIcon()
  {
    return doubleLeftIcon;
  }
  
  /**
   * Gets the left arrow icon.
   * 
   * @return The left arrow icon.
   */
  public static ImageIcon getLeftIcon()
  {
    return leftIcon;
  }

  /**
   * Gets the right arrow icon.
   * 
   * @return The right arrow icon.
   */
  public static ImageIcon getRightIcon()
  {
    return rightIcon;
  }
  
  /**
   * Gets the double right arrow icon.
   * 
   * @return The double right arrow icon.
   */
  public static ImageIcon getDoubleRightIcon()
  {
    return doubleRightIcon;
  }
  
  /**
   * Gets the table search icon.
   * 
   * @return The table search icon.
   */
  public static ImageIcon getTableSearchIcon()
  {
    return tableSearchIcon;
  }
  
  /**
   * Gets the large version of the table search icon.
   * 
   * @return The large version of the table search icon.
   */
  public static ImageIcon getLargeTableSearchIcon()
  {
    return largeTableSearchIcon;
  }
  
  /**
   * Gets the double check icon.
   * 
   * @return The double check icon.
   */
  public static ImageIcon getCheckAllIcon()
  {
    return checkAllIcon;
  }
  
  /**
   * Gets the red traffic light icon.
   * 
   * @return The red traffic light icon.
   */
  public static ImageIcon getRedTrafficLightIcon()
  {
    return redTrafficLightIcon;
  }
}