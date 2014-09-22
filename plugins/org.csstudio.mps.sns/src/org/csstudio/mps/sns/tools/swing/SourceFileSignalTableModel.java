package org.csstudio.mps.sns.tools.swing;

import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.EpicsRecordType;
import org.csstudio.mps.sns.tools.data.Signal;
import org.csstudio.mps.sns.tools.data.SignalType;
import org.csstudio.mps.sns.tools.swing.AbstractSignalTableModel;
import java.math.*;

import java.sql.*;

import java.util.*;

import javax.swing.*;
import org.csstudio.mps.sns.view.MPSBrowserView;

/**
 * Provides a model for the DB tab's table in the signal functions interface.
 * 
 * @author Chris Fowlkes
 */
public class SourceFileSignalTableModel extends AbstractSignalTableModel 
{
//  /**
//   * Holds the name of the signal ID column.
//   */
//  public static String signalIDColumnName = "SGNL_ID";
//  /**
//   * Holds the name of the device ID column.
//   */
//  public static String deviceIDColumnName = "DVC_ID";
//  /**
//   * Holds the name of the record type ID column.
//   */
//  public static String recordTypeIDColumnName = "REC_TYPE_ID";
//  /**
//   * Holds the name of the machine protection indicator column.
//   */
//  public static String machineProtectionIndicatorColumnName = "MCHN_PROT_IND";
//  /**
//   * Holds the name of the external source column.
//   */
//  public static String externalSourceColumnName = "EXT_SRC";
//  /**
//   * Holds the name of the multimode indicator column.
//   */
//  public static String multimodeIndicatorColumnName = "MULTIMODE_IND";
//  /**
//   * Holds the name of the invalid id indicator column.
//   */
//  public static String invalidIDIndicatorColumnName = "INVALID_ID_IND";
//  /**
//   * Holds the name of the signal name column.
//   */
//  public static String signalNameColumnName = "SGNL_NM";
//  /**
//   * Holds the name of the signal ID alias column.
//   */
//  public static String signalIDAliasColumnName = "SGNL_ID_ALIAS";
//  /**
//   * Holds the name of the bulk indicator column.
//   */
//  public static String bulkIndicatorColumnName = "BULK_IND";
////  /**
////   * Holds the name of the external source file modification date column.
////   */
////  public static String externalSourceFileModificationDateColumnName = "EXT_SRC_FILE_MOD_DTE";
//  /**
//   * Holds the name of the modified by UID column.
//   */
//  public static String modifiedByUIDColumnName = "MOD_BY_UID";
//  /**
//   * Holds the name of the modified date column.
//   */
//  public static String modifiedDateColumnName = "MOD_DTE";
//  /**
//   * Holds the name of the external source file name column.
//   */
//  public static String externalSourceFileNameColumnName = "EXT_SRC_FILE_NM";
//  /**
//   * Holds the name of the archive indicator column.
//   */
//  public static String archiveIndicatorColumnName = "ARCH_IND";
//  /**
//   * Holds the name of the archive frequency column.
//   */
//  public static String archiveFrequencyColumnName = "ARCH_FREQ";
//  /**
//   * Holds the name of the archive type column.
//   */
//  public static String archiveTypeColumnName = "ARCH_TYPE";
//  /**
//   * Holds the name of the cable complete indicator column.
//   */
//  public static String cableCompleteIndicatorColumnName = "CBL_CMPLT_IND";
//  /**
//   * Holds the name of the alarm indicator column.
//   */
//  public static String alarmIndicatorColumnName = "ALARM_IND";
  /**
   * Holds the names of the columns visible in the table.
   */
  private ArrayList visibleColumnNames = new ArrayList(19);
  /**
   * Holds the <CODE>DataSource</CODE> for the window. This is used to make 
   * database connections.
   */
  private Connection oracleConnection;
  /**
   * Flag used to determine if a change has been made.
   */
  private boolean commitNeeded = false;
  /**
   * Holds the name of the source file for which to display the data.
   */
  private String sourceFileName;
  /**
   * Holds the <CODE>JProgressBar</CODE> used to dislpay progress information to 
   * the user.
   */
  private JProgressBar progressBar;
  /**
   * Holds the <CODE>messageLabel</CODE> used to convey status messages to the 
   * user.
   */
  private JLabel messageLabel;
  /**
   * Holds the names of the columns in the table that are read only.
   */
  private String[] readOnlyColumnNames;

