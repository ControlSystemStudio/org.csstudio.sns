package org.csstudio.mps.sns.tools.data;

import com.cosylab.gui.components.ProgressEvent;
import java.awt.datatransfer.*;

import java.io.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.tools.database.CachingDatabaseAdaptor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class provides a container for the data in the ARCH_GRP and 
 * ARCH_REQ_GRP. An <CODE>ArchiveGroup</CODE> holds a reference to the 
 * <CODE>File</CODE> that holds the <CODE>ArchiveGroup</CODE>, and the instances 
 * of <CODE>ArchiveRequest</CODE> found in the group.
 * 
 * @author Chris Fowlkes
 */
public class ArchiveGroup extends RDBData implements Transferable, Cloneable 
{
  /**
   * Holds the name of the file that contains the <CODE>ArchiveGroup</CODE>.
   */
  private String fileName;
  /**
   * Holds the name of the directory that contains the 
   * <CODE>ArchiveGroup</CODE> file.
   */
  private String fileLocation;
  /**
   * Holds the instances of <CODE>ArchiveRequest</CODE> in the 
   * <CODE>ArchiveGroup</CODE>.
   */
  private ArrayList archiveRequests = new ArrayList();
  /**
   * Holds the <CODE>DataFlavor</CODE> used to transfer a single instance of 
   * <CODE>ArchiveGroup</CODE> via drag and drop.
   */
  final static public DataFlavor ARCHIVE_GROUP_FLAVOR = new DataFlavor(org.csstudio.mps.sns.tools.data.ArchiveGroup.class, "ArchiveGroup");
  /**
   * Holds the name of the column in the RDB to which the file name property 
   * corresponds.
   */
  final static public String FILE_NAME_COLUMN = "ARCH_GRP_FILE_NM";
  /**
   * Holds the name of the column in the RDB to which the file location property 
   * corresponds.
   */
  final static public String FILE_LOCATION_COLUMN = "ARCH_GRP_DIR_LOC";
  /**
   * Holds the name of the archive group table name.
   */
  final static public String ARCHIVE_GROUP_TABLE_NAME = "ARCH_GRP";
  /**
   * Holds the name of the schema for the archive group table.
   */
  final static public String ARCHIVE_GROUP_SCHEMA_NAME = "EPICS";

  /**
   * Creates a new <CODE>ArchiveGroup</CODE>.
   */
  public ArchiveGroup()
  {
  }

  /**
   * Creates a new <CODE>ArchiveGroup</CODE>.
   * 
   * @param fileLocation The initial value of the file location property.
   * @param fileName The initial value of the file name property.
   */
  public ArchiveGroup(String fileLocation, String fileName)
  {
    this();
    setFileLocation(fileLocation);
    setFileName(fileName);
  }
  
  /**
   * Creates a new <CODE>ArchiveGroup</CODE>.
   * 
   * @param result The <CODE>ResultSet</CODE> that contains the data for the <CODE>ArchiveGroup</CODE>.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public ArchiveGroup(ResultSet result) throws java.sql.SQLException
  {
    super(result);
  }

  /**
   * Gets the name of the file used to store the <CODE>ArchiveGroup</CODE>.
   * 
   * @return The name of the file associated with the <CODE>ArchiveGroup</CODE>.
   */
  public String getFileName()
  {
    return fileName;
  }

  /**
   * Sets the name of the file used to store the <CODE>ArchiveGroup</CODE>.
   * 
   * @param newFileName The name of the file associated with the <CODE>ArchiveGroup</CODE>.
   */
  public void setFileName(String newFileName)
  {
    String oldValue = fileName;
    fileName = newFileName;
    markFieldChanged(FILE_NAME_COLUMN);
    firePropertyChange("fileName", oldValue, fileName);
  }

  /**
   * Returns the location of the file that contains the 
   * <CODE>ArchiveGroup</CODE>.
   * 
   * @return The location of the file that contains the <CODE>ArchiveGroup</CODE>.
   */
  public String getFileLocation()
  {
    return fileLocation;
  }

