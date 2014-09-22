package org.csstudio.mps.sns.apps.mpsfileeditor;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import java.io.*;

import java.util.*;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import org.csstudio.mps.sns.application.JeriDialog;

/**
 * Provides an interface for displaying files to the user. The user can then 
 * print the files, or if the editable property is set to <CODE>true</CODE> the 
 * user can edit the files.
 * 
 * @author Chris Fowlkes
 */
public class MPSFileEditDialog extends JeriDialog implements Printable, Pageable 
{
  private BorderLayout dialogLayout = new BorderLayout();
  private JTabbedPane tabbedPane = new JTabbedPane();
  private JPanel mainPanel = new JPanel();
  private BorderLayout mainPanelLayout = new BorderLayout();
  private JPanel outerButtonPanel = new JPanel();
  private JPanel innerButtonPanel = new JPanel();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private BorderLayout outerButtonPanelLayout = new BorderLayout();
  private GridLayout innerButtonPanelLayout = new GridLayout();
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
   * Holds the dialog used to select the file to write to.
   */
//  private JFileChooser fileSaveDialog;
  private HashMap textPanes = new HashMap();
//  private File saveDirectory;
  private JToolBar toolBar = new JToolBar();
  private JButton printButton = new JButton();
  private JPanel radioButtonPanel = new JPanel();
  private JRadioButton exportAllRadioButton = new JRadioButton();
  private JRadioButton exportSelectedTabRadioButton = new JRadioButton();
  private ButtonGroup radioButtonGroup = new ButtonGroup();
  /**
   * Holds the paper orientation for print jobs. This is by the user via the 
   * print dialog.
   */
  private OrientationRequested orientation = OrientationRequested.PORTRAIT;
  /**
   * Holds the variable that determines if the text in the dialogs is editable
   * or not.
   */
  private boolean editable = true;
  
  /**
   * Creates a new <CODE>MPSFileEditDialog</CODE>.
   */
  public MPSFileEditDialog()
  {
    this(null, "", false);
  }

  /**
   * Creates a new <CODE>MPSFileEditDialog</CODE>.
   * 
   * @param parent The parent for the dialog.
   * @param title The text to appear in the title bar of the dialog.
   * @param modal Pass as <CODE>true</CODE> for a modal dialog, <CODE>false</CODE> for a non-modal dialog.
   */
  public MPSFileEditDialog(Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
    try
    {
      jbInit();
      radioButtonGroup.add(exportAllRadioButton);
      radioButtonGroup.add(exportSelectedTabRadioButton);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

  }

  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(400, 300));
    this.getContentPane().setLayout(dialogLayout);
    this.addWindowListener(new java.awt.event.WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          this_windowClosing(e);
        }
      });
    dialogLayout.setVgap(5);
    mainPanel.setLayout(mainPanelLayout);
    mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
    mainPanelLayout.setVgap(5);
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    outerButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    innerButtonPanel.setLayout(innerButtonPanelLayout);
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
    innerButtonPanelLayout.setHgap(5);
    printButton.setToolTipText("Print Selected File");
    printButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          printButton_actionPerformed(e);
        }
      });
    exportAllRadioButton.setText("Export All");
    exportAllRadioButton.setSelected(true);
    exportAllRadioButton.setMnemonic('A');
    exportSelectedTabRadioButton.setText("Export Selected Tab");
    exportSelectedTabRadioButton.setMnemonic('S');
    radioButtonPanel.add(exportAllRadioButton, null);
    radioButtonPanel.add(exportSelectedTabRadioButton, null);
    mainPanel.add(tabbedPane, BorderLayout.CENTER);
    innerButtonPanel.add(okButton, null);
    innerButtonPanel.add(cancelButton, null);
    outerButtonPanel.add(innerButtonPanel, BorderLayout.EAST);
    outerButtonPanel.add(radioButtonPanel, BorderLayout.CENTER);
    mainPanel.add(outerButtonPanel, BorderLayout.SOUTH);
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    toolBar.add(printButton, null);
    this.getContentPane().add(toolBar, BorderLayout.NORTH);
  }

  /**
   * Sets the files for the dialog to display.
   * 
   * @param files The files to display.
   */
  public void setFiles(HashMap files)
  {
    tabbedPane.removeAll();
    textPanes.clear();
    Iterator fileNames = files.keySet().iterator();
    while(fileNames.hasNext())
    {
      Object currentFileName = fileNames.next();
      String currentFileContents = files.get(currentFileName).toString();
      JTextArea currentTextArea = new JTextArea(currentFileContents);
      currentTextArea.setEditable(isEditable());
      JScrollPane currentScrollPane = new JScrollPane();
      currentScrollPane.getViewport().add(currentTextArea);
      tabbedPane.addTab(currentFileName.toString(), currentScrollPane);
      textPanes.put(currentFileName, currentTextArea);
    }//while(fileNames.hasNext())
  }

  /**
   * Gets the files to export. The <CODE>HashMap</CODE> returned will have the 
   * file names as keys and the current value of the correspnding text areas for 
   * values.
   * 
   * @return The files selected for export.
   */
  public HashMap getFiles()
  {
    int fileCount = tabbedPane.getTabCount();
    HashMap files = new HashMap();
    if(exportSelectedTabRadioButton.isSelected())
    {
      String currentFileName = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
      JTextArea currentTextArea = (JTextArea)textPanes.get(currentFileName);
      String fileContents = currentTextArea.getText();
      files.put(currentFileName, fileContents);
    }//if(exportSelectedTabRadioButton.isSelected())
    else
      for(int i=0;i<fileCount;i++)
      {
        String currentFileName = tabbedPane.getTitleAt(i);
        JTextArea currentTextArea = (JTextArea)textPanes.get(currentFileName);
        String fileContents = currentTextArea.getText();
        files.put(currentFileName, fileContents);
      }//for(int i=0;i<fileCount;i++)
    return files;
  }

