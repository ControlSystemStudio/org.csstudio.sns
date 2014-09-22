package org.csstudio.mps.sns.apps.mpsbrowser;
import org.csstudio.mps.sns.IconLoader;
import org.csstudio.mps.sns.MainFrame;

import org.csstudio.mps.sns.application.JeriInternalFrame;
import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.EpicsSubsystem;
import org.csstudio.mps.sns.tools.data.EpicsSystem;
import org.csstudio.mps.sns.tools.data.MPSBoard;
import org.csstudio.mps.sns.tools.data.MPSChain;
import org.csstudio.mps.sns.tools.data.MPSChannel;
import org.csstudio.mps.sns.tools.data.MPSChassis;
import org.csstudio.mps.sns.tools.database.swing.DatabaseToolBar;
import org.csstudio.mps.sns.tools.swing.ColumnFindDialog;
import org.csstudio.mps.sns.tools.swing.ColumnSelectDialog;
import org.csstudio.mps.sns.tools.swing.TableSearcher;
import org.csstudio.mps.sns.tools.swing.autocompletecombobox.ComboBoxCellEditor;
import org.csstudio.mps.sns.tools.swing.treetable.JTreeTable;
import org.csstudio.mps.sns.tools.swing.treetable.TreeTableModel;
import org.csstudio.mps.sns.tools.swing.treetable.TreeTableModelAdapter;
import org.csstudio.mps.sns.tools.swing.treetable.TreeTableSearcher;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeEvent;

import java.io.*;

import java.math.*;

import java.sql.*;

import java.util.*;
import java.util.List;
import java.util.regex.*;

import javax.sql.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.*;
import javax.swing.tree.*;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.tools.swing.SwingWorker;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

/**
 * Provides an interface for the user to edit the MPS tables with.
 * 
 * @author Chris Fowlkes
 */
public class MPSBrowserFrame extends JeriInternalFrame
{
  private DatabaseToolBar toolBar = new DatabaseToolBar();
  private JTabbedPane tabbedPane = new JTabbedPane();
  private JScrollPane deviceScrollPane = new JScrollPane();
  private JScrollPane detailsScrollPane = new JScrollPane();
  private MPSDetailsTableModel detailsTableModel = new MPSDetailsTableModel();
  private JTreeTable detailsTable = new MPSBrowserFrame.TextReplaceTreeTable(detailsTableModel);
  private JTable deviceTable = new MPSBrowserFrame.TextReplaceTable();
  private JComboBox chainList = new JComboBox();
  private BoardTableModel deviceTableModel = new BoardTableModel();
  private JComboBox mpsDeviceEditor = new JComboBox();
  private JComboBox chainEditor = new JComboBox();
  private JComboBox serialNumberEditor = new JComboBox();
  private JComboBox chassisEditor = new JComboBox();
  private JComboBox fparFPLConfigEditor = new JComboBox();
  private JPanel statusBarPanel = new JPanel();
  private BorderLayout statusBarPanelLayout = new BorderLayout();
  private JLabel progressLabel = new JLabel();
  private JProgressBar progressBar = new JProgressBar();
  private JPopupMenu popupMenu = new JPopupMenu();
  private JMenuItem expandAllPopupMenuItem = new JMenuItem();
  private JMenuItem exportPopupMenuItem = new JMenuItem();
  private JMenuItem columnsPopupMenuItem = new JMenuItem();
  /**
   * Holds the dialog used to hide or show columns.
   */
  private ColumnSelectDialog columnsDialog;
  private JMenuItem collapseAllPopupMenuItem = new JMenuItem();
  private JMenuItem duplicatePopupMenuItem = new JMenuItem();
  private JCheckBox filterCheckBox = new JCheckBox();
  /**
   * Holds the name of the chain last selected in the combo box. This should 
   * only be used to determine if the value has changed. To get the chain 
   * currently selected, get the selected item from the combo box.
   */
  private Object currentChain;
  /**
   * Holds the device ID cell editor.
   */
  private MPSBrowserFrame.MPSDeviceComboBoxCellEditor mpsDeviceCellEditor;
  private JMenuItem resetToDefaultsPopupMenuItem = new JMenuItem();
  private JMenuItem setToZerosPopupMenuItem = new JMenuItem();
  private JMenuItem setToOnesPopupMenuItem = new JMenuItem();
  private JMenuItem saveAsPopupMenuItem = new JMenuItem();
  /**
   * Holds the <CODE>JFileChooser</CODE> used when the save as pop up menu item
   * is clicked.
   */
  private JFileChooser saveDialog;
  private MPSBrowser mpsBrowser = new MPSBrowser();
  /**
   * Used to determine if a secondary thread is executing.
   */
  private boolean threadActive = false;
  /**
   * Holds the index of the last row selected in the detail table. Used to 
   * determine if a post is needed.
   */
  private int lastDetailRowSelected = -1;
  private JMenuBar menuBar = new JMenuBar();
  private JMenu treeMenu = new JMenu();
  private JMenuItem expandAllMenuItem = new JMenuItem();
  private JMenuItem collapseAllMenuItem = new JMenuItem();
  private JMenu maskMenu = new JMenu();
  private JMenuItem exportMenuItem = new JMenuItem();
  private JMenuItem columnsMenuItem = new JMenuItem();
  private JMenuItem duplicateMenuItem = new JMenuItem();
  private JMenuItem resetToDefaultsMenuItem = new JMenuItem();
  private JMenuItem setToZerosMenuItem = new JMenuItem();
  private JMenuItem setToOnesMenuItem = new JMenuItem();
  private JMenuItem saveAsMenuItem = new JMenuItem();
  private JMenu tableMenu = new JMenu();
  private JMenuItem searchMenuItem = new JMenuItem();
  private JButton searchChainButton = new JButton();
  private JPanel toolBarPanel = new JPanel();
  private BorderLayout toolBarPanelLayout = new BorderLayout();
  private JPanel searchPanel = new JPanel();
  private BorderLayout searchPanelLayout = new BorderLayout();
  private JComboBox searchFieldComboBox = new JComboBox();
  private JTextField searchTextField = new JTextField();
  private JButton searchButton = new JButton();
  private JButton auditButton = new JButton();

