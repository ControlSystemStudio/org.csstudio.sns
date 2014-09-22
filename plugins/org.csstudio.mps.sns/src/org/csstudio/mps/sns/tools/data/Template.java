package org.csstudio.mps.sns.tools.data;
import java.util.*;

import java.sql.*;
import org.csstudio.mps.sns.tools.data.ArchiveGroup;
import org.csstudio.mps.sns.tools.data.Signal;

/**
 * Provides a class to hold the data from the template database table.
 * 
 * @author Chris Fowlkes
 */
public class Template 
{
  /**
   * Holds the name of the template file.
   */
  private String fileName;
  /**
   * Holds the modified date of the template file.
   */
  private Timestamp fileModifiedDate;
  /**
   * Holds the instances of <CODE>Signal</CODE> associated with this 
   * <CODE>Template</CODE>.
   */
  private ArrayList signals = new ArrayList();
  /**
   * Holds the ID of the <CODE>Template</CODE>.
   */
  private String id;
  /**
   * Holds the description of the <CODE>Template</CODE>.
   */
  private String description;
  /**
   * Provides a flag to easily determine if the <CODE>Template</CODE> is already 
   * in the database. <CODE>false</CODE> by default.
   */
  private boolean inDatabase = false;
  /**
   * Holds the macros used in the <CODE>Template</CODE>.
   */
  private ArrayList macros = new ArrayList();
  /**
   * Holds the instances of <CODE>ArchiveGroup</CODE> used in the <CODE>Template</CODE>.
   */
  private ArrayList archiveGroups = new ArrayList();
  /**
   * Holds the instances of <CODE>ArchiveRequest</CODE> used in the <CODE>Template</CODE>.
   */
  private ArrayList archiveRequests = new ArrayList();

  /**
   * Creates a new <CODE>Template</CODE>.
   */
  public Template()
  {
  }

  /**
   * Creates a new <CODE>Template</CODE>.
   * 
   * @param fileName The name of the file from which the <CODE>Template</CODE> was created.
   * @param fileModifiedDate The modified date of the file from which the template was created.
   */
  public Template(String newFileName, Timestamp newFileModifiedDate)
  {
    this();
    setFileName(newFileName);
    setFileModifiedDate(newFileModifiedDate);
  }

  /**
   * Gets the name of the template file.
   * 
   * @return The name of the file associated with the template.
   */
  public String getFileName()
  {
    return fileName;
  }

  /**
   * Sets the name of the template file.
   * 
   * @param newFileName The name of the file associated with the <CODE>Tamplate</CODE>.
   */
  public void setFileName(String newFileName)
  {
    fileName = newFileName;
  }

  /**
   * Gets the modified date of the template file as of when the file was 
   * imported.
   * 
   * @return The modified date of the template file.
   */
  public Timestamp getFileModifiedDate()
  {
    return fileModifiedDate;
  }

  /**
   * Sets the modified date of the template file.
   * 
   * @param newFileModifiedDate The modified date of the template file.
   */
  public void setFileModifiedDate(Timestamp newFileModifiedDate)
  {
    fileModifiedDate = new Timestamp(newFileModifiedDate.getTime());
  }

  /**
   * Adds the given <CODE>Signal</CODE> to the <CODE>Template</CODE>.
   * 
   * @param newSignal The <CODE>Signal</CODE> to add to the <CODE>Template</CODE>.
   */
  public void addSignal(Signal newSignal)
  {
    signals.add(newSignal);
  }

  /**
   * Gets the number of instances of <CODE>Signal</CODE> associated with the 
   * <CODE>Template</CODE>
   * 
   * @return The number of instances of <CODE>Signal</CODE> associated with the <CODE>Template</CODE>
   */
  public int getSignalCount()
  {
    if(signals == null)
      return 0;
    else
      return signals.size();
  }
  
  /**
   * Gets the instance of <CODE>Signal</CODE> at the given index.
   * 
   * @param signalIndex The zero based index of the <CODE>Signal</CODE> to return.
   */
  public Signal getSignalAt(int signalIndex)
  {
    return (Signal)signals.get(signalIndex);
  }

  /**
   * Gets the ID for the <CODE>Template</CODE>.
   * 
   * @return The ID of the <CODE>Template</CODE>.
   */
  public String getID()
  {
    return id;
  }

  /**
   * Sets the ID of the <CODE>Template</CODE>.
   * 
   * @param newID The ID of the <CODE>Template</CODE>.
   */
  public void setID(String newID)
  {
    id = newID;
  }

  /**
   * Gets the description of the <CODE>Template</CODE>.
   * 
   * @return The description of the <CODE>Template</CODE>.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Sets the description of the <CODE>Template</CODE>.
   * 
   * @param newDescription The description of the <CODE>Template</CODE>.
   */
  public void setDescription(String newDescription)
  {
    description = newDescription;
  }

  /**
   * Gets the in database flag for the <CODE>Template</CODE>. This is needed to 
   * determine if the query to save the data should be an insert or update 
   * statement. The default value is <CODE>false</CODE>.
   * 
   * @return <CODE>true</CODE> if the <CODE>Template</CODE> is already in the database, <CODE>false</CODE> if not.
   */
  public boolean isInDatabase()
  {
    return inDatabase;
  }

  /**
   * Allows the in database flag to be set for the <CODE>Template</CODE>. This 
   * is needed to determine if the query to save the data should be an insert or 
   * update statement. The default value is <CODE>false</CODE>.
   * 
   * @param inDatabase Pass as <CODE>true</CODE> if the <CODE>Template</CODE> is already in the database, <CODE>false</CODE> if not.
   */
  public void setInDatabase(boolean newInDatabase)
  {
    inDatabase = newInDatabase;
  }

