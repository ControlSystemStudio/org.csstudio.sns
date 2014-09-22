package org.csstudio.mps.sns.application;
import com.cosylab.gui.components.ProgressEvent;
import com.cosylab.gui.components.ProgressListener;

import org.csstudio.mps.sns.tools.database.CachingDatabaseAdaptor;

import org.csstudio.mps.sns.tools.database.DatabaseAdaptor;

import java.beans.IndexedPropertyChangeEvent;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import java.util.List;

import javax.sql.DataSource;

import javax.swing.SwingUtilities;

/**
 * Holds the data and logic for a module within Jeri. Subclasses should contain
 * no user interface code and makes no assumptions about the interface.
 * 
 * @author Chris Fowlkes
 */
abstract public class JeriModule implements ProgressListener 
{    
  /**
   * Holds the title of the interface.
   */
  private String title;
//  private transient ArrayList propertyChangeListeners = new ArrayList(2);
  private transient ArrayList<ProgressListener> progressListeners = new ArrayList(2);
  /**
   * Holds the <CODE>DatabaseAdaptor</CODE> used by the module to get to the 
   * database.
   */
  private CachingDatabaseAdaptor databaseAdaptor;
  /**
   * Holds the <CODE>Connection</CODE> to the database used to reconcile all
   * changes and retrieve new data.
   */
  private Connection oracleConnection;
  private boolean threadSafe = false;
  private PropertyChangeSupport propertyChangeSupport;

  /**
   * Subclasses need to implement this method for saving the data to a URL.
   * 
   * @param url The URL to which the data should be saved.
   */
  abstract public void saveAs(URL url);
  
  /**
   * This method should return the title of the module. The title is a name for
   * the module, that might appear in the title bar of the window displaying it.
   * 
   * @return The title of the module.
   */
  public String getTitle()
  {
    return title;
  }
  
  /**
   * Sets the value of the title property.
   */
  public void setTitle(String title)
  {
    String oldValue = this.title;
    this.title = title;
    firePropertyChange(new PropertyChangeEvent(this, "title", oldValue, title));
  }

  /**
   * Creates the instance of <CODE>PropertyChangeSupport</CODE> used by the 
   * class if it has not yet been created.
   */
  private void createPropertyChangeSupport()
  {
    if(propertyChangeSupport == null)
      propertyChangeSupport = new PropertyChangeSupport(this);
  }
  
  /**
   * Adds the given <CODE>PropertyChangeListener</CODE> to the module.
   * 
   * @param l The <CODE>PropertyChangeListener</CODE> to add to the module.
   */
  public synchronized void addPropertyChangeListener(PropertyChangeListener l)
  {
    createPropertyChangeSupport();
    propertyChangeSupport.addPropertyChangeListener(l);
//    if (!propertyChangeListeners.contains(l))
//    {
//      propertyChangeListeners.add(l);
//    }
  }

  /**
   * Removes the given <CODE>PropertyChangeListener</CODE> from the module.
   * 
   * @param l The <CODE>PropertyChangeListener</CODE> to remove from the module.
   */
  public synchronized void removePropertyChangeListener(PropertyChangeListener l)
  {
    createPropertyChangeSupport();
    propertyChangeSupport.removePropertyChangeListener(l);
//    propertyChangeListeners.remove(l);
  }

  /**
   * Creates a <CODE>PropertyChangeEvent</CODE> and fires it.
   * 
   * @param propertyName The name of the property changed.
   * @param oldValue The value of the property before the change.
   * @param newValue The new value of the property.
   */
  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
  {
    firePropertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
  }
  
