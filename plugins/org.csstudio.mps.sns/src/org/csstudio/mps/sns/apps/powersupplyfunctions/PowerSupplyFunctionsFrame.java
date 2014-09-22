package org.csstudio.mps.sns.apps.powersupplyfunctions;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.apps.recorddetails.RecordDetailsPanel;
import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.IOC;
import org.csstudio.mps.sns.tools.data.Magnet;
import org.csstudio.mps.sns.tools.data.PowerSupply;
import org.csstudio.mps.sns.tools.data.PowerSupplyController;
import org.csstudio.mps.sns.tools.data.PowerSupplyInterface;
import org.csstudio.mps.sns.tools.database.swing.DatabaseEditTableModel;
import java.beans.VetoableChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JTable;
import javax.sql.DataSource;
import java.sql.SQLException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import javax.swing.JToolBar;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.tree.TreeSelectionModel;
import org.csstudio.mps.sns.application.JeriInternalFrame;
import java.beans.PropertyChangeEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.awt.Dimension;

/**
 * Provides an interface for displaying and editing power supply data.
 * 
 * @author Chris Fowlkes
 */
public class PowerSupplyFunctionsFrame extends JeriInternalFrame 
{
  private JSplitPane splitPane = new JSplitPane();
  private JPanel statusBarPanel = new JPanel();
  private BorderLayout statusBarPanelLayout = new BorderLayout();
  private JLabel progressLabel = new JLabel();
  private JProgressBar progressBar = new JProgressBar();
  private JScrollPane treeScrollPane = new JScrollPane();
  private JScrollPane tableScrollPane = new JScrollPane();
  private JTree tree = new JTree();
  /**
   * Holds the data and functionality for the interface.
   */
  private PowerSupplyFunctions powerSupplyFunctions = new PowerSupplyFunctions();
  /**
   * Flag used to tell if a thread is active.
   */
  private boolean threadActive = false;
  private JToolBar toolBar = new JToolBar();
  private JButton refreshButton = new JButton();
//  /**
//   * Provides the model for the table in the interface.
//   */
//  private DatabaseEditTableModel tableModel = new DatabaseEditTableModel();
  private JButton commitButton = new JButton();
  private JButton cancelButton = new JButton();
  private JButton postButton = new JButton();
//  private JButton deleteButton = new JButton();
  private JButton rollbackButton = new JButton();
//  private JButton insertButton = new JButton();
//  private JButton lastButton = new JButton();
//  private JButton nextButton = new JButton();
//  private JButton priorButton = new JButton();
//  private JButton firstButton = new JButton();
  /**
   * Holds the last row selected in the tree. Used to determine if the selected 
   * node has changed.
   */
  private TreePath selectedPath;
  private JPanel scrollPanel = new JPanel();
  private RecordDetailsPanel dataPanel = new RecordDetailsPanel();
  private BorderLayout scrollPanelLayout = new BorderLayout();
//  private JButton removeFilterButton = new JButton();
//  private JButton filterButton = new JButton();
//  /**
//   * Holds the icon that denotes there is a filter applied.
//   */
//  private Icon editFilterIcon;
//  /**
//   * Holds the icon that denotes there is no filter currently applied.
//   */
//  private Icon newFilterIcon;

  /**
   * Creates a new <CODE>PowerSupply</CODE>.
   */
  public PowerSupplyFunctionsFrame()
  {
    try
    {
      jbInit();
      clearTree();
      DefaultTreeCellRenderer treeRenderer = (DefaultTreeCellRenderer)tree.getCellRenderer();
      treeRenderer.setLeafIcon(null);
      treeRenderer.setClosedIcon(null);
      treeRenderer.setOpenIcon(null);
      tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//      table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
//      {
//        public void valueChanged(ListSelectionEvent e)
//        {
//          enableControls();
//        }
//      });
      enableControls();
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
    this.setTitle("Power Supply");
    this.setSize(new Dimension(800, 600));
    statusBarPanel.setLayout(statusBarPanelLayout);
    progressLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    progressLabel.setText(" ");
    progressBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    treeScrollPane.addComponentListener(new java.awt.event.ComponentAdapter()
      {
        public void componentResized(ComponentEvent e)
        {
          treeScrollPane_componentResized(e);
        }
      });
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    tree.addTreeSelectionListener(new TreeSelectionListener()
      {
        public void valueChanged(TreeSelectionEvent e)
        {
          tree_valueChanged(e);
        }
      });
//    table.setModel(tableModel);
//    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    powerSupplyFunctions.setMessageLabel(progressLabel);
    powerSupplyFunctions.setProgressBar(progressBar);
    refreshButton.setToolTipText("Refresh Data");
    refreshButton.setMargin(new Insets(2, 2, 2, 2));
    refreshButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          refreshButton_actionPerformed(e);
        }
      });
