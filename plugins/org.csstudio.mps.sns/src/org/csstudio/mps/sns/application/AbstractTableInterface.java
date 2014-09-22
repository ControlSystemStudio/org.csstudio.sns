package org.csstudio.mps.sns.application;

import org.csstudio.mps.sns.apps.recorddetails.RecordDetailsDialog;
import org.csstudio.mps.sns.tools.swing.TableSearcher;
import org.csstudio.mps.sns.tools.swing.TableSorter;
import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.io.*;

import java.sql.*;

import java.util.*;

import javax.sql.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.csstudio.mps.sns.tools.database.swing.DatabaseTableModel;
import org.csstudio.mps.sns.application.JeriInternalFrame;

/**
 * Provides an interface with a <CODE>JTable</CODE> for displaying data and a 
 * <CODE>JToolBar</CODE> for navigating the data. This class is niether 
 * <CODE>abstract</CODE> or an <CODE>interface</CODE>, and should probably be 
 * renamed at some point.
 * 
 * @author Chris Fowlkes
 */
public class AbstractTableInterface extends JeriInternalFrame 
{
  private JLabel statusBar = new JLabel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JScrollPane scrollPane = new JScrollPane();
  private JTable table = new JTable();
  private JPanel mainPanel = new JPanel();
  private BorderLayout mainPanelLayout = new BorderLayout();
  private JToolBar toolBar = new JToolBar();
  private JButton commitButton = new JButton();
  private JButton rollbackButton = new JButton();
  private JButton addFilterButton = new JButton();
  private JButton removeFilterButton = new JButton();
  private JButton firstButton = new JButton();
  private JButton priorButton = new JButton();
  private JButton nextButton = new JButton();
  private JButton lastButton = new JButton();
  private JButton insertButton = new JButton();
  private JButton deleteButton = new JButton();
  private JButton postButton = new JButton();
  private JButton cancelButton = new JButton();
  private JButton refreshButton = new JButton();
  /**
   * Holds the icon that denotes there is a filter applied.
   */
  private Icon editFilterIcon;
  /**
   * Holds the icon that denotes there is no filter currently applied.
   */
  private Icon newFilterIcon;
  /**
   * Holds the dialog used for the single record view.
   */
  private RecordDetailsDialog singleRecordDialog;
  /**
   * Holds the model index of the last column the popup menu was shown over.
   */
  private int popupColumn = -1;
  /**
   * Holds the instance of <CODE>TableSearcher</CODE> used for search and 
   * replace operations in the table.
   */
  private TableSearcher searcher = new TableSearcher();

