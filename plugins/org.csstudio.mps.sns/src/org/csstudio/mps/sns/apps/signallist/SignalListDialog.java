package org.csstudio.mps.sns.apps.signallist;

import org.csstudio.mps.sns.apps.filter.FilterFrame;
import org.csstudio.mps.sns.tools.data.Signal;
import org.csstudio.mps.sns.sql.SelectStatement;
import org.csstudio.mps.sns.sql.TableJoin;
import org.csstudio.mps.sns.tools.swing.AbstractSignalTableModel;
import org.csstudio.mps.sns.application.JeriDialog;

import java.awt.*;
import java.awt.event.*;

import java.beans.PropertyChangeEvent;

import java.sql.*;

import java.util.*;

import javax.sql.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * Provides an interface that allows the user to select instances of 
 * <CODE>Signal</CODE> from a <CODE>JList</CODE>.
 * 
 * @author Chris Fowlkes
 */
public abstract class SignalListDialog extends JeriDialog //implements DragSourceListener, DragGestureListener 
{
  private JToolBar toolBar = new JToolBar();
  private JScrollPane scrollPane = new JScrollPane();
  private JPanel outerButtonPanel = new JPanel();
  private JPanel innerButtonPanel = new JPanel();
  private JButton okButton = new JButton();
  private JButton closeButton = new JButton();
  private JButton newFilterButton = new JButton();
  private JButton removeFilterButton = new JButton();
  private JButton refreshButton = new JButton();
  private BorderLayout outerButtonPanelLayout = new BorderLayout();
  private GridLayout innerButtonPanelLayout = new GridLayout();
  /**
   * Constant that denotes the OK button was clicked.
   */
  public final static int OK = 1;
  /**
   * Constant that denotes the OK button was not clicked.
   */
  public final static int CANCEL = 0;
  /**
   * Holds and indicator to determine if the OK button was used to exit the 
   * dialog.
   */
  private int result = CANCEL;
  /**
   * Holds the <CODE>DataSource</CODE> for the window. This is used to make 
   * database connections.
   */
  private DataSource connectionPool;
  private JPanel statusBarPanel = new JPanel();
  private BorderLayout statusBarPanelLayout = new BorderLayout();
  private JLabel progressLabel = new JLabel();
  private JProgressBar progressBar = new JProgressBar();
  /**
   * Holds the icon that denotes there is a filter applied.
   */
  private Icon editFilterIcon;
  /**
   * Holds the icon that denotes there is no filter currently applied.
   */
  private Icon newFilterIcon;
  /**
   * Holds the filter used to load the data.
   */
  private String filter;
  /**
   * Holds the dialog used to apply filters to the data.
   */
  private FilterFrame filterDialog;
  private JTable signalList = new JTable();
  /**
   * Holds the model for the table. By default this is an instance of 
   * <CODE>ArchiveSignalListTableModel</CODE>.
   */
  private AbstractSignalTableModel signalListModel = new ArchiveSignalListTableModel();
  /**
   * Holds the main <CODE>JFrame</CODE> of the application.
   */
  private Frame mainWindow;
//  /**
//   * Holds the <CODE>DragSource</CODE> for the class. This is what makes the
//   * rows in the <CODE>JList</CODE> draggable.
//   */
//  private DragSource dragSource = DragSource.getDefaultDragSource();

  /**
   * Creates a new <CODE>SignalListDialog</CODE>.
   */
  public SignalListDialog()
  {
    this(null, "", false);
  }

