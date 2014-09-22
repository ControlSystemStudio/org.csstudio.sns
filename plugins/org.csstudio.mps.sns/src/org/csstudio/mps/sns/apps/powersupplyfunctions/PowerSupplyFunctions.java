package org.csstudio.mps.sns.apps.powersupplyfunctions;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.application.JeriDataModule;
import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.IOC;
import org.csstudio.mps.sns.tools.data.Magnet;
import org.csstudio.mps.sns.tools.data.PowerSupply;
import org.csstudio.mps.sns.tools.data.PowerSupplyController;
import org.csstudio.mps.sns.tools.data.PowerSupplyInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Holds the functionality for the power supply interface.
 * 
 * @author Chris Fowlkes
 */
public class PowerSupplyFunctions extends JeriDataModule 
{
  /**
   * Holds the <CODE>PreparedStatement used to load the data.
   */
  private PreparedStatement query;
  /**
   * Holds the data currently being displayed in the edit panel.
   */
  private ResultSet data;
  /**
   * Flag used to determine if a commit is needed.
   */
  private boolean commitNeeded = false;
  /**
   * Holds the column display names for the tables so that they do not have to 
   * be reloaded each time.
   */
  private HashMap columnDisplayNames = new HashMap();

  /**
   * Creates a new <CODE>PowerSupplyFunctions</CODE>.
   */
  public PowerSupplyFunctions()
  {
  }

