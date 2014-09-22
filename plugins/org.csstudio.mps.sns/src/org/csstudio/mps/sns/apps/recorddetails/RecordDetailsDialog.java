package org.csstudio.mps.sns.apps.recorddetails;
import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import org.csstudio.mps.sns.application.JeriDialog;

/**
 * Holds the interface used to display a single record in a form type display.
 * 
 * @author Chris Fowlkes
 */
public class RecordDetailsDialog extends JeriDialog
{
  private JScrollPane scrollPane = new JScrollPane();
  private RecordDetailsPanel mainPanel = new RecordDetailsPanel();
  private JPanel outerButtonPanel = new JPanel();
  private BorderLayout outerButtonPanelLayout = new BorderLayout();
  private JButton closeButton = new JButton();
  private JToolBar toolBar = new JToolBar();

  /**
   * Creates a new <CODE>RecordDetailsDialog</CODE>.
   */
  public RecordDetailsDialog()
  {
    this(null, "", false);
  }

  /**
   * Creates a new <CODE>RecordDetailsDialog</CODE>.
   * 
   * @param parent The parent window to the dialog.
   * @param title The title of the dialog that is to appear in the title bar.
   * @param modal Pass <CODE>true</CODE> for a modal dialog.
   */
  public RecordDetailsDialog(Frame parent, String title, boolean modal)
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
   * @throws java.lang.Exception Thrown on initialization error.
   */
  private void jbInit() throws Exception
  {
    this.setSize(new Dimension(400, 300));
    outerButtonPanel.setLayout(outerButtonPanelLayout);
    outerButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
    closeButton.setText("Close");
    closeButton.setMnemonic('C');
    closeButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          closeButton_actionPerformed(e);
        }
      });
    scrollPane.getViewport().add(mainPanel, null);
    this.getContentPane().add(scrollPane, BorderLayout.CENTER);
    outerButtonPanel.add(closeButton, BorderLayout.EAST);
    this.getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);
    this.getContentPane().add(toolBar, BorderLayout.NORTH);
  }

  /**
   * Returns the editor at the given index.
   * 
   * @param i The index of the editor to return.
   * @return The editor at the given index.
   */
  public Component editorAt(int i)
  {
    return mainPanel.editorAt(i);
  }

  /**
   * Called when the close button is clicked. This method closes the dialog.
   * 
   * @param e The <CODE>ActionEvent</CODE> that caused the invocation of this method.
   */
  private void closeButton_actionPerformed(ActionEvent e)
  {
    setVisible(false);
  }

  /**
   * Sets the <CODE>JTable</CODE> used to display the data in the main 
   * interface.
   * 
   * @param newTable The <CODE>JTable</CODE> used to display the data in the main interface.
   */
  public void setTable(JTable newTable)
  {
    mainPanel.setTable(newTable);
  }

  /**
   * Sets display names for the fields in the dialog box. This method should not
   * be called until after the <CODE>setTable</CODE> method. Doing so will 
   * result in an exception.
   * 
   * @param displayNames The user friendly names for the fields in the dialog.
   */
  public void setDisplayNames(HashMap displayNames)
  {
    mainPanel.setDisplayNames(displayNames);
  }

  /**
   * Adds a button to the toolbar.
   * 
   * @param newButton The new <CODE>JButton</CODE> to add to the toolbar.
   */
  public void addToolBarButton(JButton newButton)
  {
    toolBar.add(newButton);
  }
}