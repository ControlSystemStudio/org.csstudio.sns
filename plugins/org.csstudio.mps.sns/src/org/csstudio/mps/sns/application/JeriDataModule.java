package org.csstudio.mps.sns.application;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import javax.swing.*;

/**
 * Provides a parent class for all classes that act as a data module for an 
 * interface. This class incorporates many of the common operations needed by 
 * such a class, including the management of a progress bar.
 * 
 * @author Chris Fowlkes
 */
public class JeriDataModule 
{
  /**
   * Holds the <CODE>JProgressBar</CODE> used to track progress.
   */
  private JProgressBar progressBar;
  /**
   * Holds the <CODE>JLabel</CODE> used to relay status message to the user.
   */
  private JLabel messageLabel;  
  /**
   * Holds the <CODE>DataSource</CODE> used by the class to connect to the 
   * database.
   */
  private DataSource dataSource;
  /**
   * Holds the <CODE>Connection</CODE> to the database used to reconcile all
   * changes and retrieve new data.
   */
  private Connection oracleConnection;
  /**
   * Flag used to determine if the import operation has been canceled.
   */
  private boolean canceled;
  /**
   * Holds the fetch size used by the queries.
   */
  final public static int FETCH_SIZE = 750;

  /**
   * Creates a new <CODE>JeriDataModule</CODE>.
   */
  public JeriDataModule()
  {
  }

  /**
   * Sets the text that appears in the message label.
   * 
   * @param newMessageText The text to put in the message <CODE>JLabel</CODE>.
   */
  protected void setMessage(final String newMessageText)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        JLabel messageLabel = getMessageLabel();
        if(messageLabel != null)
          messageLabel.setText(newMessageText);
      }
    });
  }

  /**
   * Sets the indeterminate state of the <CODE>JProgressBar</CODE>.
   * 
   * @param newProgressIndeterminate Pass as <CODE>true</CODE> to put the <CODE>JProgressBar</CODE> into an indeterminate state, <CODE>false</CODE> otherwise.
   */
  protected void setProgressIndeterminate(final boolean newProgressIndeterminate)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        JProgressBar progressBar = getProgressBar();
        if(progressBar != null)
          progressBar.setIndeterminate(newProgressIndeterminate);
      }
    });
  }

  /**
   * Sets the maximum value of the <CODE>JProgressBar</CODE>.
   * 
   * @param newProgressMaximum The new maximum value for the <CODE>JProgressBar</CODE>.
   */
  protected void setProgressMaximum(final int newProgressMaximum)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        JProgressBar progressBar = getProgressBar();
        if(progressBar != null)
          progressBar.setMaximum(newProgressMaximum);
      }
    });
  }

  /**
   * Sets the value of the <CODE>JProgressBar</CODE>.
   * 
   * @param newProgressValue The new value of the <CODE>JProgressBar</CODE>.
   */
  protected void setProgressValue(final int newProgressValue)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        JProgressBar progressBar = getProgressBar();
        if(progressBar != null)
          progressBar.setValue(newProgressValue);
      }
    });
  }

  /**
   * Gets the <CODE>JProgressBar</CODE> used to track progress of operations.
   * 
   * @return The <CODE>JProgressBar</CODE> used to relay progress to the user.
   */
  public JProgressBar getProgressBar()
  {
    return progressBar;
  }

  /**
   * Sets the <CODE>JProgressBar</CODE> used to track progress of operations.
   * 
   * @param progressBar The <CODE>JProgressBar</CODE> used to relay progress to the user.
   */
  public void setProgressBar(JProgressBar progressBar)
  {
    this.progressBar = progressBar;
  }

  /**
   * Gets the <CODE>JLabel</CODE> used to relay status messages.
   * 
   * @return The <CODE>JProgressBar</CODE> used to relay status messages to the user.
   */
  public JLabel getMessageLabel()
  {
    return messageLabel;
  }

  /**
   * Gets the <CODE>JLabel</CODE> used to relay status messages.
   * 
   * @param messageLabel The <CODE>JProgressBar</CODE> used to relay status messages to the user.
   */
  public void setMessageLabel(JLabel messageLabel)
  {
    this.messageLabel = messageLabel;
  }
  
  /**
   * Sets the <CODE>DataSource</CODE> for the interface.
   * 
   * @param newDataSource The <CODE>DataSource</CODE> the interface is to use to connect to the database.
   */
  protected void setDataSource(DataSource newDataSource)
  {
    dataSource = newDataSource;
  }
  
  /**
   * Gets the value passed into the <CODE>setDataSource</CODE> property.
   * 
   * @return The <CODE>DataSource</CODE> used to connect to the database.
   */
  protected DataSource getDataSource()
  {
    return dataSource;
  }

  /**
   * Gets the <CODE>Connection</CODE> used to connect to the database. If a 
   * <CODE>Connection</CODE> was passed into the <CODE>setConnection</CODE>
   * method, it will be returned. Otherwise, if a <CODE>DataSource</CODE> was 
   * passed into the <CODE>setDataSource</CODE> method, this method will call
   * <CODE>getConnection()</CODE> on it and return the results. If neither
   * property has been set, this method will return <CODE>null</CODE>.
   * 
   * @return The <CODE>Connection</CODE> used to connect to the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  protected Connection getConnection() throws java.sql.SQLException
  {
    if(oracleConnection != null)
      return oracleConnection;
    else
    {
      DataSource dataSource = getDataSource();
      if(dataSource != null)
        return dataSource.getConnection();
      else
        return null;
    }
  }

  /**
   * Sets the <CODE>Connection</CODE> to use to connect to the database. This 
   * will override the value passed into <CODE>setDataSource</CODE> if passed a 
   * non-null value.
   * 
   * @param oracleConnection The <CODE>Connection</CODE> to use to connect to the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  protected void setConnection(Connection oracleConnection) throws java.sql.SQLException
  {
    this.oracleConnection = oracleConnection;
  }

  /**
   * Resets the progress bar and clears the status label.
   */
  protected void clearProgress()
  {
    setMessage(" ");
    setProgressValue(0);
    setProgressIndeterminate(false);
  }

  /**
   * Runs the given query and returns the integer value returned by it. The
   * query needs to return one integer as a result.
   * 
   * @param countQuery The query that gets a record count.
   * @return The integer returned by the query.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  protected int runCountQuery(PreparedStatement countQuery) throws java.sql.SQLException
  {
    ResultSet result = countQuery.executeQuery();
    try
    {
      result.next();
      return result.getInt(1);
    }
    finally
    {
      result.close();
    }
  }

  /**
   * Determines if the import operation has been canceled.
   * 
   * @return The value passed into the <CODE>setCanceled</CODE> method.
   */
  public boolean isCanceled()
  {
    return canceled;
  }

  /**
   * Sets the value of the canceled property. value is checked periodically
   * during long operations so they can be aborted if canceled.
   * 
   * @param canceled Pass as <CODE>true</CODE> to cancel any ongoing operations.
   */
  public void setCanceled(boolean canceled)
  {
    this.canceled = canceled;
  }
}