  /**
   * Creates a new <CODE>SourceFileSignalTableModel</CODE>.
   */
  public SourceFileSignalTableModel()
  {
    visibleColumnNames.add(SIGNAL_ID_FIELD_NAME);
    visibleColumnNames.add(DEVICE_ID_FIELD_NAME);
    visibleColumnNames.add(RECORD_TYPE_ID_FIELD_NAME);
    visibleColumnNames.add(MACHINE_PROTECTION_INDICATOR_FIELD_NAME);
    visibleColumnNames.add(EXTERNAL_SOURCE_FIELD_NAME);
    visibleColumnNames.add(MULTIMODE_INDICATOR_FIELD_NAME);
    visibleColumnNames.add(INVALID_ID_INDICATOR_FIELD_NAME);
    visibleColumnNames.add(SIGNAL_NAME_FIELD_NAME);
    visibleColumnNames.add(SIGNAL_ID_ALIAS_FIELD_NAME);
    visibleColumnNames.add(BULK_INDICATOR_FIELD_NAME);
//    visibleColumnNames.add(externalSourceFileModificationDateColumnName);
    visibleColumnNames.add(MODIFIED_BY_UID_FIELD_NAME);
    visibleColumnNames.add(MODIFIED_DATE_FIELD_NAME);
    visibleColumnNames.add(ARCHIVE_INDICATOR_FIELD_NAME);
    visibleColumnNames.add(ARCHIVE_FREQUENCY_FIELD_NAME);
    visibleColumnNames.add(ARCHIVE_TYPE_FIELD_NAME);
    visibleColumnNames.add(CABLE_COMPLETE_INDICATOR_FIELD_NAME);
    visibleColumnNames.add(ALARM_INDICATOR_FIELD_NAME);
  }

  /**
   * Gets the number of columns in the table.
   * 
   * @return The number of columns in the table.
   */
  public int getColumnCount()
  {
    return visibleColumnNames.size();
  }

  /**
   * Gets the value of the given cell.
   * 
   * @param rowIndex The row index of the cell of which to return the value.
   * @param columnIndex The column index of the cell of which to return the value.
   */
  public Object getValueAt(int rowIndex, int columnIndex)
  {
    return getValueAt(rowIndex, getColumnName(columnIndex));
  }

  /**
   * Gets the class of the given column.
   * 
   * @param columnIndex The index of the column of which to return the value.
   * @return The <CODE>Class</CODE> of the column at the given index.
   */
  public Class getColumnClass(int columnIndex)
  {
    return super.getColumnClass(getColumnName(columnIndex));
  }

  /**
   * Gets the name of the column at the given index.
   * 
   * @param The index of the column for which to return the name.
   * @return The name of the column at the given index.
   */
  public String getColumnName(int column)
  {
    return visibleColumnNames.get(column).toString();
  }