  /**
   * Exposing the setConnection method.
   * 
   * @param oracleConnection The <CODE>Connection</CODE> to use to connect to the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void setConnection(Connection oracleConnection) throws SQLException
  {
    super.setConnection(oracleConnection);
    oracleConnection.setAutoCommit(false);
  }
  
  /**
   * Loads the power supply data from the database.
   * 
   * @return The power supply data.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public IOC[] loadPowerSupplyData() throws java.sql.SQLException
  {
    try
    {
      setMessage("Loading Power Supply Data");
      setProgressIndeterminate(true);
//SELECT   ioc_dvc.dvc_id AS ioc_dvc_id, psc_dvc.dvc_id AS psc_dvc_id,
//         psi_dvc.dvc_id AS psi_dvc_id, ps_dvc.dvc_id AS ps_dvc_id,
//         mag_dvc.dvc_id AS mag_dvc_id
//    FROM epics.ioc_dvc,
//         epics.psc_dvc,
//         epics.psi_dvc,
//         epics.ps_dvc,
//         epics.dvc,
//         (SELECT dvc.dvc_id
//            FROM EPICS.dvc, EPICS.mag_dvc
//           WHERE dvc.dvc_id = mag_dvc.dvc_id) mag_dvc
//   WHERE ioc_dvc.dvc_id = psc_dvc.ioc_dvc_id
//     AND psc_dvc.dvc_id = psi_dvc.psc_dvc_id(+)
//     AND psi_dvc.dvc_id = ps_dvc.psi_dvc_id(+)
//     AND ps_dvc.dvc_id = dvc.parent_dvc_id(+)
//       AND dvc.dvc_id = mag_dvc.dvc_id (+)
//ORDER BY ioc_dvc.dvc_id,
//         psc_dvc.dvc_id,
//         psi_dvc.dvc_id,
//         ps_dvc.dvc_id,
//         dvc.dvc_id
      StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
      StringBuffer whereClause = new StringBuffer(MPSBrowserView.SCHEMA);
      whereClause.append(".IOC_DVC, ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".PSC_DVC, ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".PSI_DVC, ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".PS_DVC, ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".DVC, (SELECT DVC.DVC_ID FROM ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".DVC, ");
      whereClause.append(MPSBrowserView.SCHEMA);
      whereClause.append(".MAG_DVC WHERE DVC.DVC_ID = MAG_DVC.DVC_ID) MAG_DVC WHERE IOC_DVC.DVC_ID = PSC_DVC.IOC_DVC_ID AND PSC_DVC.DVC_ID = PSI_DVC.PSC_DVC_ID (+) AND PSI_DVC.DVC_ID = PS_DVC.PSI_DVC_ID (+) AND PS_DVC.DVC_ID = DVC.PARENT_DVC_ID(+) AND DVC.DVC_ID = MAG_DVC.DVC_ID (+)");
      sql.append(whereClause);
      Connection oracleConnection = getConnection();
      int expectedCount;
      PreparedStatement countQuery = oracleConnection.prepareStatement(sql.toString());
      try
      {
        expectedCount = runCountQuery(countQuery);
        setProgressMaximum(expectedCount);
      }
      finally
      {
        countQuery.close();
      }
      sql = new StringBuffer("SELECT IOC_DVC.DVC_ID AS IOC_DVC_ID, PSC_DVC.DVC_ID AS PSC_DVC_ID, PSI_DVC.DVC_ID AS PSI_DVC_ID, PS_DVC.DVC_ID AS PS_DVC_ID, MAG_DVC.DVC_ID AS MAG_DVC_ID FROM ");
      sql.append(whereClause);
      sql.append(" ORDER BY IOC_DVC.DVC_ID, PSC_DVC.DVC_ID, PSI_DVC.DVC_ID, PS_DVC.DVC_ID, DVC.DVC_ID");
      PreparedStatement powerSupplyQuery = oracleConnection.prepareStatement(sql.toString());
      try
      {
        ResultSet powerSupplyResults = powerSupplyQuery.executeQuery();
        try
        {
          ArrayList powerSupplies = new ArrayList();
          IOC ioc = null;
          String iocID = null;
          PowerSupplyController psc = null;
          String pscID = null;
          PowerSupplyInterface psi = null;
          String psiID = null;
          PowerSupply ps = null;
          String psID = null;
          int progress = 0;
          setProgressValue(0);
          setProgressIndeterminate(false);
          while(powerSupplyResults.next())
          {
            String newID = powerSupplyResults.getString("IOC_DVC_ID");
            if(iocID == null || ! iocID.equals(newID))
            {
              ioc = new IOC(newID);
              iocID = newID;
              powerSupplies.add(ioc);
            }
            newID = powerSupplyResults.getString("PSC_DVC_ID");
            if(newID != null)
            {
              if(pscID == null || ! pscID.equals(newID))
              {
                psc = new PowerSupplyController(newID);
                pscID = newID;
                ioc.addPowerSupplyController(psc);
              }
              newID = powerSupplyResults.getString("PSI_DVC_ID");
              if(newID != null)
              {
                if(psiID == null || ! psiID.equals(newID))
                {
                  psi = new PowerSupplyInterface(newID);
                  psiID = newID;
                  psc.addPowerSupplyInterface(psi);
                }
                newID = powerSupplyResults.getString("PS_DVC_ID");
                if(newID != null)
                {
                  if(psID == null || ! psID.equals(newID))
                  {
                    ps = new PowerSupply(newID);
                    psID = newID;
                    psi.addPowerSupply(ps);
                  }
                  newID = powerSupplyResults.getString("MAG_DVC_ID");
                  if(newID != null)
                    ps.addMagnet(new Magnet(newID));
                }
              }
            }
            setProgressValue(++progress);
          }
          return (IOC[])powerSupplies.toArray(new IOC[powerSupplies.size()]);
        }
        finally
        {
          powerSupplyResults.close();
        }
      }
      finally
      {
        powerSupplyQuery.close();
      }
    }
    finally
    {
      clearProgress();
    }
  }
  
  /**
   * Closes the <CODE>Connection</CODE> to the database held by this class.
   * 
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void closeConnection() throws java.sql.SQLException
  {
    getConnection().close();
  }

  /**
   * Loads the data for the selected node in the tree.
   * 
   * @param selectedObject The <CODE>Device</CODE> that is selected in the tree.
   * @return The <CODE>ResultSet</CODE> containing the data for the node.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public ResultSet loadData(Device selectedObject) throws java.sql.SQLException
  {
    try
    {
      setProgressIndeterminate(true);
      String tableName = determineTableName(selectedObject);
      StringBuffer sql = new StringBuffer("SELECT ");
      sql.append(tableName);
      sql.append(".* FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".");
      sql.append(tableName);
      sql.append(" WHERE DVC_ID = ?");
      if(query != null)
      {
        if(data != null)
          data.close();
        query.close();
      }
      String sqlText = sql.toString();
      query = getConnection().prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
      query.setString(1, selectedObject.getID());
      data = query.executeQuery();
      return data;
    }
    finally
    {
      clearProgress();
    }
  }

  /**
   * Determines the name of the table that deals with the given 
   * <CODE>Object</CODE>.
   * 
   * @param selectedObject The selected <CODE>Object</CODE>.
   * @return The name of the table that contains the given <CODE>Object</CODE>.
   */
  private String determineTableName(Object selectedObject)
  {
    String tableName;
    if(selectedObject instanceof IOC)
      tableName = "IOC_DVC";
    else
      if(selectedObject instanceof PowerSupplyController)
        tableName = "PSC_DVC";
      else
        if(selectedObject instanceof PowerSupplyInterface)
          tableName = "PSI_DVC";
        else
          if(selectedObject instanceof PowerSupply)
            tableName = "PS_DVC";
          else
            tableName = "MAG_DVC";
    return tableName;
  }
  
