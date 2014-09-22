package org.csstudio.mps.sns.tools.data;

import com.cosylab.gui.components.ProgressEvent;
import java.io.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import java.awt.datatransfer.*;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.tools.data.Signal;
import org.csstudio.mps.sns.tools.database.CachingDatabaseAdaptor;
import org.csstudio.mps.sns.tools.database.DatabaseAdaptor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.math.BigDecimal;

import java.sql.SQLException;

import java.sql.Statement;

import java.text.SimpleDateFormat;

import javax.swing.JProgressBar;

/**
 * Provides a class to hold the data in the ARCH_REQ and SGNL_REC_ARCH tables in 
 * the database.
 * 
 * @author Chris Fowlkes
 */
public class ArchiveRequest extends RDBData implements Transferable, Cloneable 
{
  /**
   * Holds the Name of the file that contains the <CODE>ArchiveRequest</CODE>.
   */
  private String fileName;
  /**
   * Holds the instances of <CODE>Signal</CODE> in the 
   * <CODE>ArchiveRequest</CODE>.
   */
  private ArrayList signals = new ArrayList();
  /**
   * Holds the <CODE>DataFlavor</CODE> used to transfer a single instance of 
   * <CODE>ArchiveRequest</CODE> via drag and drop.
   */
  final static public DataFlavor ARCHIVE_REQUEST_FLAVOR = new DataFlavor(org.csstudio.mps.sns.tools.data.ArchiveRequest.class, "ArchiveRequest");
  /**
   * Holds the location of the request file.
   */
  private String fileLocation;
  /**
   * Holds the name of the RDB column that holds the value for the file name 
   * property.
   */
  final static public String FILE_NAME_COLUMN = "ARCH_REQ_FILE_NM";
  /**
   * Holds the name of the RDB table that holds the value for the class.
   */
  final static public String ARCHIVE_REQUEST_TABLE_NAME = "ARCH_REQ";
  /**
   * Holds the name of the schema for the RDB table that holds the value for the 
   * class.
   */
  final static public String ARCHIVE_REQUEST_SCHEMA_NAME = "EPICS";

  /**
   * Creates a new <CODE>ArchiveRequest</CODE>.
   */
  public ArchiveRequest()
  {
  }

  /**
   * Creates a new <CODE>ArchiveRequest</CODE>.
   * 
   * @param fileName The name of the request file represented by this <CODE>ArchiveRequest</CODE>.
   */
  public ArchiveRequest(String fileName)
  {
    setFileName(fileName);
  }
  
  /**
   * Creates a new <CODE>ArchiveRequest</CODE>.
   * 
   * @param fileLocation The location of the file represented by the <CODE>ArchiveRequest</CODE>.
   * @param fileName The name of the file represented by this <CODE>ArchiveRequest</CODE>.
   */
  public ArchiveRequest(String fileLocation, String fileName)
  {
    this(fileName);
    setFileLocation(fileLocation);
  }
  
  /**
   * Creates a new <CODE>ArchiveRequest</CODE>.
   * 
   * @param result The <CODE>ResultSet</CODE> that contains the data for the <CODE>ArchiveRequest</CODE>.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public ArchiveRequest(ResultSet result) throws java.sql.SQLException
  {
    super(result);
  }

  /**
   * Gets the name of the file used to store the <CODE>ArchiveRequest</CODE>.
   * 
   * @return The name of the file associated with the <CODE>ArchiveRequest</CODE>.
   */
  public String getFileName()
  {
    return fileName;
  }

  /**
   * Sets the name of the file used to store the <CODE>ArchiveRequest</CODE>.
   * 
   * @param newFileName The <CODE>File</CODE> associated with the <CODE>ArchiveRequest</CODE>.
   */
  public void setFileName(String newFileName)
  {
    String oldValue = fileName;
    fileName = newFileName;
    markFieldChanged(FILE_NAME_COLUMN);
    firePropertyChange("fileName", oldValue, fileName);
  }

  /**
   * Adds the given <CODE>Signal</CODE> to the <CODE>ArchiveRequest</CODE>.
   * 
   * @param newSignal The <CODE>Signal</CODE> to add to the <CODE>ArchiveRequest</CODE>.
   */
  public void addSignal(Signal newSignal)
  {
    Signal[] oldValue = getSignals();
    signals.add(newSignal);
    firePropertyChange("signals", oldValue, getSignals());
  }
  
  /**
   * Gets the instances of <CODE>Signal</CODE> that have been added to the 
   * <CODE>ArchiveRequest</CODE>.
   * 
   * @return The instanbces of <CODE>Signal</CODE> for the <CODE>ArchiveRequest</CODE>
   */
  public Signal[] getSignals()
  {
    return (Signal[])signals.toArray(new Signal[signals.size()]);
  }

  /**
   * Gets the <CODE>Signal</CODE> with the given ID.
   * 
   * @param signalID The ID of the <CODE>Signal</CODE> to return.
   * @return The <CODE>Signal</CODE> with the given ID, or <CODE>null</CODE> of no match is found.
   */
  public Signal getSignal(String signalID)
  {
    int index = findSignalIndex(signalID);
    if(index == -1)
      return null;
    else
      return getSignalAt(index);
  }

