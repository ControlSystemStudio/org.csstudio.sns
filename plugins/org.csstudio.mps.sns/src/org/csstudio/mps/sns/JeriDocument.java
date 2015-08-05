package org.csstudio.mps.sns;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.csstudio.mps.sns.application.XalDocument;
import org.csstudio.mps.sns.tools.database.CachingDatabaseAdaptor;
import org.csstudio.mps.sns.tools.database.LoginDialog;
import org.csstudio.mps.sns.tools.database.OracleCachingDatabaseAdaptor;
import org.csstudio.mps.sns.view.MPSBrowserView;

import oracle.jdbc.pool.OracleDataSource;
import oracle.sql.CLOB;

/**
 * Provides a document for the Jeri application. This holds the connection to
 * the database and the link to the properties file that stores the user's
 * settings.
 *
 * @author Chris Fowlkes
 */
public class JeriDocument extends XalDocument
{
  /**
   * Holds the name of the file used to store the application settings for the
   * user.
   */
  private String propertyFileName;
  /**
   * Holds the instance of <CODE>Properties</CODE> used to store the user's
   * settings.
   */
  private Properties applicationProperties;
  /**
   * Holds the title for the document.
   */
  private String title = "";
  /**
   * Holds the <CODE>DataSource</CODE> used by the application to connect to the
   * database.
   */
  private CachingDatabaseAdaptor dataSource;
  /**
   * A flag that determines if an insert or update statement is used to save the
   * property file to the database.
   */
  private boolean propertyFileInDatabase;
  /**
   * Holds the roles the user has in the database.
   */
  private String[] roles;

  /**
   * Creates a new <CODE>JeriDocument</CODE>.
   */
  public JeriDocument()
  {
    loadLocalProperties();
  }

  /**
   * Gets the name of the property file used by the application to save the
   * user's settings.
   *
   * @return The nae of the property file used to store aplication settings for the user.
   */
  public String getPropertyFileName()
  {
    return propertyFileName;
  }

  /**
   * Gets the instance of <CODE>Properties</CODE> used to store the application
   * settings for the user.
   *
   * @return The instance of <CODE>Properties</CODE> containing the user's applicatin settings.
   */
  public Properties getApplicationProperties()
  {
    return applicationProperties;
  }

