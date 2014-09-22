package org.csstudio.mps.sns.apps.mpsbrowser;
import org.csstudio.mps.sns.MainFrame;

import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.MPSBoard;
import org.csstudio.mps.sns.tools.data.MPSChannel;
import org.csstudio.mps.sns.tools.data.MPSChannelAudit;
import org.csstudio.mps.sns.tools.swing.autocompletecombobox.ComboBoxCellEditor;
import org.csstudio.mps.sns.tools.swing.treetable.AbstractTreeTableModel;
import org.csstudio.mps.sns.tools.swing.treetable.TreeTableModel;
import java.awt.GridLayout;
import java.math.*;

import java.sql.*;

import java.util.*;

import javax.sql.*;

import javax.swing.*;

import org.csstudio.mps.sns.view.MPSBrowserView;

/**
 * Provides a model for displaying the channel data in the MPS Browser 
 * interface.
 * 
 * @author Chris Fowlkes
 */
public class MPSDetailsTableModel extends AbstractTreeTableModel 
{
  /**
   * Holds the name of the column that corresponds to the DVC_ID field in the 
   * database.
   */
  public final static String DVC_ID = "DVC_ID";
  /**
   * Holds the name of the column that corresponds to the APPR_DTE field in the 
   * database.
   */
  public final static String APPR_DTE = "APPR_DTE";
  /**
   * Holds the name of the column that corresponds to the CHANNEL_NBR field in 
   * the database.
   */
  public final static String CHANNEL_NBR = "CHANNEL_NBR";
  /**
   * Holds the name of the column that corresponds to the MPS_DVC_ID field in 
   * the database.
   */
  public final static String MPS_DVC_ID = "MPS_DVC_ID";
  /**
   * Holds the name of the column that corresponds to the LOCKED_IND field in 
   * the database.
   */
  public final static String LOCKED_IND = "LOCKED_IND";
  /**
   * Holds the name of the column that corresponds to the CHAIN_IN_USE_IND field 
   * in the database.
   */
  public final static String CHAN_IN_USE_IND = "CHAN_IN_USE_IND";
  /**
   * Holds the name of the column that corresponds to the SW_JUMP field in the 
   * database.
   */
  public final static String SW_JUMP = "SW_JUMP";
  /**
   * Holds the name of the column that corresponds to the LIMIT field in the 
   * database.
   */
  public final static String LIMIT = "LIMIT";
  /**
   * Holds the name of the column that corresponds to the RATE field in the 
   * database.
   */
  public final static String RATE = "RATE";
  /**
   * Holds the name of the column that corresponds to the PASS_IND field in the 
   * database.
   */
  public final static String PASS_IND = "PASS_IND";
  /**
   * Holds the name of the column that corresponds to the AUDIT_DTE field in the 
   * database.
   */
  public final static String AUDIT_DTE = "AUDIT_DTE";
  private ArrayList channelsChanged = new ArrayList();
  private boolean editable = true;
  private ArrayList visibleColumnNames = new ArrayList(9);
  private ArrayList hiddenColumnNames = new ArrayList();
  /**
   * Holds the <CODE>DataSource</CODE> for the model. This is used to make 
   * database connections.
   */
  private DataSource connectionPool;
  /**
   * Holds the <CODE>JComboBox</CODE> used to select a MPS device ID for each 
   * channel. The model needs this so that it can keep the list updated so that 
   * it only contains items not already assigned to a channel.
   */
  private ComboBoxCellEditor mpsDeviceEditor;
  /**
   * Holds a reference to the main window of the application.
   */
  private MainFrame mainWindow;
  /**
   * Holds a reference to the <CODE>MPSBrowser</CODE> containing the functions 
   * to access the database.
   */
  private MPSBrowser mpsBrowser;
  