  /**
   * Gets the index of the <CODE>Signal</CODE> with the given ID.
   * 
   * @param signalID The ID of the <CODE>Signal</CODE> to which to return the index.
   * @return The index of the <CODE>Signal</CODE> with the given ID.
   */
  public int findSignalIndex(String signalID)
  {
    int signalCount = getSignalCount();
    for(int i=0;i<signalCount;i++)
    {
      Signal currentSignal = getSignalAt(i);
      if(currentSignal.getID().equals(signalID))
        return i;
    }
    return -1;
  }

  /**
   * Gets the number of instances of <CODE>Signal</CODE> that have been added to
   * the <CODE>ArchiveRequest</CODE>.
   * 
   * @return The number of instances of <CODE>Signal</CODE> in the <CODE>ArchiveRequest</CODE>.
   */
  public int getSignalCount()
  {
    return signals.size();
  }

  /**
   * Gets the <CODE>Signal</CODE> at the given index.
   * 
   * @param index The index of the <CODE>Signal</CODE> to return.
   * @return index The <CODE>Signal</CODE> at the given index.
   */
  public Signal getSignalAt(int index)
  {
    return (Signal)signals.get(index);
  }

  /**
   * Provides the <CODE>String</CODE> representation of the 
   * <CODE>ArchiveRequest</CODE>. This is the name of the <CODE>File</CODE> 
   * associated with the it.
   * 
   * @return The name of the <CODE>File</CODE> for the <CODE>ArchiveRequest</CODE>.
   */
  public String toString()
  {
    String fileName = getFileName();
    if(fileName == null)
      return "";
    else
      return fileName;
  }

  /**
   * Removes all instances of <CODE>Signal</CODE> from the 
   * <CODE>ArchiveRequest</CODE>.
   */
  public void removeAllSignals()
  {
    Signal[] oldValue = getSignals();
    signals.clear();
    firePropertyChange("signals", oldValue, getSignals());
  }

  /**
   * Removes the given <CODE>Signal</CODE> from the <CODE>ArchiveRequest</CODE>.
   * 
   * @param signalToRemove The <CODE>Signal</CODE> to remove.
   * @return <CODE>true</CODE> if the <CODE>ArchiveRequest</CODE> contained the <CODE>Signal</CODE>, <CODE>false</CODE> if not.
   */
  public boolean removeSignal(Signal signalToRemove)
  {
    Signal[] oldValue = getSignals();
    boolean removed = signals.remove(signalToRemove);
    if(removed)
      firePropertyChange("signals", oldValue, getSignals());
    return removed;
  }

  /**
   * Gets the instances of <CODE>DataFlavor</CODE> supported by the 
   * <CODE>ArchiveRequest</CODE>.
   * 
   * @return The instances of <CODE>DataFlavor</CODE> that represent the ways a <CODE>Signal</CODE> can be transferred.
   */
  public DataFlavor[] getTransferDataFlavors()
  {
    DataFlavor[] flavors = new DataFlavor[2];
    flavors[0] = ARCHIVE_REQUEST_FLAVOR;
    flavors[1] = DataFlavor.stringFlavor;
    return flavors;
  }

  /**
   * Tests to see if a given <CODE>DataFlavor</CODE> is supported.
   * 
   * @param flavor The flavor to test for support.
   * @return <CODE>true</CODE> if the flavor is supported, <CODE>false</CODE> if it is not supported.
   */
  public boolean isDataFlavorSupported(DataFlavor flavor)
  {
    if(flavor.getRepresentationClass().equals(getClass()))
      return true;
    else
      return flavor.equals(DataFlavor.stringFlavor);
  }

  /**
   * Gets the transfer data.
   * 
   * @param flavor The <CODE>DataFlavor</CODE> to be transferred.
   * @return The data to be transferred.
   * @throws UnsupportedFlavorException Thrown if the given <CODE>DataFlavor</CODE> is not supported.
   */
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
  {
    if(flavor.getRepresentationClass().equals(getClass()))
      return this;
    else
      if(flavor.equals(DataFlavor.stringFlavor))
        return getFileName();
      else
        throw new UnsupportedFlavorException(flavor);
  }

  /**
   * Tests to see of the <CODE>Object</CODE> passed in is an 
   * <CODE>ArchiveRequest</CODE> that is equivalent to this one.
   * 
   * @param obj The <CODE>ArchiveRequest</CODE> to which to compare.
   * @return <CODE>true</CODE> if the instances of <CODE>ArchiveRequest</CODE> are equal.
   */
  public boolean equals(Object obj)
  {
    if(obj == null || ! (obj instanceof ArchiveRequest))
      return false;
    else
      if(MPSBrowserView.compare(getFileName(), ((ArchiveRequest)obj).getFileName()))
        return MPSBrowserView.compare(getFileLocation(), ((ArchiveRequest)obj).getFileLocation());
      else
        return false;
  }

