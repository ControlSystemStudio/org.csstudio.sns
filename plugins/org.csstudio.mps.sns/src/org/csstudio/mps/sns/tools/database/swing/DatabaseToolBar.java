package org.csstudio.mps.sns.tools.database.swing;

import org.csstudio.mps.sns.application.Application;

import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Provides a tool bar with buttons for the common relational database 
 * operations.
 * 
 * @author Chris Fowlkes
 */
public class DatabaseToolBar extends JToolBar 
{
  private JButton commitButton = new JButton();
  private static ImageIcon commitIcon = new ImageIcon();
  private JButton rollbackButton = new JButton();
  private static ImageIcon rollbackIcon = new ImageIcon();
  private JButton filterButton = new JButton();
  private static ImageIcon newFilterIcon = new ImageIcon();
  private static ImageIcon editFilterIcon = new ImageIcon();
  private JButton removeFilterButton = new JButton();
  private static ImageIcon removeFilterIcon = new ImageIcon();
  private JButton firstButton = new JButton();
  private static ImageIcon firstIcon = new ImageIcon();
  private JButton priorButton = new JButton();
  private static ImageIcon priorIcon = new ImageIcon();
  private JButton nextButton = new JButton();
  private static ImageIcon nextIcon = new ImageIcon();
  private JButton lastButton = new JButton();
  private static ImageIcon lastIcon = new ImageIcon();
  private JButton addButton = new JButton();
  private static ImageIcon addIcon = new ImageIcon();
  private JButton removeButton = new JButton();
  private static ImageIcon removeIcon = new ImageIcon();
  private JButton postButton = new JButton();
  private static ImageIcon postIcon = new ImageIcon();
  private JButton cancelButton = new JButton();
  private static ImageIcon cancelIcon = new ImageIcon();
  private JButton refreshButton = new JButton();
  private static ImageIcon refreshIcon = new ImageIcon();
  private JTable table;
  private ListSelectionListener tableSelectionListener;
  private DatabaseTableModel model;
  
