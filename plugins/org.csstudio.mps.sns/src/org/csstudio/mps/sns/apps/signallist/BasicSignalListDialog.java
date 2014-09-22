package org.csstudio.mps.sns.apps.signallist;

import org.csstudio.mps.sns.tools.swing.AbstractSignalTableModel;
import java.awt.*;

import java.math.*;

import java.sql.*;

import java.util.*;

import javax.swing.*;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.tools.data.Signal;
import org.csstudio.mps.sns.tools.data.SignalType;
import org.csstudio.mps.sns.tools.data.EpicsRecordType;

/**
 * Provides a dialog for displaying the archive information associated with 
 * instances of <CODE>Signal</CODE>.
 * 
 * @author Chris Fowlkes
 */
public class BasicSignalListDialog extends SignalListDialog 
{
  /**
   * Flag used to determine of the data load thread is active or not.
   */
  private boolean dataLoading = false;
  /**
   * Flag used by the data load <CODE>Thread</CODE> to determine if the dialog 
   * has been canceled.
   */
  private boolean canceled = false;

  /**
   * Creates a new <CODE>BasicSignalListDialog</CODE>.
   */
  public BasicSignalListDialog()
  {
  }

  /**
   * Creates a new <CODE>BasicSignalListDialog</CODE> with the given parent, 
   * title, and modality.
   * 
   * @param parent The parent window for the dialog.
   * @param title The title to appear in the title bar of the dialog.
   * @param modal Pass as <CODE>true</CODE> for a modal dialog, <CODE>false</CODE> otherwise.
   */
  public BasicSignalListDialog(Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
  }

  /**
   * Reloads the data in the dialog. This method launches a <CODE>Thread</CODE>
   * to do the data load. This means that when the method returns, the data is 
   * not yet in the model.
   */
  public void reload()
  {
    Thread dataLoadThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(this)
        {
          try
          {
            dataLoading = true;
            canceled = false;
            enableButtons();
            setProgressIndeterminate(true);
            setMessage("Loading Signal Data...");
            int progress = 0;
            Connection oracleConnection = getDataSource().getConnection();
            try
            {
              Statement query = oracleConnection.createStatement();
              try
              {
                StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
                StringBuffer whereClause = new StringBuffer(MPSBrowserView.SCHEMA);
                whereClause.append(".SGNL_REC");
                if(isFiltered())
                {
                  whereClause.append(" WHERE ");
                  whereClause.append(getFilter());
                }//if(isFiltered())
                sql.append(whereClause);
                ResultSet result = query.executeQuery(sql.toString());
                try
                {
                  result.next();
                  int signalCount = result.getInt(1);
                  setProgressMaximum(signalCount);
                  sql = new StringBuffer("SELECT SGNL_ID, REC_TYPE_ID FROM ");
                  sql.append(whereClause);
                  sql.append(" ORDER BY SGNL_ID");
                  result = query.executeQuery(sql.toString());
                  AbstractSignalTableModel signalListModel = getSignalListModel();
                  signalListModel.clear();
                  ArrayList signals = new ArrayList(signalCount);
                  setProgressValue(0);
                  setProgressIndeterminate(false);
                  while(result.next())
                  {
                    String currentSignalID = result.getString("SGNL_ID");
                    Signal currentSignal = new Signal(currentSignalID);
                    String currentRecordTypeID = result.getString("REC_TYPE_ID");
                    EpicsRecordType currentRecordType = new EpicsRecordType(currentRecordTypeID);
                    SignalType currentSignalType = new SignalType();
                    currentSignalType.setRecordType(currentRecordType);
                    currentSignal.setType(currentSignalType);
                    signalListModel.addSignal(currentSignal);
                    setProgressValue(++progress);
                    if(canceled)
                      break;
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
            }//try
            finally
            {
              setMessage("Row Count: " + progress);
              oracleConnection.close();
            }//finally
          }//try
          catch(java.sql.SQLException ex)
          {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(BasicSignalListDialog.this, ex.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
          }//catch(java.sql.SQLException ex)
          finally
          {
            dataLoading = false;
            canceled = false;
            setProgressValue(0);
            setProgressIndeterminate(false);
            enableButtons();
          }//finally
        }//synchronized(this)
      }
    });
    dataLoadThread.start();
  }

  /**
   * Returns <CODE>true</CODE> if the data is being loaded.
   * 
   * @return <CODE>true</CODE> if the data is currently being loaded, <CODE>false</CODE> if it is not being loaded.
   */
  public boolean isDataLoading()
  {
    return dataLoading;
  }

  /**
   * Cancels the current data load operation.
   */
  protected void cancelDataLoad()
  {
    canceled = true;
  }
}