  /**
   * Subclasses must implement this method to make their custom main window.
   */
  @Override
protected void makeMainWindow()
  {
    LoginDialog login = new LoginDialog((JFrame)null, "MainWindow Login", true);
    int loginHeight = login.getHeight();
    int loginWidth = 300;
    login.setSize(loginWidth, loginHeight);
    login.center();
    //Need to setup the connection details...
    Properties rdbProperties = new Properties();
    try
    {
      rdbProperties.load(this.getClass().getResourceAsStream("resources/rdb.properties"));
      OracleCachingDatabaseAdaptor currentAdaptor;
      int choice = 1;
      String defaultDatabase = applicationProperties.getProperty("login.database", "");
      do
      {
        currentAdaptor = findAdaptor(rdbProperties, choice++, 1);
        if(currentAdaptor != null)
        {
          login.addDatabaseAdaptor(currentAdaptor);
          if(defaultDatabase.equals(currentAdaptor.getDescription()))
            login.setDatabase(currentAdaptor);
        }
      }while(currentAdaptor != null);
//      OracleCachingDatabaseAdaptor production = new OracleCachingDatabaseAdaptor();
//      production.setConnectionCacheName("JERI");
//      production.setServerName("snsdb1.sns.ornl.gov");
//      production.setDatabaseName("prod");
//      production.setDescription("Production");
//      production.setPortNumber(1521);
//      production.setDriverType("thin");
//      OracleCachingDatabaseAdaptor failOver = new OracleCachingDatabaseAdaptor();
//      failOver.setConnectionCacheName("JERI2");
//      failOver.setDescription("Production 2");
//      failOver.setServerName("oto.sns.ornl.gov");
//      failOver.setDatabaseName("devl");
//      failOver.setPortNumber(1521);
//      failOver.setDriverType("thin");
//      production.setFailOverDatabase(failOver);
//      login.addDatabaseAdaptor(production);
//      OracleCachingDatabaseAdaptor development = new OracleCachingDatabaseAdaptor();
//      development.setConnectionCacheName("JERI3");
//      development.setDescription("Development");
//      development.setServerName("oto.sns.ornl.gov");
//      development.setDatabaseName("devl");
//      development.setPortNumber(1521);
//      development.setDriverType("thin");
//      login.addDatabaseAdaptor(development);
      //Default user ID and database to last valid ones used.
      login.setUserID(applicationProperties.getProperty("login.userID", ""));
//      if(applicationProperties.getProperty("login.database", "").equals("Development"))
//        login.setDatabase(development);
//      else
//        login.setDatabase(production);

      login.setVisible(true);

      if(login.getResult() == LoginDialog.OK)
      {
        //Attempt to load the property file from the database.
        String userID = login.getUserID();
        dataSource = login.getDatabaseAdaptor();
        loadPropertyFileFromDatabase(userID);
        //Save the settings in the properties file.
        Properties applicationProperties = getApplicationProperties();
        applicationProperties.setProperty("login.userID", userID);
//        if(dataSource == development)
//          applicationProperties.setProperty("login.database", "Development");
//        else
//          applicationProperties.setProperty("login.database", "Production");
        String database = ((OracleCachingDatabaseAdaptor)dataSource).getDescription();
        applicationProperties.setProperty("login.database", database);
        //Create the main interface
        MainFrame frame = new MainFrame(this);
        roles = dataSource.getUserRoles();
        //Sort so the user can search.
        Arrays.sort(roles);
        //Restore screen size to what it was last time. Size of screen by default.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        String frameHeightProperty = applicationProperties.getProperty("mainWindow.height", String.valueOf(screenSize.height));
        String frameWidthProperty = applicationProperties.getProperty("mainWindow.width", String.valueOf(screenSize.width));
        String frameXProperty = applicationProperties.getProperty("mainWindow.x", "0");
        String frameYProperty = applicationProperties.getProperty("mainWindow.y", "0");
        int frameHeight = Integer.parseInt(frameHeightProperty);
        int frameWidth = Integer.parseInt(frameWidthProperty);
        int frameX = Integer.parseInt(frameXProperty);
        int frameY = Integer.parseInt(frameYProperty);
        if(frameHeight > screenSize.height)
          frameHeight = screenSize.height;
        if(frameWidth > screenSize.width)
          frameWidth = screenSize.width;
        frame.setSize(frameWidth, frameHeight);
        //If window was at (0, 0) put it there, otherwise center it.
        if(frameX == 0 && frameY == 0)
          frame.setLocation(0, 0);
        else
          frame.setLocation((screenSize.width - frameWidth) / 2, (screenSize.height - frameHeight) / 2);
        title = ((OracleCachingDatabaseAdaptor)dataSource).getDescription();
        mainWindow = frame;
      }
      else
    	  System.out.println("MPS Model Cancelled During Login.");
        //System.exit(0);//User clicked cancel. Exit application.
    }
    catch(java.io.IOException ex)
    {
      ex.printStackTrace();
      //System.exit(1);//User clicked cancel. Exit application.
    }
  }

  /**
   * Looks in the given property file for the given RDB connection details.
   *
   * @param rdbProperties The instance of <CODE>Properties</CODE> containing the property file data.
   * @param choiceIndex The index of the choice of the database.
   * @param connectionIndex The index of the connection.
   * @return The <CODE>OracleCachingDatabaseAdaptor</CODE> from the instance of <CODE>Properties</CODE>.
   */
  private OracleCachingDatabaseAdaptor findAdaptor(Properties rdbProperties, int choiceIndex, int connectionIndex)
  {
    StringBuffer databaseKey = new StringBuffer("connection");
    databaseKey.append(choiceIndex);
    databaseKey.append("_");
    databaseKey.append(connectionIndex);
    databaseKey.append("_cache_name");
    String cacheName = rdbProperties.getProperty(databaseKey.toString());
    if(cacheName == null)
      return null;
    OracleCachingDatabaseAdaptor adaptor = new OracleCachingDatabaseAdaptor();
    adaptor.setConnectionCacheName(cacheName);
    databaseKey = new StringBuffer(cacheName);
    databaseKey.append("_Server_Name");
    adaptor.setServerName(rdbProperties.getProperty(databaseKey.toString()));
    databaseKey = new StringBuffer(cacheName);
    databaseKey.append("_Service_Name");
    adaptor.setServiceName(rdbProperties.getProperty(databaseKey.toString()));
    databaseKey = new StringBuffer(cacheName);
    databaseKey.append("_Description");
    adaptor.setDescription(rdbProperties.getProperty(databaseKey.toString()));
    databaseKey = new StringBuffer(cacheName);
    databaseKey.append("_Port_Number");
    String key = databaseKey.toString();
    String port = rdbProperties.getProperty(key, "1521");
    int portNumber = Integer.parseInt(port);
    adaptor.setPortNumber(portNumber);
    databaseKey = new StringBuffer(cacheName);
    databaseKey.append("_Driver_Type");
    key = databaseKey.toString();
    String driverType = rdbProperties.getProperty(key, "thin");
    adaptor.setDriverType(driverType);
    OracleCachingDatabaseAdaptor failOver = findAdaptor(rdbProperties, choiceIndex, connectionIndex + 1);
    if(failOver != null)
      adaptor.setFailOverDatabase(failOver);
    return adaptor;
  }