  /**
   * Returns a hash code for the <CODE>ArchiveRequest</CODE>. If the 
   * <CODE>equals</CODE> method for a class returns <CODE>true</CODE>, the 
   * <CODE>hashCode</CODE> methods for those instances of <CODE>Object</CODE> 
   * must also return the same value. The reverse is not the case, meaning that
   * just because the value returned by the <CODE>hashCode()</CODE> methods of 
   * two instances of a class is the same equality can not be assumed.
   * 
   * @return A hash code for the <CODE>ArchiveRequest</CODE>.
   */
  public int hashCode()
  {
    int hashCode = 1;
    hashCode = hashCode * 37 + findPropertyHashCode(getFileName());
    hashCode = hashCode * 37 + findPropertyHashCode(getFileLocation());
    return hashCode;
  }
  
  /**
   * Checks the given <CODE>Object</CODE> for <CODE>null</CODE> before invoking 
   * <CODE>hashCode()</CODE> on it. If <CODE>null</CODE> is passed in, 
   * <CODE>0</CODE> is returned, otherwise the value returned by the 
   * <CODE>hashCode</CODE> method is returned.
   * 
   * @param propertyValue The <CODE>Object</CODE> of which to return the hash code.
   * @return The hash code for the given <CODE>Object</CODE>.
   */
  private int findPropertyHashCode(Object propertyValue)
  {
    if(propertyValue == null)
      return 0;
    else
      return propertyValue.hashCode();
  }

  /**
   * Creates a copy of the <CODE>ArchiveRequest</CODE>.
   * 
   * @return The copy of the <CODE>ArchiveRequest</CODE>.
   */
  public Object clone()
  {
    ArchiveRequest clone = new ArchiveRequest(getFileLocation(), getFileName());
    int signalCount = getSignalCount();
    for(int i=0;i<signalCount;i++)
      clone.addSignal(getSignalAt(i));
    return clone;
  }

  /**
   * Gets the location of the archive request file.
   * 
   * @return The location of the request file.
   */
  public String getFileLocation()
  {
    return fileLocation;
  }

  /**
   * Sets the location of the request file.
   * 
   * @param newFileLocation The location of the request file.
   */
  public void setFileLocation(String newFileLocation)
  {
    fileLocation = newFileLocation;
  }

  /**
   * Needs to be overridden by the subclass to return the name of the table to 
   * which the data belongs. If no schema is needed, <CODE>null</CODE> should be 
   * returned.
   * 
   * @return The name of the RDB schema that corresponds to the class, or <CODE>null</CODE> if none applies.
   */
  protected String getSchemaName()
  {
    return "EPICS";
  }

  /**
   * Needs to be overridden by the subclass to return the name of the table to 
   * which the data belongs.
   * 
   * @return The name of the RDB table that corresponds to the class.
   */
  protected String getTableName()
  {
    return ARCHIVE_REQUEST_TABLE_NAME;
  }

  /**
   * Should return the value of the RDB field as stored in the 
   * <CODE>RDBData</CODE> instance.
   * 
   * @param rdbFieldName The name of the field of which to return the value.
   * @return The value of the property that corresponds to the given RDB field.
   */
  protected Object getValue(String rdbFieldName)
  {
    if(rdbFieldName.equals(FILE_NAME_COLUMN))
      return getFileName();
    throw new java.lang.IllegalArgumentException(rdbFieldName + " is not a valid field name.");
  }

  /**
   * Inserts the given instances of <CODE>Signal</CODE> into the 
   * <CODE>ArchiveRequest</CODE> in the RDB.
   * 
   * @param connection The <CODE>Connection</CODE> to use to connect to the database.
   * @param signals The instances of <CODE>Signal</CODE> to associate with the <CODE>ArchiveRequest</CODE>.
   * @throws java.sql.SQLException
   */
  public void insertSignals(Connection connection, Signal[] signals) throws java.sql.SQLException
  {
    String message = "Inserting Signals Into Archive Request...";
    fireTaskStarted(new ProgressEvent(this, message, 0, -1));
    try
    {
      PreparedStatement query = connection.prepareStatement("INSERT INTO EPICS.SGNL_REC_ARCH (SGNL_ID, ARCH_REQ_FILE_NM, ARCH_FREQ, ARCH_TYPE) VALUES (?, ?, ?, ?)");
      try
      {
        String fileName = getFileName();
        fireProgress(new ProgressEvent(this, message, 0, signals.length));
        for(int i=0;i<signals.length;i++)
        {
          String signalID = signals[i].getID();
          if(getSignal(signalID) == null)
          {
            query.setString(1, signalID);
            query.setString(2, fileName);
            query.setBigDecimal(3, signals[i].getArchiveFrequency());//Used as default.
            query.setString(4, signals[i].getArchiveType());//Used as default.
            query.execute();
            setCommitNeeded(true);
          }
          fireProgress(new ProgressEvent(this, message, i + 1, signals.length));
        }
      }
      finally
      {
        query.close();
      }
    }
    catch(java.sql.SQLException ex)
    {
      fireTaskInterrupted(new ProgressEvent(this, ex.getMessage(), 0, 0));
      throw ex;
    }
    finally
    {
      fireTaskComplete(new ProgressEvent(this, null, 0, 0));
    }
  }

