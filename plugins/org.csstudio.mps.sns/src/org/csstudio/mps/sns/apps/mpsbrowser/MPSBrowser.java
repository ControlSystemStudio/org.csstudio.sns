package org.csstudio.mps.sns.apps.mpsbrowser;

import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.EpicsSubsystem;
import org.csstudio.mps.sns.tools.data.EpicsSystem;
import org.csstudio.mps.sns.tools.data.MPSBoard;
import org.csstudio.mps.sns.tools.data.MPSChain;
import org.csstudio.mps.sns.tools.data.MPSChannel;
import org.csstudio.mps.sns.tools.data.MPSChannelAudit;
import org.csstudio.mps.sns.tools.data.MPSChassis;
import org.csstudio.mps.sns.tools.data.Signal;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.application.JeriDataModule;
import java.awt.Cursor;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * This class holds the functionality and data for the MPS browser function in 
 * JERI.
 * 
 * @author Chris Fowlkes
 */
public class MPSBrowser extends JeriDataModule
{
  /**
   * Holds the chain data.
   */
  private HashMap allChains = new HashMap();
  /**
   * Holds the chassis data.
   */
  private HashMap allChassis = new HashMap();
  /**
   * Holds the number of rows fetched at a time when the data is loaded.
   */
  private int fetchSize = 750;
  /**
   * Stores the names of the binary field names from the database.
   */
  private String[] binaryColumnNames;
  /**
   * Flag used to determine if a commit needs to be done due to pending changes 
   * or not.
   */
  private boolean commitNeeded = false;
  /**
   * Used to remove boards from the database.
   */
  private PreparedStatement boardDeleteQuery;
  /**
   * Used to remove channels from the database.
   */
  private PreparedStatement channelDeleteQuery;

  /**
   * Creates a new <CODE>MPSBrowser</CODE>.
   */
  public MPSBrowser()
  {
  }

