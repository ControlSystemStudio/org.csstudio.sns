package org.csstudio.mps.sns.tools.swing;
import org.csstudio.mps.sns.application.JeriDialog;
import java.awt.Frame;
import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
/**
 * Provids a dialog for displaying an image.
 * 
 * @author Chris Fowlkes
 */
public class ImageDialog extends JeriDialog 
{
  private JLabel imageControl = new JLabel();

  /**
   * Creates a new <CODE>ImageDialog</CODE>.
   */
  public ImageDialog()
  {
    this(null, "", false);
  }

  /**
   * Creates a new <CODE>ImageDialog</CODE>.
   * 
   * @param modal Pass <CODE>true</CODE> for a model dialog, <CODE>false</CODE> otherwise.
   * @param title The title to appear in the dialog's title bar.
   * @param parent The parent of the dialog.
   */
  public ImageDialog(Frame parent, String title, boolean modal)
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
    this.setSize(new Dimension(400, 300));
    this.getContentPane().add(imageControl, BorderLayout.CENTER);
  }

  /**
   * Sets the image being displayed.
   * 
   * @param image The image to display.
   */
  public void setImage(ImageIcon image)
  {
    imageControl.setIcon(image);
    pack();
  }
}