package org.csstudio.mps.sns;


import org.csstudio.mps.sns.application.Commander;
import org.csstudio.mps.sns.application.XalDocument;
import org.csstudio.mps.sns.application.XalWindow;
import org.csstudio.mps.sns.JeriDocument;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.application.DatabaseTableFrame;
import org.csstudio.mps.sns.apps.dbexport.DBExportFrame;
import org.csstudio.mps.sns.apps.mpsbrowser.MPSBrowserFrame;
import org.csstudio.mps.sns.apps.signallist.SignalListFrame;
import org.csstudio.mps.sns.apps.templateimport.TemplateImportFrame;
import org.csstudio.mps.sns.apps.userproperties.UserPropertiesFrame;
import org.csstudio.mps.sns.tools.data.ArchiveGroup;
import org.csstudio.mps.sns.tools.data.ArchiveRequest;
import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.IOC;
import org.csstudio.mps.sns.tools.data.MPSBoard;
import org.csstudio.mps.sns.tools.data.Signal;
import org.csstudio.mps.sns.tools.database.CachingDatabaseAdaptor;
import org.csstudio.mps.sns.apps.mpsexport.MPSExportFrame;
import org.csstudio.mps.sns.apps.powersupplyfunctions.PowerSupplyFunctionsFrame;
import org.csstudio.mps.sns.apps.powersupplystartupexport.PowerSupplyStartupExportFrame;
import org.csstudio.mps.sns.apps.ppsdbexport.ppsDBExportFrame;

import java.awt.*;
import java.awt.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import java.io.*;

import java.net.*;

import java.sql.*;

import java.util.*;

import javax.sql.*;

import javax.swing.*;
import javax.swing.event.*;

import oracle.jdbc.pool.OracleDataSource;

import oracle.sql.BLOB;

/**
 * Provides the main window for the application.
 * 
 * @author Chris Fowlkes
 * @author Delphy Nypaver Armstrong
 */
public class MainFrame extends XalWindow 
{
  private JDesktopPane desktop = new JDesktopPane();

  /**
   * Holds a reference to the <CODE>DatabaseTableFrame</CODE> used to view 
   * reports from the signal tables.
   */
  private DatabaseTableFrame signalReportsWindow;
 
  private JSeparator signalsSeparator = new JSeparator();
  /**
   * Holds the icon for the commit button.
   */
  private ImageIcon commitIcon = new ImageIcon();
  /**
   * Holds the icon for the rollback button.
   */
  private ImageIcon rollbackIcon = new ImageIcon();
  /**
   * Holds the icon for the new filter button.
   */
  private ImageIcon newFilterIcon = new ImageIcon();
  /**
   * Holds the icon for the edit filter button.
   */
  private ImageIcon editFilterIcon = new ImageIcon();
  /**
   * Holds the icon for the remove filter button.
   */
  private ImageIcon removeFilterIcon = new ImageIcon();
  /**
   * Holds the icon for the first button.
   */
  private ImageIcon firstIcon = new ImageIcon();
  /**
   * Holds the icon for the prior button.
   */
  private ImageIcon priorIcon = new ImageIcon();
  /**
   * Holds the icon for the next button.
   */
  private ImageIcon nextIcon = new ImageIcon();
  /**
   * Holds the icon for the last button.
   */
  private ImageIcon lastIcon = new ImageIcon();
  /**
   * Holds the icon for the post button.
   */
  private ImageIcon postIcon = new ImageIcon();
  /**
   * Holds the icon for the cancel button.
   */
  private ImageIcon cancelIcon = new ImageIcon();
  /**
   * Holds the icon for the refresh button.
   */
  private ImageIcon refreshIcon = new ImageIcon();
  /**
   * Holds the icon for the finish button in the signal wizard interface.
   */
  private ImageIcon finishIcon = new ImageIcon();
  /**
   * Holds the plus sign icon.
   */
  private ImageIcon plusIcon = new ImageIcon();
  /**
   * Holds the minus sign icon.
   */
  private ImageIcon minusIcon = new ImageIcon();
  /**
   * Holds the icon for the table borwser's group button.
   */
  private ImageIcon groupIcon = new ImageIcon();
  /**
   * Holds the icon for any print buttons.
   */
  private ImageIcon printIcon = new ImageIcon();
  /**
   * Holds the icon for any up arrow buttons.
   */
  private ImageIcon upIcon = new ImageIcon();
  /**
   * Holds the icon for any down arrow buttons.
   */
  private ImageIcon downIcon = new ImageIcon();
   /**
   * Holds the interface used to edit the machine mode table.
   */
  private MPSBrowserFrame machineModeBrowser;
  /**
   * Holds the look and feel radio menu items.
   */
  private ButtonGroup lookAndFeelGroup = new ButtonGroup();
  /**
   * Holds the interface used to export data to a db file.
   */
  private DBExportFrame exportWindow;
  private ppsDBExportFrame PPSexportWindow;
  private MPSExportFrame mpsExportWindow;
  /**
   * Holds the interface used to edit the MPS chassis serial numbers.
   */
  private DatabaseTableFrame mpsSerialNumberWindow;
  /**
   * Holds the interface used to edit the MPS properties table.
   */
  private DatabaseTableFrame mpsPropertiesWindow;
  /**
   * Holds the interface used to edit the DELAYED_REAL_TIME_SGNL table.
   */
  private DatabaseTableFrame pvValueToRDBWindow;
  /**
   * Holds the interface used to edit the machine mode default mask table.
   */
  private DatabaseTableFrame machineModeDefaultsWindow;
  private DatabaseTableFrame cableWindow;
  /**
   * Holds the interface used to import template files into the database.
   */
  private TemplateImportFrame templateImportWindow;
  private PowerSupplyStartupExportFrame powerSupplyExportWindow;
  /**
   * Holds the interface for the signal functions window.
   */
  private JInternalFrame signalFunctionsWindow;
  /**
   * Holds the interface used to select a <CODE>Signal</CODE> for the signal 
   * rename impact report.
   */
  private SignalListFrame impactReportWindow;