  /**
   * Reloads all of the data in the interface.
   * 
   * @param selectedChain The <CODE>MPSChain</CODE> selected, <CODE>null</CODE> if no chain is selected.
   * @return The instances of <CODE>MPSBoard</CODE> containing the data loaded.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public MPSBoard[] loadBoards(MPSChain selectedChain) throws java.sql.SQLException
  {
    try
    {
    	
      setMessage("Loading MPS Data");
      setProgressIndeterminate(true);
      Connection oracleConnection = getConnection();
      if(oracleConnection.isClosed())
        return new MPSBoard[0];
      Iterator chainIterator = allChains.values().iterator();
      //Clear all chains of boards...
      while(chainIterator.hasNext())
        ((MPSChain)chainIterator.next()).clear();
      Iterator chassisIterator = allChassis.values().iterator();
      while(chassisIterator.hasNext())
        ((MPSChassis)chassisIterator.next()).clear();
      StringBuffer sql = new StringBuffer("SELECT MPS_SGNL_PARAM.*, DVC.ACT_DVC_IND, DVC.SYS_ID, DVC.SUBSYS_ID");
      StringBuffer whereClause = new StringBuffer(" FROM ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".MPS_SGNL_PARAM, ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".DVC WHERE MPS_SGNL_PARAM.DVC_ID = DVC.DVC_ID");
      if(selectedChain != null)
        whereClause.append(" AND MPS_SGNL_PARAM.MPS_CHAIN_ID = ?");
      sql.append(whereClause);
      sql.append(" ORDER BY MPS_SGNL_PARAM.DVC_ID, MPS_SGNL_PARAM.APPR_DTE");
      PreparedStatement query = oracleConnection.prepareStatement(sql.toString());
      try
      {  
        int expectedCount;
        sql = new StringBuffer("SELECT COUNT(*)");
        sql.append(whereClause);
        PreparedStatement countQuery = oracleConnection.prepareStatement(sql.toString());
        try
        {
          if(selectedChain != null)
          {
            query.setString(1, selectedChain.getID());
            countQuery.setString(1, selectedChain.getID());
          }
          expectedCount = runCountQuery(countQuery);
          setProgressMaximum(expectedCount);
        }
        finally
        {
          countQuery.close();
        }
        //Load the data in MPS_SGNL_PARAM.
        query.setFetchSize(fetchSize);
        ResultSet deviceResult = query.executeQuery();
        try
        {
          String currentDeviceID = null;
          MPSChain currentChain = null;
          MPSChassis currentChassis = null;
          java.sql.Date currentApproveDate = null;              
          sql = new StringBuffer("SELECT MACHINE_MODE.* FROM ");
          sql.append(MPSBrowserView.SCHEMA);
          sql.append(".MACHINE_MODE WHERE MACHINE_MODE.DVC_ID = ? AND MACHINE_MODE.APPR_DTE = ? ORDER BY MACHINE_MODE.CHANNEL_NBR");
          PreparedStatement detailsQuery = oracleConnection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
          try
          {
            PreparedStatement auditQuery = oracleConnection.prepareStatement("SELECT PASS_IND, AUDIT_DTE FROM EPICS.MPS_CHAN_AUDIT WHERE DVC_ID = ? AND CHANNEL_NBR = ? AND MPS_DVC_ID = ? AND AUDIT_DTE = (SELECT MAX(AUDIT_DTE) FROM EPICS.MPS_CHAN_AUDIT WHERE DVC_ID = ? AND CHANNEL_NBR = ? AND MPS_DVC_ID = ?)");
            try
            {
              ArrayList allBoards = new ArrayList(expectedCount);
              int progress = 0;
              setProgressValue(0);
              setProgressIndeterminate(false);
              while(deviceResult.next())
              {
                //Load the channel data for each device.
                currentDeviceID = deviceResult.getString("DVC_ID");
                currentApproveDate = deviceResult.getDate("APPR_DTE");
                MPSBoard newBoard = new MPSBoard(currentDeviceID);
                EpicsSystem currentSystem = new EpicsSystem(deviceResult.getString("SYS_ID"));
                newBoard.setSystem(currentSystem);
                EpicsSubsystem currentSubsystem = new EpicsSubsystem(deviceResult.getString("SUBSYS_ID"));
                newBoard.setSubsystem(currentSubsystem);
                newBoard.setApproveDate(currentApproveDate);
                newBoard.setActiveDeviceIndicator(deviceResult.getString("ACT_DVC_IND"));
                String currentChainID = deviceResult.getString("MPS_CHAIN_ID");
                if(currentChain == null || ! currentChain.getID().equals(currentChainID))
                  currentChain = (MPSChain)allChains.get(currentChainID);
                currentChain.addBoard(newBoard);
                newBoard.setSerialNumber(deviceResult.getString("SERIAL_NBR"));
                newBoard.setSoftwareVersion(deviceResult.getString("SFT_VER"));
                newBoard.setFPLTotal(deviceResult.getString("FPL_TOTAL"));
                newBoard.setFPARTotal(deviceResult.getString("FPAR_TOTAL"));
                newBoard.setChassisConfigurationJumpers(deviceResult.getInt("CHASSIS_CONFIG_JMP"));
                newBoard.setHeartbeatIndicator(deviceResult.getString("HEARTBEAT_IND"));
                newBoard.setSWJumper(deviceResult.getString("SW_JUMP"));
                newBoard.setMPSIn(deviceResult.getInt("MPS_IN"));
                newBoard.setFPARFastProtectLatchConfig(deviceResult.getString("FPAR_FPL_CONFIG"));
                newBoard.setFPARTotal(deviceResult.getString("FPAR_TOTAL"));
                newBoard.setDateCode(deviceResult.getDate("DTE_CDE"));
                newBoard.setChainEndIndicator(deviceResult.getString("CHAIN_END_IND"));
                newBoard.setPMCNumber(deviceResult.getInt("PMC_NBR"));
                String currentChassisID = deviceResult.getString("IOC_DVC_ID");
                if(currentChassis == null || ! currentChassis.getID().equals(currentChassisID))
                {
                  currentChassis = (MPSChassis)allChassis.get(currentChassisID);
                  if(currentChassis == null)
                  {
                    //Haven't encountered this chassis before. Add to list.
                    currentChassis = new MPSChassis(currentChassisID);
                    allChassis.put(currentChassisID, currentChassis);
                  }
                }
                newBoard.setChannelsInDatabase(false);//These will be set to true as the're loaded.
                currentChassis.addBoard(newBoard);
                detailsQuery.setString(1, currentDeviceID);
                detailsQuery.setDate(2, currentApproveDate);
                ResultSet detailsResult = detailsQuery.executeQuery();
                try
                {
                  while(detailsResult.next())
                  {
                    MPSChannel channel = new MPSChannel(detailsResult);
                    //SELECT PASS_IND, AUDIT_DTE FROM EPICS.MPS_CHAN_AUDIT 
                    //WHERE DVC_ID = ? AND CHANNEL_NBR = ? AND MPS_DVC_ID = ? 
                    //AND AUDIT_DTE = (SELECT MAX(AUDIT_DTE) 
                    //FROM EPICS.MPS_CHAN_AUDIT WHERE DVC_ID = ? 
                    //AND CHANNEL_NBR = ? AND MPS_DVC_ID = ?)
                    auditQuery.setString(1, currentDeviceID);
                    int channelNumber = channel.getNumber();
                    auditQuery.setInt(2, channelNumber);
                    String mpsDeviceID = channel.getDevice().getID();
                    auditQuery.setString(3, mpsDeviceID);
                    auditQuery.setString(4, currentDeviceID);
                    auditQuery.setInt(5, channelNumber);
                    auditQuery.setString(6, mpsDeviceID);
                    ResultSet auditResults = auditQuery.executeQuery();
                    try 
                    {
                      if(auditResults.next())
                        channel.addAudit(new MPSChannelAudit(auditResults));
                    }
                    finally 
                    {
                      auditResults.close();
                    }
                    newBoard.addChannel(channel);
                  }
                }
                finally
                {
                  detailsResult.close();
                }
                allBoards.add(newBoard);
                setProgressValue(++progress);
              }
              return (MPSBoard[])allBoards.toArray(new MPSBoard[(allBoards.size())]);
            }
            finally
            {
              auditQuery.close();
            }
          }
          finally
          {
            detailsQuery.close();
          }
        }
        finally
        {
          deviceResult.close();
        }
      }
      finally
      {
        query.close();
      }
    }
    finally
    {
      clearProgress();
    }
  }
  
//  /**
//   * Gets the instances of <CODE>MPSBoard</CODE> created when the 
//   * <CODE>reloadData</CODE> method was last invoked.
//   * 
//   * @return The instances of <CODE>MPSBoard</CODE> created y the <CODE>reloadData</CODE> method.
//   */
//  public MPSBoard[] getAllBoards()
//  {
//    return allBoards;
//  }
  