//  public File getSaveDirectory()
//  {
//    return saveDirectory;
//  }
  
  private void cancelButton_actionPerformed(ActionEvent e)
  {
    result = CANCEL;
    setVisible(false);
  }

  /**
   * Gets the button clicked to exit the dialog. This method will return 
   * <CODE>OK</CODE> if the ok button was clicked, otherwise it will return 
   * <CODE>CANCEL</CODE>.
   * 
   * @return <CODE>OK</CODE> if the ok button was clicked to close the dialog, <CODE>CANCEL</CODE> otherwise.
   */
  public int getResult()
  {
    return result;
  }

  private void okButton_actionPerformed(ActionEvent e)
  {
//    Properties settings = getApplicationProperties();
    result = OK;
    setVisible(false);
//    if(fileSaveDialog == null)
//    {
//      fileSaveDialog = new JFileChooser();
//      fileSaveDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//      String directory = settings.getProperty("MPSExportFrame.fileDirectory");
//      if(directory != null)
//        fileSaveDialog.setCurrentDirectory(new File(directory));
//    }//if(fileSaveDialog == null)
//    if(fileSaveDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
//    {
//      saveDirectory = fileSaveDialog.getSelectedFile();
//      if(saveDirectory != null && validateFile(saveDirectory))
//      {
//        String path = saveDirectory.getAbsolutePath();
//        settings.setProperty("MPSExportFrame.fileDirectory", path);
//        result = OK;
//        setVisible(false);
//      }//if(saveDirectory != null && validateFile(saveDirectory))
//    }//if(fileSaveDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
  }
  
