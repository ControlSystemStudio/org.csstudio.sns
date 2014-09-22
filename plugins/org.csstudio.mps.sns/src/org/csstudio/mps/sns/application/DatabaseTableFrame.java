package org.csstudio.mps.sns.application;

import org.csstudio.mps.sns.apps.recorddetails.RecordDetailsDialog;
import org.csstudio.mps.sns.sql.SelectStatement;
import org.csstudio.mps.sns.sql.TableJoin;
import org.csstudio.mps.sns.tools.database.CachingDatabaseAdaptor;
import org.csstudio.mps.sns.application.AbstractTableInterface;
import org.csstudio.mps.sns.tools.swing.ColumnSelectDialog;
import org.csstudio.mps.sns.tools.swing.ColumnSelectTableModel;
import org.csstudio.mps.sns.tools.swing.FillColumnDialog;
import org.csstudio.mps.sns.apps.filter.FilterFrame;
import org.csstudio.mps.sns.tools.swing.TableSearcher;
import org.csstudio.mps.sns.tools.swing.TableSorter;
import org.csstudio.mps.sns.tools.database.DatabaseAdaptor;
import java.awt.*;
import java.awt.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import java.io.*;

import java.sql.*;

import java.util.*;

import javax.sql.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.csstudio.mps.sns.tools.database.swing.DatabaseEditTableModel;
import org.csstudio.mps.sns.tools.database.swing.DatabaseTableModel;
import org.csstudio.mps.sns.view.MPSBrowserView;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import java.awt.Event;

/**
 * Provides an interface for viewing and updating the device tables.
 * 
 * @author Chris Fowlkes
 */
public class DatabaseTableFrame extends AbstractTableInterface 
{
  private DatabaseEditTableModel tableModel = new DatabaseEditTableModel();
  private JComboBox tableList = new JComboBox();
  /**
   * Holds the dialog used to apply filters to the data.
   */
  private FilterFrame filterDialog;
  /**
   * Holds the table names selectable in the list. The index of the list items
   * will correspond to the index of the array, but the list can contain 
   * different (user friendly) table names.
   */
  String[] tableNames;
  /**
   * Holds the dialog used to change the columns displayed in the table.
   */
  private ColumnSelectDialog columnsDialog;
  /**
   * Holds the <CODE>JFileChooser</CODE> used when the save as pop up menu item
   * is clicked.
   */
  private JFileChooser saveDialog;
  /**
   * Holds a <CODE>String</CODE> that identifies this instance of 
   * <CODE>DatabaseTableFrame</CODE> in the property file.
   */
  private String propertyKey;
  /**
   * Holds the dialog used to fill a column with values.
   */
  private FillColumnDialog columnFillDialog;
  private TableJoin[] builderTableJoins;
  private String[][] builderColumnInformation;
  private String builderLinkColumn;
  private JMenuBar menuBar = new JMenuBar();
  private JMenu columnMenu = new JMenu();
  private JMenu recordMenu = new JMenu();
  private JMenuItem fillColumnMenuItem = new JMenuItem();
  private JMenuItem columnsMenuItem = new JMenuItem();
  private JMenuItem singleRecordMenuItem = new JMenuItem();
  private JMenuItem duplicateMenuItem = new JMenuItem();
  private JMenuItem saveAsMenuItem = new JMenuItem();
  private JMenu searchMenu = new JMenu();
  private JMenuItem findMenuItem = new JMenuItem();
  private JMenuItem replaceMenuItem = new JMenuItem();
  private JMenuItem findNextMenuItem = new JMenuItem();
  
  /**
   * Creates a new <CODE>DatabaseTableFrame</CODE>. This constructor creates an 
   * instance of the window that has no title and is resizable, closeable, 
   * maximizable, and iconifiable.
   */
  public DatabaseTableFrame()
  {
    this(null);
  }
  
