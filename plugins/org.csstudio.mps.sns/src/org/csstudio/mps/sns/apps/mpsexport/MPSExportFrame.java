package org.csstudio.mps.sns.apps.mpsexport;

import org.csstudio.mps.sns.apps.mpsfileeditor.MPSFileEditDialog;
import org.csstudio.mps.sns.apps.templateimport.TemplateParser;
import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.math.*;

import java.sql.*;

import java.text.*;

import java.util.*;

import javax.sql.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import oracle.jdbc.pool.*;
import oracle.sql.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JRadioButton;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.tools.data.MPSChain;
import org.csstudio.mps.sns.tools.data.MPSChassis;
import org.csstudio.mps.sns.tools.data.SignalField;
import org.csstudio.mps.sns.tools.data.MPSChannel;
import org.csstudio.mps.sns.tools.data.SignalFieldType;
import org.csstudio.mps.sns.tools.data.Signal;
import org.csstudio.mps.sns.tools.data.Template;
import org.csstudio.mps.sns.tools.data.SignalType;
import org.csstudio.mps.sns.tools.data.MPSBoard;
import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.EpicsRecordType;
import org.csstudio.mps.sns.application.JeriInternalFrame;

/**
 * Provides an interface for the user to export MPS data to a db file.
 * 
 * @author Chris Fowlkes
 */
public class MPSExportFrame extends JeriInternalFrame 
{
  private JScrollPane scrollPane = new JScrollPane();
  private BorderLayout frameLayout = new BorderLayout();
  private JPanel southPanel = new JPanel();
  private JPanel buttonPanel = new JPanel();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private BorderLayout southPanelLayout = new BorderLayout();
  private GridLayout buttonPanelLayout = new GridLayout();
  private JPanel statusBarPanel = new JPanel();
  private BorderLayout statusBarPanelLayout = new BorderLayout();
  private JLabel progressLabel = new JLabel();
  private JProgressBar progressBar = new JProgressBar();
  private JList list = new JList();
  private DefaultListModel listModel = new DefaultListModel();
  /**
   * Flag used to determine if the export thread is running.
   */
  private boolean exporting = false;
  /**
   * Flag used to determine if the export or signal load has been canceled.
   */
  private boolean threadCanceled = false;
  private JPanel listPanel = new JPanel();
  private BorderLayout listPanelLayout = new BorderLayout();
  private JPanel eastPanel = new JPanel();
  private BorderLayout eastPanelLayout = new BorderLayout();
  private JPanel addButtonPanel = new JPanel();
  private GridLayout addButtonPanelLayout = new GridLayout();
  private JButton chainButton = new JButton();
  private JButton deviceButton = new JButton();
  private JButton clearButton = new JButton();
  /**
   * Holds the chains used in the export.
   */
  private Object[] allChains;
  /**
   * Holds the IOCs used in the export.
   */
  private Object[] allIOCs;
  /**
   * Holds the version from the MPS property table in the database.
   */
  private String mpsVersion;
  /**
   * Holds the MPS version date from the MPS property table in the database.
   */
  private String mpsVersionDate;
  /**
   * Holds the DB file location from the MPS property table in the database.
   */
  private String dbFileLocation;
  /**
   * Holds the image file location from the MPS property table in the database.
   */
  private String imageFileLocation;
  /**
   * Holds the name of the PCI DBD file from the MPS property table.
   */
  private String pciDBDFileName;
  /**
   * Holds the location of the template file.
   */
  private String templateFileLocation;
  /**
   * Holds the binary column names. These are the names of the binary MPS 
   * columns in the database.
   */
  private String[] binaryColumnNames;
  /**
   * Holds the file edit dialog. This shows the user all of the files to be 
   * generated.
   */
  private MPSFileEditDialog fileEditDialog;
  /**
   * Holds the icon for the print button.
   */
  private Icon printIcon;
  /**
   * Used to parse the MPS template files and store them in the database.
   */
  private TemplateParser templateParser;
  private JLabel epicsVersionLabel = new JLabel();
  private JComboBox epicsVersionCombo = new JComboBox();
  /**
   * Holds the numeric release number for the R3.13.9 EPICS release.
   */
  private int r3_13_9;
  /**
   * Holds the numeric release number for the R3.14.4 EPICS release.
   */
  private int r3_14_4;
  private JPanel radioButtonPanel = new JPanel();
  private JRadioButton unixButton = new JRadioButton();
  private JRadioButton windowsButton = new JRadioButton();
  private GridLayout radioButtonPanelLayout = new GridLayout();
  /**
   * Used to make the operating system radio buttons on the interface exclusive.
   */
  private ButtonGroup operatingSystemRadioButtonGroup = new ButtonGroup();
  /**
   * Holds the location for archive files.
   */
  private String archiveFileLocation;
  /**
   * Holds the location for csv files.
   */
  private String csvFileLocation;
  /**
   * Holds the location for the startup file.
   */
  private String startupFileLocation;
  private JPanel inUseRadioButtonPanel = new JPanel();
  private JPanel operatingSystemRadioButtonPanel = new JPanel();
  private GridLayout operatingSystemRadioButtonPanelLayout = new GridLayout();
  private GridLayout inUseRadioButtonPanelLayout = new GridLayout();
  private JRadioButton allChannelsRadioButton = new JRadioButton();
  private JRadioButton inUseRadioButton = new JRadioButton();
  /**
   * Used to make the channel radio buttons on the interface exclusive.
   */
  private ButtonGroup channelRadioButtonGroup = new ButtonGroup();
  /**
   * Holds the functionality for the interface.
   */
  private MPSExport mpsExport = new MPSExport();