  private JTextArea currentFilterText = new JTextArea();
  private JPanel filterPanel = new JPanel();
  private JTextArea filterText = new JTextArea();
  private JButton addFilterButton = new JButton();
  private BorderLayout filterPanelLayout = new BorderLayout();
  private JTable deviceTypeTable = new JTable();
  private JButton nextButton = new JButton();
  private JButton previousButton = new JButton();


 /**
   * Provides a document for the application.
   */
  private JeriDocument jeriDocument;
  /**
   * Holds the interface used to view user properties files.
   */
  private UserPropertiesFrame userPropertiesWindow;
  
  /**
   * Creates a new <CODE>MainFrame</CODE>.
   * 
   * @param document The <CODE>XalDocument</CODE> for the application. Must be an instance of <CODE>JeriDocument</CODE>.
   */
  public MainFrame(XalDocument document)
  {
    super(document);
    jeriDocument = (JeriDocument)document;
    try
    {
      jbInit();
      loadIcons();

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
    this.addComponentListener(new java.awt.event.ComponentAdapter()
      {
        public void componentResized(ComponentEvent e)
        {
          this_componentResized(e);
        }

        public void componentMoved(ComponentEvent e)
        {
          this_componentMoved(e);
        }
      });
    this.addWindowListener(new java.awt.event.WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          this_windowClosing(e);
        }
        
        public void windowOpened(WindowEvent e)
        {
          this_windowOpened(e);
        }
      });
    desktop.setBackground(new Color(96, 96, 96));
    desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    this.getContentPane().add(desktop, BorderLayout.CENTER);
  }

  /**
   * Called when the exit menu item is clicked. This method calls
   * <CODE>exitApplication</CODE>.
   *
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  void exitMenuItem_ActionPerformed(ActionEvent e)
  {
	  System.out.println("System exit  exitMenuItem_ActionPerformed");
    exitApplication();
  }

 
  /**
   * Selects and shows an <CODE>JInternalFrame</CODE>. If the window is closed 
   * or minimized, this method restores it before showing and selecting it.
   *
   * @param frame The <CODE>JInternaFrame</CODE> to select.
   */
  private void selectWindow(JInternalFrame frame)
  {
    try
    {
      if(frame.isClosed())
      {
        desktop.add(frame);
        centerInternalFrame(frame);
      }
      if(frame.isIcon())
        desktop.getDesktopManager().deiconifyFrame(frame);
      if(! frame.isVisible())
        frame.setVisible(true);
      if(! frame.isSelected())
        frame.setSelected(true);
    }
    catch(java.beans.PropertyVetoException ex)
    {
      ex.printStackTrace();
    }
  }

  /**
   * Centers an internal frame on the desktop. This method checks to make sure
   * the window is not bigger than the desktop and centers it.
   *
   * @param frame The <CODE>JInternalFrame</CODE> to center.
   */
  private void centerInternalFrame(JInternalFrame frame)
  {
    int desktopWidth = desktop.getWidth();
    int desktopHeight = desktop.getHeight();
    int frameWidth = frame.getWidth();
    int frameHeight = frame.getHeight();
    if(frameWidth <= 0 || frameHeight <= 0)
    {
      frame.pack();
      frameWidth = frame.getWidth();
      frameHeight = frame.getHeight();
    }
    //Make sure internal window is not larger than the main window.
    if(frameWidth > desktopWidth || frameHeight > desktopHeight)
    {
      if(frameWidth > desktopWidth)
        frameWidth = desktopWidth;
      if(frameHeight > desktopHeight)
        frameHeight = desktopHeight;
      frame.setSize(frameWidth, frameHeight);
    }
    int x = (desktopWidth - frameWidth) / 2;
    int y = (desktopHeight - frameHeight) / 2;
    frame.setLocation(x, y);
  }
  
  /**
   * Looks for the item or menu with the given text in the application's menu 
   * bar.
   * 
   * @param text The text of the <CODE>JMenuItem</CODE> to return.
   * @return The <CODE>JMenuItem</CODE> with the given text, or <CODE>null</CODE> if no matching <CODE>JMenuItem</CODE> was found.
   */
  private JMenuItem findMenuItem(String text)
  {
    JMenuBar menuBar = getJMenuBar();
    int menuCount = menuBar.getMenuCount();
    JMenuItem item = null;
    for(int i=0;i<menuCount;i++) 
    {
      item = findMenuItem(text, menuBar.getMenu(i));
      if(item != null)
        break;//found it. break out of loop.
    }
    return item;
  }
  
  
  /**
   * Finds the item in the given <CODE>JMenu</CODE> that has the given text. If 
   * no match is found <CODE>null</CODE> is returned.
   * 
   * @param text The text to look for in the <CODE>JMenu</CODE>.
   * @param menu The <CODE>JMenu</CODE> to search.
   * @return The mathcing <CODE>JMenuItem</CODE> or <CODE>null</CODE>.
   */
  private JMenuItem findMenuItem(String text, JMenu menu)
  {
    JMenuItem item;
    if(menu.getText().equals(text))
      item = menu;
    else
    {
      item = null;
      int itemCount = menu.getItemCount();
      for(int j=0;j<itemCount;j++) 
      {
        item = menu.getItem(j);
        if(item != null)//null indicates a seperator.
        {
          if(item instanceof JMenu)
            item = findMenuItem(text, (JMenu)item);
          else
            if(! item.getText().equals(text))
              item = null;
          if(item != null)
            break;//found it. break out of loop.
        }
      }
    }
    return item;
  }

  
  
  /**
   * Gets the properties stored in the applications properties file.
   *
   * @return The settings for the application.
   */
  public Properties getApplicationProperties()
  {
    return jeriDocument.getApplicationProperties();
  }

  /**
   * Called when the application is closed by clicking on the X button in the 
   * title bar. This method calls <CODE>exitApplication</CODE>.
   * 
   * @param e The <CODE>WindowEvent</CODE> that caused the invocation of this method.
   */
  void this_windowClosing(WindowEvent e)
  {
	  System.out.println("MPS Model Closed (via this_windowClosing event)");
	  exitApplication();
  }
  
  /**
   * Called when the window is first opened. This method disables the MPS export
   * menu item for unauthorized users.
   * 
   * @param e The <CODE>WindowEvent</CODE> that caused the invocation of this method.
   */
  void this_windowOpened(WindowEvent e)
  {
    findMenuItem("Export MPS Data...").setEnabled(jeriDocument.checkRole("MPS_USER"));
    String userID = ((OracleDataSource)jeriDocument.getDatabaseAdaptor().getDataSource()).getUser();
    userID = userID.toLowerCase();
    //This code removed because it relies on old menu values
    //boolean adminVisible = userID.equals("xxp") || userID.equals("9pj");
    //findMenuItem("Admin").setVisible(adminVisible);
    //loadLookAndFeels();

  }

  /**
   * Exits the application. This method stores all of the applications settings 
   * in the property file and exits.
   */
  public void exitApplication()
  {
  }

  /**
   * Called when the window is resized. This method records the new size in the 
   * application settings, which are saved when the application exits.
   *
   * @param e The <CODE>ComponentEvent</CODE> that caused the invocation of this method.
   */
  void this_componentResized(ComponentEvent e)
  {
    Dimension newSize = getSize();
    Properties settings = getApplicationProperties();
    if(settings != null)
    {
      settings.setProperty("mainWindow.width", String.valueOf(newSize.width));
      settings.setProperty("mainWindow.height", String.valueOf(newSize.height));
    }
  }

  /**
   * Called when the window is moved. This method records the new location in 
   * the application settings, which are saved when the application exits.
   *
   * @param e The <CODE>ComponentEvent</CODE> that caused the invocation of this method.
   */
  void this_componentMoved(ComponentEvent e)
  {
    Point newLocation = getLocation();
    Properties settings = getApplicationProperties();
    if(settings != null)
    {
      settings.setProperty("mainWindow.x", String.valueOf(newLocation.x));
      settings.setProperty("mainWindow.y", String.valueOf(newLocation.y));
    }
  }


  /**
   * Loads the images for the icons used in the application from the image files
   * packaged in the application.
   */
  private void loadIcons()
  {
    try
    {
      Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
      Class thisClass = getClass();
      commitIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/SaveDB.gif")));
      rollbackIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Undo.gif")));
      editFilterIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/UpdateSheet.gif")));
      newFilterIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/NewSheet.gif")));
      removeFilterIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/DeleteSheet.gif")));
      firstIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Begin.gif")));
      priorIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Left.gif")));
      nextIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Right.gif")));
      lastIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/End.gif")));
      plusIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Plus.gif")));
      minusIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Minus.gif")));
      postIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Check.gif")));
      cancelIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Delete.gif")));
      refreshIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/RotCWDown.gif")));
      finishIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/GoalFlag.gif")));
      plusIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Plus.gif")));
      minusIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Minus.gif")));
      groupIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Package.gif")));
      printIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Print.gif")));
      upIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Up.gif")));
      downIcon.setImage(defaultToolkit.createImage(thisClass.getResource("resources/images/Down.gif")));
    }
    catch(java.lang.Exception ex)
    {
      ex.printStackTrace();
    }
  }
  /**
   * Loads the available look and feels into the menu.
   */
  private void loadLookAndFeels()
  {
    UIManager.LookAndFeelInfo[] lookAndFeelList = UIManager.getInstalledLookAndFeels();
    String currentLookAndFeel = UIManager.getLookAndFeel().getName();
    for(int i=0;i<lookAndFeelList.length;i++)
    {
      final UIManager.LookAndFeelInfo currentInfo = lookAndFeelList[i];
      try
      {
        Class currentLookAndFeelClass;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if(loader != null)
          currentLookAndFeelClass = Class.forName(currentInfo.getClassName(), true, loader);
        else
          currentLookAndFeelClass = Class.forName(currentInfo.getClassName());
        if(((LookAndFeel)currentLookAndFeelClass.newInstance()).isSupportedLookAndFeel())
        {
          String currentName = currentInfo.getName();
          JRadioButtonMenuItem currentItem = new JRadioButtonMenuItem(currentName);
          JMenu lookAndFeelMenu = (JMenu)findMenuItem("Look And Feel");
          lookAndFeelMenu.add(currentItem);
          lookAndFeelGroup.add(currentItem);
          if(currentName.equals(currentLookAndFeel))
            currentItem.setSelected(true);
          currentItem.addItemListener(new ItemListener()
          {
            public void itemStateChanged(ItemEvent e)
            {
              if(e.getStateChange() == ItemEvent.SELECTED)
              {
                try
                {
                  String className = currentInfo.getClassName();
                  UIManager.setLookAndFeel(className);
                  SwingUtilities.updateComponentTreeUI(MainFrame.this);
                  getApplicationProperties().setProperty("LookAndFeel", className);
                }
                catch(Exception ex)
                {
                  JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
              }
            }
          });
        }
      }
      catch(java.lang.ClassNotFoundException ex)
      {//Do nothing. Can't find the look and feel.
      }
      catch(java.lang.IllegalAccessException ex)
      {
        ex.printStackTrace();
      }
      catch(java.lang.InstantiationException ex)
      {
        ex.printStackTrace();
      }
    }
  }


  /**
   * Called when the machine mode browser menu item is clicked. This method
   * shows an interface that allows the user to edit, or view depending on 
   * permissions, the MACHINE_MODE table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void machineModeBrowserMenuItem_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if(machineModeBrowser == null)
      {
        //Need to initialize the window.
        machineModeBrowser = new MPSBrowserFrame();
        machineModeBrowser.setApplicationProperties(getApplicationProperties());
        machineModeBrowser.setEditable(jeriDocument.checkRole("MPS_USER"));
        machineModeBrowser.setDataSource(jeriDocument.getDatabaseAdaptor().getDataSource());
        machineModeBrowser.setTitle("Machine Mode Browser");
        //machineModeBrowser.setMainWindow(this);
        desktop.add(machineModeBrowser);
        centerInternalFrame(machineModeBrowser);
        machineModeBrowser.addInternalFrameListener(new InternalFrameAdapter()
        {
          public void internalFrameClosed(InternalFrameEvent e)
          {
            machineModeBrowser = null;//Destroy after closing.
          }
        });
      }
      selectWindow(machineModeBrowser);
    }
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
      machineModeBrowser = null;
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }


  /**
   * Called when the MPS Browser is implemented from CSS. This method
   * shows an interface that allows the user to edit, or view depending on 
   * permissions, the MACHINE_MODE table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void machineModeBrowserMenuItem_actionPerformed()
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if(machineModeBrowser == null)
      {
        //Need to initialize the window.
        machineModeBrowser = new MPSBrowserFrame();
        machineModeBrowser.setApplicationProperties(getApplicationProperties());
        machineModeBrowser.setEditable(jeriDocument.checkRole("MPS_USER"));
        machineModeBrowser.setDataSource(jeriDocument.getDatabaseAdaptor().getDataSource());
        machineModeBrowser.setTitle("Machine Mode Browser");
        //machineModeBrowser.setMainWindow(this);
        desktop.add(machineModeBrowser);
        centerInternalFrame(machineModeBrowser);
        machineModeBrowser.addInternalFrameListener(new InternalFrameAdapter()
        {
          public void internalFrameClosed(InternalFrameEvent e)
          {
            machineModeBrowser = null;//Destroy after closing.
          }
        });
      }
      selectWindow(machineModeBrowser);
    }
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
      machineModeBrowser = null;
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  /**
   * Shows the MPS export interface.
   * 
   * @param boards The instances of <CODE>MPSBoard</CODE> with which to populate the interface.
   */
  public void showMPSExport(MPSBoard[] boards)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      showMPSExport();
      mpsExportWindow.setBoards(boards);
    }
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
      mpsExportWindow = null;
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  private void showMPSExport() throws java.sql.SQLException
  {
    try
    {
      if(mpsExportWindow == null)
      {
        //Need to initialize the window.
        mpsExportWindow = new MPSExportFrame();
        mpsExportWindow.setApplicationProperties(getApplicationProperties());
        mpsExportWindow.setDataSource(jeriDocument.getDatabaseAdaptor().getDataSource());
        //mpsExportWindow.setMainWindow(this);
        mpsExportWindow.setPrintIcon(printIcon);
        desktop.add(mpsExportWindow);
        centerInternalFrame(mpsExportWindow);
        mpsExportWindow.addInternalFrameListener(new InternalFrameAdapter()
        {
          public void internalFrameClosed(InternalFrameEvent e)
          {
            mpsExportWindow = null;//Destroy after closing.
          }
        });
      }
      selectWindow(mpsExportWindow);
    }
    catch(java.sql.SQLException ex)
    {
      mpsExportWindow = null;
      throw ex;
    }
  }

  /**
   * Called when the import LIBOBJS file menu item is clicked. This method alows
   * the user to import an EPICS LIBOBJS file to store in the database.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void importLIBOBJSMenuItem_actionPerformed(ActionEvent e)
  {
    try
    {
      String epicsVersion = JOptionPane.showInputDialog(this, "Enter the EPICS version number associated with the file.");
      if(epicsVersion != null)
      {
        JFileChooser fileDialog = new JFileChooser();
        if(fileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
          Connection oracleConnection = jeriDocument.getDatabaseAdaptor().getDataSource().getConnection();
          try
          {
            oracleConnection.setAutoCommit(false);
            Statement query = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            try
            {
              StringBuffer sql = new StringBuffer("SELECT CONTENTS FROM ");
              sql.append(MPSBrowserView.SCHEMA);
              sql.append(".EPICS_LIBOBJS WHERE EPICS_VER = '");
              sql.append(epicsVersion);
              sql.append("' FOR UPDATE");
              String updateQuery = sql.toString();
              ResultSet result = query.executeQuery(updateQuery);
              try
              {
                if(result.next())
                {
                  int confirm = JOptionPane.showConfirmDialog(this, "A file for this version already exists in the RDB. Do you want to replace it?");
                  if(confirm != JOptionPane.YES_OPTION)
                    return;
                }
                else
                {
                  sql = new StringBuffer("INSERT INTO ");
                  sql.append(MPSBrowserView.SCHEMA);
                  sql.append(".EPICS_LIBOBJS (EPICS_VER, CONTENTS) VALUES ('");
                  sql.append(epicsVersion);
                  sql.append("', EMPTY_BLOB())");
                  query.execute(sql.toString());
                  result = query.executeQuery(updateQuery);
                  result.next();
                }
                File libobjFile = fileDialog.getSelectedFile();
                BLOB contents = (BLOB)result.getBlob("CONTENTS");
                byte[] buffer = new byte[contents.getBufferSize()];
                BufferedInputStream iStream = new BufferedInputStream(new FileInputStream(libobjFile));
                try
                {
                  OutputStream oStream = contents.setBinaryStream(0);
                  try
                  {
                    int bytesRead = iStream.read(buffer);
                    while(bytesRead != -1)
                    {
                      oStream.write(buffer, 0, bytesRead);
                      bytesRead = iStream.read(buffer);
                    }
                    oStream.flush();
                    oracleConnection.commit();
                    JOptionPane.showMessageDialog(this, "Import Successful.");
                  }
                  finally
                  {
                    oStream.close();
                  }
                }
                finally
                {
                  iStream.close();
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
        }
      }
    }
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }
    catch(java.io.IOException exc)
    {
      exc.printStackTrace();
      JOptionPane.showMessageDialog(this, exc.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Called when the export MPS menu item is clicked. This method shows the 
   * interface used to export MPS data to files.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void exportMPSMenuItem_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      showMPSExport();
    }
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  /**
   * Called when the MPS properties menu item is called. This method shows an 
   * interface that allows the user to edit the MPS_PROP table's data.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void mpsPropertiesMenuItem_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if(mpsPropertiesWindow == null)
      {
        //Need to initialize the window.
        mpsPropertiesWindow = new DatabaseTableFrame();
        mpsPropertiesWindow.setSchema(MPSBrowserView.SCHEMA);
        mpsPropertiesWindow.setPropertyKey("mpsPropertiesWindow");
        mpsPropertiesWindow.setApplicationProperties(getApplicationProperties());
        mpsPropertiesWindow.setTableNames(new String[]{"MPS_PROP"}, null);
        mpsPropertiesWindow.setCommitIcon(commitIcon);
        mpsPropertiesWindow.setRollbackIcon(rollbackIcon);
        mpsPropertiesWindow.setEditFilterIcon(editFilterIcon);
        mpsPropertiesWindow.setNewFilterIcon(newFilterIcon);
        mpsPropertiesWindow.setRemoveFilterIcon(removeFilterIcon);
        mpsPropertiesWindow.setFirstIcon(firstIcon);
        mpsPropertiesWindow.setPriorIcon(priorIcon);
        mpsPropertiesWindow.setNextIcon(nextIcon);
        mpsPropertiesWindow.setLastIcon(lastIcon);
        mpsPropertiesWindow.setInsertIcon(plusIcon);
        mpsPropertiesWindow.setDeleteIcon(minusIcon);
        mpsPropertiesWindow.setPostIcon(postIcon);
        mpsPropertiesWindow.setCancelIcon(cancelIcon);
        mpsPropertiesWindow.setRefreshIcon(refreshIcon);
        mpsPropertiesWindow.setTitle("MPS Properties");
        //mpsPropertiesWindow.setMainWindow(IWorkbenchPage this);
        mpsPropertiesWindow.setEditable(jeriDocument.checkRole("MPS_USER"));
        desktop.add(mpsPropertiesWindow);
        centerInternalFrame(mpsPropertiesWindow);
        mpsPropertiesWindow.addInternalFrameListener(new InternalFrameAdapter()
        {
          public void internalFrameClosed(InternalFrameEvent e)
          {
            mpsPropertiesWindow = null;//Destroy after closing.
          }
        });
        selectWindow(mpsPropertiesWindow);
        mpsPropertiesWindow.setDatabaseAdaptor(jeriDocument.getDatabaseAdaptor());
        mpsPropertiesWindow.refresh();
      }
      else
        selectWindow(mpsPropertiesWindow);
    }
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
      mpsPropertiesWindow = null;
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }


  /**
   * Shows a window that allows the user to edit the MACHINE_MODE_DEF_MASK 
   * table.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void machineModeDefaultsMenuItem_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if(machineModeDefaultsWindow == null)
      {
        //Need to initialize the window.
        machineModeDefaultsWindow = new DatabaseTableFrame();
        machineModeDefaultsWindow.setSchema(MPSBrowserView.SCHEMA);
        machineModeDefaultsWindow.setPropertyKey("machineModeDefaultsWindow");
        machineModeDefaultsWindow.setApplicationProperties(getApplicationProperties());
        machineModeDefaultsWindow.setTableNames(new String[]{"MACHINE_MODE_DEF_MASK"}, null);
        machineModeDefaultsWindow.setCommitIcon(commitIcon);
        machineModeDefaultsWindow.setRollbackIcon(rollbackIcon);
        machineModeDefaultsWindow.setEditFilterIcon(editFilterIcon);
        machineModeDefaultsWindow.setNewFilterIcon(newFilterIcon);
        machineModeDefaultsWindow.setRemoveFilterIcon(removeFilterIcon);
        machineModeDefaultsWindow.setFirstIcon(firstIcon);
        machineModeDefaultsWindow.setPriorIcon(priorIcon);
        machineModeDefaultsWindow.setNextIcon(nextIcon);
        machineModeDefaultsWindow.setLastIcon(lastIcon);
        machineModeDefaultsWindow.setInsertIcon(plusIcon);
        machineModeDefaultsWindow.setDeleteIcon(minusIcon);
        machineModeDefaultsWindow.setPostIcon(postIcon);
        machineModeDefaultsWindow.setCancelIcon(cancelIcon);
        machineModeDefaultsWindow.setRefreshIcon(refreshIcon);
        machineModeDefaultsWindow.setTitle("Machine Mode Defaults");
        //machineModeDefaultsWindow.setMainWindow(this);
        machineModeDefaultsWindow.setEditable(jeriDocument.checkRole("MPS_USER"));
        desktop.add(machineModeDefaultsWindow);
        centerInternalFrame(machineModeDefaultsWindow);
        machineModeDefaultsWindow.addInternalFrameListener(new InternalFrameAdapter()
        {
          public void internalFrameClosed(InternalFrameEvent e)
          {
            machineModeDefaultsWindow = null;//Destroy after closing.
          }
        });
        selectWindow(machineModeDefaultsWindow);
        machineModeDefaultsWindow.setDatabaseAdaptor(jeriDocument.getDatabaseAdaptor());
        machineModeDefaultsWindow.refresh();
      }
      else
        selectWindow(machineModeDefaultsWindow);
    }
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
      machineModeDefaultsWindow = null;
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }


  /**
   * Called when the import template menu item is clicked. This method shows the 
   * interface that allows the user to import one or more template files into
   * the database.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void importTemplateMenuItem_actionPerformed(ActionEvent e)
  {
    try
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if(templateImportWindow == null)
      {
        //Need to initialize the window.
        templateImportWindow = new TemplateImportFrame();
        templateImportWindow.setApplicationProperties(getApplicationProperties());
        templateImportWindow.setDataSource(jeriDocument.getDatabaseAdaptor().getDataSource());
        //templateImportWindow.setMainWindow(this);
        desktop.add(templateImportWindow);
        centerInternalFrame(templateImportWindow);
        templateImportWindow.addInternalFrameListener(new InternalFrameAdapter()
        {
          public void internalFrameClosed(InternalFrameEvent e)
          {
            templateImportWindow = null;//Destroy after closing.
          }
        });
      }
      selectWindow(templateImportWindow);
    }
    catch(java.sql.SQLException ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
      templateImportWindow = null;
    }
    finally
    {
      setCursor(Cursor.getDefaultCursor());
    }
  }


  /**
   * Saves the size of the given window as the default.
   * 
   * @param frame The window for which to save the size.
   * @param key The key for the windows settings in the property file.
   */
  private void saveSize(JInternalFrame frame, String key)
  {
    Properties settings = getApplicationProperties();
    int height = frame.getHeight();
    String heightString = String.valueOf(height);
    settings.setProperty(key + ".height", heightString);
    int width = signalFunctionsWindow.getWidth();
    String widthString = String.valueOf(width);
    settings.setProperty(key + ".width", widthString);
  }

  /**
   * Sizes the given window to the size it last was.
   * 
   * @param frame The window to resize.
   * @param key The key for the window in the property file.
   * @param defaultWidth The default width of the window.
   * @param defaultHeight The default height of the window.
   */
  private void restoreSize(JInternalFrame frame, String key, int defaultWidth, int defaultHeight)
  {
    Properties settings = getApplicationProperties();
    String defaultWidthString = String.valueOf(defaultWidth);
    String widthString = settings.getProperty(key + ".width", defaultWidthString);
    int width = Integer.parseInt(widthString), desktopWidth = desktop.getWidth();
    width = Math.min(width, desktopWidth);
    String defaultHeightString = String.valueOf(defaultHeight);
    String heightString = settings.getProperty(key + ".height", defaultHeightString);
    int height = Integer.parseInt(heightString);
    int desktopHeight = desktop.getHeight();
    height = Math.min(height, desktopHeight);
    frame.setSize(width, height);
  }

  public DataSource getDataSource()
  {
    return jeriDocument.getDatabaseAdaptor().getDataSource();
  }
  
  /**
   * This application does not use a toolbar, so this method returns <CODE>false</CODE>.
   * 
   * @return <CODE>false</CODE> since this applicatin does not use a toolbar.
   */
  public boolean usesToolbar()
  {
    return false;
  }

  /**
   * Adds the event handlers to the menu items.
   * 
   * @param commander The <CODE>Commander</CODE> that acts as the controller in the MVC.
   */
  protected void customizeCommands(Commander commander)
  {    
	commander.registerAction(new AbstractAction("machine-mode-browser")
    {
      public void actionPerformed(ActionEvent e)
      {
        machineModeBrowserMenuItem_actionPerformed(e);
      }
    });
	commander.registerAction(new AbstractAction("machine-mode-browser-on-open")
    {
      public void actionPerformed(ActionEvent e)
      {
        machineModeBrowserMenuItem_actionPerformed(e);
      }
    });
    
	commander.registerAction(new AbstractAction("export-mps-data")
    {
      public void actionPerformed(ActionEvent e)
      {
        exportMPSMenuItem_actionPerformed(e);
      }
    });
    commander.registerAction(new AbstractAction("mps-properties")
    {
      public void actionPerformed(ActionEvent e)
      {
        mpsPropertiesMenuItem_actionPerformed(e);
      }
    });
    commander.registerAction(new AbstractAction("machine-mode-defaults")
    {
      public void actionPerformed(ActionEvent e)
      {
        machineModeDefaultsMenuItem_actionPerformed(e);
      }
    });
  }  

  
  
  
}