  /**
   * Attempts to load the property file from the database.
   *
   * @param userID The ID of the user.
   */
  private void loadPropertyFileFromDatabase(String userID)
  {
    propertyFileInDatabase = false;
    try
    {
      Connection oracleConnection = getDatabaseAdaptor().getConnection();
      try
      {
        Statement query = oracleConnection.createStatement();
        try
        {
          //Getting the properties stored in the database and replacing the local ones.
          StringBuffer sqlBuffer = new StringBuffer("SELECT JAVA_PROP FROM ");
          sqlBuffer.append(MPSBrowserView.SCHEMA);
          sqlBuffer.append(".JERI_USR_PROP WHERE USERID = '");
          sqlBuffer.append(userID);
          sqlBuffer.append("' AND PROP_CD = 'P'");
          ResultSet result = query.executeQuery(sqlBuffer.toString());
          try
          {
            if(result.next())
            {
              applicationProperties.clear();
              applicationProperties.load(result.getClob("JAVA_PROP").getAsciiStream());
              propertyFileInDatabase = true;
            }//if(result.next())
            else
              Logger.getLogger("global").log(Level.WARNING, "Unable to load application properties from the database.");
          }//try
          finally
          {
            result.close();
          }//finally
        }//try
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
    catch(java.sql.SQLException ex)
    {
      String message;
      if(propertyFileInDatabase)
        message = "Error closing the connection to the database.";
      else
        message = "Unable to load application properties from the database.";
      Logger.getLogger("global").log(Level.SEVERE, message, ex);
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null, message, "SQL Error", JOptionPane.ERROR_MESSAGE);
    }
    catch(java.io.IOException ex)
    {
      String message = "Unable to load application properties from the database.";
      Logger.getLogger("global").log(Level.SEVERE, message, ex);
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null, message, "IO Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Subclasses need to implement this method for saving the document to a URL.
   * @param url The URL to which this document should be saved.
   */
  @Override
public void saveDocumentAs(URL url)
  {
  }

  /**
   * Gets the title of the document. This is a descriptive name of the database
   * instance to which the user is connected.
   *
   * @return The title for the document.
   */
  @Override
public String getTitle()
  {
    return title;
  }

  /**
   * Determine whether the user should be warned when closing a document with
   * unsaved changes. This method returns <CODE>false</CODE> since the document
   * can not be changed by the user.
   *
   * @return <CODE>false</CODE> since the data can not be changed by the user.
   */
  @Override
public boolean warnUserOfUnsavedChangesWhenClosing()
  {
    return false;
  }

  /**
   * Called when the document will be closed.  This method automatically saves
   * the user's application settings.
   */
  @Override
protected void willClose()
  {
    super.willClose();
    String fileName = getPropertyFileName();
    //Save the properties file.
    Properties applicationProperties = getApplicationProperties();
    try
    {
      File propertyFile = new java.io.File(fileName);
      if(! propertyFile.exists())
      {
        propertyFile.getParentFile().mkdirs();
        propertyFile.createNewFile();
      }//if(! propertyFile.exists())
      FileOutputStream oStream = new java.io.FileOutputStream(propertyFile);
      applicationProperties.store(oStream, "ControlCenter property file version 1.0");
    }//try
    catch(Exception ex)
    {
      ex.printStackTrace(System.out);
      displayError("IO Error", "Unable to save preference file (" + fileName + ").");
    }//catch(java.io.IOException e)
    try
    {
      //save preference file to database.
      OracleDataSource connectionCache = (OracleDataSource)getDatabaseAdaptor().getDataSource();
      Connection oracleConnection = connectionCache.getConnection();
      oracleConnection.setAutoCommit(false);
      try
      {
        Statement query = oracleConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        try
        {
          String userID = connectionCache.getUser();
          if(! isPropertyFileInDatabase())
          {
            StringBuffer sql = new StringBuffer("INSERT INTO ");
            sql.append(MPSBrowserView.SCHEMA);
            sql.append(".JERI_USR_PROP (USERID, PROP_CD, JAVA_PROP) VALUES ('");
            sql.append(userID);
            sql.append("', 'P', EMPTY_CLOB())");
            query.execute(sql.toString());
          }//if(! isPropertyFileInDatabase())
          StringBuffer sql = new StringBuffer("SELECT JAVA_PROP FROM ");
          sql.append(MPSBrowserView.SCHEMA);
          sql.append(".JERI_USR_PROP WHERE USERID = '");
          sql.append(userID);
          sql.append("' AND PROP_CD = 'P' FOR UPDATE");
          ResultSet result = query.executeQuery(sql.toString());
          try
          {
            result.next();
            CLOB settings = (CLOB)result.getClob("JAVA_PROP");
            // This seems to cause a problem with the new cluster database
            OutputStream oStream = settings.setAsciiStream(0);
            applicationProperties.store(oStream, "ControlCenter property file version 1.0");
            oracleConnection.commit();
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
      catch(java.sql.SQLException exc)
      {
        oracleConnection.rollback();
        exc.printStackTrace();
        // throw(exc);
      }//catch(java.sql.SQLException exc)
      finally
      {
        oracleConnection.close();
      }//finally
      //Release all open database connections.
      connectionCache.close();
    }//try
    catch(Exception exc)
    {
      exc.printStackTrace();
    }//catch(Exception exc)
  }

  /**
   * Gets the <CODE>DataSource</CODE> used to connect to the database.
   *
   * @return The <CODE>DataSource</CODE> used to connect to the database.
   */
  public CachingDatabaseAdaptor getDatabaseAdaptor()
  {
    return dataSource;
  }

  /**
   * Gets the value of the property file in database flag. This flag determines
   * the type of statement used to save the property file.
   *
   * @return <CODE>true</CODE> if the property file is in the database, <CODE>false</CODE> if not.
   */
  public boolean isPropertyFileInDatabase()
  {
    return propertyFileInDatabase;
  }

  /**
   * Gets the database roles the user has.
   *
   * @return The database roles the user has.
   */
  public String[] getRoles()
  {
    return roles;
  }

  /**
   * Checks to see if the current user has the given role.
   *
   * @param role The role for which to look.
   * @return <CODE>true</CODE> if the user has the given role, <CODE>false</CODE> otherwise.
   */
  public boolean checkRole(String role)
  {
    return Arrays.binarySearch(getRoles(), role) >= 0;
  }

  /**
   * Loads the application properties from a file.
   */
  private void loadLocalProperties()
  {
	//Use property file for application settings.
    StringBuffer fileNameBuffer = new StringBuffer(System.getProperty("user.home"));

    String fs = System.getProperty("file.separator");
    if(! fileNameBuffer.toString().endsWith(fs))
        fileNameBuffer.append(fs);
    fileNameBuffer.append(".Jeri");
    fileNameBuffer.append(fs);
    fileNameBuffer.append("properties");
    try
    {
      propertyFileName = fileNameBuffer.toString();
      applicationProperties = new Properties();
      applicationProperties.load(new FileInputStream(propertyFileName));
      //Loading look and feel...
      Properties applicationProperties = getApplicationProperties();
      String metalLookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
      String localLookAndFeel = applicationProperties.getProperty("LookAndFeel", metalLookAndFeel);
      UIManager.setLookAndFeel(localLookAndFeel);
    }//try
    catch(Exception e)
    {
      e.printStackTrace();
    }//catch(Exception e)
  }
}