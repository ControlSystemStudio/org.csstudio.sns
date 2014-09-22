package org.csstudio.mps.sns;
import org.csstudio.mps.sns.application.Application;
import org.csstudio.mps.sns.application.ApplicationAdaptor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.*;
import javax.sql.*;
import java.util.*;
import java.io.*;
import org.csstudio.mps.sns.MainFrame;
import org.csstudio.mps.sns.application.XalDocument;
import java.net.URL;

/**
 * Provides the main executeable class for the application.
 * 
 * @author Chris Fowlkes
 */
public class Main extends ApplicationAdaptor
{
  /**
   * Provides a schema name to qualify queries with.
   */
  //public static final String SCHEMA = "EPICS";
  
  /**
   * Creates a new instance of <CODE>Main</CODE>. This method initializes the 
   * application, gives the user a login dialog which on successful login, 
   * creates and shows the main interface of the application.
   */
  public Main()
  {
    try 
    {
      UIManager.installLookAndFeel("GTK", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
    }//try
    catch(Exception e) 
    {//GTK not available, do nothing.
    }//catch(Exception e)
    try 
    {
      //Let there be sound...
      UIManager.put("AuditoryCues.playList", UIManager.get("AuditoryCues.allAuditoryCues"));
    }//try
    catch(Exception e) 
    {//Sound not available, do nothing.
    }//catch(Exception e)
  }

  /**
   * Starts the application.
   * 
   * @param args Not used.

  public static void main(String[] args)
  {
    Application.launch(new Main());
  }
   */
  /**
   * Compares two objects with the <CODE>equals</CODE> method. This method also 
   * considers two <CODE>null</CODE> objects equal as well.
   * 
   * @param object1 The first <CODE>Object</CODE> to compare.
   * @param object2 The <CODE>Object</CODE> to compare to.
   * @return <CODE>true</CODE> if the two items are equal.

  public static boolean compare(Object object1, Object object2)
  {
    //Sometimes if a null is passed in as a string, we end up here...
    if((object1 == null || object2 == null) && (object1 instanceof String || object2 instanceof String))
      return compare((String)object1, (String)object2);
    if(object1 == null)
    {
      if(object2 != null)
        return false;
    }
    else
      if(! object1.equals(object2))
        return false;
    return true;
  }
   */
  /**
   * Compares two instances of <CODE>String</CODE>. This method considers 
   * <CODE>null</CODE> and empty string to be equal and does not consider 
   * leading or trailing whitespace.
   * 
   * @param string1 The first <CODE>String</CODE> to compare.
   * @param string2 The <CODE>String</CODE> to compare to.
   * @return <CODE>true</CODE> if the two items are equal.

  public static boolean compare(String string1, String string2)
  {
    if(string1 == null)
      string1 = "";
    if(string2 == null)
      string2 = "";
    return string1.trim().equals(string2.trim());
  }
     */
  /**
   * Returns <CODE>true</CODE> if the given exception was caused by a closed
   * connection.
   * 
   * @param ex The <CODE>SQLException</CODE> to check.
   * @return <CODE>true</CODE> if the connection has closed, <CODE>false</CODE> otherwise.

  public static boolean checkConnection(SQLException ex)
  {
    int errorCode = ex.getErrorCode();
    return errorCode == 2396 || errorCode == 1012;
  }
   */
  /**
   * Reurns the application name (Jeri) for the title bar.
   * 
   * @return The name of the application.
   */
  public String applicationName()
  {
    return "MPS via CSS";
  }

  /**
   * This method is not supported by this class.
   * 
   * @return This method always returns <CODE>null</CODE>.
   */
  public XalDocument newDocument(URL url)
  {
    return null;
  }

  /**
   * Creates and returns a new <CODE>JeriDocument</CODE>.
   * 
   * @return An instance <CODE>JeriDocument</CODE>.
   */
  public XalDocument newEmptyDocument()
  {
    return new JeriDocument();
  }

  /**
   * This class does not read any file types, so this method returns an empty 
   * array.
   * 
   * @return Always returns an empty array.
   */
  public String[] readableDocumentTypes()
  {
    return new String[0];
  }

  /**
   * This class does not write any file types, so this method returns an empty
   * array.
   * 
   * @return Always returns <CODE>null</CODE>.
   */
  public String[] writableDocumentTypes()
  {
    return new String[0];
  }
}