  /**
   * Deletes the data for the instance of <CODE>RDBData</CODE> from the RDB.
   * 
   * @param connection The <CODE>Connection</CODE> used to connect to the RDB.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void delete(Connection connection) throws SQLException
  {
    PreparedStatement groupDeleteQuery = connection.prepareStatement("DELETE FROM EPICS.ARCH_REQ_GRP WHERE ARCH_REQ_FILE_NM = ?");
    try
    {
      String fileName = getFileName();
      groupDeleteQuery.setString(1, fileName);
      groupDeleteQuery.execute();
      setCommitNeeded(true);
      super.delete(connection);
    }
    finally
    {
      groupDeleteQuery.close();
    }
  }

  /**
   * Loads the signal data for the <CODE>ArchiveRequest</CODE> from the RDB.
   * 
   * @param connection The connection to use to connect to the RDB.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void loadSignals(Connection connection) throws java.sql.SQLException
  {
    try
    {
      String message = "Loading Archive Request Contents";
      fireTaskStarted(new ProgressEvent(this, message, 0, -1));
      StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
      StringBuffer whereClause = new StringBuffer("EPICS.sgnl_rec_arch, EPICS.sgnl_fld, EPICS.arch_sgnl_fld_disp WHERE ((sgnl_rec_arch.sgnl_id = sgnl_fld.sgnl_id(+)) AND (arch_sgnl_fld_disp.fld_id(+) = sgnl_fld.fld_id)) AND ARCH_REQ_FILE_NM = ?");
      sql.append(whereClause);
      String currentFileName = getFileName();
      int maximum;
      PreparedStatement countQuery = connection.prepareStatement(sql.toString());
      try
      {
        countQuery.setString(1, currentFileName);
        ResultSet result = countQuery.executeQuery();
        try
        {
          result.next();
          maximum = result.getInt(1);
        }
        finally
        {
          result.close();
        }
      }
      finally
      {
        countQuery.close();
      }
      sql = new StringBuffer("SELECT sgnl_rec_arch.sgnl_id, sgnl_rec_arch.arch_freq, sgnl_rec_arch.ext_arch_freq, sgnl_rec_arch.arch_type, sgnl_rec_arch.ext_arch_type, sgnl_rec_arch.disable_chan_ind, sgnl_fld.fld_id, sgnl_fld.fld_val, sgnl_fld.rec_type_id FROM ");
      sql.append(whereClause);
      sql.append(" order by sgnl_rec_arch.sgnl_id");
      PreparedStatement query = connection.prepareStatement(sql.toString());
      try
      {
        query.setString(1, currentFileName);
        ResultSet result = query.executeQuery();
        try
        {
          int progress = 0;
          removeAllSignals();
          String oldSignalID = null;
          Signal currentSignal = null;
          fireProgress(new ProgressEvent(this, message, 0, maximum));
          while(result.next())
          {
            String newSignalID = result.getString("SGNL_ID");
            if(currentSignal == null || ! oldSignalID.equals(newSignalID))
            {
              currentSignal = createSignal(result);
              addSignal(currentSignal);
            }
            if(result.getString("FLD_ID") != null)
              currentSignal.addField(new SignalField(result));
            oldSignalID = newSignalID;
            fireProgress(new ProgressEvent(this, message, ++progress, maximum));
          }
        }
        finally
        {
          result.close();
        }
      }
      finally
      {
        query.close();
      }
    }
    catch(java.sql.SQLException ex)
    {
      fireTaskInterrupted(new ProgressEvent(this, ex.getMessage(), 0, 0));
      throw ex;
    }
    finally
    {
      fireTaskComplete(new ProgressEvent(this, null, 0, 0));
    }
  }
  
  /**
   * Creates and returns an instance of <CODE>Signal</CODE> that will be added 
   * to the <CODE>ArchiveRequest</CODE>.
   * 
   * @param data The data with which to create the instance of <CODE>Signal</CODE>.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  protected Signal createSignal(ResultSet data) throws java.sql.SQLException
  {
    return new Signal(data);
  }
  
  /**
   * Deletes the given instances of <CODE>Signal</CODE> from the 
   * <CODE>ArchiveRequest</CODE> in the RDB. They are not removed from the 
   * instance of the <CODE>ArchiveRequest</CODE>.
   * 
   * @param connection The <CODE>Connection</CODE> to use to connect to the RDB.
   * @param signals The instances of <CODE>Signal</CODE> to remove from the <CODE>ArchiveRequest</CODE> in the RDB.
   * @param Pass as <CODE>true</CODE> if the instances of <CODE>Signal</CODE> should be removed from the instance of <CODE>ArchiveRequest</CODE>, <CODE>false</CODE> otherwise.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void deleteSignals(Connection connection, Signal[] signals, boolean removeSignals) throws java.sql.SQLException
  {
    String message = "Removing Signals From Archive Request...";
    try
    {
      fireTaskStarted(new ProgressEvent(this, message, 0, -1));
      PreparedStatement query = connection.prepareStatement("DELETE FROM EPICS.SGNL_REC_ARCH WHERE ARCH_REQ_FILE_NM = ? AND SGNL_ID = ?");
      try
      {
        query.setString(1, getFileName());
        fireProgress(new ProgressEvent(this, message, 0, signals.length));
        for(int i=signals.length-1;i>=0;i--) 
        {
          query.setString(2, signals[i].getID());
          query.execute();
          setCommitNeeded(true);
          if(removeSignals)
            removeSignal(signals[i]);
        }
      }
      finally
      {
        query.close();
      }
    }
    catch(java.sql.SQLException ex)
    {
      fireTaskInterrupted(new ProgressEvent(this, ex.getMessage(), 0, 0));
      throw ex;
    }
    finally
    {
      fireTaskComplete(new ProgressEvent(this, null, 0, 0));
    }
  }
  
  /**
   * Returns the instances of <CODE>Signal</CODE> that have been changed.
   * 
   * @return The instances of <CODE>Signal</CODE> that have been changed.
   */
  public Signal[] getChangedSignals()
  {
    ArrayList changedSignals = new ArrayList();
    int signalCount = getSignalCount();
    for(int i=0;i<signalCount;i++) 
    {
      Signal signal = getSignalAt(i);
      if(signal.isChanged())
        changedSignals.add(signal);
    }
    return (Signal[])changedSignals.toArray(new Signal[changedSignals.size()]);
  }
  
