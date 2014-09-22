package org.csstudio.mps.sns.apps.powersupplystartupexport;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.sql.*;

import java.util.*;

import javax.sql.*;

import javax.swing.*;
import javax.swing.event.*;

import oracle.sql.*;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.application.JeriInternalFrame;

/**
 * Provides an interface for generating a power supply startup file.
 * 
 * @author Chris Fowlkes
 */
public class PowerSupplyStartupExportFrame extends JeriInternalFrame 
{
  private JPanel listsPanel = new JPanel();
  private GridLayout listsPanelLayout = new GridLayout();
  private JPanel outerButtonPanel = new JPanel();
  private BorderLayout outerButtonPanelLayout = new BorderLayout();
  private JPanel innerButtonPanel = new JPanel();
  private JButton exportButton = new JButton();
  private JButton cancelButton = new JButton();
  private JPanel labelPanel = new JPanel();
  private GridLayout labelPanelLayout = new GridLayout();
  private JLabel selectedLabel = new JLabel();
  private JLabel availableLabel = new JLabel();
  private JPanel selectedListPanel = new JPanel();
  private BorderLayout selectedListPanelLayout = new BorderLayout();
  private JScrollPane selectedListScrollPane = new JScrollPane();
  private JList selectedList = new JList();
  private JPanel centerButtonPanel = new JPanel();
  private GridLayout centerButtonPanelLayout = new GridLayout();
  private JPanel dropSelectedPanel = new JPanel();
  private BorderLayout dropSelectedPanelLayout = new BorderLayout();
  private JButton dropSelectedButton = new JButton();
  private JPanel dropAllPanel = new JPanel();
  private BorderLayout dropAllPanelLayout = new BorderLayout();
  private JButton dropAllButton = new JButton();
  private JPanel addSelectedPanel = new JPanel();
  private BorderLayout addSelectedPanelLayout = new BorderLayout();
  private JButton addSelectedButton = new JButton();
  private JPanel addAllPanel = new JPanel();
  private BorderLayout addAllPanelLayout = new BorderLayout();
  private JButton addAllButton = new JButton();
  private JPanel moveUpPanel = new JPanel();
  private BorderLayout moveUpPanelLayout = new BorderLayout();
  private JButton moveUpButton = new JButton();
  private JPanel moveDownPanel = new JPanel();
  private BorderLayout moveDownPanelLayout = new BorderLayout();
  private JButton moveDownButton = new JButton();
  private JScrollPane availableListScrollPane = new JScrollPane();
  private JList availableList = new JList();
  private DefaultListModel selectedListModel = new DefaultListModel();
  private DefaultListModel availableListModel = new DefaultListModel();