  /**
   * Provides the <CODE>String</CODE> representation for the 
   * <CODE>Template</CODE>. This methos returns the value of the ID property.
   * 
   * @return The <CODE>String</CODE> representation of the <CODE>Template</CODE>, which is the value of the ID property.
   */
  public String toString()
  {
    return getID();
  }

  /**
   * Gets the number of macros in the <CODE>Template</CODE>.
   * 
   * @return The number of macros in the <CODE>Template</CODE>.
   */
  public int getMacroCount()
  {
    return macros.size();
  }

  /**
   * Gets the macro at the given index.
   * 
   * @param index The index of the macro to return.
   * @return The macro at the given index.
   */
  public String getMacroAt(int index)
  {
    return macros.get(index).toString();
  }

  /**
   * Adds the macro to the <CODE>template</CODE>.
   * 
   * @param macro The macro to add to the <CODE>Template</CODE>.
   */
  public void addMacro(String macro)
  {
    macros.add(macro);
  }

  /**
   * Determines if the given macro is already in the <CODE>Template</CODE>.
   * 
   * @param macro The macro to look for.
   * @return <CODE>true</CODE> if the macro is in the <CODE>Template</CODE>, <CODE>false</CODE> if not.
   */
  public boolean containsMacro(String macro)
  {
    return macros.contains(macro);
  }
  
  /**
   * Gets the number of groups associated with the <CODE>Template</CODE>.
   * 
   * @return The number of instances of <CODE>ArchiveGroup</CODE> in the <CODE>Template</CODE>.
   */
  public int getArchiveGroupCount()
  {
    return archiveGroups.size();
  }

  /**
   * Adds the given <CODE>ArchiveGroup</CODE> to the <CODE>Template</CODE>. This 
   * method adds the <CODE>ArchiveGroup</CODE> to the <CODE>Template</CODE> 
   * without checking to see if the <CODE>Template</CODE> already contains an
   * <CODE>ArchiveGroup</CODE> with the same ID.
   * 
   * @param newArchiveGroup The <CODE>ArchiveGroup</CODE> to add to the <CODE>Template</CODE>.
   */
  public void addArchiveGroup(ArchiveGroup newArchiveGroup)
  {
    archiveGroups.add(newArchiveGroup);
  }

  /**
   * Adds the given <CODE>ArchiveRequest</CODE> to the <CODE>Template</CODE>. This 
   * method adds the <CODE>ArchiveRequest</CODE> to the <CODE>Template</CODE> 
   * without checking to see if the <CODE>Template</CODE> already contains an
   * <CODE>ArchiveRequest</CODE> with the same ID.
   * 
   * @param newArchiveRequest The <CODE>ArchiveRequest</CODE> to add to the <CODE>Template</CODE>.
   */
  public void addArchiveRequest(ArchiveRequest newArchiveRequest)
  {
    archiveRequests.add(newArchiveRequest);
  }

  /**
   * Gets the number of instances of <CODE>ArchiveRequest</CODE> that have been 
   * added to the <CODE>Template</CODE>.
   * 
   * @return The number of instances of <CODE>ArchiveRequest</CODE> in the <CODE>Template</CODE>.
   */
  public int getArchiveRequestCount()
  {
    return archiveRequests.size();
  }

  /**
   * Gets The <CODE>ArchiveGroup</CODE> at the given index.
   * 
   * @param index The index of the <CODE>ArchiveGroup</CODE> to return.
   * @return The <CODE>ArchiveGroup</CODE> at the given index.
   */
  public ArchiveGroup getArchiveGroupAt(int index)
  {
    return (ArchiveGroup)archiveGroups.get(index);
  }

  /**
   * Gets The <CODE>ArchiveRequest</CODE> at the given index.
   * 
   * @param index The index of the <CODE>ArchiveRequest</CODE> to return.
   * @return The <CODE>ArchiveGroup</CODE> at the given index.
   */
  public ArchiveRequest getArchiveRequestAt(int index)
  {
    return (ArchiveRequest)archiveRequests.get(index);
  }

  /**
   * Gets the <CODE>ArchiveRequest</CODE> with the given file name. If the 
   * <CODE>Template</CODE> does not contain a <CODE>ArchiveRequest</CODE> with 
   * the given file name, <CODE>null</CODE> is returned.
   * 
   * @param requestFileName The file name of the <CODE>ArchiveRequest</CODE> to return.
   * @return The <CODE>ArchiveRequest</CODE> with the given file name, or <CODE>null</CODE>.
   */
  public ArchiveRequest getArchiveRequest(String requestFileName)
  {
    int requestCount = getArchiveRequestCount();
    for(int i=0;i<requestCount;i++)
    {
      ArchiveRequest currentRequest = getArchiveRequestAt(i);
      if(requestFileName.equals(currentRequest.getFileName()))
        return currentRequest;
    }//for(int i=0;i<requestCount;i++)
    return null;
  }

  /**
   * Gets the <CODE>ArchiveGroup</CODE> with the given file name. If the 
   * <CODE>Template</CODE> does not contain a <CODE>ArchiveGroup</CODE> with 
   * the given file name, <CODE>null</CODE> is returned.
   * 
   * @param groupFileName The file name of the <CODE>ArchiveGroup</CODE> to return.
   * @return The <CODE>ArchiveGroup</CODE> with the given file name, or <CODE>null</CODE>.
   */
  public ArchiveGroup getArchiveGroup(String groupFileName)
  {
    int groupCount = getArchiveGroupCount();
    for(int i=0;i<groupCount;i++)
    {
      ArchiveGroup currentGroup = getArchiveGroupAt(i);
      if(groupFileName.equals(currentGroup.getFileName()))
        return currentGroup;
    }//for(int i=0;i<groupCount;i++)
    return null;
  }
}