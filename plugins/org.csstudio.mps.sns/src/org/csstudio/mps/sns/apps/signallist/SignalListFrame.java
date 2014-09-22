package org.csstudio.mps.sns.apps.signallist;

import org.csstudio.mps.sns.MainFrame;
import org.csstudio.mps.sns.apps.fileeditor.FileEditDialog;
import org.csstudio.mps.sns.sql.SelectStatement;
import org.csstudio.mps.sns.sql.TableJoin;
import org.csstudio.mps.sns.tools.swing.BasicSignalTableModel;
import java.awt.*;
import java.awt.event.*;

import java.beans.PropertyChangeEvent;

import java.sql.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import oracle.sql.*;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.apps.filter.FilterFrame;
import org.csstudio.mps.sns.tools.data.Signal;
import org.csstudio.mps.sns.tools.data.SignalType;
import org.csstudio.mps.sns.tools.data.EpicsRecordType;
import org.csstudio.mps.sns.application.JeriInternalFrame;

/**
 * Provides an interface for displaying instances of <CODE>Signal</CODE> in a 
 * <CODE>JInternalFrame</CODE>. The interface shows the results of the signal
 * impact rename stored procedure for the <CODE>Signal</CODE> selected in a
 * dialog when the user selectes a <CODE>Signal</CODE>, but could be changed to 
 * do other things.
 * 
 * @author Chris Fowlkes
 */
public class SignalListFrame extends JeriInternalFrame 
{
  /**
   * Holds the icon that denotes there is a filter applied.
   */
  private Icon editFilterIcon;
  /**
   * Holds the icon that denotes there is no filter currently applied.
   */
  private Icon newFilterIcon;
  private String filter;
  private JScrollPane scrollPane = new JScrollPane();
  private JPanel outerButtonPanel = new JPanel();
  private JPanel innerButtonPanel = new JPanel();
  private JButton okButton = new JButton();
  private JButton closeButton = new JButton();
  private JTable table = new JTable();
  private BorderLayout outerButtonPanelLayout = new BorderLayout();
  private GridLayout innerButtonPanelLayout = new GridLayout();
  private JToolBar toolBar = new JToolBar();
  private JButton newFilterButton = new JButton();
  private JButton removeFilterButton = new JButton();
  private JButton refreshButton = new JButton();
  private JPanel statusBarPanel = new JPanel();
  private BorderLayout statusBarPanelLayout = new BorderLayout();
  private JLabel progressLabel = new JLabel();
  private JProgressBar progressBar = new JProgressBar();
  /**
   * Flag used to determine of the data load thread is active or not.
   */
  private boolean dataLoading = false;
  /**
   * Flag used by the data load <CODE>Thread</CODE> to determine if the dialog 
   * has been canceled.
   */
  private boolean canceled = false;
  private BasicSignalTableModel tableModel = new BasicSignalTableModel();
  /**
   * Holds the dialog used to apply filters to the data.
   */
  private FilterFrame filterDialog;
  /**
   * Holds the icon for the print button.
   */
  private Icon printIcon;