  /**
   * Saves changes to the instances of <CODE>Signal</CODE> associated with the 
   * <CODE>ArchiveRequest</CODE>.
   * 
   * @param connection The <CODE>Connection</CODE> to the RDB use to save the data.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void updateSignals(Connection connection) throws java.sql.SQLException
  {
    String message = "Saving Signals From Archive Request";
    try
    {
      fireTaskStarted(new ProgressEvent(this, message, 0, -1));
      String fileName = getFileName();
      PreparedStatement query = connection.prepareStatement("UPDATE EPICS.SGNL_REC_ARCH SET ARCH_FREQ = ?, ARCH_TYPE = ?, DISABLE_CHAN_IND = ? WHERE SGNL_ID = ? AND ARCH_REQ_FILE_NM = ?");
      try
      {
        Signal[] changedSignals = getChangedSignals();
        String[] fieldNames = new String[]{Signal.ARCHIVE_FREQUENCY_COLUMN_NAME, Signal.ARCHIVE_TYPE_COLUMN_NAME, Signal.DISABLE_CHANNEL_INDICATOR_COLUMN_NAME};
        fireTaskStarted(new ProgressEvent(this, message, 0, changedSignals.length));
        for(int i=0;i<changedSignals.length;i++)
        {
          query.setBigDecimal(1, changedSignals[i].getArchiveFrequency());
          query.setString(2, changedSignals[i].getArchiveType());
          query.setString(3, changedSignals[i].getDisableChannelIndicator());
          query.setString(4, changedSignals[i].getID());
          query.setString(5, fileName);
          query.execute();
          changedSignals[i].resetChangedFlag(fieldNames);
          setCommitNeeded(true);
          fireTaskStarted(new ProgressEvent(this, message, i + 1, changedSignals.length));
        }
      }
      finally
      {
        query.close();
      }
    }
    catch(java.sql.SQLException ex)
    {
      fireTaskInterrupted(new ProgressEvent(this, ex.getMessage(), 0, 0));
      throw ex;
    }
    finally
    {
      fireTaskComplete(new ProgressEvent(this, null, 0, 0));
    }
  }

  /**
   * Should set the value of the property to the value of the RDB field. If the 
   * field name passed in does not correspond to a property in the class, it 
   * should just be ignored.
   * 
   * @param rdbFieldName The name of the field for the data.
   * @param value The value of the RDB field.
   * @return The value of the property that corresponds to the given RDB field.
   */
  protected void setValue(String rdbFieldName, Object value)
  {
    if(rdbFieldName.equalsIgnoreCase(FILE_NAME_COLUMN))
      setFileName((String)value);
  }

