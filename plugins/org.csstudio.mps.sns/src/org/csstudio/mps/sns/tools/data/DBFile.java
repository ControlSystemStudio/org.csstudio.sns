package org.csstudio.mps.sns.tools.data;
import com.cosylab.gui.components.ProgressEvent;
import com.cosylab.gui.components.ProgressListener;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.IOC;
import org.csstudio.mps.sns.tools.data.Signal;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents an EPICS database file.
 * 
 * @author Chris Fowlkes
 */
public class DBFile extends RDBData
{
  /**
   * Holds the name of the corresponding file.
   */
  private String fileName;
  /**
   * Holds the directory which contains the corresponding file.
   */
  private String directoryName;
  /**
   * Holds the modified date of the corresponding file.
   */
  private Date modifiedDate;
  /**
   * Holds the instances of <CODE>Signal</CODE> that have been added to the
   * <CODE>DBFile</CODE>.
   */
  private ArrayList signals = new ArrayList();
  /**
   * Holds the <CODE>IOC</CODE> to which this <CODE>DBFile</CODE> belongs.
   * @attribute 
   */
  private IOC ioc;
  /**
   * Holds the <CODE>File</CODE> used to create the <CODE>DBFile</CODE>.
   */
  private File file;
  /**
   * Holds the name of the column in the RDB for the file name property.
   */
  public final static String FILE_NAME_COLUMN = "EXT_SRC_FILE_NM";
  /**
   * Holds the name of the column in the RDB for the directory name property.
   */
  public final static String DIRECTORY_NAME_COLUMN = "EXT_SRC_DIR_NM";
  /**
   * Holds the name of the column in the RDB for the IOC property.
   */
  public final static String IOC_COLUMN = "DVC_ID";
  /**
   * Holds the name of the RDB table for the class.
   */
  public final static String DB_FILE_TABLE_NAME = "IOC_DB_FILE_ASGN";

  /**
   * Creates a new <CODE>DBFile</CODE>.
   */
  public DBFile()
  {
  }

  /**
   * Creates and initializes a new <CODE>DBFile</CODE>.
   * 
   * @param file The <CODE>File</CODE> that this <CODE>DBFile</CODE> represents.
   */
  public DBFile(File file)
  {
    this();
    setFile(file);
  }
  
  /**
   * Creates and initializes a new <CODE>DBFile</CODE>.
   * 
   * @param fileName The name of the file represented.
   */
  public DBFile(String fileName)
  {
    this();
    setFileName(fileName);
    setDirectoryName(directoryName);
  }
  
  /**
   * Creates and initializes a new <CODE>DBFile</CODE>.
   * 
   * @param fileName The name of the file represented.
   * @param directoryName The location of the file represented.
   */
  public DBFile(String fileName, String directoryName)
  {
    this(fileName);
    setDirectoryName(directoryName);
  }

  /**
   * Creates and initializes a new <CODE>DBFile</CODE>.
   * 
   * @param fileName The name of the file represented.
   * @param directoryName The location of the file represented.
   * @param modifiedDate The modified date of the file represented.
   */
  public DBFile(String fileName, String directoryName, Date modifiedDate)
  {
    this(fileName, directoryName);
    setModifiedDate(modifiedDate);
  }
  
  /**
   * Creates and initializes a new <CODE>DBFile</CODE>.
   * 
   * @param fileName The name of the file represented.
   * @param directoryName The location of the file represented.
   * @param modifiedDate The modified date of the file represented.
   */
  public DBFile(String fileName, String directoryName, long modifiedDate)
  {
    this(fileName, directoryName);
    setModifiedDate(modifiedDate);
  }
  
  /**
   * Creates and initializes a new <CODE>DBFile</CODE>.
   * 
   * @param result The <CODE>ResultSet</CODE> containing the <CODE>DBFile</CODE>.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public DBFile(ResultSet result) throws java.sql.SQLException
  {
    super(result);
  }
  
  /**
   * Gets the name of the corresponding DB file.
   * 
   * @return The name of the file that this <CODE>DBFile</CODE> represents.
   */
  public String getFileName()
  {
    return fileName;
  }

  /**
   * Sets the name of the corresponding DB file.
   * 
   * @param fileName The name of the file that this <CODE>DBFile</CODE> represents.
   */
  public void setFileName(String fileName)
  {
    this.fileName = fileName;
    setFile(null);
    markFieldChanged(FILE_NAME_COLUMN);
  }