  /**
   * Creates a new <CODE>MPSDetailsTableModel</CODE>.
   */
  public MPSDetailsTableModel()
  {
    super(new ArrayList());
    visibleColumnNames.add(DVC_ID);
    visibleColumnNames.add(APPR_DTE);
    visibleColumnNames.add(CHANNEL_NBR);
    visibleColumnNames.add(MPS_DVC_ID);
    visibleColumnNames.add(LOCKED_IND);
    visibleColumnNames.add(CHAN_IN_USE_IND);
    visibleColumnNames.add(PASS_IND);
    visibleColumnNames.add(AUDIT_DTE);
    visibleColumnNames.add(SW_JUMP);
    visibleColumnNames.add(LIMIT);
    visibleColumnNames.add(RATE);

  }

  /**
   * The number of columns visible in the model.
   * 
   * @return The number of columns in the model.
   */
  public int getColumnCount()
  {
    return visibleColumnNames.size();
  }

  /**
   * Gets the name of the column at the given index.
   * 
   * @param column The index of the column of which to return the name.
   * @return The name of the column at the given index.
   */
  public String getColumnName(int column)
  {
    return visibleColumnNames.get(column).toString();
  }

  /**
   * Gets the value of the given cell.
   * 
   * @param node The node to which the cell belongs.
   * @param column The column index of the cell.
   * @return The value of the given cell.
   */
  public Object getValueAt(Object node, int column)
  {
    String columnName = getColumnName(column);
    if(node == null)
      return "";
    if(node instanceof MPSChannel)
    {
      if(columnName.equals(DVC_ID) || columnName.equals(APPR_DTE))
        return "";
      if(columnName.equals(CHANNEL_NBR))
        return new Integer(((MPSChannel)node).getNumber());
      if(columnName.equals(MPS_DVC_ID))
      {
        Device currentDevice = ((MPSChannel)node).getDevice();
        if(currentDevice == null)
          return "";
        else
          return currentDevice.getID();
      }
      if(columnName.equals(LOCKED_IND))
      {
        String locked = ((MPSChannel)node).getLockedIndicator();
        if(locked != null && locked.equals("Y"))
          return Boolean.TRUE;
        else
          return Boolean.FALSE;
      }
      if(columnName.equals(CHAN_IN_USE_IND))
      {
        String inUse = ((MPSChannel)node).getInUseIndicator();
        if(inUse != null && inUse.equals("Y"))
          return Boolean.TRUE;
        else
          return Boolean.FALSE;
      }
      if(columnName.equals(SW_JUMP))
        return ((MPSChannel)node).getSWJumper();
      if(columnName.equals(LIMIT))
        return new Integer(((MPSChannel)node).getLimit());
      if(columnName.equals(RATE))
        return new Integer(((MPSChannel)node).getRate());
      if(columnName.equals(AUDIT_DTE))
      {
        MPSChannelAudit audit = ((MPSChannel)node).getLatestAudit();
        if(audit == null)
          return null;
        else
          return audit.getDate();
      }
      if(columnName.equals(PASS_IND))
      {
        MPSChannelAudit audit = ((MPSChannel)node).getLatestAudit();
        if(audit == null)
          return null;
        else
          return audit.getPassIndicator();
      }
      //Must be a binary value...
      BigInteger value = ((MPSChannel)node).getBinaryValue(columnName);
      if(value == null)
        if(columnName.equals("BEAM_OFF"))
          return new BigInteger("0");
        else
          return new BigInteger("1");
      return value;
    }
    if(node instanceof MPSBoard)
    {
      if(columnName.equals(DVC_ID))
        return ((MPSBoard)node).getID();
      if(columnName.equals(APPR_DTE))
        return ((MPSBoard)node).getApproveDate();
      if(columnName.equals(LOCKED_IND))
        return Boolean.FALSE;//Equivalent to null for binary column.
      if(columnName.equals(CHAN_IN_USE_IND))
        return Boolean.FALSE;//Equivalent to null for binary column.
      if(columnName.equals(CHANNEL_NBR) || columnName.equals(MPS_DVC_ID) || columnName.equals(SW_JUMP) || columnName.equals(LIMIT) || columnName.equals(RATE) || columnName.equals(AUDIT_DTE) || columnName.equals(PASS_IND))
        return "";
      //Must be a binary column. Return mask value.
      int[] mask = ((MPSBoard)node).getModeMask(getColumnName(column));
      StringBuffer maskValue = new StringBuffer(7);
      maskValue.append(mask[0]);
      while(maskValue.length() < 6)
        maskValue.append(" ");
      maskValue.append(mask[1]);
      return maskValue;
    }
    return "";
  }