//    tableModel.addTableModelListener(new TableModelListener()
//      {
//        public void tableChanged(TableModelEvent e)
//        {
//          tableModel_tableChanged(e);
//        }
//      });
    commitButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          commitButton_actionPerformed(e);
        }
      });
    cancelButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          cancelButton_actionPerformed(e);
        }
      });
    postButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          postButton_actionPerformed(e);
        }
      });
    rollbackButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          rollbackButton_actionPerformed(e);
        }
      });
    scrollPanel.setLayout(scrollPanelLayout);
    dataPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    dataPanel.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          dataPanel_stateChanged(e);
        }
      });
    dataPanel.addDocumentListener(new DocumentListener()
      {
        public void changedUpdate(DocumentEvent e)
        {
          dataPanel_changedUpdate(e);
        }
        public void insertUpdate(DocumentEvent e)
        {
          dataPanel_insertUpdate(e);
        }
        public void removeUpdate(DocumentEvent e)
        {
          dataPanel_removeUpdate(e);
        }
      });
//    lastButton.addActionListener(new ActionListener()
//      {
//        public void actionPerformed(ActionEvent e)
//        {
//          lastButton_actionPerformed(e);
//        }
//      });
//    nextButton.addActionListener(new ActionListener()
//      {
//        public void actionPerformed(ActionEvent e)
//        {
//          nextButton_actionPerformed(e);
//        }
//      });
//    priorButton.addActionListener(new ActionListener()
//      {
//        public void actionPerformed(ActionEvent e)
//        {
//          priorButton_actionPerformed(e);
//        }
//      });
//    firstButton.addActionListener(new ActionListener()
//      {
//        public void actionPerformed(ActionEvent e)
//        {
//          firstButton_actionPerformed(e);
//        }
//      });
    toolBar.add(commitButton, null);
    toolBar.add(rollbackButton, null);