  /**
   * Creates the contents for an archive request file. The progress bar passed
   * into this method needs to have the value of it's minimum property set to 0
   * before invoking this method.
   * 
   * @param userID The ID of the user creating the file.
   * @return The contents for an archive request file.
   * @throws java.io.IOException Thrown on sql error.
   */
  public String createFileContents(String userID) throws java.io.IOException
  {
    String newLine = System.getProperty("line.separator");
    int exportedCount = 0;
    StringWriter oStream = new StringWriter();
    try
    {
      oStream.write("# This file was generated by JERI user ");
      oStream.write(userID);
      oStream.write(" on ");
      SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d yyyy h:mm a");
      oStream.write(format.format(new java.util.Date()));
      oStream.write(newLine);
      //The signals that have been disabled need to be first in the file.
      final int signalCount = getSignalCount();
      ArrayList signals = new ArrayList(signalCount);
      int disabledSignalCount = 0;
      for(int i=0;i<signalCount;i++)
      {
        Signal currentSignal = getSignalAt(i);
        if(currentSignal.getDisableChannelIndicator().equals("Y"))
          signals.add(disabledSignalCount++, currentSignal);
        else
          signals.add(currentSignal);
      }
//      setProgressMaximum(signalCount, progressBar);
//      setProgressValue(0, progressBar);
//      setProgressIndeterminate(false, progressBar);
      for(int i=0;i<signalCount;i++) 
      {
        Signal currentSignal = (Signal)signals.get(i);
        String archiveable = currentSignal.getArchiveIndicator();
        if(archiveable == null || ! archiveable.equals("Y"))
          continue;//skip if not archiveable.
        oStream.write(currentSignal.getID());
        oStream.write("\t");
        BigDecimal frequency = currentSignal.getArchiveFrequency();
        String frequencyValue;
        if(frequency == null)
          frequencyValue = null;
        else
        {
          frequencyValue = frequency.toString().trim();
          if(frequencyValue.equals(""))
            frequencyValue = null;
        }
        //null frequency means we need to check the ext val.
        if(frequency == null)
        {
          frequency = currentSignal.getExternalArchiveFrequency();
          if(frequency == null)
            frequencyValue = "";
          else
            frequencyValue = frequency.toString().trim();
        }
        oStream.write(frequencyValue);
        String archiveType = currentSignal.getArchiveType();
        if(archiveType == null || archiveType.trim().equals(""))
        {
          archiveType = currentSignal.getExternalArchiveType();
          if(archiveType == null)
            archiveType = "";
        }
        if(archiveType.equals("Monitor"))
        {
          oStream.write("\tMonitor");
          if(currentSignal.getDisableChannelIndicator().equals("Y"))
            oStream.write("\tDisable");
        }
        oStream.write(newLine);
        exportedCount++;
//        setProgressValue(i + 1, progressBar);
      }
      oStream.write("# EOF");
      oStream.write(newLine);
      oStream.write(newLine);
      oStream.flush();
    }
    finally
    {
      oStream.close();
    }
    return oStream.getBuffer().toString();
  }
  
  /**
   * Writes the <CODE>ArchiveRequest</CODE> to a file.
   * 
   * @param userID The ID of the user generating the file.
   * @throws java.io.IOException Thrown on sql error.
   */
  public void writeToFile(String userID) throws java.io.IOException
  {
    String fileContents = createFileContents(userID);
    File requestFile = new File(getFileLocation(), getDiskFileName());
    writeToFile(requestFile, fileContents);
  }

  /**
   * Writes the <CODE>ArchiveRequest</CODE> to a file.
   * 
   * @param exportFile The <CODE>File</CODE> to export to.
   * @throws java.io.IOException Thrown on sql error.
   */
  static public void writeToFile(File exportFile, String fileContents) throws java.io.IOException
  {
    File directory = exportFile.getParentFile();
    if(! directory.exists())
      directory.mkdirs();
    BufferedWriter oStream = new BufferedWriter(new FileWriter(exportFile));
    try
    {
      oStream.write(fileContents);
      oStream.flush();
    }
    finally
    {
      oStream.close();
    }
  }

  /**
   * Gets the name of the file as it should be on the disk. This method replaces 
   * unuseable characters in the filename.
   * 
   * @return The name of the file on the disk.
   */
  public String getDiskFileName()
  {
    return getFileName().replaceAll("[\\W_&&[^\\.]]", "-");
  }
  
  /**
   * Gets the disk name and location for the <CODE>ArchiveRequest</CODE>.
   * @return
   */
  public String getDiskFileNameAndLocation()
  {
    StringBuffer location = new StringBuffer(getFileLocation());
    location.append(File.separator);
    location.append(getDiskFileName());
    return location.toString();
  }
  