  /**
   * Sets the value of the given cell.
   * 
   * @param aValue The new value for the given cell.
   * @param rowIndex The row index of the cell for which to set the value.
   * @param columnIndex The column index of the cell for which to set the value.
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    if(! isCellEditable(rowIndex, columnIndex))
      throw new java.lang.IllegalArgumentException("The '" + getColumnName(columnIndex) + "' column is not editable.");
    String columnName = getColumnName(columnIndex);
    Signal currentSignal = getSignalAt(rowIndex);
    try
    {
      updateDatabaseField(aValue, columnName, currentSignal.getID());
      if(columnName.equals(RECORD_TYPE_ID_FIELD_NAME))
      {
        EpicsRecordType newType = new EpicsRecordType(aValue.toString());
        currentSignal.getType().setRecordType(newType);
      }//if(columnName.equals(recordTypeIDColumnName))
      else
        if(columnName.equals(MACHINE_PROTECTION_INDICATOR_FIELD_NAME))
          if(((Boolean)aValue).booleanValue())
            currentSignal.getType().setMachineProtection("Y");
          else
            currentSignal.getType().setMachineProtection("N");
        else
          if(columnName.equals(MULTIMODE_INDICATOR_FIELD_NAME))
            if(((Boolean)aValue).booleanValue())
              currentSignal.getType().setMultimode("Y");
            else
              currentSignal.getType().setMultimode("N");
          else
            if(columnName.equals(INVALID_ID_INDICATOR_FIELD_NAME))
              if(((Boolean)aValue).booleanValue())
                currentSignal.setInvalidID("Y");
              else
                currentSignal.setInvalidID("N");
            else
              if(columnName.equals(SIGNAL_ID_ALIAS_FIELD_NAME))
                if(aValue == null)
                  currentSignal.setIDAlias(null);
                else
                  currentSignal.setIDAlias(aValue.toString());
              else
                if(columnName.equals(BULK_INDICATOR_FIELD_NAME))
                  if(((Boolean)aValue).booleanValue())
                    currentSignal.getType().setBulk("Y");
                  else
                    currentSignal.getType().setBulk("N");
                else
                  if(columnName.equals(ARCHIVE_INDICATOR_FIELD_NAME))
                    if(((Boolean)aValue).booleanValue())
                      currentSignal.setArchiveIndicator("Y");
                    else
                      currentSignal.setArchiveIndicator("N");
                  else
                    if(columnName.equals(ARCHIVE_FREQUENCY_FIELD_NAME))
                    {
                      String stringValue = aValue.toString();
                      BigDecimal bigDecimalValue = new BigDecimal(stringValue);
                      currentSignal.setArchiveFrequency(bigDecimalValue);
                    }//if(columnName.equals(archiveFrequencyColumnName))
                    else
                      if(columnName.equals(ARCHIVE_TYPE_FIELD_NAME))
                        currentSignal.setArchiveType(aValue.toString());
                      else
                        if(columnName.equals(CABLE_COMPLETE_INDICATOR_FIELD_NAME))
                          if(((Boolean)aValue).booleanValue())
                            currentSignal.setCableCompleteIndicator("Y");
                          else
                            currentSignal.setCableCompleteIndicator("N");
                        else
                          if(columnName.equals(ALARM_INDICATOR_FIELD_NAME))
                            if(((Boolean)aValue).booleanValue())
                              currentSignal.setAlarmIndicator("Y");
                            else
                              currentSignal.setAlarmIndicator("N");
                          else
                            throw new java.lang.IllegalArgumentException("Column '" + columnName + " should not be editable.");
      setCommitNeeded(true);
      fireTableCellUpdated(rowIndex, columnIndex);
    }//try
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }//catch(java.sql.SQLException ex)
  }

  /**
   * Updates the record type field for the given <CODE>Signal</CODE>.
   * 
   * @param newRecordType The new record type ID.
   * @param signal The <CODE>Signal</CODE> of which to set the record type.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  private void updateDatabaseField(Object newValue, String fieldName, String signalID) throws java.sql.SQLException
  {
    Statement query = getConnection().createStatement();
    try
    {
      StringBuffer sql = new StringBuffer("UPDATE ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".SGNL_REC SET ");
      sql.append(fieldName);
      sql.append(" = ");
      if(newValue == null)
        sql.append("NULL");
      else
      {
        if(newValue instanceof Boolean)
          if(((Boolean)newValue).booleanValue())
            sql.append("'Y'");
          else
            sql.append("'N'");
        else
        {
          sql.append("'");
          sql.append(newValue.toString());
          sql.append("'");
        }//else
      }//else
      sql.append(" WHERE SGNL_ID = '");
      sql.append(signalID);
      sql.append("'");
    }//try
    finally
    {
      query.close();
    }//finally
  }

  /**
   * Determines if the given cell is editable or not.
   * 
   * @param rowIndex The row index of the cell to check.
   * @param columnIndex The column index of the row to check.
   * @return <CODE>true</CODE> if the given cell is editable, <CODE>false</CODE> if is not editable.
   */
  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    String columnName = getColumnName(columnIndex);
    String[] readOnlyColumnNames = getReadOnlyColumnNames();
    return Arrays.binarySearch(readOnlyColumnNames, columnName) < 0;
  }

  /**
   * Gets the names of the columns that are read only in the model in a sorted
   * array. This method does not determine if the columns are visible, so the 
   * names of hidden columns will be returned if those columns are read only.
   * 
   * @return The names of the read only columns.
   */
  public String[] getReadOnlyColumnNames()
  {
    if(readOnlyColumnNames == null)
    {
      readOnlyColumnNames = new String[7];
      readOnlyColumnNames[0] = SIGNAL_ID_FIELD_NAME;
      readOnlyColumnNames[1] = DEVICE_ID_FIELD_NAME;
      readOnlyColumnNames[2] = EXTERNAL_SOURCE_FIELD_NAME;
      readOnlyColumnNames[3] = SIGNAL_NAME_FIELD_NAME;
//      readOnlyColumnNames[4] = externalSourceFileModificationDateColumnName;
      readOnlyColumnNames[5] = MODIFIED_BY_UID_FIELD_NAME;
      readOnlyColumnNames[6] = MODIFIED_DATE_FIELD_NAME;
      Arrays.sort(readOnlyColumnNames);
    }//if(readOnlyColumnNames == null)
    return readOnlyColumnNames;
  }

  /**
   * This method returns the names of the columns that are editable. This is
   * done by calling <CODE>getReadOnlyColumnNames()</CODE> and returning the 
   * names of the columns not in that group. Unlike that method, this one will 
   * only return the names of columns if they are visible.
   * 
   * @return The names of the columns in the model that are editable.
   */
  public String[] getEditableColumnNames()
  {
    int columnCount = getColumnCount();
    String[] readOnlyColumnNames = getReadOnlyColumnNames();
    //Holding the names of the editable columns in an array list. Don't know how
    //many there will be because read only columns includes hidden column names.
    ArrayList editableColumnNames = new ArrayList();
    for(int i=0;i<columnCount;i++)
    {
      String currentColumnName = getColumnName(i);
      if(Arrays.binarySearch(readOnlyColumnNames, currentColumnName) < 0)
        editableColumnNames.add(currentColumnName);
    }//for(int i=0;i<columnCount;i++)
    String[] editableColumnNamesArray = new String[editableColumnNames.size()];
    editableColumnNamesArray = (String[])editableColumnNames.toArray(editableColumnNamesArray);
    Arrays.sort(editableColumnNamesArray);
    return editableColumnNamesArray;
  }
  
  /**
   * Gets the <CODE>Connection</CODE> used by the model to connect to the 
   * database.
   *
   * @return The <CODE>Connection</CODE> used to connect to the database.
   */
  public Connection getConnection()
  {
    return oracleConnection;
  }
  
  /**
   * Sets the <CODE>DataSource</CODE> used by the window to connect to the 
   * database.
   *
   * @param newConnection The <CODE>Connection</CODE> to use to connect to the database.
   */
  public void setConnection(Connection newConnection)
  {
    oracleConnection = newConnection;
  }

  /**
   * Sets the value of the commit needed flag. This method should be called when 
   * a commit is done on the <CODE>Connection</CODE> that was passed into the 
   * <CODE>setConnection</CODE> method, as this class has no way of telling if a 
   * commit has been done or not.
   * 
   * @param newCommitNeeded Pass as <CODE>false</CODE> to reset the flag.
   */
  public void setCommitNeeded(boolean newCommitNeeded)
  {
    commitNeeded = newCommitNeeded;
  }

  /**
   * Gets the value of the commit needed flag. This method returns 
   * <CODE>true</CODE> if an operation that requires a commit has been performed 
   * on the <CODE>Connection</CODE> that was passed into the 
   * <CODE>setConnection</CODE> method since the commit flag was last reset.
   * 
   * @return <CODE>true</CODE> if a change requiring a commit has been made, <CODE>false</CODE> otherwise.
   */
  public boolean isCommitNeeded()
  {
    return commitNeeded;
  }

  /**
   * Gets the name of the source file to which all the instances of 
   * <CODE>Signal</CODE> belong.
   * 
   * @return The name of the source file being displayed.
   */
  public String getSourceFileName()
  {
    return sourceFileName;
  }

  /**
   * Sets the name of the source file for which to display the 
   * <CODE>Signal</CODE> data. This method calls the <CODE>Refresh()</CODE> 
   * method.
   * 
   * @param newSourceFileName The name of the source file to display.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void setSourceFileName(String newSourceFileName) throws java.sql.SQLException
  {
    sourceFileName = newSourceFileName;
    refresh();
  }

  /**
   * Refreshes the data in the table.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void refresh() throws java.sql.SQLException
  {
    try
    {
      setMessageText("Loading Signals...");
      setProgressIndeterminate(true);
      String sourceFileName = getSourceFileName();
      if(sourceFileName == null)
        clear();
      else
      {
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        StringBuffer whereClause = new StringBuffer(MPSBrowserView.SCHEMA);
        whereClause.append(".SGNL_REC WHERE EXT_SRC_FILE_NM = ?");
        PreparedStatement countQuery = getConnection().prepareStatement(sql.toString());
        try
        {
          countQuery.setString(1, sourceFileName);
          ResultSet countResult = countQuery.executeQuery();
          try
          {
            countResult.next();
            setProgressMaximum(countResult.getInt(1));
          }
          finally
          {
            countResult.close();
          }
        }
        finally
        {
          countQuery.close();
        }
        sql.append(whereClause);
        sql = new StringBuffer("SELECT * FROM ");
        sql.append(whereClause);
        sql.append(" ORDER BY SGNL_ID");
        PreparedStatement query = getConnection().prepareStatement(sql.toString());
        try
        {
          ResultSet signalRecords = query.executeQuery();
          try
          {
            clear();
            int progress = 0;
            setProgressValue(0);
            setProgressIndeterminate(false);
            //Loop through signals returned in the signal query.
            while(signalRecords.next())
            {
              String signalID = signalRecords.getString("sgnl_id");
              Signal newSignal = new Signal(signalID);
              newSignal.setDevice(new Device(signalRecords.getString("dvc_id")));
              String signalRecordType = signalRecords.getString("rec_type_id");
              SignalType newSignalType = new SignalType();
              newSignalType.setRecordType(new EpicsRecordType(signalRecordType));
              newSignal.setType(newSignalType);
              newSignalType.setMachineProtection(signalRecords.getString("mchn_prot_ind"));
              newSignal.setExternalSource(signalRecords.getString("ext_src"));
              newSignalType.setMultimode(signalRecords.getString("multimode_ind"));
              newSignal.setInvalidID(signalRecords.getString("invalid_id_ind"));
              newSignal.setName(signalRecords.getString("sgnl_nm"));
              newSignal.setIDAlias(signalRecords.getString("sgnl_id_alias"));
              newSignalType.setBulk(signalRecords.getString("bulk_ind"));
//              newSignal.setExternalSourceFileModifiedDate(signalRecords.getDate("ext_src_file_mod_dte"));
              newSignal.setModifiedByUID(signalRecords.getString("mod_by_uid"));
              newSignal.setModifiedDate(signalRecords.getDate("mod_dte"));
//              newSignal.setExternalSourceFileName(signalRecords.getString("ext_src_file_nm"));
              newSignal.setArchiveIndicator(signalRecords.getString("arch_ind"));
              newSignal.setArchiveFrequency(signalRecords.getBigDecimal("arch_freq"));
              newSignal.setArchiveType(signalRecords.getString("arch_type"));
              newSignal.setCableCompleteIndicator(signalRecords.getString("cbl_cmplt_ind"));
              newSignal.setAlarmIndicator(signalRecords.getString("alarm_ind"));
              addSignal(newSignal);
              setProgressValue(++progress);
            }//while (records.next())
          }//try
          finally
          {
            signalRecords.close();
          }//finally
        }//try
        finally
        {
          query.close();
        }//finally
      }//else
    }//try
    finally
    {
      setMessageText(" ");
      setProgressValue(0);
      setProgressIndeterminate(false);
    }
  }

  /**
   * Holds the <CODE>JProgressBar</CODE> used to display progress information
   * for the user.
   * 
   * @param newProgressBar The <CODE>JProgressBar</CODE> used to display progress for the user.
   */
  public void setProgressBar(JProgressBar newProgressBar)
  {
    progressBar = newProgressBar;
  }

  /**
   * Gets the <CODE>JProgressBar</CODE> used to display progress informatino for 
   * the user.
   * 
   * @return The <CODE>JProgressBar</CODE> used to display progress information for the user.
   */
  public JProgressBar getProgressBar()
  {
    return progressBar;
  }

  /**
   * Sets the text that appears in the message label.
   * 
   * @param newMessageText The text to put in the message <CODE>JLabel</CODE>.
   */
  private void setMessageText(final String newMessageText)
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
  private void setProgressIndeterminate(final boolean newProgressIndeterminate)
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
  private void setProgressMaximum(final int newProgressMaximum)
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
  private void setProgressValue(final int newProgressValue)
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
   * Sets the <CODE>JLabel</CODE> used to relay status information to the user.
   * 
   * @param newMessageLabel The <CODE>JLabel</CODE> used to relay staus information to the user.
   */
  public void setMessageLabel(JLabel newMessageLabel)
  {
    messageLabel = newMessageLabel;
  }

  /**
   * Gets the <CODE>JLabel</CODE> used to relay status information to the user.
   * 
   * @returns The <CODE>JLabel</CODE> used to relay status information to the user.
   */
  public JLabel getMessageLabel()
  {
    return messageLabel;
  }
}