  /**
   * Sets the location of the file that contains the <CODE>ArchiveGroup</CODE>.
   * 
   * @param newFileLocation The location of the file containing the <CODE>ArchiveGroup</CODE>.
   */
  public void setFileLocation(String newFileLocation)
  {
    String oldValue = fileLocation;
    fileLocation = newFileLocation;
    markFieldChanged(FILE_LOCATION_COLUMN);
    firePropertyChange("fileLocation", oldValue, fileLocation);
  }

  /**
   * Adds the <CODE>ArchiveRequest</CODE> to the <CODE>ArchiveGroup</CODE>.
   * 
   * @param newArchiveRequest The <CODE>ArchiveRequest</CODE> to add to the <CODE>ArchiveGroup</CODE>.
   */
  public void addArchiveRequest(ArchiveRequest newArchiveRequest)
  {
    ArchiveRequest[] oldArchiveRequests = getArchiveRequests();
    archiveRequests.add(newArchiveRequest);
    ArchiveRequest[] newArchiveRequests = getArchiveRequests();
    firePropertyChange("archiveRequests", oldArchiveRequests, newArchiveRequests);
  }

  /**
   * Returns the number of instances of <CODE>ArchiveRequest</CODE> associated 
   * with this <CODE>ArchiveGroup</CODE>.
   * 
   * @return The number of instances of <CODE>ArchiveRequest</CODE> associated with this <CODE>ArchiveGroup</CODE>.
   */
  public int getArchiveRequestCount()
  {
    return archiveRequests.size();
  }

  /**
   * Gets the <CODE>ArchiveRequest</CODE> at the given index.
   * 
   * @param index The index of the <CODE>ArchiveRequest</CODE> to return.
   * @return The <CODE>ArchiveRequest</CODE> at the given index.
   */
  public ArchiveRequest getArchiveRequestAt(int index)
  {
    return (ArchiveRequest)archiveRequests.get(index);
  }

  /**
   * Provides a <CODE>String</CODE> representetion of the 
   * <CODE>ArchiveGroup</CODE>. This is the name of the <CODE>File</CODE>
   * associated with the group.
   * 
   * @return The name of the <CODE>File</CODE> associated with the group.
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
   * Tests to see of the <CODE>Object</CODE> passed in is an 
   * <CODE>ArchiveGroup</CODE> that is equivalent to this one.
   * 
   * @param obj The <CODE>ArchiveGroup</CODE> to which to compare.
   * @return <CODE>true</CODE> if the instances of <CODE>ArchiveGroup</CODE> are equal.
   */
  public boolean equals(Object obj)
  {
    if(obj == null || ! (obj instanceof ArchiveGroup))
      return false;
    else
      if(MPSBrowserView.compare(getFileLocation(), ((ArchiveGroup)obj).getFileLocation()))
        return MPSBrowserView.compare(getFileName(), ((ArchiveGroup)obj).getFileName());
      else
        return false;
  }

