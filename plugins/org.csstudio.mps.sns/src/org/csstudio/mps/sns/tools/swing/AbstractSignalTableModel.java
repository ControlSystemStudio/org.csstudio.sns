package org.csstudio.mps.sns.tools.swing;
import java.io.*;

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import org.csstudio.mps.sns.tools.data.Signal;
import org.csstudio.mps.sns.tools.data.SignalType;

/**
 * holds the methods common to all table models containing a list of data 
 * representing instances of <CODE>Signal</CODE>.
 * 
 * @author Chris Fowlkes
 */
public abstract class AbstractSignalTableModel extends AbstractTableModel 
{
  /**
   * Holds the instances of <CODE>Signal</CODE> displayed in the table.
   */
  private ArrayList signals;
  /**
   * Holds the name of the signal ID column.
   */
  public final static String SIGNAL_ID_FIELD_NAME = "SGNL_ID";
  /**
   * Holds the name of the device ID column.
   */
  public final static String DEVICE_ID_FIELD_NAME = "DVC_ID";
  /**
   * Holds the name of the record type ID column.
   */
  public final static String RECORD_TYPE_ID_FIELD_NAME = "REC_TYPE_ID";
  /**
   * Holds the name of the machine protection indicator column.
   */
  public final static String MACHINE_PROTECTION_INDICATOR_FIELD_NAME = "MCHN_PROT_IND";
  /**
   * Holds the name of the external source column.
   */
  public final static String EXTERNAL_SOURCE_FIELD_NAME = "EXT_SRC";
  /**
   * Holds the name of the multimode indicator column.
   */
  public final static String MULTIMODE_INDICATOR_FIELD_NAME = "MULTIMODE_IND";
  /**
   * Holds the name of the invalid id indicator column.
   */
  public final static String INVALID_ID_INDICATOR_FIELD_NAME = "INVALID_ID_IND";
  /**
   * Holds the name of the signal name column.
   */
  public final static String SIGNAL_NAME_FIELD_NAME = "SGNL_NM";
  /**
   * Holds the name of the signal ID alias column.
   */
  public final static String SIGNAL_ID_ALIAS_FIELD_NAME = "SGNL_ID_ALIAS";
  /**
   * Holds the name of the bulk indicator column.
   */
  public final static String BULK_INDICATOR_FIELD_NAME = "BULK_IND";
//  /**
//   * Holds the name of the external source file modification date column.
//   */
//  public final static String externalSourceFileModificationDateFieldName = "EXT_SRC_FILE_MOD_DTE";
  /**
   * Holds the name of the modified by UID column.
   */
  public final static String MODIFIED_BY_UID_FIELD_NAME = "MOD_BY_UID";
  /**
   * Holds the name of the modified date column.
   */
  public final static String MODIFIED_DATE_FIELD_NAME = "MOD_DTE";
//  /**
//   * Holds the name of the external source file name column.
//   */
//  public final static String EXTERNAL_SOURCE_FILE_NAME_FIELD_NAME = "EXT_SRC_FILE_NM";
  /**
   * Holds the name of the archive indicator column.
   */
  public final static String ARCHIVE_INDICATOR_FIELD_NAME = "ARCH_IND";
  /**
   * Holds the name of the archive frequency column.
   */
  public final static String ARCHIVE_FREQUENCY_FIELD_NAME = "ARCH_FREQ";
  /**
   * Holds the name of the archive type column.
   */
  public final static String ARCHIVE_TYPE_FIELD_NAME = "ARCH_TYPE";
  /**
   * Holds the name of the cable complete indicator column.
   */
  public final static String CABLE_COMPLETE_INDICATOR_FIELD_NAME = "CBL_CMPLT_IND";
  /**
   * Holds the name of the alarm indicator column.
   */
  public final static String ALARM_INDICATOR_FIELD_NAME = "ALARM_IND";
  
  /**
   * Creates a new <CODE>AbstractSignalTableModel</CODE>.
   */
  public AbstractSignalTableModel()
  {
  }