  /**
   * Loads all instances of <CODE>ArchiveRequest</CODE> from the database.
   * 
   * @param databaseAdaptor The <CODE>CachingDatabaseAdaptor</CODE> touse to connect to the database.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> to be notified of progress.
   * @return The instances of <CODE>ArchiveRequest</CODE> loaded from the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  static public ArchiveRequest[] loadAll(CachingDatabaseAdaptor databaseAdaptor, ArrayList progressListeners) throws java.sql.SQLException
  {
    Connection oracleConnection = databaseAdaptor.getConnection();
    try
    {
      return loadAll(oracleConnection, progressListeners);
    }
    finally
    {
      oracleConnection.close();
    }
  }
  
  /**
   * Loads all instances of <CODE>ArchiveRequest</CODE> from the database.
   * 
   * @param databaseAdaptor The <CODE>Connection</CODE> touse to connect to the database.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> to be notified of progress.
   * @return The instances of <CODE>ArchiveRequest</CODE> loaded from the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  static public ArchiveRequest[] loadAll(Connection connection, ArrayList progressListeners) throws java.sql.SQLException
  {
    try
    {
      ProgressEvent event = new ProgressEvent(ArchiveRequest.class, "Downloading Requests", 0, -1);
      fireTaskStarted(event, progressListeners);
      Statement query = connection.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        sql.append(ARCHIVE_REQUEST_SCHEMA_NAME);
        sql.append(".");
        sql.append(ARCHIVE_REQUEST_TABLE_NAME);
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          result.next();
          int requestCount = result.getInt(1);
          ArrayList allRequests = new ArrayList(requestCount);
          sql = new StringBuffer("SELECT * FROM ");
          sql.append(ARCHIVE_REQUEST_SCHEMA_NAME);
          sql.append(".");
          sql.append(ARCHIVE_REQUEST_TABLE_NAME);
          result = query.executeQuery(sql.toString());
          int progress = 0;
          event = new ProgressEvent(ArchiveRequest.class, "Downloading Requests", progress, requestCount);
          fireProgress(event, progressListeners);
          while(result.next())
          {
            allRequests.add(new ArchiveRequest(result));
            event = new ProgressEvent(ArchiveRequest.class, "Downloading Requests", ++progress, requestCount);
            fireProgress(event, progressListeners);
          }
          ArchiveRequest[] requests = new ArchiveRequest[allRequests.size()];
          requests = (ArchiveRequest[])allRequests.toArray(requests);
          event = new ProgressEvent(ArchiveRequest.class, null, 0, 0);
          fireTaskComplete(event, progressListeners);
          return requests;
        }
        finally
        {
          result.close();
        }
      }
      finally
      {
        query.close();
      }
    }
    catch(java.sql.SQLException e)
    {
      ProgressEvent event = new ProgressEvent(ArchiveRequest.class, e.getMessage(), 0, 0);
      fireTaskInterrupted(event, progressListeners);
      throw e;
    }
  }

  /**
   * Loads the instances of <CODE>Signal</CODE> into the instances of 
   * <CODE>ArchiveRequest</CODE> provided. Any instances of <CODE>Signal</CODE>
   * already in the instances of <CODE>ArchiveRequest</CODE> will be removed and 
   * only data in the database will be restored.
   * 
   * @param requests The instances of <CODE>ArchiveRequest</CODE> for which to load the instances of <CODE>Signal</CODE>.
   * @param dataSource The <CODE>CachingDataSource</CODE> to use to connect to the database.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> to notify of changes in progress.
   * @throws SQLException Thrown on SQL error.
   */
  static public void loadSignals(ArchiveRequest[] requests, CachingDatabaseAdaptor databaseAdaptor, ArrayList progressListeners) throws java.sql.SQLException
  {
    Connection connection = databaseAdaptor.getConnection();
    try 
    {
      loadSignals(requests, connection, progressListeners);
    } 
    finally 
    {
      connection.close();
    }
  }

  /**
   * Loads the instances of <CODE>Signal</CODE> into the instances of 
   * <CODE>ArchiveRequest</CODE> provided. Any instances of <CODE>Signal</CODE>
   * already in the instances of <CODE>ArchiveRequest</CODE> will be removed and 
   * only data in the database will be restored.
   * 
   * @param requests The instances of <CODE>ArchiveRequest</CODE> for which to load the instances of <CODE>Signal</CODE>.
   * @param connection The <CODE>Connection</CODE> to use to connect to the database.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> to notify of changes in progress.
   * @throws SQLException Thrown on SQL error.
   */
  static public void loadSignals(ArchiveRequest[] requests, Connection connection, ArrayList progressListeners) throws java.sql.SQLException
  {
    try
    {
      ProgressEvent event = new ProgressEvent(ArchiveRequest.class, "Loading signal data...", 0, -1);
      fireTaskStarted(event, progressListeners);
      //Need a way to hold the signals for each request file.
      HashMap signals = new HashMap();
      StringBuffer sql = new StringBuffer("SELECT SGNL_REC_ARCH.SGNL_ID, SGNL_REC.ARCH_IND, SGNL_REC_ARCH.ARCH_FREQ, SGNL_REC_ARCH.EXT_ARCH_FREQ, SGNL_REC_ARCH.ARCH_TYPE, SGNL_REC_ARCH.EXT_ARCH_TYPE, SGNL_REC_ARCH.DISABLE_CHAN_IND FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".SGNL_REC, ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".SGNL_REC_ARCH WHERE ARCH_REQ_FILE_NM = ? AND SGNL_REC.SGNL_ID = SGNL_REC_ARCH.SGNL_ID");
      PreparedStatement query = connection.prepareStatement(sql.toString());
      try
      {
        event = new ProgressEvent(ArchiveRequest.class, "Loading signal data...", 0, requests.length);
        fireProgress(event, progressListeners);
        for(int i=0;i<requests.length;i++) 
        {
          requests[i].removeAllSignals();
          String currentFileName = requests[i].getFileName();
          Signal[] currentSignals = (Signal[])signals.get(currentFileName);
          if(currentSignals != null)
            for(int j=0;j<currentSignals.length;j++)
              requests[i].addSignal(currentSignals[j]);
          else
          {
            //Signals for this request file have not been loaded yet. Need to 
            //go to the database.
            query.setString(1, currentFileName);
            ResultSet result = query.executeQuery();
            try
            {
              while(result.next())
              {
                String currentIndicator = result.getString("ARCH_IND");
                Signal newSignal = new Signal(result.getString("SGNL_ID"));
                newSignal.setArchiveIndicator(currentIndicator);
                newSignal.setArchiveFrequency(result.getBigDecimal("ARCH_FREQ"));
                newSignal.setExternalArchiveFrequency(result.getBigDecimal("EXT_ARCH_FREQ"));
                newSignal.setArchiveType(result.getString("ARCH_TYPE"));
                newSignal.setExternalArchiveType(result.getString("EXT_ARCH_TYPE"));
                newSignal.setDisableChannelIndicator(result.getString("DISABLE_CHAN_IND"));
                requests[i].addSignal(newSignal);
              }
              //Need to remember these values so if the same request file is
              //encountered again in the tree we don't have to make another
              //round trip to the database.
              currentSignals = new Signal[requests[i].getSignalCount()];
              for(int j=0;j<currentSignals.length;j++)
                currentSignals[j] = requests[i].getSignalAt(j);
              signals.put(currentFileName, currentSignals);
            }
            finally
            {
              result.close();
            }
          }
          event = new ProgressEvent(ArchiveRequest.class, "Loading signal data...", i + 1, requests.length);
          fireProgress(event, progressListeners);
        }
      }
      finally
      {
        query.close();
      }
      event = new ProgressEvent(ArchiveRequest.class, null, 0, 0);
      fireTaskComplete(event, progressListeners);
    }
    catch(java.sql.SQLException e)
    {
      ProgressEvent event = new ProgressEvent(ArchiveRequest.class, e.getMessage(), 0, 0);
      fireTaskInterrupted(event, progressListeners);
      throw e;
    }
  }
  