  /**
   * Creates a new <CODE>ExportFrame</CODE>.
   */
  public MPSExportFrame()
  {
    try
    {
      jbInit();
      operatingSystemRadioButtonGroup.add(unixButton);
      operatingSystemRadioButtonGroup.add(windowsButton);
      channelRadioButtonGroup.add(allChannelsRadioButton);
      channelRadioButtonGroup.add(inUseRadioButton);
      listModel.addListDataListener(new ListDataListener()
      {
        public void contentsChanged(ListDataEvent e)
        {
          enableOKButton();
        }

        public void intervalAdded(ListDataEvent e)
        {
          enableOKButton();
        }

        public void intervalRemoved(ListDataEvent e)
        {
          enableOKButton();
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
   * @throws java.lanng.Exception Thrown on initialization error.
   */
  private void jbInit() throws Exception
  {
    this.getContentPane().setLayout(frameLayout);
    this.setSize(new Dimension(400, 300));
    this.setTitle("Export MPS Data");
    frameLayout.setVgap(5);
    frameLayout.setHgap(5);
    southPanel.setLayout(southPanelLayout);
    buttonPanel.setLayout(buttonPanelLayout);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    okButton.setText("Export...");
    okButton.setMnemonic('x');
    okButton.setEnabled(false);
    okButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          okButton_actionPerformed(e);
        }
      });
    cancelButton.setText("Close");
    cancelButton.setMnemonic('C');
    cancelButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          cancelButton_actionPerformed(e);
        }
      });
    southPanelLayout.setVgap(5);
    southPanelLayout.setHgap(5);
    buttonPanelLayout.setHgap(5);
    statusBarPanel.setLayout(statusBarPanelLayout);
    progressLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    progressLabel.setText(" ");
    progressBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    list.setModel(listModel);
    listPanel.setLayout(listPanelLayout);
    listPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
    listPanelLayout.setHgap(5);
    listPanelLayout.setVgap(5);
    eastPanel.setLayout(eastPanelLayout);
    addButtonPanel.setLayout(addButtonPanelLayout);
    addButtonPanelLayout.setRows(3);
    addButtonPanelLayout.setColumns(1);
    addButtonPanelLayout.setVgap(5);
    chainButton.setText("Export Chain...");
    chainButton.setMnemonic('H');
    chainButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          chainButton_actionPerformed(e);
        }
      });
    deviceButton.setText("Export IOC...");
    deviceButton.setMnemonic('I');
    deviceButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          deviceButton_actionPerformed(e);
        }
      });
    clearButton.setText("Clear");
    clearButton.setMnemonic('e');
    clearButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          clearButton_actionPerformed(e);
        }
      });
    epicsVersionLabel.setText("EPICS Version:");
    epicsVersionLabel.setDisplayedMnemonic('P');
    epicsVersionLabel.setLabelFor(epicsVersionCombo);
    epicsVersionCombo.addItemListener(new ItemListener()
      {
        public void itemStateChanged(ItemEvent e)
        {
          epicsVersionCombo_itemStateChanged(e);
        }
      });
    radioButtonPanel.setLayout(radioButtonPanelLayout);
    unixButton.setText("UNIX");
    unixButton.setSelected(true);
    unixButton.setMnemonic('U');
    windowsButton.setText("Windows");
    windowsButton.setMnemonic('W');
    radioButtonPanelLayout.setRows(2);
    radioButtonPanelLayout.setColumns(1);
    radioButtonPanelLayout.setVgap(5);
    inUseRadioButtonPanel.setLayout(inUseRadioButtonPanelLayout);
    inUseRadioButtonPanel.setBorder(BorderFactory.createTitledBorder("Channels"));
    operatingSystemRadioButtonPanel.setLayout(operatingSystemRadioButtonPanelLayout);
    operatingSystemRadioButtonPanel.setBorder(BorderFactory.createTitledBorder("OS"));
    operatingSystemRadioButtonPanelLayout.setRows(2);
    inUseRadioButtonPanelLayout.setRows(2);
    allChannelsRadioButton.setText("All");
    allChannelsRadioButton.setMnemonic('L');
    inUseRadioButton.setText("In Use");
    inUseRadioButton.setMnemonic('U');
    inUseRadioButton.setSelected(true);
    eastPanel.add(addButtonPanel, BorderLayout.NORTH);
    inUseRadioButtonPanel.add(allChannelsRadioButton, null);
    inUseRadioButtonPanel.add(inUseRadioButton, null);
    radioButtonPanel.add(inUseRadioButtonPanel, null);
    operatingSystemRadioButtonPanel.add(unixButton, null);
    operatingSystemRadioButtonPanel.add(windowsButton, null);
    radioButtonPanel.add(operatingSystemRadioButtonPanel, null);
    eastPanel.add(radioButtonPanel, BorderLayout.SOUTH);
    addButtonPanel.add(chainButton, null);
    addButtonPanel.add(deviceButton, null);
    addButtonPanel.add(clearButton, null);
    statusBarPanel.add(progressLabel, BorderLayout.CENTER);
    statusBarPanel.add(progressBar, BorderLayout.EAST);
    buttonPanel.add(okButton, null);
    buttonPanel.add(cancelButton, null);
    southPanel.add(buttonPanel, BorderLayout.EAST);
    southPanel.add(statusBarPanel, BorderLayout.SOUTH);
    southPanel.add(epicsVersionLabel, BorderLayout.WEST);
    southPanel.add(epicsVersionCombo, BorderLayout.CENTER);
    scrollPane.getViewport().add(list, null);
    listPanel.add(scrollPane, BorderLayout.CENTER);
    listPanel.add(eastPanel, BorderLayout.EAST);
    this.getContentPane().add(southPanel, BorderLayout.SOUTH);
    this.getContentPane().add(listPanel, BorderLayout.CENTER);
  }

  /**
   * Called when the file button is clicked. This method allows the user to 
   * choose a source file from the database to export.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void chainButton_actionPerformed(ActionEvent e)
  {
    boolean multipleChainWarningShown = false;
    MPSChain selectedChain = (MPSChain)JOptionPane.showInputDialog(this, "Select a chain to export.", "Select Chain", JOptionPane.QUESTION_MESSAGE, null, allChains, allChains[(0)]);
    if(selectedChain != null)
    {
      int listItemCount = listModel.size();
      ArrayList listDeviceIDs = new ArrayList(listItemCount);
      for(int i=0;i<listItemCount;i++)
        listDeviceIDs.add(((MPSBoard)listModel.get(i)).getID());
      int boardCount = selectedChain.getBoardCount();
      for(int i=0;i<boardCount;i++)
      {
        MPSBoard currentBoard = selectedChain.getBoardAt(i);
        if(! multipleChainWarningShown)
        {
          MPSChassis currentChassis = currentBoard.getChassis();
          int chassisBoardCount = currentChassis.getBoardCount();
          for(int j=0;j<chassisBoardCount;j++)
            if(! currentChassis.getBoardAt(j).getChain().equals(selectedChain))
            {
              multipleChainWarningShown = true;
              JOptionPane.showMessageDialog(this, "MIOC's one or more of these IOCs are in different Chains!", "Multiple Chain Warning", JOptionPane.WARNING_MESSAGE);
              break;
            }
        }
        String currentDeviceID = currentBoard.getID();
        if(! listDeviceIDs.contains(currentDeviceID))
        {
          listDeviceIDs.add(currentDeviceID);
          listModel.addElement(currentBoard);
        }
      }
    }
  }

  /**
   * Called when the export group button is clicked. It allows the user to 
   * select a group of signals to export.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void deviceButton_actionPerformed(ActionEvent e)
  {
    MPSChassis selectedIOC = (MPSChassis)JOptionPane.showInputDialog(this, "Select an IOC to export.", "Select IOC", JOptionPane.QUESTION_MESSAGE, null, allIOCs, allIOCs[(0)]);
    if(selectedIOC != null)
    {
      int deviceCount = selectedIOC.getBoardCount();
      for(int i=0;i<deviceCount;i++) 
        listModel.addElement(selectedIOC.getBoardAt(i));
    }
  }
  
  /**
   * Called when the clear button is clicked. This method clears the 
   * <CODE>JList</CODE>.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void clearButton_actionPerformed(ActionEvent e)
  {
    listModel.clear();
  }

  /**
   * Called when the export button is clicked. This method starts the data export
   * process.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocatin of this method.
   */
  private void okButton_actionPerformed(ActionEvent e)
  {
    chainButton.setEnabled(false);
    deviceButton.setEnabled(false);
    clearButton.setEnabled(false);
    okButton.setEnabled(false);
    cancelButton.setText("Cancel");
    Thread fileThread = new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          exporting = true;
          threadCanceled = false;
          MPSBoard[] boards = new MPSBoard[listModel.size()];
          for(int i=0;i<boards.length;i++)
            boards[i] = (MPSBoard)listModel.get(i);
          loadChannelData(boards);
          if(threadCanceled)
            return;
          if(fileEditDialog == null)
          {
            fileEditDialog = new MPSFileEditDialog(getMainWindow(), "Export Files", true);
            fileEditDialog.setPrintIcon(getPrintIcon());
            fileEditDialog.setApplicationProperties(getApplicationProperties());
            fileEditDialog.center();
          }
          fileEditDialog.setFiles(createFiles(boards));
          if(threadCanceled)
            return;
          SwingUtilities.invokeAndWait(new Runnable()
          {
            public void run()
            {
              fileEditDialog.setVisible(true);
            }
          });
          if(fileEditDialog.getResult() == MPSFileEditDialog.OK && ! threadCanceled)
          {
            HashMap filesToSave = fileEditDialog.getFiles();
            saveFiles(filesToSave);
            Object notes = filesToSave.get("NOTES");
            if(notes != null)
              saveNotes(notes.toString(), boards);
          }
        }
        catch(java.sql.SQLException ex)
        {
          ex.printStackTrace();
          showError(ex.getMessage(), "SQL Error");
        }
        catch(java.lang.reflect.InvocationTargetException ex)
        {
          ex.printStackTrace();
          showError(ex.getMessage(), "Error");
        }
        catch(java.lang.InterruptedException ex)
        {
          ex.printStackTrace();
          showError(ex.getMessage(), "Error");
        }
        catch(java.io.IOException ex)
        {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(MPSExportFrame.this, ex.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
        }
        finally
        {
          exporting = false;
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              chainButton.setEnabled(true);
              deviceButton.setEnabled(true);
              clearButton.setEnabled(true);
              enableOKButton();
              cancelButton.setText("Close");
            }
          });
        }
      }
    });
    fileThread.start();  
  }

  /**
   * Saves the value entered into the notes field by the user to a stored 
   * procedure.
   *
   * @param notes The notes from the notes tab to save.
   * @param boards The instances of <CODE>MPSBoard</CODE> being exported.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  private void saveNotes(String notes, MPSBoard[] boards) throws java.sql.SQLException
  {
    Connection oracleConnection = getDataSource().getConnection();
    try
    {
      Statement query = oracleConnection.createStatement();
      try
      {
        StringBuffer buffer = new StringBuffer("SELECT BN, FIRST_NM, LAST_NAME FROM EMPLOYEE_V WHERE USER_ID = '");
        buffer.append(((OracleDataSource)getDataSource()).getUser().toUpperCase());
        buffer.append("'");
        ResultSet userData = query.executeQuery(buffer.toString());
        try
        {
          userData.next();
          String procedureCall = "{call LOGBOOK.LOGBOOK_PKG.INSERT_LOGBOOK_ENTRY(?, ?, ?, ?, ?)}";
          CallableStatement procedure = oracleConnection.prepareCall(procedureCall);
          try
          {
            procedure.setString(1, userData.getString("BN"));
            procedure.setString(2, "Machine Protection System");
            procedure.setString(3, "Machine Protection System configuration files generated");
            procedure.setNull(4, Types.VARCHAR);
            buffer = new StringBuffer("Device(s): ");
            for(int i=0;i<boards.length;i++)
            {
              if(i > 0)
                buffer.append(", ");
              buffer.append(boards[i].getID());
            }
            buffer.append("\nDate: ");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
            buffer.append(dateFormat.format(new java.util.Date()));
            buffer.append("\nGenerated By: ");
            buffer.append(userData.getString("FIRST_NM"));
            buffer.append(" ");
            buffer.append(userData.getString("LAST_NAME"));
            buffer.append("\nMPS configuration file(s) have been generated for the device(s) above.\n");
            notes = notes.trim();
            if(! notes.equals(""))
            {
              buffer.append("Note: ");
              buffer.append(notes);
            }
            procedure.setString(5, buffer.toString());
            procedure.execute();
          }
          finally
          {
            procedure.close();
          }
        }
        finally
        {
          userData.close();
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
  
  /**
   * Saves the files being exported. All of the files are saved to the directory
   * passed in exceot for the archive request file. It is saved to the directory
   * specified in the MPS_PROP.ARCH_REQ_FILE_LOC field in the database.
   * 
   * @param saveDirectory The directory to save the files to.
   * @param files The file names and contents to save.
   * @throws java.io.IOException Thrown on IO error.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  private void saveFiles(final HashMap files) throws java.io.IOException, java.sql.SQLException
  {
    setProgressMaximum(files.size());
    int progress = 0;
    setProgressValue(progress);
    try
    {
      setMessage("Writing Files");
      Iterator fileNames = files.keySet().iterator();
      while(fileNames.hasNext())
      {
        String currentFileName = fileNames.next().toString();
        if(currentFileName.equals("NOTES"))
          continue;//Skip notes tab since it's not saved as a file.
        File currentFile;
        currentFile = new File(currentFileName);
        BufferedWriter oStream = new BufferedWriter(new FileWriter(currentFile));
        try
        {
          oStream.write(files.get(currentFileName).toString());
          oStream.flush();
        }
        finally
        {
          oStream.close();
        }
        setProgressValue(++progress);
      }
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          StringBuffer message = new StringBuffer("<HTML>Successfully exported the data to the following files:<UL>");
          Iterator fileIterator = files.keySet().iterator();
          while(fileIterator.hasNext())
          {
            message.append("<LI>");
            message.append(fileIterator.next());
            message.append("</LI>");
          }
          message.append("</UL></HTML>");
          String messageString = message.toString();
          JOptionPane.showMessageDialog(MPSExportFrame.this, messageString, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
      });
    }
    finally
    {
      setProgressValue(0);
      setMessage(" ");
    }
  }

  /**
   * Loads the channel data from the database for the given instances of 
   * <CODE>MPSBoard</CODE>.
   * 
   * @param selectedBoards The instances of <CODE>MPSBoard</CODE> for which to load the channel data.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  private void loadChannelData(MPSBoard[] selectedBoards) throws java.sql.SQLException
  {
    setProgressMaximum(selectedBoards.length - 1);
    setProgressValue(0);
    try
    {
      setMessage("Loading Channel Data");
      Connection oracleConnection = getDataSource().getConnection();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT FPAR_FPL_CONFIG, IOC_DVC_ID, PMC_NBR, CHAIN_END_IND FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".MPS_SGNL_PARAM WHERE DVC_ID = ? AND APPR_DTE = ?");
        PreparedStatement boardQuery = oracleConnection.prepareStatement(sql.toString());
        try
        {
          sql = new StringBuffer("SELECT MACHINE_MODE.* FROM ");
          sql.append(MPSBrowserView.SCHEMA);
          sql.append(".MACHINE_MODE WHERE MACHINE_MODE.DVC_ID = ? AND MACHINE_MODE.APPR_DTE = ?");
          PreparedStatement channelQuery = oracleConnection.prepareStatement(sql.toString());
          try
          {
            for(int i=0;i<selectedBoards.length;i++)
            {
              String currentDeviceID = selectedBoards[i].getID();
              boardQuery.setString(1, currentDeviceID);
              java.sql.Date currentApproveDate = selectedBoards[i].getApproveDate();
              boardQuery.setDate(2, currentApproveDate);
              ResultSet result = boardQuery.executeQuery();
              try
              {
                result.next();
                String configuration = result.getString("FPAR_FPL_CONFIG");
                selectedBoards[i].setFPARFastProtectLatchConfig(configuration);
                selectedBoards[i].setPMCNumber(result.getInt("PMC_NBR"));
                selectedBoards[i].setChainEndIndicator(result.getString("CHAIN_END_IND"));
                channelQuery.setString(1, currentDeviceID);
                channelQuery.setDate(2, currentApproveDate);
                result = channelQuery.executeQuery();
                while(result.next())
                {
                  if(binaryColumnNames == null)
                    loadColumnNames(result);
                  int currentChannelNumber = result.getInt("CHANNEL_NBR");
                  MPSChannel currentChannel = new MPSChannel(currentChannelNumber);
                  String currentMPSDeviceID = result.getString("MPS_DVC_ID");
                  if(currentMPSDeviceID != null)
                  {
                    Device currentDevice = new Device(currentMPSDeviceID);
                    currentChannel.setDevice(currentDevice);
                  }
                  currentChannel.setRate(result.getInt("RATE"));
                  currentChannel.setLimit(result.getInt("LIMIT"));
                  currentChannel.setSWJumper(result.getString("SW_JUMP"));
                  String inUse = result.getString("CHAN_IN_USE_IND");
                  if(inUse == null)
                    currentChannel.setInUseIndicator("N");
                  else
                    currentChannel.setInUseIndicator(inUse);
                  for(int j=0;j<binaryColumnNames.length;j++)
                  {
                    byte[] currentBytes = result.getBytes(binaryColumnNames[j]);
                    BigInteger currentBinaryValue;
                    if(currentBytes == null)
                      currentBinaryValue = new BigInteger("1");
                    else
                      currentBinaryValue = new BigInteger(currentBytes);
                    currentChannel.setBinaryValue(binaryColumnNames[j], currentBinaryValue);
                  }
                  selectedBoards[i].addChannel(currentChannel);
                }
              }
              finally
              {
                result.close();
              }
              setProgressValue(i);
            }
          }
          finally
          {
            channelQuery.close();
          }
        }
        finally
        {
          boardQuery.close();
        }
      }
      finally
      {
        oracleConnection.close();
      }
    }
    finally
    {
      setProgressValue(0);
      setMessage(" ");
    }
  }

  /**
   * Loads the names of the columns in the given <CODE>ResultSet</CODE>. The
   * column names are loaded into the <CODE>binaryColumns</CODE> variable.
   * 
   * @param result The <CODE>ResultSet</CODE> for which to return the column names.
   */
  private void loadColumnNames(ResultSet result) throws java.sql.SQLException
  {
    ResultSetMetaData metaData = result.getMetaData();
    int binaryColumnCount = 38;
    binaryColumnNames = new String[binaryColumnCount];
    for(int i=0;i<binaryColumnCount;i++)
      binaryColumnNames[i] = metaData.getColumnName(i + 8);
  }

  /**
   * Creates and returns all of the output files. The files are returned in a 
   * <CODE>HashMap</CODE> with the file names as keys and contents as values.
   * 
   * @param selectedBoards The instances of <CODE>MPSBoard</CODE> to export.
   * @return A <CODE>HashMap</CODE> containing the file names and contents.
   * @throws java.sql.SQLException Thrown on sql error.
   * @throws java.io.IOException Thrown on IO error.
   */
  private HashMap createFiles(MPSBoard[] selectedBoards) throws java.sql.SQLException, java.io.IOException
  {
    HashMap allFiles = new HashMap(selectedBoards.length + 4);
    setProgressMaximum(selectedBoards.length - 1);
    setProgressValue(0);
    try
    {
      reloadTemplate("mps.template");
      reloadTemplate("mps_input.template");
      setMessage("Generating File Contents");
      loadMPSProperties();
      ArrayList chassisFilesCreated = new ArrayList();
      for(int i=0;i<selectedBoards.length;i++)
      {
        String currentChainID = selectedBoards[i].getChain().getID();
        StringBuffer fileName = new StringBuffer(csvFileLocation);
        fileName.append(currentChainID.toUpperCase());
        fileName.append("Chain/");
        fileName.append(selectedBoards[i].getID().replace(':', '_'));
        fileName.append(".csv");
        allFiles.put(fileName.toString(), createCSVFile(selectedBoards[i]));
        MPSChassis currentChassis = selectedBoards[i].getChassis();
        String currentChassisID = currentChassis.getID();
        if(! chassisFilesCreated.contains(currentChassisID))
        {
          String chassisDirectory = currentChassisID.replace(':', '_').toUpperCase();
          //Creating the startup file...
          ArrayList chassisBoardList = new ArrayList();
          chassisBoardList.add(selectedBoards[i]);
          for(int j=i+1;j<selectedBoards.length;j++)
          {
            MPSBoard currentChassisBoard = selectedBoards[j];
            if(currentChassisBoard.getChassis() == currentChassis)
              chassisBoardList.add(currentChassisBoard);
          }
          MPSBoard[] chassisBoards = new MPSBoard[chassisBoardList.size()];
          chassisBoardList.toArray(chassisBoards);
          fileName = new StringBuffer(chassisDirectory);
          fileName.append(".db");
          String dbFileName = fileName.toString();
          fileName = new StringBuffer(startupFileLocation);
          fileName.append(chassisDirectory);
          fileName.append(".mps");
          allFiles.put(fileName.toString(), createLoadFile(dbFileName, chassisBoards));
          //Next we'll create the substitutions file.
          fileName = new StringBuffer(dbFileLocation);
          fileName.append(chassisDirectory);
          fileName.append(".substitutions");
          allFiles.put(fileName.toString(), createSubstitutionsFile(chassisBoards));
          //finally we create the db file.
          boolean allChannels = allChannelsRadioButton.isSelected();
          String newDBFile = mpsExport.createDBFile(chassisBoards, allChannels);
          if(newDBFile == null)
            newDBFile = "";
          fileName = new StringBuffer(dbFileLocation);
          fileName.append(dbFileName);
          allFiles.put(fileName.toString(), newDBFile);
          chassisFilesCreated.add(currentChassisID);
        }
        if(threadCanceled)
          break;
        //Some of the sub methods use the progress bar. Reset values each time.
        setMessage("Generating File Contents");
        setProgressMaximum(selectedBoards.length - 1);
        setProgressValue(i);
      }
      allFiles.put("NOTES", "");
    }
    finally
    {
      setProgressValue(0);
      setMessage(" ");
    }
    return allFiles;
  }

  /**
   * Loads a template file into the database.
   * 
   * @param templateFile The template to load into the database.
   * @throws java.sql.SQLException Thrown on sql error.
   * @throws java.io.IOException Thrown on IO error.
   */
  private String reloadTemplate(String fileName) throws java.sql.SQLException, java.io.IOException
  {
    String templateContents;
    File templateFile = new File(templateFileLocation, fileName);
    if(templateFile.exists())
    {
      //Need to check the date.
      Connection oracleConnection = getDataSource().getConnection();
      try
      {
        Statement query = oracleConnection.createStatement();
        try
        {
          String templateID = fileName.split("\\.")[0];
          StringBuffer sql = new StringBuffer("SELECT EXT_SRC_FILE_MOD_DTE FROM ");
          sql.append(MPSBrowserView.SCHEMA);
          sql.append(".TEMPLATE WHERE TMPL_ID = '");
          sql.append(templateID);
          sql.append("'");
          ResultSet result = query.executeQuery(sql.toString());
          try
          {
            if(result.next())
            {
              Timestamp databaseDate = result.getTimestamp("EXT_SRC_FILE_MOD_DTE");
              databaseDate.setNanos(0);
              Timestamp fileDate = new Timestamp(templateFile.lastModified());
              fileDate.setNanos(0);
              if(databaseDate.before(fileDate))
                templateContents = loadTemplateFromFile(templateFile);
              else
              {
                templateContents = loadTemplateFromDatabase(templateID);
              }
            }
            else
              templateContents = loadTemplateFromFile(templateFile);
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
    else
      templateContents = loadTemplateFromDatabase(fileName);
    return templateContents;
  }

  /**
   * Loads the given template from the database. 
   * 
   * @param The ID of the template to load from the database.
   * @return The contents for the given template ID.
   */
  private String loadTemplateFromDatabase(String templateID) throws java.sql.SQLException
  {
    setMessage("Loading Templates");
    Template databaseTemplate = new Template();
    Connection oracleConnection = getDataSource().getConnection();
    setProgressIndeterminate(true);
    try
    {
      oracleConnection.setAutoCommit(false);
      Statement query = oracleConnection.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".TMPL_SGNL_FLD, ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".TEMPLATE WHERE TMPL_SGNL_FLD.TMPL_ID = TEMPLATE.TMPL_ID AND TEMPLATE.TMPL_ID = '");
        sql.append(templateID);
        sql.append("'");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          result.next();
          setProgressMaximum(result.getInt(1));
          sql = new StringBuffer("SELECT TMPL_SGNL_ID, FLD_ID, REC_TYPE_ID, VAL FROM ");
          sql.append(MPSBrowserView.SCHEMA);
          sql.append(".TMPL_SGNL_FLD, ");
          sql.append(MPSBrowserView.SCHEMA);
          sql.append(".TEMPLATE WHERE TMPL_SGNL_FLD.TMPL_ID = TEMPLATE.TMPL_ID AND TEMPLATE.TMPL_ID = '");
          sql.append(templateID);
          sql.append("'");
          result = query.executeQuery(sql.toString());
          String currentSignalID = "";
          Signal currentSignal = null;
          int progress = 0;
          setProgressValue(0);
          setProgressIndeterminate(false);
          while(result.next())
          {
            String newSignalID = result.getString("TMPL_SGNL_ID");
            if(! newSignalID.equals(currentSignalID))
            {
              currentSignalID = newSignalID;
              currentSignal = new Signal(currentSignalID);
              databaseTemplate.addSignal(currentSignal);
              SignalType currentSignalType = new SignalType();
              String currentRecordTypeID = result.getString("REC_TYPE_ID");
              EpicsRecordType currentEpicsRecordType = new EpicsRecordType(currentRecordTypeID);
              currentSignalType.setRecordType(currentEpicsRecordType);
              currentSignal.setType(currentSignalType);
            }
            SignalField currentField = new SignalField();
            currentSignal.addField(currentField);
            String currentFieldID = result.getString("FLD_ID");
            currentField.setType(new SignalFieldType(currentFieldID));
            currentField.setValue(result.getString("VAL"));
            setProgressValue(++progress);
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
      setProgressIndeterminate(false);
      setProgressValue(0);
      oracleConnection.close();
    }
    return convertTemplateToFile(databaseTemplate);
  }

  /**
   * Loads a template from a <CODE>File</CODE>. This method loads and returns 
   * the template from a <CODE>File</CODE>. The template is also saved in the 
   * database.
   * 
   * @param templateFile The <CODE>File</CODE> containing the template to load.
   * @return The contents of the <CODE>File</CODE>.
   */
  private String loadTemplateFromFile(File templateFile) throws java.sql.SQLException
  {
    if(templateParser == null)
    {
      templateParser = new TemplateParser();
      templateParser.setDataSource(getDataSource());
      templateParser.setProgressBar(progressBar);
      templateParser.setProgressLabel(progressLabel);
    }
    templateParser.setTemplateFiles(new File[]{templateFile});
    templateParser.parse();
    if(threadCanceled)
      return null;
    templateParser.saveToDatabase();
    if(threadCanceled)
      return null;
    Template currentTemplate = (Template)templateParser.getTemplates().get(0);
    return convertTemplateToFile(currentTemplate);
  }

  /**
   * Converts an <CODE>Template</CODE> to it's corresponding file contents.
   * 
   * @param currentTemplate The <CODE>Template</CODE> to convert.
   * @return The file contents of the corresponding <CODE>Template</CODE>.
   */
  private String convertTemplateToFile(Template currentTemplate)
  {
    String newLine = System.getProperty("line.separator");
    StringBuffer fileContents = new StringBuffer();
    int signalCount = currentTemplate.getSignalCount();
    for(int i=0;i<signalCount;i++)
    {
      Signal currentSignal = currentTemplate.getSignalAt(i);
      fileContents.append("record(");
      fileContents.append(currentSignal.getType().getRecordType().getID());
      fileContents.append(", \"");
      fileContents.append(currentSignal.getID());
      fileContents.append("\") {");
      fileContents.append(newLine);
      int fieldCount = currentSignal.getFieldCount();
      for(int j=0;j<fieldCount;j++)
      {
        SignalField currentField = currentSignal.getFieldAt(j);
        fileContents.append("\tfield(");
        fileContents.append(currentField.getType().getID());
        fileContents.append(", \"");
        fileContents.append(currentField.getValue());
        fileContents.append("\")");
        fileContents.append(newLine);
      }
      fileContents.append("}");
      fileContents.append(newLine);
      fileContents.append(newLine);
    }
    return fileContents.toString();
  }

  /**
   * Creates a substitutions file for a chassis.
   * 
   * @param boardsInChassis The boards in the chassis for which to create the substitutions file.
   * @return The contents of a substitutions file corresponding to the chassis.
   */
  private String createSubstitutionsFile(MPSBoard[] boardsInChassis) throws java.sql.SQLException
  {
    StringWriter oStream = new StringWriter();
    oStream.write("file ");
    String epicsVersion = epicsVersionCombo.getSelectedItem().toString();
    String templateFileLocation = this.templateFileLocation.replaceAll("\\$EPICSVER", epicsVersion);
    oStream.write(templateFileLocation);
    oStream.write("/mps.template {");
    String newLine = System.getProperty("line.separator");
    oStream.write(newLine);
    oStream.write(newLine);
    for(int i=0;i<boardsInChassis.length;i++)
    {
      oStream.write("{ mps_chas=");
      oStream.write(boardsInChassis[i].getID());
      oStream.write(" , board=");
      oStream.write(String.valueOf(boardsInChassis[i].getPMCNumber()));
      oStream.write(" }");
      oStream.write(newLine);
    }
    oStream.write(newLine);
    oStream.write("}");
    oStream.write(newLine);
    oStream.write(newLine);
    oStream.write("file ");
    oStream.write(templateFileLocation);
    oStream.write("/mps_input.template {");
    oStream.write(newLine);
    oStream.write(newLine);
    for(int i=0;i<boardsInChassis.length;i++)
    {
      String fparFPLConfig = boardsInChassis[i].getFPARFastProtectLatchConfiguration();
      for(int j=0;j<16;j++)
      {
        MPSChannel currentChannel = boardsInChassis[i].channelAt(j);
        if(allChannelsRadioButton.isSelected() || currentChannel.getInUseIndicator().equals("Y"))
        {
          Device currentDevice = currentChannel.getDevice();
          if(currentDevice != null)
          {
            oStream.write("{ mps_in=");
            oStream.write(currentDevice.getID());
            oStream.write(", mps_chas=");
            oStream.write(boardsInChassis[i].getID());
            oStream.write(", board=");
            oStream.write(String.valueOf(boardsInChassis[i].getPMCNumber()));
            oStream.write(", chan=");
            int currentChannelNumber = currentChannel.getNumber();
            oStream.write(String.valueOf(currentChannelNumber));
            oStream.write(", rate=");
            oStream.write(String.valueOf(currentChannel.getRate()));
            oStream.write(", limit=");
            oStream.write(String.valueOf(currentChannel.getLimit()));
            oStream.write(", chain=");
            if(fparFPLConfig.equals("8L8AR"))
            {
              if(currentChannelNumber < 8)
                oStream.write("FPAR_");
              else
                if(currentChannelNumber < 16)
                  oStream.write("FPL_");
            }
            else
              if(fparFPLConfig.equals("16FPAR"))
                oStream.write("FPAR_");
              else
                if(fparFPLConfig.equals("16L"))
                  oStream.write("FPL_");
            oStream.write(boardsInChassis[i].getChain().getID());
            oStream.write(" }");
            oStream.write(newLine);
          }
        }
      }
    }
    oStream.write(newLine);
    oStream.write("}");
    oStream.flush();
    return oStream.getBuffer().toString();
  }

  /**
   * Creates a load file for a chassis.
   * 
   * @param boardsInChassis The instances of <CODE>MPSBoard</CODE> for the chassis. This should contain atleast on item.
   * @param dbFileName The name of the db file to be created for the chassis.
   * @return The contents of a load file for the given chassis.
   */
  private String createLoadFile(String dbFileName, MPSBoard[] boardsInChassis) throws java.sql.SQLException
  {
    int selectedEpicsVersion = epicsVersionCombo.getSelectedIndex();
    StringWriter oStream = new StringWriter();
    String newLine = System.getProperty("line.separator");
    if(boardsInChassis[0].getChainEndIndicator().equals("Y"))
    {
      oStream.write("cd pcidirectbin");
      oStream.write(newLine);
      oStream.write("ld <pciDirectLib");
      if(selectedEpicsVersion >= r3_14_4)
        oStream.write(".munch");
      oStream.write(newLine);
      oStream.write(newLine);
      oStream.write("cd pcidirect");
      oStream.write(newLine);
      oStream.write("dbLoadDatabase(\"dbd/");
      oStream.write(pciDBDFileName);
      oStream.write("\"");
      if(selectedEpicsVersion >= r3_14_4)
        oStream.write(",0,0");
      oStream.write(")");
      oStream.write(newLine);
      if(selectedEpicsVersion >= r3_14_4)
      {
        oStream.write("pciDirect_registerRecordDeviceDriver(pdbbase)");
        oStream.write(newLine);
      }
      oStream.write(newLine);
    }
    oStream.write("#Load the new MPS driver");
    oStream.write(newLine);
    oStream.write("cd mps_driverbin");
    oStream.write(newLine);
    oStream.write("ld <mpsLib.");
    if(selectedEpicsVersion >= r3_14_4)
      oStream.write("munch");
    else
      oStream.write("o");
    oStream.write(newLine);
    oStream.write(newLine);
    oStream.write("# Load  the database for the mps driver");
    oStream.write(newLine);
    oStream.write("cd mps_driver");
    oStream.write(newLine);
    oStream.write("dbLoadDatabase(\"dbd/drvMps.dbd\")");
    oStream.write(newLine);    
    if(selectedEpicsVersion >= r3_14_4)
    {
      oStream.write("drvMps_registerRecordDeviceDriver()");
      oStream.write(newLine);
    }
    oStream.write(newLine);
    oStream.write(newLine);
    oStream.write("cd mps");
    oStream.write(newLine);
    oStream.write("dbLoadRecords(\"db/");
    oStream.write(dbFileName);
    oStream.write("\"");
    oStream.write(")");
    oStream.write(newLine);
    oStream.write(newLine);
    oStream.write("cd mps");
    oStream.write(newLine);
    oStream.write("pmcimage = \"MPS_fpga/mps_");
    int versionDateHalf = mpsVersionDate.length() / 2;
    String firstHalf = mpsVersionDate.substring(0, versionDateHalf);
    firstHalf = firstHalf.substring(firstHalf.indexOf('x') + 1);
    oStream.write(firstHalf);
    oStream.write("_");
    String secondHalf = mpsVersionDate.substring(versionDateHalf);
    secondHalf = secondHalf.substring(secondHalf.indexOf('x') + 1);
    oStream.write(secondHalf);
    oStream.write(".rbf\"");
    oStream.write(newLine);
    if(boardsInChassis[0].getChainEndIndicator().equals("Y"))
    {
      oStream.write("pmcimage1 = \"MPS_fpga/mpsdriver.rbf\"");
      oStream.write(newLine);
    }
    oStream.write(newLine);
    int maxPMC = 0;
    for(int i=0;i<boardsInChassis.length;i++)
    {
      oStream.write("ldMps ");
      int currentPMC = boardsInChassis[i].getPMCNumber();
      oStream.write(String.valueOf(currentPMC));
      oStream.write(",pmcimage");
      oStream.write(newLine);
      maxPMC = Math.max(maxPMC, currentPMC);
    }
    if(boardsInChassis[0].getChainEndIndicator().equals("Y"))
    {
      oStream.write("ldMps ");
      oStream.write(String.valueOf(maxPMC + 1));
      oStream.write(",pmcimage1");
      oStream.write(newLine);
    }
    oStream.write(newLine);
    oStream.write("cd mps");
    oStream.write(newLine);
    for(int i=0;i<boardsInChassis.length;i++)
    {
      oStream.write("maskFile");
      oStream.write(String.valueOf(boardsInChassis[i].getPMCNumber()));
      oStream.write("=\"");
      oStream.write(boardsInChassis[i].getChain().getID().replace(':', '_').toUpperCase());
      oStream.write("Chain/");
      oStream.write(boardsInChassis[i].getID().replace(':', '_'));
      oStream.write(".csv\"");
      oStream.write(newLine);
    }
    oStream.write(newLine);
    for(int i=0;i<boardsInChassis.length;i++)
    {
      oStream.write("ldMpsMask ");
      String boardNumber = String.valueOf(boardsInChassis[i].getPMCNumber());
      oStream.write(boardNumber);
      oStream.write(",maskFile");
      oStream.write(boardNumber);
      oStream.write(newLine);
    }
    oStream.flush();
    return oStream.getBuffer().toString();
  }

  /**
   * Creates a csv file for the given <CODE>MPSBoard</CODE>.
   * 
   * @param board The <CODE>MPSBoard</CODE> for which to create the csv file.
   * @return The contents of a csv file for the given <CODE>MPSBoard</CODE>.
   */
  private String createCSVFile(MPSBoard board) throws java.sql.SQLException
  {
    StringWriter oStream = new StringWriter();
    oStream.write("#");
    oStream.write(board.getID());
    oStream.write(",");
    oStream.write(board.getChain().getID());
    String newLine = System.getProperty("line.separator");
    oStream.write(newLine);
    oStream.write("# Computer  generated file DO NOT HAND EDIT");
    oStream.write(newLine);
    oStream.write("# MODE MASK configuration file for ");
    oStream.write(board.getChassis().getID());
    oStream.write(newLine);
    oStream.write("#Config check fields:");
    oStream.write(newLine);
    oStream.write("#This value is compared to a string field in the database.");
    oStream.write(newLine);
    oStream.write("version,");
    oStream.write(mpsVersion);
    oStream.write(",");
    oStream.write(newLine);
    oStream.write("#The values below must match values read from the hardware");
    oStream.write(newLine);
    oStream.write("j1-j2,");
    String configuration = board.getFPARFastProtectLatchConfiguration();
    if(configuration.equals("16L"))
      oStream.write("2");
    else
      if(configuration.equals("16FPAR"))
        oStream.write("1");
      else
        if(configuration.equals("8L8AR"))
          oStream.write("3");
    oStream.write(",");
    oStream.write(newLine);
    oStream.write("maskJump,");
    int maskJump = board.getMaskJump();
    oStream.write(String.valueOf(maskJump));
    oStream.write(newLine);
    oStream.write("fpga,");
    int versionDateHalf = mpsVersionDate.length() / 2;
    oStream.write(mpsVersionDate.substring(0, versionDateHalf));
    oStream.write(",");
    oStream.write(mpsVersionDate.substring(versionDateHalf));
    oStream.write(newLine);
    oStream.write("chassis,");
    String currentSerialNumber = board.getSerialNumber();
    if(currentSerialNumber == null)
      throw new SQLException(board.getID() + " has a null serial number. Export failed.");
    int serialNumberHalf = currentSerialNumber.length() / 2;
    oStream.write(currentSerialNumber.substring(0, serialNumberHalf));
    oStream.write(",");
    oStream.write(currentSerialNumber.substring(serialNumberHalf));
    oStream.write(newLine);
    oStream.write("#modemasks");
    oStream.write(newLine);
    int[] checksums = new int[]{0, 0, 0};
    for(int i=0;i<binaryColumnNames.length;i++)
    {
      oStream.write(String.valueOf(i));
      oStream.write(",");
      int[] currentBinaryValues = board.getModeMask(binaryColumnNames[i]);
      oStream.write(String.valueOf(currentBinaryValues[0]));
      oStream.write(",");
      oStream.write(String.valueOf(currentBinaryValues[1]));
      oStream.write(newLine);
      checksums[0] += i;
      checksums[1] += currentBinaryValues[0];
      checksums[2] += currentBinaryValues[1];
    }
    oStream.write(String.valueOf(checksums[0] * -1));
    oStream.write(",");
    oStream.write(String.valueOf(checksums[1]));
    oStream.write(",");
    oStream.write(String.valueOf(checksums[2]));
    oStream.flush();
    return oStream.getBuffer().toString();
  }

  /**
   * Loads the data in the MPS properties table and stores it in several 
   * variables.
   */
  private void loadMPSProperties() throws java.sql.SQLException
  {
    Connection oracleConnection = getDataSource().getConnection();
    try
    {
      Statement query = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      try
      {
        StringBuffer sql = new StringBuffer("SELECT VER, VER_DTE, DB_FILE_LOC, IMAGE_FILE_LOC, TMPL_FILE_LOC, CSV_FILE_LOC, ARCH_REQ_FILE_LOC, STARTUP_FILE_LOC, PCI_DBD_FILE_NM FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".MPS_PROP WHERE UPPER(OS_ID) = '");
        if(unixButton.isSelected())
          sql.append("UNIX");
        else
          sql.append("WINDOWS");
        sql.append("'");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          result.last();
          String directorySeperator;
          if(unixButton.isSelected())
            directorySeperator = "/";
          else
            directorySeperator = "\\";
          String epicsVersion = epicsVersionCombo.getSelectedItem().toString();
          mpsVersion = result.getString("VER");
          mpsVersionDate = result.getString("VER_DTE");
          dbFileLocation = result.getString("DB_FILE_LOC");
          dbFileLocation = dbFileLocation.replaceAll("\\$EPICSVER", epicsVersion);
          if(! dbFileLocation.endsWith(directorySeperator))
            dbFileLocation += directorySeperator;
          imageFileLocation = result.getString("IMAGE_FILE_LOC");
          imageFileLocation = imageFileLocation.replaceAll("\\$EPICSVER", epicsVersion);
          templateFileLocation = result.getString("TMPL_FILE_LOC");
          templateFileLocation = templateFileLocation.replaceAll("\\$EPICSVER", epicsVersion);
          csvFileLocation  = result.getString("CSV_FILE_LOC");
          csvFileLocation = csvFileLocation.replaceAll("\\$EPICSVER", epicsVersion);
          if(! csvFileLocation.endsWith(directorySeperator))
            csvFileLocation += directorySeperator;
          archiveFileLocation = result.getString("ARCH_REQ_FILE_LOC");
          archiveFileLocation = archiveFileLocation.replaceAll("\\$EPICSVER", epicsVersion);
          startupFileLocation = result.getString("STARTUP_FILE_LOC");
          startupFileLocation = startupFileLocation.replaceAll("\\$EPICSVER", epicsVersion);
          pciDBDFileName = result.getString("PCI_DBD_FILE_NM");
          if(! startupFileLocation.endsWith(directorySeperator))
            startupFileLocation += directorySeperator;
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

  /**
   * Called when the cancel button is clicked. This method cancels the current 
   * thread if one has been started, otherwise it closes the dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocatin of this method.
   */
  private void cancelButton_actionPerformed(ActionEvent e)
  {
    if(exporting)
    {
      threadCanceled = true;
      if(templateParser != null)
        templateParser.cancel();
    }
    else
    {
      setVisible(false);
      fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSED);
    }
  }

  /**
   * Enables or disables the OK button. If there are tiems in the list and text 
   * in the file name text box.
   */
  public void enableOKButton()
  {
    if(exporting)
      okButton.setEnabled(false);
    else
      okButton.setEnabled(listModel.size() > 0);
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
   * Sets the data source property. This method loads the EPICS versions into 
   * the combo box using the given <CODE>DataSource</CODE> to connect to the 
   * database.
   * 
   * @param connectionPool The <CODE>DataSource</CODE> used to connect to the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void setDataSource(DataSource connectionPool) throws SQLException
  {
    Connection oracleConnection = connectionPool.getConnection();
    try
    {
      Statement query = oracleConnection.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT MPS_CHAIN.MPS_CHAIN_ID, MPS_CHAIN.MPS_CHAIN_NM, MPS_SGNL_PARAM.DVC_ID, MPS_SGNL_PARAM.APPR_DTE, MPS_SGNL_PARAM.IOC_DVC_ID, MPS_SGNL_PARAM.SERIAL_NBR FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".MPS_CHAIN, ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".MPS_SGNL_PARAM WHERE MPS_CHAIN.MPS_CHAIN_ID = MPS_SGNL_PARAM.MPS_CHAIN_ID ORDER BY MPS_CHAIN.MPS_CHAIN_ID");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          ArrayList chainList = new ArrayList();
          HashMap allChassis = new HashMap();
          MPSChain currentChain = null;
          while(result.next())
          {
            String currentChainID = result.getString("MPS_CHAIN_ID");
            if(currentChain == null || ! currentChain.getID().equals(currentChainID))
            {
              String currentName = result.getString("MPS_CHAIN_NM");
              currentChain = new MPSChain(currentChainID, currentName);
              chainList.add(currentChain);
            }
            MPSBoard currentBoard = new MPSBoard(result.getString("DVC_ID"));
            currentBoard.setApproveDate(result.getDate("APPR_DTE"));
            currentBoard.setSerialNumber(result.getString("SERIAL_NBR"));
            currentChain.addBoard(currentBoard);
            String currentChassisID = result.getString("IOC_DVC_ID");
            MPSChassis currentChassis = (MPSChassis)allChassis.get(currentChassisID);
            if(currentChassis == null)
            {
              currentChassis = new MPSChassis(currentChassisID);
              allChassis.put(currentChassisID, currentChassis);
            }
            currentChassis.addBoard(currentBoard);
          }
          allChains = chainList.toArray();
          allIOCs = allChassis.values().toArray();
          Arrays.sort(allIOCs);
          sql = new StringBuffer("SELECT EPICS_VER FROM ");
          sql.append(MPSBrowserView.SCHEMA);
          sql.append(".EPICS_VER");
          result = query.executeQuery(sql.toString());
          Object currentItem = epicsVersionCombo.getSelectedItem();
          epicsVersionCombo.removeAllItems();
          int position = 0;
          while(result.next())
          {
            String currentVersion = result.getString("EPICS_VER");
            if(currentVersion.equals("R3.13.9"))
              r3_13_9 = position;
            else
              if(currentVersion.equals("R3.14.4"))
                r3_14_4 = position;
            epicsVersionCombo.addItem(currentVersion);
            position++;
          }
          if(currentItem != null)
          {
            int selectedIndex = ((DefaultComboBoxModel)epicsVersionCombo.getModel()).getIndexOf(currentItem);
            if(selectedIndex >= 0)
              epicsVersionCombo.setSelectedIndex(selectedIndex);
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
    super.setDataSource(connectionPool);
    mpsExport.setDataSource(connectionPool);
  }
  
  /**
   * Shows an error message. This method shows a <CODE>JOptionPane</CODE> using 
   * <CODE>SwingUtilities.invokeLater</CODE> to show an error message in a 
   * thread safe manner.
   * 
   * @param message The error message to display.
   * @param title The title for the <CODE>JOptionPane</CODE>.
   */
  private void showError(final String message, final String title)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        JOptionPane.showMessageDialog(MPSExportFrame.this, message, title, JOptionPane.ERROR_MESSAGE);
      }
    });
  }

  /**
   * Sets the instances of <CODE>MPSBoard</CODE> in the list.
   * 
   * @param boards The instances of <CODE>MPSBoard</CODE> to display in the list.
   */
  public void setBoards(MPSBoard[] boards)
  {
    for(int i=0;i<boards.length;i++)
      listModel.addElement(boards[i]);
  }

  /**
   * Gets the image for the print button.
   * 
   * @return The <CODE>Icon</CODE> on the print button.
   */
  public Icon getPrintIcon()
  {
    return printIcon;
  }

  /**
   * Puts an image on the print button.
   * 
   * @param printIcon The <CODE>Icon</CODE> for the print toolbar button.
   */
  public void setPrintIcon(Icon printIcon)
  {
    this.printIcon = printIcon;
  }

  /**
   * Called when the selected item in the EPICS version combo changes. This 
   * method records the new value in the application's properties file.
   * 
   * @param e The <CODE>ItemEvent</CODE> that caused the invocatin of this method.
   */
  private void epicsVersionCombo_itemStateChanged(ItemEvent e)
  {
    if(e.getStateChange() == ItemEvent.SELECTED)
    {
      Properties settings = getApplicationProperties();
      if(settings != null)
        settings.setProperty("MPSExportFrame.EPICSVersion", e.getItem().toString());
    }
  }

  /**
   * Sets the instance of <CODE>Properties</CODE> the application uses to store
   * settings.
   * 
   * @param applicationProperties The instance of <CODE>Properties</CODE> used to store user settings for the application.
   */
  public void setApplicationProperties(Properties applicationProperties)
  {
    super.setApplicationProperties(applicationProperties);
    String lastSelected = applicationProperties.getProperty("MPSExportFrame.EPICSVersion", "");
    if(! lastSelected.equals(""))
      if(getDataSource() == null)
      {
        epicsVersionCombo.addItem(lastSelected);
        epicsVersionCombo.setSelectedItem(lastSelected);
      }
      else
      {
        int index = ((DefaultComboBoxModel)epicsVersionCombo.getModel()).getIndexOf(lastSelected);
        if(index >= 0)
          epicsVersionCombo.setSelectedIndex(index);
      }
  }
}