  /**
   * Returns child at the given index of the given node.
   * 
   * @param parent The parent node of the <CODE>Object</CODE> to return.
   * @param index The index of the child to return.
   * @return The child at the given index of the given node.
   */
  public Object getChild(Object parent, int index)
  {
    if(parent instanceof MPSBoard)
      return ((MPSBoard)parent).channelAt(index);
    else
      return ((ArrayList)parent).get(index);
  }

  /**
   * Returns the number of children in the given parent node.
   
   * @param parent The node for which to return the number of children.
   * @return The number of children in the given node.
   */
  public int getChildCount(Object parent)
  {
    if(parent instanceof MPSChannel)
      return 0;
    else
      if(parent instanceof MPSBoard)
        return 16;
      else
        return ((ArrayList)parent).size();
  }

  /**
   * Sets the names of the binary columns displayed in the table.
   * 
   * @param binaryColumnNames The names of the binary columns displayed in the table.
   */
  public void setBinaryColumnNames(String[] binaryColumnNames)
  {
    for(int i=0;i<binaryColumnNames.length;i++)
      if(! (visibleColumnNames.contains(binaryColumnNames[i]) || hiddenColumnNames.contains(binaryColumnNames[i])))
        visibleColumnNames.add(binaryColumnNames[i]);
  }
  
  /**
   * Gets the data type displayed by the given column.
   * 
   * @param column The index of the column of which to return the type.
   * @return The type of data displayed in the given column.
   */
  public Class getColumnClass(int column)
  {
    String columnName = getColumnName(column);
    if(columnName.equals(DVC_ID))
      return TreeTableModel.class;
    if(columnName.equals(MPS_DVC_ID))
      return org.csstudio.mps.sns.tools.data.Device.class;
    if(columnName.equals(LOCKED_IND))
      return Boolean.class;
    if(columnName.equals(CHAN_IN_USE_IND))
      return Boolean.class;
    return super.getColumnClass(column);
  }

  /**
   * Used to set the boards containing the data for the model.
   * 
   * @param boards The instances of <CODE>MPSBoard</CODE> that comprise the data in the model.
   */
  public void setBoards(MPSBoard[] boards)
  {
    channelsChanged.clear();
    ArrayList root = (ArrayList)getRoot();
    root.clear();
    channelsChanged.clear();
    root.addAll(Arrays.asList(boards));
    int[] indices = new int[boards.length];
    for(int i=0;i<indices.length;i++) 
      indices[i] = i;
    fireTreeStructureChanged(this, new Object[]{root}, indices, boards);
  }

  /**
   * Used to add a <CODE>MPSBoard</CODE> to the model. The channels in the newly 
   * created board are not considered changed and will not be returned by the 
   * <CODE>getChannelsChanged</CODE> method until a value in the table for the 
   * given channel is changed.
   * 
   * @param board The <CODE>MPSBoard</CODE> to add to the data in the model.
   */
  public void addBoard(MPSBoard board)
  {
    ArrayList records = (ArrayList)getRoot();
    records.add(board);
    Object[] path = new Object[]{records};
    int[] indices = new int[]{records.size() - 1};
    Object[] nodes = new Object[]{board};
    fireTreeStructureChanged(this, path, indices, nodes);
  }