  /**
   * Loads the names of the binary columns from the database.
   * 
   * @return The names of the binary columns.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public String[] loadBinaryColumnNames() throws java.sql.SQLException
  {
    try
    {
      setMessage("Loading Binary Column Names");
      setProgressIndeterminate(true);
      Connection oracleConnection = getConnection();
      if(oracleConnection.isClosed())
        return new String[0];
      StringBuffer sql = new StringBuffer("SELECT MACHINE_MODE.* FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".MACHINE_MODE WHERE MACHINE_MODE.DVC_ID = ? AND MACHINE_MODE.DVC_ID = ?");
      PreparedStatement query = oracleConnection.prepareStatement(sql.toString());
      try
      {
        //Need a result set with no data to be as fast as possible.
        query.setString(1, "A");
        query.setString(2, "B");
        ResultSet result = query.executeQuery();
        try
        {
          ArrayList binaryColumns = new ArrayList();
          ResultSetMetaData metaData = result.getMetaData();
          int columnCount = metaData.getColumnCount();
          setProgressMaximum(columnCount);
          setProgressValue(0);
          setProgressIndeterminate(false);
          for(int i=1;i<=columnCount;i++)
          {
            if(metaData.getColumnType(i) == Types.VARBINARY)
              binaryColumns.add(metaData.getColumnName(i));
            setProgressValue(i);
          }
          binaryColumnNames = (String[])binaryColumns.toArray(new String[binaryColumns.size()]);
//          for(int i=columnCount;i>0;i--)
//            if(metaData.getColumnType(i) == Types.VARBINARY)
//              binaryColumns.add(metaData.getColumnName(i));
//          binaryColumnNames = new String[binaryColumns.size()];
//          for(int i=0;i<binaryColumnNames.length;i++)
//            binaryColumnNames[binaryColumnNames.length-i-1] = binaryColumns.get(i).toString();
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
    finally
    {
      clearProgress();
    }
    return binaryColumnNames;
  }
  
  /**
   * Loads the IOCs being displayed.
   * 
   * @return The IOCs from the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public MPSChassis[] loadIOCs() throws java.sql.SQLException
  {
    try
    {
      setMessage("Loading IOCs");
      setProgressIndeterminate(true);
      Connection oracleConnection = getConnection();
      if(oracleConnection.isClosed())
        return new MPSChassis[0];
      //Load IOCs.
      StringBuffer sql = new StringBuffer("SELECT DVC_ID");
      StringBuffer whereClause = new StringBuffer(" FROM ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".DVC WHERE DVC_TYPE_ID = ?");
      sql.append(whereClause);
      sql.append(" ORDER BY DVC_ID");
      PreparedStatement query = oracleConnection.prepareStatement(sql.toString());
      try
      {
        sql = new StringBuffer("SELECT COUNT(*)");
        sql.append(whereClause);
        int expectedCount;
        PreparedStatement countQuery = oracleConnection.prepareStatement(sql.toString());
        try
        {
          query.setString(1, "IOC");
          countQuery.setString(1, "IOC");
          expectedCount = runCountQuery(countQuery);
          setProgressMaximum(expectedCount);
        }
        finally
        {
          countQuery.close();
        }
        ResultSet result = query.executeQuery();
        try
        {
          ArrayList allChassis = new ArrayList(expectedCount);
          int progress = 0;
          setProgressValue(0);
          setProgressIndeterminate(false);
          while(result.next())
          {
            allChassis.add(new MPSChassis(result.getString("DVC_ID")));
            setProgressValue(++progress);
          }
          return (MPSChassis[])allChassis.toArray(new MPSChassis[(allChassis.size())]);
        }//try
        finally
        {
          result.close();
        }//finally
      }
      finally
      {
        query.close();
      }
    }
    finally
    {
      clearProgress();
    }
  }
  /**
   * Gets the names of the binary column names. This method will not load the 
   * names from the database. You must call <CODE>loadBinaryColumnNames</CODE> 
   * to do that. If the names have not yet been loaded, this method returns
   * <CODE>null</CODE>.
   * 
   * @return The names of the binary columns, or <CODE>null</CODE> if they have not been loaded.
   */
  public String[] getBinaryColumnNames()
  {
    return binaryColumnNames;
  }
  