  /**
   * Loads the instances of <CODE>ArchiveGroup</CODE> associated with the 
   * <CODE>ArchiveRequest</CODE>. This method does not add the 
   * <CODE>ArchiveRequest</CODE> to the instances of <CODE>ArchiveGroup</CODE>.
   * 
   * @param databaseAdaptor The <CODE>CachingDatabasAdaptor</CODE> with which to connect to the database.
   * @return The instances of <CODE>ArchiveGroup</CODE> loaded for the <CODE>ArchiveRequest</CODE>.
   * @throws SQLException Thrown on SQL error.
   */
  public ArchiveGroup[] loadArchiveGroups(CachingDatabaseAdaptor databaseAdaptor) throws java.sql.SQLException
  {
    Connection connection = databaseAdaptor.getConnection();
    try 
    {
      return loadArchiveGroups(connection);
    } 
    finally 
    {
      connection.close();
    }
  }

  /**
   * Loads the instances of <CODE>ArchiveGroup</CODE> associated with the 
   * <CODE>ArchiveRequest</CODE>. This method does not add the 
   * <CODE>ArchiveRequest</CODE> to the instances of <CODE>ArchiveGroup</CODE>.
   * 
   * @param connection The <CODE>Connection</CODE> with which to connect to the database.
   * @return The instances of <CODE>ArchiveGroup</CODE> loaded for the <CODE>ArchiveRequest</CODE>.
   * @throws SQLException Thrown on SQL error.
   */
  public ArchiveGroup[] loadArchiveGroups(Connection connection) throws java.sql.SQLException
  {
    try
    {
      fireTaskStarted(new ProgressEvent(this, "Downloading Groups", 0, -1));
      ArrayList groups = new ArrayList();
      Statement query = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      try
      {
        StringBuffer sql = new StringBuffer("SELECT ARCH_GRP.ARCH_GRP_FILE_NM, ARCH_GRP.ARCH_GRP_DIR_LOC FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".ARCH_REQ_GRP, ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".ARCH_GRP WHERE ARCH_REQ_GRP.ARCH_GRP_FILE_NM = ARCH_GRP.ARCH_GRP_FILE_NM AND ARCH_REQ_GRP.ARCH_REQ_FILE_NM = '");
        sql.append(getFileName());
        sql.append("'");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          result.last();
          int max = result.getRow();
          result.beforeFirst();
          int position = 0;
          fireProgress(new ProgressEvent(this, "Downloading Groups", 0, max));
          while(result.next())
          {
            String fileName = result.getString("ARCH_GRP_FILE_NM");
            String fileLocation = result.getString("ARCH_GRP_DIR_LOC");
            ArchiveGroup newGroup = new ArchiveGroup(fileLocation, fileName);
            groups.add(newGroup);
            fireProgress(new ProgressEvent(this, "Downloading Groups", position, max));
          }
        }
        finally
        {
          result.close();
        }
      }
      finally
      {
        query.close();
      }
      fireTaskComplete(new ProgressEvent(this, null, 0, 0));
      return (ArchiveGroup[])groups.toArray(new ArchiveGroup[groups.size()]);
    }
    catch(java.sql.SQLException e)
    {
      fireTaskInterrupted(new ProgressEvent(this, e.getMessage(), 0, 0));
      throw e;
    }
  }
}