//    toolBar.add(filterButton, null);
//    toolBar.add(removeFilterButton, null);
//    toolBar.add(firstButton, null);
//    toolBar.add(priorButton, null);
//    toolBar.add(nextButton, null);
//    toolBar.add(lastButton, null);
//    toolBar.add(insertButton, null);
//    toolBar.add(deleteButton, null);
    toolBar.add(postButton, null);
    toolBar.add(cancelButton, null);
    toolBar.add(refreshButton, null);
    statusBarPanel.add(progressLabel, BorderLayout.CENTER);
    statusBarPanel.add(progressBar, BorderLayout.EAST);
    treeScrollPane.getViewport().add(tree, null);
    splitPane.add(treeScrollPane, JSplitPane.LEFT);
    scrollPanel.add(dataPanel, BorderLayout.NORTH);
    tableScrollPane.getViewport().add(scrollPanel, null);
    splitPane.add(tableScrollPane, JSplitPane.RIGHT);
    this.getContentPane().add(splitPane, BorderLayout.CENTER);
    this.getContentPane().add(statusBarPanel, BorderLayout.SOUTH);
    this.getContentPane().add(toolBar, BorderLayout.NORTH);
  }

  /**
   * Sets the <CODE>DataSource</CODE> this class uses to connect to the 
   * database.
   * 
   * @param connectionPool The <CODE>DataSource</CODE> to use to connect to the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void setDataSource(DataSource connectionPool) throws SQLException
  {
    super.setDataSource(connectionPool);
    Connection oracleConnection = connectionPool.getConnection();
    oracleConnection.setAutoCommit(false);
//    tableModel.setDataSource(connectionPool);
    powerSupplyFunctions.setConnection(oracleConnection);
    Thread refreshThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(powerSupplyFunctions)
        {
          try 
          {
            threadActive = true;
            enableControls();
            refresh();
            restoreTreeSelection();
          } 
          catch(java.sql.SQLException ex) 
          {
            ex.printStackTrace();
            showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
          } 
          finally 
          {
            threadActive = false;
            enableControls();
          }          
        }
      }
    });
    refreshThread.start();
  }
  
  /**
   * Reloads the data in the tree.
   */
  private void refresh() throws java.sql.SQLException
  {
    IOC[] powerSupplies = powerSupplyFunctions.loadPowerSupplyData();
    updateTreeData(powerSupplies);
  }
  
  /**
   * Uses <CODE>SwingUtilities.invokeLater</CODE> to put the given instances of
   * <CODE>PowerSupply</CODE> in the tree.
   * 
   * @param iocs The instances of <CODE>IOC</CODE> containing the data to add to the tree.
   */
  private void updateTreeData(final IOC[] iocs)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        clearTree();
        for(int iocIndex=0;iocIndex<iocs.length;iocIndex++)
        {
          DefaultMutableTreeNode iocNode = new DefaultMutableTreeNode(iocs[iocIndex]);
          model.insertNodeInto(iocNode, root, iocIndex);
          int pscCount = iocs[iocIndex].getPowerSupplyControllerCount();
          for(int pscIndex=0;pscIndex<pscCount;pscIndex++) 
          {
            PowerSupplyController psc = iocs[iocIndex].getPowerSupplyControllerAt(pscIndex);
            DefaultMutableTreeNode pscNode = new DefaultMutableTreeNode(psc);
            model.insertNodeInto(pscNode, iocNode, pscIndex);
            int psiCount = psc.getPowerSupplyInterfaceCount();
            for(int psiIndex=0;psiIndex<psiCount;psiIndex++) 
            {
              PowerSupplyInterface psi = psc.getPowerSupplyInterfaceAt(psiIndex);
              DefaultMutableTreeNode psiNode = new DefaultMutableTreeNode(psi);
              model.insertNodeInto(psiNode, pscNode, psiIndex);
              int psCount = psi.getPowerSupplyCount();
              for(int psIndex=0;psIndex<psCount;psIndex++) 
              {
                PowerSupply ps = psi.getPowerSupplyAt(psIndex);
                DefaultMutableTreeNode psNode = new DefaultMutableTreeNode(ps);
                model.insertNodeInto(psNode, psiNode, psIndex);
                int magnetCount = ps.getMagnetCount();
                for(int magnetIndex=0;magnetIndex<magnetCount;magnetIndex++) 
                {
                  Magnet magnet = ps.getMagnetAt(magnetIndex);
                  DefaultMutableTreeNode magnetNode = new DefaultMutableTreeNode(magnet);
                  model.insertNodeInto(magnetNode, psNode, magnetIndex);
                }
              }
            }
          }
        }
        tree.expandPath(new TreePath(new Object[]{root}));
      }
    });
  }

  /**
   * Removes all nodes from the given tree except the root. This method is not 
   * thread safe.
   */
  private void clearTree()
  {
    DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
    DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode)treeModel.getRoot();
    int childCount = treeRoot.getChildCount();
    TreeNode[] children = new TreeNode[childCount];
    for(int i=0;i<childCount;i++)
      children[i] = treeRoot.getChildAt(i);
    for(int i=0;i<children.length;i++)
      treeModel.removeNodeFromParent((DefaultMutableTreeNode)children[i]);
  }

  /**
   * Enables or disables the appropriate controls. This method uses 
   * <CODE>SwingUtilities.invokeLater</CODE> to make it thread safe.
   */
  private void enableControls()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
//        try
//        {
        tree.setEnabled(! threadActive);
        dataPanel.setEnabled(! threadActive);
        refreshButton.setEnabled(! threadActive);
        //Enable commit and rollback buttons if data is changed.
        boolean changed = dataPanel.isChanged();
        boolean commitNeeded = powerSupplyFunctions.isCommitNeeded();
        commitButton.setEnabled(! threadActive && (commitNeeded || changed));
        rollbackButton.setEnabled(! threadActive && (commitNeeded || changed));
//          //Enable filter buttons.
//          if(isFiltered())
//          {
//            removeFilterButton.setEnabled(true);
//            addFilterButton.setToolTipText("Edit Filter");
//            addFilterButton.setIcon(editFilterIcon);
//          }//if(getTableModel().isFiltered())
//          else
//          {
//            //No filter applied.
//            removeFilterButton.setEnabled(false);
//            addFilterButton.setToolTipText("New Filter");
//            addFilterButton.setIcon(newFilterIcon);
//          }//else
          //Enabling of navigation buttons depends on which rows are selected.
//          int rowCount = table.getRowCount();
//          ListSelectionModel selectionModel = table.getSelectionModel();
//          firstButton.setEnabled(! threadActive && rowCount > 0 && ! selectionModel.isSelectedIndex(0));
//          int firstSelectedRow = selectionModel.getMinSelectionIndex();
//          priorButton.setEnabled(! threadActive && firstSelectedRow > 0);
//          int lastSelectedRow = selectionModel.getMaxSelectionIndex();
//          nextButton.setEnabled(! threadActive && lastSelectedRow >= 0 && lastSelectedRow < rowCount - 1);
//          lastButton.setEnabled(! threadActive && rowCount > 0 && ! selectionModel.isSelectedIndex(rowCount - 1));
//          deleteButton.setEnabled(! threadActive && table.getSelectedRowCount() > 0);
          //Check for unposted changes...
        postButton.setEnabled(! threadActive && changed);
        cancelButton.setEnabled(! threadActive && changed);
