package org.csstudio.mps.sns.tools.swing;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Helper class that shows a message in a <CODE>JOptionPane</CODE> that scrolls
 * if too large.
 */
public class ScrollableOptionPane 
{
  /**
   * Can't create instances of this class.
   */
  private ScrollableOptionPane()
  {
  }
  
  /**
   * Shows a <CODE>JOptionPane</CODE> with the given message in a 
   * <CODE>JScrollPane</CODE>.
   * 
   * @param parentComponent The parent for the <CODE>JOptionPane</CODE>.
   * @param message The message to display.
   * @param title The title for the <CODE>JOptionPane</CODE>.
   * @param optionType The <CODE>JOptionPane</CODE> option type for the confirm dialog.
   * @return The value returned by <CODE>JOptionPane.showConfirmDialog</CODE>.
   */
  static public int showConfirmDialog(Component parentComponent, Object message, String title, int optionType)
  {
    JScrollPane scrollPane = createScrollPane(message.toString());
    return JOptionPane.showConfirmDialog(parentComponent, scrollPane, title, optionType);
  }

  /**
   * Shows a <CODE>JOptionPane</CODE> with the given message in a 
   * <CODE>JScrollPane</CODE>.
   * 
   * @param parentComponent The parent for the <CODE>JOptionPane</CODE>.
   * @param message The message to display.
   * @param title The title for the <CODE>JOptionPane</CODE>.
   * @param messageType The <CODE>JOptionPane</CODE> message type.
   */
  static public void showMessageDialog(Component parentComponent, Object message, String title, int messageType)
  {
    JScrollPane scrollPane = createScrollPane(message.toString());
    JOptionPane.showMessageDialog(parentComponent, scrollPane, title, messageType);
  }
  
  /**
   * Wraps the given text in a <CODE>JScrollPane</CODE>.
   * 
   * @param messageText The text to show in the <CODE>JScrollPane</CODE>.
   * @return The <CODE>JScrollPane</CODE> with the given text inside.
   */
  static private JScrollPane createScrollPane(String messageText)
  {
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    JTextPane textComponent = new JTextPane();
    textComponent.setEditorKit(new HTMLEditorKit());
    textComponent.setEditable(false);
    textComponent.setOpaque(false);
    textComponent.setBorder(BorderFactory.createEmptyBorder());
    textComponent.setText(messageText);
    scrollPane.getViewport().add(textComponent, null);
    Dimension currentSize = scrollPane.getPreferredSize();
    int width = Math.min(400, currentSize.width);
    int height = Math.min(400, currentSize.height);
    scrollPane.setPreferredSize(new Dimension(width, height));
    textComponent.setCaretPosition(0);
    return scrollPane;
  }
}