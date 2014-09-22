package org.csstudio.mps.sns.tools.swing;
import java.sql.SQLException;
import java.util.*;
import java.sql.*;
import org.csstudio.mps.sns.tools.database.swing.DatabaseEditTableModel;
import org.csstudio.mps.sns.view.MPSBrowserView;

public class DeviceViewTableModel extends DatabaseEditTableModel 
{
  public DeviceViewTableModel()
  {
    setSchema(MPSBrowserView.SCHEMA);
  }

  public void delete(int[] rowNumbers) throws SQLException
  {
    Arrays.sort(rowNumbers);
    String viewName = getTableName();
    StringBuffer sql = new StringBuffer("{call ");
    sql.append(MPSBrowserView.SCHEMA);
    sql.append(".EPICS_PKG.DVC_DEL(?, ?)}");
    CallableStatement deleteProcedure = super.getConnection().prepareCall(sql.toString());
    int rowCount = getRowCount();
    try
    {
      for(int i=rowNumbers.length-1;i>=0;i--)
      {
        if(rowNumbers[i] == rowCount && isInsertRowVisible())
          super.delete(new int[]{rowNumbers[i]});//Deletes the insert row.
        else
        {
          Object value = getValueAt(rowNumbers[i], 0);
          if(value != null)
          {
            deleteProcedure.setString(1, viewName);
            deleteProcedure.setString(2, value.toString().trim());
            deleteProcedure.execute();
            setCommitNeeded(true);
          }//if(value != null)
        }//else
      }//for(int i=rowNumbers.length-1;i>=0;i--)
    }//try
    finally
    {
      deleteProcedure.close();
    }
    refresh();
  }
}