  /**
   * Creates a new <CODE>PowerSupplyStartupExportFrame</CODE>.
   */
  public PowerSupplyStartupExportFrame()
  {
    try
    {
      jbInit();
      selectedList.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          enableButtons();
        }
      });
      availableList.getSelectionModel().addListSelectionListener(new ListSelectionListener()
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
   * @throws java.lang.Exception Thron on initialization error.
   */
  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(400, 300));
    this.setTitle("Power Supply Startup Export");
    listsPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    listsPanel.setLayout(listsPanelLayout);
    listsPanelLayout.setHgap(5);
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    exportButton.setText("Export");
    exportButton.setMnemonic('x');
    exportButton.setEnabled(false);
    exportButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          exportButton_actionPerformed(e);
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
    labelPanel.setLayout(labelPanelLayout);
    labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    selectedLabel.setText("Selected IOCs");
    availableLabel.setText("Available IOCs");
    selectedListPanel.setLayout(selectedListPanelLayout);
    selectedListPanelLayout.setHgap(5);
    selectedList.setModel(selectedListModel);
    centerButtonPanel.setLayout(centerButtonPanelLayout);
    centerButtonPanelLayout.setRows(6);
    dropSelectedPanel.setLayout(dropSelectedPanelLayout);
    dropSelectedButton.setEnabled(false);
    dropSelectedButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          dropSelectedButton_actionPerformed(e);
        }
      });
    dropAllPanel.setLayout(dropAllPanelLayout);
    dropAllButton.setEnabled(false);
    dropAllButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          dropAllButton_actionPerformed(e);
        }
      });
    addSelectedPanel.setLayout(addSelectedPanelLayout);
    addSelectedButton.setEnabled(false);
    addSelectedButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          addSelectedButton_actionPerformed(e);
        }
      });
    addAllPanel.setLayout(addAllPanelLayout);
    addAllButton.setEnabled(false);
    addAllButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          addAllButton_actionPerformed(e);
        }
      });
    moveUpPanel.setLayout(moveUpPanelLayout);
    moveUpButton.setEnabled(false);
    moveUpButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          moveUpButton_actionPerformed(e);
        }
      });
    moveDownPanel.setLayout(moveDownPanelLayout);
    moveDownButton.setEnabled(false);
    moveDownButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          moveDownButton_actionPerformed(e);
        }
      });
    availableList.setModel(availableListModel);
    selectedListModel.addListDataListener(new ListDataListener()
      {
        public void intervalAdded(ListDataEvent e)
        {
          selectedListModel_contentsChanged(e);
        }

        public void intervalRemoved(ListDataEvent e)
        {
          selectedListModel_contentsChanged(e);
        }

        public void contentsChanged(ListDataEvent e)
        {
          selectedListModel_contentsChanged(e);
        }
      });
    selectedListScrollPane.getViewport().add(selectedList, null);
    availableListScrollPane.getViewport();
    availableListScrollPane.getViewport();
    selectedListPanel.add(selectedListScrollPane, BorderLayout.CENTER);
    selectedListPanel.add(centerButtonPanel, BorderLayout.EAST);
    selectedListScrollPane.getViewport();
    centerButtonPanel.add(dropSelectedPanel, null);
    centerButtonPanel.add(dropAllPanel, null);
    centerButtonPanel.add(addSelectedPanel, null);
    centerButtonPanel.add(addAllPanel, null);
    centerButtonPanel.add(moveUpPanel, null);
    centerButtonPanel.add(moveDownPanel, null);
    dropSelectedPanel.add(dropSelectedButton, BorderLayout.NORTH);
    dropAllPanel.add(dropAllButton, BorderLayout.NORTH);
    addSelectedPanel.add(addSelectedButton, BorderLayout.NORTH);
    addAllPanel.add(addAllButton, BorderLayout.NORTH);
    moveUpPanel.add(moveUpButton, BorderLayout.NORTH);
    moveDownPanel.add(moveDownButton, BorderLayout.NORTH);
    listsPanel.add(selectedListPanel, null);
    availableListScrollPane.getViewport().add(availableList, null);
    listsPanel.add(availableListScrollPane, null);
    this.getContentPane().add(listsPanel, BorderLayout.CENTER);
    innerButtonPanel.add(exportButton, null);
    innerButtonPanel.add(cancelButton, null);
    outerButtonPanel.add(innerButtonPanel, BorderLayout.EAST);
    this.getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);
    labelPanel.add(selectedLabel, null);
    labelPanel.add(availableLabel, null);
    this.getContentPane().add(labelPanel, BorderLayout.NORTH);
  }

  /**
   * Called when the drop selected button is clicked. This method moves the IOCs 
   * selected in the selected list to the available list.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void dropSelectedButton_actionPerformed(ActionEvent e)
  {
    Object[] iocsToMove = selectedList.getSelectedValues();
    for(int i=0;i<iocsToMove.length;i++)
      if(selectedListModel.removeElement(iocsToMove[i]))
        availableListModel.addElement(iocsToMove[i]);
      else//Should not be able to get here since we know the item is in the list.
        JOptionPane.showMessageDialog(this, "Internal error removing " + iocsToMove, "Internal Error", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Called when the drop all button is clicked. This method moves all of the 
   * IOCs in the selected list to the available list.
   * 
   * @param The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void dropAllButton_actionPerformed(ActionEvent e)
  {
    Enumeration iocsToMove = selectedListModel.elements();
    while(iocsToMove.hasMoreElements())
      availableListModel.addElement(iocsToMove.nextElement());
    selectedListModel.removeAllElements();
  }

  /**
   * Called when the add selected button is clicked. This method moves the IOCs 
   * selected in the available list to the selected list.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void addSelectedButton_actionPerformed(ActionEvent e)
  {
    Object[] iocsToMove = availableList.getSelectedValues();
    for(int i=0;i<iocsToMove.length;i++)
      if(availableListModel.removeElement(iocsToMove[i]))
        selectedListModel.addElement(iocsToMove[i]);
      else//Should not be able to get here since we know the item is in the list.
        JOptionPane.showMessageDialog(this, "Internal error selecting " + iocsToMove, "Internal Error", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Called when the add all button is clicked. This method moves all of the 
   * IOCs in the available list to the selected list.
   * 
   * @param The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void addAllButton_actionPerformed(ActionEvent e)
  {
    Enumeration iocsToMove = availableListModel.elements();
    while(iocsToMove.hasMoreElements())
      selectedListModel.addElement(iocsToMove.nextElement());
    availableListModel.removeAllElements();
  }

  /**
   * Called when the move up button is clicked. This method moves the IOCs 
   * selected in the selected list up one in the list.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void moveUpButton_actionPerformed(ActionEvent e)
  {
    int[] selectedIndices = selectedList.getSelectedIndices();
    Arrays.sort(selectedIndices);
    Object itemToMove = selectedListModel.remove(selectedIndices[0] - 1);
    int moveTo = selectedIndices[selectedIndices.length - 1];
    selectedListModel.insertElementAt(itemToMove, moveTo);
    for(int i=0;i<selectedIndices.length;i++)
      selectedIndices[i]--;
    selectedList.setSelectedIndices(selectedIndices);
    selectedList.ensureIndexIsVisible(selectedIndices[0]);
  }

  /**
   * Called when the move down button is clicked. This method moves the IOCs 
   * selected in the selected list up one in the list.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void moveDownButton_actionPerformed(ActionEvent e)
  {
    int[] selectedIndices = selectedList.getSelectedIndices();
    Arrays.sort(selectedIndices);
    int moveFrom = selectedIndices[selectedIndices.length - 1] + 1;
    Object itemToMove = selectedListModel.remove(moveFrom);
    selectedListModel.insertElementAt(itemToMove, selectedIndices[0]);
    for(int i=0;i<selectedIndices.length;i++)
      selectedIndices[i]++;
    selectedList.setSelectedIndices(selectedIndices);
    int lastIndex = selectedIndices[selectedIndices.length - 1];
    selectedList.ensureIndexIsVisible(lastIndex);
  }

  /**
   * Calls a stored procedure to create the startup file.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void exportButton_actionPerformed(ActionEvent e)
  {
    try
    {
      Connection oracleConnection = getDataSource().getConnection();
      try
      {
        StringBuffer sql = new StringBuffer("{? = call ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".EPICS_PKG.CREATE_PS_STARTUP_FILE(?)}");
        CallableStatement procedure = oracleConnection.prepareCall(sql.toString());
        try
        {
          procedure.registerOutParameter(1, Types.VARCHAR);
          ArrayDescriptor arrayDescription = ArrayDescriptor.createDescriptor(MPSBrowserView.SCHEMA + ".DVC_ID_TAB_TYP", oracleConnection);
          Object[] deviceIDs = selectedListModel.toArray();
          ARRAY deviceIDArray = new ARRAY(arrayDescription, oracleConnection, deviceIDs);
          procedure.setArray(2, deviceIDArray);
          procedure.execute();
//          StringBuffer startupFile = new StringBuffer();
//          Object[] fileContents = (Object[])procedure.getArray(1).getArray();
//          String newLine = System.getProperty("line.separator");
//          for(int i=0;i<fileContents.length;i++)
//          {
//            startupFile.append(fileContents[i]);
//            startupFile.append(newLine);
//          }//for(int i=0;i<fileContents.length;i++)
//          System.out.println(startupFile);
          System.out.println(procedure.getString(1));
        }//try
        finally
        {
          procedure.close();
        }//finally
      }//try
      finally
      {
        oracleConnection.close();
      }//finally
    }//try
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }//catch(java.sql.SQLException ex)
  }

  /**
   * Closes the window without generating the file.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void cancelButton_actionPerformed(ActionEvent e)
  {
    setVisible(false);
    fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSED);
  }

  /**
   * Sets the <CODE>Icon</CODE> on the add selected button.
   * 
   * @param newLeftArrowIcon The <CODE>Icon</CODE> on the add selected button.
   */
  public void setLeftArrowIcon(Icon newLeftArrowIcon)
  {
    addSelectedButton.setIcon(newLeftArrowIcon);
  }

  /**
   * Sets the <CODE>Icon</CODE> on the add all button.
   * 
   * @param newDoubleLeftArrowIcon The <CODE>Icon</CODE> on the add all button.
   */
  public void setDoubleLeftArrowIcon(Icon newDoubleLeftArrowIcon)
  {
    addAllButton.setIcon(newDoubleLeftArrowIcon);
  }

  /**
   * Sets the <CODE>Icon</CODE> on the drop selected button.
   * 
   * @param newRightArrowIcon The <CODE>Icon</CODE> on the drop selected button.
   */
  public void setRightArrowIcon(Icon newRightArrowIcon)
  {
    dropSelectedButton.setIcon(newRightArrowIcon);
  }

  /**
   * Sets the <CODE>Icon</CODE> on the drop all button.
   * 
   * @param newDoubleRightArrowIcon The <CODE>Icon</CODE> on the drop all button.
   */
  public void setDoubleRightArrowIcon(Icon newDoubleRightArrowIcon)
  {
    dropAllButton.setIcon(newDoubleRightArrowIcon);
  }

  /**
   * Sets the <CODE>Icon</CODE> on the move up button.
   * 
   * @param newUpArrowIcon The <CODE>Icon</CODE> on the move up button.
   */
  public void setUpArrowIcon(Icon newUpArrowIcon)
  {
    moveUpButton.setIcon(newUpArrowIcon);
  }

  /**
   * Sets the <CODE>Icon</CODE> on the move down button.
   * 
   * @param newDownArrowIcon The <CODE>Icon</CODE> on the move down button.
   */
  public void setDownArrowIcon(Icon newDownArrowIcon)
  {
    moveDownButton.setIcon(newDownArrowIcon);
  }

  /**
   * Enables and disables the buttons in the interface.
   */
  public void enableButtons()
  {
    addSelectedButton.setEnabled(availableList.getSelectedIndices().length > 0);
    addAllButton.setEnabled(availableListModel.getSize() > 0);
    int[] leftSelectedIndices = selectedList.getSelectedIndices();
    dropSelectedButton.setEnabled(leftSelectedIndices.length > 0);
    int leftCount = selectedListModel.getSize();
    dropAllButton.setEnabled(leftCount > 0);
    if(leftSelectedIndices.length > 0)
    {
      //Can only enable if contiguous and there is room to move. If moving up
      //first row must not be selected, if moving down last row must not be 
      //selected.
      Arrays.sort(leftSelectedIndices);
      boolean contiguous = true;
      int i = 1;
      while(contiguous && i < leftSelectedIndices.length)
      {
        int currentIndex = leftSelectedIndices[i];
        int lastIndex = leftSelectedIndices[i - 1];
        if(currentIndex != lastIndex + 1)
          contiguous = false;
        else
          i++;
      }//while(contiguous && i < leftSelectedIndices.length)
      if(contiguous)
      {
        moveUpButton.setEnabled(! selectedList.isSelectedIndex(0));
        moveDownButton.setEnabled(! selectedList.isSelectedIndex(leftCount - 1));
      }//if(contiguous)
      else
      {
        moveUpButton.setEnabled(false);
        moveDownButton.setEnabled(false);
      }//else
    }//if(leftSelectedIndices.length > 0)
    else
    {
      moveUpButton.setEnabled(false);
      moveDownButton.setEnabled(false);
    }//else
    exportButton.setEnabled(leftCount > 0);
  }

  /**
   * Sets the datasource used by the interface to connect to the database. This 
   * method also causes the data to be loaded from the database.
   * 
   * @param connectionPool The <CODE>DataSource</CODE> used to connect to the database.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void setDataSource(DataSource connectionPool) throws SQLException
  {
    super.setDataSource(connectionPool);
    Connection oracleConnection = connectionPool.getConnection();
    try
    {
      Statement query = oracleConnection.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT DVC_ID FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".DVC WHERE SUBSYS_ID = 'Mag' AND DVC_TYPE_ID = 'PS'");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          availableListModel.clear();
          selectedListModel.clear();
          while(result.next())
            availableListModel.addElement(result.getString("DVC_ID"));
          enableButtons();
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
      oracleConnection.close();
    }//finally
  }

  /**
   * Called when the contents of the selected list change. This method calls
   * <CODE>enableButtons()</CODE>.
   * 
   * @param e The <CODE>ListDataEvent</CODE> that caused the invocation of this method.
   */
  private void selectedListModel_contentsChanged(ListDataEvent e)
  {
    enableButtons();
  }
}