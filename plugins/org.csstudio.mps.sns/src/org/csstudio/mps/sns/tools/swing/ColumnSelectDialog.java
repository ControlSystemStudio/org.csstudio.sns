package org.csstudio.mps.sns.tools.swing;

import org.csstudio.mps.sns.IconLoader;
import org.csstudio.mps.sns.application.JeriDialog;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

import java.sql.*;

import java.util.*;

import javax.sql.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * This class provides an interface that allows the user to select columns for
 * the <CODE>SignalTableBrowser</CODE> interface. 
 * 
 * @author Chris Fowlkes
 */
public class ColumnSelectDialog extends JeriDialog 
{
  private JPanel outerButtonPanel = new JPanel();
  private FlowLayout outerButtonPanelLayout = new FlowLayout();
  private JPanel innerButtonPanel = new JPanel();
  private GridLayout innerButtonPanelLayout = new GridLayout();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private JPanel centerPanel = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel labelPanel = new JPanel();
  private GridLayout labelPanelLayout = new GridLayout();
  private JLabel selectedLabel = new JLabel();
  private JPanel tablePanel = new JPanel();
  private GridLayout tablePanelLayout = new GridLayout();
  private JPanel selectedColumnsPanel = new JPanel();
  private BorderLayout selectedColumnsPanelLayout = new BorderLayout();
  private JScrollPane allColumnsScrollPane = new JScrollPane();
  private JScrollPane selectedColumnsScrollPane = new JScrollPane();
  private JPanel moveButtonPanel = new JPanel();
  private JTable allColumnsTable = new JTable();
  private JButton dropSelectedButton = new JButton();
  private GridLayout moveButtonPanelLayout = new GridLayout();
  private JButton dropAllButton = new JButton();
  private JButton addSelectedButton = new JButton();
  private JButton addAllButton = new JButton();
  private JPanel dropAllPanel = new JPanel();
  private JPanel dropSelectedPanel = new JPanel();
  private JPanel addSelectedPanel = new JPanel();
  private JPanel addAllPanel = new JPanel();
  private BorderLayout addAllPanelLayout = new BorderLayout();
  private BorderLayout addSelectedPanelLayout = new BorderLayout();
  private BorderLayout dropAllPanelLayout = new BorderLayout();
  private BorderLayout dropSelectedPanelLayout = new BorderLayout();
  /**
   * Holds the <CODE>DataSource</CODE> used to get the available columns from
   * the database.
   */
  private DataSource dataSource;
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
   * Provides a model for the "choose from" table.
   */
  private ColumnSelectTableModel columnsTableModel = new ColumnSelectTableModel();
  /**
   * Provides sorting functionality for the table.
   */
  private TableSorter sorter = new TableSorter(columnsTableModel);
  private JList selectedColumnsList = new JList();
  private DefaultListModel selectedColumnsListModel = new DefaultListModel();
  /**
   * Holds the names of the original columns so that the OK button can only be
   * enabled when changes have been made.
   */
  private String[] originalColumns = new String[0];
  private JLabel availableLabel = new JLabel();
  /**
   * Holds the <CODE>DataFlavor</CODE> used to drag and drop.
   */
  private DataFlavor columnFlavor = new DataFlavor(Object[].class, "Column");

  /**
   * Creates a new, non-modal <CODE>ColumnSelectDialog</CODE>.
   */
  public ColumnSelectDialog()
  {
    this(null, "", false);
  }

