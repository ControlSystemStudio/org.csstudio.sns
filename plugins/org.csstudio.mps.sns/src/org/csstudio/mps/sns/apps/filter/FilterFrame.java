package org.csstudio.mps.sns.apps.filter;
import org.csstudio.mps.sns.sql.SelectStatement;
import org.csstudio.mps.sns.sql.TableJoin;
import org.csstudio.mps.sns.sql.WhereClauseItem;
import org.csstudio.mps.sns.tools.data.DeviceType;
import org.csstudio.mps.sns.tools.data.EpicsRecordType;
import org.csstudio.mps.sns.tools.data.EpicsSubsystem;
import org.csstudio.mps.sns.tools.data.EpicsSystem;
import org.csstudio.mps.sns.application.JeriDialog;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.sql.*;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Rectangle;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import oracle.jdbc.OracleConnection;
import java.awt.Dimension;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.JProgressBar;
import javax.swing.ListModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.apps.filter.FilterOpenDialog;

/**
 * Provides a dialog for filtering results from the database. This dialog can be
 * used to generate a where clause for a query.
 * 
 * @author Chris Fowlkes
 */
public class FilterFrame extends JeriDialog 
{
  String sqlFragment="";
  BorderLayout windowLayout = new BorderLayout();
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
   * Holds the <CODE>DataSource</CODE> used to connect to the database.
   */
  private JTabbedPane tabbedPane = new JTabbedPane();
  private JPanel builderPanel = new JPanel();
  private JPanel outerButtonPanel = new JPanel();
  private BorderLayout outerButtonPanelLayout = new BorderLayout();
  private JPanel rightButtonPanel = new JPanel();
  private GridLayout rightButtonPanelLayout = new GridLayout();
  private JButton clearButton = new JButton();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private JPanel leftButtonPanel = new JPanel();
  private GridLayout leftButtonPanelLayout = new GridLayout();
  private JButton loadButton = new JButton();
  private JButton saveButton = new JButton();
  private JButton deleteButton = new JButton();
  private GridLayout builderPanelLayout = new GridLayout();
  private JPanel statusBarPanel = new JPanel();
  private BorderLayout statusBarPanelLayout = new BorderLayout();
  private JLabel progressLabel = new JLabel();
  private JProgressBar progressBar = new JProgressBar();
  /**
   * Holds a reference to the class that holds the data and functions used by 
   * the interface.
   */
  private Filter filter = new Filter();
  /**
   * Flag used to determine if a thread is active.
   */
  private boolean threadActive = false;
  private JPanel editorPanel = new JPanel();
  private BorderLayout editorPanelLayout = new BorderLayout();
  private JPanel queryEditPanel = new JPanel();
  private GridLayout queryEditPanelLayout = new GridLayout();
  private JPanel topQueryPanel = new JPanel();
  private JComboBox fieldsCombo = new JComboBox();
  private JComboBox operatorsCombo = new JComboBox();
  private JTextField jTextField1 = new JTextField();
  private JButton addOpenParenButton = new JButton();
  private JPanel bottomQueryPanel = new JPanel();
  private JButton addToFilterButton = new JButton();
  private JButton andIntoFilterButton = new JButton();
  private JButton orIntoFilterButton = new JButton();
  private JTextField jTextField2 = new JTextField();
  private JButton addCloseParenButton = new JButton();
  private JScrollPane scrollPane = new JScrollPane();
  private JTextArea textArea = new JTextArea();
  /**
   * Holds the constant used to determine if the dialog should be or is in 
   * builder mode.
   */
  final public static int BUILDER_MODE = 0;
  /**
   * Holds the constant used to determine if the dialog should be or is in 
   * editor mode.
   */
  final public static int EDITOR_MODE = 1;
  /**
   * Holds all of the information about the tables being joined in the builder
   * in instances of <CODE>TableJoin</CODE>.
   */
  ArrayList tableJoins = new ArrayList();
  /**
   * Holds the instances of <CODE>JList</CODE> that appear on the builder tab.
   * The contents of the <CODE>ArrayList</CODE> will actually be an 
   * array of size three where the first item is the table name, the second item
   * is the column name, and the third item is the <CODE>JList</CODE>.
   */
  private ArrayList listControls = new ArrayList();
  /**
   * Holds the table names associated with each list control. There should be a 
   * one to one relationship between this variable and 
   * <CODE>listControls</CODE>.
   */
  private ArrayList listControlTableNames = new ArrayList();
  /**
   * Holds the column names associated with each list control. There should be a 
   * one to one relationship between this variable and 
   * <CODE>listControls</CODE>.
   */
  private ArrayList listControlColumnNames = new ArrayList();
  /**
   * Flag used to determine if a filter is being restored. If so the events
   * that reload the lists should not fire.
   */
  private boolean loadingFilter = false;
  /**
   * Holds the dialog that allows the user to select a filter.
   */
  private FilterOpenDialog filterSelectDialog;

  /**
   * Creates a new <CODE>FilterFrame</CODE>.
   */
  public FilterFrame()
  {
    this(null, "", true);
  }

