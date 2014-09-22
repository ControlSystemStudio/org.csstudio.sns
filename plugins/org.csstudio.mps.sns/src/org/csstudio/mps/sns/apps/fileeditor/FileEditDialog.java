package org.csstudio.mps.sns.apps.fileeditor;
import org.csstudio.mps.sns.application.JeriDialog;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.print.Pageable;
import java.awt.print.Printable;

/**
 * Provides an interface for editing or printing text.
 * 
 * @author Chris Fowlkes
 */
public class FileEditDialog extends JeriDialog implements Pageable, Printable 
{
  private JToolBar toolBar = new JToolBar();
  private JScrollPane scrollPane = new JScrollPane();
  private JButton printButton = new JButton();
  private JTextArea textArea = new JTextArea();
  /**
   * Holds the paper orientation for print jobs. This is by the user via the 
   * print dialog.
   */
  private OrientationRequested orientation = OrientationRequested.PORTRAIT;
  private int linesPerPage = 60;
  private JPanel buttonPanel = new JPanel();
  private JButton closeButton = new JButton();
  private BorderLayout buttonPanelLayout = new BorderLayout();

  /**
   * Creates a new <CODE>FileEditDialog</CODE>.
   */
  public FileEditDialog()
  {
    this(null, "", false);
  }

  /**
   * Creates a new <CODE>FileEditDialog</CODE>.
   * 
   * @param parent The parent window for the dialog.
   * @param title The title that will appear in the title bar of the dialog.
   * @param modal Pass as <CODE>true</CODE> to make the dialog modal, <CODE>false</CODE> otherwise.
   */
  public FileEditDialog(Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

  }

  /**
   * Component initialization.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  private void jbInit() throws Exception
  {
    this.setSize(450, 350);
    printButton.setMargin(new Insets(2, 2, 2, 2));
    printButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          printButton_actionPerformed(e);
        }
      });
    buttonPanel.setLayout(buttonPanelLayout);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
    closeButton.setText("Close");
    closeButton.setMnemonic('C');
    closeButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          closeButton_actionPerformed(e);
        }
      });
    toolBar.add(printButton, null);
    this.getContentPane().add(toolBar, BorderLayout.NORTH);
    scrollPane.getViewport().add(textArea, null);
    this.getContentPane().add(scrollPane, BorderLayout.CENTER);
    buttonPanel.add(closeButton, BorderLayout.EAST);
    this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Sets the text for the window.
   * 
   * @param newText The text for the window.
   */
  public void setText(String newText)
  {
    textArea.setText(newText);
  }

  /**
   * Gets the text that appears in the window.
   * 
   * @return The text from the <CODE>TextArea</CODE> in the window.
   */
  public String getText()
  {
    return textArea.getText();
  }

  /**
   * Makes the text editable or read only. <CODE>true</CODE> (editable) by 
   * default.
   * 
   * @param newEditable Pass as <CODE>false</CODE> to make the text read only, <CODE>true</CODE> to make it editable.
   */
  public void setEditable(boolean newEditable)
  {
    textArea.setEditable(newEditable);
  }

  /**
   * Determines if the text is editable.
   * 
   * @return <CODE>true</CODE> if the text is editable, <CODE>false if it is not editable.
   */
  public boolean isEditable()
  {
    return textArea.isEditable();
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
   * Called when the print button is clicked.
   * 
   * @param e The <CODE>ActinoEvent</CODE> that caused the invocation of this method.
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
   * Calculates the number of pages required to print the report. This method 
   * calculates the number of pages based on the number of lines per page.
   * 
   * @return The number of pages required to print the report.
   */
  public int getNumberOfPages()
  {
    int lineCount = textArea.getLineCount();
    int pageCount = lineCount / linesPerPage;
    if(lineCount % linesPerPage > 0)
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
   * returns the <CODE>Printable</CODE> instance for the given page.
   * 
   * @param pageIndex The index of the page for which to return the <CODE>Printable</CODE>.
   * @return The <CODE>Printable</CODE> for the given page.
   */
  public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException
  {
    return this;
  }

  /**
   * This method prints a page.
   * 
   * @param graphics The instance of <CODE>Graphics</CODE> to print to.
   * @param pageFormat The <CODE>PageFormat</CODE> for the page.
   * @param pageIndex The index of the page to print.
   * @throws PrinterException Thrown on error.
   */
  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException
  {
    if(pageIndex >= getNumberOfPages())
      return Printable.NO_SUCH_PAGE;
    int lineCount = textArea.getLineCount();
    java.awt.Graphics2D graphics2 = (java.awt.Graphics2D)graphics;
    if(lineCount > 0)
    {
      Element documentRoot = textArea.getDocument().getDefaultRootElement();
      int firstLineLocation = documentRoot.getElement(0).getStartOffset();
      try
      {
        Rectangle firstLineRectangle = textArea.modelToView(firstLineLocation);
        graphics2.translate(pageFormat.getImageableX(), pageFormat.getImageableY() - pageIndex * pageFormat.getImageableHeight());
        double scaleBy = pageFormat.getImageableHeight() / (firstLineRectangle.height * linesPerPage);
        if(scaleBy < 1)
          graphics2.scale(scaleBy, scaleBy);
      }//try
      catch(javax.swing.text.BadLocationException ex)
      {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    textArea.paint(graphics2);
    return java.awt.print.Printable.PAGE_EXISTS;
  }

  /**
   * Called when the close button is clicked.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void closeButton_actionPerformed(ActionEvent e)
  {
    setVisible(false);
  }
}