//        }
//        catch(java.sql.SQLException ex)
//        {
//          ex.printStackTrace();
//          showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
//        }
      }
    });
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
//    tableModel.setApplicationProperties(applicationProperties);
    //Restore divider location to last value.
    String locationString = applicationProperties.getProperty("PowerSupplyFunctionsFrame.divider" ,"200");
    splitPane.setDividerLocation(Integer.parseInt(locationString));
  }

  /**
   * Called when the tree scroll pane is resized. This method records the new 
   * location of the divider for the split pane.
   * 
   * @param e The <CODE>ComponentEvent</CODE> that caused the invocation of this method.
   */
  private void treeScrollPane_componentResized(ComponentEvent e)
  {
    String location = String.valueOf(splitPane.getDividerLocation());
    getApplicationProperties().setProperty("PowerSupplyFunctionsFrame.divider", location);
  }

  /**
   * Called when the refresh button is clicked. This method calls 
   * <CODE>refresh()</CODE> to reload the tree data.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void refreshButton_actionPerformed(ActionEvent e)
  {
    Thread refreshThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(powerSupplyFunctions)
        {
          try 
          {
            threadActive = true;
            enableControls();
            refresh();
            restoreTreeSelection();
          } 
          catch(java.sql.SQLException ex) 
          {
            ex.printStackTrace();
            showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
          } 
          finally 
          {
            threadActive = false;
            enableControls();
          }          
        }
      }
    });
    refreshThread.start();
  }

  /**
   * Puts an image on the refresh button.
   * 
   * @param newRefreshIcon The <CODE>Icon</CODE> for the refresh toolbar button.
   */
  public void setRefreshIcon(Icon newRefreshIcon)
  {
    refreshButton.setIcon(newRefreshIcon);
  }

  /**
   * Gets the <CODE>Icon</CODE> used on the refresh button.
   * 
   * @return The <CODE>Icon</CODE> for the refresh toolbar button.
   */
  public Icon getRefreshIcon()
  {
    return refreshButton.getIcon();
  }

  /**
   * If there are changes pending, this method prompts the user to commit them.
   * If the user does not click cancel, a commit or rollback is done depending 
   * on the user's selection.
   * 
   * @return <CODE>true</CODE> if the operation that called this method should continue, <CODE>false</CODE> if the user canceled.
   * @throws java.sql.SQLException Thrown on sql error.
   * @throws java.lang.InterruptedException Thrown if interupted while prompting user.
   * @throws java.lang.reflect.InvocationTargetException Thrown on invocation error when prompting user.
   */
  private boolean promptSaveChanges() throws java.sql.SQLException, java.lang.InterruptedException, java.lang.reflect.InvocationTargetException
  {
    if(dataPanel.isChanged())
    {
      final int[] option = new int[1];
      SwingUtilities.invokeAndWait(new Runnable()
      {
        public void run()
        {
          option[0] = JOptionPane.showConfirmDialog(PowerSupplyFunctionsFrame.this, "Do you want to commit the changes made?", "Changes Pending", JOptionPane.YES_NO_CANCEL_OPTION);
        }
      });      
      if(option[0] == JOptionPane.YES_OPTION)
      {
        powerSupplyFunctions.post();
        dataPanel.setChanged(false);
        powerSupplyFunctions.commit();
        enableControls();
      }
      else
        if(option[0] == JOptionPane.NO_OPTION)
        {
          powerSupplyFunctions.cancel();
          dataPanel.setChanged(false);
          powerSupplyFunctions.rollback();
          refresh();
          restoreTreeSelection();
          enableControls();
        }
        else
          return false;//user cancelled.
    }//if(tableModel.isCommitNeeded() || tableModel.isInsertRowChanged() || tableModel.isEditRowChanged())
    return true;
  }

  /**
   * Called when the selection for the tree changes. This method loads the 
   * appropriate data into the table.
   * 
   * @param e The <CODE>TreeSelectionEvent</CODE> that caused the invocation of this event.
   */
  private void tree_valueChanged(final TreeSelectionEvent e)
  {
    final TreePath newSelectedPath = e.getNewLeadSelectionPath();
    if(newSelectedPath != null)
    {
      if(! newSelectedPath.equals(selectedPath))
      {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)newSelectedPath.getLastPathComponent();
        if(selectedNode != null)
        {
          final Object selectedObject = selectedNode.getUserObject();
          if(selectedObject != null)
          {
            Thread loadThread = new Thread(new Runnable()
            {
              public void run()
              {
                synchronized(powerSupplyFunctions)
                {
                  try 
                  {
                    threadActive = true;
                    enableControls();
                    if(promptSaveChanges())
                    {
                      updateDataPanel(selectedObject);
                      selectedPath = newSelectedPath;
                    }
                    else
                      tree.setSelectionPath(e.getOldLeadSelectionPath());
                  }
                  catch(java.sql.SQLException ex)
                  {
                    ex.printStackTrace();
                    showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
                    tree.setSelectionPath(e.getOldLeadSelectionPath());
                  }
                  catch(java.lang.InterruptedException ex)
                  {
                    ex.printStackTrace();
                    showMessage(ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    tree.setSelectionPath(e.getOldLeadSelectionPath());
                  }
                  catch(java.lang.reflect.InvocationTargetException ex)
                  {
                    ex.printStackTrace();
                    showMessage(ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    tree.setSelectionPath(e.getOldLeadSelectionPath());
                  }
                  finally
                  {
                    threadActive = false;
                    enableControls();
                  }
                }
              }
            });
            loadThread.start();
          }
        }
      }
    }
  }
  
  /**
   * Updates the data panel to refer to the given object.
   * 
   * @aram selectedObject The <CODE>Object</CODE> to display in the data panel.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  private void updateDataPanel(Object selectedObject) throws java.sql.SQLException
  {
    final HashMap displayNames = powerSupplyFunctions.loadColumnDisplayNames(selectedObject);
    ResultSet data = powerSupplyFunctions.loadData((Device)selectedObject);
    data.next();
    dataPanel.setData(data);
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        dataPanel.setDisplayNames(displayNames);
//                            tableScrollPane.doLayout();
        tableScrollPane.validate();
      }
    });
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

//  /**
//   * Sets the image to use on the filter button to denote that a filter has been
//   * applied.
//   * 
//   * @param editFilterIcon The <CODE>Icon</CODE> for the add filter toolbar button.
//   */
//  public void setEditFilterIcon(Icon editFilterIcon)
//  {
//    this.editFilterIcon = editFilterIcon;
//    if(getTableModel().isFiltered())
//      addFilterButton.setIcon(editFilterIcon);
//  }
//
//  /**
//   * Sets the image to use on the filter button to denote that a filter has not 
//   * been applied.
//   * 
//   * @param newFilterIcon The <CODE>Icon</CODE> for the add filter toolbar button.
//   */
//  public void setNewFilterIcon(Icon newFilterIcon)
//  {
//    this.newFilterIcon = newFilterIcon;
//    if(! getTableModel().isFiltered())
//      addFilterButton.setIcon(newFilterIcon);
//  }
//
//  /**
//   * Puts an image on the remove filter button.
//   * 
//   * @param removeFilterIcon The <CODE>Icon</CODE> for the remove filter toolbar button.
//   */
//  public void setRemoveFilterIcon(Icon removeFilterIcon)
//  {
//    removeFilterButton.setIcon(removeFilterIcon);
//  }
//
//  /**
//   * Puts an image on the first button.
//   * 
//   * @param firstIcon The <CODE>Icon</CODE> for the first toolbar button.
//   */
//  public void setFirstIcon(Icon firstIcon)
//  {
//    firstButton.setIcon(firstIcon);
//  }
//
//  /**
//   * Gets the image on the first button.
//   * 
//   * @return The image that appears on the first tool bar button.
//   */
//  public Icon getFirstIcon()
//  {
//    return firstButton.getIcon();
//  }
//  
//  /**
//   * Puts an image on the prior button.
//   * 
//   * @param priorIcon The <CODE>Icon</CODE> for the prior tool bar button.
//   */
//  public void setPriorIcon(Icon priorIcon)
//  {
//    priorButton.setIcon(priorIcon);
//  }
//
//  /**
//   * Gets the image on the prior button.
//   * 
//   * @return The image that appears on the prior tool bar button.
//   */
//  public Icon getPriorIcon()
//  {
//    return priorButton.getIcon();
//  }
//  
//  /**
//   * Puts an image on the next button.
//   * 
//   * @param nextIcon The <CODE>Icon</CODE> for the next toolbar button.
//   */
//  public void setNextIcon(Icon nextIcon)
//  {
//    nextButton.setIcon(nextIcon);
//  }
//
//  /**
//   * Gets the image on the next button.
//   * 
//   * @return The imabe that appears on the next tool bar button.
//   */
//  public Icon getNextIcon()
//  {
//    return nextButton.getIcon();  
//  }
//  
//  /**
//   * Puts an image on the last button.
//   * 
//   * @param lastIcon The <CODE>Icon</CODE> for the last toolbar button.
//   */
//  public void setLastIcon(Icon lastIcon)
//  {
//    lastButton.setIcon(lastIcon);
//  }
//
//  /**
//   * Gets tha image on the last record button.
//   * 
//   * @return The icon from the tool bar button that takes the user to the last record.
//   */
//  public Icon getLastIcon()
//  {
//    return lastButton.getIcon();
//  }
//  
//  /**
//   * Puts an image on the insert button.
//   * 
//   * @param insertIcon The <CODE>Icon</CODE> for the insert toolbar button.
//   */
//  public void setInsertIcon(Icon insertIcon)
//  {
//    insertButton.setIcon(insertIcon);
//  }
//
//  /**
//   * Puts an image on the delete button.
//   * 
//   * @param deleteIcon The <CODE>Icon</CODE> for the delete toolbar button.
//   */
//  public void setDeleteIcon(Icon deleteIcon)
//  {
//    deleteButton.setIcon(deleteIcon);
//  }

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

//  /**
//   * Called when the data in the table changes. This method enables the 
//   * appropriate buttons on the toolbar.
//   * 
//   * @param e The <CODE>TableModelEvent</CODE> that caused the invocation of this method.
//   */
//  private void tableModel_tableChanged(TableModelEvent e)
//  {
//    enableControls();
//    if(e.getFirstRow() == TableModelEvent.HEADER_ROW)
//      tableStructureChanged();
//  }
//
//  /**
//   * Called when the table structure is changed. This method is called by the 
//   * <CODE>tableModel_tableChanged</CODE> method whenever that method is invoked
//   * to handle a table structure change.
//   */
//  private void tableStructureChanged()
//  {
//    SwingUtilities.invokeLater(new Runnable()
//    {
//      public void run()
//      {
//        restoreColumnWidths();
//        arrangeColumns();
//        final String key = "PowerSupplyFunctionsFrame." + tableModel.getTableName();
//        int columnCount = table.getColumnCount();
//        TableColumnModel allColumns = table.getColumnModel();
//        for(int i=0;i<columnCount;i++)
//        {
//          final String propertyName = key + "." + table.getColumnName(i) + ".width";
//          allColumns.getColumn(i).addPropertyChangeListener(new java.beans.PropertyChangeListener()
//          {
//            public void propertyChange(PropertyChangeEvent e)
//            {
//              if(e.getPropertyName().equals("preferredWidth"))
//                getApplicationProperties().setProperty(propertyName, e.getNewValue().toString());
//            }
//          });
//        }//for(int i=0;i<columnCount;i++)
//        //Need to save column positions when moved.
//        allColumns.addColumnModelListener(new TableColumnModelListener()
//        {
//          public void columnAdded(TableColumnModelEvent e)
//          {
//          }
//    
//          public void columnMarginChanged(ChangeEvent e)
//          {
//          }
//    
//          public void columnMoved(final TableColumnModelEvent e)
//          {
//            saveColumnPositions();
//          }
//    
//          public void columnRemoved(TableColumnModelEvent e)
//          {
//          }
//    
//          public void columnSelectionChanged(ListSelectionEvent e)
//          {
//          }
//        });
//      }
//    });
//  }
//  
//  /**
//   * This method puts the columns in the order the user left them in.
//   */
//  private void arrangeColumns()
//  {
//    final String key = "PowerSupplyFunctionsFrame." + tableModel.getTableName();
//    Properties settings = getApplicationProperties();
//    String columnCountProperty = settings.getProperty(key + ".ColumnCount");
//    if(columnCountProperty != null)
//    {
//      int columnCount = Integer.parseInt(columnCountProperty), moveTo = 0;
//      int tableColumnCount = table.getColumnCount();
//      ArrayList modelColumns = new ArrayList(tableColumnCount);
//      for(int i=0;i<tableColumnCount;i++)
//        modelColumns.add(tableModel.getColumnName(i));
//      for(int i=0;i<columnCount;i++)
//      {
//        String currentColumnName = settings.getProperty(key + ".Column" + i);
//        if(currentColumnName != null)
//        {
//          int modelIndex = modelColumns.indexOf(currentColumnName);
//          if(modelIndex >= 0)//Column is in the model, move it...
//          {
//            int moveFrom = table.convertColumnIndexToView(modelIndex);//Convert to view index.
//            if(moveTo != moveFrom)
//              table.moveColumn(moveFrom, moveTo);
//            moveTo++;//Next position.
//          }//if(modelIndex >= 0)
//        }//if(currentColumnName != null)
//      }//for(int i=0;i<columnCount;i++)
//    }//if(columnCountProperty != null)
//  }
//
//  /**
//   * This method restores the column widths to the widths saved in the 
//   * applicaiton's properties file.
//   */
//  private void restoreColumnWidths()
//  {
//    final String key = "PowerSupplyFunctionsFrame." + tableModel.getTableName();
//    TableColumnModel allColumns = table.getColumnModel();
//    int columnCount = allColumns.getColumnCount();
//    TableColumn currentColumn;
//    String widthProperty;
//    for(int i=0;i<columnCount;i++)
//    {
//      currentColumn = allColumns.getColumn(i);
//      final String propertyName = key + "." + table.getColumnName(i) + ".width";
//      widthProperty = getApplicationProperties().getProperty(propertyName, "100");
//      currentColumn.setPreferredWidth(Integer.parseInt(widthProperty));
//      currentColumn.addPropertyChangeListener(new java.beans.PropertyChangeListener()
//      {
//        public void propertyChange(PropertyChangeEvent e)
//        {
//          if(e.getPropertyName().equals("preferredWidth"))
//            getApplicationProperties().setProperty(propertyName, e.getNewValue().toString());
//        }
//      });
//    }//for(int i=0;i<columnCount;i++)
//  }
//
//  /**
//   * This method saves the positions of the table's columns in the application's
//   * properties file.
//   */
//  private void saveColumnPositions()
//  {
//    SwingUtilities.invokeLater(new Runnable()
//    {
//      public void run()
//      {
//        String propertyName = "PowerSupplyFunctionsFrame." + tableModel.getTableName();
//        int columnCount = table.getColumnCount();
//        Properties settings = getApplicationProperties();
//        settings.setProperty(propertyName + ".ColumnCount", String.valueOf(columnCount));
//        for(int position=0;position<columnCount;position++)
//        {
//          String column = table.getColumnName(position);
//          settings.setProperty(propertyName + ".Column" + position, column);
//        }//for(int position=0;position<columnCount;position++)
//      }
//    });
//  }

  /**
   * Called when the commit button is clicked. This method commits the changes 
   * made.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void commitButton_actionPerformed(ActionEvent e)
  {
    Thread commitThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(powerSupplyFunctions)
        {
          try 
          {
            threadActive = true;
            enableControls();
            powerSupplyFunctions.post();
            dataPanel.setChanged(false);
            powerSupplyFunctions.commit();
          }
          catch(java.sql.SQLException ex)
          {
            ex.printStackTrace();
            showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
          }
          finally
          {
            threadActive = false;
            enableControls();
          }
        }
      }
    });
    commitThread.start();
  }

  /**
   * Called when the rollback button is clicked. This method rolls back the 
   * changes made.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void rollbackButton_actionPerformed(ActionEvent e)
  {
    Thread rollbackThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(powerSupplyFunctions)
        {
          try 
          {
            threadActive = true;
            enableControls();
            powerSupplyFunctions.cancel();
            dataPanel.setChanged(false);
            powerSupplyFunctions.rollback();
            refresh();
            restoreTreeSelection();
          }
          catch(java.sql.SQLException ex)
          {
            ex.printStackTrace();
            showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
          }
          finally
          {
            threadActive = false;
            enableControls();
          }
        }
      }
    });
    rollbackThread.start();
  }

  /**
   * Sets the tree selection to match what is passed in based solely on the 
   * <CODE>String</CODE> values passed into the method, This is done by calling 
   * <CODE>toString()</CODE> on the items in the array and comparing that to the 
   * <CODE>toString()</CODE> method results from the nodes in the tree. This is 
   * done using <CODE>SwingUtilities.invokeLater</CODE> to make the method 
   * thread safe.
   * 
   * @param tree The <CODE>JTree</CODE> of which to set the selection. Can not be <CODE>null</CODE>.
   * @param pathObjects The values to look for in the tree paths.
   */
  private void selectCorrespondingPath(final Object[] pathObjects)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        if(pathObjects.length <= 0)
          return;
        ArrayList newPathNodes = new ArrayList();
        //pathObjects[0] should always be the tree root.
        TreeModel model = tree.getModel();
        newPathNodes.add(model.getRoot());
        for(int i=1;i<pathObjects.length;i++)
        {
          String stringValue = pathObjects[i].toString();
          Object parentNode = newPathNodes.get(i-1);
          Object newNode = findNode(parentNode, stringValue);
          if(newNode == null)
            break;
          else
            newPathNodes.add(newNode);
        }//for(int i=1;i<pathObjects.length;i++)
        tree.setSelectionPath(new TreePath(newPathNodes.toArray()));
      }
    });
  }

  /**
   * Looks through the given <CODE>TreeNode</CODE> for a child whose 
   * <CODE>toString()</CODE> value matches the given text.
   * 
   * @param model The <CODE>TreeModel</CODE> containing the <CODE>parentNode</CODE>.
   * @param parentNode The node in the tree in which to search.
   * @param stringValue The value to look for in the parent node.
   * @return The node matching the given text or <CODE>null</CODE> if a match was not found.
   */
  private Object findNode(Object parentNode, String stringValue)
  {
    TreeModel model = tree.getModel();
    int childCount = model.getChildCount(parentNode);
    for(int i=0;i<childCount;i++)
    {
      Object currentChild = model.getChild(parentNode, i);
      if(currentChild.toString().equals(stringValue))
        return currentChild;
    }//for(int i=0;i<childCount;i++)
    return null;
  }

  /**
   * Restores the tree selectino to the value selected previously and refreshes
   * the data panel.
   */
  private void restoreTreeSelection()
  {
    if(selectedPath != null)
    {
      final TreePath oldPath = selectedPath;
      selectedPath = null;
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          selectCorrespondingPath(oldPath.getPath());
        }
      });
    }
  }