  /**
   * Used to remove a <CODE>MPSBoard</CODE> to the model.
   * 
   * @param board The <CODE>MPSBoard</CODE> to remove from the data in the model.
   */
  public void removeBoard(MPSBoard board)
  {
    ArrayList records = (ArrayList)getRoot();
    Object[] path = new Object[]{records};
    int[] indices = new int[]{records.size() - 1};
    Object[] nodes = new Object[]{board};
    records.remove(board);
    fireTreeStructureChanged(this, path, indices, nodes);
  }

  /**
   * Clears the model by removing all data.
   */
  public void clear()
  {
    ArrayList records = (ArrayList)getRoot();
    Object[] path = new Object[]{records};
    Object[] nodes = records.toArray();
    int[] indices = new int[nodes.length];
    for(int i=0;i<indices.length;i++)
      indices[i] = i;
    records.clear();
    fireTreeStructureChanged(this, path, indices, nodes);
  }

  /**
   * Determines if the given cell is editable.
   * 
   * @param node The node in the <CODE>TreeTable</CODE> being edited.
   * @param column The column to be edited.
   * @return <CODE>true</CODE> if the cell is editable, <CODE>false</CODE> if it is not editable.
   */
  public boolean isCellEditable(Object node, int column)
  {
    String columnName = getColumnName(column);
    if(node instanceof MPSChannel)
    {
      if(! isEditable())
        return false;
      MPSChannel channel = (MPSChannel)node;
      if(MPSBrowserView.compare(channel.getLockedIndicator(), "Y"))
        return columnName.equals("LOCKED_IND");
      else
        if(columnName.equals(DVC_ID) || columnName.equals(APPR_DTE) || columnName.equals(CHANNEL_NBR) || columnName.equals(AUDIT_DTE) || columnName.equals(PASS_IND))
          return false;
        else
          return true;
    }
    else
      return columnName.equals(DVC_ID);//Must be editable for tree to get mouse clicks.
  }

