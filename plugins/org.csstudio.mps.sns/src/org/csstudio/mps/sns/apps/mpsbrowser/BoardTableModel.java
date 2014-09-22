package org.csstudio.mps.sns.apps.mpsbrowser;
import org.csstudio.mps.sns.tools.data.MPSBoard;
import org.csstudio.mps.sns.tools.data.MPSChain;
import org.csstudio.mps.sns.tools.data.MPSChassis;
import org.csstudio.mps.sns.tools.data.Signal;
import org.csstudio.mps.sns.tools.swing.autocompletecombobox.ComboBoxCellEditor;
import java.util.*;
import java.text.*;
import java.sql.*;

import javax.sql.*;

import javax.swing.table.*;
import org.csstudio.mps.sns.view.MPSBrowserView;


/**
 * Provides a model for the "device table" in the MPS Browser. This table holds
 * data for the MPS devices.
 * 
 * @author Chris Fowlkes
 */
public class BoardTableModel extends AbstractTableModel
{
  /**
   * Holds instances of <CODE>MPSBoard</CODE> that make up the data contained in
   * the model. Each instance of <CODE>MPSBoard</CODE> is represented by a line
   * in the table.
   */
  private ArrayList boards = new ArrayList();
  /**
   * Constant used to hold the model's name for the DVC_ID column.
   */
  public final static String DVC_ID = "DVC_ID";
  /**
   * Constant used to hold the model's name for the ACT_DVC_IND column.
   */
  public final static String ACT_DVC_IND = "ACT_DVC_IND";
  /**
   * Constant used to hold the model's name for the APPR_DTE column.
   */
  public final static String APPR_DTE = "APPR_DTE";
  /**
   * Constant used to hold the model's name for the MPS_CHAIN_ID column.
   */
  public final static String MPS_CHAIN_ID = "MPS_CHAIN_ID";
  /**
   * Constant used to hold the model's name for the SERIAL_NBR column.
   */
  public final static String SERIAL_NBR = "SERIAL_NBR";
  /**
   * Constant used to hold the model's name for the SFT_VER column.
   */
  public final static String SFT_VER = "SFT_VER";
  /**
   * Constant used to hold the model's name for the FPL_TOTAL column.
   */
  public final static String FPL_TOTAL = "FPL_TOTAL";
  /**
   * Constant used to hold the model's name for the FPAR_TOTAL column.
   */
  public final static String FPAR_TOTAL = "FPAR_TOTAL";
  /**
   * Constant used to hold the model's name for the CHASSIS_CONFIG_JMP column.
   */
  public final static String CHASSIS_CONFIG_JMP = "CHASSIS_CONFIG_JMP";
  /**
   * Constant used to hold the model's name for the HEARTBEAT_IND column.
   */
  public final static String HEARTBEAT_IND = "HEARTBEAT_IND";
  /**
   * Constant used to hold the model's name for the SW_JUMP column.
   */
  public final static String SW_JUMP = "SW_JUMP";
  /**
   * Constant used to hold the model's name for the MPS_IN column.
   */
  public final static String MPS_IN = "MPS_IN";
  /**
   * Constant used to hold the model's name for the FPAR_FPL_CONFIG column.
   */
  public final static String FPAR_FPL_CONFIG = "FPAR_FPL_CONFIG";
  /**
   * Constant used to hold the model's name for the DTE_CDE column.
   */
  public final static String DTE_CDE = "DTE_CDE";
  /**
   * Constant used to hold the model's name for the CHAIN_END_IND column.
   */
  public final static String CHAIN_END_IND = "CHAIN_END_IND";
  /**
   * Constant used to hold the model's name for the PMC_NBR column.
   */
  public final static String PMC_NBR = "PMC_NBR";
  /**
   * Constant used to hold the model's name for the IOC_DVC_ID column.
   */
  public final static String IOC_DVC_ID = "IOC_DVC_ID";
  /**
   * Used to convert any dates into the "MM-dd-yyyy" format for display.
   */
  private SimpleDateFormat displayFormat = new SimpleDateFormat("MM-dd-yyyy");
  /**
   * Holds the names of the columns that are visible in the table.
   */
  private ArrayList visibleColumnNames = new ArrayList(16);
  /**
   * Holds the names of the table's columns that have been hidden.
   */
  private ArrayList hiddenColumnNames = new ArrayList();
  /**
   * Holds a reference to all of the instances of <CODE>MPSBoard</CODE> that 
   * have been changed.
   */
  private ArrayList boardsChanged = new ArrayList();
  /**
   * A flag used to determine if the current user canedit the data or not. 
   */
  private boolean editable = true;
  /**
   * Holds the <CODE>JComboBox</CODE> used to select a serial number for each 
   * chassis. The model needs this so that it can keep the list updated so that 
   * it only contains items not already assigned to a chassis.
   */
  private ComboBoxCellEditor serialNumberEditor;
  /**
   * Holds the <CODE>DataSource</CODE> for the model. This is used to make 
   * database connections.
   */
  private DataSource connectionPool;