  /**
   * Creates a new <CODE>AbstractTableInterface</CODE>.
   */
  public AbstractTableInterface()
  {
    try
    {
      jbInit();
      table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          table_selectionChanged(e);
        }
      });
      table.getTableHeader().addMouseListener(new MouseAdapter()
      {
        public void mouseClicked(MouseEvent e)
        {
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              tableRowsSorted();
            }
          });
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
   * @throws java.lang.Exception Thrown on error.
   */
  private void jbInit() throws Exception
  {
    this.getContentPane().setLayout(borderLayout1);
    this.setSize(new Dimension(600, 500));
    statusBar.setText("Row Count: 0");
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.addMouseListener(new MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          checkPopup(e);
        }

        public void mouseReleased(MouseEvent e)
        {
          checkPopup(e);
        }
      });
    mainPanel.setLayout(mainPanelLayout);
    commitButton.setActionCommand("commitButton");
    commitButton.setToolTipText("Commit");
    commitButton.setEnabled(false);
    commitButton.setMargin(new Insets(2, 2, 2, 2));
    commitButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          commitButton_actionPerformed(e);
        }
      });
    rollbackButton.setToolTipText("Rollback");
    rollbackButton.setEnabled(false);
    rollbackButton.setMargin(new Insets(2, 2, 2, 2));
    rollbackButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          rollbackButton_actionPerformed(e);
        }
      });
    addFilterButton.setToolTipText("New Filter");
    addFilterButton.setMargin(new Insets(2, 2, 2, 2));
    addFilterButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          addFilterButton_actionPerformed(e);
        }
      });
    removeFilterButton.setToolTipText("Remove Filter");
    removeFilterButton.setEnabled(false);
    removeFilterButton.setMargin(new Insets(2, 2, 2, 2));
    removeFilterButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          removeFilterButton_actionPerformed(e);
        }
      });
    firstButton.setToolTipText("First Record");
    firstButton.setEnabled(false);
    firstButton.setMargin(new Insets(2, 2, 2, 2));
    firstButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          firstButton_actionPerformed(e);
        }
      });
    priorButton.setToolTipText("Prior Record");
    priorButton.setEnabled(false);
    priorButton.setMargin(new Insets(2, 2, 2, 2));
    priorButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          priorButton_actionPerformed(e);
        }
      });
    nextButton.setToolTipText("Next Record");
    nextButton.setEnabled(false);
    nextButton.setMargin(new Insets(2, 2, 2, 2));
    nextButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          nextButton_actionPerformed(e);
        }
      });
    lastButton.setToolTipText("Last Record");
    lastButton.setEnabled(false);
    lastButton.setMargin(new Insets(2, 2, 2, 2));
    lastButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          lastButton_actionPerformed(e);
        }
      });
    insertButton.setToolTipText("Insert Record");
    insertButton.setMargin(new Insets(2, 2, 2, 2));
    insertButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          insertButton_actionPerformed(e);
        }
      });
    deleteButton.setToolTipText("Delete Record");
    deleteButton.setEnabled(false);
    deleteButton.setMargin(new Insets(2, 2, 2, 2));
    deleteButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          deleteButton_actionPerformed(e);
        }
      });
    postButton.setToolTipText("Post Edit");
    postButton.setEnabled(false);
    postButton.setMargin(new Insets(2, 2, 2, 2));
    postButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          postButton_actionPerformed(e);
        }
      });
    cancelButton.setToolTipText("Cancel Edit");
    cancelButton.setEnabled(false);
    cancelButton.setMargin(new Insets(2, 2, 2, 2));
    cancelButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          cancelButton_actionPerformed(e);
        }
      });
    refreshButton.setToolTipText("Refresh Data");
    refreshButton.setMargin(new Insets(2, 2, 2, 2));
    refreshButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          refreshButton_actionPerformed(e);
        }
      });
    searcher.setTable(table);
    scrollPane.getViewport().add(table, null);
    mainPanel.add(statusBar, BorderLayout.SOUTH);
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    this.getContentPane().add(toolBar, BorderLayout.NORTH);
    toolBar.add(commitButton, null);
    toolBar.add(rollbackButton, null);
    toolBar.add(addFilterButton, null);
    toolBar.add(removeFilterButton, null);
    toolBar.add(firstButton, null);
    toolBar.add(priorButton, null);
    toolBar.add(nextButton, null);
    toolBar.add(lastButton, null);
    toolBar.add(insertButton, null);
    toolBar.add(deleteButton, null);
    toolBar.add(postButton, null);
    toolBar.add(cancelButton, null);
    toolBar.add(refreshButton, null);
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
        try
        {
          statusBar.setText("Row Count: " + table.getRowCount());
          enableToolBarButtons();
          if(e.getFirstRow() == TableModelEvent.HEADER_ROW)
            tableStructureChanged();
        }//try
        catch(java.sql.SQLException ex)
        {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(AbstractTableInterface.this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }//catch(java.sql.SQLException ex)
      }
    });
  }

  /**
   * Called when the table structure is changed. This method is called by the 
   * <CODE>tableModel_tableChanged</CODE> method whenever that method is invoked
   * to handle a table structure change.
   */
  private void tableStructureChanged()
  {
    restoreColumnWidths();
    arrangeColumns();
    final String key = getPropertyFileKey();
    int columnCount = table.getColumnCount();
    TableColumnModel allColumns = table.getColumnModel();
    for(int i=0;i<columnCount;i++)
    {
      final String propertyName = key + "." + table.getColumnName(i) + ".width";
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
   * This method puts the columns in the order the user left them in.
   */
  private void arrangeColumns()
  {
    String key = getPropertyFileKey();
    Properties settings = getApplicationProperties();
    String columnCountProperty = settings.getProperty(key + ".ColumnCount");
    if(columnCountProperty != null)
    {
      int columnCount = Integer.parseInt(columnCountProperty), moveTo = 0;
      int tableColumnCount = table.getColumnCount();
      ArrayList modelColumns = new ArrayList(tableColumnCount);
      TableModel model = getTableModel();
      for(int i=0;i<tableColumnCount;i++)
        modelColumns.add(model.getColumnName(i));
      for(int i=0;i<columnCount;i++)
      {
        String currentColumnName = settings.getProperty(key + ".Column" + i);
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
    }//if(columnCountProperty != null)
  }

  /**
   * This method restores the column widths to the widths saved in the 
   * applicaiton's properties file.
   */
  private void restoreColumnWidths()
  {
    String key = getPropertyFileKey();
    TableColumnModel allColumns = table.getColumnModel();
    int columnCount = allColumns.getColumnCount();
    TableColumn currentColumn;
    String widthProperty;
    for(int i=0;i<columnCount;i++)
    {
      currentColumn = allColumns.getColumn(i);
      final String propertyName = key + "." + table.getColumnName(i) + ".width";
      widthProperty = getApplicationProperties().getProperty(propertyName, "100");
      currentColumn.setPreferredWidth(Integer.parseInt(widthProperty));
      currentColumn.addPropertyChangeListener(new java.beans.PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent e)
        {
          if(e.getPropertyName().equals("preferredWidth"))
            getApplicationProperties().setProperty(propertyName, e.getNewValue().toString());
        }
      });
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
        String propertyName = getPropertyFileKey(), column;
        int columnCount = table.getColumnCount();
        Properties settings = getApplicationProperties();
        settings.setProperty(propertyName + ".ColumnCount", String.valueOf(columnCount));
        for(int position=0;position<columnCount;position++)
        {
          column = table.getColumnName(position);
          settings.setProperty(propertyName + ".Column" + position, column);
        }//for(int position=0;position<columnCount;position++)
      }
    });
  }

  /**
   * Called when the commit button is clicked. This method commits the changes 
   * made since the last commit or rollback to the database.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void commitButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if(table.isEditing())
        table.getCellEditor().stopCellEditing();
      getTableModel().commit();
      enableToolBarButtons();
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
   * Called when the rollback button is clicked. This method cancels the changes
   * made since the last commit or rollback.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void rollbackButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if(table.isEditing())
        table.getCellEditor().stopCellEditing();
      getTableModel().rollback();
      refresh();
      enableToolBarButtons();
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
   * Called when the add filter button is clicked. This method allows the user
   * to filter the data by showing the filter dialog and applying the filter to 
   * the data.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocatin of this method.
   */
  void addFilterButton_actionPerformed(ActionEvent e)
  {
    try
    {
      filter();
      enableToolBarButtons();
    }//try
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }//catch(java.sql.SQLException ex)
  }

  /**
   * Called when the remove filter button is clicked. This method removes the 
   * filter from the data.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocatin of this method.
   */
  void removeFilterButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      removeFilter();
      enableToolBarButtons();
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
   * Called when the first record button is clicked. This method selects the 
   * first record in the table.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void firstButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      selectAndShowRow(0);
    }//try
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }//finally
  }

  /**
   * Called when the previous record button is clicked. This selects the row 
   * before the first selected row in the table.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void priorButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      selectAndShowRow(table.getSelectionModel().getMinSelectionIndex() - 1);
    }//try
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }//finally
  }

  /**
   * Called when the next record button is clicked. This method selectes the row 
   * after the last selected row in the table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void nextButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      selectAndShowRow(table.getSelectionModel().getMaxSelectionIndex() + 1);
    }//try
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }//finally
  }

  /**
   * Called when the last record button is clicked. This method selects the last 
   * row in the table.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void lastButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      selectAndShowRow(table.getRowCount() - 1);
    }//try
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }//finally
  }

  /**
   * Called when the insert button is clicked. This method inserts a row at the 
   * bottom of the table.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void insertButton_actionPerformed(ActionEvent e)
  {
    try
    {
      if(table.isEditing())
        table.getCellEditor().stopCellEditing();
      getTableModel().insert();
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          try
          {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            selectAndShowRow(table.getRowCount() - 1);
          }//try
          finally
          {
            setCursor(Cursor.getDefaultCursor());
          }//finally
        }
      });
      enableToolBarButtons();
    }//try
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }//catch(java.sql.SQLException ex)
  }

  /**
   * Called when the delete button is clicked. This method deletes the selected
   * row from the table.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void deleteButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if(table.isEditing())
        table.getCellEditor().stopCellEditing();
      int[] rowsToDelete = table.getSelectedRows();
      for(int i=0;i<rowsToDelete.length;i++)
        rowsToDelete[i] = convertDisplayRowToModel(rowsToDelete[i]);
      getTableModel().delete(rowsToDelete);
      enableToolBarButtons();
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
   * Called when the post row button is clicked. This method posts the currently 
   * selected row.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void postButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if(table.isEditing())
        table.getCellEditor().stopCellEditing();
      getTableModel().post();
      enableToolBarButtons();
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
   * Called when the cancel button is clicked. This method cancels edits made to 
   * the selected row.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void cancelButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if(table.isEditing())
        table.getCellEditor().stopCellEditing();
      getTableModel().cancel();
      enableToolBarButtons();
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
   * Called when the refresh button is clicked. This method refreshes the data 
   * in the table.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void refreshButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if(table.isEditing())
        table.getCellEditor().stopCellEditing();
      refresh();
      enableToolBarButtons();
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
   * Called when the row selected in the table changes. If the row that has been
   * deselected was changed, this method posts that row.
   *
   * @param e The <CODE>ListSelectionEvent</CODE> that caused the invocation of this method.
   */
  void table_selectionChanged(ListSelectionEvent e)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        try
        {
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          int[] selectedRows = table.getSelectedRows();
          for(int i=0;i<selectedRows.length;i++)
            selectedRows[i] = convertDisplayRowToModel(selectedRows[i]);
          tableSelectionChanged(selectedRows);
          enableToolBarButtons();
        }//try
        catch(java.sql.SQLException ex)
        {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(AbstractTableInterface.this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }//catch(java.sql.SQLException ex)
        finally
        {
          setCursor(Cursor.getDefaultCursor());
        }//finally
      }
    });
  }

  /**
   * Selects the given row in the table. This method selects the given row and 
   * makes sure it is visible by scrolling the table if needed. The row passed 
   * in needs to be the row in the table, not the row in the model. Model row 
   * numbers can be converted to display row numbers with the 
   * <CODE>convertModelRowToDisplay</CODE> method.
   *
   * @param row The number of the row to select in the table.
   */
  protected void selectAndShowRow(int row)
  {
    table.getSelectionModel().setSelectionInterval(row, row);
    Rectangle visible = table.getVisibleRect();
    Rectangle firstCell = table.getCellRect(row, 0, true);
    Rectangle scrollTo = new Rectangle(visible.x, firstCell.y, visible.width, firstCell.height);
    table.scrollRectToVisible(scrollTo);
  }

  /**
   * Selects the given rows in the table. This method selects the given rows and 
   * scrolls the table so that the first one is visible.
   * 
   * @param rows The indexes of the rows to select.
   */
  protected void selectAndShowRows(int[] rows)
  {
    table.getSelectionModel().clearSelection();
    if(rows.length > 0)
    {
      Arrays.sort(rows);
      ListSelectionModel selectionModel = table.getSelectionModel();
      for(int i=0;i<rows.length;i++) 
        selectionModel.addSelectionInterval(rows[i], rows[i]);
      //Need to scroll the table.
      Rectangle visible = table.getVisibleRect();
      Rectangle firstCell = table.getCellRect(rows[0], 0, true);
      Rectangle lastCell = table.getCellRect(rows[rows.length-1], 0, true);
      int height = lastCell.y + lastCell.height - firstCell.y;
      Rectangle scrollTo = new Rectangle(visible.x, firstCell.y, visible.width, height);
      table.scrollRectToVisible(scrollTo);
    }//if(rows.length > 0)
  }

  /**
   * Puts an image on the commit button.
   * 
   * @param commitIcon The <CODE>Icon</CODE> for the commit toolbar button.
   */
  public void setCommitIcon(Icon commitIcon)
  {
    commitButton.setIcon(commitIcon);
  }

  /**
   * Puts an image on the rollback button.
   * 
   * @param rollbackIcon The <CODE>Icon</CODE> for the rollback toolbar button.
   */
  public void setRollbackIcon(Icon rollbackIcon)
  {
    rollbackButton.setIcon(rollbackIcon);
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
    if(getTableModel().isFiltered())
      addFilterButton.setIcon(editFilterIcon);
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
    if(! getTableModel().isFiltered())
      addFilterButton.setIcon(newFilterIcon);
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
   * Puts an image on the first button.
   * 
   * @param firstIcon The <CODE>Icon</CODE> for the first toolbar button.
   */
  public void setFirstIcon(Icon firstIcon)
  {
    firstButton.setIcon(firstIcon);
  }

  /**
   * Gets the image on the first button.
   * 
   * @return The image that appears on the first tool bar button.
   */
  public Icon getFirstIcon()
  {
    return firstButton.getIcon();
  }
  
  /**
   * Puts an image on the prior button.
   * 
   * @param priorIcon The <CODE>Icon</CODE> for the prior tool bar button.
   */
  public void setPriorIcon(Icon priorIcon)
  {
    priorButton.setIcon(priorIcon);
  }

  /**
   * Gets the image on the prior button.
   * 
   * @return The image that appears on the prior tool bar button.
   */
  public Icon getPriorIcon()
  {
    return priorButton.getIcon();
  }
  
  /**
   * Puts an image on the next button.
   * 
   * @param nextIcon The <CODE>Icon</CODE> for the next toolbar button.
   */
  public void setNextIcon(Icon nextIcon)
  {
    nextButton.setIcon(nextIcon);
  }

  /**
   * Gets the image on the next button.
   * 
   * @return The imabe that appears on the next tool bar button.
   */
  public Icon getNextIcon()
  {
    return nextButton.getIcon();  
  }
  
  /**
   * Puts an image on the last button.
   * 
   * @param lastIcon The <CODE>Icon</CODE> for the last toolbar button.
   */
  public void setLastIcon(Icon lastIcon)
  {
    lastButton.setIcon(lastIcon);
  }

  /**
   * Gets tha image on the last record button.
   * 
   * @return The icon from the tool bar button that takes the user to the last record.
   */
  public Icon getLastIcon()
  {
    return lastButton.getIcon();
  }
  
  /**
   * Puts an image on the insert button.
   * 
   * @param insertIcon The <CODE>Icon</CODE> for the insert toolbar button.
   */
  public void setInsertIcon(Icon insertIcon)
  {
    insertButton.setIcon(insertIcon);
  }

  /**
   * Puts an image on the delete button.
   * 
   * @param deleteIcon The <CODE>Icon</CODE> for the delete toolbar button.
   */
  public void setDeleteIcon(Icon deleteIcon)
  {
    deleteButton.setIcon(deleteIcon);
  }

  /**
   * Puts an image on the post button.
   * 
   * @param postIcon The <CODE>Icon</CODE> for the post toolbar button.
   */
  public void setPostIcon(Icon postIcon)
  {
    postButton.setIcon(postIcon);
  }

  /**
   * Puts an image on the cancel button.
   * 
   * @param cancelIcon The <CODE>Icon</CODE> for the cancel toolbar button.
   */
  public void setCancelIcon(Icon cancelIcon)
  {
    cancelButton.setIcon(cancelIcon);
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
   * Sets the model for the table to the one given. The given model is wrapped 
   * in an instance of <CODE>TableSorter</CODE> before being assigned to the 
   * <CODE>JTable</CODE>.
   * 
   * @param tableModel The <CODE>TableModel</CODE> that holds the data to be displayed.
   */
  public void setTableModel(DatabaseTableModel tableModel)
  {
    TableSorter sorter = new TableSorter(tableModel);
    sorter.addMouseListenerToHeaderInTable(table);
    table.setModel(sorter);
    tableModel.addTableModelListener(new TableModelListener()
    {
      public void tableChanged(TableModelEvent e)
      {
        tableModel_tableChanged(e);
      }
    });
  }

  /**
   * Gets the numbers of the rows that are selected in the table.
   * 
   * @return The indices of the selected rows.
   */
  public int[] getSelectedRows()
  {
    return table.getSelectedRows();
  }
  
  /**
   * Returns the column selected.
   * 
   * @return The index of the selcted column.
   */
  public int getSelectedColumn()
  {
    return table.getSelectedColumn();
  }

  /**
   * Gets the row that is the anchor selection for the table. The anchor 
   * selection is the last row selected.
   * 
   * @return The last row added to the selection or -1 if no row is selected.
   */
  public int getAnchorSelection()
  {
    return table.getSelectionModel().getAnchorSelectionIndex();
  }
  
  /**
   * Gets the model being used by the table.
   * 
   * @return The tabes model.
   */
  public DatabaseTableModel getTableModel()
  {
    return (DatabaseTableModel)((TableSorter)table.getModel()).getModel();
  }

  /**
   * Adds a <CODE>Component</CODE> to the tool bar.
   * 
   * @param control The <CODE>Component</CODE> to add to the interface's toolbar.
   */
  public void addToToolBar(Component control)
  {
    toolBar.add(control, null);
  }
  
  /**
   * Adds a <CODE>Component</CODE> to the tool bar.
   * 
   * @param control The <CODE>Component</CODE> to add to the interface's toolbar.
   * @param index The index at which to add the <CODE>Component</CODE>.
   */
  public void addToToolBar(Component control, int index)
  {
    toolBar.add(control, index);
  }

  /**
   * Removes the given <CODE>Component</CODE> from the tool bar.
   * 
   * @param control The <CODE>Component</CODE> to remove from the tool bar.
   */
  public void removeFromToolBar(Component control)
  {
    toolBar.remove(control);
  }
  
  /**
   * Enables or disables the buttons on the tool bar based on the state of the 
   * table and the data in it.
   *
   * @throws java.sql.SQLException Thrown on sql error.
   */
  protected void enableToolBarButtons() throws java.sql.SQLException
  {
    //Enable commit and rollback buttons if data is changed.
    boolean modelChanged = getTableModel().isCommitNeeded();
    commitButton.setEnabled(modelChanged);
    rollbackButton.setEnabled(modelChanged);
    //Enable filter buttons.
    if(isFiltered())
    {
      removeFilterButton.setEnabled(true);
      addFilterButton.setToolTipText("Edit Filter");
      addFilterButton.setIcon(editFilterIcon);
    }//if(getTableModel().isFiltered())
    else
    {
      //No filter applied.
      removeFilterButton.setEnabled(false);
      addFilterButton.setToolTipText("New Filter");
      addFilterButton.setIcon(newFilterIcon);
    }//else
    //Enabling of navigation buttons depends on which rows are selected.
    int rowCount = table.getRowCount();
    ListSelectionModel selectionModel = table.getSelectionModel();
    firstButton.setEnabled(rowCount > 0 && ! selectionModel.isSelectedIndex(0));
    int firstSelectedRow = selectionModel.getMinSelectionIndex();
    priorButton.setEnabled(firstSelectedRow > 0);
    int lastSelectedRow = selectionModel.getMaxSelectionIndex();
    nextButton.setEnabled(lastSelectedRow >= 0 && lastSelectedRow < rowCount - 1);
    lastButton.setEnabled(rowCount > 0 && ! selectionModel.isSelectedIndex(rowCount - 1));
    deleteButton.setEnabled(table.getSelectedRowCount() > 0);
    //Check for unposted changes...
    boolean changed;
    changed = getTableModel().isChanged();
    postButton.setEnabled(changed);
    cancelButton.setEnabled(changed);
  }

  /**
   * Determines if the data is filtered.
   * 
   * @return <CODE>true</CODE> if the data is filtered, <CODE>false</CODE> otherwise.
   */
  public boolean isFiltered()
  {
    return getTableModel().isFiltered();
  }
  
  /**
   * Sets the editor for columns representing a certain <CODE>Class</CODE> in 
   * the model. This method allows subclasses to assign a specific editor to 
   * certain columns in the table.
   * 
   * @param columnClass The <CODE>Class</CODE> of the columns that should use the editor.
   * @param editor The <CODE>TableCellEditor</CODE> for the columns.
   */
  protected void setDefaultTableEditor(Class columnClass, TableCellEditor editor)
  {
    table.setDefaultEditor(columnClass, editor);
  }

  /**
   * Sets the renderer for columns representing a certain <CODE>Class</CODE> in 
   * the model. This method allows subclasses to assign a specific renderer to 
   * certain columns in the table.
   * 
   * @param columnClass The <CODE>Class</CODE> of the columns that should use the renderer.
   * @param renderer The <CODE>TableCellRenderer</CODE> for the columns.
   */
  protected void setDefaultTableRenderer(Class columnClass, TableCellRenderer renderer)
  {
    table.setDefaultRenderer(columnClass, renderer);
  }

  /**
   * Checks the <CODE>MouseEvent</CODE> to see if it is the popup trigger and
   * takes the appropriate action. If the <CODE>MouseEvent</CODE> is the popup 
   * trigger and nothing is selected, the row clicked is selected. The method 
   * then calls the <CODE>showPopup</CODE> method.
   * 
   * @param e The <CODE>MouseEvent</CODE> that caused the invocation of this method.
   */
  private void checkPopup(final MouseEvent e)
  {
    if(e.isPopupTrigger())
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          Point clickPoint = e.getPoint();
          int rowClicked = table.rowAtPoint(clickPoint);
          if(rowClicked >= 0 && ! table.isRowSelected(rowClicked))
            table.getSelectionModel().setSelectionInterval(rowClicked, rowClicked);
          int viewColumn = table.columnAtPoint(clickPoint);
          popupColumn = table.convertColumnIndexToModel(viewColumn);
          showPopup(e);
        }
      });
  }

  /**
   * Hides or shows the insert button. If the insert button is not visible the 
   * <CODE>insert()</CODE> method will not be called. By default, the button is 
   * visible.
   * 
   * @param insertButtonVisible Pass as <CODE>true</CODE> to show the insert button, <CODE>false</CODE> to hide.
   */
  protected void setInsertButtonVisible(boolean insertButtonVisible)
  {
    insertButton.setVisible(insertButtonVisible);
  }

  /**
   * Determines if the insert button is visible. By default the button is 
   * visible.
   * 
   * @return <CODE>true</CODE> if the insert button is visible, <CODE>false</CODE> if not.
   */
  protected boolean isInsertButtonVisible()
  {
    return insertButton.isVisible();
  }

  /**
   * Hides or shows the delete button. If the delete button is not visible the 
   * <CODE>delete</CODE> method will not be called. By default, the button is 
   * visible.
   * 
   * @param deleteButtonVisible Pass as <CODE>true</CODE> to show the insert button, <CODE>false</CODE> to hide.
   */
  protected void setDeleteButtonVisible(boolean deleteButtonVisible)
  {
    deleteButton.setVisible(deleteButtonVisible);
  }

  /**
   * Determines if the delete button is visible. By default the button is 
   * visible.
   * 
   * @return <CODE>true</CODE> if the delete button is visible, <CODE>false</CODE> if not.
   */
  protected boolean isDeleteButtonVisible()
  {
    return deleteButton.isVisible();
  }

  /**
   * Hides or shows the filter buttons. If the add and remove filter buttons are
   * not visible the <CODE>filter()</CODE> and <CODE>removeFilter()</CODE> 
   * methods will not be called. By default, the buttons are visible.
   * 
   * @param filterButtonsVisible Pass as <CODE>true</CODE> to show the filter buttons, <CODE>false</CODE> to hide them.
   */
  protected void setFilterButtonsVisible(boolean filterButtonsVisible)
  {
    addFilterButton.setVisible(filterButtonsVisible);
    removeFilterButton.setVisible(filterButtonsVisible);
  }

  /**
   * Determines if both filter buttons are visible. By default the buttons are 
   * visible.
   * 
   * @return <CODE>true</CODE> if the filter buttons are visible, <CODE>false</CODE> if not.
   */
  protected boolean isFilterButtonsVisible()
  {
    return addFilterButton.isVisible() && removeFilterButton.isVisible();
  }

  /**
   * Gets the model index for the column at the given <CODE>Point</CODE>.
   * 
   * @param columnLocation The <CODE>Point</CODE> at which to find the column.
   * @return The index in the model of the column at the <CODE>Point</CODE> given.
   */
  protected int getColumnAt(Point columnLocation)
  {
    return table.convertColumnIndexToModel(table.columnAtPoint(columnLocation));
  }

  /**
   * This method converts a displayed row number to the model row number. This
   * is needed in case the table is sorted.
   * 
   * @param tableRow The index of the row in the table.
   * @return The index of the row in the model.
   */
  protected int convertDisplayRowToModel(int tableRow)
  {
    return ((TableSorter)table.getModel()).getModelRowNumber(tableRow);
  }

  /**
   * This method converts a model row number to the displayed row number. This
   * is needed in case the table is sorted.
   * 
   * @param modelRow The index of the row in the model.
   * @return The index of the row in the table.
   */
  protected int convertModelRowToDisplay(int modelRow)
  {
    return ((TableSorter)table.getModel()).getTableRowNumber(modelRow);
  }

  /**
   * Converts the column model number to the display column number.
   * 
   * @param modelColumn The index of the column in the model.
   * @return The index of the column in the table.
   */
  protected int convertModelColumnToDisplay(int modelColumn)
  {
    return table.convertColumnIndexToView(modelColumn);
  }

  /**
   * Converts the column model number to the display column number.
   * 
   * @param displayColumn The index of the column in the model.
   * @return The index of the column in the model.
   */
  protected int convertDisplayColumnToModel(int displayColumn)
  {
    return table.convertColumnIndexToModel(displayColumn);
  }
  
  /**
   * Adds a <CODE>Component</CODE> to the interface between the tool bar and the 
   * table. 
   * 
   * @param message The Component to add between the tool bar and the table.
   */
  public void addMessage(Component message)
  {
    mainPanel.add(message, BorderLayout.NORTH);
  }

  /**
   * Enables or disables the refresh button. The refresh button is always 
   * enabled, unless disabled by this method. This provides subclasses a way to
   * prevent refreshing if the model is not ready.
   * 
   * @param refreshEnabled Pass as <CODE>false</CODE> to disable the refresh button, <CODE>true</CODE> to enable it.
   */
  public void setRefreshEnabled(boolean refreshEnabled)
  {
    refreshButton.setEnabled(refreshEnabled);
  }

  /**
   * Determines if the refresh button is enabled.
   * 
   * @return <CODE>true</CODE> if the refresh button is enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isRefreshEnabled()
  {
    return refreshButton.isEnabled();
  }

  /**
   * Used to put the interface in read only mode. In read only mode, the editing 
   * buttons are not visible. By default, the interface is editable.
   * 
   * @param editable Pass as <CODE>false</CODE> to hide all of the editing toolbar buttons, <CODE>true</CODE> to show them.
   */
  public void setEditable(boolean editable)
  {
    commitButton.setVisible(editable);
    rollbackButton.setVisible(editable);
    insertButton.setVisible(editable);
    deleteButton.setVisible(editable);
    postButton.setVisible(editable);
    cancelButton.setVisible(editable);
  }

  /**
   * Selects all rows in the table.
   */
  protected void selectAllRows()
  {
    int lastIndex = table.getRowCount() - 1;
    table.getSelectionModel().setSelectionInterval(0, lastIndex);
  }

  /**
   * This method is called when the table is sorted. It is empty and is only 
   * provided so that it can be overridden and implemented by the sub class.
   */
  protected void tableRowsSorted()
  {
  }

  /**
   * Called when the filter button in the tool bar is clicked. This method is 
   * not implemented but is provided to be overridden by the subclass.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  protected void filter() throws java.sql.SQLException
  {
  }
  
  /**
   * Called when the remove filter button in the tool bar is clicked. This 
   * method is not implemented but is provided to be overridden by the subclass.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  protected void removeFilter() throws java.sql.SQLException
  {
  }

  /**
   * Called when the refresh button in the tool bar is clicked. By default this 
   * method calls the <CODE>refresh()</CODE> method in the 
   * <CODE>DatabaseTableModel</CODE> class.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void refresh() throws java.sql.SQLException
  {
    table.clearSelection();
    getTableModel().refresh();
  }

  /**
   * Gets the key used to make things saved in the property file for the 
   * application unique to the interface. The key returned is the name of the
   * extending class in most cases. By default this method returns 
   * <CODE>getClass().getName()</CODE>.
   * 
   * @return A unique text string for the class extending this one.
   */
  protected String getPropertyFileKey()
  {
    return getClass().getName();
  }

  /**
   * This method will be called when the table's row selection changes. It is 
   * not implemented but provided to override.
   * 
   * @param selectedRows The currently (after the change) selected rows in the table.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  protected void tableSelectionChanged(int[] selectedRows) throws java.sql.SQLException
  {
  }

  /**
   * This will be called when the user has invoked the popup menu on the table. 
   * It is not implemented but provided to override.
   * 
   * @param e The <CODE>MouseEvent</CODE> that caused the invocation of the method.
   */
  protected void showPopup(MouseEvent e)
  {
  }

  /**
   * Gets the editor for the given cell.
   * 
   * @param rowIndex The row index of the cell for which to return the editor.
   * @param columnIndex The column index of the cell for which to return the editor.
   * @return The <CODE>Component</CODE> used to edit the given cell.
   */
  protected Component getEditor(int rowIndex, int columnIndex)
  {
    Object currentValue = table.getValueAt(rowIndex, columnIndex);
    return table.getCellEditor(rowIndex, columnIndex).getTableCellEditorComponent(table, currentValue, true, rowIndex, columnIndex);
  }

  /**
   * Gets the <CODE>JToolBar</CODE> used to navigate through the table.
   * 
   * @return The <CODE>RecordDetailsDialog</CODE> for the interface.
   */
  final public RecordDetailsDialog getRecordDetailsDialog()
  {
    if(singleRecordDialog == null)
    {
      singleRecordDialog = new RecordDetailsDialog(getMainWindow(), "Single Record View", true);
      singleRecordDialog.addToolBarButton(copyButton(firstButton));
      singleRecordDialog.addToolBarButton(copyButton(priorButton));
      singleRecordDialog.addToolBarButton(copyButton(nextButton));
      singleRecordDialog.addToolBarButton(copyButton(lastButton));
      singleRecordDialog.addToolBarButton(copyButton(insertButton));
      singleRecordDialog.addToolBarButton(copyButton(deleteButton));
      singleRecordDialog.addToolBarButton(copyButton(postButton));
      singleRecordDialog.addToolBarButton(copyButton(cancelButton));
      singleRecordDialog.setTable(table);
      singleRecordDialog.setApplicationProperties(getApplicationProperties());
    }//if(singleRecordDialog == null)
    return singleRecordDialog;
  }

  /**
   * Creates a copy of the given tool bar button for the record detail dialog.
   * 
   * @param toolBarButton The <CODE>JButton</CODE> of which to provide a copy.
   * @return A copy of the <CODE>JButton</CODE> passed in as a parameter.
   */
  private JButton copyButton(final JButton toolBarButton)
  {
    final JButton copy = new JButton();
    copy.setIcon(toolBarButton.getIcon());
    copy.setEnabled(toolBarButton.isEnabled());
    copy.setToolTipText(toolBarButton.getToolTipText());
    copy.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        toolBarButton.doClick();
      }
    });
    toolBarButton.addPropertyChangeListener(new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent e) 
      {
        if(e.getPropertyName().equals("enabled"))
          copy.setEnabled(((Boolean)e.getNewValue()).booleanValue());
      }
    });
    return copy;
  }

  /**
   * Gets the column the popup menu was shown over. The value returned is the 
   * model index of the column, not necissarily the index of the column in the 
   * table.
   * 
   * @return The index of the column the popup was last shown over.
   */
  protected int getPopupColumn()
  {
    return popupColumn;
  }

  /**
   * Returns the <CODE>TableSearcher</CODE> responsible for search and replace 
   * operations on the table.
   * 
   * @return The <CODE>TableSearcher</CODE> for the <CODE>JTable</CODE> in the interface.
   */
  protected TableSearcher getTableSearcher()
  {
    return searcher;
  }

  /**
   * Sets the instance of <CODE>Properties</CODE> used to store application 
   * settings.
   * 
   * @param applicationProperties The instance of <CODE>Properties</CODE> used to store user settings.
   */
  public void setApplicationProperties(Properties applicationProperties)
  {
    super.setApplicationProperties(applicationProperties);
    getTableSearcher().setApplicationProperties(applicationProperties);
  }

  /**
   * Saves the selected rows to the given CSV file.
   * 
   * @param csvFile The CSV file to which to save the selected rows.
   * @throws java.io.IOException Thrown on IO error.
   */
  protected void saveSelectedRowsAsCSV(File csvFile) throws java.io.IOException
  {
    int[] selectedRows = table.getSelectedRows();
    BufferedWriter oStream = new BufferedWriter(new FileWriter(csvFile));
    try
    {
      int columnCount = table.getColumnCount();
      for(int j=0;j<columnCount;j++)
      {
        if(j > 0)
          oStream.write(", ");
        oStream.write(table.getColumnName(j));
      }//for(int j=0;j<;j++)
      oStream.newLine();
      for(int i=0;i<selectedRows.length;i++)
      {
        for(int j=0;j<columnCount;j++)
        {
          if(j>0)
            oStream.write(", ");
          Object currentValue = table.getValueAt(i, j);
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

  /**
   * This method is used to stop the editing of the table.
   */
  protected void stopEditing()
  {
    if(table.isEditing())
      table.getCellEditor().stopCellEditing();
  }
}