//  /**
//   * Called when the commit button is clicked. This method commits the changes 
//   * made.
//   * 
//   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
//   */
//  private void firstButton_actionPerformed(ActionEvent e)
//  {
//    table.getSelectionModel().setSelectionInterval(0, 0);
//  }
//
//  /**
//   * Called when the prior button is clicked. This method select the prior row 
//   * in the table.
//   * 
//   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
//   */
//  private void priorButton_actionPerformed(ActionEvent e)
//  {
//    int row = table.getSelectedRow() - 1;
//    table.getSelectionModel().setSelectionInterval(row, row);
//  }
//
//  /**
//   * Called when the next button is clicked. This method selects the next row in 
//   * the table.
//   * 
//   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
//   */
//  private void nextButton_actionPerformed(ActionEvent e)
//  {
//    int row = table.getSelectedRow() + 1;
//    table.getSelectionModel().setSelectionInterval(row, row);
//  }
//
//  /**
//   * Called when the last button is clicked. This method selects the last row in 
//   * the table.
//   * 
//   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
//   */
//  private void lastButton_actionPerformed(ActionEvent e)
//  {
//    int row = table.getRowCount() - 1;
//    table.getSelectionModel().setSelectionInterval(row, row);
//  }

  /**
   * Called when the post button is clicked. This method posts the changes made.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void postButton_actionPerformed(ActionEvent e)
  {
    Thread postThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(powerSupplyFunctions)
        {
          try 
          {
            threadActive = true;
            enableControls();
            powerSupplyFunctions.post();
            dataPanel.setChanged(false);
            enableControls();
          }
          catch(java.sql.SQLException ex)
          {
            ex.printStackTrace();
            showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
          }
          finally
          {
            threadActive = false;
            enableControls();
          }
        }
      }
    });
    postThread.start();
  }

  /**
   * Called when the ccancel button is clicked. This method cancels the changes 
   * made that have not been posted.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void cancelButton_actionPerformed(ActionEvent e)
  {
    Thread cancelThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(powerSupplyFunctions)
        {
          try 
          {
            threadActive = true;
            enableControls();
            powerSupplyFunctions.cancel();
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
            Object selectedObject = selectedNode.getUserObject();
            updateDataPanel(selectedObject);
            dataPanel.setChanged(false);
            enableControls();
          }
          catch(java.sql.SQLException ex)
          {
            ex.printStackTrace();
            showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
          }
          finally
          {
            threadActive = false;
            enableControls();
          }
        }
      }
    });
    cancelThread.start();
  }

  /**
   * Called when the state of a checkbox on the data panel is changed.
   * 
   * @param e The <CODE>ChangeEvent</CODE> that caused the invocation of this method.
   */
  private void dataPanel_stateChanged(ChangeEvent e)
  {
    enableControls();
  }

  /**
   * Called when the text in a text field on the data panel is changed.
   * 
   * @param e The <CODE>DocumentEvent</CODE> that caused the invocation of this method.
   */
  private void dataPanel_changedUpdate(DocumentEvent e)
  {
    enableControls();
  }

  /**
   * Called when the text is inserted into a text field on the data panel.
   * 
   * @param e The <CODE>DocumentEvent</CODE> that caused the invocation of this method.
   */
  private void dataPanel_insertUpdate(DocumentEvent e)
  {
    enableControls();
  }
  
  /**
   * Called when the text is removed from a text field on the data panel.
   * 
   * @param e The <CODE>DocumentEvent</CODE> that caused the invocation of this method.
   */
  private void dataPanel_removeUpdate(DocumentEvent e)
  {
    enableControls();
  }
}