  /**
   * Creates a new <CODE>BoardTableModel</CODE>.
   */
  public BoardTableModel()
  {
    visibleColumnNames.add(DVC_ID);
    visibleColumnNames.add(ACT_DVC_IND);
    visibleColumnNames.add(APPR_DTE);
    visibleColumnNames.add(IOC_DVC_ID);
    visibleColumnNames.add(MPS_CHAIN_ID);
    visibleColumnNames.add(SERIAL_NBR);
    visibleColumnNames.add(SFT_VER);
    visibleColumnNames.add(FPL_TOTAL);
    visibleColumnNames.add(FPAR_TOTAL);
    visibleColumnNames.add(CHASSIS_CONFIG_JMP);
    visibleColumnNames.add(HEARTBEAT_IND);
    visibleColumnNames.add(SW_JUMP);
    visibleColumnNames.add(MPS_IN);
    visibleColumnNames.add(FPAR_FPL_CONFIG);
    visibleColumnNames.add(DTE_CDE);
    visibleColumnNames.add(CHAIN_END_IND);
    visibleColumnNames.add(PMC_NBR);
  }

  /**
   * Returns the number of rows in the model.
   * 
   * @return The number of rows in the table.
   */
  public int getRowCount()
  {
    if(boards == null)
      return 0;
    else
      return boards.size();
  }

  /**
   * Returns the number of columns in the model that are visible. Columns can be 
   * hidden with the <CODE>showColumns</CODE> method.
   * 
   * @return The number of visible columns in the model.
   */
  public int getColumnCount()
  {
    return visibleColumnNames.size();
  }

  /**
   * Returns the value for a given cell.
   * 
   * @param rowIndex The row number of the cell to return the value of.
   * @param columnIndex The column number of the cell to return the value of.
   * @return The value of the given cell.
   */
  public Object getValueAt(int rowIndex, int columnIndex)
  {
    MPSBoard currentBoard = getBoardAt(rowIndex);
    String columnName = getColumnName(columnIndex);
    if(columnName.equals(DVC_ID))
      return currentBoard.getID();
    if(columnName.equals(ACT_DVC_IND))
    {
      String currentIndicator = currentBoard.getActiveDeviceIndicator();
      if(currentIndicator == null || ! currentIndicator.equals("Y"))
        return Boolean.FALSE;
      else
        return Boolean.TRUE;
    }//if(columnName.equals(ACT_DVC_IND))
    if(columnName.equals(APPR_DTE))
    {
      java.sql.Date approveDate = currentBoard.getApproveDate();
      if(approveDate == null)
        return null;
      else
        return displayFormat.format(approveDate);
    }//if(columnName.equals(APPR_DTE))
    if(columnName.equals(MPS_CHAIN_ID))
      return currentBoard.getChain();
    if(columnName.equals(SERIAL_NBR))
      return currentBoard.getSerialNumber();
    if(columnName.equals(SFT_VER))
      return currentBoard.getSoftwareVersion();
    if(columnName.equals(FPL_TOTAL))
      return currentBoard.getFastProtectLatchTotal();
    if(columnName.equals(FPAR_TOTAL))
      return currentBoard.getFPARTotal();
    if(columnName.equals(CHASSIS_CONFIG_JMP))
      return new Integer(currentBoard.getChassisConfigurationJumpers());
    if(columnName.equals(HEARTBEAT_IND))
      return currentBoard.getHeartbeatIndicator();
    if(columnName.equals(SW_JUMP))
      return currentBoard.getSWJumper();
    if(columnName.equals(MPS_IN))
      return new Integer(currentBoard.getMPSIn());
    if(columnName.equals(FPAR_FPL_CONFIG))
      return currentBoard.getFPARFastProtectLatchConfiguration();
    if(columnName.equals(DTE_CDE))
    {
      java.sql.Date currentDate = currentBoard.getDateCode();
      if(currentDate == null)
        return null;
      else
        return displayFormat.format(currentDate);
    }//if(columnName.equals(DTE_CDE))
    if(columnName.equals(CHAIN_END_IND))
      return currentBoard.getChainEndIndicator();
    if(columnName.equals(PMC_NBR))
      return new Integer(currentBoard.getPMCNumber());
    //IOC_DVC_ID is all that's left...
    return currentBoard.getChassis();
  }