  /**
   * Sets the value of the given cell.
   * 
   * @param aValue The new value for the given cell.
   * @param node Represents the row of which to set the value.
   * @param column The column of the cell of which to set the value.
   */
  public void setValueAt(Object aValue, Object node, int column)
  {
    if(MPSBrowserView.compare(String.valueOf(aValue), String.valueOf(getValueAt(node, column))))
      return;//No change made.
    if(node instanceof MPSChannel)
    {
      String columnName = getColumnName(column);
      MPSChannel selectedChannel = (MPSChannel)node;
      if(columnName.equals(MPS_DVC_ID))
      {
        //Need to get the old value and stick it in the editor if it's not null.
        Device channelDevice = selectedChannel.getDevice();
        String oldDeviceID;
        if(channelDevice == null)
          oldDeviceID = null;
        else
          oldDeviceID = channelDevice.getID();
        ArrayList defaultItems = mpsDeviceEditor.getDefaultItems();
        if(oldDeviceID != null)
          defaultItems.add(oldDeviceID);
        //Set the new value in the model.
        if(aValue == null)
          selectedChannel.setDevice(null);
        else
        {
          String deviceID = aValue.toString().trim();
          if(deviceID.equals(""))
            selectedChannel.setDevice(null);
          else
          {
            selectedChannel.setDevice(new Device(deviceID));
            //New value is not null. Remove it from the editor list.
            defaultItems.remove(deviceID);
            //Need to load the default mask values for the given signal ID.
          }
        }
        try
        {
          if(! loadMaskDefaults(selectedChannel, oldDeviceID))
            channelChanged(selectedChannel, false);
        }
        catch(java.sql.SQLException ex)
        {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(getMainWindow(), ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
      }
      else if(columnName.equals(LOCKED_IND))
      {
        if(aValue != null && ((Boolean)aValue).booleanValue())
          selectedChannel.setLockedIndicator("Y");
        else
          selectedChannel.setLockedIndicator("N");
        channelChanged(selectedChannel, false);
      }
      else if(columnName.equals(CHAN_IN_USE_IND))
      {
        if(aValue != null && ((Boolean)aValue).booleanValue())
        {
          selectedChannel.setInUseIndicator("Y");
          Device channelDevice = selectedChannel.getDevice();
          try
          {
            int option = JOptionPane.showConfirmDialog(getMainWindow(), "Do you want to set the mask values to defaults?", "Set To Defaults", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(option == JOptionPane.YES_OPTION)
              loadMaskDefaults(selectedChannel, null);
            else
              channelChanged(selectedChannel, false);
          }
          catch(java.sql.SQLException ex)
          {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(getMainWindow(), ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
          }
        }
        else
        {
          selectedChannel.setInUseIndicator("N");
          int option = JOptionPane.showConfirmDialog(getMainWindow(), "Do you want to set the mask values to one?", "Set To One", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
          if(option == JOptionPane.YES_OPTION)
            setChannelValuesToOne(selectedChannel);
          else
            channelChanged(selectedChannel, false);
        }
      }
      else if(columnName.equals(SW_JUMP))
      {
        if(aValue == null)
          selectedChannel.setSWJumper("N");
        else
        {
          String swJumper = aValue.toString().trim();
          if(swJumper.equals(""))
            selectedChannel.setSWJumper("N");
          else
          {
            swJumper = swJumper.toUpperCase();
            if(swJumper.equals("Y") || swJumper.equals("N"))
              selectedChannel.setSWJumper(swJumper);
          }
        }
        channelChanged(selectedChannel, false);
      }
      else if(columnName.equals(LIMIT))
      {
        if(aValue != null)
          try
          {
            selectedChannel.setLimit(Integer.parseInt(aValue.toString().trim()));
            channelChanged(selectedChannel, false);
          }
          catch(java.lang.NumberFormatException ex)
          {
            System.out.println(ex.getMessage());
          }
      }
      else if(columnName.equals(RATE))
      {
        if(aValue != null)
          try
          {
            selectedChannel.setRate(Integer.parseInt(aValue.toString().trim()));
            channelChanged(selectedChannel, false);
          }
          catch(java.lang.NumberFormatException ex)
          {
            System.out.println(ex.getMessage());
          }
      }
      else
      {
        //Must be a binary value
        if(aValue == null)
          selectedChannel.setBinaryValue(getColumnName(column), null);
        else
        {
          String binaryValue = aValue.toString().trim();
          if(binaryValue.equals(""))
            selectedChannel.setBinaryValue(getColumnName(column), null);
          else
          {
            BigInteger newValue = new BigInteger(binaryValue);
            int intValue = newValue.intValue();
            if(intValue == 0 || intValue == 1)//Only accept 1 or 0
              selectedChannel.setBinaryValue(getColumnName(column), newValue);
          }
        }
        channelChanged(selectedChannel, true);
      }
    }
  }

  /**
   * This method fires the proper events and adds the channel to the 
   * <CODE>channelsChanged Arraylist</CODE> so that the changes will be saved.
   * 
   * @param selectedChannel The <CODE>MPSChannel</CODE> that was changed.
   * @param boardChanged Pass as <CODE>true</CODE> if the change affects the mask values in the board row also.
   */
  private void channelChanged(MPSChannel selectedChannel, boolean boardChanged)
  {
    if(! channelsChanged.contains(selectedChannel))
      channelsChanged.add(selectedChannel);
    Object[] path;
    int[] childIndices = new int[1];
    Object root = getRoot();
    path = new Object[]{root, selectedChannel.getBoard()};
    childIndices[0] = selectedChannel.getNumber();
    fireTreeNodesChanged(this, path,  childIndices, new Object[]{selectedChannel});
    if(boardChanged)
    {
      //Need to fire changed for the board also to update the check sums.
      Object[] grandParent = new Object[]{root};
      int[] indexOfParent = new int[]{((ArrayList)root).indexOf(path[1])};
      Object[] parent = new Object[]{path[1]};
      fireTreeNodesChanged(this, grandParent, indexOfParent, parent);
    }
  }

  /**
   * Used to determine if the data has been changed. This method returns 
   * <CODE>true</CODE> if the data has been changed or new data has been 
   * inserted since the last time <CODE>setBoards</CODE> or 
   * <CODE>changesPosted</CODE> was invoked.
   * 
   * @return <CODE>true</CODE> if the data has been changed, <CODE>false</CODE> if not.
   */
  public boolean isPostNeeded()
  {
    return channelsChanged.size() > 0;
  }

  /**
   * Gets the instances of <CODE>MPSChannel</CODE> that have been changed.
   * 
   * @return The instances of <CODE>MPSChannel</CODE> that have been changed.
   */
  public MPSChannel[] getChannelsChanged()
  {
    return (MPSChannel[])channelsChanged.toArray(new MPSChannel[(channelsChanged.size())]);
  }

  /**
   * Makes the model editable or read only. By default the model is editable.
   * 
   * @param editable Pass as <CODE>false</CODE> to make the model read only, <CODE>true</CODE> to make it editable.
   */
  public void setEditable(boolean editable)
  {
    this.editable = editable;
  }

  /**
   * Determines if the model is editable.
   * 
   * @return <CODE>true</CODE> if the model is editable, <CODE>false</CODE> if not.
   */
  public boolean isEditable()
  {
    return editable;
  }

  /**
   * Gets the names of the columns that are visible.
   * 
   * @return The names of the columns that are visible.
   */
  public ArrayList getVisibleColumnNames()
  {
    return visibleColumnNames;
  }

  /**
   * Gets the names of the columns that have been hidden.
   * 
   * @return The names of the columns that have been hidden.
   */
  public ArrayList getHiddenColumnNames()
  {
    return hiddenColumnNames;
  }

  /**
   * Shows the given columns. 
   * 
   * @param columnNames The names of the columns to show in the table.
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
    }
  }

  /**
   * This method notifes the model that the pending changes have been posted. 
   * This causes the model to update it's data changed flags.
   */
  public void changesPosted()
  {
    channelsChanged.clear();
  }

  /**
   * Loads the default mask values into the channel. This method calls 
   * <CODE>setChannelValuesToOne</CODE> if the <CODE>Device</CODE> for the given
   * channel is <CODE>null</CODE>. Otherwise it calls 
   * <CODE>setChannelValuesToDefault</CODE>.
   * 
   * @param channel The <CODE>MPSChannel</CODE> to load the default values for.
   * @param oldDeviceID The old device ID value.
   * @return <CODE>true</CODE> if <CODE>channelChanged</CODE> was called for the channel. <CODE>false</CODE> otherwise.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public boolean loadMaskDefaults(MPSChannel channel, String oldDeviceID) throws java.sql.SQLException
  {
    boolean eventsFired = false;
    Device channelDevice = channel.getDevice();
    if(channelDevice == null)
    {
      if(oldDeviceID != null)   
      {
        setChannelValuesToOne(channel);//Clear everything for blank device.
        eventsFired = true;
      }
    }
    else
      if(! MPSBrowserView.compare(oldDeviceID, channelDevice.getID()))
      {
        setChannelValuesToDefault(channel, oldDeviceID);
        eventsFired = true;
      }
    return eventsFired;
  }

  /**
   * Loads the default mask values into the channel. The default values are 
   * determined by calling the MACH_MODE_DEFAULTS stored procedure. The default
   * values are determined by the MPS device ID assigned to the channel. Passing 
   * a <CODE>MPSChannel</CODE> with a <CODE>null</CODE> value for the MPS device 
   * property will result in a <CODE>java.lang.NullPointerException</CODE>.
   * 
   * @param channel The <CODE>MPSChannel</CODE> to load the default values for.
   * @param oldDeviceID The old device ID value.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void setChannelValuesToDefault(MPSChannel channel, String oldDeviceID) throws java.sql.SQLException
  {
    JFrame mainWindow = getMainWindow();
    if(oldDeviceID != null)//Something was selected previously, prompt...
    {
      int replace = JOptionPane.showConfirmDialog(mainWindow, "Do you want to replace the existing values with the defaults for the new device?", "Confirm Replace", JOptionPane.YES_NO_OPTION);
      if(replace == JOptionPane.NO_OPTION)
        return;//User wants to keep old values.
    }
    JPanel optionPanel = new JPanel();
    optionPanel.setLayout(new GridLayout(2, 1));
    ButtonGroup group = new ButtonGroup();
    JRadioButton defaultButton = new JRadioButton("Default Masks");
    group.add(defaultButton);
    optionPanel.add(defaultButton, null);
    JRadioButton maskButton = new JRadioButton("Mask Off Previous Chains");
    group.add(maskButton);
    optionPanel.add(maskButton, null);
    ButtonModel defaultModel = defaultButton.getModel();
    group.setSelected(defaultModel, true);
    int result = JOptionPane.showConfirmDialog(mainWindow, optionPanel, "Reset to Defaults", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    if(result == JOptionPane.OK_OPTION)
      if(group.getSelection() == defaultModel)
        mpsBrowser.loadDefaults(channel, null);
      else
        mpsBrowser.loadDefaults(channel, channel.getBoard().getChain().getID());
    channelChanged(channel, true);
  }

  /**
   * Sets all of the binary values to one, except for BEAM_OFF, which is set to 0.
   * 
   * @param channel The <CODE>MPSChannel</CODE> for which to set the values.
   */
  public void setChannelValuesToOne(MPSChannel channel)
  {
    mpsBrowser.changeChannelValuesToOne(channel);
    channelChanged(channel, true);
  }

  /**
   * Sets all of the binary values to 0, except for BEAM_OFF, which is set to 1.
   * 
   * @param channel The <CODE>MPSChannel</CODE> for which to set the values.
   */
  public void setChannelValuesToZero(MPSChannel channel)
  {
    mpsBrowser.changeChannelValuesToZero(channel);
    channelChanged(channel, true);
  }
  
  /**
   * Gets the <CODE>DataSource</CODE> used by the model to connect to the 
   * database.
   *
   * @return The <CODE>DataSource</CODE> used to connect to the database.
   */
  public DataSource getDataSource()
  {
    return connectionPool;
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

  /**
   * Sets the <CODE>JComboBox</CODE> used to select a MPS Device for each 
   * channel. The model needs this so that it can keep the list updated so that 
   * it only contains items not already assigned to a channel.
   * 
   * @param mpsDeviceEditor The <CODE>JComboBox</CODE> used to edit the signal ID column.
   */
  public void setMPSDeviceEditor(ComboBoxCellEditor mpsDeviceEditor)
  {
    this.mpsDeviceEditor = mpsDeviceEditor;
  }

  /**
   * Gives the window a reference to the main window. This is needed to launch 
   * the signal table browser with the generated signals. If this is not 
   * provided it will be located.
   * 
   * @param mainWindow The main window of the application.
   */
  public void setMainWindow(MainFrame mainWindow)
  {
    this.mainWindow = mainWindow;
  }

  /**
   * Gets a reference to the main window of the application.
   * 
   * @return The main window of the application.
   */
  public MainFrame getMainWindow()
  {
	    return mainWindow;
  }
  
  /**
   * Sets the <CODE>MPSBrowser</CODE> that contains the database functions.
   * 
   * @param newMPSBrowser The <CODE>MPSBrowser</CODE> containing the database functions.
   */
  public void setMPSBrowser(MPSBrowser newMPSBrowser)
  {
	  mpsBrowser = newMPSBrowser;
  }
  
  /**
   * Gets the <CODE>MPSBrowser</CODE> containing the database functions.
   * 
   * @return The <CODE>MPSBrowser</CODE> containing the database functions.
   */
  public MPSBrowser getMPSBrowser()
  {
    return mpsBrowser;
  }
}