  /**
   * Returns a hash code for the <CODE>ArchiveGroup</CODE>. If the 
   * <CODE>equals</CODE> method for a class returns <CODE>true</CODE>, the 
   * <CODE>hashCode</CODE> methods for those instances of <CODE>Object</CODE> 
   * must also return the same value. The reverse is not the case, meaning that
   * just because the value returned by the <CODE>hashCode()</CODE> methods of 
   * two instances of a class is the same equality can not be assumed.
   * 
   * @return A hash code for the <CODE>ArchiveGroup</CODE>.
   */
  public int hashCode()
  {
    int hashCode = 1;
    hashCode = hashCode * 37 + findPropertyHashCode(getFileLocation());
    hashCode = hashCode * 37 + findPropertyHashCode(getFileName());
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
   * Removes all of the instances of <CODE>ArchiveRequest</CODE> from the 
   * <CODE>ArchiveGroup</CODE>.
   */
  public void removeAllRequests()
  {
    ArchiveRequest[] oldArchiveRequests = getArchiveRequests();
    archiveRequests.clear();
    ArchiveRequest[] newArchiveRequests = getArchiveRequests();
    firePropertyChange("archiveRequests", oldArchiveRequests, newArchiveRequests);
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
    flavors[0] = ARCHIVE_GROUP_FLAVOR;
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
   * Creates a copy of the <CODE>ArchiveGroup</CODE>.
   * 
   * @return A copy of the <CODE>ArchiveGroup</CODE>.
   */
  public Object clone()
  {
    ArchiveGroup clone = new ArchiveGroup(getFileLocation(), getFileName());
    int requestCount = getArchiveRequestCount();
    for(int i=0;i<requestCount;i++)
      clone.addArchiveRequest((ArchiveRequest)getArchiveRequestAt(i).clone());
    return clone;
  }

  /**
   * Gets the <CODE>ArchiveRequest</CODE> associated with this 
   * <CODE>ArchiveGroup</CODE> that has the given file name. If an 
   * <CODE>ArchiveRequest</CODE> with the given file name is not found in the
   * <CODE>ArchiveGroup</CODE>, <CODE>null</CODE> is returned.
   * 
   * @param requestFileName The file name of the <CODE>ArchiveRequest</CODE> to return.
   * @return The <CODE>ArchiveRequest</CODE> with the given file name, or <CODE>null</CODE> if a mathing <CODE>ArchiveRequest</CODE> is not found.
   */
  public ArchiveRequest getArchiveRequest(String requestFileName)
  {
    ArchiveRequest request = null;
    int requestCount = getArchiveRequestCount();
    for(int i=0;i<requestCount;i++)
    {
      ArchiveRequest currentRequest = getArchiveRequestAt(i);
      String currentRequestFileName = currentRequest.getFileName();
      if(currentRequestFileName != null && currentRequestFileName.equals(requestFileName))
      {
        request = currentRequest;
        break;
      }
    }
    return request;
  }

  /**
   * Gets the instances of <CODE>ArchiveRequest</CODE> for the group.
   * 
   * @return The instances of <CODE>ArchiveRequest</CODE> for the group.
   */
  public ArchiveRequest[] getArchiveRequests()
  {
    int requestCount = archiveRequests.size();
    ArchiveRequest[] requestArray = new ArchiveRequest[requestCount];
    return (ArchiveRequest[])archiveRequests.toArray(requestArray);
  }

  /**
   * Return the name of the schema to which the data belongs. The schema for
   * data is EPICS.
   * 
   * @return The name of the RDB schema that corresponds to the class.
   */
  protected String getSchemaName()
  {
    return "EPICS";
  }

  /**
   * Returns the name of the table to which the data belongs.
   * 
   * @return The name of the RDB table that corresponds to the class.
   */
  protected String getTableName()
  {
    return ARCHIVE_GROUP_TABLE_NAME;
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
    else
      if(rdbFieldName.equals(FILE_LOCATION_COLUMN))
        return getFileLocation();
    throw new java.lang.IllegalArgumentException(rdbFieldName + " is not a valid field name.");
  }
  
  /**
   * Inserts the given <CODE>ArchiveRequest</CODE> into the RDB for the 
   * <CODE>ArchiveGroup</CODE>.
   * 
   * @param request The <CODE>ArchiveRequest</CODE> to insert.
   * @param connection The <CODE>Connection</CODE> to use to connect to the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void insertRequest(ArchiveRequest request, Connection connection) throws java.sql.SQLException
  {
    PreparedStatement query = connection.prepareStatement("INSERT INTO EPICS.ARCH_REQ_GRP (ARCH_REQ_FILE_NM, ARCH_GRP_FILE_NM) VALUES (?, ?)");
    try
    {
      query.setString(1, request.getFileName());
      query.setString(2, getFileName());
      query.execute();
      setCommitNeeded(true);
    }
    finally
    {
      query.close();
    }
  }

  /**
   * Deletes the given <CODE>ArchiveRequest</CODE> from the 
   * <CODE>ArchiveGroup</CODE> in the RDB.
   * 
   * @param request The <CODE>ArchiveRequest</CODE> to delete.
   * @param connection The <CODE>Connection</CODE> to use to connect to the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void deleteRequest(ArchiveRequest request, Connection connection) throws java.sql.SQLException
  {
    PreparedStatement query = connection.prepareStatement("DELETE FROM EPICS.ARCH_REQ_GRP WHERE ARCH_REQ_FILE_NM = ? AND ARCH_GRP_FILE_NM = ?");
    try
    {
      query.setString(1, request.getFileName());
      query.setString(2, getFileName());
      query.execute();
      setCommitNeeded(true);
    }
    finally
    {
      query.close();
    }
  }
  
  /**
   * Removes the <CODE>ArchiveRequest</CODE> from the <CODE>ArchiveGroup</CODE>.
   * 
   * @param request The <CODE>ArchiveRequest</CODE> to remove from the <CODE>ArchiveGroup</CODE>.
   */
  public void removeArchiveRequest(ArchiveRequest request)
  {
    ArchiveRequest[] oldArchiveRequests = getArchiveRequests();
    archiveRequests.remove(request);
    ArchiveRequest[] newArchiveRequests = getArchiveRequests();
    firePropertyChange("archiveRequests", oldArchiveRequests, newArchiveRequests);
  }
  
  /**
   * Removes the <CODE>ArchiveRequest</CODE> with the given file name from the 
   * <CODE>ArchiveGroup</CODE>.
   * 
   * @param request The <CODE>ArchiveRequest</CODE> to remove from the <CODE>ArchiveGroup</CODE>.
   */
  public void removeArchiveRequest(String requestFileName)
  {
    removeArchiveRequest(getArchiveRequest(requestFileName));
  }

  /**
   * Deletes the data for the instance of <CODE>RDBData</CODE> from the RDB.
   * 
   * @param connection The <CODE>Connection</CODE> used to connect to the RDB.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void delete(Connection connection) throws SQLException
  {
    PreparedStatement requestDeleteQuery = connection.prepareStatement("DELETE FROM EPICS.ARCH_REQ_GRP WHERE ARCH_GRP_FILE_NM = ?");
    try
    {
      String fileName = getFileName();
      requestDeleteQuery.setString(1, fileName);
      requestDeleteQuery.execute();
      setCommitNeeded(true);
      super.delete(connection);
    }
    finally
    {
      requestDeleteQuery.close();
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
    if(rdbFieldName.equalsIgnoreCase(FILE_LOCATION_COLUMN))
      setFileLocation((String)value);
  }
  
  /**
   * Gets the index of the given <CODE>ArchiveRequest</CODE>.
   * 
   * @param request The <CODE>ArchiveRequest</CODE> of which to find the index.
   * @return The index of the <CODE>ArchiveRequest</CODE>.
   */
  public int indexOf(ArchiveRequest request)
  {
    return archiveRequests.indexOf(request);
  }
  
  /**
   * Gets all of the instances of <CODE>ArchiveRequest</CODE> for the 
   * <CODE>archiveGroup</CODE> as an <CODE>Enumeration</CODE>.
   * 
   * @return The instances of <CODE>ArchiveRequest</CODE> in the <CODE>ArciveGroup</CODE>.
   */
  public Enumeration getArchiveRequestEnumeration()
  {
    Vector vector = new Vector(archiveRequests);
    return vector.elements();
  }
  
  /**
   * Loads all instances of <CODE>ArchiveGroup</CODE> from the database.
   * 
   * @param databaseAdaptor The <CODE>CachingDatabaseAdaptor</CODE> touse to connect to the database.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> to be notified of progress.
   * @return The instances of <CODE>ArchiveGroup</CODE> loaded from the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  static public ArchiveGroup[] loadAll(CachingDatabaseAdaptor databaseAdaptor, ArrayList progressListeners) throws java.sql.SQLException
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
   * Loads all instances of <CODE>ArchiveGroup</CODE> from the database.
   * 
   * @param connection The <CODE>Connection</CODE> touse to connect to the database.
   * @param progressListeners The instances of <CODE>ProgressListener</CODE> to be notified of progress.
   * @return The instances of <CODE>ArchiveGroup</CODE> loaded from the database.
   * @throws SQLException Thrown on SQL error.
   */
  static public ArchiveGroup[] loadAll(Connection connection, ArrayList progressListeners) throws java.sql.SQLException
  {
    try
    {
      ProgressEvent event = new ProgressEvent(ArchiveGroup.class, "Downloading Groups", 0, -1);
      fireTaskStarted(event, progressListeners);
      Statement query = connection.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        sql.append(ARCHIVE_GROUP_SCHEMA_NAME);
        sql.append(".");
        sql.append(ARCHIVE_GROUP_TABLE_NAME);
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          result.next();
          int groupCount = result.getInt(1);
          ArrayList allGroups = new ArrayList(groupCount);
          sql = new StringBuffer("SELECT * FROM ");
          sql.append(ARCHIVE_GROUP_SCHEMA_NAME);
          sql.append(".");
          sql.append(ARCHIVE_GROUP_TABLE_NAME);
          result = query.executeQuery(sql.toString());
          int progress = 0;
          event = new ProgressEvent(ArchiveGroup.class, "Downloading Groups", progress, groupCount);
          fireProgress(event, progressListeners);
          while(result.next())
          {
            allGroups.add(new ArchiveGroup(result));
            event = new ProgressEvent(ArchiveGroup.class, "Downloading Groups", ++progress, groupCount);
            fireProgress(event, progressListeners);
          }
          ArchiveGroup[] groups = new ArchiveGroup[allGroups.size()];
          groups = (ArchiveGroup[])allGroups.toArray(groups);
          event = new ProgressEvent(ArchiveGroup.class, "Downloading Groups", ++progress, groupCount);
          fireTaskComplete(event, progressListeners);
          return groups;
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
      ProgressEvent event = new ProgressEvent(ArchiveGroup.class, e.getMessage(), 0, 0);
      fireTaskInterrupted(event, progressListeners);
      throw e;
    }
  }
  
  /**
   * This method loads all instances of <CODE>ArchiveRequest</CODE> fir the
   * given <CODE>ArchiveGroup</CODE>. This method does not add the instances of 
   * <CODE>ArchiveRequest</CODE> to the <CODE>ArchiveGroup</CODE>, it only
   * returns them.
   * 
   * @param databaseAdaptor The <CODE>DatabaseAdaptor</CODE> to use to connect to the database.
   * @return The instances of <CODE>Archiverequest</CODE> loaded.
   * @throws SQLException Thrown on SQl error.
   */
  public ArchiveRequest[] loadArchiveRequests(CachingDatabaseAdaptor databaseAdaptor) throws java.sql.SQLException
  {
    Connection connection = databaseAdaptor.getConnection();
    try 
    {
      return loadArchiveRequests(connection);
    }
    finally 
    {
      connection.close();
    }
  }
  
  /**
   * This method loads all instances of <CODE>ArchiveRequest</CODE> fir the
   * given <CODE>ArchiveGroup</CODE>. This method does not add the instances of 
   * <CODE>ArchiveRequest</CODE> to the <CODE>ArchiveGroup</CODE>, it only
   * returns them.
   * 
   * @param connection The <CODE>Connection</CODE> to use to connect to the database.
   * @return The instances of <CODE>Archiverequest</CODE> loaded.
   * @throws SQLException Thrown on SQl error.
   */
  public ArchiveRequest[] loadArchiveRequests(Connection connection) throws java.sql.SQLException
  {
    ArrayList requests = new ArrayList();
    try
    {
      fireTaskStarted(new ProgressEvent(this, "Downloading Requests", 0, -1));
      Statement query = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      try
      {
        StringBuffer sql = new StringBuffer("SELECT ARCH_REQ_FILE_NM FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".ARCH_REQ_GRP WHERE ARCH_GRP_FILE_NM = '");
        sql.append(getFileName());
        sql.append("'");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          result.last();
          int max = result.getRow();
          result.beforeFirst();
          int position = 0;
          String fileLocation = getFileLocation();
          fireProgress(new ProgressEvent(this, "Downloading Requests", 0, max));
          while(result.next())
          {
            String fileName = result.getString("ARCH_REQ_FILE_NM");
            requests.add(new ArchiveRequest(fileLocation, fileName));
            fireProgress(new ProgressEvent(this, "Downloading Requests", position, max));
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
    }
    catch(java.sql.SQLException e)
    {
      fireTaskInterrupted(new ProgressEvent(this, e.getMessage(), 0, 0));
      throw e;
    }
    return (ArchiveRequest[])requests.toArray(new ArchiveRequest[requests.size()]);
  }
}