  /**
   * Creates a new <CODE>DatabaseToolBar</CODE>.
   */
  public DatabaseToolBar()
  {
    try
    {
      jbInit();
      tableSelectionListener = new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          enableButtons();
        }
      };
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

  }

  /**
   * Loads the images for the icons used in the tool bar from the image files in 
   * the resources directory.
   */
  static
  {
    try
    {
      Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
      Class thisClass = Class.forName("org.csstudio.mps.sns.tools.database.swing.DatabaseToolBar");
      commitIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/SaveDB.gif")));
      rollbackIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/Undo.gif")));
      newFilterIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/NewSheet.gif")));
      editFilterIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/UpdateSheet.gif")));
      removeFilterIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/DeleteSheet.gif")));
      firstIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/Begin.gif")));
      priorIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/Left.gif")));
      nextIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/Right.gif")));
      lastIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/End.gif")));
      addIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/Plus.gif")));
      removeIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/Minus.gif")));
      postIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/Check.gif")));
      cancelIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/Delete.gif")));
      refreshIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/RotCWDown.gif")));
    }
    catch(java.lang.Exception ex)
    {
      ex.printStackTrace();
    }
  }

  /**
   * Component initialization.
   * 
   * @throws java.lang.Exception Thrown on initialization error.
   */
  private void jbInit() throws Exception
  {
    commitButton.setActionCommand("commitButton");
    commitButton.setToolTipText("Commit");
    commitButton.setEnabled(false);
    commitButton.setMargin(new Insets(2, 2, 2, 2));
    commitButton.setIcon(commitIcon);
    rollbackButton.setToolTipText("Rollback");
    rollbackButton.setEnabled(false);
    rollbackButton.setMargin(new Insets(2, 2, 2, 2));
    rollbackButton.setIcon(rollbackIcon);
    filterButton.setToolTipText("New Filter");
    filterButton.setEnabled(true);
    filterButton.setMargin(new Insets(2, 2, 2, 2));
    filterButton.setIcon(newFilterIcon);
    removeFilterButton.setToolTipText("Remove Filter");
    removeFilterButton.setEnabled(false);
    removeFilterButton.setMargin(new Insets(2, 2, 2, 2));
    removeFilterButton.setIcon(removeFilterIcon);
    firstButton.setToolTipText("First Record");
    firstButton.setEnabled(false);
    firstButton.setMargin(new Insets(2, 2, 2, 2));
    firstButton.setIcon(firstIcon);
    firstButton.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e)
                                    {
                                      firstButton_actionPerformed(e);
                                    }
                                  }
    );
    priorButton.setToolTipText("Prior Record");
    priorButton.setEnabled(false);
    priorButton.setMargin(new Insets(2, 2, 2, 2));
    priorButton.setIcon(priorIcon);
    priorButton.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e)
                                    {
                                      priorButton_actionPerformed(e);
                                    }
                                  }
    );
    nextButton.setToolTipText("Next Record");
    nextButton.setEnabled(false);
    nextButton.setMargin(new Insets(2, 2, 2, 2));
    nextButton.setIcon(nextIcon);
    nextButton.addActionListener(new ActionListener() {
                                   public void actionPerformed(ActionEvent e)
                                   {
                                     nextButton_actionPerformed(e);
                                   }
                                 }
    );
    lastButton.setToolTipText("Last Record");
    lastButton.setEnabled(false);
    lastButton.setMargin(new Insets(2, 2, 2, 2));
    lastButton.setIcon(lastIcon);
    lastButton.addActionListener(new ActionListener() {
                                   public void actionPerformed(ActionEvent e)
                                   {
                                     lastButton_actionPerformed(e);
                                   }
                                 }
    );
    addButton.setToolTipText("Insert Record");
    addButton.setEnabled(true);
    addButton.setMargin(new Insets(2, 2, 2, 2));
    addButton.setIcon(addIcon);
    removeButton.setToolTipText("Remove Record");
    removeButton.setEnabled(false);
    removeButton.setMargin(new Insets(2, 2, 2, 2));
    removeButton.setIcon(removeIcon);
    postButton.setMargin(new Insets(2, 2, 2, 2));
    postButton.setToolTipText("Post Edit");
    postButton.setEnabled(false);
    postButton.setIcon(postIcon);
    cancelButton.setMargin(new Insets(2, 2, 2, 2));
    cancelButton.setToolTipText("Cancel Edit");
    cancelButton.setEnabled(false);
    cancelButton.setIcon(cancelIcon);
    refreshButton.setToolTipText("Refresh Data");
    refreshButton.setMargin(new Insets(2, 2, 2, 2));
    refreshButton.setIcon(refreshIcon);
    this.add(commitButton, null);
    this.add(rollbackButton, null);
    this.add(filterButton, null);
    this.add(removeFilterButton, null);
    this.add(firstButton, null);
    this.add(priorButton, null);
    this.add(nextButton, null);
    this.add(lastButton, null);
    this.add(addButton, null);
    this.add(removeButton, null);
    this.add(postButton, null);
    this.add(cancelButton, null);
    this.add(refreshButton, null);
  }

  /**
   * Shows or hides the two filter buttons. They are visible by default.
   * 
   * @param filterButtonsVisible Pass as <CODE>true</CODE> to show the buttons, <CODE>false</CODE> to hide them.
   */
  public void setFilterButtonsVisible(boolean filterButtonsVisible)
  {
    filterButton.setVisible(filterButtonsVisible);
    removeFilterButton.setVisible(filterButtonsVisible);
  }
  
  /**
   * Determines if the filter buttons are visible. They are visible by default.
   * 
   * @return <CODE>true</CODE> if the buttons are visible, <CODE>false</CODE> otherwise.
   */
  public boolean isFilterButtonsVisible()
  {
    return filterButton.isVisible();
  }

  /**
   * Shows or hides the commit, rollback, add, remove, post, and cancel buttons. 
   * They are visible by default.
   * 
   * @param visible Pass as <CODE>false</CODE> to hide the buttons, <CODE>true</CODE> otherwise.
   */
  public void setEditButtonsVisible(boolean visible)
  {
    commitButton.setVisible(visible);
    rollbackButton.setVisible(visible);
    addButton.setVisible(visible);
    removeButton.setVisible(visible);
    postButton.setVisible(visible);
    cancelButton.setVisible(visible);
  }
  
  /**
   * Determines if the commit, rollback, add, remove, post, and cancel buttons 
   * are visible.
   * 
   * @return <CODE>true</CODE> if the buttons are visible, <CODE>false</CODE> otherwise.
   */
  public boolean isEditButtonsVisible()
  {
    return commitButton.isVisible();
  }
  
  /**
   * Shows or hides the insert and delete record buttons. They are visible by 
   * default.
   * 
   * @param addRemoveButtonsVisible Pass as <CODE>true</CODE> to show the buttons, <CODE>false</CODE> to hide them.
   */
  public void setAddRemoveButtonsVisible(boolean addRemoveButtonsVisible)
  {
    addButton.setVisible(addRemoveButtonsVisible);
    removeButton.setVisible(addRemoveButtonsVisible);
  }
  
  /**
   * Determines if the insert and delete record buttons are visible. They are 
   * visible by default.
   * 
   * @return <CODE>true</CODE> if the buttons are visible, <CODE>false</CODE> otherwise.
   */
  public boolean isAddRemoveButtonsVisible()
  {
    return addButton.isVisible();
  }
  
  /**
   * Adds an <CODE>ActionListener</CODE> to the commit button.
   * 
   * @param listener The custom <CODE>ActionListener</CODE> to add to the commit button.
   */
  public void addCommitActionListener(ActionListener listener)
  {
    commitButton.addActionListener(listener);
  }
  
  /**
   * Adds an <CODE>ActionListener</CODE> to the rollback button.
   * 
   * @param listener The custom <CODE>ActionListener</CODE> to add to the rollback button.
   */
  public void addRollbackActionListener(ActionListener listener)
  {
    rollbackButton.addActionListener(listener);
  }
  
  /**
   * Adds an <CODE>ActionListener</CODE> to the filter button.
   * 
   * @param listener The custom <CODE>ActionListener</CODE> to add to the filter button.
   */
  public void addFilterActionListener(ActionListener listener)
  {
    filterButton.addActionListener(listener);
  }
  
  /**
   * Adds an <CODE>ActionListener</CODE> to the remove filter button.
   * 
   * @param listener The custom <CODE>ActionListener</CODE> to add to the remove filter button.
   */
  public void addRemoveFilterActionListener(ActionListener listener)
  {
    removeFilterButton.addActionListener(listener);
  }
  
  /**
   * Adds an <CODE>ActionListener</CODE> to the first record button.
   * 
   * @param listener The custom <CODE>ActionListener</CODE> to add to the first record button.
   */
  public void addFirstActionListener(ActionListener listener)
  {
    firstButton.addActionListener(listener);
  }
  
  /**
   * Adds an <CODE>ActionListener</CODE> to the prior record button.
   * 
   * @param listener The custom <CODE>ActionListener</CODE> to add to the prior record button.
   */
  public void addPriorActionListener(ActionListener listener)
  {
    priorButton.addActionListener(listener);
  }
  
  /**
   * Adds an <CODE>ActionListener</CODE> to the next record button.
   * 
   * @param listener The custom <CODE>ActionListener</CODE> to add to the next record button.
   */
  public void addNextActionListener(ActionListener listener)
  {
    nextButton.addActionListener(listener);
  }
  
  /**
   * Adds an <CODE>ActionListener</CODE> to the last record button.
   * 
   * @param listener The custom <CODE>ActionListener</CODE> to add to the last record button.
   */
  public void addLastActionListener(ActionListener listener)
  {
    lastButton.addActionListener(listener);
  }
  
  /**
   * Adds an <CODE>ActionListener</CODE> to the insert record button.
   * 
   * @param listener The custom <CODE>ActionListener</CODE> to add to the insert record button.
   */
  public void addInsertActionListener(ActionListener listener)
  {
    addButton.addActionListener(listener);
  }
  
  /**
   * Adds an <CODE>ActionListener</CODE> to the remove record button.
   * 
   * @param listener The custom <CODE>ActionListener</CODE> to add to the commit button.
   */
  public void addRemoveActionListener(ActionListener listener)
  {
    removeButton.addActionListener(listener);
  }
  
  /**
   * Adds an <CODE>ActionListener</CODE> to the post button.
   * 
   * @param listener The custom <CODE>ActionListener</CODE> to add to the post button.
   */
  public void addPostActionListener(ActionListener listener)
  {
    postButton.addActionListener(listener);
  }
  
  /**
   * Adds an <CODE>ActionListener</CODE> to the cancel button.
   * 
   * @param listener The custom <CODE>ActionListener</CODE> to add to the cancel button.
   */
  public void addCancelActionListener(ActionListener listener)
  {
    cancelButton.addActionListener(listener);
  }
  
  /**
   * Adds an <CODE>ActionListener</CODE> to the refresh button.
   * 
   * @param listener The custom <CODE>ActionListener</CODE> to add to the refresh button.
   */
  public void addRefreshActionListener(ActionListener listener)
  {
    refreshButton.addActionListener(listener);
  }
  
  /**
   * Enables or diasables the commit button.
   * 
   * @param enabled Pass as <CODE>true</CODE> to enable the button, <CODE>false</CODE> to disable it.
   */
  public void setCommitButtonEnabled(boolean enabled)
  {
    commitButton.setEnabled(enabled);
  }
  
  /**
   * Determines if the commit button is enabled or disabled.
   * 
   * @return <CODE>true</CODE> if enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isCommitButtonEnabled()
  {
    return commitButton.isEnabled();
  }
  
  /**
   * Enables or diasables the rollback button.
   * 
   * @param enabled Pass as <CODE>true</CODE> to enable the button, <CODE>false</CODE> to disable it.
   */
  public void setRollbackButtonEnabled(boolean enabled)
  {
    rollbackButton.setEnabled(enabled);
  }
  
  /**
   * Determines if the rollback button is enabled or disabled.
   * 
   * @return <CODE>true</CODE> if enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isRollbackButtonEnabled()
  {
    return rollbackButton.isEnabled();
  }
  
  /**
   * Enables or diasables the filter button.
   * 
   * @param enabled Pass as <CODE>true</CODE> to enable the button, <CODE>false</CODE> to disable it.
   */
  public void setFilterButtonEnabled(boolean enabled)
  {
    filterButton.setEnabled(enabled);
  }
  
  /**
   * Determines if the filter button is enabled or disabled.
   * 
   * @return <CODE>true</CODE> if enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isFilterButtonEnabled()
  {
    return filterButton.isEnabled();
  }
  
  /**
   * Enables or diasables the remove filter button.
   * 
   * @param enabled Pass as <CODE>true</CODE> to enable the button, <CODE>false</CODE> to disable it.
   */
  public void setRemoveFilterButtonEnabled(boolean enabled)
  {
    removeFilterButton.setEnabled(enabled);
  }
  
  /**
   * Determines if the remove filter button is enabled or disabled.
   * 
   * @return <CODE>true</CODE> if enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isRemoveFilterButtonEnabled()
  {
    return removeFilterButton.isEnabled();
  }
  
  /**
   * Enables or diasables the first button.
   * 
   * @param enabled Pass as <CODE>true</CODE> to enable the button, <CODE>false</CODE> to disable it.
   */
  public void setFirstButtonEnabled(boolean enabled)
  {
    firstButton.setEnabled(enabled);
  }
  
  /**
   * Determines if the first button is enabled or disabled.
   * 
   * @return <CODE>true</CODE> if enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isFirstButtonEnabled()
  {
    return firstButton.isEnabled();
  }
  
  /**
   * Enables or diasables the prior button.
   * 
   * @param enabled Pass as <CODE>true</CODE> to enable the button, <CODE>false</CODE> to disable it.
   */
  public void setPriorButtonEnabled(boolean enabled)
  {
    priorButton.setEnabled(enabled);
  }
  
  /**
   * Determines if the prior button is enabled or disabled.
   * 
   * @return <CODE>true</CODE> if enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isPriorButtonEnabled()
  {
    return rollbackButton.isEnabled();
  }
  
  
  /**
   * Enables or diasables the next button.
   * 
   * @param enabled Pass as <CODE>true</CODE> to enable the button, <CODE>false</CODE> to disable it.
   */
  public void setNextButtonEnabled(boolean enabled)
  {
    nextButton.setEnabled(enabled);
  }
  
  /**
   * Determines if the next button is enabled or disabled.
   * 
   * @return <CODE>true</CODE> if enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isNextButtonEnabled()
  {
    return nextButton.isEnabled();
  }
  
  /**
   * Enables or diasables the last button.
   * 
   * @param enabled Pass as <CODE>true</CODE> to enable the button, <CODE>false</CODE> to disable it.
   */
  public void setLastButtonEnabled(boolean enabled)
  {
    lastButton.setEnabled(enabled);
  }
  
  /**
   * Determines if the last button is enabled or disabled.
   * 
   * @return <CODE>true</CODE> if enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isLastButtonEnabled()
  {
    return lastButton.isEnabled();
  }
  
  /**
   * Enables or diasables the insert button.
   * 
   * @param enabled Pass as <CODE>true</CODE> to enable the button, <CODE>false</CODE> to disable it.
   */
  public void setInsertButtonEnabled(boolean enabled)
  {
    addButton.setEnabled(enabled);
  }
  
  /**
   * Determines if the insert button is enabled or disabled.
   * 
   * @return <CODE>true</CODE> if enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isInsertButtonEnabled()
  {
    return addButton.isEnabled();
  }
  
  /**
   * Enables or diasables the remove button.
   * 
   * @param enabled Pass as <CODE>true</CODE> to enable the button, <CODE>false</CODE> to disable it.
   */
  public void setRemoveButtonEnabled(boolean enabled)
  {
    removeButton.setEnabled(enabled);
  }
  
  /**
   * Determines if the remove button is enabled or disabled.
   * 
   * @return <CODE>true</CODE> if enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isRemoveButtonEnabled()
  {
    return removeButton.isEnabled();
  }
  
  /**
   * Enables or diasables the post button.
   * 
   * @param enabled Pass as <CODE>true</CODE> to enable the button, <CODE>false</CODE> to disable it.
   */
  public void setPostButtonEnabled(boolean enabled)
  {
    postButton.setEnabled(enabled);
  }
  
  /**
   * Determines if the post button is enabled or disabled.
   * 
   * @return <CODE>true</CODE> if enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isPostButtonEnabled()
  {
    return postButton.isEnabled();
  }
  
  /**
   * Enables or diasables the cancel button.
   * 
   * @param enabled Pass as <CODE>true</CODE> to enable the button, <CODE>false</CODE> to disable it.
   */
  public void setCancelButtonEnabled(boolean enabled)
  {
    cancelButton.setEnabled(enabled);
  }
  
  /**
   * Determines if the cancel button is enabled or disabled.
   * 
   * @return <CODE>true</CODE> if enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isCancelButtonEnabled()
  {
    return cancelButton.isEnabled();
  }
  
  /**
   * Enables or diasables the refresh button.
   * 
   * @param enabled Pass as <CODE>true</CODE> to enable the button, <CODE>false</CODE> to disable it.
   */
  public void setRefreshButtonEnabled(boolean enabled)
  {
    refreshButton.setEnabled(enabled);
  }
  
  /**
   * Determines if the refresh button is enabled or disabled.
   * 
   * @return <CODE>true</CODE> if enabled, <CODE>false</CODE> otherwise.
   */
  public boolean isRefreshButtonEnabled()
  {
    return refreshButton.isEnabled();
  }
  
  /**
   * Sets the <CODE>JTable</CODE> that the toolbar navigates.
   * 
   * @param table The <CODE>JTable</CODE> 
   */
  public void setTable(JTable table)
  {
    if(this.table != null)
      this.table.getSelectionModel().removeListSelectionListener(tableSelectionListener);
    this.table = table;
    if(table != null)
      table.getSelectionModel().addListSelectionListener(tableSelectionListener);
    enableButtons();
  }
  
  /**
   * Gets the <CODE>JTable</CODE> the toolbar navigates.
   * 
   * @return 
   */
  public JTable getTable()
  {
    return table;
  }

  /**
   * Called when the first button is clicked. Selects the first row in the 
   * table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void firstButton_actionPerformed(ActionEvent e)
  {
    selectAndShowRow(0);
  }

  /**
   * Called when the prior button is clicked. Selects the previous row in the 
   * table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void priorButton_actionPerformed(ActionEvent e)
  {
    JTable table = getTable();
    if(table != null)
      selectAndShowRow(table.getSelectionModel().getMinSelectionIndex() - 1);
  }

  /**
   * Called when the next button is clicked. Selects the next row in the table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void nextButton_actionPerformed(ActionEvent e)
  {
    JTable table = getTable();
    if(table != null)
      selectAndShowRow(table.getSelectionModel().getMaxSelectionIndex() + 1);
  }

  /**
   * Called when the last button is clicked. Selects the last row in the table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void lastButton_actionPerformed(ActionEvent e)
  {
    JTable table = getTable();
    if(table != null)
      selectAndShowRow(table.getRowCount() - 1);
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
    JTable table = getTable();
    if(table != null)
    {
      table.getSelectionModel().setSelectionInterval(row, row);
      Rectangle visible = table.getVisibleRect();
      Rectangle firstCell = table.getCellRect(row, 0, true);
      Rectangle scrollTo = new Rectangle(visible.x, firstCell.y, visible.width, firstCell.height);
      table.scrollRectToVisible(scrollTo);
    }
  }
  
  private void enableButtons()
  {
    JTable table = getTable();
    if(table == null)
    {
      firstButton.setEnabled(false);
      priorButton.setEnabled(false);
      nextButton.setEnabled(false);
      lastButton.setEnabled(false);
    }
    else
    {
      //Enabling of navigation buttons depends on which rows are selected.
      int rowCount = table.getRowCount();
      ListSelectionModel selectionModel = table.getSelectionModel();
      firstButton.setEnabled(rowCount > 0 && ! selectionModel.isSelectedIndex(0));
      int firstSelectedRow = selectionModel.getMinSelectionIndex();
      priorButton.setEnabled(firstSelectedRow > 0);
      int lastSelectedRow = selectionModel.getMaxSelectionIndex();
      nextButton.setEnabled(lastSelectedRow >= 0 && lastSelectedRow < rowCount - 1);
      lastButton.setEnabled(rowCount > 0 && ! selectionModel.isSelectedIndex(rowCount - 1));
    }
  }
}