  /**
   * Sets all of the instances of <CODE>Signal</CODE> for the table at once.
   * This class keeps the <CODE>ArrayList</CODE> passed into the method so that
   * changing the contents of the <CODE>ArrayList</CODE> after this method exits
   * will change the contents of the model.
   * 
   * @param signals The instances of <CODE>Signal</CODE> that hold the data in the model.
   */
  public void setSignals(ArrayList signals)
  {
    this.signals = signals;
    fireTableDataChanged();
  }

  /**
   * Returns the number of rows in the table. This is equivalent to the number 
   * of instances of <CODE>Signal</CODE> in the table.
   * 
   * @return The number of rows in the model.
   */
  public int getRowCount()
  {
    if(signals == null)
      return 0;
    else
      return signals.size();
  }

  /**
   * Returns the <CODE>Signal</CODE> for a given row in the table.
   * 
   * @param row The row number of the <CODE>Signal</CODE> to return.
   * @return The <CODE>Signal</CODE> at the given row.
   */
  public Signal getSignalAt(int row)
  {
    return (Signal)signals.get(row);
  }

  /**
   * Finds and returns the <CODE>Signal</CODE> with the given ID. If no 
   * <CODE>Signal</CODE> is found with the given ID, <CODE>null</CODE> is 
   * returned.
   * 
   * @param signalID The ID of the <CODE>Signal</CODE> to return.
   * @return The <CODE>Signal</CODE> with the given ID or <CODE>null</CODE> if it was found.
   */
  public Signal getSignal(String signalID)
  {
    int row = findSignalRow(signalID);
    if(row >= 0)
      return getSignalAt(row);
    else
      return null;
  }

  /**
   * Finds and returns the row index for the <CODE>Signal</CODE> with the given 
   * ID. If no <CODE>Signal</CODE> is found with the given ID, <CODE>-1</CODE> 
   * is returned.
   * 
   * @param signalID The ID of the <CODE>Signal</CODE> for which to return the row index.
   * @return The row index of the <CODE>Signal</CODE> with the given ID or <CODE>-1</CODE> if it was found.
   */
  public int findSignalRow(String signalID)
  {
    int signalCount = getRowCount();
    for(int i=0;i<signalCount;i++)
    {
      Signal currentSignal = getSignalAt(i);
      if(currentSignal.getID().equals(signalID))
        return i;
    }//for(int i=0;i<signalCount;i++)
    return -1;
  }