  /**
   * Commits all changes made to the data.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void commit() throws java.sql.SQLException
  {
    getConnection().commit();
    commitNeeded = false;
  }

  /**
   * Does a rollback on any pending changes.
   * 
   * @throws java.sql.SQLException thrown on SQL error.
   */
  public void rollback() throws java.sql.SQLException
  {
    getConnection().rollback();
    commitNeeded = false;
  }
  
  /**
   * Deletes the given channels from the database.
   * 
   * @param board The board of which to delete the channels.
   * @throws java.sql.SQLException Thrown on a SQL error.
   */
  public void deleteChannels(MPSBoard board) throws java.sql.SQLException
  {
    if(channelDeleteQuery == null)
    {
      StringBuffer sql = new StringBuffer("DELETE FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".MACHINE_MODE WHERE DVC_ID = ? AND APPR_DTE = ?");
      channelDeleteQuery = getConnection().prepareStatement(sql.toString());
    }//if(channelDeleteQuery == null)
    channelDeleteQuery.setString(1, board.getID());
    channelDeleteQuery.setDate(2, board.getApproveDate());
    channelDeleteQuery.execute();
    commitNeeded = true;
  }
  
  /**
   * Deletes the given <CODE>MPSBoard</CODE> from the database. This method does 
   * not delete the data for any channels associated with the board. If there is
   * channel data in the database when this method is called an exception will
   * be thrown.
   * 
   * @param board The instance of <CODE>MPSBoard</CODE> representing the data to delete.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void deleteBoard(MPSBoard board) throws java.sql.SQLException
  {
    if(boardDeleteQuery == null)
    {
      StringBuffer sql = new StringBuffer("DELETE FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".MPS_SGNL_PARAM WHERE DVC_ID = ? AND APPR_DTE = ?");
      boardDeleteQuery = getConnection().prepareStatement(sql.toString());
    }//if(boardDeleteQuery == null)
    boardDeleteQuery.setString(1, board.getID());
    boardDeleteQuery.setDate(2, board.getApproveDate());
    boardDeleteQuery.execute();
    commitNeeded = true;
  }

  /**
   * Sets the <CODE>Connection</CODE> used to connect ot the database.
   * 
   * @param oracleConnection The <CODE>Connection</CODE> used to connect to the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void setConnection(Connection oracleConnection) throws java.sql.SQLException
  {
    super.setConnection(oracleConnection);
    oracleConnection.setAutoCommit(false);
  }
  
  /**
   * Loads the instances of <CODE>MPSChain</CODE> from the database.
   * 
   * @return The instances of <CODE>MPSChain</CODE> created from all of the database data.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public MPSChain[] loadChains() throws java.sql.SQLException
  {
    try
    {
      setMessage("Loading Chains");
      setProgressIndeterminate(true);
      Connection oracleConnection = getConnection();
      if(oracleConnection.isClosed())
        return new MPSChain[0];
      StringBuffer sql = new StringBuffer("SELECT COUNT(*)");
      StringBuffer whereClause = new StringBuffer(" FROM ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".MPS_CHAIN");
      sql.append(whereClause);
      int expectedCount;
      PreparedStatement countQuery = oracleConnection.prepareStatement(sql.toString());
      try
      {
        expectedCount = runCountQuery(countQuery);
      }
      finally
      {
        countQuery.close();
      }
      setProgressMaximum(expectedCount);
      sql = new StringBuffer("SELECT MPS_CHAIN_ID, MPS_CHAIN_NM");
      sql.append(whereClause);
      PreparedStatement query = oracleConnection.prepareStatement(sql.toString());
      try
      {
        query.setFetchSize(fetchSize);
        //Load all chains
        ResultSet result = query.executeQuery();
        try
        {
          int progress = 0;
          setProgressValue(0);
          setProgressIndeterminate(false);
          while(result.next())
          {
            String currentID = result.getString("MPS_CHAIN_ID");
            String currentName = result.getString("MPS_CHAIN_NM");
            MPSChain currentChain = new MPSChain(currentID, currentName);
            allChains.put(currentID, currentChain);
            setProgressValue(++progress);
          }//while(result.next())
        }//try
        finally
        {
          result.close();
        }//finally
      }//try
      finally
      {
        query.close();
      }//finally
      Collection chainList = allChains.values();
      return (MPSChain[])chainList.toArray(new MPSChain[(chainList.size())]);
    }
    finally
    {
      clearProgress();
    }
  }
  
  /**
   * Determines if changes have been made.
   * 
   * @return <CODE>true</CODE> if changes are pending, <CODE>false</CODE> otherwise.
   */
  public boolean isCommitNeeded()
  {
    return commitNeeded;
  }
  
  /**
   * Posts the changes to the given instances of <CODE>MPSBoard</CODE>.
   * 
   * @param changedBoards The instances of <CODE>MPSBoard</CODE> changed.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void postBoardChanges(MPSBoard[] changedBoards) throws java.sql.SQLException
  {
    try
    {
      setMessage("Posting Device Tab Changes");
      setProgressIndeterminate(true);
      setProgressMaximum(changedBoards.length);
      PreparedStatement deviceUpdateQuery = null;
      try
      {
        PreparedStatement deviceInsertQuery = null;
        try
        {
          Connection oracleConnection = getConnection();
          setProgressValue(0);
          setProgressIndeterminate(false);
          for(int i=0;i<changedBoards.length;i++)
          {
            PreparedStatement deviceQuery;
            if(changedBoards[i].isInDatabase())
            {
              if(deviceUpdateQuery == null)
              {
                StringBuffer sql = new StringBuffer("UPDATE ");
                sql.append(MPSBrowserView.SCHEMA);
                sql.append(".MPS_SGNL_PARAM SET MPS_CHAIN_ID = ?, SERIAL_NBR = ?, SFT_VER = ?, FPL_TOTAL = ?, FPAR_TOTAL = ?, CHASSIS_CONFIG_JMP = ?, HEARTBEAT_IND = ?, SW_JUMP = ?, MPS_IN = ?, FPAR_FPL_CONFIG = ?, DTE_CDE = ?, CHAIN_END_IND = ?, PMC_NBR = ?, IOC_DVC_ID = ? WHERE DVC_ID = ? AND APPR_DTE = ?");
                deviceUpdateQuery = oracleConnection.prepareStatement(sql.toString());
              }//if(deviceUpdateQuery == null)
              deviceQuery = deviceUpdateQuery;
            }//if(currentBoard.isInDatabase())
            else
            {
              if(deviceInsertQuery == null)
              {
                StringBuffer sql = new StringBuffer("INSERT INTO ");
                sql.append(MPSBrowserView.SCHEMA);
                sql.append(".MPS_SGNL_PARAM (MPS_CHAIN_ID, SERIAL_NBR, SFT_VER, FPL_TOTAL, FPAR_TOTAL, CHASSIS_CONFIG_JMP, HEARTBEAT_IND, SW_JUMP, MPS_IN, FPAR_FPL_CONFIG, DTE_CDE, CHAIN_END_IND, PMC_NBR, IOC_DVC_ID, DVC_ID, APPR_DTE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                deviceUpdateQuery = oracleConnection.prepareStatement(sql.toString());
              }//if(deviceUpdateQuery == null)
              deviceQuery = deviceUpdateQuery;
            }//else
            MPSChain currentChain = changedBoards[i].getChain();
            if(currentChain == null)
              deviceQuery.setString(1, null);//This should get rejected by RDB
            else
              deviceQuery.setString(1, currentChain.getID());
            deviceQuery.setString(2, changedBoards[i].getSerialNumber());
            deviceQuery.setString(3, changedBoards[i].getSoftwareVersion());
            deviceQuery.setString(4, changedBoards[i].getFPLTotal());
            deviceQuery.setString(5, changedBoards[i].getFPARTotal());
            deviceQuery.setInt(6, changedBoards[i].getChassisConfigurationJumpers());
            String heartbeatIndicator = changedBoards[i].getHeartbeatIndicator();
            if(heartbeatIndicator == null)
              heartbeatIndicator = "N";
            deviceQuery.setString(7, heartbeatIndicator);
            deviceQuery.setString(8, changedBoards[i].getSWJumper());
            deviceQuery.setInt(9, changedBoards[i].getMPSIn());
            deviceQuery.setString(10, changedBoards[i].getFPARFastProtectLatchConfiguration());
            deviceQuery.setDate(11, changedBoards[i].getDateCode());
            String chainEndIndicator = changedBoards[i].getChainEndIndicator();
            if(chainEndIndicator == null)
              chainEndIndicator = "N";
            deviceQuery.setString(12, chainEndIndicator);
            deviceQuery.setInt(13, changedBoards[i].getPMCNumber());
            MPSChassis currentChassis = changedBoards[i].getChassis();
            if(currentChassis == null)
              deviceQuery.setString(14, null);//This should get rejected by RDB
            else
              deviceQuery.setString(14, currentChassis.getID());
            deviceQuery.setString(15, changedBoards[i].getID());
            deviceQuery.setDate(16, changedBoards[i].getApproveDate());
            deviceQuery.execute();
            changedBoards[i].setInDatabase(true);
            commitNeeded = true;
            setProgressValue(i + 1);
          }
        }
        finally
        {
          if(deviceInsertQuery != null)
            deviceInsertQuery.close();
        }
      }
      finally
      {
        if(deviceUpdateQuery != null)
          deviceUpdateQuery.close();
      }
    }
    finally
    {
      clearProgress();
    }
  }
  
  /**
   * Posts the changes to the given instances of <CODE>MPSChannel</CODE>.
   * 
   * @param changedChannels The instances of <CODE>MPSChannel</CODE> to post.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void postChannelChanges(MPSChannel[] changedChannels) throws java.sql.SQLException
  {
    try
    {
      setMessage("Posting Details Tab Changes");
      setProgressIndeterminate(true);
      setProgressMaximum(changedChannels.length);
      PreparedStatement channelUpdateQuery = null;
      try
      {
        PreparedStatement channelInsertQuery = null;
        try
        {
          Connection oracleConnection = getConnection();
          setProgressValue(0);
          setProgressIndeterminate(false);
          for(int i=0;i<changedChannels.length;i++) 
          {
            PreparedStatement channelQuery;
            if(changedChannels[i].isInDatabase())
            {
              if(channelUpdateQuery == null)
              {
                StringBuffer sql = new StringBuffer("UPDATE ");
                sql.append(MPSBrowserView.SCHEMA);
                sql.append(".MACHINE_MODE SET MPS_DVC_ID = ?, LOCKED_IND = ?, SW_JUMP = ?, LIMIT = ?, RATE = ?, CHAN_IN_USE_IND = ?");
                for(int j=0;j<binaryColumnNames.length;j++)
                {
                  sql.append(", ");
                  sql.append(binaryColumnNames[j]);
                  sql.append(" = ?");
                }//for(int i=0;i<binaryColumnNames.length;i++)
                sql.append(" WHERE DVC_ID = ? AND APPR_DTE = ? AND CHANNEL_NBR = ?");
                channelUpdateQuery = oracleConnection.prepareStatement(sql.toString());
              }//if(channelUpdateQuery == null)
              channelQuery = channelUpdateQuery;
            }//if(currentChannel.isInDatabase())
            else
            {
              if(channelInsertQuery == null)
              {
                StringBuffer sql = new StringBuffer("INSERT INTO ");
                sql.append(MPSBrowserView.SCHEMA);
                sql.append(".MACHINE_MODE (MPS_DVC_ID, LOCKED_IND, SW_JUMP, LIMIT, RATE, CHAN_IN_USE_IND");
                for(int j=0;j<binaryColumnNames.length;j++)
                {
                  sql.append(", ");
                  sql.append(binaryColumnNames[j]);
                }//for(int i=0;i<binaryColumnNames.length;i++)
                sql.append(", DVC_ID, APPR_DTE, CHANNEL_NBR) VALUES (?, ?, ?, ?, ?, ?");
                for(int j=0;j<binaryColumnNames.length;j++)
                  sql.append(", ?");
                sql.append(", ?, ?, ?)");
                channelInsertQuery = oracleConnection.prepareStatement(sql.toString());
              }//if(channelInsertQuery == null)
              channelQuery = channelInsertQuery;
            }//else
            Device currentMPSDevice = changedChannels[i].getDevice();
            if(currentMPSDevice == null)
              channelQuery.setString(1, null);
            else
              channelQuery.setString(1, currentMPSDevice.getID());
            channelQuery.setString(2, changedChannels[i].getLockedIndicator());
            channelQuery.setString(3, changedChannels[i].getSWJumper());
            channelQuery.setInt(4, changedChannels[i].getLimit());
            channelQuery.setInt(5, changedChannels[i].getRate());
            channelQuery.setString(6, changedChannels[i].getInUseIndicator());
            for(int j=0;j<binaryColumnNames.length;j++)
            {
              BigInteger bigIntegerValue = changedChannels[i].getBinaryValue(binaryColumnNames[j]);
              if(bigIntegerValue == null)
                if(binaryColumnNames[j].equals("BEAM_OFF"))
                  bigIntegerValue = new BigInteger("0");
                else
                  bigIntegerValue = new BigInteger("1");
              channelQuery.setBytes(j + 7, bigIntegerValue.toByteArray());
            }  
            MPSBoard currentBoard = changedChannels[i].getBoard();
            channelQuery.setString(binaryColumnNames.length + 7, currentBoard.getID());
            channelQuery.setDate(binaryColumnNames.length + 8, currentBoard.getApproveDate());
            channelQuery.setInt(binaryColumnNames.length + 9, changedChannels[i].getNumber());
            channelQuery.execute();
            changedChannels[i].setInDatabase(true);
            commitNeeded = true;
            setProgressValue(i + 1);
          }
        }
        finally
        {
          if(channelInsertQuery != null)
            channelInsertQuery.close();
        }
      }
      finally
      {
        if(channelUpdateQuery != null)
          channelUpdateQuery.close();
      }
    }
    finally
    {
      clearProgress();
    }
  }
  
  /**
   * Closes the connection to the database used by the class.
   * 
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void closeConnection() throws java.sql.SQLException
  {
    getConnection().close();
  }
  
  /**
   * Loads the default values for the binary fields in the 
   * <CODE>MPSChannel</CODE>.
   * 
   * @param channel The channel on which to set the default values.
   * @param chainID The ID of the chain. Pass as <CODE>null</CODE> to do a standard load defaults, passing a value masks off previous chains.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void loadDefaults(MPSChannel channel, String chainID) throws java.sql.SQLException
  {
    Connection oracleConnection = getConnection();
    CallableStatement defaultsCall = oracleConnection.prepareCall("{? = call EPICS.EPICS_MPS_PKG.MACH_MODE_DEFAULTS(?, ?)}");
    try
    {
      defaultsCall.registerOutParameter(1, Types.VARCHAR);
      defaultsCall.setString(2, channel.getDevice().getID());
      defaultsCall.setString(3, chainID);
      defaultsCall.execute();
      char[] defaultValues = defaultsCall.getString(1).toCharArray();
      channel.setSWJumper(String.valueOf(defaultValues[0]));
      for(int i=1;i<defaultValues.length;i++)
      {
        BigInteger nextValue = new BigInteger(String.valueOf(defaultValues[i]));
        channel.setBinaryValue(binaryColumnNames[i-1], nextValue);
      }//for(int i=1;i<defaultValues.length;i++)
    }//try
    finally
    {
      defaultsCall.close();
    }//finally
  }

  /**
   * Makes all of the binary values one except for BEAM_OFF, which is set to
   * zero. SW_JUMP is set to <CODE>"N"</CODE>.
   * 
   * @param channel The channel on which to set the values.
   */
  public void changeChannelValuesToOne(MPSChannel channel)
  {
    channel.setSWJumper("N");
//    channel.setFastProtectLatch(null);
    BigInteger one = new BigInteger("1");
    for(int i=0;i<binaryColumnNames.length;i++)
      if(binaryColumnNames[i].equals("BEAM_OFF"))
        channel.setBinaryValue(binaryColumnNames[i], new BigInteger("0"));
      else
        channel.setBinaryValue(binaryColumnNames[i], one);
  }
  
  /**
   * Makes all of the binary values zero except for BEAM_OFF, which is set to
   * one. SW_JUMP is set to <CODE>"Y"</CODE>.
   * 
   * @param channel The channel on which to set the values.
   */
  public void changeChannelValuesToZero(MPSChannel channel)
  {
    channel.setSWJumper("Y");
//    channel.setFastProtectLatch(null);
    BigInteger zero = new BigInteger("0");
    for(int i=0;i<binaryColumnNames.length;i++)
      if(binaryColumnNames[i].equals("BEAM_OFF"))
        channel.setBinaryValue(binaryColumnNames[i], new BigInteger("1"));
      else
        channel.setBinaryValue(binaryColumnNames[i], zero);
  }

  /**
   * Reloads the serial numbers in the signal field editor. This method should 
   * only be invoked after <CODE>setDataSource</CODE> and 
   * <CODE>setSignalEditor</CODE> have both been invoked.
   * 
   * @return The serial numbers loaded.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public ArrayList loadSerialNumbers() throws java.sql.SQLException
  {
    try
    {
      setMessage("Loading Serial Numbers");
      setProgressIndeterminate(true);
      //Load signal data.
      Connection oracleConnection = getConnection();
      if(oracleConnection.isClosed())
        return new ArrayList();
      StringBuffer sql = new StringBuffer("SELECT COUNT(*)");
      StringBuffer whereClause = new StringBuffer(" FROM ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".MPS_CHASSIS_SER_NBR WHERE SER_NBR NOT IN (SELECT SERIAL_NBR FROM ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".MPS_SGNL_PARAM)");
      sql.append(whereClause);
      int expectedCount;
      PreparedStatement countQuery = oracleConnection.prepareStatement(sql.toString());
      try
      {
        expectedCount = runCountQuery(countQuery);
      }
      finally
      {
        countQuery.close();
      }
      ArrayList defaultItems = new ArrayList(expectedCount + 1);
      setProgressMaximum(expectedCount);
      sql = new StringBuffer("SELECT SER_NBR");
      sql.append(whereClause);
      PreparedStatement query = oracleConnection.prepareStatement(sql.toString());
      try
      {
        ResultSet result = query.executeQuery();
        try
        {
          defaultItems.add(null);
          int progress = 0;
          setProgressValue(0);
          setProgressIndeterminate(false);
          while(result.next())
          {
            defaultItems.add(result.getString("SER_NBR"));
            setProgressValue(++progress);
          }
          return defaultItems;
        }//try
        finally
        {
          result.close();
        }//finally
      }//try
      finally
      {
        query.close();
      }//finally
    }
    finally
    {
      clearProgress();
    }
  }

  /**
   * Reloads the signal IDs in the signal field editor. This method should only
   * be invoked after <CODE>setDataSource</CODE> and 
   * <CODE>setSignalEditor</CODE> have both been invoked.
   * 
   * @return The instances of <CODE>Device</CODE> loaded.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public Device[] loadDeviceIDs() throws java.sql.SQLException
  {
    try
    {
      setMessage("Loading Device IDs");
      setProgressIndeterminate(true);
      //Load Device data.
      Connection oracleConnection  = getConnection();
      if(oracleConnection.isClosed())
        return new Device[0];
      StringBuffer sql = new StringBuffer("SELECT DVC_ID, SYS_ID, SUBSYS_ID");
      StringBuffer whereClause = new StringBuffer(" FROM ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".DVC WHERE DVC_ID NOT IN (SELECT MPS_DVC_ID FROM ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".MACHINE_MODE WHERE MPS_DVC_ID IS NOT NULL) AND MPS_DVC_IND = ?");
      sql.append(whereClause);
      sql.append(" ORDER BY DVC_ID");
      PreparedStatement query = oracleConnection.prepareStatement(sql.toString());
      try
      {
        int expectedCount;
        sql = new StringBuffer("SELECT COUNT(*)");
        sql.append(whereClause);
        PreparedStatement countQuery = oracleConnection.prepareStatement(sql.toString());
        try
        {
          query.setString(1, "Y");
          countQuery.setString(1, "Y");
          expectedCount = runCountQuery(countQuery);
          setProgressMaximum(expectedCount);
        }
        finally
        {
          countQuery.close();
        }
        ResultSet result = query.executeQuery();
        try
        {
          ArrayList devices = new ArrayList(expectedCount);
          int progress = 0;
          setProgressValue(0);
          setProgressIndeterminate(false);
          while(result.next())
          {
            Device newDevice = new Device(result.getString("DVC_ID"));
            newDevice.setSystem(new EpicsSystem(result.getString("SYS_ID")));
            newDevice.setSubsystem(new EpicsSubsystem(result.getString("SUBSYS_ID")));
            devices.add(newDevice);
            setProgressValue(++progress);
          }
          return (Device[])devices.toArray(new Device[(devices.size())]);
        }//try
        finally
        {
          result.close();
        }//finally
      }//try
      finally
      {
        query.close();
      }//finally
    }
    finally
    {
      clearProgress();
    }
  }
  
  /**
   * Updates the audit results for the given instances of 
   * <CODE>MPSChannel</CODE>.
   * 
   * @param channels The instances of <CODE>MPSChannel</CODE> to update.
   * @param passed Pass as <CODE>true</CODE> to update, <CODE>false</CODE> otherwise.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void updateAuditResults(MPSChannel[] channels, boolean passed) throws java.sql.SQLException
  {
    try
    {
      setMessage("Updating Audit Results");
      setProgressIndeterminate(true);
      Connection oracleConnection  = getConnection();
      PreparedStatement query = oracleConnection.prepareStatement("INSERT INTO EPICS.MPS_CHAN_AUDIT (DVC_ID, APPR_DTE, CHANNEL_NBR, MPS_DVC_ID, PASS_IND, AUDIT_TYPE_IND) VALUES (?, ?, ?, ?, ?, ?)");
      try
      {
        String passIndicator = passed ? "Y" : "N";
        query.setString(6, "M");
        setProgressMaximum(channels.length);
        setProgressValue(0);
        setProgressIndeterminate(false);
        for(int i=0;i<channels.length;i++) 
        {
          MPSBoard board = channels[i].getBoard();
          query.setString(1, board.getID());
          query.setDate(2, board.getApproveDate());
          query.setInt(3, channels[i].getNumber());
          query.setString(4, channels[i].getDevice().getID());
          query.setString(5, passIndicator);
          query.execute();
          commitNeeded = true;
          setProgressValue(i + 1);
        }
      }
      finally
      {
        query.close();
      }
    }
    finally
    {
      clearProgress();
    }
  }
}