  /**
   * Gets the directory name of the corresponding DB file.
   * 
   * @return The directory name of the file that this <CODE>DBFile</CODE> represents.
   */
  public String getDirectoryName()
  {
    return directoryName;
  }

  /**
   * Sets the directory name of the corresponding DB file.
   * 
   * @param directoryName The directory name of the file that this <CODE>DBFile</CODE> represents.
   */
  public void setDirectoryName(String directoryName)
  {
    this.directoryName = directoryName;
    setFile(null);
    markFieldChanged(DIRECTORY_NAME_COLUMN);
  }

  /**
   * Gets the modified date of the corresponding DB file.
   * 
   * @return The modified date of the file that this <CODE>DBFile</CODE> represents.
   */
  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  /**
   * Sets the modified date of the corresponding DB file.
   * 
   * @param modifiedDate The modified date of the file that this <CODE>DBFile</CODE> represents.
   */
  public void setModifiedDate(Date modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }
  
  /**
   * Sets the modified date of the corresponding DB file.
   * 
   * @param modifiedDate The modified date of the file that this <CODE>DBFile</CODE> represents.
   */
  public void setModifiedDate(long modifiedDate)
  {
    this.modifiedDate = new Date(modifiedDate);
  }
  
  /**
   * Convenience method for setting all of the properties concerning the file at 
   * once for files that exist.
   * 
   * @param file The DB file this class represents.
   */
  public void setFile(File file)
  {
    if(file != null)
    {
      setFileName(file.getName());
      setDirectoryName(file.getParent());
      setModifiedDate(file.lastModified());
    }
    this.file = file;
  }
  
  /**
   * Adds the <CODE>Signal</CODE> to the <CODE>DBFile</CODE>. This method does 
   * not add the <CODE>Signal</CODE> to the actual file on disk.
   * 
   * @param signal The <CODE>Signal</CODE> to add to the <CODE>DBFile</CODE>.
   */
  public void addSignal(Signal signal)
  {
    signals.add(signal);
    if(signal.getDBFile(getFileName()) == null)
      signal.addDBFile(this);
  }
  
  /**
   * Gets the number of instances of <CODE>Signal</CODE> that have been added to
   * this <CODE>DBFile</CODE>.
   * 
   * @return The number of instances of <CODE>Signal</CODE> in the <CODE>DBFile</CODE>.
   */
  public int getSignalCount()
  {
    return signals.size();
  }
  
  /**
   * Gets the <CODE>Signal</CODE> at the given index.
   * 
   * @param index The index of the <CODE>Signal</CODE> to return.
   * @return The <CODE>Signal</CODE> at the given index.
   */
  public Signal getSignalAt(int index)
  {
    return (Signal)signals.get(index);
  }

  /**
   * Removes all instances of <CODE>Signal</CODE> from this <CODE>DBFile</CODE>.
   */
  public void removeAllSignals()
  {
    signals.clear();
  }
  
  /**
   * Gets the <CODE>Signal</CODE> with the given ID. If the <CODE>Signal</CODE> 
   * is not found, <CODE>null</CODE> is returned.
   * 
   * @param signalID The ID of the <CODE>Signal</CODE> to return.
   * @return The <CODE>Signal</CODE> with the given ID or <CODE>null</CODE> if none is found.
   */
  public Signal getSignal(String signalID)
  {
    int index = getIndexOfSignal(signalID);
    if(index >= 0)
      return getSignalAt(index);
    else
      return null;
  }
  
  /**
   * Removes the <CODE>Signal</CODE> with the given ID from the 
   * <CODE>DBFile</CODE>. Tis method does not remove the record from the actual
   * file on disk.
   * 
   * @param signalID The ID of the <CODE>Signal</CODE> to remove from the <CODE>DBFile</CODE>.
   */
  public void removeSignal(String signalID)
  {
    int index = getIndexOfSignal(signalID);
    if(index != -1)
    {
      Signal removedSignal = (Signal)signals.remove(index);
      removedSignal.removeDBFile(getFileName());
    }
  }
  
  /**
   * Returns the index of the <CODE>Signal</CODE> with the given ID.
   * 
   * @param signalID The ID of the <CODE>Signal</CODE> of which to return the index.
   * @return The index of the <CODE>Signal</CODE> with the given ID, <CODE>-1</CODE> if it is not found.
   */
  public int getIndexOfSignal(String signalID)
  {
    int signalCount = getSignalCount();
    for(int i=0;i<signalCount;i++) 
    {
      Signal signal = getSignalAt(i);
      if(signal.getID().equals(signalID))
        return i;
    }
    return -1;
  }
  
