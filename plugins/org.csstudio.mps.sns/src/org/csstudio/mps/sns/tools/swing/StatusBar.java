package org.csstudio.mps.sns.tools.swing;

import com.cosylab.gui.components.ProgressEvent;
import com.cosylab.gui.components.ProgressListener;

import org.csstudio.mps.sns.application.Application;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

/**
 * A control for displaying progress and status messages.
 * 
 * @author Chris Fowlkes
 */
public class StatusBar extends JPanel implements ProgressListener
{
  private BorderLayout layout = new BorderLayout();
  private JLabel label = new JLabel();
  private JProgressBar progressBar = new JProgressBar();

  /**
   * Creates a new <CODE>StatusBar</CODE>.
   */
  public StatusBar()
  {
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Component initialization.
   * 
   * @throws Exception Thrown on SQL error.
   */
  private void jbInit() throws Exception
  {
    this.setLayout(layout);
    this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    label.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    this.add(label, BorderLayout.CENTER);
    this.add(progressBar, BorderLayout.EAST);
  }

  /**
   * Sets the text displayed in the component.
   * 
   * @param text The text to display.
   */
  public void setText(String text)
  {
    label.setText(text);
  }
  
  /**
   * Gets the text displayed.
   * 
   * @return The text being displayed.
   */
  public String getText()
  {
    return label.getText();
  }

  /**
   * Called when progress is made on a task.
   * 
   * @param progressEvent The <CODE>ProgressEvent</CODE> that caused the invocation of this method.
   */
  public void progress(ProgressEvent progressEvent)
  {
    setText(progressEvent.getStatus());
    int max = progressEvent.getTotal();
    if(max >= 0)
    {
      setIndeterminate(false);
      setMaximum(max);
    }
    else
      setIndeterminate(true);
    setValue(progressEvent.getCurrent());
  }

  /**
   * Called when the task is complete. If the <CODE>ProgressEvent</CODE> as a 
   * value in the status property, that status is displayed in a dialog.
   * 
   * @param progressEvent The <CODE>ProgressEvent</CODE> that caused the invocation of this method.
   */
  public void taskComplete(ProgressEvent progressEvent)
  {
    String message = progressEvent.getStatus();
    if(!(message == null || message.trim().equals("")))
      JOptionPane.showMessageDialog(Application.getActiveWindow(), message);
    setText(" ");
    setIndeterminate(false);
    setMaximum(0);
    setValue(0);
  }

  /**
   * Called when the task is interrupted. If the <CODE>ProgressEvent</CODE> as a 
   * value in the status property, that status is displayed in an error dialog.
   * 
   * @param progressEvent The <CODE>ProgressEvent</CODE> that caused the invocation of this method.
   */
  public void taskInterruped(ProgressEvent progressEvent)
  {
    String message = progressEvent.getStatus();
    if(!(message == null || message.trim().equals("")))
      Application.displayError("Error", message);              
    setText(" ");
    setIndeterminate(false);
    setMaximum(0);
    setValue(0);
  }

  /**
   * Called when the task is started.
   * 
   * @param progressEvent The <CODE>ProgressEvent</CODE> that caused the invocation of this method.
   */
  public void taskStarted(ProgressEvent progressEvent)
  {
    setText(progressEvent.getStatus());
    int max = progressEvent.getTotal();
    if(max >= 0)
    {
      setIndeterminate(false);
      setMaximum(max);
    }
    else
      setIndeterminate(true);
    setValue(progressEvent.getCurrent());
  }
  
  /**
   * Sets the indeterminate property on the progress bar.
   * 
   * @param indeterminate Pass as <CODE>true</CODE> if the max value hasn't been determined.
   */
  public void setIndeterminate(boolean indeterminate)
  {
    progressBar.setIndeterminate(indeterminate);
  }
  
  /**
   * Gets the indeterminate property on the progress bar.
   * 
   * @return <CODE>true</CODE> if the max value hasn't been determined, <CODE>false</CODE> otherwise.
   */
  public boolean isIndeterminate()
  {
    return progressBar.isIndeterminate();
  }
  
  /**
   * Sets the maximum property on the progress bar.
   * 
   * @param maximum The maximum value for the progress bar.
   */
  public void setMaximum(int maximum)
  {
    progressBar.setMaximum(maximum);
  }
  
  /**
   * Gets the maximum property on the progress bar.
   * 
   * @return The maximum value for the progress bar.
   */
  public int getMaximum()
  {
    return progressBar.getMaximum();
  }
  
  /**
   * Sets the value for the progress bar.
   * 
   * @param value The value for the progress bar.
   */
  public void setValue(int value)
  {
    progressBar.setValue(value);
  }
  
  /**
   * Gets the value for the progress bar.
   * 
   * @return The value for the progress bar.
   */
  public int getValue()
  {
    return progressBar.getValue();
  }
}