  /**
   * Creates a new <CODE>SignalListDialog</CODE> with the given parent, title,
   * and modality.
   * 
   * @param parent The parent window for the dialog.
   * @param title The title to appear in the title bar of the dialog.
   * @param modal Pass as <CODE>true</CODE> for a modal dialog, <CODE>false</CODE> otherwise.
   */
  public SignalListDialog(Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
    try
    {
      jbInit();
      mainWindow = parent;
      signalList.getSelectionModel().addListSelectionListener(new ListSelectionListener()
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
   * @throws java.lang.Exception Thrown on sql error.
   */
  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(400, 300));
    this.addWindowListener(new java.awt.event.WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          this_windowClosing(e);
        }
      });
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    innerButtonPanel.setLayout(innerButtonPanelLayout);
    innerButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    okButton.setText("OK");
    okButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          okButton_actionPerformed(e);
        }
      });
    closeButton.setText("Close");
    closeButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          closeButton_actionPerformed(e);
        }
      });
    newFilterButton.setToolTipText("New Filter");
    newFilterButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          newFilterButton_actionPerformed(e);
        }
      });
    removeFilterButton.setToolTipText("Remove Filter");
    removeFilterButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          removeFilterButton_actionPerformed(e);
        }
      });
    refreshButton.setToolTipText("Refresh");
    refreshButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          refreshButton_actionPerformed(e);
        }
      });
    innerButtonPanelLayout.setHgap(5);
    statusBarPanel.setLayout(statusBarPanelLayout);
    progressLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    progressLabel.setText("Row Count: 0");
    progressBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    signalList.setModel(signalListModel);
    signalList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    signalListModel.addTableModelListener(new TableModelListener()
      {
        public void tableChanged(TableModelEvent e)
        {
          signalListModel_tableChanged(e);
        }
      });
    statusBarPanel.add(progressLabel, BorderLayout.CENTER);
    statusBarPanel.add(progressBar, BorderLayout.EAST);
    toolBar.add(newFilterButton, null);
    toolBar.add(removeFilterButton, null);
    toolBar.add(refreshButton, null);
    this.getContentPane().add(toolBar, BorderLayout.NORTH);
    scrollPane.getViewport().add(signalList, null);
    this.getContentPane().add(scrollPane, BorderLayout.CENTER);
    innerButtonPanel.add(okButton, null);
    innerButtonPanel.add(closeButton, null);
    outerButtonPanel.add(innerButtonPanel, BorderLayout.EAST);
    outerButtonPanel.add(statusBarPanel, BorderLayout.SOUTH);
    this.getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);
  }

  /**
   * Called when the OK button is clicked. This method closes the dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void okButton_actionPerformed(ActionEvent e)
  {
    result = OK;
    setVisible(false);
  }

  /**
   * Called when the cancel button is clicked. This method closes the dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void closeButton_actionPerformed(ActionEvent e)
  {
    if(isDataLoading())
      cancelDataLoad();
    else
    {
      result = CANCEL;
      setVisible(false);
    }//else
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
   * Filters the data. This method shows the <CODE>FilterFrame</CODE> and 
   * applies the resulting filter to the data and reloads.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  protected void filter() throws SQLException
  {
    Properties settings = getApplicationProperties();
    if(filterDialog == null)
    {
      filterDialog = new FilterFrame(mainWindow, "Filter Data", true);
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
        currentFilter = settings.getProperty("SignalListDialog.filter");
        filterType = "E";
      }
      else
        filterType = settings.getProperty("SignalListDialog.savedFilterType");
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
      settings.setProperty("SignalListDialog.savedFilter", saveableFilter);
      if(filterDialog.getMode() == FilterFrame.EDITOR_MODE)
        settings.setProperty("SignalListDialog.savedFilterType", "E");
      else
        settings.setProperty("SignalListDialog.savedFilterType", "B");
      SelectStatement signalStatement = filterDialog.getFilter();
      signalStatement.addColumn("SGNL_REC", "SGNL_ID");
      StringBuffer signalFilter = new StringBuffer(" SGNL_ID IN ( ");
      signalFilter.append(signalStatement);
      signalFilter.append(" ) ");
      String newFilter = signalFilter.toString();
      settings.setProperty("SignalListDialog.filterWhere", signalFilter.toString());
      settings.setProperty("SignalListDialog.filtered", Boolean.TRUE.toString());
      boolean signalFilterApplied = signalFilter != null && ! newFilter.trim().equals("");
      settings.setProperty("SignalListDialog.savedFilter", newFilter);
      settings.setProperty("SignalListDialog.signalFiltered", String.valueOf(signalFilterApplied));
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
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      removeFilter();
      enableButtons();
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
   * Removes the filter that is currently applied.
   * 
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  protected void removeFilter() throws SQLException
  {
    setFilter("");
    reload();
    signalListModel.fireTableDataChanged();
    getApplicationProperties().setProperty("SignalListDialog.filtered", Boolean.FALSE.toString());
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
   * Abstract method used by this class to determine if a subclass has a data 
   * loading thread running. If the controls on the dialog are diabled.
   * 
   * @return <CODE>true</CODE> if the data is in the process of loading, <CODE>false</CODE> otherwise.
   */
  abstract public boolean isDataLoading();

  /**
   * Reloads the data in the list.
   */
  abstract public void reload();

  /**
   * Cancels the dataload.
   */
  abstract protected void cancelDataLoad();

  /**
   * Enables or disables the buttons on the interface depending on the state.
   */
  protected void enableButtons()
  {
    okButton.setEnabled(signalList.getSelectedRowCount() > 0);
    boolean dataLoading = isDataLoading();
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
  }
  
  /**
   * Gets the <CODE>DataSource</CODE> used by the window to connect to the 
   * database.
   *
   * @return The <CODE>DataSource</CODE> used to connect to the database.
   */
  public DataSource getDataSource()
  {
    return connectionPool;
  }
  
  /**
   * Sets the <CODE>DataSource</CODE> used by the window to connect to the 
   * database.
   *
   * @param connectionPool The <CODE>DataSource</CODE> to use to connect to the database.
   * @throws java.sql.SQLException Not thrown in this method, but can be thrown in subclasses.
   */
  public void setDataSource(DataSource connectionPool) throws java.sql.SQLException
  {
    this.connectionPool = connectionPool;
  }

  /**
   * Returns an <CODE>int</CODE> used to determine which button the dialog was
   * closed with.
   *
   * @return Returns <CODE>COMMIT</CODE> if the commit button was clicked, returns <CODE>CANCEL</CODE> otherwise.
   */
  public int getResult()
  {
    return result;
  }

  /**
   * Called when the window is closed by clicking the X button in the title bar.
   * This method sets the value of the result property to equate closing the 
   * dialog with canceling it.
   *
   * @param e The <CODE>WindowEvent</CODE> that caused the invocation of this method.
   */
  private void this_windowClosing(WindowEvent e)
  {
    result = CANCEL;
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
   * Gets the instances of <CODE>Signal</CODE> selected in the dialog. The only
   * property that has been set for the instances of <CODE>Signal</CODE> 
   * returned is the ID property.
   * 
   * @return The instances of <CODE>Signal</CODE> selected in the dialog.
   */
  public Signal[] getValue()
  {
    int[] selectedRows = signalList.getSelectedRows();
    Signal[] selectedSignals = new Signal[selectedRows.length];
    for(int i=0;i<selectedRows.length;i++)
      selectedSignals[i] = signalListModel.getSignalAt(selectedRows[i]);
    return selectedSignals;
  }

  /**
   * Adds listeners to the table and sets up the column positions and widths.
   */
  protected void structureChanged()
  {
    restoreColumnWidths();
    arrangeColumns();
    int columnCount = signalList.getColumnCount();
    TableColumnModel allColumns = signalList.getColumnModel();
    for(int i=0;i<columnCount;i++)
    {
      StringBuffer property = new StringBuffer("SignalListDialog.");
      property.append(signalList.getColumnName(i));
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
  private void signalListModel_tableChanged(final TableModelEvent e)
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
    TableColumnModel allColumns = signalList.getColumnModel();
    int columnCount = allColumns.getColumnCount();
    TableColumn currentColumn;
    for(int i=0;i<columnCount;i++)
    {
      currentColumn = allColumns.getColumn(i);
      StringBuffer property = new StringBuffer("SignalListDialog.");
      property.append(signalList.getColumnName(i));
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
    int columnCount = signalList.getColumnCount(), moveTo = 0;
    int tableColumnCount = signalList.getColumnCount();
    ArrayList modelColumns = new ArrayList(tableColumnCount);
    TableModel selectedModel = signalList.getModel();
    for(int i=0;i<tableColumnCount;i++)
      modelColumns.add(selectedModel.getColumnName(i));
    for(int i=0;i<columnCount;i++)
    {
      StringBuffer property = new StringBuffer("SignalListDialog.Column");
      property.append(i);
      String currentColumnName = settings.getProperty(property.toString());
      if(currentColumnName != null)
      {
        int modelIndex = modelColumns.indexOf(currentColumnName);
        if(modelIndex >= 0)//Column is in the model, move it...
        {
          int moveFrom = signalList.convertColumnIndexToView(modelIndex);//Convert to view index.
          if(moveTo != moveFrom)
            signalList.moveColumn(moveFrom, moveTo);
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
        int columnCount = signalList.getColumnCount();
        Properties settings = getApplicationProperties();
        for(int position=0;position<columnCount;position++)
        {
          String column = signalList.getColumnName(position);
          StringBuffer property = new StringBuffer("SignalListDialog.Column");
          property.append(position);
          settings.setProperty(property.toString(), column);
        }//for(int position=0;position<columnCount;position++)
      }
    });
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
    String filtered = applicationProperties.getProperty("SignalListDialog.filtered", Boolean.FALSE.toString());
    if(Boolean.valueOf(filtered).booleanValue())
    {
      String filter = applicationProperties.getProperty("SignalListDialog.filterWhere");
      if(filter == null)
        filter = applicationProperties.getProperty("SignalListDialog.filter");
      setFilter(filter);
    }//if(Boolean.valueOf(filtered).booleanValue())
    structureChanged();
  }
  
//  /**
//   * Called when an item drug from the tree enters a drop target.
//   *
//   * @param dsde The <CODE>DragSourceDragEvent</CODE> that caused the invocation of this method.
//   */
//  public void dragEnter(DragSourceDragEvent dsde)
//  {
//    if((dsde.getDropAction() & DnDConstants.ACTION_COPY) != 0)
//      dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
//    else
//      dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
//  }
//
//  /**
//   * Called when an item drug from the tree is drug over a drop target. This
//   * method is not implemented but is provided as part of the
//   * <CODE>DragGestureListener</CODE> interface.
//   *
//   * @param dsde The <CODE>DragSourceDragEvent</CODE> that caused the invocation of this method.
//   */
//  public void dragOver(DragSourceDragEvent dsde)
//  {
//  }
//
//  /**
//   * Called when an item drug from the tree's drop action changes. This method
//   * is provided as part of the <CODE>DragGestureListener</CODE> interface.
//   *
//   * @param dsde The <CODE>DragSourceDragEvent</CODE> that caused the invocation of this method.
//   */
//  public void dropActionChanged(DragSourceDragEvent dsde)
//  {
//    if((dsde.getDropAction() & DnDConstants.ACTION_COPY) != 0)
//      dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
//    else
//      dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyNoDrop);
//  }
//
//  /**
//   * Called when an item drug from the tree leaves a drop target. This method
//   * is not implemented but is provided as part of the
//   * <CODE>DragGestureListener</CODE> interface.
//   *
//   * @param dsde The <CODE>DragSourceEvent</CODE> that caused the invocation of this method.
//   */
//  public void dragExit(DragSourceEvent dse)
//  {
//    dse.getDragSourceContext().setCursor(DragSource.DefaultCopyNoDrop);
//  }
//
//  /**
//   * Called when an item drug from the list's drag operation ends.
//   *
//   * @param dsde The <CODE>DragSourceDropEvent</CODE> that caused the invocation of this method.
//   */
//  public void dragDropEnd(DragSourceDropEvent dsde)
//  {
//  }
//
//  /**
//   * Called when the drag operation is started. This method is provided as part
//   * of the <CODE>DragGestureListener</CODE> interface. It starts the drag
//   * operation.
//   *
//   * @param dge The <CODE>DragGestureEvent</CODE> that caused the invocation of this method.
//   */
//  public void dragGestureRecognized(DragGestureEvent dge)
//  {
//    Signal selectedSignal = (Signal)signalList.getSelectedValue();
//    Signal[] selectedSignals = getValue();
//    dragSource.startDrag(dge, DragSource.DefaultCopyNoDrop, selectedSignal, this);
//  }

  /**
   * Sets the model for the table. By default this is an instance of 
   * <CODE>ArchivesignalListTableModel</CODE>.
   * 
   * @param newSignalListModel The model containing the fields for the table.
   */
  public void setSignalListModel(AbstractSignalTableModel newSignalListModel)
  {
    signalListModel = newSignalListModel;
    signalList.setModel(signalListModel);
    signalListModel.addTableModelListener(new TableModelListener()
      {
        public void tableChanged(TableModelEvent e)
        {
          signalListModel_tableChanged(e);
        }
      });
    structureChanged();
    signalListModel.fireTableDataChanged();
  }

  /**
   * Gets the model used in the table.
   * 
   * @return The model used in the table.
   */
  public AbstractSignalTableModel getSignalListModel()
  {
    return signalListModel;
  }
  
  /**
   * Sets the selection mode for the dialog. Valid Values are 
   * <CODE>ListSelectionModel.MULTIPLE_INTERVAL_SELECTION</CODE>, 
   * <CODE>ListSelectionModel.SINGLE_INTERVAL_SELECTION</CODE>, and 
   * <CODE>ListSelectionModel.SINGL_SELECTION</CODE>. The default mode is 
   * <CODE>ListSelectionModel.MULTIPLE_INTERVAL_SELECTION</CODE>.
   * 
   * @param selectionMode The selection mode for the dialog.
   */
  public void setSelectionMode(int selectionMode)
  {
    signalList.setSelectionMode(selectionMode);
  }
  
  /**
   * Gets the selection mode for the dialog. Valid Values are 
   * <CODE>ListSelectionModel.MULTIPLE_INTERVAL_SELECTION</CODE>, 
   * <CODE>ListSelectionModel.SINGLE_INTERVAL_SELECTION</CODE>, and 
   * <CODE>ListSelectionModel.SINGL_SELECTION</CODE>. The default mode is 
   * <CODE>ListSelectionModel.MULTIPLE_INTERVAL_SELECTION</CODE>.
   * 
   * @return The selection mode for the dialog.
   */
  public void getSelectionMode()
  {
    signalList.getSelectionModel().getSelectionMode();
  }
}