  /**
   * Sets the <CODE>IOC</CODE> to which this <CODE>DBFile</CODE> belongs.  This 
   * method will cause the <CODE>DBFile</CODE> to add itself to the 
   * <CODE>IOC</CODE> so it is not necessary to call this method and the 
   * <CODE>IOC.addDBFile</CODE>.
   * 
   * @param ioc The <CODE>IOC</CODE> to which the <CODE>DBFile</CODE> belongs.
   */
  public void setIOC(IOC ioc)
  {
    IOC oldIOC = this.ioc;
    this.ioc = ioc;
    if(ioc != null)
    {
      if(ioc.getDBFile(getFileName()) == null)
        ioc.addDBFile(this);
    }
    if(oldIOC != null && oldIOC != ioc)
      oldIOC.removeDBFile(getFileName());
  }
  
  /**
   * Gets the <CODE>IOC</CODE> to which the <CODE>DBFile</CODE> belongs.
   * 
   * @return The <CODE>IOC</CODE> to which the <CODE>DBFile</CODE> belongs.
   */
  public IOC getIOC()
  {
    return ioc;
  }
  
  /**
   * Gets the <CODE>File</CODE> that the <CODE>DBFile</CODE> represents. If a 
   * <CODE>File</CODE> was not passed into the <CODE>setFile</CODE> method, one
   * is created with the values that were passed into the 
   * <CODE>setFileName</CODE> and <CODE>setDirectoryName</CODE> methods.
   * 
   * @return The <CODE>File</CODE> this <CODE>DBFile</CODE> represents.
   */
  public File getFile()
  {
    if(file == null)
      file = new File(getDirectoryName(), getFileName());
    return file;
  }

  /**
   * Returns the <CODE>String</CODE> value of the <CODE>DBFile</CODE>. This is 
   * equivalent to the value of the file name property.
   * 
   * @return The name of the <CODE>DBFile</CODE>.
   */
  public String toString()
  {
    String name = getFileName();
    if(name == null)
      name = "";
    return name;
  }
  
  /**
   * Gets all of the instances of <CODE>Signal</CODE> in the 
   * <CODE>DBFile</CODE>.
   * 
   * @return All of the instances of <CODE>Signal</CODE> in the <CODE>DBFile</CODE>.
   */
  public Signal[] getSignals()
  {
    Signal[] signalArray = new Signal[getSignalCount()];
    for(int i=0;i<signalArray.length;i++) 
      signalArray[i] = getSignalAt(i);
    return signalArray;
  }
  
  /**
   * Inserts the given instances of <CODE>Signal</CODE> into the 
   * <CODE>DBFile</CODE>.
   * 
   * @param connection The <CODE>Connection</CODE> to use to connect to the RDB.
   * @param signals The instances of <CODE>Signal</CODE> to insert into the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void insertSignals(Connection connection, Signal[] signals) throws java.sql.SQLException
  {
    String message = "Adding signals to DB file.";
    fireTaskStarted(new ProgressEvent(this, message, 0, -1));
    try
    {
      PreparedStatement insertQuery = connection.prepareStatement("INSERT INTO EPICS.IOC_DB_FILE_ASGN_SGNL (EXT_SRC_FILE_NM, DVC_ID, SGNL_ID) VALUES(?, ?, ?)");
      try
      {
        insertQuery.setString(1, getFileName());
        insertQuery.setString(2, getIOC().getID());
        fireProgress(new ProgressEvent(this, message, 0, signals.length));
        for(int i=0;i<signals.length;i++)
        {
          insertQuery.setString(3, signals[i].getID());
          insertQuery.execute();
          fireProgress(new ProgressEvent(this, message, i + 1, signals.length));
        }
      }
      finally
      {
        insertQuery.close();
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
    return DB_FILE_TABLE_NAME;
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
    if(rdbFieldName.equals(DIRECTORY_NAME_COLUMN))
      return getDirectoryName();
    else
      if(rdbFieldName.equals(IOC_COLUMN))
        return getIOC().getID();
      else
        if(rdbFieldName.equals(FILE_NAME_COLUMN))
          return getFileName();
    throw new java.lang.IllegalArgumentException(rdbFieldName + " is not a valid field name.");
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
    if(rdbFieldName.equalsIgnoreCase(DIRECTORY_NAME_COLUMN))
      setDirectoryName((String)value);
  }
}