  /**
   * Sets the instances of <CODE>MPSBoard</CODE> represented in the table. The 
   * data is passed in as an <CODE>ArrayList</CODE> containing instances of 
   * <CODE>MPSBoard</CODE>, each of which will make up a row of data in the 
   * table.
   * 
   * @param boards The instances of <CODE>MPSBoard</CODE> that make up the data for the model.
   */
  public void setBoards(MPSBoard[] boards)
  {
    boardsChanged.clear();
    this.boards.clear();
    this.boards.addAll(Arrays.asList(boards));
    fireTableDataChanged();
  }

  /**
   * Used to add a single board to the model.
   * 
   * @param board The <CODE>MPSBoard</CODE> being added to the model.
   */
  public void addBoard(MPSBoard board)
  {
    int position = getRowCount();
    boards.add(board);
    boardsChanged.add(board);
    fireTableRowsInserted(position, position);
  }

  /**
   * Used to remove a single board from the model. 
   * 
   * @param board The <CODE>MPSBoard</CODE> being removed from the model.
   */
  public void removeBoard(MPSBoard board)
  {
    int position = boards.indexOf(board);
    boards.remove(board);
    fireTableRowsDeleted(position, position);
  }

  /**
   * Gets the <CODE>MPSBoard</CODE> represented by the given line.
   * 
   * @param index The index of the line in the model to return the <CODE>MPSBoard</CODE> for.
   * @return The <CODE>MPSBoard</CODE> represented by the data at the given row index.
   */
  public MPSBoard getBoardAt(int index)
  {
    return (MPSBoard)boards.get(index);
  }

  /**
   * Gets the model's name of the column at the given index. This is the name
   * that appears in the header bar above the column.
   * 
   * @param column The index of the column to return the name for.
   * @return The name of the columjn at the given index.
   */
  public String getColumnName(int column)
  {
    return visibleColumnNames.get(column).toString();
  }

  /**
   * Returns the <CODE>Class</CODE> of the column at the given index. This is 
   * primarily used to assign a custom editor such as a pick list to a column.
   * 
   * @param columnIndex The index of the column to return the <CODE>Class</CODE> of.
   * @return The <CODE>Class</CODE> of the data represented by the gicen column index.
   */
  public Class getColumnClass(int columnIndex)
  {
    String columnName = getColumnName(columnIndex);
    if(columnName.equals(MPS_CHAIN_ID))
      return org.csstudio.mps.sns.tools.data.MPSChain.class;
    if(columnName.equals(SERIAL_NBR))
      return Number.class;
    if(columnName.equals(IOC_DVC_ID))
      return org.csstudio.mps.sns.tools.data.MPSChassis.class;
    if(columnName.equals(FPAR_FPL_CONFIG))
      return String.class;
    if(columnName.equals(ACT_DVC_IND))
      return Boolean.class;
    return super.getColumnClass(columnIndex);
  }