  /**
   * Creates a new <CODE>SignalListFrame</CODE>.
   */
  public SignalListFrame()
  {
    try
    {
      jbInit();
      table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          enableButtons();
        }
      });
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

  }

  /**
   * Component initialization.
   * 
   * @throws java.lang.Exception Thrown on initialization error.
   */
  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(400, 300));
    this.setTitle("Select a Signal for the Report");
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    innerButtonPanel.setLayout(innerButtonPanelLayout);
    innerButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 5));
    okButton.setText("Report...");
    okButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          okButton_actionPerformed(e);
        }
      });
    closeButton.setText("Close");
    closeButton.setMnemonic('C');
    closeButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          closeButton_actionPerformed(e);
        }
      });
    table.setModel(tableModel);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    outerButtonPanelLayout.setVgap(5);
    innerButtonPanelLayout.setHgap(5);
    newFilterButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          newFilterButton_actionPerformed(e);
        }
      });
    removeFilterButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          removeFilterButton_actionPerformed(e);
        }
      });
    refreshButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          refreshButton_actionPerformed(e);
        }
      });
    statusBarPanel.setLayout(statusBarPanelLayout);
    progressLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    progressLabel.setText(" ");
    progressBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    tableModel.addTableModelListener(new TableModelListener()
      {
        public void tableChanged(TableModelEvent e)
        {
          tableModel_tableChanged(e);
        }
      });
    scrollPane.getViewport().add(table, null);
    statusBarPanel.add(progressLabel, BorderLayout.CENTER);
    statusBarPanel.add(progressBar, BorderLayout.EAST);
    this.getContentPane().add(scrollPane, BorderLayout.CENTER);
    innerButtonPanel.add(okButton, null);
    innerButtonPanel.add(closeButton, null);
    outerButtonPanel.add(innerButtonPanel, BorderLayout.EAST);
    outerButtonPanel.add(statusBarPanel, BorderLayout.SOUTH);
    this.getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);
    toolBar.add(newFilterButton, null);
    toolBar.add(removeFilterButton, null);
    toolBar.add(refreshButton, null);
    this.getContentPane().add(toolBar, BorderLayout.NORTH);
  }

  /**
   * Called when the cancel button is clicked. This method closes the dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void closeButton_actionPerformed(ActionEvent e)
  {
    if(dataLoading)
      canceled = true;
    else
    {
      setVisible(false);
      fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSED);
    }
  }

  /**
   * Called when the new filter button is clicked. This method allows the user
   * to filter the data shown on the list.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void newFilterButton_actionPerformed(ActionEvent e)
  {
    try
    {
      filter();
      enableButtons();
    }//try
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }//catch(java.sql.SQLException ex)
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
                  tableModel.clear();
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
                    tableModel.addSignal(currentSignal);
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
            JOptionPane.showMessageDialog(SignalListFrame.this, ex.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
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
   * Filters the data. This method shows the <CODE>FilterFrame</CODE> and 
   * applies the resulting filter to the data and reloads.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  protected void filter() throws java.sql.SQLException
  {
    Properties settings = getApplicationProperties();
    if(filterDialog == null)
    {
      filterDialog = new FilterFrame(getMainWindow(), "Filter Data", true);
      filterDialog.center();
      filterDialog.setDataSource(getDataSource());
      filterDialog.setTableName("SGNL_REC");
      filterDialog.clearBuilderTab();
      TableJoin signalDeviceJoin = new TableJoin("SGNL_REC", "DVC");
      signalDeviceJoin.addCriteria("DVC_ID", "DVC_ID");
      filterDialog.addBuilderTable(signalDeviceJoin);
      filterDialog.addBuilderColumn("SYS_ID", "DVC", "System");
      filterDialog.addBuilderColumn("SUBSYS_ID", "DVC", "Subsystem");
      filterDialog.addBuilderColumn("DVC_TYPE_ID", "DVC", "Device Type");
      filterDialog.addBuilderColumn("SGNL_NM", "SGNL_REC", "Signal Name");
      filterDialog.addBuilderColumn("REC_TYPE_ID", "SGNL_REC", "Record Type");
      String currentFilter = settings.getProperty("SignalListFrame.savedFilter");
      String filterType;
      if(currentFilter == null)//Check for old format.
      {
        currentFilter = settings.getProperty("SignalListFrame.filter");
        filterType = "E";
      }
      else
        filterType = settings.getProperty("SignalListFrame.savedFilterType");
      if(currentFilter != null)
        if(filterType.equals("E"))
          filterDialog.restoreEditorFilter(currentFilter);
        else
          filterDialog.restoreBuilderFilter(currentFilter);
    }//if(filterDialog == null)
    //Show the dialog.
    filterDialog.setVisible(true);
    if(filterDialog.getResult() == FilterFrame.OK)
    {
      String saveableFilter = filterDialog.getSaveableFilter();
      settings.setProperty("SignalListFrame.savedFilter", saveableFilter);
      if(filterDialog.getMode() == FilterFrame.EDITOR_MODE)
        settings.setProperty("SignalListFrame.savedFilterType", "E");
      else
        settings.setProperty("SignalListFrame.savedFilterType", "B");
      SelectStatement signalStatement = filterDialog.getFilter();
      StringBuffer signalFilter = new StringBuffer(" SGNL_ID IN ( ");
      signalFilter.append(signalStatement);
      signalFilter.append(" ) ");
      String newFilter = signalFilter.toString();
      settings.setProperty("SignalListFrame.filterWhere", signalFilter.toString());
      settings.setProperty("SignalListFrame.filtered", Boolean.TRUE.toString());
      boolean signalFilterApplied = signalFilter != null && ! newFilter.trim().equals("");
      settings.setProperty("SignalListFrame.savedFilter", newFilter);
      settings.setProperty("SignalListFrame.signalFiltered", String.valueOf(signalFilterApplied));
      signalStatement.addColumn("SGNL_REC", "SGNL_ID");
      setFilter(newFilter);
      reload();
    }
  }

  /**
   * Called when the remove filter button is clicked. This method allows the 
   * user to remove the filter that is currently applied to the data.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void removeFilterButton_actionPerformed(ActionEvent e)
  {
    try
    {
      removeFilter();
      enableButtons();
    }//try
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }//catch(java.sql.SQLException ex)
  }

  /**
   * Removes the filter that is currently applied.
   * 
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  protected void removeFilter() throws java.sql.SQLException
  {
    setFilter("");
    reload();
    getApplicationProperties().setProperty("SignalListFrame.filtered", Boolean.FALSE.toString());
  }

  /**
   * Called when the refresh button is clicked. This method reloads the data in 
   * the list.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void refreshButton_actionPerformed(ActionEvent e)
  {
    reload();
  }

  /**
   * Called when the ok button is clicked. This method shows the report 
   * generated by the sgnl_rename_impact stored procedure in a dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void okButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      int[] selectedRows = table.getSelectedRows();
      Signal[] newSignals = new Signal[selectedRows.length];
      for(int i=0;i<selectedRows.length;i++)
        newSignals[i] = tableModel.getSignalAt(selectedRows[i]);
      String reportText = createSignalRenameImpactReport(newSignals);
      MainFrame mainWindow = getMainWindow();
      FileEditDialog reportDialog = new FileEditDialog(mainWindow, "Signal Rename Impact", true);
      reportDialog.setApplicationProperties(getApplicationProperties());
      reportDialog.setEditable(false);
      reportDialog.setText(reportText);
      reportDialog.setPrintIcon(printIcon);
      reportDialog.center();
      reportDialog.setVisible(true);
//      HashMap report = new HashMap();
//      report.put("Signal Rename Impact Report", reportText);
//      MPSFileEditDialog reportDialog = new MPSFileEditDialog();
//      reportDialog = new MPSFileEditDialog(getMainWindow(), "Reports", true);
//      reportDialog.setPrintIcon(getPrintIcon());
//      reportDialog.setApplicationProperties(getApplicationProperties());
//      reportDialog.setEditable(false);
//      reportDialog.center();
//      reportDialog.setFiles(report);
//      reportDialog.show();
    }//try
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }//catch(java.sql.SQLException ex)
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }//finally
  }

  /**
   * Calls the sgnl_rename_impact stored procedure.
   * 
   * @param signals Contains the signal IDs to send to the stored procedure.
   * @return The text returned by the stored procedure.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  private String createSignalRenameImpactReport(Signal[] signals) throws java.sql.SQLException
  {
    //Need to call stored procedure to do the update. Procedure defined as 
    //follows: function rename_sgnl (sgnl_tab in renamed_sgnl_tab, p_delete in 
    //char, p_commit in char, p_data in char) return varchar2;
    StringBuffer sql = new StringBuffer("{? = call ");
    sql.append(MPSBrowserView.SCHEMA);
    sql.append(".EPICS_PKG.sgnl_rename_impact(?)}");
    Connection oracleConnection = getDataSource().getConnection();
    try
    {
      CallableStatement rdbFunction = oracleConnection.prepareCall(sql.toString());
      try
      {
        rdbFunction.registerOutParameter(1, Types.VARCHAR);
        //Declare and initialize the Input array object - to an array of STRUCT Objects.
        Object[] p1arrobj = new Object[signals.length];
        for(int i=0;i<signals.length;i++)
        {
          //First, declare the Object arrays that will store the data.
          Object[] p1recobj = new Object[]{null, signals[i].getID()};
          StructDescriptor desc1 = StructDescriptor.createDescriptor(MPSBrowserView.SCHEMA + ".RENAMED_SGNL_TYPE", oracleConnection);// RECTYPE is our RENAMED_SGNL_TYPE
          //Create the STRUCT objects to associate the host objects
          //with the database records.
          STRUCT p1struct = new STRUCT(desc1, oracleConnection, p1recobj);
          p1arrobj[i] = p1struct;
        }//for(int i=0;i<signals.length;i++)
        //Set up the ARRAY object.
        ArrayDescriptor desc2 = ArrayDescriptor.createDescriptor(MPSBrowserView.SCHEMA + ".RENAMED_SGNL_TAB", oracleConnection);//RECTAB is our RENAMED_SGNL_TAB
        ARRAY p1arr = new ARRAY(desc2, oracleConnection, p1arrobj);
        //The first parameter is in out so we have to use setARRAY to
        //pass it to the statement 
        ((oracle.jdbc.OracleCallableStatement)rdbFunction).setARRAY(2, p1arr);
        rdbFunction.execute();
        return rdbFunction.getString(1);
      }//try
      finally
      {
        rdbFunction.close();
      }//finally
    }//try
    finally
    {
      oracleConnection.close();
    }//finally
  }

  /**
   * Enables or disables the buttons on the interface depending on the state.
   */
  protected void enableButtons()
  {
    okButton.setEnabled(table.getSelectedRowCount() > 0);
    if(dataLoading)
      closeButton.setText("Cancel");
    else
      closeButton.setText("Close");
    String filter = getFilter();
    boolean filtered = !(filter == null || filter.trim().equals(""));
    newFilterButton.setEnabled(! dataLoading);
    if(filtered)
    {
      newFilterButton.setIcon(editFilterIcon);
      newFilterButton.setToolTipText("Edit Filter");
    }//if(filtered)
    else
    {
      newFilterButton.setIcon(newFilterIcon);
      newFilterButton.setToolTipText("New Filter");
    }//else
    removeFilterButton.setEnabled(! dataLoading && filtered);
    refreshButton.setEnabled(! dataLoading);
    if(dataLoading)
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    else
      setCursor(Cursor.getDefaultCursor());
  }

  /**
   * Uses <CODE>SwingUtilities.invokeLater</CODE> to set the indeterminate 
   * property of the progress bar.
   * 
   * @param indeterminate The new value of the indeterminate property of the progress bar.
   */
  protected void setProgressIndeterminate(final boolean indeterminate)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        progressBar.setIndeterminate(indeterminate);
      }
    });
  }
  
  /**
   * Uses <CODE>SwingUtilities.invokeLater</CODE> to safely set the value of the 
   * progress bar from a <CODE>Thread</CODE>.
   * 
   * @param progressValue The value to pass to the <CODE>setValue</CODE> method of the progress bar.
   */
  protected void setProgressValue(final int progressValue)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        progressBar.setValue(progressValue);
      }
    });
  }

  /**
   * Uses <CODE>SwingUtilities.invokeLater</CODE> to safely set the maximum 
   * value of the progress bar from a <CODE>Thread</CODE>.
   * 
   * @param progressMaximum The value to pass to the <CODE>setMaximum</CODE> method of the progress bar.
   */
  protected void setProgressMaximum(final int progressMaximum)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        progressBar.setMaximum(progressMaximum);
      }
    });
  }

  /**
   * Uses <CODE>SwingUtilities.invokeLater</CODE> to safely set the text of the 
   * label in the status bar from a <CODE>Thread</CODE>.
   * 
   * @param message The value to pass to the <CODE>setText</CODE> method of the label.
   */
  protected void setMessage(final String message)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
         progressLabel.setText(message);
      }
    });
  }

  /**
   * Sets the image to use on the filter button to denote that a filter has been
   * applied.
   * 
   * @param editFilterIcon The <CODE>Icon</CODE> for the add filter toolbar button.
   */
  public void setEditFilterIcon(Icon editFilterIcon)
  {
    this.editFilterIcon = editFilterIcon;
    if(isFiltered())
      newFilterButton.setIcon(editFilterIcon);
  }

  /**
   * Sets the image to use on the filter button to denote that a filter has not 
   * been applied.
   * 
   * @param newFilterIcon The <CODE>Icon</CODE> for the add filter toolbar button.
   */
  public void setNewFilterIcon(Icon newFilterIcon)
  {
    this.newFilterIcon = newFilterIcon;
    if(! isFiltered())
      newFilterButton.setIcon(newFilterIcon);
  }

  /**
   * Puts an image on the remove filter button.
   * 
   * @param removeFilterIcon The <CODE>Icon</CODE> for the remove filter toolbar button.
   */
  public void setRemoveFilterIcon(Icon removeFilterIcon)
  {
    removeFilterButton.setIcon(removeFilterIcon);
  }

  /**
   * Puts an image on the refresh button.
   * 
   * @param refreshIcon The <CODE>Icon</CODE> for the refresh toolbar button.
   */
  public void setRefreshIcon(Icon refreshIcon)
  {
    refreshButton.setIcon(refreshIcon);
  }

  /**
   * Returns <CODE>true</CODE> if the data is filtered. The data can be filtered 
   * by the user with either the filter dialog or by the user selecting a group.
   * 
   * @return <CODE>true</CODE> if the user is not seeing all of the records in the sgnl_rec table.
   */
  public boolean isFiltered()
  {
    String filter = getFilter();
    return filter != null && ! filter.trim().equals("");
  }

  /**
   * Sets the filter used retrieve the data.
   * 
   * @param filter The filter to use when retrieving records.
   */
  public void setFilter(String filter)
  {
    this.filter = filter;
  }
  
  /**
   * Gets the filter used to load the data. 
   * 
   * @return The filter used to select the signals in the table.
   */
  public String getFilter()
  {
    return filter;
  }

  /**
   * Adds listeners to the table and sets up the column positions and widths.
   */
  protected void structureChanged()
  {
    restoreColumnWidths();
    arrangeColumns();
    int columnCount = table.getColumnCount();
    TableColumnModel allColumns = table.getColumnModel();
    for(int i=0;i<columnCount;i++)
    {
      StringBuffer property = new StringBuffer("SignalListFrame.");
      property.append(table.getColumnName(i));
      property.append(".width");
      final String propertyName = property.toString();
      allColumns.getColumn(i).addPropertyChangeListener(new java.beans.PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent e)
        {
          if(e.getPropertyName().equals("preferredWidth"))
            getApplicationProperties().setProperty(propertyName, e.getNewValue().toString());
        }
      });
    }//for(int i=0;i<columnCount;i++)
    //Need to save column positions when moved.
    allColumns.addColumnModelListener(new TableColumnModelListener()
    {
      public void columnAdded(TableColumnModelEvent e)
      {
      }

      public void columnMarginChanged(ChangeEvent e)
      {
      }

      public void columnMoved(final TableColumnModelEvent e)
      {
        saveColumnPositions();
      }

      public void columnRemoved(TableColumnModelEvent e)
      {
      }

      public void columnSelectionChanged(ListSelectionEvent e)
      {
      }
    });
  }

  /**
   * Restores the widths of the columns. The column widths are saved in and 
   * restored from the application properties for each table.
   *
   * @param e The <CODE>TableModelEvent</CODE> that caused the invocation of this method.
   */
  private void tableModel_tableChanged(final TableModelEvent e)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        if(e.getFirstRow() == TableModelEvent.HEADER_ROW)
          structureChanged();
      }
    });
  }

  /**
   * This method restores the column widths to the widths saved in the 
   * applicaiton's properties file.
   */
  private void restoreColumnWidths()
  {
    TableColumnModel allColumns = table.getColumnModel();
    int columnCount = allColumns.getColumnCount();
    TableColumn currentColumn;
    for(int i=0;i<columnCount;i++)
    {
      currentColumn = allColumns.getColumn(i);
      StringBuffer property = new StringBuffer("SignalListFrame.");
      property.append(table.getColumnName(i));
      property.append(".width");
      String propertyName = property.toString();
      String widthProperty = getApplicationProperties().getProperty(propertyName, "100");
      currentColumn.setPreferredWidth(Integer.parseInt(widthProperty));
    }//for(int i=0;i<columnCount;i++)
  }

  /**
   * This method puts the columns in the order the user left them in.
   */
  private void arrangeColumns()
  {
    Properties settings = getApplicationProperties();
    int columnCount = table.getColumnCount(), moveTo = 0;
    int tableColumnCount = table.getColumnCount();
    ArrayList modelColumns = new ArrayList(tableColumnCount);
    TableModel selectedModel = table.getModel();
    for(int i=0;i<tableColumnCount;i++)
      modelColumns.add(selectedModel.getColumnName(i));
    for(int i=0;i<columnCount;i++)
    {
      StringBuffer property = new StringBuffer("SignalListFrame.Column");
      property.append(i);
      String currentColumnName = settings.getProperty(property.toString());
      if(currentColumnName != null)
      {
        int modelIndex = modelColumns.indexOf(currentColumnName);
        if(modelIndex >= 0)//Column is in the model, move it...
        {
          int moveFrom = table.convertColumnIndexToView(modelIndex);//Convert to view index.
          if(moveTo != moveFrom)
            table.moveColumn(moveFrom, moveTo);
          moveTo++;//Next position.
        }//if(modelIndex >= 0)
      }//if(currentColumnName != null)
    }//for(int i=0;i<columnCount;i++)
  }

  /**
   * This method saves the positions of the table's columns in the application's
   * properties file.
   */
  private void saveColumnPositions()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        int columnCount = table.getColumnCount();
        Properties settings = getApplicationProperties();
        for(int position=0;position<columnCount;position++)
        {
          String column = table.getColumnName(position);
          StringBuffer property = new StringBuffer("SignalListFrame.Column");
          property.append(position);
          settings.setProperty(property.toString(), column);
        }//for(int position=0;position<columnCount;position++)
      }
    });
  }

  /**
   * Gets the image for the print button.
   * 
   * @return The <CODE>Icon</CODE> on the print button.
   */
  public Icon getPrintIcon()
  {
    return printIcon;
  }

  /**
   * Puts an image on the print button.
   * 
   * @param printIcon The <CODE>Icon</CODE> for the print toolbar button.
   */
  public void setPrintIcon(Icon printIcon)
  {
    this.printIcon = printIcon;
  }

  /**
   * sets the instance of <CODE>Properties</CODE> that is used to store the 
   * settings for the application.
   * 
   * @param applicationProperties The <CODE>Properties</CODE> that holds the settings for the application.
   */
  public void setApplicationProperties(Properties applicationProperties)
  {
    super.setApplicationProperties(applicationProperties);
    String filtered = applicationProperties.getProperty("SignalListFrame.filtered", Boolean.FALSE.toString());
    if(Boolean.valueOf(filtered).booleanValue())
    {
      String filter = applicationProperties.getProperty("SignalListFrame.filterWhere");
      if(filter == null)
        filter = applicationProperties.getProperty("SignalListFrame.filter");
      setFilter(filter);
    }//if(Boolean.valueOf(filtered).booleanValue())
    structureChanged();
  }
}