  /**
   * Commits the changes made to the data.
   * 
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void commit() throws java.sql.SQLException
  {
    try
    {
      setProgressIndeterminate(true);
      setMessage("Commiting Changes");
      getConnection().commit();
      commitNeeded = false;
    }
    finally
    {
      clearProgress();
    }
  }

  /**
   * Performs a rollback to cancel any changes.
   * 
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void rollback() throws java.sql.SQLException
  {
    try
    {
      setProgressIndeterminate(true);
      setMessage("Canceling Posted Changes");
      getConnection().rollback();
      commitNeeded = false;
    }
    finally
    {
      clearProgress();
    }
  }
  
  /**
   * Posts the changes made to the <CODE>ResultSet</CODE>.
   * 
   * @throws java.sql.SQLException
   */
  public void post() throws java.sql.SQLException
  {
    try
    {
      setProgressIndeterminate(true);
      setMessage("Posting Changes");
      data.updateRow();
      commitNeeded = true;
    }
    finally
    {
      clearProgress();
    }
  }
  
  /**
   * Cancels any changes made to the data.
   * 
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void cancel() throws java.sql.SQLException
  {
    try
    {
      setProgressIndeterminate(true);
      setMessage("Posting Unposted Changes");
      data.cancelRowUpdates();
    }
    finally
    {
      clearProgress();
    }
  }
  
  /**
   * Determines if changes requiring a commit are pending.
   * 
   * @return <CODE>true</CODE> if changes are pending, <CODE>false</CODE> otherwise.
   */
  public boolean isCommitNeeded()
  {
    return commitNeeded;
  }

  /**
   * Loads the column display names.
   * 
   * @return The user friendly names for the columns.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public HashMap loadColumnDisplayNames(Object selectedObject) throws java.sql.SQLException
  {
    String tableName = determineTableName(selectedObject);
    HashMap displayNames = (HashMap)columnDisplayNames.get(tableName);
    if(displayNames == null)
      try
      {
        setProgressIndeterminate(true);
        setMessage("Loading Column Display Names");
        Connection oracleConnection = getConnection();
        PreparedStatement countQuery = oracleConnection.prepareStatement("SELECT COUNT(*) FROM ALL_COL_COMMENTS WHERE OWNER = ? AND TABLE_NAME = ?");
        try
        {
          countQuery.setString(1, MPSBrowserView.SCHEMA);
          countQuery.setString(2, tableName);
          ResultSet result = countQuery.executeQuery();
          try
          {
            result.next();
            setProgressMaximum(result.getInt(1));
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
        PreparedStatement query = oracleConnection.prepareStatement("SELECT COLUMN_NAME, COMMENTS FROM ALL_COL_COMMENTS WHERE OWNER = ? AND TABLE_NAME = ?");
        try
        {
          query.setString(1, MPSBrowserView.SCHEMA);
          query.setString(2, tableName);
          ResultSet result = query.executeQuery();
          try
          {
            displayNames = new HashMap();
            int progress = 0;
            setProgressValue(0);
            setProgressIndeterminate(false);
            while(result.next())
            {
              String currentDisplayName = result.getString("COMMENTS");
              String currentColumnName = result.getString("COLUMN_NAME");
              if(currentDisplayName == null || currentDisplayName.indexOf(':') < 0)
                displayNames.put(currentColumnName, currentColumnName);
              else
              {
                currentDisplayName = currentDisplayName.split(":")[0];
                displayNames.put(currentColumnName, currentDisplayName);
              }//else           
              setProgressValue(++progress);
            }//while(result.next())
            columnDisplayNames.put(tableName, displayNames);
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
    return displayNames;
  }
}