//  /**
//   * Validates the <CODE>File</CODE> given. This method determines the 
//   * <CODE>File</CODE> that the data should be exported to. This is done by 
//   * validating the given directory (it should either contain or be a directory 
//   * named db) and creating it if it does not exist. A <CODE>File</CODE> is then 
//   * created using the directory and the file name in the file name text box. If
//   * this file exists, the user is prompted to overwrite. If the user does not
//   * want to overwrite, or there is another problem with the file selected, 
//   * <CODE>null</CODE> is returned.
//   * 
//   * @param selectedDirectory The directory selected by the user to eport the file to.
//   * @return <CODE>false</CODE> if the user canceled the operation, <CODE>true</CODE> if the given directory checked out.
//   */
//  private boolean validateFile(File selectedDirectory)
//  {
//    File[] requiredFiles;
//    if(exportAllRadioButton.isSelected())
//    {
//      requiredFiles = new File[tabbedPane.getTabCount()];
//      for(int i=0;i<requiredFiles.length;i++)
//        requiredFiles[i] = new File(selectedDirectory, tabbedPane.getTitleAt(i));
//    }//if(exportAllRadioButton.isSelected())
//    else
//    {
//      String selectedTabTitle = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
//      requiredFiles = new File[]{new File(selectedDirectory, selectedTabTitle)};
//    }//else
//    ArrayList directoriesToCreate = new ArrayList();
//    ArrayList filesThatExist = new ArrayList();
//    for(int i=0;i<requiredFiles.length;i++)
//    {
//      File directory = requiredFiles[i].getParentFile();
//      if(! directory.exists())
//      {
//        if(! directoriesToCreate.contains(directory))
//          directoriesToCreate.add(directory);
//      }//if(! directory.exists())
//      else
//        if(requiredFiles[i].exists())
//          filesThatExist.add(requiredFiles[i]);
//    }//for(int i=0;i<requiredFiles.length;i++)
//    int fileCount = filesThatExist.size();
//    int response = JOptionPane.YES_OPTION;
//    if(fileCount > 0)
//    {
//      StringBuffer message = new StringBuffer("<HTML>The following files already exist:<UL>");
//      for(int i=0;i<fileCount;i++)
//      {
//        message.append("<LI>");
//        message.append(filesThatExist.get(i));
//      }//for(int i=0;i<fileCount;i++)
//      message.append("</UL>Do you want to overwrite them?</HTML>");
//      response = JOptionPane.showConfirmDialog(this, message.toString(), "Overwrite Existing Files", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//    }//if(fileCount > 0)
//    fileCount = directoriesToCreate.size();
//    if(response == JOptionPane.YES_OPTION && fileCount > 0)
//    {
//      StringBuffer message = new StringBuffer("<HTML>The following directories do not exist:<UL>");
//      for(int i=0;i<fileCount;i++)
//      {
//        message.append("<LI>");
//        message.append(directoriesToCreate.get(i));
//      }//for(int i=0;i<fileCount;i++)
//      message.append("</UL>Do you want to create them?</HTML>");
//      response = JOptionPane.showConfirmDialog(this, message.toString(), "Create Directories", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//    }//if(response == JOptionPane.YES_OPTION && fileCount > 0)
//    if(response == JOptionPane.YES_OPTION)
//      for(int i=0;i<fileCount;i++)
//        ((File)directoriesToCreate.get(i)).mkdirs();
//    else
//      JOptionPane.showMessageDialog(this, "Export not completed.", "Export Error", JOptionPane.WARNING_MESSAGE);
//    return response == JOptionPane.YES_OPTION;
//  }

  private void this_windowClosing(WindowEvent e)
  {
    result = CANCEL;
    setVisible(false);
  }

  /**
   * Called when the print button is clicked. This method start the process of
   * printing the current tab.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void printButton_actionPerformed(ActionEvent e)
  {
    PrintRequestAttributeSet printAttributes = new HashPrintRequestAttributeSet();
    //At this time none of the plain text flavors are implemented, so we'll have
    //to do this the old "printable" and "pageable" way.
    DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, printAttributes);
    PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
    PrintService service = ServiceUI.printDialog(null, 200, 200, printService, defaultService, flavor, printAttributes);
    if(service != null) 
    {
      orientation = (OrientationRequested)printAttributes.get(OrientationRequested.class);
      if(orientation == null)
        orientation = OrientationRequested.PORTRAIT;
      DocPrintJob job = service.createPrintJob();
      DocAttributeSet documentAttributes = new HashDocAttributeSet();
      documentAttributes.addAll(printAttributes);
      Doc doc = new SimpleDoc(this, flavor, documentAttributes);
      try
      {
        job.print(doc, printAttributes);
      }//try
      catch(javax.print.PrintException ex)
      {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
      }//catch(javax.print.PrintException ex)
    }//if(service != null) 
  }

  /**
   * This method prints a page.
   * 
   * @param graphics The instance of <CODE>Graphics</CODE> to print to.
   * @param pageFormat The <CODE>PageFormat</CODE> for the page.
   * @param pageIndex The index of the page to print.
   * @return <CODE>Printable.PAGE_EXISTS</CODE> if the given page index is valid, <CODE>Printable.NO_SUCH_PAGE</CODE> if the given page index is invalid.
   * @throws PrinterException Thrown on error.
   */
  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException
  {
    if(pageIndex >= getNumberOfPages())
      return Printable.NO_SUCH_PAGE;
    String currentFileName = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
    JTextArea currentTextArea = (JTextArea)textPanes.get(currentFileName);
    int lineCount = currentTextArea.getLineCount();
    java.awt.Graphics2D graphics2 = (java.awt.Graphics2D)graphics;
    if(lineCount > 0)
    {
      Element documentRoot = currentTextArea.getDocument().getDefaultRootElement();
      int firstLineLocation = documentRoot.getElement(0).getStartOffset();
      try
      {
        Rectangle firstLineRectangle = currentTextArea.modelToView(firstLineLocation);
        graphics2.translate(pageFormat.getImageableX(), pageFormat.getImageableY() - pageIndex * pageFormat.getImageableHeight());
        double scaleBy = pageFormat.getImageableHeight() / (firstLineRectangle.height * 60);
        if(scaleBy < 1)
          graphics2.scale(scaleBy, scaleBy);
      }//try
      catch(javax.swing.text.BadLocationException ex)
      {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    currentTextArea.paint(graphics2);
    return java.awt.print.Printable.PAGE_EXISTS;
  }

  /**
   * Gets the number of pages for the document to be printed. Each page printed 
   * contains 60 lines, so this method determines how many pages are needed by
   * dividing the line count of the selected text area by 60.
   * 
   * @return The number of pages required to print the selected text.
   */
  public int getNumberOfPages()
  {
    //Let's print 60 lines per page...
    String currentFileName = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
    JTextArea currentTextArea = (JTextArea)textPanes.get(currentFileName);
    int lineCount = currentTextArea.getLineCount();
    int pageCount = lineCount / 60;
    if(lineCount % 60 > 0)
      pageCount++;
    return pageCount;
  }

  /**
   * Returns the <CODE>PageFormat</CODE> for the print job.
   * 
   * @param pageIndex The page for which to return the <CODE>PageFormat</CODE>.
   * @return The <CODE>PageFormat</CODE> for the given page.
   */
  public PageFormat getPageFormat(int pageIndex)
  {
    PageFormat format = new PageFormat();
    if(orientation.equals(OrientationRequested.LANDSCAPE))
      format.setOrientation(PageFormat.LANDSCAPE);
    else
      format.setOrientation(PageFormat.PORTRAIT);
    return format;
  }

  /**
   * Gets the <CODE>Printable</CODE> for the given page.
   * 
   * @param pageIndex The index of the page for which to return the <CODE>Printable</CODE>.
   * @return The <CODE>Printable</CODE> for the given page.
   */
  public Printable getPrintable(int pageIndex)
  {
    return this;
  }

  /**
   * Puts an image on the print button.
   * 
   * @param printIcon The <CODE>Icon</CODE> for the print toolbar button.
   */
  public void setPrintIcon(Icon printIcon)
  {
    printButton.setIcon(printIcon);
  }

  /**
   * Makes the text in the dialog editable or read only. By default they are 
   * editable.
   * 
   * @param newEditable Pass <CODE>true</CODE> to make the tables editable, <CODE>false</CODE> to make them read only.
   */
  public void setEditable(boolean newEditable)
  {
    editable = newEditable;
    //Make sure existing text panes have the correct value for the editable property.
    int fileCount = tabbedPane.getTabCount();
    boolean editable = isEditable();
    for(int i=0;i<fileCount;i++)
    {
      String currentFileName = tabbedPane.getTitleAt(i);
      JTextArea currentTextArea = (JTextArea)textPanes.get(currentFileName);
      currentTextArea.setEditable(editable);
    }//for(int i=0;i<fileCount;i++)
  }

  /**
   * Determines if the text is editable or not.
   * 
   * @return <CODE>true</CODE> if the text is editable, <CODE>false</CODE> if they are read only.
   */
  public boolean isEditable()
  {
    return editable;
  }
  
  /**
   * Shows or hides the export options panel. The panel is visible by default.
   * 
   * @param newExportOptionsVisible Pass as <CODE>false</CODE> to hide the export options.
   */
  public void setExportOptionsVisible(boolean newExportOptionsVisible)
  {
    radioButtonPanel.setVisible(newExportOptionsVisible);
  }

  /**
   * Returns <CODE>true</CODE> if the export options are visible.
   * 
   * @return <CODE>true</CODE> if the export options are visible, <CODE>false</CODE> otherwise.
   */
  public boolean isExportOptionsvisible()
  {
    return radioButtonPanel.isVisible();
  }
  
  /**
   * Shows or hides the OK button. The button is visible by default. Hiding the 
   * button will change the text on the cancel button to 'Close'.
   * 
   * @param newOKButtonVisible Pass as <CODE>false</CODE> to hide the OK button and change the text on the cancel button to 'Close'.
   */
  public void setOKButtonVisible(boolean newOKButtonVisible)
  {
    okButton.setVisible(newOKButtonVisible);
    if(newOKButtonVisible)
      cancelButton.setText("Cancel");
    else
      cancelButton.setText("Close");
  }
  
  /**
   * Returns <CODE>true</CODE> if the OK button is visible.
   * 
   * @return <CODE>true</CODE> if the OK button is visible, <CODE>false</CODE> otherwise.
   */
  public boolean isOKButtonVisible()
  {
    return okButton.isVisible();
  }
}