  /**
   * Creates a new <CODE>DatabaseTableFrame</CODE>. This constructor creates an 
   * instance of the window that has no title and is resizable, closeable, 
   * maximizable, and iconifiable.
   * 
   * @param toolBarComponents The components to add to the toolbar on the left side of the <CODE>JComboBox</CODE>.
   */
  protected DatabaseTableFrame(Component[] toolBarComponents)
  {
    try
    {
      jbInit();
      if(toolBarComponents != null)
        for(int i=0;i<toolBarComponents.length;i++)
          addToToolBar(toolBarComponents[i]);
      addToToolBar(tableList);
      setTableModel(tableModel);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception Thrown on error.
   */
  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(600, 500));
    this.setJMenuBar(menuBar);
    this.addVetoableChangeListener(new VetoableChangeListener()
      {
        public void vetoableChange(PropertyChangeEvent e) throws java.beans.PropertyVetoException
        {
          this_vetoableChange(e);
        }
      });
    tableList.addItemListener(new ItemListener()
      {
        public void itemStateChanged(ItemEvent e)
        {
          tableList_itemStateChanged(e);
        }
      });
    columnMenu.setText("Column");
    columnMenu.setMnemonic('C');
    recordMenu.setText("Record");
    recordMenu.setMnemonic('R');
    fillColumnMenuItem.setText("Fill Column...");
    fillColumnMenuItem.setMnemonic('l');
    fillColumnMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          fillColumnMenuItem_actionPerformed(e);
        }
      });
    columnsMenuItem.setText("Select Columns...");
    columnsMenuItem.setMnemonic('S');
    columnsMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          columnsMenuItem_actionPerformed(e);
        }
      });
    singleRecordMenuItem.setText("Single Record View...");
    singleRecordMenuItem.setMnemonic('S');
    singleRecordMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          singleRecordMenuItem_actionPerformed(e);
        }
      });
    duplicateMenuItem.setText("Duplicate Row");
    duplicateMenuItem.setMnemonic('D');
    duplicateMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          duplicateMenuItem_actionPerformed(e);
        }
      });
    saveAsMenuItem.setText("Save As...");
    saveAsMenuItem.setMnemonic('a');
    saveAsMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          saveAsMenuItem_actionPerformed(e);
        }
      });
    searchMenu.setText("Search");
    searchMenu.setMnemonic('a');
    findMenuItem.setText("Find...");
    findMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK, false));
    findMenuItem.setMnemonic('F');
    findMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          findMenuItem_actionPerformed(e);
        }
      });
    replaceMenuItem.setText("Replace...");
    replaceMenuItem.setMnemonic('R');
    replaceMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK, false));
    replaceMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          replaceMenuItem_actionPerformed(e);
        }
      });
    findNextMenuItem.setText("Find Next");
    findNextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, false));
    findNextMenuItem.setEnabled(false);
    findNextMenuItem.setMnemonic('N');
    findNextMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          findNextMenuItem_actionPerformed(e);
        }
      });
    searchMenu.add(findMenuItem);
    searchMenu.add(replaceMenuItem);
    searchMenu.add(findNextMenuItem);
    columnMenu.add(fillColumnMenuItem);
    columnMenu.add(columnsMenuItem);
    columnMenu.add(searchMenu);
    menuBar.add(columnMenu);
    recordMenu.add(singleRecordMenuItem);
    recordMenu.add(duplicateMenuItem);
    recordMenu.add(saveAsMenuItem);
    menuBar.add(recordMenu);
  }

  /**
   * Called when the selected item in the table combo changes. This method sets 
   * the table name property in the table model, which refreshes the data in the
   * table.
   *
   * @param e The <CODE>ItemEvent</CODE> that caused the invocation of this method.
   */
  void tableList_itemStateChanged(ItemEvent e)
  {
    if(e.getStateChange() == ItemEvent.SELECTED)
      try
      {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int selectedIndex = ((DefaultComboBoxModel)tableList.getModel()).getIndexOf(e.getItem());
        String newTableName = tableNames[selectedIndex];
        String currentTableName = tableModel.getTableName();
        if(currentTableName == null || ! currentTableName.equals(newTableName))
          if(promptSaveChanges())
            setSelectedTable(newTableName);
          else
            tableList.setSelectedItem(currentTableName);//Go back to the last selected table.
      }
      catch(java.sql.SQLException ex)
      {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(DatabaseTableFrame.this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
      }
      finally
      {
        setCursor(Cursor.getDefaultCursor());
      }
  }
  
  /**
   * Sets the <CODE>CachingDatabaseAdaptor</CODE> used by the window to connect 
   * to the database.
   *
   * @param connectionPool The <CODE>CachingDatabaseAdaptor</CODE> to use to connect to the database.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void setDatabaseAdaptor(CachingDatabaseAdaptor connectionPool) throws java.sql.SQLException
  {
    super.setDataSource(connectionPool.getDataSource());
    tableModel.setDatabaseAdaptor(connectionPool);
  }

  /**
   * Sets the names of the tables viewable in the interface. If more than one 
   * table name is given they are displayed in a drop down list.
   * 
   * @param tableNames The names of the tables that can be viewed in the table.
   * @param displayNames The text to display in the drop down list for the tables. Pass as <CODE>null</CODE> to display the table names.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void setTableNames(String[] tableNames, String[] displayNames) throws java.sql.SQLException
  {
    if(displayNames == null)
      displayNames = tableNames;
    if(tableNames.length != displayNames.length)
      throw new java.lang.IllegalArgumentException("Must have the same number of table names as descriptions.");
    if(tableNames.length == 1)
    {
      tableList.setVisible(false);
      tableModel.setTableName(tableNames[0]);
      checkLastFilter();
    }
    else
    {
      this.tableNames = tableNames;
      tableList.setVisible(true);
      for(int i=0;i<displayNames.length;i++)
        tableList.addItem(displayNames[i]);
    }
  }

  /**
   * Selects the given table. If more than one table is being used by the 
   * interface, this method sets the selected table. The selected table must
   * have been added to the window with the <CODE>setTableNames</CODE> method.
   * 
   * @param selectedTable The name of the table to display on the next data refresh.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void setSelectedTable(String selectedTable) throws java.sql.SQLException
  {
    int index = -1;
    for(int i=0;i<tableNames.length;i++) 
      if(tableNames[i].equals(selectedTable))
      {
        index = i;
        break;
      }
    if(index >= 0)
    {
      tableModel.setTableName(selectedTable);
      tableModel.setPrimaryKeyEditable(selectedTable.equals("DVC_TYPE"));
      //Checking to see if the data was filtered last time.
      checkLastFilter();
      if(tableModel.getDatabaseAdaptor() != null)
      {
        refresh();
        //Save the table as the last one opened.
        StringBuffer key = new StringBuffer(getPropertyKey());
        key.append(".selectedTableName");
        getApplicationProperties().setProperty(key.toString(), selectedTable);
      }
      tableList.setSelectedIndex(index);
    }
  }

  /**
   * sets the selected table to the one that was selected when the interface was 
   * last used. Calling this method before calling 
   * <CODE>setApplicationProperties</CODE> will result in a 
   * <CODE>NullPointerException</CODE>.
   * 
   * @param defaultTableName The name of the table to select by default.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void restoreLastTableSelected(String defaultTableName) throws java.sql.SQLException
  {
    StringBuffer buffer = new StringBuffer(getPropertyKey());
    buffer.append(".selectedTableName");
    String propertyName = buffer.toString();
    String tableName = getApplicationProperties().getProperty(propertyName, defaultTableName);
    setSelectedTable(tableName);
  }
  
  /**
   * Checks to see if a filter was applied the last time the table was viewed.
   * If so, this method applies the filter.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  private void checkLastFilter() throws java.sql.SQLException
  {
    Properties applicationProperties = getApplicationProperties();
    String key = getPropertyFileKey();
    String filteredKey = key + ".filtered";
    String filteredDefault = Boolean.FALSE.toString();
    String filtered = applicationProperties.getProperty(filteredKey , filteredDefault);
    if(Boolean.valueOf(filtered).booleanValue())
    {
      String where = applicationProperties.getProperty(key + ".filterWhere");
      if(where != null)
        tableModel.setFilter(where);
      else
      {
        //Check for an old school filter...
        String stringFilter = applicationProperties.getProperty(key + ".filter");
        if(stringFilter != null)
          tableModel.setFilter(stringFilter);
        else
          tableModel.setFilter(null);
      }
    }
    else
      tableModel.setFilter(null);
    enableToolBarButtons();
  }
  
  /**
   * Called when the window is closed. This method checks to see if the user 
   * needs to commit any changes. If changes are pending, the user is prompted 
   * to commit them.
   *
   * @param e The <CODE>PropertyChangeEvent</CODE> that caused the invocation of this method.
   * @throws java.beans.PropertyVetoException Thorwn if the user cancels the close operation.
   */
  void this_vetoableChange(PropertyChangeEvent e) throws PropertyVetoException
  {
    if(e.getPropertyName().equals("closed") && e.getNewValue().equals(Boolean.TRUE))
      try
      {
        if(! promptSaveChanges())
          throw new PropertyVetoException("Close canceled by user.", e);
        try
        {
          tableModel.closeConnection();
        }
        catch(Exception exc)
        {
          exc.printStackTrace();
        }
      }
      catch(java.sql.SQLException ex)
      {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        throw new PropertyVetoException(ex.getMessage(), e);
      }
  }

  /**
   * If there are changes pending, this method prompts the user to commit them.
   * If the user does not click cancel, a commit or rollback is done depending 
   * on the user's selection.
   * 
   * @return <CODE>true</CODE> if the operation that called this method should continue, <CODE>false</CODE> if the user canceled.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  private boolean promptSaveChanges() throws java.sql.SQLException
  {
    if(tableModel.isCommitNeeded() || tableModel.isInsertRowChanged() || tableModel.isEditRowChanged())
    {
      int option = JOptionPane.showConfirmDialog(this, "Do you want to commit the changes made?", "Changes Pending", JOptionPane.YES_NO_CANCEL_OPTION);
      if(option == JOptionPane.YES_OPTION)
        tableModel.commit();      
      else
        if(option == JOptionPane.NO_OPTION)
          try
          {
            tableModel.rollback();//No need to refresh data. Call directly.
          }
          catch(java.sql.SQLException exc)
          {
            exc.printStackTrace();
          }
        else
          return false;//user cancelled.
    }
    return true;
  }
  
  /**
   * Called when the row selected in the table changes. If the row that has been
   * deselected was changed, this method posts that row.
   *
   * @param selectedRows The indexes of the rows currenlty selected.
   */
  protected void tableSelectionChanged(int[] selectedRows)
  {
    //Don't know if current row is edit or insert, must determine.
    int mode = tableModel.getMode();
    if(mode != org.csstudio.mps.sns.tools.database.swing.DatabaseEditTableModel.READ_MODE)
    {
      int editRow;
      if(mode == org.csstudio.mps.sns.tools.database.swing.DatabaseEditTableModel.INSERT_MODE)
        editRow = tableModel.getRowCount() - 1;
      else
        editRow = tableModel.getEditRowIndex();
      if(Arrays.binarySearch(selectedRows, editRow) < 0)//Edit row no longer selected
        try
        {
          if(mode == org.csstudio.mps.sns.tools.database.swing.DatabaseEditTableModel.INSERT_MODE)
          {
            if(tableModel.isInsertRowChanged())
              tableModel.postInsertRow();
            else
              tableModel.cancelInsertRow();
          }
          else
            if(tableModel.getMode() == org.csstudio.mps.sns.tools.database.swing.DatabaseEditTableModel.EDIT_MODE)
              if(tableModel.isEditRowChanged())
                tableModel.postEditRow();
              else
                tableModel.cancelEditRow();
        }
        catch(java.sql.SQLException ex)
        {
          ex.printStackTrace();
          selectAndShowRow(convertModelRowToDisplay(editRow));
          JOptionPane.showMessageDialog(DatabaseTableFrame.this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }
  }

  /**
   * Called when the filter toolbar button is clicked. This method allows the 
   * user to apply a filter to the data by showing the <CODE>FilterFrame</CODE>
   * interface.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  protected void filter() throws java.sql.SQLException
  {
    //Need to post any changes first so nothing gets lost.
    if(tableModel.isChanged())
      tableModel.post();
    //Setting the filter for the data.
    String tableName = tableModel.getTableName();
    if(filterDialog == null)
    {
      filterDialog = new FilterFrame(getMainWindow(), "Filter Data", true);
      filterDialog.center();
      filterDialog.setDataSource(tableModel.getDatabaseAdaptor().getDataSource());
      filterDialog.setApplicationProperties(getApplicationProperties());
      filterDialog.setSchema(tableModel.getSchema());
    }
    filterDialog.clearBuilderTab();
    if(builderLinkColumn != null)
    {
      if(builderTableJoins != null)
        for(int i=0;i<builderTableJoins.length;i++) 
          filterDialog.addBuilderTable(builderTableJoins[i]);
      if(builderColumnInformation != null)
        for(int i=0;i<builderColumnInformation.length;i++) 
          filterDialog.addBuilderColumn(builderColumnInformation[i][0], builderColumnInformation[i][1], builderColumnInformation[i][2]);
    }
    filterDialog.setTableName(tableName);
    Properties settings = getApplicationProperties();
    String key = getPropertyFileKey();
    String currentFilter = settings.getProperty(key + ".savedFilter");
    String filterType;
    if(currentFilter == null)//Check for old format.
    {
      currentFilter = settings.getProperty(key + ".filter");
      filterType = "E";
    }
    else
      filterType = settings.getProperty(key + ".savedFilterType");
    if(currentFilter != null)
      if(filterType.equals("E"))
        filterDialog.restoreEditorFilter(currentFilter);
      else
        filterDialog.restoreBuilderFilter(currentFilter);
    //Show the dialog.
    filterDialog.setVisible(true);
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if(filterDialog.getResult() == FilterFrame.OK)
      {
        SelectStatement newFilter = filterDialog.getFilter();
        String where = newFilter.getWhereClause();
        if(filterDialog.getMode() == FilterFrame.EDITOR_MODE)
          settings.setProperty(key + ".savedFilterType", "E");
        else
        {
          settings.setProperty(key + ".savedFilterType", "B");
          StringBuffer filterText = new StringBuffer(" ");
          filterText.append(builderLinkColumn);
          filterText.append(" IN ( ");
          newFilter.addColumn(tableName, builderLinkColumn);
          filterText.append(newFilter);
          filterText.append(" ) ");
          where = filterText.toString();
        }
        String saveableFilter = filterDialog.getSaveableFilter();
        settings.setProperty(key + ".savedFilter", saveableFilter);
        settings.setProperty(key + ".filterWhere", where);
        settings.setProperty(key + ".filterFrom", newFilter.getFromClause());
        settings.setProperty(key + ".filtered", Boolean.TRUE.toString());
        setTextFilter(where);
      }
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  /**
   * Sets and saves the text filter.
   * 
   * @param textFilter The filter to set and save.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  protected void setTextFilter(String textFilter)throws java.sql.SQLException
  {
    tableModel.setFilter(textFilter);
    refresh();
  }

  /**
   * Eables the builder tab of the filter dialog.
   * 
   * @param joins The table joins needed by the filter dialog.
   * @param columnInformation The columns to include in the builder.
   * @param linkColumnName The column that links the filter columns to the data.
   */
  public void enableBuilderFilter(TableJoin[] joins, String[][] columnInformation, String linkColumnName)
  {
    builderTableJoins = joins;
    builderColumnInformation = columnInformation;
    builderLinkColumn = linkColumnName;
  }

  /**
   * This method is called when the remove filter button in the toolbar is 
   * clicked. It removes the filter from the data in the model.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void removeFilter() throws java.sql.SQLException
  {
    if(tableModel.isChanged())
      tableModel.post();
    tableModel.setFilter(null);
    refresh();
    StringBuffer buffer = new StringBuffer(getPropertyFileKey());
    buffer.append(".filtered");
    String key = buffer.toString();
    getApplicationProperties().setProperty(key, Boolean.FALSE.toString());
  }

  /**
   * Determines if the data should be editable or not.
   * 
   * @param editable Pass as <CODE>false</CODE> to make read only, <CODE>true</CODE> by default.
   */
  public void setEditable(boolean editable)
  {
    tableModel.setEditable(editable);
    replaceMenuItem.setVisible(editable);
    duplicateMenuItem.setVisible(editable);
    super.setEditable(editable);
  }

  /**
   * Determines if the data should be editable or not.
   * 
   * @return <CODE>false</CODE> if read only, <CODE>true</CODE> otherwise.
   */
  public boolean isEditable()
  {
    return tableModel.isEditable();
  }

  /**
   * Called when the select columns popup menu item is clicked. This method
   * shows a dialog that allows the user to select the columns they can see for
   * the table being edited.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void columnsMenuItem_actionPerformed(ActionEvent e)
  {
    try
    {
      try
      {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(columnsDialog == null)
        {
          columnsDialog = new ColumnSelectDialog(getMainWindow(), "Column Select", true);
          columnsDialog.setApplicationProperties(getApplicationProperties());
          ColumnSelectTableModel model = new ColumnSelectTableModel();
          columnsDialog.setColumnsTableModel(model);
        }
        columnsDialog.setSelectedColumns(tableModel.getVisibleColumns());
        columnsDialog.setAvailableColumns(tableModel.getHiddenColumns());
        columnsDialog.center();
      }
      finally
      {
        setCursor(Cursor.getDefaultCursor());
      }
      columnsDialog.setVisible(true);
      if(columnsDialog.getResult() == ColumnSelectDialog.OK)
        tableModel.setVisibleColumns(columnsDialog.getSelectedColumns());
    }
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Sets the properties stored in the applications properties file. This method 
   * also passes the instance of <CODE>Properties</CODE> to the super class and
   * also passes it to the <CODE>DatabaseEditTableModel</CODE> which uses it to
   * store visible columns.
   * 
   * @param applicationProperties The applicationProperties application settings.
   */
  public void setApplicationProperties(Properties applicationProperties)
  {
    super.setApplicationProperties(applicationProperties);
    tableModel.setApplicationProperties(applicationProperties);
  }

  /**
   * Determines if the table being edited contains the given column name. This
   * method is a wrapper for the <CODE>tableContainsField</CODE> method in the
   * <CODE>DatabaseEditTableModel</CODE> class.
   * 
   * @param columnName The name of the column to look for in the table model.
   * @return <CODE>true</CODE> if the table contains the given column, <CODE>false</CODE> if not.
   */
  public boolean tableContainsField(String columnName)
  {
    return tableModel.tableContainsField(columnName);
  }

  /**
   * Gets the <CODE>String</CODE> value of the field requested. This method is a 
   * wrapper for the <CODE>getStringValue</CODE> method in the 
   * <CODE>DatabaseTableEditModel</CODE> class.
   * 
   * @param row The row number in the table, not the model, to return the value of.
   * @param columnName The name of the column to return the value of.
   * @return The value of the given field.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public String getStringValue(int row, String columnName) throws java.sql.SQLException
  {
    return tableModel.getStringValue(convertDisplayRowToModel(row), columnName);
  }

  /**
   * Finds the model row that contains the given value for the given field. This
   * method is a wrapper for the <CODE>findFieldValue</CODE> method in the 
   * <CODE>DatabaseTableEditModel</CODE> class. Before the value is returned it 
   * is converted to the table index rather than the model index.
   * 
   * @param fieldName The name of the field to search.
   * @param value The value to search the field for.
   * @return The index of the row that contains the given value for the given field, -1 if it is not found.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public int findFieldValue(String fieldName, String value) throws java.sql.SQLException
  {
    int rowIndex = tableModel.findFieldValue(fieldName, value);
    if(rowIndex >= 0)
      rowIndex = ((TableSorter)getTableModel()).getTableRowNumber(rowIndex);
    return rowIndex;
  }

  /**
   * Gets the name of the table being displayed in the <CODE>JTable</CODE>.
   * 
   * @return The name of the database table being displayed.
   */
  public String getTableName()
  {
    return tableModel.getTableName();
  }

  /**
   * Forces the model to use tables in the given schema instead of the schema
   * defined by <CODE>Jeri.SCHEMA</CODE>.
   * 
   * @param schema The schema to which the database table belongs. Defaults to <CODE>Jeri.SCHEMA</CODE>.
   */
  final public void setSchema(String schema)
  {
    tableModel.setSchema(schema);
    if(filterDialog != null)
      filterDialog.setSchema(tableModel.getSchema());
  }

  /**
   * Makes sure a column will not have a checkbox editor. By default only one
   * character text fields that end with "_IND" will be checkboxes. To force a
   * field that falls into this criteria to be a text field, pass the column
   * name to this method.
   * 
   * @param columnName The name of the indicator column that is to be a text field instead of a checkbox.
   */
  public void addNoCheckBoxField(String columnName)
  {
    tableModel.addNoCheckBoxField(columnName);
  }

  /**
   * Sets the table model used in the interface.
   * 
   * @param tableModel The model to use in the table.
   */
  public void setTableModel(DatabaseTableModel tableModel)
  {
    this.tableModel = (DatabaseEditTableModel)tableModel;
    super.setTableModel(tableModel);
  }

  /**
   * Called when the single record view popup menu item is clicked. This method 
   * shows the selected record in the record details dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void singleRecordMenuItem_actionPerformed(ActionEvent e)
  {
    RecordDetailsDialog singleRecordDialog = getRecordDetailsDialog();
    try
    {
      singleRecordDialog.setDisplayNames(loadColumnDisplayNames());
      singleRecordDialog.center();
      singleRecordDialog.setVisible(true);
    }
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Loads the column display names.
   * 
   * @return The user friendly names for the columns.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  private HashMap loadColumnDisplayNames() throws java.sql.SQLException
  {
    String[] columnNames = new String[tableModel.getColumnCount()];
    HashMap displayNames = new HashMap(columnNames.length);
    Connection oracleConnection = getDataSource().getConnection();
    try
    {
      Statement query = oracleConnection.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT COLUMN_NAME, COMMENTS FROM ALL_COL_COMMENTS WHERE OWNER = '");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append("' AND TABLE_NAME = '");
        sql.append(tableModel.getTableName());
        sql.append("'");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
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
            }           
          }
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
      oracleConnection.close();
    }
    return displayNames;
  }

  /**
   * Called when the find menu item is clicked. This method calls 
   * <CODE>searchReplace</CODE>.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void findMenuItem_actionPerformed(ActionEvent e)
  {
    TableSearcher searcher = getTableSearcher();
    searcher.searchReplace(false, getMainWindow());
    if(searcher.getSearchValue() != null)
      findNextMenuItem.setEnabled(true);
  }

  /**
   * Called when the replace menu item is clicked. This method calls the 
   * <CODE>searchReplace</CODE> method.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void replaceMenuItem_actionPerformed(ActionEvent e)
  {
    TableSearcher searcher = getTableSearcher();
    searcher.searchReplace(true, getMainWindow());
    if(searcher.getSearchValue() != null)
      findNextMenuItem.setEnabled(true);
  }

  /**
   * Called when the find next menu item is clicked. This method finds the next 
   * occurance of the value last searched for by calling the 
   * <CODE>findNext()</CODE> method.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void findNextMenuItem_actionPerformed(ActionEvent e)
  {
    TableSearcher searcher = getTableSearcher();
    if(searcher.findNext(-1) < 0)
      JOptionPane.showMessageDialog(this, "The search value '" + searcher.getSearchValue() + "' was not found.", "Find Error", JOptionPane.ERROR_MESSAGE);
    if(searcher.getSearchValue() != null)
      findNextMenuItem.setEnabled(true);
  }

  /**
   * Called when the duplicate menu item is clicked. This method inserts a new 
   * row into the table and initializes all fields to the values of the current 
   * row.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  protected void duplicateMenuItem_actionPerformed(ActionEvent e)
  {
    int selectedRow = getSelectedRows()[0];
    Object[] defaultValues = new Object[tableModel.getColumnCount()];
    for(int i=0;i<defaultValues.length;i++)
      defaultValues[i] = tableModel.getValueAt(selectedRow, i);
    tableModel.insert(defaultValues);
    selectAndShowRow(tableModel.getRowCount() - 1);
  }

  /**
   * Enables or disables the buttons in the toolbar and popup menu.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  protected void enableToolBarButtons() throws java.sql.SQLException
  {
    super.enableToolBarButtons();
    int selectedRowCount = getSelectedRows().length;
    boolean enableDuplicate = shouldDuplicateMenuItemBeEnabled(selectedRowCount);
    duplicateMenuItem.setEnabled(enableDuplicate);
    singleRecordMenuItem.setEnabled(selectedRowCount == 1);
    saveAsMenuItem.setEnabled(selectedRowCount >= 1);
    Object searchValue = getTableSearcher().getSearchValue();
    findNextMenuItem.setEnabled(searchValue != null);
    int menuCount = menuBar.getMenuCount();
    for(int i=0;i<menuCount;i++) 
      enableMenu(menuBar.getMenu(i));
  }

  /**
   * Disables the menu if all if the items in it are disabled. Call this method
   * after all items in the menu are enabled or disabled.
   * 
   * @param menu The menu to enable or disable.
   * @return <CODE>true</CODE> if the menu item is enabled, <CODE>false</CODE> otherwise.
   */
  private boolean enableMenu(JMenu menu)
  {
    int menuComponentCount = menu.getMenuComponentCount();
    boolean enableMenu = false;
    for(int j=0;j<menuComponentCount;j++) 
    {
      Component menuComponent = menu.getMenuComponent(j);
      if(menuComponent instanceof JMenu)
      {
        if(enableMenu((JMenu)menuComponent))
          enableMenu = true;
      }
      else
        if(menuComponent instanceof JMenuItem)
          if(((JMenuItem)menuComponent).isEnabled())
            enableMenu = true;
    }
    menu.setEnabled(enableMenu);
    return enableMenu;
  }
  
  /**
   * Determines if the dplicate menu item should be enabled.
   * 
   * @return <CODE>true</CODE> if the duplicate menu item should be enabled, <CODE>false</CODE> otherwise.
   */
  protected boolean shouldDuplicateMenuItemBeEnabled(int selectedRowCount)
  {
    return selectedRowCount == 1;
  }

  /**
   * Called when the save as menu item is clicked. This method saves the 
   * selected rows to a CSV file.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invoccation of this method.
   */
  private void saveAsMenuItem_actionPerformed(ActionEvent e)
  {
    Properties applicationSettings = getApplicationProperties();
    if(saveDialog == null)
    {
      String lastPath = applicationSettings.getProperty("DatabaseTableFrame.saveAsDirectory");
      saveDialog = new JFileChooser(lastPath);
      saveDialog.setFileFilter(new javax.swing.filechooser.FileFilter()
      {
        public boolean accept(File chosenFile)
        {
          if(chosenFile.isDirectory())
            return true;
          else
          {
            String fileName = chosenFile.getName().toLowerCase();
            if(fileName.endsWith(".csv"))
              return true;
            else
              return false;
          }
        }

        public String getDescription()
        {
          return "CSV Files (*.csv)";
        }
      });
    }
    if(saveDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
      try
      {
        File saveFile = saveDialog.getSelectedFile();
        String saveDirectory = saveFile.getParent();
        applicationSettings.setProperty("DatabaseTableFrame.saveAsDirectory", saveDirectory);
        saveSelectedRowsAsCSV(saveFile);
      }
      catch(java.io.IOException ex)
      {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
      }
  }

  /**
   * Hides the duplicate menu item or shows the duplicate menu item. The value 
   * is <CODE>true</CODE> by default.
   * 
   * @param newDuplicateMenuItemVisible Pass as <CODE>false</CODE> to hide the duplicate menu item.
   */
  protected final void setDuplicateMenuItemVisible(boolean newDuplicateMenuItemVisible)
  {
    duplicateMenuItem.setVisible(newDuplicateMenuItemVisible);
  }

  /**
   * Gets the property used to identify this instance of 
   * <CODE>DatabaseTableFrame</CODE> in the property file. 
   * 
   * @return The key that differentiates this instance of <CODE>DatabaseTableFrame</CODE> from others.
   */
  public String getPropertyKey()
  {
    if(propertyKey == null)
    {
      String[] classPackage = this.getClass().getName().split("\\.");
      return classPackage[classPackage.length - 1];
    }
    return propertyKey;
  }

  /**
   * Sets the property used to identify this instance of 
   * <CODE>DatabaseTableFrame</CODE> in the property file. This should be called 
   * before <CODE>setApplicationProperties</CODE> to insure that all keys are 
   * properly loaded.
   * 
   * @param newPropertyKey The key that differentiates this instance of <CODE>DatabaseTableFrame</CODE> from others.
   */
  public void setPropertyKey(String newPropertyKey)
  {
    propertyKey = newPropertyKey;
  }

  /**
   * Gets the key for the property file that incorporates the selected table.
   * 
   * @return The key for the property file that incorporates the selected table name.
   */
  protected String getPropertyFileKey()
  {
    StringBuffer key = new StringBuffer(getPropertyKey());
    key.append(".");
    key.append(getTableName());
    return key.toString();
  }

  /**
   * Called when the fill column menu item is clicked. This method allows the 
   * user to apply a value in a column to the rows selected in the table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void fillColumnMenuItem_actionPerformed(ActionEvent e)
  {
    if(columnFillDialog == null)
    {
      columnFillDialog = new FillColumnDialog(getMainWindow(), "Fill Column", true);
      columnFillDialog.setApplicationProperties(getApplicationProperties());
    }
    //Determine which columns are editable...
    ArrayList editableColumns = new ArrayList();
    ArrayList keyColumns;
    if(tableModel.isPrimaryKeyEditable())
      keyColumns = null;//All columns are editable.
    else
      keyColumns = tableModel.getPrimaryKeys();//Keys are not editable.
    int columnCount = tableModel.getColumnCount();
    for(int i=0;i<columnCount;i++)
    {
      String currentColumnName = tableModel.getColumnName(i);
      if(keyColumns == null || ! keyColumns.contains(currentColumnName))
        editableColumns.add(currentColumnName);
      Class currentColumnClass = tableModel.getColumnClass(i);
      columnFillDialog.setColumnClass(currentColumnName, currentColumnClass);
    }
    String[] fields = new String[editableColumns.size()];
    fields = (String[])editableColumns.toArray(fields);
    columnFillDialog.setFields(fields);
    int modelColumn = convertDisplayColumnToModel(getSelectedColumn());
    if(modelColumn >= 0 && modelColumn < columnCount)
      columnFillDialog.setSelectedField(tableModel.getColumnName(modelColumn));
    columnFillDialog.center();
    columnFillDialog.setVisible(true);
    if(columnFillDialog.getResult() == FillColumnDialog.OK)
    {
      String fillColumnName = columnFillDialog.getSelectedField();
      final int column = tableModel.findColumn(fillColumnName);
      final Object value = columnFillDialog.getValue();
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          try
          {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int[] rows = getSelectedRows();
            for(int i=0;i<rows.length;i++)
              rows[i] = convertDisplayRowToModel(rows[i]);//Convert to model rows.
            try
            {
              fillColumn(value, rows, column);
            }
            catch(java.sql.SQLException ex)
            {
              ex.printStackTrace();
              JOptionPane.showMessageDialog(DatabaseTableFrame.this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            }
          }
          finally
          {
            setCursor(Cursor.getDefaultCursor());
          }
        }
      });
    }
  }

  /**
   * Fills in the values for the given column. This method gives the field in
   * the signals given the value passed in.
   * 
   * @param value The value to set the field to.
   * @param modelRowNumbers The model indexes of the rows for which to set the value.
   * @param column The model index of the column for which to set the value.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void fillColumn(Object value, int[] modelRowNumbers, int column) throws java.sql.SQLException
  {
    stopEditing();
    final int[] tableRowNumbers = new int[modelRowNumbers.length];
    try
    {
      for(int i=0;i<modelRowNumbers.length;i++)
      {
        tableModel.setValueAt(value, modelRowNumbers[i], column);
        tableRowNumbers[i] = convertModelColumnToDisplay(modelRowNumbers[i]);
      }
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          selectAndShowRows(tableRowNumbers);
        }
      });
    }
    finally
    {
      tableModel.fireTableDataChanged();
    }
  }

  /**
   * Adds the given control to the toolbar between the default buttons and the 
   * table drop down list.
   * 
   * @param control The <CODE>Component</CODE> to add to the tool bar.
   */
  public void addToToolBar(Component control)
  {
    super.removeFromToolBar(tableList);
    super.addToToolBar(control);
    super.addToToolBar(tableList);
  }
}