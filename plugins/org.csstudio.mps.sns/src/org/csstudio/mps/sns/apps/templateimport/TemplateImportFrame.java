package org.csstudio.mps.sns.apps.templateimport;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.csstudio.mps.sns.tools.data.Template;
import org.csstudio.mps.sns.application.JeriInternalFrame;

/**
 * Provides an interface for importing a template file into the database.
 * 
 * @author Chris Fowlkes
 */
public class TemplateImportFrame extends JeriInternalFrame 
{
  private JPanel outerButtonPanel = new JPanel();
  private BorderLayout outerButtonPanelLayout = new BorderLayout();
  private JPanel innerButtonPanel = new JPanel();
  private GridLayout innerButtonPanelLayout = new GridLayout();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private JPanel statusBarPanel = new JPanel();
  private BorderLayout statusBarPanelLayout = new BorderLayout();
  private JLabel progressLabel = new JLabel();
  private JProgressBar progressBar = new JProgressBar();
  /**
   * Flag used to tell if the import has been canceled.
   */
  private boolean importCanceled = false;
  /**
   * Flag used to tell if the import is ongoing.
   */
  private boolean importing = false;
  private JPanel filePanel = new JPanel();
  private BorderLayout filePanelLayout = new BorderLayout();
  private JLabel fileLabel = new JLabel();
  private JTextField fileField = new JTextField();
  private JButton browseButton = new JButton();
  private JPanel messagePanel = new JPanel();
  private JScrollPane scrollPane = new JScrollPane();
  private BorderLayout messagePanelLayout = new BorderLayout();
  private JPanel centerPanel = new JPanel();
  private BorderLayout centerPanelLayout = new BorderLayout();
  private JList messageList = new JList();
  /**
   * Holds the dialog used to browse for files.
   */
  private JFileChooser fileDialog;
  private DefaultListModel messagesListModel = new DefaultListModel();
  /**
   * Holds the <CODE>TemplateParser</CODE> responsible for the import.
   */
  private TemplateParser fileParser;
  /**
   * Holds the dialog that allows the user to name the templates imported.
   */
  private TemplateImportDialog templateDialog;

