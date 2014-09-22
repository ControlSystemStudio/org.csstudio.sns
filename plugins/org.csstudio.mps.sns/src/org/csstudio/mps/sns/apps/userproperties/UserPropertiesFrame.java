package org.csstudio.mps.sns.apps.userproperties;
import org.csstudio.mps.sns.application.JeriInternalFrame;
import org.csstudio.mps.sns.tools.swing.TableSorter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import javax.swing.JComboBox;
import java.awt.BorderLayout;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.sql.DataSource;
import java.sql.SQLException;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JProgressBar;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.table.DefaultTableModel;

/**
 * Provides an interface for examining user's property files.
 * 
 * @author Chris Fowlkes
 */
public class UserPropertiesFrame extends JeriInternalFrame 
{
  private JComboBox userCombo = new JComboBox();
  private JScrollPane scrollPane = new JScrollPane();
  private JTable table = new JTable();
  /**
   * Holds the functionality for the interface.
   */
  private UserProperties properties = new UserProperties();
  private JPanel statusBarPanel = new JPanel();
  private BorderLayout statusBarPanelLayout = new BorderLayout();
  private JLabel progressLabel = new JLabel();
  private JProgressBar progressBar = new JProgressBar();
  /**
   * Provides the model for the table.
   */
  private DefaultTableModel model = new DefaultTableModel();
  /**
   * Sorts the rows in the table.
   */
  private TableSorter sorter = new TableSorter(model);

  /**
   * Creates a new <CODE>UserPropertiesFrame</CODE>.
   */
  public UserPropertiesFrame()
  {
    try
    {
      jbInit();
      model.addColumn("Key");
      model.addColumn("Value");
      sorter.addMouseListenerToHeaderInTable(table);
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
    userCombo.addItemListener(new ItemListener()
      {
        public void itemStateChanged(ItemEvent e)
        {
          userCombo_itemStateChanged(e);
        }
      });
    table.setModel(sorter);
    properties.setMessageLabel(progressLabel);
    properties.setProgressBar(progressBar);
    statusBarPanel.setLayout(statusBarPanelLayout);
    progressLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    progressLabel.setText(" ");
    progressBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    statusBarPanel.add(progressLabel, BorderLayout.CENTER);
    statusBarPanel.add(progressBar, BorderLayout.EAST);
    this.getContentPane().add(userCombo, BorderLayout.NORTH);
    scrollPane.getViewport().add(table, null);
    this.getContentPane().add(scrollPane, BorderLayout.CENTER);
    this.getContentPane().add(statusBarPanel, BorderLayout.SOUTH);
  }

  /**
   * Sets the <CODE>DataSource</CODE> used to connect to the database.
   * 
   * @param connectionPool The <CODE>DataSource</CODE> used to connect to the database.
   * @throws java.sql.SQLException Thrown on <CODE>SQLException</CODE>.
   */
  public void setDataSource(DataSource connectionPool) throws SQLException
  {
    super.setDataSource(connectionPool);
    properties.setDataSource(connectionPool);
    ArrayList userIDs = properties.loadUserIDs();
    int idCount = userIDs.size();
    for(int i=0;i<idCount;i++) 
      userCombo.addItem(userIDs.get(i));
  }

  /**
   * Called when the user como selection changes.
   * 
   * @param e The <CODE>ItemEvent</CODE> that caused the invocation of this method.
   */
  private void userCombo_itemStateChanged(ItemEvent e)
  {
    if(e.getStateChange() == ItemEvent.SELECTED)
    {
      String userID = e.getItem().toString();
      try
      {
        Properties userSettings = properties.loadPropertiesForUser(userID);
        for(int i=model.getRowCount()-1;i>=0;i--)
          model.removeRow(i);
        if(userSettings != null)
        {
          Iterator keys = userSettings.keySet().iterator();
          while(keys.hasNext())
          {
            Object key = keys.next();
            model.addRow(new Object[]{key, userSettings.get(key)});
          }
        }
      }
      catch(java.sql.SQLException ex)
      {
        ex.printStackTrace();
        showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
      }
      catch(java.io.IOException ex)
      {
        ex.printStackTrace();
        showMessage(ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}