  /**
   * Fires the given property change event. This method should be called if the 
   * title property changes.
   * 
   * @param e The <CODE>PropertyChangeEvent</CODE> to fire.
   */
  protected void firePropertyChange(final PropertyChangeEvent e)
  {
    createPropertyChangeSupport();
    if((! isThreadSafe()) || SwingUtilities.isEventDispatchThread())
      propertyChangeSupport.firePropertyChange(e);
    else
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          propertyChangeSupport.firePropertyChange(e);
        }
      });
  }

  /**
   * Creates a <CODE>PropertyChangeEvent</CODE> and fires it.
   * 
   * @param propertyName The name of the property changed.
   * @param index The index of the property.
   * @param oldValue The value of the property before the change.
   * @param newValue The new value of the property.
   */
  protected void fireIndexedPropertyChange(final String propertyName, final int index, final Object oldValue, final Object newValue)
  {
    createPropertyChangeSupport();
    if((! isThreadSafe()) || SwingUtilities.isEventDispatchThread())
      propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    else
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
        }
      });
  }

//  /**
//   * Handles the firing of the actual <CODE>PropertyChangeEvent</CODE>.
//   * 
//   * @param e The <CODE>PropertyChangeEvent</CODE> to fire.
//   */
//  private void firePropertyChangeEvent(PropertyChangeEvent e)
//  {
//    List listeners = (List)propertyChangeListeners.clone();
//    int count = listeners.size();
//
//    for (int i = 0;i < count;i++)
//    {
//      ((PropertyChangeListener)listeners.get(i)).propertyChange(e);
//    }
//  }

  /**
   * Fires a progress change event. 
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   */
  protected void fireProgress(final ProgressEvent e)
  {
    fireProgress(e, getProgressListeners());
  }
  
  /**
   * Fires a progress change event. 
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> monitoring the method.
   */
  protected void fireProgress(final ProgressEvent e, final ArrayList<ProgressListener> progressListeners)
  {
    if((! isThreadSafe()) || SwingUtilities.isEventDispatchThread())
      fireProgressEvent(e, progressListeners);
    else
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          fireProgressEvent(e, progressListeners);
        }
      });
  }
  
  /**
   * Fires the actual <CODE>ProgressEvent</CODE>.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> monitoring the method.
   */
  private void fireProgressEvent(ProgressEvent e, ArrayList<ProgressListener> progressListeners)
  {
    List<ProgressListener> listeners = (List)progressListeners.clone();
    int count = listeners.size();

    for (int i = 0;i < count;i++)
    {
      listeners.get(i).progress(e);
    }
  }

  /**
   * Fires a task started event. This is done in a thread safe manner using
   * <CODE>SwingUtilities.invokeLater</CODE>.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   */
  protected void fireTaskStarted(final ProgressEvent e)
  {
    fireTaskStarted(e, getProgressListeners());
  }
  
  /**
   * Fires a task started event. This is done in a thread safe manner using
   * <CODE>SwingUtilities.invokeLater</CODE>.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> monitoring the method.
   */
  protected void fireTaskStarted(final ProgressEvent e, final ArrayList<ProgressListener> progressListeners)
  {
    if((! isThreadSafe()) || SwingUtilities.isEventDispatchThread())
      fireTaskStartedEvent(e, progressListeners);
    else
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          fireTaskStartedEvent(e, progressListeners);
        }
      });
  }
  
  /**
   * Fires the actual task started event.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> monitoring the method.
   */
  private void fireTaskStartedEvent(ProgressEvent e, ArrayList<ProgressListener> progressListeners)
  {
    List<ProgressListener> listeners = (List)progressListeners.clone();
    int count = listeners.size();

    for (int i = 0;i < count;i++)
    {
      listeners.get(i).taskStarted(e);
    }
  }

  /**
   * Fires a task started event. This is done in a thread safe manner using
   * <CODE>SwingUtilities.invokeLater</CODE>.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   */
  protected void fireTaskInterrupted(final ProgressEvent e)
  {
    fireTaskInterrupted(e, getProgressListeners());
  }
  
  /**
   * Fires a task started event. This is done in a thread safe manner using
   * <CODE>SwingUtilities.invokeLater</CODE>.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> monitoring the method.
   */
  protected void fireTaskInterrupted(final ProgressEvent e, final ArrayList<ProgressListener> progressListeners)
  {
    if((! isThreadSafe()) || SwingUtilities.isEventDispatchThread())
      fireTaskInterruptedEvent(e, progressListeners);
    else
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          fireTaskInterruptedEvent(e, progressListeners);
        }
      });
  }
  
  /**
   * Fires the actual task interrupted event.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> monitoring the method.
   */
  private void fireTaskInterruptedEvent(ProgressEvent e, ArrayList<ProgressListener> progressListeners)
  {
    List<ProgressListener> listeners = (List)progressListeners.clone();
    int count = listeners.size();

    for (int i = 0;i < count;i++)
    {
      listeners.get(i).taskInterruped(e);
    }
  }

  /**
   * Fires a task started event. This is done in a thread safe manner using
   * <CODE>SwingUtilities.invokeLater</CODE>.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   */
  protected void fireTaskComplete(final ProgressEvent e)
  {
    fireTaskComplete(e, getProgressListeners());
  }

  /**
   * Fires a task started event. This is done in a thread safe manner using
   * <CODE>SwingUtilities.invokeLater</CODE>.
   * 
   * @param e The <CODE>ProgressEvent</CODE> to fire.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> monitoring the method.
   */
  protected void fireTaskComplete(final ProgressEvent e, final ArrayList<ProgressListener> progressListeners)
  {
    if((! isThreadSafe()) || SwingUtilities.isEventDispatchThread())
      fireTaskCompleteEvent(e, progressListeners);
    else
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          fireTaskCompleteEvent(e, progressListeners);
        }
      });
  }
  
  /**
   * Fires the actual task complete event.
   * 
   * @param e The task complete event to fire.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> monitoring the method.
   */
  private void fireTaskCompleteEvent(ProgressEvent e, ArrayList<ProgressListener> progressListeners)
  {
    List listeners = (List)progressListeners.clone();
    int count = listeners.size();

    for (int i = 0;i < count;i++)
    {
      ((ProgressListener)listeners.get(i)).taskComplete(e);
    }
  }

  /**
   * Adds the given <CODE>ProgressListener</CODE> to the module.
   * 
   * @param l The <CODE>ProgressListener</CODE> to add to the module.
   */
  public synchronized void addProgressListener(ProgressListener l)
  {
    if (!progressListeners.contains(l))
    {
      progressListeners.add(l);
    }
  }

  /**
   * Removes the given <CODE>ProgressListener</CODE> from the module.
   * 
   * @param l The <CODE>ProgressListener</CODE> to remove from the module.
   */
  public synchronized void removeProgressListener(ProgressListener l)
  {
    progressListeners.remove(l);
  }
  
  /**
   * Gets the instances of <CODE>ProgressListener</CODE> that have been added to
   * the module.
   * 
   * @return The instances of <CODE>ProgressListener</CODE> that have been added to the module.
   */
  protected ArrayList<ProgressListener> getProgressListeners()
  {
    return progressListeners;
  }

  /**
   * Compares two objects with the <CODE>equals</CODE> method. This method also 
   * considers two <CODE>null</CODE> objects equal as well.
   * 
   * @param object1 The first <CODE>Object</CODE> to compare.
   * @param object2 The <CODE>Object</CODE> to compare to.
   * @return <CODE>true</CODE> if the two items are equal.
   */
  public static boolean compare(Object object1, Object object2)
  {
    //Sometimes if a null is passed in as a string, we end up here...
    if((object1 == null || object2 == null) && (object1 instanceof String || object2 instanceof String))
      return compare((String)object1, (String)object2);
    if(object1 == null)
    {
      if(object2 != null)
        return false;
    }
    else
      if(! object1.equals(object2))
        return false;
    return true;
  }

  /**
   * Compares two instances of <CODE>String</CODE>. This method considers 
   * <CODE>null</CODE> and empty string to be equal and does not consider 
   * leading or trailing whitespace.
   * 
   * @param string1 The first <CODE>String</CODE> to compare.
   * @param string2 The <CODE>String</CODE> to compare to.
   * @return <CODE>true</CODE> if the two items are equal.
   */
  public static boolean compare(String string1, String string2)
  {
    if(string1 == null)
      string1 = "";
    if(string2 == null)
      string2 = "";
    return string1.trim().equals(string2.trim());
  }
  
  /**
   * Runs a sql statement that returns a single <CODE>int</CODE> value, such as
   * a count query.
   * 
   * @param sql The sql atatement to run.
   * @return The <CODE>int</CODE> returned by the statement.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  protected int findRecordCount(Connection connection, String sql) throws java.sql.SQLException
  {
    PreparedStatement query = connection.prepareStatement(sql);
    try
    {
      return findRecordCount(query);
    }
    finally
    {
      query.close();
    }
  }
  
  /**
   * Runs a sql statement that returns a single <CODE>int</CODE> value, such as
   * a count query.
   * 
   * @param query The <CODE>PreparedStatement</CODE> to run to get the record count.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  protected int findRecordCount(PreparedStatement query) throws java.sql.SQLException
  {
    int count = 0;
    ResultSet result = query.executeQuery();
    try
    {
      while(result.next())
        count += result.getInt(1);
    }
    finally
    {
      result.close();
    }
    return count;
  }

  /**
   * Called when a progress event is fired by a class to which the module is 
   * listening. The event is passed on to all instances of 
   * <CODE>ProgressListener</CODE> that are registered with the module.
   * 
   * @param p0 The <CODE>ProgressEvent</CODE> that caused the invocation of this method.
   */
  public void progress(ProgressEvent p0)
  {
    fireProgress(p0);
  }

  /**
   * Called when a progress event is fired by a class to which the module is 
   * listening. The event is passed on to all instances of 
   * <CODE>ProgressListener</CODE> that are registered with the module.
   * 
   * @param p0 The <CODE>ProgressEvent</CODE> that caused the invocation of this method.
   */
  public void taskComplete(ProgressEvent p0)
  {
    fireTaskComplete(p0);
  }

  /**
   * Called when a progress event is fired by a class to which the module is 
   * listening. The event is passed on to all instances of 
   * <CODE>ProgressListener</CODE> that are registered with the module.
   * 
   * @param p0 The <CODE>ProgressEvent</CODE> that caused the invocation of this method.
   */
  public void taskInterruped(ProgressEvent p0)
  {
    fireTaskInterrupted(p0);
  }

  /**
   * Called when a progress event is fired by a class to which the module is 
   * listening. The event is passed on to all instances of 
   * <CODE>ProgressListener</CODE> that are registered with the module.
   * 
   * @param p0 The <CODE>ProgressEvent</CODE> that caused the invocation of this method.
   */
  public void taskStarted(ProgressEvent p0)
  {
    fireTaskStarted(p0);
  }

  /**
   * Sets the <CODE>DatabaseAdaptor</CODE> for the module.
   * 
   * @param databaseAdaptor The <CODE>DatabaseAdaptor</CODE> for the module.
   */
  protected void setDatabaseAdaptor(CachingDatabaseAdaptor databaseAdaptor)
  {
    this.databaseAdaptor = databaseAdaptor;
  }
  
  /**
   * Gets the <CODE>DatabaseAdaptor</CODE> for the module, through which 
   * connections to the database can be made.
   * 
   * @return The <CODE>DatabaseAdaptor</CODE> for the module.
   */
  protected CachingDatabaseAdaptor getDatabaseAdaptor()
  {
    return this.databaseAdaptor;
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
      CachingDatabaseAdaptor adaptor = getDatabaseAdaptor();
      if(adaptor != null)
        return adaptor.getConnection();
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
   * When set to <CODE>true</CODE> all events will be fired in the event thread.
   * 
   * @param threadSafe Pass as <CODE>true</CODE> to make the module threadsafe.
   */
  public void setThreadSafe(boolean threadSafe)
  {
    this.threadSafe = threadSafe;
  }

  /**
   * Determines the value of the thread safe property.
   * 
   * @return The value of the thread safe property.
   */
  public boolean isThreadSafe()
  {
    return threadSafe;
  }
}