  /**
   * Creates a new <CODE>TemplateImportFrame</CODE>.
   */
  public TemplateImportFrame()
  {
    try
    {
      jbInit();
      fileField.getDocument().addDocumentListener(new DocumentListener()
      {
        public void changedUpdate(DocumentEvent e)
        {
          enableOKButton();
        }

        public void insertUpdate(DocumentEvent e)
        {
          enableOKButton();
        }

        public void removeUpdate(DocumentEvent e)
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
   * @throws java.lang.Exception Thrown on initialization error.
   */
  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(400, 300));
    this.setTitle("Template Import");
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    outerButtonPanelLayout.setHgap(5);
    outerButtonPanelLayout.setVgap(5);
    innerButtonPanel.setLayout(innerButtonPanelLayout);
    innerButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    innerButtonPanelLayout.setHgap(5);
    okButton.setText("Import...");
    okButton.setMnemonic('O');
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
    statusBarPanel.setLayout(statusBarPanelLayout);
    progressLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    progressLabel.setText(" ");
    progressBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    filePanel.setLayout(filePanelLayout);
    filePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
    filePanelLayout.setHgap(5);
    fileLabel.setText("File Name(s):");
    fileLabel.setDisplayedMnemonic('N');
    fileLabel.setLabelFor(fileField);
    browseButton.setText("Browse...");
    browseButton.setMnemonic('B');
    browseButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          browseButton_actionPerformed(e);
        }
      });
    messagePanel.setBorder(BorderFactory.createTitledBorder("Messages"));
    messagePanel.setLayout(messagePanelLayout);
    centerPanel.setLayout(centerPanelLayout);
    centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    messageList.setModel(messagesListModel);
    filePanel.add(fileLabel, BorderLayout.WEST);
    filePanel.add(fileField, BorderLayout.CENTER);
    filePanel.add(browseButton, BorderLayout.EAST);
    innerButtonPanel.add(okButton, null);
    innerButtonPanel.add(cancelButton, null);
    statusBarPanel.add(progressLabel, BorderLayout.CENTER);
    statusBarPanel.add(progressBar, BorderLayout.EAST);
    this.getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);
    this.getContentPane().add(filePanel, BorderLayout.NORTH);
    scrollPane.getViewport().add(messageList, null);
    messagePanel.add(scrollPane, BorderLayout.CENTER);
    centerPanel.add(messagePanel, BorderLayout.CENTER);
    this.getContentPane().add(centerPanel, BorderLayout.CENTER);
    outerButtonPanel.add(innerButtonPanel, BorderLayout.EAST);
    outerButtonPanel.add(statusBarPanel, BorderLayout.SOUTH);
  }

  /**
   * Called when the ok button is clicked. This method imports the data from the 
   * files in the files list.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void okButton_actionPerformed(ActionEvent e)
  {
    progressBar.setIndeterminate(true);
    okButton.setEnabled(false);
    cancelButton.setText("Cancel");
    browseButton.setEnabled(false);
    fileField.setEnabled(false);
    messagesListModel.clear();
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    Thread importThread = new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          importing = true;
          //Step 1: parse the files specified by the user.
          ArrayList templateFileList = getFiles();
          File[] templateFiles = new File[templateFileList.size()];
          templateFiles = (File[])templateFileList.toArray(templateFiles);
          if(fileParser == null)
          {
            fileParser = new TemplateParser();
            fileParser.setDataSource(getDataSource());
            fileParser.setProgressBar(progressBar);
            fileParser.setProgressLabel(progressLabel);
          }//if(fileParser == null)
          fileParser.setTemplateFiles(templateFiles);
          fileParser.parse();
          if(importCanceled)
            return;
          if(templateDialog == null)
          {
            templateDialog = new TemplateImportDialog(getMainWindow(), "Imported Templates", true);
            templateDialog.setApplicationProperties(getApplicationProperties());
          }//if(templateDialog == null)
          SwingUtilities.invokeAndWait(new Runnable()
          {
            public void run()
            {
              ArrayList templates = fileParser.getTemplates();
              Template[] templateArray = new Template[templates.size()];
              templateArray = (Template[])templates.toArray(templateArray);
              templateDialog.setTemplates(templateArray);
              templateDialog.center();
              templateDialog.setVisible(true);
            }
          });
          if(templateDialog.getResult() == TemplateImportDialog.CANCEL)
            return;
          final ArrayList templatesInDatabase = fileParser.findTemplatesInDatabase();
          SwingUtilities.invokeAndWait(new Runnable()
          {
            public void run()
            {
              int templateCount = templatesInDatabase.size();
              if(templateCount > 0)
              {
                StringBuffer message = new StringBuffer("<HTML>The following template IDs are already used:<UL>");
                for(int i=0;i<templateCount;i++)
                {
                  message.append("<LI>");
                  message.append(((Template)templatesInDatabase.get(i)).getID());
                }//for(int i=0;i<templateCount;i++)
                message.append("</UL>Do you want to replace them?</HTML>");
                int option = JOptionPane.showConfirmDialog(TemplateImportFrame.this, message, "Confirm Replace", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.NO_OPTION)
                  importCanceled = true;
              }//if(templateCount > 0)
            }
          });
          if(importCanceled)
            return;
          fileParser.saveToDatabase();
          StringBuffer message = new StringBuffer("Imported ");
          int templateCount = fileParser.getTemplates().size();
          message.append(templateCount);
          message.append(" template");
          if(templateCount != 1)
            message.append("s");
          message.append(".");
          showMessage(message.toString(), "Import Success", JOptionPane.INFORMATION_MESSAGE);
        }//try
        catch(java.lang.reflect.InvocationTargetException ex)
        {
          ex.printStackTrace();
          showMessage(ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
        }//catch(java.lang.reflect.InvocationTargetException ex)
        catch(java.lang.InterruptedException ex)
        {
          ex.printStackTrace();
          showMessage(ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
        }//catch(java.lang.reflect.InvocationTargetException ex)
        catch(java.sql.SQLException ex)
        {
          ex.printStackTrace();
          showMessage(ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }//catch(java.lang.reflect.InvocationTargetException ex)
        finally
        {
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              okButton.setEnabled(true);
              cancelButton.setText("Close");
              browseButton.setEnabled(true);
              fileField.setEnabled(true);
              progressBar.setValue(0);
              progressLabel.setText(" ");
              progressBar.setIndeterminate(false);
              setCursor(Cursor.getDefaultCursor());
            }
          });
          importing = false;
        }//finally
      }
    });
    importThread.start();
  }

  /**
   * Called when the cancel button is clicked.If an import is going on, this 
   * method cancels that import, if not, this method closes the window without 
   * doing the import.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void cancelButton_actionPerformed(ActionEvent e)
  {
    if(importing)
    {
      importCanceled = true;
      fileParser.cancel();
    }//if(importing)
    else
    {
      setVisible(false);
      fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSED);
    }//else
  }

  /**
   * Called when the browse button is clicked. This method shows the open file 
   * dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void browseButton_actionPerformed(ActionEvent e)
  {
    Properties settings = getApplicationProperties();
    if(fileDialog == null)
    {
      fileDialog = new JFileChooser();
      fileDialog.setMultiSelectionEnabled(true);
      fileDialog.addChoosableFileFilter(new javax.swing.filechooser.FileFilter()
      {
        public boolean accept(File chosenFile)
        {
          if(chosenFile.isDirectory())
            return true;
          else
          {
            String fileName = chosenFile.getName().toLowerCase();
            if(fileName.endsWith(".template") || fileName.endsWith(".vdb"))
              return true;
            else
              return false;
          }//else
        }

        public String getDescription()
        {
          return "Template Files (*.template, *.vdb)";
        }
      });
      String directory = settings.getProperty("TemplateImportFrame.fileDirectory");
      if(directory != null)
        fileDialog.setCurrentDirectory(new File(directory));
    }//if(fileDialog == null)
    if(fileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
    {
      openFiles(fileDialog.getSelectedFiles());
      String path = fileDialog.getCurrentDirectory().getAbsolutePath();
      settings.setProperty("TemplateImportFrame.fileDirectory", path);
    }//if(fileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
  }

  /**
   * Adds the given instances of <CODE>File</CODE> to the file text box.
   * 
   * @param selectedFiles The instances of <CODE>File</CODE> to add to the list to parse.
   */
  private void openFiles(File[] selectedFiles)
  {
    StringBuffer fileNames = new StringBuffer(fileField.getText());
    for(int i=0;i<selectedFiles.length;i++)
    {
      String currentFileName = selectedFiles[i].getAbsolutePath().trim();
      if(fileNames.indexOf(currentFileName) < 0)
      {
        if(fileNames.length() > 0)
          fileNames.append("; ");
        fileNames.append(currentFileName);
      }//if(fileNames.indexOf(currentFileName) >= 0)
    }//for(int i=0;i<selectedFiles.length;i++)
    fileField.setText(fileNames.toString());
  }

  /**
   * Gets the instances of <CODE>File</CODE> that represent the files named in 
   * the file text box.
   * 
   * @return An <CODE>ArrayList</CODE> containing the instancess of <CODE>File</CODE> for the files listed.
   */
  private ArrayList getFiles()
  {
    StringTokenizer fileNames = new StringTokenizer(fileField.getText(), ";");
    ArrayList files = new ArrayList();
    while(fileNames.hasMoreTokens())
    {
      String currentFileName = fileNames.nextToken().trim();
      if(! currentFileName.equals(""))
        files.add(new File(currentFileName));
    }//while(fileNames.hasMoreTokens())
    return files;
  }

  /**
   * Enables or disables the ok button. If the file field contains anything, the
   * ok button is enabled. If it is empty the ok button is disabled.
   */
  private void enableOKButton()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        boolean enable = fileField.getText().length() > 0;
        okButton.setEnabled(enable);
      }
    });
  }
}