  /**
   * Creates a new <CODE>ColumnSelectDialog</CODE>.
   * 
   * @param parent The parent to the dialog.
   * @param title The title to put in the title bar of the dialog.
   * @param modal Pass as <CODE>true</CODE> for a modal dialog, <CODE>false</CODE> otherwise.
   */
  public ColumnSelectDialog(Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
    try
    {
      jbInit();
      dropSelectedButton.setIcon(IconLoader.getRightIcon());
      dropAllButton.setIcon(IconLoader.getDoubleRightIcon());
      addSelectedButton.setIcon(IconLoader.getLeftIcon());
      addAllButton.setIcon(IconLoader.getDoubleLeftIcon());
      allColumnsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          enableButtons();
        }
      });
      selectedColumnsList.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          enableButtons();
        }
      });
      selectedColumnsList.setTransferHandler(new ColumnSelectDialog.ColumnSelectDialogTransferHandler());
      selectedColumnsList.setDragEnabled(true);
      allColumnsTable.setTransferHandler(new ColumnSelectDialog.ColumnSelectDialogTransferHandler());
      allColumnsTable.setDragEnabled(true);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Component initialization.
   * 
   * @throws Exception Thrown on initialization error.
   */
  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(500, 300));
    this.addWindowListener(new java.awt.event.WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          this_windowClosing(e);
        }
      });
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    outerButtonPanelLayout.setAlignment(2);
    innerButtonPanel.setLayout(innerButtonPanelLayout);
    innerButtonPanelLayout.setHgap(5);
    okButton.setText("OK");
    okButton.setMnemonic('O');
    okButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          okButton_actionPerformed(e);
        }
      });
    cancelButton.setText("Cancel");
    cancelButton.setMnemonic('C');
    cancelButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          cancelButton_actionPerformed(e);
        }
      });
    centerPanel.setLayout(borderLayout1);
    labelPanel.setLayout(labelPanelLayout);
    selectedLabel.setText("Selected Columns");
    tablePanel.setLayout(tablePanelLayout);
    tablePanelLayout.setHgap(5);
    selectedColumnsPanel.setLayout(selectedColumnsPanelLayout);
    selectedColumnsPanelLayout.setHgap(5);
    moveButtonPanel.setLayout(moveButtonPanelLayout);
    dropSelectedButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          dropSelectedButton_actionPerformed(e);
        }
      });
    moveButtonPanelLayout.setColumns(1);
    moveButtonPanelLayout.setRows(4);
    dropAllButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          dropAllButton_actionPerformed(e);
        }
      });
    addSelectedButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          addSelectedButton_actionPerformed(e);
        }
      });
    addAllButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          addAllButton_actionPerformed(e);
        }
      });
    dropAllPanel.setLayout(dropAllPanelLayout);
    dropSelectedPanel.setLayout(dropSelectedPanelLayout);
    addSelectedPanel.setLayout(addSelectedPanelLayout);
    addAllPanel.setLayout(addAllPanelLayout);
    selectedColumnsList.setModel(selectedColumnsListModel);
    availableLabel.setText("Available Columns");
    innerButtonPanel.add(okButton, null);
    innerButtonPanel.add(cancelButton, null);
    outerButtonPanel.add(innerButtonPanel, null);
    this.getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);
    labelPanel.add(selectedLabel, null);
    labelPanel.add(availableLabel, null);
    centerPanel.add(labelPanel, BorderLayout.NORTH);
    selectedColumnsScrollPane.getViewport().add(selectedColumnsList, null);
    selectedColumnsPanel.add(selectedColumnsScrollPane, BorderLayout.CENTER);
    dropSelectedPanel.add(dropSelectedButton, BorderLayout.NORTH);
    moveButtonPanel.add(dropSelectedPanel, null);
    dropAllPanel.add(dropAllButton, BorderLayout.NORTH);
    moveButtonPanel.add(dropAllPanel, null);
    addSelectedPanel.add(addSelectedButton, BorderLayout.NORTH);
    moveButtonPanel.add(addSelectedPanel, null);
    addAllPanel.add(addAllButton, BorderLayout.NORTH);
    moveButtonPanel.add(addAllPanel, null);
    selectedColumnsPanel.add(moveButtonPanel, BorderLayout.EAST);
    tablePanel.add(selectedColumnsPanel, null);
    allColumnsTable.setModel(sorter);
    allColumnsScrollPane.getViewport().add(allColumnsTable, null);
    tablePanel.add(allColumnsScrollPane, null);
    centerPanel.add(tablePanel, BorderLayout.CENTER);
    this.getContentPane().add(centerPanel, BorderLayout.CENTER);
  }

  /**
   * Allows the available column view to be customized to show more information.
   * 
   * @param columnsTableModel <CODE>ColumnSelectTableModel</CODE> to serve as the model for the <CODE>JTable</CODE>.
   */
  public void setColumnsTableModel(ColumnSelectTableModel columnsTableModel)
  {
    this.columnsTableModel = columnsTableModel;
    sorter = new TableSorter(columnsTableModel);
    allColumnsTable.setModel(sorter);
    sorter.addMouseListenerToHeaderInTable(allColumnsTable);
  }

  /**
   * Gets the model used in the available columns <CODE>JTable</CODE>.
   * 
   * @return The model used in the available columns <CODE>JTable</CODE>.
   */
  public ColumnSelectTableModel getColumnsTableModel()
  {
    return columnsTableModel;
  }
  
  /**
   * Gets the columns selected in the dialog.
   * 
   * @return <CODE>ArrayList</CODE> containing the names of the columns selected.
   */
  public ArrayList getSelectedColumns()
  {
    ArrayList columns = new ArrayList();
    int count = selectedColumnsListModel.size();
    for(int i=0;i<count;i++)
      columns.add(selectedColumnsListModel.elementAt(i));
    return columns;
  }

  /**
   * Called when the cancel button is clicked. This method sets the value of the
   * result property to <CODE>CANCEL</CODE> and hides the dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void cancelButton_actionPerformed(ActionEvent e)
  {
    result = CANCEL;
    setVisible(false);
  }

  /**
   * Called when the window is closed by the user clicking on the X button in
   * the title bar.
   * 
   * @param e The <CODE>WindowEvent</CODE> that caused the invocation of this method.
   */
  private void this_windowClosing(WindowEvent e)
  {
    result = CANCEL;
    setVisible(false);
  }

  /**
   * Called when the cancel button is clicked. This method sets the value of the
   * result property to <CODE>CANCEL</CODE> and hides the dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void okButton_actionPerformed(ActionEvent e)
  {
    result = OK;
    setVisible(false);
  }

  /**
   * Enables and disables the buttons in the middle of the interface. The single
   * arrow buttons are only enabled when something that can be moved with them 
   * is selected, and the double arrow buttons are enabled as long as there is 
   * atleast one item that can be moved with them is present. This method is 
   * called when the items selected or the number of items in either table 
   * changes. 
   */
  private void enableButtons()
  {
    int listSize = selectedColumnsListModel.getSize();
    dropSelectedButton.setEnabled(selectedColumnsList.getSelectedIndices().length > 0);
    dropAllButton.setEnabled(listSize > 0);
    addSelectedButton.setEnabled(allColumnsTable.getSelectedRowCount() > 0);
    addAllButton.setEnabled(allColumnsTable.getRowCount() > 0);
    boolean enableOK = false;
    if(listSize == originalColumns.length)
    {
      for(int i=0;i<originalColumns.length;i++)
        if(! selectedColumnsListModel.contains(originalColumns[i]))
        {
          enableOK = true;
          break;
        }
    }
    else
      enableOK = true;
    okButton.setEnabled(enableOK);
  }

  /**
   * Drops the selected field IDs from the selected list, and adds them to the
   * table containing all columns.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void dropSelectedButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      dropColumns(selectedColumnsList.getSelectedValues());
      enableButtons();
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  /**
   * Drops all field IDs from the selected list, and adds them to the table 
   * containing all columns.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void dropAllButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      Object[] columns = new Object[selectedColumnsListModel.size()];
      for(int i=0;i<columns.length;i++)
        columns[i] = selectedColumnsListModel.get(i);
      dropColumns(columns);
      enableButtons();
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  /**
   * Moves the fields selected in the table to the list.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void addSelectedButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      selectColumns(getColumnsSelectedInTable());
      enableButtons();
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  /**
   * Gets the columns selected in the table.
   * 
   * @return The columns currently selected in the table.
   */
  private Object[] getColumnsSelectedInTable()
  {
    int[] selectedIndexes = allColumnsTable.getSelectedRows();
    Object[] selectedColumns = new Object[selectedIndexes.length];
    for(int i=0;i<selectedIndexes.length;i++)
    {
      int modelIndex = sorter.getModelRowNumber(selectedIndexes[i]);
      selectedColumns[i] = columnsTableModel.getColumnAtRow(modelIndex);
    }
    return selectedColumns;
  }

  /**
   * Moves all of the fields in the table to the list.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void addAllButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      Object[] columns = new Object[columnsTableModel.getRowCount()];
      for(int i=0;i<columns.length;i++)
        columns[i] = columnsTableModel.getColumnAtRow(i);
      selectColumns(columns);
      enableButtons();
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
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
   * Sets the columns selected in the dialog. These items will appear in the
   * list regardless of what is currently in it or the table.
   * 
   * @param selectedColumns The columns selected in the dialog.
   */
  public void setSelectedColumns(ArrayList selectedColumns)
  {
    selectedColumnsListModel.removeAllElements();
    int count = selectedColumns.size();
    for(int i=0;i<count;i++)
      selectedColumnsListModel.addElement(selectedColumns.get(i));
  }

  /**
   * Sets the columns that will appear in the available table. These items will 
   * appear in the table regardless of what is currently in it or the list.
   * 
   * @param availableColumns The columns that are available to be added to the table.
   */
  public void setAvailableColumns(ArrayList availableColumns)
  {
    columnsTableModel.setColumns(availableColumns);
  }

  /**
   * Gets the columns in the available table.
   * 
   * @return The values currently in the table.
   */
  public ArrayList getAvailableColumns()
  {
    return columnsTableModel.getColumns();
  }

  /**
   * Selects the given columns from the available table.
   * 
   * @param columns The columns in the table to move to the list.
   */
  protected void selectColumns(Object[] columns)
  {
    addColumnsToList(removeColumnsFromTable(columns));
  }

  /**
   * Drops the given columns from the selected list.
   * 
   * @param columns The columns to move to the table.
   */
  protected void dropColumns(Object[] columns)
  {
    addColumnsToTable(removeColumnsFromList(columns));
  }

  /**
   * Removes the given rows from the available table. This method will convert 
   * the row numbers to the appropriate model rows.
   * 
   * @param columns The columns to remove from the table.
   * @return The columns that were removed from the table.
   */
  protected Object[] removeColumnsFromTable(Object[] columns)
  {
    return columnsTableModel.removeRows(columns);
  }

  /**
   * Removes the given columns from the selected list.
   * 
   * @param The columns to remove from the list.
   * @return The columns that were removed from the list.
   */
  protected Object[] removeColumnsFromList(Object[] columns)
  {
    ArrayList elementsRemoved = new ArrayList();
    for(int i=0;i<columns.length;i++)
      if(selectedColumnsListModel.removeElement(columns[i]))
        elementsRemoved.add(columns[i]);
    return elementsRemoved.toArray();
  }

  /**
   * Adds the given columns to the end of the table.
   * 
   * @param columns The columns to add to the table.
   */
  protected void addColumnsToTable(Object[] columns)
  {
    columnsTableModel.addRows(columns);
  }

  /**
   * Adds the columns to the end of the list.
   * 
   * @param columns The columns to add to the list.
   */
  protected void addColumnsToList(Object[] columns)
  {
    addColumnsToList(columns, selectedColumnsListModel.getSize());
  }

  /**
   * Adds the columns to the list at the given index.
   * 
   * @param columns The columns to add to the list.
   * @param index The index in the list at which to insert them.
   */
  protected void addColumnsToList(Object[] columns, int index)
  {
    for(int i=0;i<columns.length;i++)
      selectedColumnsListModel.add(index + i, columns[i]);
  }

  /**
   * This class handles drag and drop operations between the components on the 
   * interface.
   * 
   * @author Chris Fowlkes
   */
  private class ColumnSelectDialogTransferHandler extends TransferHandler 
  {
    /**
     * Creates a new <CODE>ColumnFindDialogTransferHandler</CODE>.
     */
    public ColumnSelectDialogTransferHandler()
    {
    }

    /**
     * Gets the source action for the drag operation. Ths method always returns
     * <CODE>TransferHandler.MOVE</CODE>.
     * 
     * @return The source action for drag and drop operations.
     */
    public int getSourceActions(JComponent c)
    {
      return TransferHandler.MOVE;
    }

    /**
     * Determines if the given <CODE>Component</CODE> can import one of the 
     * given flavors.
     * 
     * @param comp The <CODE>Component</CODE> on which the drop will be performed.
     * @param transferFlavors The instances of <CODE>DataFlavor</CODE> that will be dropped on the component.
     */
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors)
    {
      for(int i=0;i<transferFlavors.length;i++)
        if(transferFlavors[i] == columnFlavor)
          return true;
      return false;
    }

    /**
     * Creates the <CODE>Transferable</CODE>.
     * 
     * @param c The component on which the drag operation was initiated.
     */
    protected Transferable createTransferable(JComponent c)
    {
      Transferable value;
      if(c == selectedColumnsList)
        value = new ColumnSelectDialog.TransferableColumns(selectedColumnsList.getSelectedValues());
      else
        value = new ColumnSelectDialog.TransferableColumns(getColumnsSelectedInTable());
      return value;
    }

    /**
     * Imports the data that was drug into the given component.
     * 
     * @param comp The component onto which the drop occured.
     * @param t The <CODE>Transferable</CODE> that contained the data being drug.
     */
    public boolean importData(JComponent comp, Transferable t)
    {
      try
      {
        Object[] columns = (Object[])t.getTransferData(columnFlavor);
        if(comp == selectedColumnsList)
          addColumnsToList(columns, selectedColumnsList.getSelectedIndex());
        else
          addColumnsToTable(columns);
      }
      catch(java.io.IOException ex)
      {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(ColumnSelectDialog.this, ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      catch(java.awt.datatransfer.UnsupportedFlavorException ex)
      {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(ColumnSelectDialog.this, "Can not drag or drop items of type " + columnFlavor.getHumanPresentableName() + ".", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      return true;
    }

    /**
     * Called when the transfer is done. This method removes the transfered
     * data from the source component.
     * 
     * @param source Component from which the data was drug.
     * @param data The data invovied in the drag and drop operation.
     * @param action The drag and drop action performed.
     */
    protected void exportDone(JComponent source, Transferable data, int action)
    {
      if(action == TransferHandler.MOVE)
        try
        {
          Object[] columns = (Object[])data.getTransferData(columnFlavor);
          if(source == selectedColumnsList)
            removeColumnsFromList(columns);
          else
            removeColumnsFromTable(columns);
        }
        catch(java.io.IOException ex)
        {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(ColumnSelectDialog.this, ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
        }
        catch(java.awt.datatransfer.UnsupportedFlavorException ex)
        {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(ColumnSelectDialog.this, "Can not remove items of type " + columnFlavor.getHumanPresentableName() + ".", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
  }

  /**
   * Provides a class that holds the columns being transfered.
   * 
   * @author Chris Fowlkes
   */
  private class TransferableColumns implements Transferable
  {
    private Object[] transferData;
    
    /**
     * Creates a new <CODE>TransferableColumns</CODE>.
     */
    public TransferableColumns()
    {
    }
    
    /**
     * Creates a new <CODE>TransferableColumns</CODE>.
     * 
     * @param newTransferData The data being transferred.
     */
    public TransferableColumns(Object[] newTranferData)
    {
      this();
      setTransferData(newTranferData);
    }

    /**
     * Gets the instances of <CODE>DataFlavor</CODE> supported.
     * 
     * @return The instances of <CODE>DataFlavor</CODE> supported.
     */
    public DataFlavor[] getTransferDataFlavors()
    {
      return new DataFlavor[]{columnFlavor};
    }

    /**
     * Tests the given <CODE>DataFlavor</CODE> to see if it is supported.
     * 
     * @param flavor The <CODE>DataFlavor</CODE> to check for support.
     */
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
      return flavor == columnFlavor;
    }

    /**
     * Gets the transfer data.
     * 
     * @param flavor The <CODE>DataFlavor</CODE> to use when getting the data.
     * @throws java.awt.datatransfer.UnsupportedFlavorException Thrown if the given <CODE>DataFlavor</CODE> is not supported.
     */
    public Object getTransferData(DataFlavor flavor) throws java.awt.datatransfer.UnsupportedFlavorException
    {
      if(! isDataFlavorSupported(flavor))
        throw new java.awt.datatransfer.UnsupportedFlavorException(flavor);
      return transferData;
    }

    /**
     * Sets the data being transferred.
     * 
     * @param newTransferData The data to transfer.
     */
    public void setTransferData(Object[] newTransferData)
    {
      transferData = newTransferData;
    }
  }
}