  /**
   * Determines if the given cell is editable. If the model's editable property 
   * is <CODE>false</CODE>, this method will always return <CODE>false</CODE>. 
   * Otherwise the method will return <CODE>true</CODE> for all columns except
   * for the DVC_ID and APPR_DTE columns. DVC_ID and APPR_DTE are only editable 
   * if the record is not in the database yet.
   * 
   * @param rowIndex The row index of the cell to return the editability of.
   * @param columnIndex The column index of the cell to return the editability of.
   * @return <CODE>true</CODE> if the given cell is editable, <CODE>false</CODE> if not.
   */
  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    if(! isEditable())
      return false;
    String columnName = getColumnName(columnIndex);
    if(columnName.equals(DVC_ID) || columnName.equals(APPR_DTE))
      return ! getBoardAt(rowIndex).isInDatabase();
    if(columnName.equals(ACT_DVC_IND))
      return false;
    return true;
  }

  /**
   * Sets the value for the given cell. The row index maps directly to an 
   * instance of <CODE>MPSBoard</CODE> in the model. The column index is 
   * converted into a column name via the <CODE>getColumnName</CODE> method to 
   * map the cell to a property in the <CODE>MPSBoard</CODE> because columns can be
   * moved, hidden and shown by the <CODE>showColumns</CODE> method.
   * 
   * @param aValue The new value of the cell.
   * @param rowIndex The row number of the cell to set the value of.
   * @param columnIndex The column index of the cell to set the value of.
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    Object originalValue = getValueAt(rowIndex, columnIndex);
    MPSBoard currentBoard = getBoardAt(rowIndex);
    String columnName = getColumnName(columnIndex);
    if(columnName.equals(DVC_ID))
      if(aValue == null)
        currentBoard.setID(null);
      else
        currentBoard.setID(aValue.toString());
    else if(columnName.equals(APPR_DTE))
      if(aValue == null)
        currentBoard.setApproveDate(null);
      else
        try
        {
          java.util.Date newDate = displayFormat.parse(aValue.toString());
          currentBoard.setApproveDate(new java.sql.Date(newDate.getTime()));
        }//try
        catch(java.text.ParseException ex)
        {
          System.out.println(ex.getMessage());
        }//catch(java.text.ParseException ex)
    else if(columnName.equals(MPS_CHAIN_ID))
    {
      if(aValue != null)
        currentBoard.setChain((MPSChain)aValue);
    }
    else if(columnName.equals(SERIAL_NBR))
    {
      //Need to add the old value to the pick list.
      String oldValue = currentBoard.getSerialNumber();
      ArrayList defaultItems = serialNumberEditor.getDefaultItems();
      if(! (oldValue == null || defaultItems.contains(oldValue)))
        defaultItems.add(oldValue);
      if(aValue == null)
        currentBoard.setSerialNumber(null);
      else
      {
        String newValue = aValue.toString();
        currentBoard.setSerialNumber(newValue);
        //Need to remove the new item from the pick list.
        defaultItems.remove(newValue);
      }//else
    }
    else if(columnName.equals(SFT_VER))
      if(aValue == null)
        currentBoard.setSoftwareVersion(null);
      else
        currentBoard.setSoftwareVersion(aValue.toString());
    else if(columnName.equals(FPL_TOTAL))
      if(aValue == null)
        currentBoard.setFastProtectLatchTotal(null);
      else
        currentBoard.setFastProtectLatchTotal(aValue.toString());
    else if(columnName.equals(FPAR_TOTAL))
      if(aValue == null)
        currentBoard.setFPARTotal(null);
      else
        currentBoard.setFPARTotal(aValue.toString());
    else if(columnName.equals(CHASSIS_CONFIG_JMP))
    {
      if(aValue != null)
        try
        {
          currentBoard.setChassisConfigurationJumpers(Integer.parseInt(aValue.toString().trim()));
        }//try
        catch(java.lang.NumberFormatException ex)
        {
          System.out.println(ex.getMessage());
        }//catch(java.lang.NumberFormatException ex)
    }
    else if(columnName.equals(HEARTBEAT_IND))
    {
      if(aValue == null)
        currentBoard.setHeartbeatIndicator(null);
      else
      {
        String stringValue = aValue.toString().toUpperCase().trim();
        int stringLength = stringValue.length();
        //Looking fo a Y or N. Take last character so existing value does not
        //have to be deleted by the user.
        if(stringLength > 1)
          stringValue = stringValue.substring(stringLength - 1);
        currentBoard.setHeartbeatIndicator(stringValue);        
      }//else
    }
    else if(columnName.equals(SW_JUMP))
      if(aValue == null)
        currentBoard.setSWJumper(null);
      else
      {
        String stringValue = aValue.toString().toUpperCase().trim();
        int stringLength = stringValue.length();
        //Looking fo a Y or N. Take last character so existing value does not
        //have to be deleted by the user.
        if(stringLength > 1)
          stringValue = stringValue.substring(stringLength - 1);
        currentBoard.setSWJumper(stringValue);        
      }//else
    else if(columnName.equals(MPS_IN))
    {
      if(aValue != null)
        try
        {
          currentBoard.setMPSIn(Integer.parseInt(aValue.toString().trim()));
        }//try
        catch(java.lang.NumberFormatException ex)
        {
          System.out.println(ex.getMessage());
        }//catch(java.lang.NumberFormatException ex)
    }
    else if(columnName.equals(FPAR_FPL_CONFIG))
      if(aValue == null)
        currentBoard.setFPARFastProtectLatchConfig(null);
      else
        currentBoard.setFPARFastProtectLatchConfig(aValue.toString());
    else if(columnName.equals(DTE_CDE))
      if(aValue == null)
        currentBoard.setDateCode(null);
      else
      {
        String dateString = aValue.toString().trim();
        if(dateString.equals(""))
          currentBoard.setDateCode(null);
        else
          try
          {
            java.util.Date newDate = displayFormat.parse(dateString);
            currentBoard.setDateCode(new java.sql.Date(newDate.getTime()));
          }//try
          catch(java.text.ParseException ex)
          {
            System.out.println(ex.getMessage());
          }//catch(java.text.ParseException ex)
      }//else
    else if(columnName.equals(CHAIN_END_IND))
    {
      if(aValue == null)
        currentBoard.setChainEndIndicator(null);
      else
      {
        String stringValue = aValue.toString().toUpperCase().trim();
        int stringLength = stringValue.length();
        //Looking fo a Y or N. Take last character so existing value does not
        //have to be deleted by the user.
        if(stringLength > 1)
          stringValue = stringValue.substring(stringLength - 1);
        currentBoard.setChainEndIndicator(stringValue);        
      }//else
    }
    else if(columnName.equals(PMC_NBR))
    {
      if(aValue != null)
        try
        {
          currentBoard.setPMCNumber(Integer.parseInt(aValue.toString().trim()));
        }//try
        catch(java.lang.NumberFormatException ex)
        {
          System.out.println(ex.getMessage());
        }//catch(java.lang.NumberFormatException ex)
    }
    else if(columnName.equals(IOC_DVC_ID))
      if(aValue != null)
        currentBoard.setChassis((MPSChassis)aValue);
    if(! MPSBrowserView.compare(originalValue, getValueAt(rowIndex, columnIndex)))
    {
      if(! boardsChanged.contains(currentBoard))
        boardsChanged.add(currentBoard);
      fireTableCellUpdated(rowIndex, columnIndex);
    }//if(! (isPostNeeded() || Jeri.compare(originalValue, getValueAt(rowIndex, columnIndex))))
  }

  /**
   * Returns an <CODE>ArrayList</CODE> containing instances of 
   * <CODE>String</CODE> that represent the names of the columns which are not 
   * currently visible in the table.
   * 
   * @return The names of the hidden columns in the model.
   */
  public ArrayList getHiddenColumnNames()
  {
    return hiddenColumnNames;
  }

  /**
   * Returns an <CODE>ArrayList</CODE> containing instances of 
   * <CODE>String</CODE> that represent the names of the columns which are 
   * currently visible in the table.
   * 
   * @return The names of the visible columns in the model.
   */
  public ArrayList getVisibleColumnNames()
  {
    return visibleColumnNames;
  }

  /**
   * Shows the given columns in the table. This method takes an 
   * <CODE>ArrayList</CODE> containing the names of the columns to make visible.
   * This method will make only the specified columns visible, hide the other
   * columns, and make sure that the order of the columns as passed in is 
   * preserved. This method is used to hide/show columns as well as reorder 
   * them.
   * 
   * @param columnNames The names of the columns to show in the model.
   */
  public void showColumns(ArrayList columnNames)
  {
    hiddenColumnNames.addAll(visibleColumnNames);
    visibleColumnNames.clear();
    int columnCount = columnNames.size();
    for(int i=0;i<columnCount;i++)
    {
      Object currentColumnName = columnNames.get(i);
      if(hiddenColumnNames.remove(currentColumnName))
        visibleColumnNames.add(currentColumnName);
    }//for(int i=0;i<columnCount;i++)
    fireTableStructureChanged();
  }

  /**
   * Returns <CODE>true</CODE> if the data has been changed.
   * 
   * @return <CODE>true</CODE> if the data has been changed, <CODE>false</CODE> if not.
   */
  public boolean isPostNeeded()
  {
    return boardsChanged.size() > 0;
  }

  /**
   * Return the instances of <CODE>MPSBoard</CODE> that have been changed. This
   * method returns the instances of <CODE>MPSBoard</CODE> that have been 
   * changed in an <CODE>ArrayList</CODE>. Once the changes have been saved to
   * the database, <CODE>changesPosted</CODE> must be called to notify the model
   * that the changes are no longer pending.
   * 
   * @return An array containing the instances of <CODE>MPSBoard</CODE> that have been changed.
   */
  public MPSBoard[] getBoardsChanged()
  {
    return (MPSBoard[])boardsChanged.toArray(new MPSBoard[(boardsChanged.size())]);
  }

  /**
   * This method is used to notify the model that the changes have been posted.
   * Once the pending changes have been saved, the saving class must invoke this 
   * method to notify the model of the save.
   */
  public void changesPosted()
  {
    boardsChanged.clear();
  }

  /**
   * Sets the value of the editable property. If the value is set to 
   * <CODE>false</CODE>, no cells in the table will be editable.
   * 
   * @param editable Pass as <CODE>false</CODE> to make the table read only.
   */
  public void setEditable(boolean editable)
  {
    this.editable = editable;
  }

  /**
   * Returns the value of the editabl property.
   * 
   * @return <CODE>true</CODE> if the table is edtable, <CODE>false</CODE> if not.
   */
  public boolean isEditable()
  {
    return editable;
  }

  /**
   * Sets the <CODE>JComboBox</CODE> used to select a serial number for each 
   * chassis. The model needs this so that it can keep the list updated so that 
   * it only contains items not already assigned to a chassis.
   * 
   * @param serialNumberEditor The <CODE>JComboBox</CODE> used to edit the serial number column.
   */
  public void setSerialNumberEditor(ComboBoxCellEditor serialNumberEditor)
  {
    this.serialNumberEditor = serialNumberEditor;
  }
  
  /**
   * Gets the combo box used to edit the serial number cell.
   * 
   * @return The combo box editor for the serial number cell.
   */
  public ComboBoxCellEditor getSerialNumberEditor()
  {
    return serialNumberEditor;
  }
  
  /**
   * Sets the <CODE>DataSource</CODE> used by the model to connect to the 
   * database.
   *
   * @param connectionPool The <CODE>DataSource</CODE> to use to connect to the database.
   */
  public void setDataSource(DataSource connectionPool)
  {
    this.connectionPool = connectionPool;
  }
}