  /**
   * Creates a new <CODE>FilterFrame</CODE>.
   * 
   * @param parent The parent window of the dialog.
   * @param title The title that appears in the title bar of the dialog.
   * @param modal Pass as <CODE>true</CODE> for a modal dialog, <CODE>false</CODE> otherwise.
   */
  public FilterFrame(Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
    try
    {
      jbInit();
      String[] opList = {"=", "< >", ">", ">=", "<", "<=", "LIKE", "BETWEEN",
                           "IS NOT NULL", "IS NULL"};
      for( int iCtr = 0; iCtr < opList.length; iCtr++ )
        operatorsCombo.addItem( opList[iCtr] );
    }
    catch(Exception e)
    {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Component initialization.
   * 
   * @throws java.lang.Exception Thrown on initialization error.
   */
  private void jbInit() throws Exception
  {
    this.getContentPane().setLayout(windowLayout);
    builderPanel.setLayout(builderPanelLayout);
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    outerButtonPanelLayout.setVgap(5);
    rightButtonPanel.setLayout(rightButtonPanelLayout);
    rightButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    rightButtonPanelLayout.setHgap(5);
    clearButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          clearButton_actionPerformed(e);
        }
      });
    clearButton.setText("Clear Filter");
    okButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          okButton_actionPerformed(e);
        }
      });
    okButton.setText("OK");
    cancelButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          cancelButton_actionPerformed(e);
        }
      });
    cancelButton.setText("Cancel");
    leftButtonPanel.setLayout(leftButtonPanelLayout);
    leftButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    leftButtonPanelLayout.setHgap(5);
    loadButton.setText("Load Filter...");
    loadButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          loadButton_actionPerformed(e);
        }
      });
    saveButton.setText("Save Filter...");
    saveButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          saveButton_actionPerformed(e);
        }
      });
    deleteButton.setText("Delete Filter...");
    deleteButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          deleteButton_actionPerformed(e);
        }
      });
    builderPanelLayout.setHgap(5);
    statusBarPanel.setLayout(statusBarPanelLayout);
    progressLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    progressBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    filter.setMessageLabel(progressLabel);
    filter.setProgressBar(progressBar);
    editorPanel.setLayout(editorPanelLayout);
    queryEditPanel.setLayout(queryEditPanelLayout);
    queryEditPanelLayout.setVgap(5);
    queryEditPanelLayout.setRows(2);
    fieldsCombo.setEditable(false);
    operatorsCombo.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          jOperators_actionPerformed(e);
        }
      });
    operatorsCombo.setEditable(false);
    jTextField1.setColumns(15);
    addOpenParenButton.setActionCommand("Add (");
    addOpenParenButton.setText("Add (");
    addToFilterButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          cmdAddInfo_actionPerformed(e);
        }
      });
    addToFilterButton.setText("Add To Filter");
    addToFilterButton.setAlignmentY((float)0.0);
    andIntoFilterButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          cmdAndFilter_actionPerformed(e);
        }
      });
    andIntoFilterButton.setText("AND into Filter");
    andIntoFilterButton.setAlignmentY((float)0.0);
    orIntoFilterButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          cmdOrFilter_actionPerformed(e);
        }
      });
    orIntoFilterButton.setText("OR into Filter");
    orIntoFilterButton.setAlignmentY((float)0.0);
    jTextField2.setColumns(10);
    addCloseParenButton.setText("Add )");
    textArea.setLineWrap(true);
    textArea.setColumns(80);
    textArea.setWrapStyleWord(true);
    this.setSize(new Dimension(650, 300));
    tabbedPane.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          tabbedPane_stateChanged(e);
        }
      });
    editorPanel.add(queryEditPanel, BorderLayout.NORTH);
    scrollPane.getViewport().add(textArea, null);
    editorPanel.add(scrollPane, BorderLayout.CENTER);
    queryEditPanel.add(topQueryPanel, null);
    queryEditPanel.add(bottomQueryPanel, null);
    topQueryPanel.add(fieldsCombo, null);
    topQueryPanel.add(operatorsCombo, null);
    topQueryPanel.add(jTextField1, null);
    topQueryPanel.add(addOpenParenButton, null);
    operatorsCombo.addItem("");
    bottomQueryPanel.add(addToFilterButton, null);
    bottomQueryPanel.add(andIntoFilterButton, null);
    bottomQueryPanel.add(orIntoFilterButton, null);
    bottomQueryPanel.add(jTextField2, null);
    bottomQueryPanel.add(addCloseParenButton, null);
    scrollPane.getViewport();
    scrollPane.getViewport();
    statusBarPanel.add(progressLabel, BorderLayout.CENTER);
    statusBarPanel.add(progressBar, BorderLayout.EAST);
    outerButtonPanel.add(rightButtonPanel, BorderLayout.EAST);
    outerButtonPanel.add(leftButtonPanel, BorderLayout.WEST);
    outerButtonPanel.add(statusBarPanel, BorderLayout.SOUTH);
    rightButtonPanel.add(clearButton, null);
    rightButtonPanel.add(okButton, null);
    rightButtonPanel.add(cancelButton, null);
    leftButtonPanel.add(loadButton, null);
    leftButtonPanel.add(saveButton, null);
    leftButtonPanel.add(deleteButton, null);
    tabbedPane.addTab("Filter Editor", editorPanel);
    this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    this.getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);
    this.addWindowListener(new java.awt.event.WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          this_windowClosing(e);
        }
      });
    windowLayout.setVgap(5);
  }

  void cmdAddInfo_actionPerformed(ActionEvent e)
  {
    if (operatorsCombo.getSelectedIndex()==10||operatorsCombo.getSelectedIndex()==9) //user selected isNull or NotNull
    {
      textArea.append(" \""+fieldsCombo.getSelectedItem().toString()+"\" "+operatorsCombo.getSelectedItem().toString());    
    }
    else if (operatorsCombo.getSelectedIndex() == 8)  //user selected between
    {
      textArea.append(" \"" + fieldsCombo.getSelectedItem().toString() + "\" " + operatorsCombo.getSelectedItem().toString() + " " + "'" + jTextField1.getText() + "'" + " AND " + "'" + jTextField2.getText() + "'");
    }
    else if (operatorsCombo.getSelectedIndex() == 7) //user selected like
    {
      textArea.append(" \"" + fieldsCombo.getSelectedItem().toString() + "\" " + operatorsCombo.getSelectedItem().toString() + " " + "'%" + jTextField1.getText() + "%'");
    }
    else
      textArea.append(" \"" + fieldsCombo.getSelectedItem().toString() + "\" " + operatorsCombo.getSelectedItem().toString() + " " + "'" + jTextField1.getText() + "'");
    
        
  }

  void cmdAndFilter_actionPerformed(ActionEvent e)
  {
    if (operatorsCombo.getSelectedIndex()==10||operatorsCombo.getSelectedIndex()==9) //user selected isNull or NotNull
    {
      jTextField1.setEnabled(false);
      textArea.append(" "+"AND \""+ fieldsCombo.getSelectedItem().toString()+"\" "+operatorsCombo.getSelectedItem().toString()+" "+jTextField1.getText());
    }    else if (operatorsCombo.getSelectedIndex() == 8)
    {
      textArea.append(" " + "AND \"" + fieldsCombo.getSelectedItem().toString() + "\" " + operatorsCombo.getSelectedItem().toString() + " " +"'"+ jTextField1.getText()+"'"+ " AND " +"'"+ jTextField2.getText()+"'");
    }
    else
      textArea.append(" " + "AND \"" + fieldsCombo.getSelectedItem().toString() + "\" " + operatorsCombo.getSelectedItem().toString() + " "+"'"+ jTextField1.getText()+"'");

 
  }

  void cmdOrFilter_actionPerformed(ActionEvent e)
  {
    textArea.append("\n");

   if (operatorsCombo.getSelectedIndex()==10||operatorsCombo.getSelectedIndex()==9) //user selected isNull or NotNull
    {
      jTextField1.setEnabled(false);
      textArea.append(" "+"OR \""+ fieldsCombo.getSelectedItem().toString()+"\" "+"'"+operatorsCombo.getSelectedItem().toString()+"'"+" "+"'"+jTextField1.getText()+"'");
    }    else if (operatorsCombo.getSelectedIndex() == 8)
    {
      textArea.append(" " + "OR \"" + fieldsCombo.getSelectedItem().toString() + "\" " + operatorsCombo.getSelectedItem().toString() + " " + "'"+jTextField1.getText()+"'" + " AND " +"'"+ jTextField2.getText()+"'");
    }
    else
    {
      textArea.append(" " + "OR \"" + fieldsCombo.getSelectedItem().toString() + "\" " + operatorsCombo.getSelectedItem().toString() + " " +"'"+ jTextField1.getText()+"'");
    }
      
  }

  /**
   * Called when the clear button is clicked. This method clears the current filter.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void clearButton_actionPerformed(ActionEvent e)
  {
    if(getMode() == EDITOR_MODE)
      textArea.setText("");
    else
      ((JList)listControls.get(0)).clearSelection();
  }

  void jOperators_actionPerformed(ActionEvent e)
  {
    if(operatorsCombo.getSelectedIndex()==10||operatorsCombo.getSelectedIndex()==9) //user has selected isNULL or notNULL
    {
      jTextField1.setEnabled(false);
      jTextField2.setEnabled(false);
      //jOperators.disable();
    }    else
    {
      operatorsCombo.isEnabled();
      jTextField1.setEnabled(true);
      jTextField2.setEnabled(true);
    }
  }

  /**
   * Called when the cancel button is clicked. This method sets the value of the
   * result property to <CODE>CANCEL</CODE> and hides the dialog.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void cancelButton_actionPerformed(ActionEvent e)
  {
    result = CANCEL;
    setVisible(false);
  }

  /**
   * Called when the OK button is clicked. This method sets the value of the
   * result property to <CODE>OK</CODE> and hides the dialog.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void okButton_actionPerformed(ActionEvent e)
  {
    result = OK;
    setVisible(false);
  }
  
  /**
   * Called when the window is closed by clicking the X button in the title bar.
   * This method sets the value of the result property to equate closing the 
   * dialog with canceling it.
   *
   * @param e The <CODE>WindowEvent</CODE> that caused the invocation of this method.
   */
  void this_windowClosing(WindowEvent e)
  {
    result = CANCEL;
  }

  /**
   * Gets the <CODE>int</CODE> representing the button used to close the dialog.
   * If the OK button was clicked, <CODE>OK</CODE> is returned, otherwise 
   * <CODE>CANCEL</CODE> is returned.
   *
   * @return If the dialog was closed by clicking the OK buttton <CODE>OK</CODE> is returned, otherwise <CODE>CANCEL</CODE> is returned.
   */
  public int getResult()
  {
    return result;
  }

  /**
   * Returns the filter created by the user. This method returns the value in
   * the <CODE>JTextArea</CODE> at the bottom of the dialog if the dialog is in 
   * editor mode. If it is in builder mode, the filter will contain the FROM 
   * keyword, table names, and a where clause.
   * 
   * @return The filter created by the user.
   */
  public SelectStatement getFilter()
  {
    String schema = getSchema();
    SelectStatement filter = new SelectStatement();
    filter.setSchema(schema);
    if(getMode() == EDITOR_MODE)
    {
      if(schema == null)
        schema = "";
      else
        schema += ".";
      filter.setFromClause(schema + getTableName());
      filter.setWhereClause(textArea.getText());
    }
    else
    {
      int listCount = listControls.size();
      for(int i=0;i<listCount;i++) 
      {
        JList list = (JList)listControls.get(i);
        int selectedCount = list.getSelectedIndices().length;
        if(selectedCount > 0)
        {
          String tableName = listControlTableNames.get(i).toString();
          String columnName = listControlColumnNames.get(i).toString();
          WhereClauseItem item = createWhereClauseItemFromList(list, tableName, columnName);
          filter.addWhereClauseItem(item);
        }
      }
      int tableCount = tableJoins.size();
      for(int i=0;i<tableCount;i++)
        filter.addTableJoin((TableJoin)tableJoins.get(i));
    }
    return filter;
  }

  /**
   * Gets a version of the filter that can be restored at a later time by
   * passing it into <CODE>restorEditorFilter</CODE> or 
   * <CODE>restoreBuilderFilter</CODE>. The value returned is suitable for 
   * storing in a property file or the database, but note that to restore the 
   * filter the proper method must be called depending on if it is a filter from 
   * the editor or the builder. Which one is returned can be determined by 
   * calling <CODE>getMode()</CODE>.
   * 
   * @return A value suitable for saving in a property file.
   */
  public String getSaveableFilter()
  {
    String filter;
    if(getMode() == EDITOR_MODE)
      filter = textArea.getText();
    else
    {
      //Will save the selected items as TABLE_NAME.COLUMN_NAME = 'value', TABLE_NAME.COLUMN_NAME = 'value'
      StringBuffer buffer = new StringBuffer();
      int listCount = listControls.size();
      boolean valueAdded = false;
      for(int i=0;i<listCount;i++) 
      {
        JList list = (JList)listControls.get(i);
        Object[] selectedValues = list.getSelectedValues();
        if(selectedValues.length > 0)
        {
          String tableName = listControlTableNames.get(i).toString();
          String columnName = listControlColumnNames.get(i).toString();
          for(int j=0;j<selectedValues.length;j++) 
          {
            if(valueAdded)
              buffer.append(", ");
            buffer.append(tableName);
            buffer.append(".");
            buffer.append(columnName);
            buffer.append(" = '");
            buffer.append(selectedValues[j]);
            buffer.append("'");
            valueAdded = true;
          }
        }
      }
      filter = buffer.toString();
    }
    return filter;
  }
  
  /**
   * Restores the filter in the editor tab. This method expects the value to be
   * in the right format. Calling this method will also put the dialog in editor
   * mode.
   * 
   * @param filter The editor filter to restore.
   */
  public void restoreEditorFilter(String filter)
  {
    textArea.setText(filter);
    tabbedPane.setSelectedComponent(editorPanel);
  }
  
  /**
   * Restores the filter in the builder tab. This method expects the value to be
   * in the right format. Calling this method will also put the dialog in 
   * builder mode.
   * 
   * @param filter The editor filter to restore.
   */
  public void restoreBuilderFilter(final String filter)
  {
    //If we just go through setting the selection in the lists, events that reload 
    //the list contents will be inserted in the event thread after this code and
    //will wipe out the selections. This will have to be done in a seperate thread.
    Thread listThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(listControls)
        {
          try
          {
            loadingFilter = true;
            System.out.println("Restore filter thread started");
            enableControls();
            //The string passed in should be in the correct format:
            //<table>.<column> = '<value>', <table2>.<column2> = '<value>'
            Matcher filterMatcher = Pattern.compile("(?:\\,\\s)?([^\\.]+)\\.(\\S+)\\s\\=\\s\\'([^\\']*)\\'").matcher(filter);
            String tableName = null;
            String columnName = null;
            JList list = null;
            boolean currentListChanged = false;
            DefaultListModel model = null;
            while(filterMatcher.find())
            {
              String newTableName = filterMatcher.group(1);
              String newColumnName = filterMatcher.group(2);
              if(! newColumnName.equals(columnName) || ! newTableName.equals(tableName))
              {
                final JList newList = findListControl(newTableName, newColumnName);
                if(currentListChanged)
                {
                  //At this point the code to change the selection in the list has
                  //been inserted into the event thread via 
                  //SwingUtilities.invokeLater(), but were not out of the woods yet.
                  //We must now force that code to execute and reload the list to  
                  //the left of the one that changed before setting the selection on 
                  //it. We'll accomplish this with SwingUtilities.invokeAndWait().
                  SwingUtilities.invokeAndWait(new Runnable()
                  {
                    public void run()
                    {
                      reloadList(newList);
                    }
                  });
                }
                else
                  SwingUtilities.invokeAndWait(new Runnable()
                  {
                    public void run()
                    {
                      newList.clearSelection();
                    }
                  });
                //Give the reload thread a chance to start.
                Thread.yield();
                list = newList;
                model = (DefaultListModel)list.getModel();
                tableName = newTableName;
                columnName = newColumnName;
                currentListChanged = false;
              }
              if(list != null)
              {
                //If we're inside the synchronized block, that means the last
                //reload has atleast been added to the event queue.
                final JList selectionList = list;
                final DefaultListModel selectionModel = model;
                final String selectionString = filterMatcher.group(3);
                while(threadActive)
                  Thread.sleep(500);
                SwingUtilities.invokeAndWait(new Runnable()
                {
                  public void run()
                  {
                    final int index = selectionModel.indexOf(selectionString);
                    if(index != -1)
                      selectionList.addSelectionInterval(index, index);
                  }
                });
                currentListChanged = true;
              }
            }
            selectTab(builderPanel);
          }
          catch(InterruptedException ex)
          {
            ex.printStackTrace();
            showMessage("Unable to restore filter.", "Internal Error", JOptionPane.ERROR_MESSAGE);
          }
          catch(InvocationTargetException ex)
          {
            ex.printStackTrace();
            showMessage("Unable to restore filter.", "Internal Error", JOptionPane.ERROR_MESSAGE);
          }
          finally
          {
            loadingFilter = false;
            enableControls();
          }
        }
      }
    });
    System.out.println("Launching filter restore thread.");
    listThread.start();
  }

  /**
   * Selects the given tab. This method uses 
   * <CODE>SwingUtilities.invokeLater</CODE> so that it can be called from a 
   * thread.
   * 
   * @param tab The <CODE>Component</CODE> that comprises the tab to select.
   */
  private void selectTab(final Component tab)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        tabbedPane.setSelectedComponent(tab);
      }
    });
  }
  
  /**
   * Looks for the <CODE>JList</CODE> that corresponds to the given table and 
   * column names. Returns <CODE>null</CODE> if the <CODE>JList</CODE> can not be found.
   * 
   * @param tableName The name of the table corresponding to the <CODE>JList</CODE> to return.
   * @param columnName The name of the column corresponding to the <CODE>JList</CODE> to return.
   * @return The <CODE>JList</CODE> that represents the table and column passed into the method.
   */
  private JList findListControl(String tableName, String columnName)
  {
    int tableNameCount = listControlTableNames.size();
    for(int i=0;i<tableNameCount;i++) 
      if(tableName.equals(listControlTableNames.get(i)) && columnName.equals(listControlColumnNames.get(i)))
        return (JList)listControls.get(i);
    return null;
  }
  
  /**
   * Sets the name of the table being filtered. This is needed to populate the 
   * field combo box. The field combo box is populated when the table name and
   * data source properties have been set.
   *
   * @param tableName The name of the table being filtered.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void setTableName(String tableName) throws java.sql.SQLException
  {
    if(MPSBrowserView.compare(filter.getTableName(), tableName))
      return;//No change.
    filter.setTableName(tableName);
    if(getDataSource() != null)
      loadColumnNames();
    enableControls();
  }

//  /**
//   * Replaces the contents of a list. This method uses 
//   * <CODE>SwingUtilities.invokeLater</CODE>, so it can be invoked from a 
//   * thread.
//   * 
//   * @param list The list model of which to replace the contents.
//   * @param contents The new contents of the list.
//   */
//  private void replaceListContents(final DefaultListModel list, final Object[] contents)
//  {
//    SwingUtilities.invokeLater(new Runnable()
//    {
//      public void run()
//      {
//        list.clear();
//        for(int i=0;i<contents.length;i++) 
//          if(contents[i] == null)
//            list.addElement("<NULL>");
//          else
//            list.addElement(contents[i]);
//      }
//    });
//  }

  /**
   * Gets the name of the table being filtered.
   * 
   * @return The value of the table name property.
   */
  public String getTableName()
  {
    return filter.getTableName();
  }

  /**
   * Sets the data source for the dialog. This is used to connect to the 
   * database when the field combo box is populated. The field combo box is 
   * populated when the table name and data source properties have been set.
   *
   * @param connectionPool The data source to use for connecting to the database.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void setDataSource(DataSource connectionPool) throws java.sql.SQLException
  {
    String tableName = getTableName();
    filter.setDataSource(connectionPool);
    if(tableName != null && ! tableName.trim().equals(""))
    {
      loadColumnNames();
      if(listControls.size() > 0)
        reloadList((JList)listControls.get(0));
    }
  }

  /**
   * Gets the <CODE>DataSource</CODE> used to connect to the database.
   *
   * @return The <CODE>DataSource</CODE> used to connect to the database.
   */
  public DataSource getDataSource()
  {
    return filter.getDataSource();
  }

  /**
   * Loads the column names into the fields combo. This is called when both the 
   * data source and table name properties have been set.
   *
   * @throws java.sql.SQLException Thrown on sql error.
   */
  private void loadColumnNames() throws java.sql.SQLException
  {
    Thread loadThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(filter)
        {
          try
          {
            threadActive = true;
            enableControls();
            ArrayList loadedNames;
            try
            {
              loadedNames = filter.loadColumnNames();
            }
            catch(java.sql.SQLException ex)
            {
              if(MPSBrowserView.checkConnection(ex))
                loadedNames = filter.loadColumnNames();
              else
                throw ex;
            }
            final ArrayList columnNames = loadedNames;
            SwingUtilities.invokeLater(new Runnable()
            {
              public void run()
              {
                int count = columnNames.size();
                fieldsCombo.removeAllItems();
                for(int i=0;i<count;i++) 
                  fieldsCombo.addItem(columnNames.get(i));
              }
            });
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
    loadThread.start();
  }
  
  /**
   * Called when the load filter button is clicked. This method allows the user 
   * to open a filter they saved in the database.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void loadButton_actionPerformed(ActionEvent e)
  {
    Thread loadThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(filter)
        {
          try
          {
            threadActive = true;
            enableControls();
            try
            {
              //Reload the filter names outside here.
              filter.loadFilterNames();
              SwingUtilities.invokeAndWait(new Runnable()
              {
                public void run()
                {
                  if(filterSelectDialog == null)
                    filterSelectDialog = new FilterOpenDialog(FilterFrame.this, "Select A Saved Filter", true);
                  try
                  {
                    filterSelectDialog.setAllFilterNames(filter.getAllFilterNames());
                    filterSelectDialog.setUserFilterNames(filter.getUserFilterNames());
                  }
                  catch(java.sql.SQLException ex)
                  {
                    ex.printStackTrace();
                    showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
                  }
                  filterSelectDialog.pack();
                  filterSelectDialog.center();
                  filterSelectDialog.setVisible(true);
                }
              });
              if(filterSelectDialog.getResult() == FilterOpenDialog.CANCEL)
                return;//User cancelled.
              String[] filterData = filter.loadFilter(filterSelectDialog.getSelectedFilterName());
              if(filterData[0].startsWith("F"))
                restoreEditorFilter(filterData[1]);
              else
                restoreBuilderFilter(filterData[1]);
            }//try
            catch(java.sql.SQLException ex)
            {
              ex.printStackTrace();
              showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            }//catch(java.sql.SQLException ex)
            catch(InterruptedException ex)
            {
              ex.printStackTrace();
              showMessage("Unable to load filter names.", "Internal Error", JOptionPane.ERROR_MESSAGE);
            }
            catch(InvocationTargetException ex)
            {
              ex.printStackTrace();
              showMessage("Unable to load filter names.", "Internal Error", JOptionPane.ERROR_MESSAGE);
            }
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

  /**
   * Called when the save filter button is clicked. This method allows the user 
   * to open a filter they saved in the database.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void saveButton_actionPerformed(ActionEvent e)
  {
    Thread loadThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(filter)
        {
          try
          {
            threadActive = true;
            enableControls();
            boolean repeat = false, overwrite = false;
            final JPanel inputPanel = new JPanel(new GridLayout(3, 1, 0, 5));
            inputPanel.add(new JLabel("Enter a name for the filter."));
            final JTextField nameField = new JTextField();
            inputPanel.add(nameField);
            JCheckBox publicCheckBox = new JCheckBox("Make Public");
            inputPanel.add(publicCheckBox);
            final int[] result = new int[1];
            boolean publicFilter;
            do
            {
              SwingUtilities.invokeAndWait(new Runnable()
              {
                public void run()
                {
                  result[0] = JOptionPane.showConfirmDialog(FilterFrame.this, inputPanel, "Save Filter", JOptionPane.OK_CANCEL_OPTION);
                }
              });              
              if(result[0] == JOptionPane.CANCEL_OPTION)
                return;//User cancelled.
              final String selectedFilterName = nameField.getText();
              publicFilter = publicCheckBox.isSelected();
              String tableName = getTableName();
              boolean exists = filter.checkUsersFilterNames(tableName, selectedFilterName, ! publicFilter, false);
              if(exists)//Filter exists. Prompt before overwriting.
              {
                SwingUtilities.invokeAndWait(new Runnable()
                {
                  public void run()
                  {
                    StringBuffer message = new StringBuffer("The filter name '");
                    message.append(selectedFilterName);
                    message.append("' is already in use. Choose another name.");
                    JOptionPane.showMessageDialog(FilterFrame.this, message.toString(), "Name In Use", JOptionPane.ERROR_MESSAGE);
                  }
                });
                repeat = true;
              }
              else
              {
                //Need to prompt for overwrite if used by the current user.
                exists = filter.checkUsersFilterNames(tableName, selectedFilterName, false, true);
                if(exists)
                {
                  SwingUtilities.invokeAndWait(new Runnable()
                  {
                    public void run()
                    {
                      result[0] = JOptionPane.showConfirmDialog(FilterFrame.this, "Do you want to overwrite the existing filter named " + nameField.getText() + "?", "Filter Exists", JOptionPane.YES_NO_CANCEL_OPTION);
                    }
                  });
                  if(result[0] == JOptionPane.YES_OPTION)
                  {
                    overwrite = true;
                    repeat = false;
                  }
                  else
                    if(result[0] == JOptionPane.NO_OPTION)
                      repeat = true;//Repeat process.
                    else
                      return;//User cancelled.
                }
                else
                {
                  //filter does not exist. Save as new.
                  repeat = false;
                  overwrite = false;
                }
              }    
            }while(repeat);
            String type;
            if(getMode() == EDITOR_MODE)
              if(publicFilter)
                type = "FP";
              else
                type = "FU";
            else
              if(publicFilter)
                type = "BP";
              else
                type = "BU";
            filter.saveFilter(! overwrite, getSaveableFilter(), type, nameField.getText());
          }
          catch(java.sql.SQLException ex)
          {
            ex.printStackTrace();
            showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
          }
          catch(InterruptedException ex)
          {
            ex.printStackTrace();
            showMessage("Unable to load filter names.", "Internal Error", JOptionPane.ERROR_MESSAGE);
          }
          catch(InvocationTargetException ex)
          {
            ex.printStackTrace();
            showMessage("Unable to load filter names.", "Internal Error", JOptionPane.ERROR_MESSAGE);
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

  /**
   * Sets the owner, or schema, of the table in the database. This is used when 
   * retrieving the columns for the table. Defaults to <CODE>Jeri.SCHEMA</CODE>.
   * 
   * @param schema The owner or schema of the table in the database.
   */
  public void setSchema(String schema)
  {
    filter.setSchema(schema);
  }

  /**
   * Gets the owner, or schema, of the table in the database.
   * 
   * @return The owner or schema of the table in the database.
   */
  public String getSchema()
  {
    return filter.getSchema();
  }

  /**
   * Called when the delete filter button is clicked. This method allows the 
   * user to delete a saved filter from the database.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of ths method.
   */
  private void deleteButton_actionPerformed(ActionEvent e)
  {
    Thread loadThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(filter)
        {
          try
          {
            threadActive = true;
            enableControls();
            try
            {
              final String[] values = filter.getUserFilterNames();
              final Object[] filterName = new Object[1];
              SwingUtilities.invokeAndWait(new Runnable()
              {
                public void run()
                {
                  Object selected;
                  if(values.length > 0)
                    selected = values[0];
                  else
                    selected = null;
                  filterName[0] = JOptionPane.showInputDialog(FilterFrame.this, "Select a saved filter to delete.", "Delete Filter", -1, null, values, selected);
                }
              });
              if(filterName[0] == null)
                return;//User cancelled.
              filter.deleteFilter(filterName[0].toString());
            }//try
            catch(java.sql.SQLException ex)
            {
              ex.printStackTrace();
              showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            }//catch(java.sql.SQLException ex)
            catch(InterruptedException ex)
            {
              ex.printStackTrace();
              showMessage("Unable to load filter names.", "Internal Error", JOptionPane.ERROR_MESSAGE);
            }
            catch(InvocationTargetException ex)
            {
              ex.printStackTrace();
              showMessage("Unable to load filter names.", "Internal Error", JOptionPane.ERROR_MESSAGE);
            }
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
  
  /**
   * Enables or disables the controls on the interface accordingly. This method 
   * uses <CODE>SwingUtilities.invokeLater</CODE> so that it can be invoked from
   * a thread.
   */
  private void enableControls()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        fieldsCombo.setEnabled(! (threadActive || loadingFilter));
        operatorsCombo.setEnabled(! (threadActive || loadingFilter));
        jTextField1.setEnabled(! (threadActive || loadingFilter));
        addOpenParenButton.setEnabled(! (threadActive || loadingFilter));
        addToFilterButton.setEnabled(! (threadActive || loadingFilter));
        andIntoFilterButton.setEnabled(! (threadActive || loadingFilter));
        orIntoFilterButton.setEnabled(! (threadActive || loadingFilter));
        jTextField2.setEnabled(! (threadActive || loadingFilter));
        addCloseParenButton.setEnabled(! (threadActive || loadingFilter));
        textArea.setEnabled(! (threadActive || loadingFilter));
        loadButton.setEnabled(! (threadActive || loadingFilter));
        saveButton.setEnabled(! (threadActive || loadingFilter));
        deleteButton.setEnabled(! (threadActive || loadingFilter));
        clearButton.setEnabled(! (threadActive || loadingFilter));
        if(threadActive || loadingFilter)
          okButton.setEnabled(false);
        else
          if(getMode() == EDITOR_MODE)
            okButton.setEnabled(true);
          else
          {
            boolean itemSelected = false;
            int listControlCount = listControls.size();
            for(int i=0;i<listControlCount;i++) 
            {
              JList currentList = (JList)listControls.get(i);
              if(currentList.getSelectedIndices().length > 0)
              {
                itemSelected = true;
                break;
              }
            }
            okButton.setEnabled(itemSelected);
          }
      }
    });
  }

  /**
   * Uses <CODE>SwingUtilities.invokeLater</CODE> to show a message in a 
   * <CODE>JOptionPane</CODE>.
   * 
   * @param message The message to display.
   * @param title The title for the popup.
   * @param type The message type, from the constants in <CODE>JOptionPane</CODE>.
   */
  private void showMessage(final String message, final String title, final int type)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        JOptionPane.showMessageDialog(FilterFrame.this, message, title, type);
      }
    });
  }

  /**
   * Gets the current mode of the dialog.
   * 
   * @return <CODE>BUILDER_MODE</CODE> if the dialog is in builder mode, <CODE>EDITOR_MODE</CODE> if the dialog is in editor mode.
   */
  public int getMode()
  {
    if(tabbedPane.getSelectedComponent() == builderPanel)
      return BUILDER_MODE;
    else
      return EDITOR_MODE;
  }

  /**
   * Enables or disables the filter builder tab. <CODE>false</CODE> by default.
   * 
   * @param builderTabEnabled Pass <CODE>true</CODE> to show the builder tab, <CODE>false</CODE> to hide it.
   */
  private void setBuilderTabEnabled(boolean builderTabEnabled)
  {
    int tabCount = tabbedPane.getTabCount();
    if(builderTabEnabled)
    {
      if(tabCount < 2)
        tabbedPane.insertTab("Filter Builder", null, builderPanel, null, 0);
    }
    else
      if(tabCount > 1)
        tabbedPane.remove(builderPanel);
  }
  
  /**
   * Returns <CODE>true</CODE> if the builder tab is available, 
   * <CODE>false</CODE> if it is not available.
   * 
   * @return <CODE>true</CODE> if the builder tab is available, <CODE>false</CODE> if it is not available.
   */
  final public boolean isBuilderTabEnabled()
  {
    return tabbedPane.getTabCount() > 1;
  }
  
  /**
   * Adds the given table to the builder filter. It is up to the calling method
   * to make sure the join information passed in makes sense. The table being 
   * joined must somehow be linked to the table massed into the 
   * <CODE>setTableName</CODE> method. Using this method will also cause the
   * dialog to generate a query that will only produce read only result sets.
   * 
   * @param joinInfo The information on the tables being joined.
   */
  public void addBuilderTable(TableJoin joinInfo)
  {
    tableJoins.add(joinInfo);
  }
  
  /**
   * Adds a <CODE>JList</CODE> to the builder tab for the given table and 
   * column.
   * 
   * @param columnName The name of the column for which to add a <CODE>JList</CODE> to the builder tab.
   * @param tableName The name of the table to which the given column belongs.
   * @param displayName The text to appear over the column. If <CODE>null</CODE> the <CODE>columnName</CODE> parameter will be used.
   */
  public void addBuilderColumn(final String columnName, final String tableName, String displayName)
  {
    //Build the new control to add to the tab.
    JPanel newPanel = new JPanel();
    newPanel.setLayout(new BorderLayout());
    JLabel newLabel;
    if(displayName == null)
      newLabel = new JLabel(columnName);
    else
      newLabel = new JLabel(displayName);
    newLabel.setHorizontalAlignment(SwingConstants.CENTER);
    newPanel.add(newLabel, BorderLayout.NORTH);
    final JList newList = new JList();
    final DefaultListModel newModel = new DefaultListModel();
    newList.setModel(newModel);
    listControls.add(newList);
    listControlTableNames.add(tableName);
    listControlColumnNames.add(columnName);
    JScrollPane newScrollPane = new JScrollPane();
    newScrollPane.getViewport().add(newList, null);
    newPanel.add(newScrollPane, BorderLayout.CENTER);
    builderPanel.add(newPanel, null);
    //Need to add a listener to the list on the left to reload this one when
    //an item is clicked.
    int listCount = getBuilderColumnCount();
    if(listCount > 1)
    {      
      final JList leftList = (JList)listControls.get(listCount - 2);
      leftList.addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e)
        {
          //When the value of the list to the left of the list added changes, 
          //need to reload the contents of the list added.
          if(! (loadingFilter || e.getValueIsAdjusting()))
          {
            if(leftList.getSelectedIndices().length > 0)
              reloadList(newList);
            else
              newModel.clear();
          }
          enableControls();
        }
      });
    }
    newList.addKeyListener(new java.awt.event.KeyAdapter()
    {
      public void keyTyped(KeyEvent e)
      {
        int rowCount = newModel.size();
        String charTyped = String.valueOf(e.getKeyChar()).toUpperCase();
        for(int i=0;i<rowCount;i++)
        {
          if(newModel.getElementAt(i).toString().toUpperCase().startsWith(charTyped))
          {
            newList.getSelectionModel().setSelectionInterval(i, i);
            Rectangle selectedRow = newList.getCellBounds(i, i);
            newList.scrollRectToVisible(selectedRow);
            break;//No need to continue.
          }//if(currentModel.getValueAt(i, 0).toString().toUpperCase().startsWith(charTyped))
        }//for(int i=0;i<rowCount;i++)
      }
    });
    //If the data source is already set, need to reload the list because the 
    //initial data load has already occurred.
    if(getDataSource() != null && listControls.size() == 1)
      reloadList(newList);
    if(! isBuilderTabEnabled())
      setBuilderTabEnabled(true);
  }
  
  /**
   * Reloads the contents of a <CODE>JList</CODE> from the builder tab. This
   * method considers the selections in the lists to the left of the one passed
   * in when doing the reload.
   * 
   * @param listToReload The list to reload.
   */
  private void reloadList(final JList listToReload)
  {
    final DefaultListModel model = (DefaultListModel)listToReload.getModel();
    listToReload.clearSelection();
    model.clear();
    //Get the items selected in all lists to the left of the list being reloaded.
    final int reloadListIndex = listControls.indexOf(listToReload);
    ArrayList filterConstraints = new ArrayList();
    for(int i=0;i<reloadListIndex;i++) 
    {
      JList currentList = (JList)listControls.get(i);
      if(currentList.getSelectedIndices().length > 0)
      {
        String currentTableName = listControlTableNames.get(i).toString();
        String currentColumnName = listControlColumnNames.get(i).toString();
        WhereClauseItem currentItem = createWhereClauseItemFromList(currentList, currentTableName, currentColumnName);
        filterConstraints.add(currentItem);
      }
    }
    //Get the tables to join with the main table.
    final TableJoin[] joins = new TableJoin[tableJoins.size()];
    tableJoins.toArray(joins);
    final WhereClauseItem[] items = new WhereClauseItem[filterConstraints.size()];
    filterConstraints.toArray(items);
    //Do the data load from the RDB in a thread for a more responsive UI.
    Thread loadThread = new Thread(new Runnable()
    {
      public void run()
      {
        synchronized(filter)
        {
          try
          {
            threadActive = true;
            enableControls();
            final String reloadListColumnName = listControlColumnNames.get(reloadListIndex).toString();
            StringBuffer statusMessage = new StringBuffer("Loading ");
            statusMessage.append(reloadListColumnName);
            statusMessage.append(" records.");
            setMessage(statusMessage.toString());
            String reloadListTableName = listControlTableNames.get(reloadListIndex).toString();
            String[] newValues;
            try
            {
              String schema = getSchema();
              try
              {
                newValues = filter.loadListContents(schema, reloadListTableName, reloadListColumnName, joins, items);
              }
              catch(java.sql.SQLException ex)
              {
                //Check for timeout, if so try again.
                if(MPSBrowserView.checkConnection(ex))
                  newValues = filter.loadListContents(schema, reloadListTableName, reloadListColumnName, joins, items);
                else
                  throw ex;
              }
              final String[] loadedItems = newValues;
              //Have to go back to the event thread to put the items in the list.
              SwingUtilities.invokeLater(new Runnable()
              {
                public void run()
                {
                  for(int i=0;i<loadedItems.length;i++)
                    if(loadedItems[i] == null)
                      model.addElement("None");
                    else
                      model.addElement(loadedItems[i]);
                }
              });
            }
            catch(java.sql.SQLException ex)
            {
              ex.printStackTrace();
              showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            }
          }
          finally
          {
            threadActive = false;
            enableControls();
            setMessage(" ");
          }
        }
      }
    });
    loadThread.start();
  }

  /**
   * Creates a <CODE>WhereClauseItem</CODE> from the given <CODE>JList</CODE>.
   * If the table or column names are passed in as <CODE>null</CODE> this method 
   * will extract them from the <CODE>listControlColumnNames</CODE> and 
   * <CODE>listControlTableNames</CODE> variables.
   * 
   * @param currentList The <CODE>JList</CODE> from which to create the <CODE>WhereClauseItem</CODE>.
   * @param tableName The name of the table that corresponds to the given <CODE>JList</CODE>. Can be <CODE>null</CODE>.
   * @param columnName The name of the column that corresponds to the given <CODE>JList</CODE>. Can be <CODE>null</CODE>.
   * @return The <CODE>WhereClauseItem</CODE> generated from the given <CODE>JList</CODE>.
   */
  private WhereClauseItem createWhereClauseItemFromList(JList currentList, String tableName, String columnName)
  {
    int index = listControls.indexOf(currentList);
    if(tableName == null)
      tableName = listControlTableNames.get(index).toString();
    if(columnName == null)
      columnName = listControlColumnNames.get(index).toString();
    WhereClauseItem currentItem = new WhereClauseItem(tableName, columnName);
    Object[] selectedValues = currentList.getSelectedValues();
    for(int j=0;j<selectedValues.length;j++) 
    {
      String stringValue = selectedValues[j].toString();
      if(stringValue.equals("None"))
        currentItem.addValue(null);
      else
        currentItem.addValue(stringValue);
    }
    return currentItem;
  }

  /**
   * returns the number of columns that have been added to the builder tab with
   * the <CODE>addBuilderColumn</CODE> method.
   * 
   * @return The number of lists on the builder tab.
   */
  public int getBuilderColumnCount()
  {
    return listControls.size();
  }
  
  /**
   * Sets the message in the status bar. This method uses 
   * <CODE>SwingUtilities.invokeLater</CODE> so that it can be called from a 
   * thread.
   * 
   * @param message The message to display in the status bar.
   */
  public void setMessage(final String message)
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
   * Clears all lists from the builder tab.
   */
  public void clearBuilderTab()
  {
    builderPanel.removeAll();
    listControls.clear();
    listControlTableNames.clear();
    listControlColumnNames.clear();
    tableJoins.clear();
    setBuilderTabEnabled(false);
  }

  /**
   * Called when the selected tab changes.
   * 
   * @param e The <CODE>ChangeEvent</CODE> that caused the invocation of this method.
   */
  private void tabbedPane_stateChanged(ChangeEvent e)
  {
    enableControls();
  }
}