  /**
   * Creates a new <CODE>MPSBrowserFrame</CODE>.
   */
  public MPSBrowserFrame()
  {
    try
    {
      jbInit();
      searchChainButton.setIcon(IconLoader.getTableSearchIcon());
      auditButton.setIcon(IconLoader.getCheckAllIcon());
      JTree tree = detailsTable.getTree();
      DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)tree.getCellRenderer();
      renderer.setClosedIcon(null);
      renderer.setOpenIcon(null);
      renderer.setLeafIcon(null);
      deviceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          deviceTable_selectionChanged(e);
        }
      });      
      deviceTable.getColumnModel().addColumnModelListener(new TableColumnModelListener()
      {
        public void columnAdded(TableColumnModelEvent e)
        {
        }

        public void columnMarginChanged(ChangeEvent e)
        {
        }

        public void columnMoved(final TableColumnModelEvent e)
        {
          saveColumnPositions(deviceTable);
        }

        public void columnRemoved(TableColumnModelEvent e)
        {
        }

        public void columnSelectionChanged(ListSelectionEvent e)
        {
        }
      });
      detailsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          detailsTable_selectionChanged(e);
        }
      });
      detailsTable.getModel().addTableModelListener(new TableModelListener()
      {
        public void tableChanged(TableModelEvent e)
        {
          tableModel_tableChanged(e);
        }
      });
      detailsTable.getColumnModel().addColumnModelListener(new TableColumnModelListener()
      {
        public void columnAdded(TableColumnModelEvent e)
        {
        }

        public void columnMarginChanged(ChangeEvent e)
        {
        }

        public void columnMoved(final TableColumnModelEvent e)
        {
          saveColumnPositions(detailsTable);
        }

        public void columnRemoved(TableColumnModelEvent e)
        {
        }

        public void columnSelectionChanged(ListSelectionEvent e)
        {
        }
      });
      mpsDeviceCellEditor = new MPSBrowserFrame.MPSDeviceComboBoxCellEditor(mpsDeviceEditor);
      detailsTableModel.setMPSDeviceEditor(mpsDeviceCellEditor);
      detailsTable.setDefaultEditor(Device.class, mpsDeviceCellEditor);
      detailsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      detailsTable.setRowHeight(mpsDeviceEditor.getMinimumSize().height);
      detailsTable.setIntercellSpacing(new Dimension(1, 1));
      ((DefaultCellEditor)detailsTable.getDefaultEditor(Object.class)).setClickCountToStart(1);
      tree.putClientProperty("JTree.lineStyle","None");
      tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
      tree.setRootVisible(false);
      tree.setShowsRootHandles(true);
      deviceTable.setDefaultEditor(MPSChain.class, new ComboBoxCellEditor(chainEditor));
      ComboBoxCellEditor serialNumberCellEditor = new ComboBoxCellEditor(serialNumberEditor);
      deviceTableModel.setSerialNumberEditor(serialNumberCellEditor);
      deviceTable.setDefaultEditor(Number.class, serialNumberCellEditor);
      deviceTable.setDefaultEditor(MPSChassis.class, new ComboBoxCellEditor(chassisEditor));
      fparFPLConfigEditor.addItem(null);
      fparFPLConfigEditor.addItem("16FPAR");
      fparFPLConfigEditor.addItem("16L");
      fparFPLConfigEditor.addItem("8L8AR");
      deviceTable.setDefaultEditor(String.class, new ComboBoxCellEditor(fparFPLConfigEditor));
      deviceTable.setRowHeight(chainEditor.getMinimumSize().height);
      deviceTableModel.addTableModelListener(new TableModelListener()
      {
        public void tableChanged(TableModelEvent e)
        {
          tableModel_tableChanged(e);
        }
      });
      detailsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
      {
        public Component getTableCellRendererComponent(JTable table,
                                               Object value,
                                               boolean isSelected,
                                               boolean hasFocus,
                                               int row,
                                               int column)      
        {
          JTree tree = detailsTable.getTree();
        	TreePath treePath = tree.getPathForRow(row);
          Object rowObject = treePath.getLastPathComponent();         
          JLabel renderer = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
          if(rowObject != null && rowObject instanceof MPSChannel)
          {
            MPSChannel currentChannel = (MPSChannel)rowObject;
            StringBuffer tip = new StringBuffer("<HTML><B>Device ID:</B> ");
            tip.append(currentChannel.getBoard().getID());
            Device currentDevice = currentChannel.getDevice();
            if(currentDevice != null)
            {
              tip.append("<BR><B>Device ID:</B> ");
              tip.append(currentDevice.getID());
            }
            tip.append("<BR><B>Channel: </B>");
            tip.append(currentChannel.getNumber());
            tip.append("</HTML>");
            renderer.setToolTipText(tip.toString());
          }
          else
            renderer.setToolTipText(null);
          return renderer;
        }
      });
      pack();
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
    this.setJMenuBar(menuBar);
    this.setSize(new Dimension(562, 300));
    this.addVetoableChangeListener(new VetoableChangeListener()
      {
        public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException
        {
          this_vetoableChange(e);
        }
      });
    tabbedPane.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          tabbedPane_stateChanged(e);
        }
      });
    detailsTableModel.setMPSBrowser(mpsBrowser);
    detailsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    detailsTable.setShowHorizontalLines(true);
    detailsTable.setShowVerticalLines(true);
    detailsTable.setFont(new Font("Monospaced", 0, 11));
    detailsTable.addKeyListener(new KeyAdapter()
      {
        public void keyPressed(KeyEvent e)
        {
          detailsTable_keyPressed(e);
        }
      });
    detailsTable.addMouseListener(new MouseAdapter()
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
    deviceTable.setModel(deviceTableModel);
    deviceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    deviceTable.addMouseListener(new MouseAdapter()
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
    chainList.addItemListener(new ItemListener()
      {
        public void itemStateChanged(ItemEvent e)
        {
          chainList_itemStateChanged(e);
        }
      });
    chainEditor.addItemListener(new ItemListener()
      {
        public void itemStateChanged(ItemEvent e)
        {
          chainEditor_itemStateChanged(e);
        }
      });
    statusBarPanel.setLayout(statusBarPanelLayout);
    progressLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    progressLabel.setText(" ");
    progressBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    popupMenu.setLabel("jPopupMenu1");
    expandAllPopupMenuItem.setText("Expand All");
    expandAllPopupMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          expandAllPopupMenuItem_actionPerformed(e);
        }
      });
    exportPopupMenuItem.setText("Export...");
    exportPopupMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          exportPopupMenuItem_actionPerformed(e);
        }
      });
    columnsPopupMenuItem.setText("Select Columns...");
    columnsPopupMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          columnsPopupMenuItem_actionPerformed(e);
        }
      });
    collapseAllPopupMenuItem.setText("Collapse All");
    collapseAllPopupMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          collapseAllPopupMenuItem_actionPerformed(e);
        }
      });
    duplicatePopupMenuItem.setText("Duplicate...");
    duplicatePopupMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          duplicatePopupMenuItem_actionPerformed(e);
        }
      });
    filterCheckBox.setText("Filter MPS Device IDs");
    filterCheckBox.setSelected(true);
    filterCheckBox.setMnemonic('I');
    resetToDefaultsPopupMenuItem.setText("Reset to Defaults");
    resetToDefaultsPopupMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          resetToDefaultsPopupMenuItem_actionPerformed(e);
        }
      });
    setToZerosPopupMenuItem.setText("Set to Zeros");
    setToZerosPopupMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          setToZerosPopupMenuItem_actionPerformed(e);
        }
      });
    setToOnesPopupMenuItem.setText("Set to Ones");
    setToOnesPopupMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          setToOnesPopupMenuItem_actionPerformed(e);
        }
      });
    saveAsPopupMenuItem.setText("Save As...");
    saveAsPopupMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          saveAsPopupMenuItem_actionPerformed(e);
        }
      });
    mpsBrowser.setMessageLabel(progressLabel);
    mpsBrowser.setProgressBar(progressBar);
    treeMenu.setText("Tree");
    treeMenu.setMnemonic('e');
    expandAllMenuItem.setText("Expand All");
    expandAllMenuItem.setMnemonic('x');
    expandAllMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          expandAllMenuItem_actionPerformed(e);
        }
      });
    collapseAllMenuItem.setText("Collapse All");
    collapseAllMenuItem.setMnemonic('C');
    collapseAllMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          collapseAllMenuItem_actionPerformed(e);
        }
      });
    maskMenu.setText("Mask");
    maskMenu.setMnemonic('M');
    exportMenuItem.setText("Export...");
    exportMenuItem.setMnemonic('x');
    exportMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          exportMenuItem_actionPerformed(e);
        }
      });
    columnsMenuItem.setText("Select Columns...");
    columnsMenuItem.setMnemonic('C');
    columnsMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          columnsMenuItem_actionPerformed(e);
        }
      });
    duplicateMenuItem.setText("Duplicate...");
    duplicateMenuItem.setMnemonic('D');
    duplicateMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          duplicateMenuItem_actionPerformed(e);
        }
      });
    resetToDefaultsMenuItem.setText("Reset To Defaults");
    resetToDefaultsMenuItem.setMnemonic('R');
    resetToDefaultsMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          resetToDefaultsMenuItem_actionPerformed(e);
        }
      });
    setToZerosMenuItem.setText("Set to Zeros");
    setToZerosMenuItem.setMnemonic('Z');
    setToZerosMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          setToZerosMenuItem_actionPerformed(e);
        }
      });
    setToOnesMenuItem.setText("Set to Ones");
    setToOnesMenuItem.setMnemonic('O');
    setToOnesMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          setToOnesMenuItem_actionPerformed(e);
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
    tableMenu.setText("Table");
    tableMenu.setMnemonic('T');
    searchMenuItem.setText("Search...");
    searchMenuItem.setMnemonic('S');
    searchMenuItem.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent e) 
      {
        searchMenuItem_actionPerformed(e);
      }
    });
    searchChainButton.setMargin(new Insets(2, 2, 2, 2));
    searchChainButton.setToolTipText("Search");
    searchChainButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          searchChainButton_actionPerformed(e);
        }
      });
    toolBarPanel.setLayout(toolBarPanelLayout);
    searchPanel.setLayout(searchPanelLayout);
    searchPanelLayout.setHgap(5);
    searchTextField.setText("Use '*' as a wildcard for searching.");
    searchTextField.addFocusListener(new FocusAdapter()
      {
        public void focusGained(FocusEvent e)
        {
          searchTextField_focusGained(e);
        }
      });
    searchButton.setText("Go To");
    searchButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          searchButton_actionPerformed(e);
        }
      });
    auditButton.setMargin(new Insets(2, 2, 2, 2));
    auditButton.setToolTipText("Audit Results");
    auditButton.setEnabled(false);
    auditButton.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e)
                                    {
                                      auditButton_actionPerformed(e);
                                    }
                                  }
    );
    statusBarPanel.add(progressLabel, BorderLayout.CENTER);
    statusBarPanel.add(progressBar, BorderLayout.EAST);
    toolBar.add(auditButton, null);
    toolBar.add(searchChainButton, null);
    toolBar.add(filterCheckBox, null);
    toolBar.add(chainList, null);
    toolBar.setCommitButtonEnabled(false);
    toolBar.addCommitActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e)
                                      {
                                        commitButton_actionPerformed(e);
                                      }
                                    }
    );
    toolBar.setRollbackButtonEnabled(false);
    toolBar.addRollbackActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent e)
                                        {
                                          rollbackButton_actionPerformed(e);
                                        }
                                      }
    );
    toolBar.setFirstButtonEnabled(false);
    toolBar.addFirstActionListener(new ActionListener() {
                                     public void actionPerformed(ActionEvent e)
                                     {
                                       firstButton_actionPerformed(e);
                                     }
                                   }
    );
    toolBar.setPriorButtonEnabled(false);
    toolBar.addPriorActionListener(new ActionListener() {
                                     public void actionPerformed(ActionEvent e)
                                     {
                                       priorButton_actionPerformed(e);
                                     }
                                   }
    );
    toolBar.setNextButtonEnabled(false);
    toolBar.addNextActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e)
                                    {
                                      nextButton_actionPerformed(e);
                                    }
                                  }
    );
    toolBar.setLastButtonEnabled(false);
    toolBar.addLastActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e)
                                    {
                                      lastButton_actionPerformed(e);
                                    }
                                  }
    );
    toolBar.addInsertActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e)
                                      {
                                        insertButton_actionPerformed(e);
                                      }
                                    }
    );
    toolBar.setRemoveButtonEnabled(false);
    toolBar.addRemoveActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e)
                                      {
                                        deleteButton_actionPerformed(e);
                                      }
                                    }
    );
    toolBar.setPostButtonEnabled(false);
    toolBar.addPostActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e)
                                    {
                                      postButton_actionPerformed(e);
                                    }
                                  }
    );
    toolBar.setCancelButtonEnabled(false);
    toolBar.addCancelActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e)
                                      {
                                        cancelButton_actionPerformed(e);
                                      }
                                    }
    );
    toolBar.addRefreshActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e)
                                       {
                                         refreshButton_actionPerformed(e);
                                       }
                                     }
    );
    deviceScrollPane.getViewport().add(deviceTable, null);
    tabbedPane.addTab("Devices", deviceScrollPane);
    detailsScrollPane.getViewport().add(detailsTable, null);
    tabbedPane.addTab("Details", detailsScrollPane);
    this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    this.getContentPane().add(statusBarPanel, BorderLayout.SOUTH);
    toolBarPanel.add(toolBar, BorderLayout.NORTH);
    searchPanel.add(searchFieldComboBox, BorderLayout.WEST);
    searchPanel.add(searchTextField, BorderLayout.CENTER);
    searchPanel.add(searchButton, BorderLayout.EAST);
    toolBarPanel.add(searchPanel, BorderLayout.SOUTH);
    this.getContentPane().add(toolBarPanel, BorderLayout.NORTH);
    popupMenu.add(expandAllPopupMenuItem);
    popupMenu.add(collapseAllPopupMenuItem);
    popupMenu.add(exportPopupMenuItem);
    popupMenu.add(columnsPopupMenuItem);
    popupMenu.add(duplicatePopupMenuItem);
    popupMenu.addSeparator();
    popupMenu.add(resetToDefaultsPopupMenuItem);
    popupMenu.add(setToZerosPopupMenuItem);
    popupMenu.add(setToOnesPopupMenuItem);
    popupMenu.addSeparator();
    popupMenu.add(saveAsPopupMenuItem);
    menuBar.add(tableMenu);
    treeMenu.add(expandAllMenuItem);
    treeMenu.add(collapseAllMenuItem);
    maskMenu.add(resetToDefaultsMenuItem);
    maskMenu.add(setToZerosMenuItem);
    maskMenu.add(setToOnesMenuItem);
    menuBar.add(treeMenu);
    menuBar.add(maskMenu);
    tableMenu.add(searchMenuItem);
    tableMenu.addSeparator();
    tableMenu.add(columnsMenuItem);
    tableMenu.add(exportMenuItem);
    tableMenu.add(duplicateMenuItem);
    tableMenu.addSeparator();
    tableMenu.add(saveAsMenuItem);
  }

  /**
   * Called when the commit button is clicked. This method commits the changes 
   * to the database. This function calls the <CODE>commit()</CODE> method to do 
   * the work.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void commitButton_actionPerformed(ActionEvent e)
  {
    try
    {
      commit();
    }
    catch(SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }
    enableButtons();
  }

  /**
   * Commits all changes made to the data.
   * 
   * @throws java.sql.SQLExceptin Thrown on sql error.
   */
  private void commit() throws SQLException
  {
    if(detailsTableModel.isPostNeeded() || deviceTableModel.isPostNeeded())
      post();
    mpsBrowser.commit();
  }
  
  /**
   * Called when the rollback button is clicked. This method cancels the changes 
   * posted to the database.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void rollbackButton_actionPerformed(ActionEvent e)
  {
    try
    {
      rollback(true);
    }
    catch(SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }
    enableButtons();
  }

  /**
   * Does a rollback on any pending changes.
   * 
   * @param reloadData Pass as <CODE>true</CODE> to reload the data after the rollback, <CODE>false</CODE> to skip the reload.
   */
  private void rollback(boolean reloadData) throws SQLException
  {
    mpsBrowser.rollback();
    if(reloadData)    
      reloadData();
  }

  /**
   * Determines which of the two tables is visible and returns it.
   * 
   * @return The visible <CODE>JTable</CODE>.
   */
  private JTable findSelectedTable()
  {
    if(tabbedPane.getSelectedComponent() == deviceScrollPane)
      return deviceTable;
    else
      return detailsTable;
  }
  
  /**
   * Called when the first button is clicked. This method selects the first row
   * in the visible table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void firstButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      selectAndShowRow(0, 0, findSelectedTable());
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  /**
   * Called when the prior button is clicked. This method selects the row before 
   * the row that is currently selected.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void priorButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      int row = findSelectedTable().getSelectionModel().getMinSelectionIndex() - 1;
      selectAndShowRow(row, row, findSelectedTable());
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  /**
   * Called when the next button is clicked. This method selects the row after
   * the row that is currently selected.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void nextButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      int row = findSelectedTable().getSelectionModel().getMaxSelectionIndex() + 1;
      selectAndShowRow(row, row, findSelectedTable());
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  /**
   * Called when the last button is clicked. This method selects the last row
   * in the visible table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void lastButton_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      int row = findSelectedTable().getRowCount() - 1;
      selectAndShowRow(row, row, findSelectedTable());
    }    
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  /**
   * Called when the insert button is clicked. This method allows the user to
   * insert an MPS record.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void insertButton_actionPerformed(ActionEvent e)
  {
    MPSBoard newBoard = new MPSBoard();
    newBoard.setInDatabase(false);
    deviceTableModel.addBoard(newBoard);
    detailsTableModel.addBoard(newBoard);
    enableButtons();
  }

  /**
   * Called when the delete button is clicked. This method deletes the selected
   * MPS record.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void deleteButton_actionPerformed(ActionEvent e)
  {
    Thread deleteThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(mpsBrowser)
        {
          try
          {
            threadActive = true;
            enableButtons();
            setMessage("Deleteing Records");
            setProgressIndeterminate(true);
            int[] selectedRows = deviceTable.getSelectedRows();
            MPSBoard[] selectedBoards = new MPSBoard[selectedRows.length];
            for(int i=0;i<selectedRows.length;i++) 
              selectedBoards[i] = deviceTableModel.getBoardAt(selectedRows[i]);
            setProgressMaximum(selectedBoards.length);
            try
            {
              setProgressValue(0);
              setProgressIndeterminate(false);
              for(int i=0;i<selectedBoards.length;i++) 
              {
                if(selectedBoards[i].isInDatabase())
                {
                  //Need to try and delete from the database first.
                  mpsBrowser.deleteChannels(selectedBoards[i]);
                  detailsTableModel.removeBoard(selectedBoards[i]);
                  mpsBrowser.deleteBoard(selectedBoards[i]);
                  deviceTableModel.removeBoard(selectedBoards[i]);
                }
                else
                {
                  detailsTableModel.removeBoard(selectedBoards[i]);
                  deviceTableModel.removeBoard(selectedBoards[i]);
                }
                setProgressValue(i + 1);
              }
            }
            catch(SQLException ex)
            {
              ex.printStackTrace();
              JOptionPane.showMessageDialog(MPSBrowserFrame.this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            }
          }
          finally
          {
            threadActive = false;
            enableButtons();
            setMessage(" ");
            setProgressValue(0);
            setProgressIndeterminate(false);
          }
        }
      }
    });
    deleteThread.start();
  }

  /**
   * Called when the post button is clicked. This method posts changes made to 
   * the local data to the database.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void postButton_actionPerformed(ActionEvent e)
  {
    post();
  }

  /**
   * Called when the cancel button is clicked. This method cancels changes made
   * to the local data.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void cancelButton_actionPerformed(ActionEvent e)
  {
    cancel();
  }

  /**
   * Called when the refresh button is clicked. This method reloads the data 
   * from the database.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void refreshButton_actionPerformed(ActionEvent e)
  {
    reloadData();
  }

  /**
   * Sets the <CODE>DataSource</CODE> used by the interface to connect to the database.
   * 
   * @param connectionPool The <CODE>DataSource</CODE> used to connect to the database.
   * @throws SQLException Thrown on sql error.
   */
  public void setDataSource(DataSource connectionPool) throws SQLException
  {
    super.setDataSource(connectionPool);
    detailsTableModel.setDataSource(connectionPool);
    deviceTableModel.setDataSource(connectionPool);
    mpsBrowser.setConnection(connectionPool.getConnection());
    Thread chainLoadThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(mpsBrowser)
        {
          try
          {
            threadActive = true;
            enableButtons();
            final MPSChain[] allChains = mpsBrowser.loadChains();
            SwingUtilities.invokeLater(new Runnable()
            {
              public void run()
              {
                chainList.removeAllItems();
                chainList.addItem("All Chains");
                for(int i=0;i<allChains.length;i++) 
                {
                  chainList.addItem(allChains[i]);
                  chainEditor.addItem(allChains[i]);
                }
              }
            });
          }
          catch(SQLException ex)
          {
            ex.printStackTrace();
            showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
          }
          finally
          {
            threadActive = false;
            enableButtons();
          }
        }
      }
    });
    chainLoadThread.start();
  }

  /**
   * Determines if the data can be edited or not. <CODE>false</CODE> is default.
   * 
   * @param editable Pass as <CODE>true</CODE> to make the data editable.
   */
  public void setEditable(boolean editable)
  {
    deviceTableModel.setEditable(editable);
    detailsTableModel.setEditable(editable);
    toolBar.setEditButtonsVisible(editable);
    auditButton.setVisible(editable);
  }

  /**
   * Called when the state of the chain drop down list changes. This method 
   * filters the IOCs visible to only show the IOCs in the newly selected chain.
   * 
   * @param e The <CODE>ItemEvent</CODE> that caused the invocation of this method.
   */
  private void chainList_itemStateChanged(ItemEvent e)
  {
    if(e.getStateChange() == ItemEvent.SELECTED)
      try
      {
        Object newChain = e.getItem();
        if(currentChain == null || ! currentChain.equals(newChain))
          if(promptSaveChanges())
          {
            currentChain = newChain;
            reloadData();
          }
          else
            chainList.setSelectedItem(currentChain);//Go back to the last selected table.
      }
      catch(SQLException ex)
      {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
      }
  }

  /**
   * Returns the IDs of the instances of <CODE>MPSBoard</CODE> that are 
   * expanded.
   * 
   * @return The instances of <CODE>MPSBoard</CODE> that are expanded in the details tab.
   */
  private ArrayList findExpandedIOCs()
  {
    ArrayList expandedIOCs = new ArrayList();
    JTree tree = detailsTable.getTree();
    int rowCount = tree.getRowCount();
    for(int i=0;i<rowCount;i++)
      if(tree.isExpanded(i))
      {
        Object currentNode = tree.getPathForRow(i).getLastPathComponent();
        if(currentNode instanceof MPSBoard)
          expandedIOCs.add(((MPSBoard)currentNode).getID());
      }
    return expandedIOCs;
  }

  /**
   * Expands the rows for the given IDs.
   * 
   * @param iocs The IDs of the instances of <CODE>MPSBoard</CODE> for which to expand the rows.
   */
  private void expandIOCs(ArrayList iocs)
  {
    JTree tree = detailsTable.getTree();
    for(int i=tree.getRowCount()-1;i>=0;i--)
    {
      Object currentNode = tree.getPathForRow(i).getLastPathComponent();
      if(currentNode instanceof MPSBoard)
      {
        if(iocs.contains(((MPSBoard)currentNode).getID()))
          tree.expandRow(i);
      }
    }
  }
  
  /**
   * Reloads all of the data in the interface.
   */
  private void reloadData()
  {
    //First figure out what nodes are expanded so they can be expanded
    //programatically after the refresh.
    final ArrayList expandedIOCs = findExpandedIOCs();
    final JTree tree = detailsTable.getTree();
    Object selectedValue = chainList.getSelectedItem();
    final MPSChain selectedChain;
    if(selectedValue instanceof MPSChain)
      selectedChain = (MPSChain)selectedValue;
    else
      selectedChain = null;
    Thread dataLoadThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(mpsBrowser)
        {
          try
          {
            threadActive = true;
            enableButtons();
            if(mpsBrowser.getBinaryColumnNames() == null)
              setBinaryColumnNames(mpsBrowser.loadBinaryColumnNames());
            final MPSBoard[] allBoards = mpsBrowser.loadBoards(selectedChain);
            setTableBoards(allBoards);
            if(deviceTableModel.isEditable())
              setChassisEditorItems(mpsBrowser.loadIOCs());
            SwingUtilities.invokeLater(new Runnable()
            {
              public void run()
              {
                expandIOCs(expandedIOCs);
              }
            });
            if(detailsTableModel.isEditable())
            {
              setDevices(mpsBrowser.loadDeviceIDs());
              setSerialNumbers(mpsBrowser.loadSerialNumbers());
            }
          }
          catch(SQLException ex)
          {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(MPSBrowserFrame.this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
          }
          finally
          {
            threadActive = false;
            enableButtons();
          }
        }
      }
    });
    dataLoadThread.start();
  }

  /**
   * Sets the instances of <CODE>Device</CODE> that appear in the device cell 
   * editor. This method uses <CODE>SwingUtilities.invokeLater</CODE> to be 
   * thread safe.
   * 
   * @param devices The instances of <CODE>Device</CODE> to put in the device cell editor.
   */
  private void setDevices(final Device[] devices)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        mpsDeviceCellEditor.setDevices(devices);
      }
    });
  }
  
  /**
   * Sets the default items in the serial number editor. This method uses 
   * <CODE>SwingUtilities.invokeLater</CODE> to make it thread safe.
   * 
   * @param defaultItems The default items in the serial number editor.
   */
  private void setSerialNumbers(final ArrayList defaultItems)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        deviceTableModel.getSerialNumberEditor().setDefaultItems(defaultItems);
      }
    });
  }

  /**
   * This method uses <CODE>SwingUtilities.invokeLater</CODE> to pass the binary
   * column names to the details table. It also handles the firing of events.
   * 
   * @param binaryColumnNames The binary column names.
   */
  private void setBinaryColumnNames(final String[] binaryColumnNames)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        detailsTableModel.setBinaryColumnNames(binaryColumnNames);
        arrangeColumns(detailsTable);
        ((TreeTableModelAdapter)detailsTable.getModel()).fireTableStructureChanged();
      }
    });
  }
  
  /**
   * Uses <CODE>SwingUtilities.invokeLater</CODE> to set the items in the 
   * chassis column editor.
   * 
   * @param The instances of <CODE>MPSChassis</CODE> to appear in the drop down.
   */
  private void setChassisEditorItems(final MPSChassis[] allChassis)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        chassisEditor.removeAllItems();
        for(int i=0;i<allChassis.length;i++) 
          chassisEditor.addItem(allChassis[i]);
      }
    });
  }

  /**
   * Sets the instances of <CODE>MPSBoard</CODE> displayed in the table. This 
   * method uses <CODE>SwingUtilities.invokeLater</CODE> to be thread safe.
   * 
   * @param allBoards The instances of <CODE>MPSBoard</CODE> to display in the tables.
   */
  private void setTableBoards(final MPSBoard[] allBoards)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        deviceTableModel.setBoards(allBoards);
        detailsTableModel.setBoards(allBoards);
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
   * @param firstRow The number of the first row to select in the table.
   * @param lastRow The number of the last row to select in the table.
   * @param table The table in which to select and show the given rows.
   */
  protected void selectAndShowRow(int firstRow, int lastRow, JTable table)
  {
    table.getSelectionModel().setSelectionInterval(firstRow, lastRow);
    Rectangle visible = table.getVisibleRect();
    Rectangle firstCell = table.getCellRect(firstRow, 0, true);
    Rectangle lastCell = table.getCellRect(lastRow, 0, true);
    int height = lastCell.y + lastCell.height - firstCell.y;
    Rectangle scrollTo = new Rectangle(visible.x, firstCell.y, visible.width, height);
    table.scrollRectToVisible(scrollTo);
  }
  
  /**
   * Enables or disables the buttons on the tool bar based on the state of the 
   * table and the data in it.
   */
  private void enableButtons()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        //Enable commit and rollback buttons if data is changed.
        boolean postNeeded = detailsTableModel.isPostNeeded() || deviceTableModel.isPostNeeded();
        boolean commitNeeded = mpsBrowser.isCommitNeeded();
        toolBar.setCommitButtonEnabled(! threadActive && (commitNeeded || postNeeded));
        toolBar.setRollbackButtonEnabled(! threadActive && (commitNeeded || postNeeded));
        JTable selectedTable = findSelectedTable();
        int rowCount = selectedTable.getRowCount();
        ListSelectionModel selectionModel = selectedTable.getSelectionModel();
        toolBar.setFirstButtonEnabled(! threadActive && rowCount > 0 && ! selectionModel.isSelectedIndex(0));
        int firstSelectedRow = selectionModel.getMinSelectionIndex();
        toolBar.setPriorButtonEnabled(! threadActive && firstSelectedRow > 0);
        int lastSelectedRow = selectionModel.getMaxSelectionIndex();
        toolBar.setNextButtonEnabled(! threadActive && lastSelectedRow >= 0 && lastSelectedRow < rowCount - 1);
        toolBar.setLastButtonEnabled(! threadActive && rowCount > 0 && ! selectionModel.isSelectedIndex(rowCount - 1));
        toolBar.setInsertButtonEnabled(! threadActive);
        toolBar.setRemoveButtonEnabled(! threadActive && selectedTable == deviceTable && ! selectionModel.isSelectionEmpty());
        toolBar.setPostButtonEnabled(! threadActive && postNeeded);
        toolBar.setCancelButtonEnabled(! threadActive && postNeeded);
        toolBar.setRefreshButtonEnabled(! threadActive);
        filterCheckBox.setEnabled(! threadActive);
        chainList.setEnabled(! threadActive);
        deviceTable.setEnabled(! threadActive);
        detailsTable.setEnabled(! threadActive);
        expandAllPopupMenuItem.setEnabled(! threadActive && selectedTable == detailsTable);
        expandAllMenuItem.setEnabled(! threadActive && selectedTable == detailsTable);
        collapseAllPopupMenuItem.setEnabled(! threadActive && selectedTable == detailsTable);
        collapseAllMenuItem.setEnabled(! threadActive && selectedTable == detailsTable);
        searchFieldComboBox.setEnabled(! threadActive);
        searchTextField.setEnabled(! threadActive);
        searchButton.setEnabled(! threadActive);
        if(selectedTable == deviceTable)
        {
          boolean enableExport = ! threadActive && deviceTable.getSelectedRowCount() > 0;
          exportPopupMenuItem.setEnabled(enableExport);
          exportMenuItem.setEnabled(enableExport);
          resetToDefaultsPopupMenuItem.setEnabled(false);
          resetToDefaultsMenuItem.setEnabled(false);
          setToZerosPopupMenuItem.setEnabled(false);
          setToZerosMenuItem.setEnabled(false);
          setToOnesPopupMenuItem.setEnabled(false);
          setToOnesMenuItem.setEnabled(false);
          auditButton.setEnabled(false);
        }
        else
        {
          JTree tree = detailsTable.getTree();
          TreePath[] selectedPaths = tree.getSelectionPaths();
          boolean enableExport, enableResetDefault, enableReset, enableAudit;
          if(threadActive || selectedPaths == null || selectedPaths.length <= 0)
          {
            enableExport = false;
            enableResetDefault = false;
            enableReset = false;
            enableAudit = false;
          }
          else
          {
            enableExport = true;
            enableResetDefault = true;
            enableReset = true;
            enableAudit = true;
            for(int i=0;i<selectedPaths.length;i++)
            {
              if(selectedPaths[i].getLastPathComponent() instanceof MPSChannel)
              {
                enableExport = false;
                MPSChannel currentChannel = (MPSChannel)selectedPaths[i].getLastPathComponent();
                if(currentChannel.getLockedIndicator().equals("Y"))
                {
                  enableResetDefault = false;
                  enableReset = false;
                }
                else
                  if(currentChannel.getDevice() == null)
                    enableResetDefault = false;//Only enabled for channels with a DVC.
              }
              else
              {
                enableResetDefault = false;
                enableReset = false;
                enableAudit = false;
              }
              if(! (enableResetDefault && enableReset && enableExport && enableAudit))
                break;//No need to continue.
            }
          }
          exportPopupMenuItem.setEnabled(enableExport);
          exportMenuItem.setEnabled(enableExport);
          resetToDefaultsPopupMenuItem.setEnabled(enableResetDefault);
          resetToDefaultsMenuItem.setEnabled(enableResetDefault);
          setToZerosPopupMenuItem.setEnabled(enableReset);
          setToZerosMenuItem.setEnabled(enableReset);
          setToOnesPopupMenuItem.setEnabled(enableReset);
          setToOnesMenuItem.setEnabled(enableReset);
          auditButton.setEnabled(enableAudit);
        }
        if(threadActive)
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        else
          setCursor(Cursor.getDefaultCursor());
      }
    });
  }

  /**
   * Called when the selected tab changes. This method calls 
   * <CODE>enableButtons()</CODE> to ensure the appropriate buttons are enabled.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void tabbedPane_stateChanged(ChangeEvent e)
  {
    enableButtons();
    populateSearchFields();
  }

  /**
   * Populates the <CODE>JComboBox</CODE> on the search panel.
   */
  private void populateSearchFields()
  {
    searchFieldComboBox.removeAllItems();
    searchFieldComboBox.addItem("DVC_ID");
    if(findSelectedTable() == detailsTable)
      searchFieldComboBox.addItem("MPS_DVC_ID");
  }

  /**
   * This method restores the column widths to the widths saved in the 
   * applicaiton's properties file for the given table.
   * 
   * @param selectedTable The <CODE>JTable</CODE> of which to restore the column widths.
   */
  private void restoreColumnWidths(JTable selectedTable)
  {
    TableColumnModel allColumns = selectedTable.getColumnModel();
    int columnCount = allColumns.getColumnCount();
    StringBuffer key = new StringBuffer("MPSBrowserFrame.");
    if(selectedTable instanceof JTreeTable)
      key.append("DetailsTable.");
    else
      key.append("DeviceTable.");
    String tableName = key.toString();
    for(int i=0;i<columnCount;i++)
    {
      String propertyName = tableName + selectedTable.getColumnName(i) + ".width";
      String widthProperty = getApplicationProperties().getProperty(propertyName, "100");
      TableColumn currentColumn = allColumns.getColumn(i);
      currentColumn.setPreferredWidth(Integer.parseInt(widthProperty));
    }
  }

  /**
   * This method saves the positions of the table's columns in the application's
   * properties file.
   */
  private void saveColumnPositions(final JTable selectedTable)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        Properties settings = getApplicationProperties();
        StringBuffer key = new StringBuffer("MPSBrowserFrame.");
        if(selectedTable instanceof JTreeTable)
          key.append("DetailsTable");
        else
          key.append("DeviceTable");
        String tableName = key.toString();
        key.append(".ColumnCount");
        int columnCount = selectedTable.getColumnCount();
        settings.setProperty(key.toString(), String.valueOf(columnCount));
        for(int position=0;position<columnCount;position++)
        {
          String column = selectedTable.getColumnName(position);
          settings.setProperty(tableName + ".Column" + position, column);
        }
      }
    });
  }

  /**
   * This method puts the columns of the given table in the order the user left 
   * them in.
   * 
   * @param selectedTable The <CODE>JTable</CODE> of which to arrange the colimns.
   */
  private void arrangeColumns(final JTable selectedTable)
  {
    Properties settings = getApplicationProperties();
    StringBuffer key = new StringBuffer("MPSBrowserFrame.");
    if(selectedTable instanceof JTreeTable)
      key.append("DetailsTable.");
    else
      key.append("DeviceTable.");
    final String tableName = key.toString();
    key.append("ColumnCount");
    String columnCountProperty = settings.getProperty(key.toString());
    if(columnCountProperty != null)
    {
      int columnCount = Integer.parseInt(columnCountProperty);
      ArrayList columnNames = new ArrayList(columnCount);
      for(int i=0;i<columnCount;i++)
      {
        String currentColumnName = settings.getProperty(tableName + "Column" + i);
        if(currentColumnName != null && ! (selectedTable instanceof JTreeTable && currentColumnName.equals("SGNL_ID")))
          if(! columnNames.contains(currentColumnName))
            columnNames.add(currentColumnName);
      }
      if(selectedTable instanceof JTreeTable)
      {
        detailsTableModel.showColumns(columnNames);
        ((TreeTableModelAdapter)detailsTable.getModel()).fireTableStructureChanged();
      }
      else
        deviceTableModel.showColumns(columnNames);
    }
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
        enableButtons();
        if(e.getFirstRow() == TableModelEvent.HEADER_ROW)
        {
          JTable selectedTable;
          String tableName = "MPSBrowserFrame.";
          if(e.getSource() == deviceTableModel)
          {
            selectedTable = deviceTable;
            tableName += "DeviceTable.";
          }
          else
          {
            selectedTable = detailsTable;
            tableName += "DetailsTable.";
          }
          restoreColumnWidths(selectedTable);
          int columnCount = selectedTable.getColumnCount();
          TableColumnModel allColumns = selectedTable.getColumnModel();
          for(int i=0;i<columnCount;i++)
          {
            final String propertyName = tableName + selectedTable.getColumnName(i) + ".width";
            allColumns.getColumn(i).addPropertyChangeListener(new PropertyChangeListener()
            {
              public void propertyChange(PropertyChangeEvent evt)
              {
                if(evt.getPropertyName().equals("preferredWidth"))
                  getApplicationProperties().setProperty(propertyName, evt.getNewValue().toString());
              }
            });
          }
        }
      }
    });
  }

  /**
   * Called when the row selection for the tree (detail) table changes. This
   * method posts any changes to mimic other table database editors that post 
   * when you leave an edited row.
   * 
   * @param e The <CODE>ListSelectionEvent</CODE> that caused the invocation of this method.
   */
  private void detailsTable_selectionChanged(ListSelectionEvent e)
  {
    if(! e.getValueIsAdjusting())
    {
      enableButtons();
      int newRow = e.getFirstIndex();
      if(newRow != -1 && lastDetailRowSelected != newRow)
      {
        if(detailsTableModel.isPostNeeded())
          post();
        lastDetailRowSelected = newRow;
      }
    }
  }
  
  /**
   * Called when the row selection for the device table changes. This method 
   * posts any changes to mimic other table database editors that post when you 
   * leave an edited row.
   * 
   * @param e The <CODE>ListSelectionEvent</CODE> that caused the invocation of this method.
   */
  private void deviceTable_selectionChanged(ListSelectionEvent e)
  {
    if(! e.getValueIsAdjusting())
    {
      int[] selectedRows = deviceTable.getSelectedRows();
      if(deviceTableModel.isPostNeeded())
      {
        //If no selected rows are changed, post changes.
        List boardsChanged = Arrays.asList(deviceTableModel.getBoardsChanged());
        boolean changedRowSelected = false;
        for(int i=0;i<selectedRows.length;i++) 
        {
          MPSBoard currentBoard = deviceTableModel.getBoardAt(selectedRows[i]);
          if(boardsChanged.contains(currentBoard))
          {
            changedRowSelected = true;
            break;
          }
        }
        if(! changedRowSelected)
          post();
      }
      if(selectedRows.length == 1)
      {
        Object[] pathNodes = new Object[2];
        pathNodes[0] = detailsTableModel.getRoot();
        pathNodes[1] = deviceTableModel.getBoardAt(selectedRows[0]);
        JTree tree = detailsTable.getTree();
        int row = tree.getRowForPath(new TreePath(pathNodes));
        tree.expandRow(row);
        selectAndShowRow(row, row + 16, detailsTable);
      }
    }
  }

  /**
   * Uses <CODE>SwingUtilities.invokeLater</CODE> to set the indeterminate 
   * property of the progress bar.
   * 
   * @param indeterminate The new value of the indeterminate property of the progress bar.
   */
  private void setProgressIndeterminate(final boolean indeterminate)
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
  private void setProgressValue(final int progressValue)
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
  private void setProgressMaximum(final int progressMaximum)
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
  private void setMessage(final String message)
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
   * Sets the instance of <CODE>Properties</CODE> used to store the settings for
   * the application. This method restores the column  positions to where the 
   * user left them.
   * 
   * @param applicationProperties The instance of <CODE>Properties</CODE> that stores the application settings.
   */
  public void setApplicationProperties(Properties applicationProperties)
  {
    super.setApplicationProperties(applicationProperties);
    arrangeColumns(deviceTable);
    arrangeColumns(detailsTable);
  }

  /**
   * Called when the mouse is clicked. This method shows the popup menu when 
   * appropriate.
   * 
   * @param e The <CODE>MaouseEvent</CODE> that caused the invocation of this method.
   */
  private void checkPopup(final MouseEvent e)
  {
    if(e.isPopupTrigger())
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          Point clickPoint = e.getPoint();
          JTable currentTable = findSelectedTable();
          int rowClicked = currentTable.rowAtPoint(clickPoint);
          if(rowClicked >= 0 && ! currentTable.isRowSelected(rowClicked))
            currentTable.getSelectionModel().setSelectionInterval(rowClicked, rowClicked);
          popupMenu.show(currentTable, clickPoint.x, clickPoint.y);
        }
      });
  }

  /**
   * Called when the expand all popup menu item is clicked. This method expands
   * all of the rows in the tree table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void expandAllPopupMenuItem_actionPerformed(ActionEvent e)
  {
    expandAll();
  }

  /**
   * Expands all of the nodes in the details table.
   */
  private void expandAll()
  {
    JTree tree = detailsTable.getTree();
    for(int i=0;i<tree.getRowCount();i++)
      tree.expandRow(i);
  }

  /**
   * Called when the export popup menu item is clicked. This method shows the
   * <CODE>MPSExportFrame</CODE> with the selected records.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void exportPopupMenuItem_actionPerformed(ActionEvent e)
  {
    export();
  }

  /**
   * Exports the selected data from the current table.
   */
  private void export()
  {
    MPSBoard[] boards;
    if(tabbedPane.getSelectedComponent() == deviceScrollPane)
    {
      int[] rows = deviceTable.getSelectedRows();
      boards = new MPSBoard[rows.length];
      for(int i=0;i<boards.length;i++)
        boards[i] = (MPSBoard)deviceTableModel.getBoardAt(rows[(i)]).clone();
    }
    else
    {
      JTree tree = detailsTable.getTree();
      TreePath[] paths = tree.getSelectionPaths();
      boards = new MPSBoard[paths.length];
      for(int i=0;i<boards.length;i++)
        boards[i] = (MPSBoard)((MPSBoard)paths[(i)].getLastPathComponent()).clone();
    }
    getMainWindow().showMPSExport(boards);
  }

 
  
  /**
   * Called when the select columns popup menu item is clicked. This method 
   * shows a dialog that allows the user to show or hide columns in the selected 
   * table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void columnsPopupMenuItem_actionPerformed(ActionEvent e)
  {
    selectColumns();
  }

  /**
   * Allows the user to select the visible columns for the selected table.
   */
  private void selectColumns()
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if(columnsDialog == null)
      {
        columnsDialog = new ColumnSelectDialog((Frame) getMainWindow(), "Column Select", true);
        columnsDialog.setApplicationProperties(getApplicationProperties());
        columnsDialog.center();
      }        
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
    boolean deviceTab = tabbedPane.getSelectedComponent() == deviceScrollPane;
    if(deviceTab)
    {
      columnsDialog.setAvailableColumns(deviceTableModel.getHiddenColumnNames());
      columnsDialog.setSelectedColumns(deviceTableModel.getVisibleColumnNames());
    }
    else
    {
      columnsDialog.setAvailableColumns(detailsTableModel.getHiddenColumnNames());
      columnsDialog.setSelectedColumns(detailsTableModel.getVisibleColumnNames());
    }
    columnsDialog.setVisible(true);
    if(columnsDialog.getResult() == ColumnSelectDialog.OK)
      try
      {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ArrayList newColumns = columnsDialog.getSelectedColumns();
        if(deviceTab)
        {
          deviceTableModel.showColumns(newColumns);
          saveColumnPositions(deviceTable);
        }
        else
        {
          detailsTableModel.showColumns(newColumns);
          ((TreeTableModelAdapter)detailsTable.getModel()).fireTableStructureChanged();
          saveColumnPositions(detailsTable);
        }
      }
      finally
      {
        setCursor(Cursor.getDefaultCursor());
      }
  }

  /**
   * Posts pending changes to the database.
   * 
   * @throws SQLException Thrown on sql error.
   */
  private void post()
  {
    Thread postThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(mpsBrowser)
        {
          try
          {
            threadActive = true;
            enableButtons();
            if(deviceTableModel.isPostNeeded())
            {
              mpsBrowser.postBoardChanges(deviceTableModel.getBoardsChanged());
              deviceTableModel.changesPosted();
            }
            if(detailsTableModel.isPostNeeded())
            {
              mpsBrowser.postChannelChanges(detailsTableModel.getChannelsChanged());
              detailsTableModel.changesPosted();
            }
          }
          catch(SQLException ex)
          {
            ex.printStackTrace();
            showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
          }
          finally
          {
            threadActive = false;
            enableButtons();
          }
        }
      }
    });
    postThread.start();
  }

  /**
   * Cancels any pending edits. This method simply calls 
   * <CODE>reloadData()</CODE>
   */
  private void cancel()
  {
    reloadData();
  }

  /**
   * Called when the collapse all popup menu item is clicked. This method 
   * collapses all of the expanded rows in the tree table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void collapseAllPopupMenuItem_actionPerformed(ActionEvent e)
  {
    collapseAll();
  }

  /**
   * Collapses all of the nodes in the details table.
   */
  private void collapseAll()
  {
    collapseAll(new ArrayList(0));
  }

  /**
   * This method is called when the user closes the window. It checks for 
   * pending changes and gives the user the coice of canceling the close, 
   * commiting the changes or rolling them back.
   * 
   * @param e The <CODE>PropertyChangeEvent</CODE> that caused the invocation of this method.
   * @throws PropertyVetoException Thrown if the user cancels the close operation.
   */
  private void this_vetoableChange(PropertyChangeEvent e) throws PropertyVetoException
  {
    if(e.getPropertyName().equals("closed") && e.getNewValue().equals(Boolean.TRUE))
      try
      {
        if(promptSaveChanges())
        {
          Thread connectionCloseThread = new Thread(new Runnable()
          {
            public void run()
            {
              try
              {
                synchronized(mpsBrowser)
                {
                  try
                  {
                    threadActive = true;
                    enableButtons();
                    mpsBrowser.closeConnection();
                  }
                  finally
                  {
                    threadActive = false;
                    enableButtons();
                  }
                }
              }
              catch(Exception exc)
              {
                exc.printStackTrace();
              }
            }
          });
          connectionCloseThread.start();
        }
        else
          throw new PropertyVetoException("User canceled close.", e);
      }
      catch(SQLException ex)
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
   * @throws SQLException Thrown on sql error.
   */
  private boolean promptSaveChanges() throws SQLException
  {
    if(mpsBrowser.isCommitNeeded() || detailsTableModel.isPostNeeded() || deviceTableModel.isPostNeeded())
    {
      int option = JOptionPane.showConfirmDialog(this, "Do you want to commit the changes made?", "Changes Pending", JOptionPane.YES_NO_CANCEL_OPTION);
      if(option == JOptionPane.YES_OPTION)
        commit();      
      else
        if(option == JOptionPane.NO_OPTION)
          try
          {
            rollback(false);//No need to refresh data.
          }
          catch(SQLException exc)
          {
            exc.printStackTrace();
          }
        else
          return false;//user cancelled.
    }
    return true;
  }

  private void auditButton_actionPerformed(ActionEvent e)
  {
    TreePath[] selectedPaths = detailsTable.getTree().getSelectionPaths();
    final MPSChannel[] channels = new MPSChannel[selectedPaths.length];
    for(int i=0;i<selectedPaths.length;i++) 
      channels[i] = (MPSChannel)selectedPaths[i].getLastPathComponent();
    final Object audit = JOptionPane.showInputDialog(this, null, "Audit Results", JOptionPane.QUESTION_MESSAGE, null, new String[]{"Passed", "Failed"}, "Passed");
    if(audit != null)
    {
      SwingWorker auditThread = new SwingWorker()
      {
        public Object construct()
        {
          try
          {
            threadActive = true;
            try
            {
              mpsBrowser.updateAuditResults(channels, audit.equals("Passed"));
            }
            catch(java.sql.SQLException ex)
            {
              ex.printStackTrace();
              showErrorMessage("SQL Error", "Error updating audit table.", ex);
              return ex;
            }
            return null;
          }
          finally
          {
            threadActive = false;
          }
        }
        
        public void finished()
        {
          super.finished();
          if(getValue() == null)
          {
            int confirm = JOptionPane.showConfirmDialog(MPSBrowserFrame.this, "Do you want to commit the audit results and all other changes now?", "Confirm Commit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(confirm == JOptionPane.YES_OPTION)
              try
              {
                commit();
              }
              catch(SQLException e)
              {
                e.printStackTrace();
                showErrorMessage("SQL Error", "Error performing commit.", e);
              }
          }
        }
      };
      auditThread.start();
    }
  }


  /**
   * Provides a class that replaces the text if the user types a value into a 
   * cell rather than appending it to the existing value.
   * 
   * @author Chris Fowlkes
   */
  private class TextReplaceTable extends JTable
  {
    /**
     * Gets the editor for the cell. If the editor is a <CODE>JTextField</CODE>,
     * all of the text is selected.
     * 
     * @param editor The editor to prepare.
     * @param row The index of the row being edited.
     * @param column The index of the column being edited.
     * @return The editor.
     */
    public Component prepareEditor(TableCellEditor editor, int row, int column)
    {
      final Component editorComponent = super.prepareEditor(editor, row, column);
      if(editorComponent instanceof JTextField) 
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            ((JTextField)editorComponent).selectAll();
          }
        });        
      return editorComponent;
    }
  }

  /**
   * Provides a class that replaces the text if the user types a value into a 
   * cell rather than appending it to the existing value.
   * 
   * @author Chris Fowlkes
   */
  private class TextReplaceTreeTable extends JTreeTable
  {
    /**
     * Creates a new <CODE>TextReplaceTreeTable</CODE>.
     * 
     * @param treeTableModel The model for the table.
     */
    public TextReplaceTreeTable(TreeTableModel treeTableModel)
    {
      super(treeTableModel);
    }
    
    /**
     * Gets the editor for the cell. If the editor is a <CODE>JTextField</CODE>,
     * all of the text is selected.
     * 
     * @param editor The editor to prepare.
     * @param row The index of the row being edited.
     * @param column The index of the column being edited.
     * @return The editor.
     */
    public Component prepareEditor(TableCellEditor editor, int row, int column)
    {
      Component editorComponent = super.prepareEditor(editor, row, column);
      if(editorComponent instanceof JTextField) 
      {
        final JTextField textField = (JTextField)editorComponent;
        final String value = textField.getText();
        textField.selectAll();
        //If the user clicked the mouse to show the editor, we need to select 
        //everything again after the editor is shown.
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            if(MPSBrowserView.compare(value, textField.getText()))
              textField.selectAll();//If the value has not changed, select all.
          }
        });        
      }
      return editorComponent;
    }
  }

  /**
   * Called when the user clicks the duplicate popup menu item. This method 
   * duplicates the IOCs represented by the selected rows in the device table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void duplicatePopupMenuItem_actionPerformed(ActionEvent e)
  {
    duplicate();
  }
  
  /**
   * Duplicates the record selected in the current table.
   */
  private void duplicate()
  {
    String response = JOptionPane.showInputDialog(this, "Enter the number of duplicates to create for each IOC.", "Duplicate Count", JOptionPane.QUESTION_MESSAGE);
    if(response != null)//null means canceled by user...
      try
      {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int count = Integer.parseInt(response);
        int[] selectedRows = findSelectedTable().getSelectedRows();
        for(int i=0;i<selectedRows.length;i++) 
          for(int j=0;j<count;j++) 
          {
            MPSBoard newBoard = (MPSBoard)deviceTableModel.getBoardAt(selectedRows[(i)]).clone();
            newBoard.setInDatabase(false);
            newBoard.setID(null);
            for(int channel=0;channel<16;channel++)
              newBoard.channelAt(channel).setDevice(null);
            deviceTableModel.addBoard(newBoard);
            detailsTableModel.addBoard(newBoard);
          }
        enableButtons();
      }
      catch(NumberFormatException ex)
      {
        String message = response +  " is not a valid number.";
        JOptionPane.showMessageDialog(this, message, "Invalid Number", JOptionPane.ERROR_MESSAGE);
      }
      finally
      {
        setCursor(Cursor.getDefaultCursor());
      }
  }

  private void detailsTable_keyPressed(KeyEvent e)
  {
    if(e.getModifiers() == ActionEvent.CTRL_MASK)
    {
      int keyCode = e.getKeyCode();
      if(keyCode == KeyEvent.VK_V)
        try
        {
          pasteDetailsRow();
        }
        catch(IOException ex)
        {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(this, ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
        }
        catch(UnsupportedFlavorException ex)
        {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(this, "The clipboard contains data of an unsupported type.", "Paste Error", JOptionPane.ERROR_MESSAGE);
        }
    }
  }

  /**
   * Pastes the data on the system clipboard into the given row.
   * 
   * @throws IOException Thrown on IO error.
   * @throws UnsupportedFlavorException Thrown if the data on the clipboard is not of the required type.
   */
  private void pasteDetailsRow() throws IOException, UnsupportedFlavorException
  {
    Object copiedData = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this).getTransferData(DataFlavor.stringFlavor);
    int[] selectedRows = detailsTable.getSelectedRows();
    if(copiedData != null)
    {
      String[] lines = copiedData.toString().split("\\n");
      if(lines.length != selectedRows.length)
      {
        String copiedRowCount = String.valueOf(selectedRows.length);
        StringBuffer errorMessage = new StringBuffer(copiedRowCount);
        if(selectedRows.length == 1)
          errorMessage.append(" row was");
        else
          errorMessage.append(" rows were");
        errorMessage.append(" copied. You must select ");
        errorMessage.append(copiedRowCount);
        if(selectedRows.length == 1)
          errorMessage.append(" row to paste into.");
        else
          errorMessage.append(" rows to paste into.");
        JOptionPane.showMessageDialog(this, errorMessage.toString(), "Paste Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      int columnCount = detailsTable.getColumnCount();
      String[][] newValues = new String[lines.length][columnCount - 4];
      for(int i=0;i<lines.length;i++)
      {
        String[] currentLine = lines[i].split("\\t");
        if(currentLine.length != columnCount)
        {
          JOptionPane.showMessageDialog(this, "Invalid clipboard data found.", "Paste Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        else
          for(int j=4;j<currentLine.length;j++) 
            newValues[i][j - 4] = currentLine[j];
      }
      TableModel model = detailsTable.getModel();
      //The Data copied starts at the 4th column in the model.
      for(int i=0;i<newValues.length;i++)
        for(int j=0;j<newValues[i].length;j++) 
          model.setValueAt(newValues[i][j], selectedRows[i], j + 4);
    }
  }

  /**
   * Provides an editor for the signal ID column that filters the available 
   * signal IDs based on the system and subsystem of the device.
   * 
   * @author Chris Fowlkes
   */
  private class MPSDeviceComboBoxCellEditor extends ComboBoxCellEditor
  {
    /**
     * Holds the default items that will appear in the <CODE>JComboBox</CODE>.
     */
    private Device[] devices;
    
    /**
     * Creates a new <CODE>SignalComboBoxCellEditor</CODE>.
     * 
     * @param comboBox The <CODE>JComboBox</CODE> used to edit the cell.
     */
    public MPSDeviceComboBoxCellEditor(JComboBox comboBox)
    {
      super(comboBox);
    }
    
    /**
     * Gets the editor component. This method goes through and strips out all of 
     * the signal IDs that do not match the system, and makes sure they match
     * the FPAR_FPL_CONFIG. For 16L IOCs FPL signals are valid, for 16FPAR FPAR
     * signals are valid. For 8L8AR, the first 8 channels are FPAR and the 
     * second 8 are FPL.
     *
     * @param table the <code>JTable</code> the component will be used in
     * @param value the value of the cell before editing
     * @param isSelected <code>true</code> if the editor is selected, <code>false</code> otherwise
     * @param row the row being edited
     * @param column the column being edited
     * @return The <CODE>Component</CODE> responsible for editing the value.
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
      if(filterCheckBox.isSelected())
      {
        TreePath treePath = detailsTable.getTree().getPathForRow(row);
        MPSBoard selectedBoard = (MPSBoard)treePath.getParentPath().getLastPathComponent();
        String boardSystemID = selectedBoard.getSystem().getID();
        String boardSubsystemID = selectedBoard.getSubsystem().getID();
        ArrayList defaultItems = new ArrayList();
        defaultItems.add(null);
        for(int i=0;i<devices.length;i++)
        {
          if(devices[i].equals(value))
            defaultItems.add(devices[i]);
          else
            if(boardSystemID.equals(devices[i].getSystem().getID()) && boardSubsystemID.equals(devices[i].getSubsystem().getID()))
              defaultItems.add(devices[i]);
        }
        setDefaultItems(defaultItems);
      }
      else
      {
        ArrayList defaultItems = new ArrayList(devices.length + 1);
        defaultItems.add(null);
        defaultItems.addAll(Arrays.asList(devices));
        setDefaultItems(defaultItems);
      }
      return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    /**
     * Sets the instnces of <CODE>Device</CODE> to display in the 
     * <CODE>ComboBox</CODE>. If the filter device IDs check box is checked,
     * this list will be filtered down when shown.
     * 
     * @param newDevices The instances of <CODE>Device</CODE> from which to select those to display in the editor.
     */
    final public void setDevices(Device[] newDevices)
    {
      devices = newDevices;
    }
  }

  /**
   * Called when a new item is selected in the chain editor. This method prompts
   * the user to commit any changes before proceeding.
   * 
   * @param e The <CODE>ItemEvent</CODE> that caused the invocation of this method.
   */
  private void chainEditor_itemStateChanged(ItemEvent e)
  {
    //1/16/2004 Don't need this anymore. Changes requested by Coles.
//    if(deviceTable.isEditing() && e.getStateChange() == ItemEvent.SELECTED)
//    {
//      int confirm = JOptionPane.showConfirmDialog(this, "In order to change the chain all changes will be commited.", "Confirm Commit", JOptionPane.OK_CANCEL_OPTION);
//      if(confirm == JOptionPane.OK_OPTION)
//      {
//        final int row = deviceTable.getEditingRow();
//        deviceTable.getCellEditor().stopCellEditing();
//        SwingUtilities.invokeLater(new Runnable()
//        {
//          public void run()
//          {
//            try
//            {
//              commit();
//              StringBuffer sql = new StringBuffer("{? = call ");
//              sql.append(Jeri.SCHEMA);
//              sql.append(".EPICS_PKG.REASSIGN_MPS_CHAN_SGNL(?, ?)}");
//              CallableStatement reassignChannelSignal = oracleConnection.prepareCall(sql.toString());
//              try
//              {
//                reassignChannelSignal.registerOutParameter(1, Types.VARCHAR);
//                MPSBoard selectedIOC = deviceTableModel.getBoardAt(row);
//                reassignChannelSignal.setString(2, selectedIOC.getID());
//                reassignChannelSignal.setString(3, selectedIOC.getChain().getID());
//                reassignChannelSignal.execute();
//                String message = reassignChannelSignal.getString(1);
//                JOptionPane.showMessageDialog(MPSBrowserFrame.this, message);
//              }//try
//              finally
//              {
//                reassignChannelSignal.close();
//              }//finally
//            }//try
//            catch(java.sql.SQLException ex)
//            {
//              ex.printStackTrace();
//              JOptionPane.showMessageDialog(MPSBrowserFrame.this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
//            }//catch(java.sql.SQLException ex)
//            finally
//            {
//              enableButtons();
//            }//finally
//          }
//        });
//      }//if(confirm == JOptionPane.OK_OPTION)
//      else
//        deviceTable.getCellEditor().cancelCellEditing();
//    }//if(deviceTable.isEditing() && e.getStateChange() == ItemEvent.SELECTED)
  }

  /**
   * Called when the reset to defaults menu item is clicked. This method calls 
   * the stored procedure that resets the mask values for the selected channel.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void resetToDefaultsPopupMenuItem_actionPerformed(ActionEvent e)
  {
    resetToDefaults();
  }

  /**
   * Resets the binary values in the selected row in the details table to the 
   * default values.
   */
  private void resetToDefaults()
  {
    try
    {
      JTree tree = detailsTable.getTree();
      TreePath[] selectedPaths = tree.getSelectionPaths();
      for(int i=0;i<selectedPaths.length;i++)
      {
        MPSChannel currentChannel = (MPSChannel)selectedPaths[(i)].getLastPathComponent();
        detailsTableModel.loadMaskDefaults(currentChannel, null);
      }
      enableButtons();
    }
    catch(SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Called when the set to zeros menu item is clicked. This method sets all of 
   * the mask values for the selected channel to zero.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void setToZerosPopupMenuItem_actionPerformed(ActionEvent e)
  {
    resetToZero();
  }
  
  /**
   * Sets the binary values in the selected row of the details table to zero.
   */
  private void resetToZero()
  {
    JTree tree = detailsTable.getTree();
    TreePath[] selectedPaths = tree.getSelectionPaths();
    for(int i=0;i<selectedPaths.length;i++)
    {
      MPSChannel currentChannel = (MPSChannel)selectedPaths[(i)].getLastPathComponent();
      detailsTableModel.setChannelValuesToZero(currentChannel);
    }
    enableButtons();
  }

  /**
   * Called when the set to ones menu item is clicked. This method sets all of 
   * the mask values for the selected channel to one.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void setToOnesPopupMenuItem_actionPerformed(ActionEvent e)
  {
    resetToOne();
  }
  
  /**
   * Sets the binary values in the row selected in the details table to one.
   */
  private void resetToOne()
  {
    JTree tree = detailsTable.getTree();
    TreePath[] selectedPaths = tree.getSelectionPaths();
    for(int i=0;i<selectedPaths.length;i++)
    {
      MPSChannel currentChannel = (MPSChannel)selectedPaths[(i)].getLastPathComponent();
      detailsTableModel.setChannelValuesToOne(currentChannel);
    }
    enableButtons();
  }

  /**
   * Called when the save as popup menu item is clicked. This menu item allows 
   * the user to save the selected rows to a .csv file.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void saveAsPopupMenuItem_actionPerformed(ActionEvent e)
  {
    saveAs();
  }
  
  /**
   * Saves the selected data in the current table.
   */
  private void saveAs()
  {
    Properties applicationSettings = getApplicationProperties();
    if(saveDialog == null)
    {
      String lastPath = applicationSettings.getProperty("MPSBrowser.saveAsDirectory");
      saveDialog = new JFileChooser(lastPath);
      saveDialog.setFileFilter(new FileFilter()
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
        applicationSettings.setProperty("MPSBrowser.saveAsDirectory", saveDirectory);
        JTable selectedTable = findSelectedTable();
        int[] selectedRows = selectedTable.getSelectedRows();
        BufferedWriter oStream = new BufferedWriter(new FileWriter(saveFile));
        try
        {
          int columnCount = selectedTable.getColumnCount();
          for(int j=0;j<columnCount;j++)
          {
            if(j > 0)
              oStream.write(", ");
            oStream.write(selectedTable.getColumnName(j));
          }
          oStream.newLine();
          for(int i=0;i<selectedRows.length;i++)
          {
            for(int j=0;j<columnCount;j++)
            {
              if(j>0)
                oStream.write(", ");
              Object currentValue = selectedTable.getValueAt(i, j);
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
              }
            }
            oStream.newLine();
          }
          oStream.flush();
        }
        finally
        {
          oStream.close();
        }
      }
      catch(IOException ex)
      {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
      }
  }

  /**
   * Passes the main window into the interface. This is used to show dialogs.
   * 
   * @param mainWindow The main window of the application.
   */
  public void setMainWindow(MainFrame mainWindow)
  {
    super.setMainWindow(mainWindow);
    detailsTableModel.setMainWindow(mainWindow);
  }

  
  
  /**
   * Called when the search menu item is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void searchMenuItem_actionPerformed(ActionEvent e) 
  {
    searchChain();
  }
  
  /**
   * Called when the columns menu item is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void columnsMenuItem_actionPerformed(ActionEvent e)
  {
    selectColumns();
  }

  /**
   * Called when the export menu item is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void exportMenuItem_actionPerformed(ActionEvent e)
  {
    export();
  }

  /**
   * Called when the duplicate menu item is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void duplicateMenuItem_actionPerformed(ActionEvent e)
  {
    duplicate();
  }

  /**
   * Called when the save as menu item is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void saveAsMenuItem_actionPerformed(ActionEvent e)
  {
    saveAs();
  }

  /**
   * Called when the expand all menu item is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void expandAllMenuItem_actionPerformed(ActionEvent e)
  {
    expandAll();
  }

  /**
   * Called when the collapse all menu item is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void collapseAllMenuItem_actionPerformed(ActionEvent e)
  {
    collapseAll();
  }

  /**
   * Called when the reset to defaults menu item is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void resetToDefaultsMenuItem_actionPerformed(ActionEvent e)
  {
    resetToDefaults();
  }

  /**
   * Called when the set to zeros menu item is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void setToZerosMenuItem_actionPerformed(ActionEvent e)
  {
    resetToZero();
  }

  /**
   * Called when the set to ones menu item is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void setToOnesMenuItem_actionPerformed(ActionEvent e)
  {
    resetToOne();
  }

  /**
   * Called when the search button is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void searchChainButton_actionPerformed(ActionEvent e)
  {
    searchChain();
  }

  /**
   * Searches the selected table for a value provided by the user. 
   */
  private void searchChain()
  {
    TableSearcher searcher = new TableSearcher();
    JTable selectedTable = findSelectedTable();
    searcher.setTable(selectedTable);
    searcher.setReplaceVisible(false);
    searcher.searchReplace(false, getMainWindow());
  }

  /**
   * Called when the search field gains focus.
   * 
   * @param e The <CODE>FocusEvent</CODE> that caused the invocation of this method.
   */
  private void searchTextField_focusGained(FocusEvent e)
  {
    String text = searchTextField.getText();
    if(text != null && text.equals("Use '*' as a wildcard for searching."))
      searchTextField.setText("");
  }

  /**
   * Called when the table search button is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocatoin of this method.
   */
  private void searchButton_actionPerformed(ActionEvent e)
  {
    JTable selectedTable = findSelectedTable();
    TableSearcher searcher;
    if(selectedTable == detailsTable)
      searcher = new TreeTableSearcher();
    else
      searcher = new TableSearcher();
    searcher.setTable(selectedTable);
    String columnName = searchFieldComboBox.getSelectedItem().toString();
    int columnIndex = searcher.findColumnIndex(columnName);
    searcher.setSearchColumnIndex(columnIndex);
    searcher.setSearchValue(searchTextField.getText());
//    if(selectedTable == detailsTable)
//    {
//      ArrayList nodesToLeaveExpanded = findExpandedIOCs();
//      expandAll();
//      searcher.findNext(-1);
//      nodesToLeaveExpanded.addAll(findSelectedIOCs());
//      int[] rowsSelected = selectedTable.getSelectedRows();
//      collapseAll(nodesToLeaveExpanded);
//      selectAndShowRow(rowsSelected[0], rowsSelected[0], selectedTable);
//    }
//    else
      searcher.findNext(-1);
  }

  /**
   * Looks through the details table and returns all of the instances of 
   * <CODE>MPSBoard</CODE> that are selected or have a channel selected under 
   * them.
   * 
   * @return The selected instances of <CODE>MPSBoard</CODE>.
   */
  private ArrayList findSelectedIOCs()
  {
    JTree tree = detailsTable.getTree();
    TreePath[] selectedPaths = tree.getSelectionPaths();
    ArrayList selectedIOCs = new ArrayList(selectedPaths.length);
    for(int i=0;i<selectedPaths.length;i++) 
    {
      Object selectedObject = selectedPaths[i].getLastPathComponent();
      MPSBoard selectedBoard;
      if(selectedObject instanceof MPSChannel)
      {
        int pathCount = selectedPaths[i].getPathCount();
        selectedIOCs.add(selectedPaths[i].getPathComponent(pathCount - 2));
      }
      else
        if(selectedObject instanceof MPSBoard)
          selectedIOCs.add(selectedObject);
    }
    return selectedIOCs;
  }

  /**
   * Collapses all tree branches that don't have a node selected.
   * 
   * @param exceptions The nodes that should ot be collapsed. Can not be <CODE>null</CODE>.
   */
  private void collapseAll(ArrayList exceptions)
  {
    JTree tree = detailsTable.getTree();
    int row = 0;
    while(row < tree.getRowCount())
    {
      Object currentNode = tree.getPathForRow(row++).getLastPathComponent();
      if(! exceptions.contains(currentNode))
        tree.collapseRow(row);
    }
  }
}