  /**
   * Adds the given <CODE>Signal</CODE> to the model. This method is thread 
   * fires the appropriate events in a thread safe manner using the 
   * <CODE>SwingUtilities.invokeLater</CODE> method.
   * 
   * @param newSignal The signal to add to the model.
   */
  public void addSignal(final Signal newSignal)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        if(signals == null)
          signals = new ArrayList();
        signals.add(newSignal);
        int newRow = getRowCount() - 1;
        fireTableRowsInserted(newRow, newRow);
      }
    });
  }

  /**
   * Clears the model. <CODE>SwingUtilities.invokeLater</CODE> is used to make 
   * this method thread safe.
   */
  public void clear()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        if(signals != null)
        {
          int signalCount = getRowCount();
          if(signalCount > 0)
          {
            signals.clear();
            fireTableRowsDeleted(0, signalCount - 1);
          }//if(signalCount > 0)
        }//if(signals != null)
      }
    });
  }

  /**
   * Gets the value for the given cell. The value returned is the value for the 
   * <CODE>Signal</CODE> at the given row index's value for the property that 
   * corresponds to the given database field. To get the ID of the 
   * <CODE>Signal</CODE> at row N, call as 
   * <CODE>getValueAt(N, "SGNL_ID")</CODE>.
   * 
   * @param rowIndex The row index of the <CODE>Signal</CODE> for which to return the value of the given property.
   * @param fieldName The database field name that corresponds to the property of which to return the value.
   * @return The value of the given property for the <CODE>Signal</CODE> at the given row index.
   */
  public Object getValueAt(int rowIndex, String fieldName)
  {
    Signal signal = getSignalAt(rowIndex);
    if(fieldName.equals(SIGNAL_ID_FIELD_NAME))
      return signal.getID();
    if(fieldName.equals(DEVICE_ID_FIELD_NAME))
      return signal.getDevice();
    if(fieldName.equals(RECORD_TYPE_ID_FIELD_NAME))
      return signal.getType().getRecordType();
    if(fieldName.equals(MACHINE_PROTECTION_INDICATOR_FIELD_NAME))
    {
      String machineProtection = signal.getType().getMachineProtection();
      if(machineProtection != null && machineProtection.equals("Y"))
        return Boolean.TRUE;
      else
        return Boolean.FALSE;
    }//if(fieldName.equals(machineProtectionIndicatorFieldName))
    if(fieldName.equals(EXTERNAL_SOURCE_FIELD_NAME))
      return signal.getExternalSource();
    if(fieldName.equals(MULTIMODE_INDICATOR_FIELD_NAME))
    {
      String multimode = signal.getType().getMultimode();
      if(multimode != null && multimode.equals("Y"))
        return Boolean.TRUE;
      else
        return Boolean.FALSE;
    }//if(fieldName.equals(multimodeIndicatorFieldName))
    if(fieldName.equals(INVALID_ID_INDICATOR_FIELD_NAME))
    {
      String invalidID = signal.getInvalidID();
      if(invalidID != null && invalidID.equals("Y"))
        return Boolean.TRUE;
      else
        return Boolean.FALSE;
    }//if(fieldName.equals(invalidIDIndicatorFieldName))
    if(fieldName.equals(SIGNAL_NAME_FIELD_NAME))
      return signal.getName();
    if(fieldName.equals(SIGNAL_ID_ALIAS_FIELD_NAME))
      return signal.getIDAlias();
    if(fieldName.equals(BULK_INDICATOR_FIELD_NAME))
    {
      String bulk = signal.getType().getBulk();
      if(bulk != null && bulk.equals("Y"))
        return Boolean.TRUE;
      else
        return Boolean.FALSE;
    }//if(fieldName.equals(bulkIndicatorFieldName))
//    if(fieldName.equals(externalSourceFileModificationDateFieldName))
//      return signal.getExternalSourceFileModifiedDate();
    if(fieldName.equals(MODIFIED_BY_UID_FIELD_NAME))
      return signal.getModifiedByUID();
    if(fieldName.equals(MODIFIED_DATE_FIELD_NAME))
      return signal.getModifiedDate();
//    if(fieldName.equals(externalSourceFileNameFieldName))
//      return signal.getExternalSourceFileName();
    if(fieldName.equals(ARCHIVE_INDICATOR_FIELD_NAME))
    {
      String archive = signal.getArchiveIndicator();
      if(archive != null && archive.equals("Y"))
        return Boolean.TRUE;
      else
        return Boolean.FALSE;
    }//if(fieldName.equals(archiveIndicatorFieldName))
    if(fieldName.equals(ARCHIVE_FREQUENCY_FIELD_NAME))
      return signal.getArchiveFrequency();
    if(fieldName.equals(ARCHIVE_TYPE_FIELD_NAME))
      return signal.getArchiveType();
    if(fieldName.equals(CABLE_COMPLETE_INDICATOR_FIELD_NAME))
    {
      String cableComplete = signal.getCableCompleteIndicator();
      if(cableComplete != null && cableComplete.equals("Y"))
        return Boolean.TRUE;
      else
        return Boolean.FALSE;
    }//if(fieldName.equals(cableCompleteIndicatorFieldName))
    if(fieldName.equals(ALARM_INDICATOR_FIELD_NAME))
    {
      String alarm = signal.getAlarmIndicator();
      if(alarm != null & alarm.equals("Y"))
        return Boolean.TRUE;
      else
        return Boolean.FALSE;
    }//if(fieldName.equals(alarmIndicatorFieldName))
    throw new java.lang.IllegalArgumentException("Could not get value for column '" + fieldName + "'.");
  }

  /**
   * Returns the <CODE>Class</CODE> for the given column. This method returns
   * <CODE>Boolean</CODE> for the ARCH_IND field, the default for all others.
   * This makes the <CODE>ARCH_IND</CODE> field a checkbox.
   * 
   * @param fieldName The name of the field in the database that corresponds to the column for which to return the <CODE>Class</CODE>.
   * @return The <CODE>Class</CODE> of the given column.
   */
  public Class getColumnClass(String fieldName)
  {
    if(fieldName.equals(SIGNAL_ID_FIELD_NAME))
      return Object.class;
    if(fieldName.equals(DEVICE_ID_FIELD_NAME))
      return Object.class;
    if(fieldName.equals(RECORD_TYPE_ID_FIELD_NAME))
      return Object.class;
    if(fieldName.equals(MACHINE_PROTECTION_INDICATOR_FIELD_NAME))
      return Boolean.class;
    if(fieldName.equals(EXTERNAL_SOURCE_FIELD_NAME))
      return Object.class;
    if(fieldName.equals(MULTIMODE_INDICATOR_FIELD_NAME))
      return Boolean.class;
    if(fieldName.equals(INVALID_ID_INDICATOR_FIELD_NAME))
      return Boolean.class;
    if(fieldName.equals(SIGNAL_NAME_FIELD_NAME))
      return Object.class;
    if(fieldName.equals(SIGNAL_ID_ALIAS_FIELD_NAME))
      return Object.class;
    if(fieldName.equals(BULK_INDICATOR_FIELD_NAME))
      return Boolean.class;
//    if(fieldName.equals(externalSourceFileModificationDateFieldName))
//      return Object.class;
    if(fieldName.equals(MODIFIED_BY_UID_FIELD_NAME))
      return Object.class;
    if(fieldName.equals(MODIFIED_DATE_FIELD_NAME))
      return Object.class;
//    if(fieldName.equals(EXTERNAL_SOURCE_FILE_NAME_FIELD_NAME))
//      return Object.class;
    if(fieldName.equals(ARCHIVE_INDICATOR_FIELD_NAME))
      return Boolean.class;
    if(fieldName.equals(ARCHIVE_FREQUENCY_FIELD_NAME))
      return Object.class;
    if(fieldName.equals(ARCHIVE_TYPE_FIELD_NAME))
      return Object.class;
    if(fieldName.equals(CABLE_COMPLETE_INDICATOR_FIELD_NAME))
      return Boolean.class;
    if(fieldName.equals(ALARM_INDICATOR_FIELD_NAME))
      return Boolean.class;
    throw new java.lang.IllegalArgumentException("Could not determine the type for column '" + fieldName + "'.");
  }

  /**
   * Saves the given rows as a CSV file.
   * 
   * @param selectedRows The indices of the rows to save.
   * @param saveFile The <CODE>File</CODE> to which to save the CSV.
   * @throws java.io.IOException Thrown on IO error.
   */
  public void saveAsCSV(int[] selectedRows, File saveFile) throws java.io.IOException
  {
    BufferedWriter oStream = new BufferedWriter(new FileWriter(saveFile));
    try
    {
      int columnCount = getColumnCount();
      for(int j=0;j<columnCount;j++)
      {
        if(j > 0)
          oStream.write(", ");
        oStream.write(getColumnName(j));
      }//for(int j=0;j<;j++)
      oStream.newLine();
      for(int i=0;i<selectedRows.length;i++)
      {
        for(int j=0;j<columnCount;j++)
        {
          if(j>0)
            oStream.write(", ");
          Object currentValue = getValueAt(i, j);
          if(currentValue != null)
          {
            String value = currentValue.toString();
            value.replaceAll("\"", "\"\"");
            boolean quote = value.indexOf("\"") > 0 || value.indexOf(",") > 0;
            if(quote)
              oStream.write("\"");
            oStream.write(value);
            if(quote)
              oStream.write("\"");
          }//if(currentValue != null)
        }//for(int j=0;j<columnCount;j++)
        oStream.newLine();
      }//for(int i=0;i<selectedRows.length;i++)
      oStream.flush();
    }//try
    finally
    {
      oStream.close();
    }//finally
  }
}