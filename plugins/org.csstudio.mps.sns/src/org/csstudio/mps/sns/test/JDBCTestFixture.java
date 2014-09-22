package org.csstudio.mps.sns.test;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.tools.database.CachingDatabaseAdaptor;
import org.csstudio.mps.sns.tools.database.OracleCachingDatabaseAdaptor;
import org.csstudio.mps.sns.tools.database.DatabaseAdaptor;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import javax.sql.*;

import javax.swing.*;

import oracle.jdbc.pool.OracleDataSource;

public class JDBCTestFixture 
{
  String _connectionURL = "jdbc:oracle:thin:@dbsdev.ornl.gov:1521:SDEVL";
  OracleDataSource _connection;
  String _password;
  String _userName;
  OracleCachingDatabaseAdaptor adaptor;
  static private JDBCTestFixture fixture;

  private JDBCTestFixture()
  {
  }

  static public JDBCTestFixture getInstance()
  {
    if(fixture == null)
      fixture = new JDBCTestFixture();
    return fixture;
  }
  
  public void setUp() throws Exception
  {
    int cacheCount = 1;
    while(_connection == null)
    {
      try
      {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        _connection = new OracleDataSource();
        adaptor = new OracleCachingDatabaseAdaptor();
        _connection.setConnectionCachingEnabled(true);
        _connection.setConnectionCacheName("JERI" + cacheCount++);
        adaptor.setConnectionCacheName("JERI" + cacheCount++);
        String url = getConnectionURL();
        _connection.setURL(url);
        adaptor.setURL(url);
        String userName = getUserName();
        _connection.setUser(userName);
        adaptor.setUser(userName);
        String password = getPassword();
        _connection.setPassword(password);
        adaptor.setPassword(password);
        //Make sure we can log in OK.
        _connection.getConnection().close();
      }
      catch(java.sql.SQLException ex)
      {
        if(ex.getErrorCode() == 1017)
        {
          _connection = null;
          adaptor = null;
          _password = null;
        }
        else
          throw ex;
      }
    }
  }

  public void tearDown() throws Exception
  {
    _connection.close();
  }

  public Connection getConnection() throws java.sql.SQLException
  {
    return _connection.getConnection();
  }

  public CachingDatabaseAdaptor getDatabaseAdaptor()
  {
    return adaptor;
  }
  
  public String getUserName()
  {
    if(_userName == null)
      login();
    return _userName;
  }

  public String getPassword()
  {
    if(_password == null)
      login();
    return _password;
  }
  
  private void login()
  {
    JPanel loginPanel = new JPanel(new BorderLayout(5, 5));
    loginPanel.add(new JLabel("Enter your RDB user ID and password."), BorderLayout.NORTH);
    JPanel labelPanel = new JPanel(new GridLayout(2, 1, 5, 5));
    labelPanel.add(new JLabel("User ID"));
    labelPanel.add(new JLabel("Password"));
    loginPanel.add(labelPanel, BorderLayout.WEST);
    JPanel fieldPanel = new JPanel(new GridLayout(2, 1, 5, 5));
    final JTextField userIDField = new JTextField();
    fieldPanel.add(userIDField);
    final JPasswordField passwordField = new JPasswordField();
    fieldPanel.add(passwordField);
    loginPanel.add(fieldPanel, BorderLayout.CENTER);
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        userIDField.requestFocus();
      }
    });
    JOptionPane.showMessageDialog(null, loginPanel, "RDB Login", JOptionPane.QUESTION_MESSAGE);
    _userName = userIDField.getText();
    _password = String.valueOf(passwordField.getPassword());
  }

  public String getConnectionURL()
  {
    return _connectionURL;
  }

  public DataSource getDataSource()
  {
    return _connection;
  }

//  public void deleteDBFileFromRDB(String fileName) throws java.sql.SQLException
//  {
//    Connection oracle = getConnection();
//    try
//    {
//      StringBuffer sql = new StringBuffer("DELETE FROM ");
//      sql.append(Jeri.SCHEMA);
//      sql.append(".IOC_DB_FILE_ASGN_SGNL WHERE EXT_SRC_FILE_NM = ?");
//      PreparedStatement signalDeleteQuery = oracle.prepareStatement(sql.toString());
//      try
//      {
//        signalDeleteQuery.setString(1, fileName);
//        signalDeleteQuery.execute();
//      }
//      finally
//      {
//        signalDeleteQuery.close();
//      }
//      sql = new StringBuffer("DELETE FROM ");
//      sql.append(Jeri.SCHEMA);
//      sql.append(".IOC_DB_FILE_ASGN WHERE EXT_SRC_FILE_NM = ?");
//      PreparedStatement fileDeleteQuery = oracle.prepareStatement(sql.toString());
//      try
//      {
//        fileDeleteQuery.setString(1, fileName);
//        fileDeleteQuery.execute();
//      }
//      finally
//      {
//        fileDeleteQuery.close();
//      }
//      oracle.commit();
//    }
//    finally
//    {
//      oracle.close();
//    }
//  }
//
  public ArrayList findValidSignalIDs(int limit) throws java.sql.SQLException
  {
    //Find and return a valid signal ID.
    Connection oracle = getConnection();
    try
    {
      Statement query = oracle.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT SGNL_ID FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".SGNL_REC");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          ArrayList ids = new ArrayList();
          int count = 0;
          while((limit == -1 || count++ < limit) && result.next())
            ids.add(result.getString("SGNL_ID"));
          return ids;
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
      oracle.close();
    }
  }
  
  public ArrayList findValidDBFileNames(int limit) throws java.sql.SQLException
  {
    //Find and return valid DB file names.
    Connection oracle = getConnection();
    try
    {
      Statement query = oracle.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT EXT_SRC_FILE_NM FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".IOC_DB_FILE_ASGN");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          ArrayList fileNames = new ArrayList();
          int count = 0;
          while((limit == -1 || count++ < limit) && result.next())
            fileNames.add(result.getString("EXT_SRC_FILE_NM"));
          return fileNames;
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
      oracle.close();
    }
  }
  
  public ArrayList findSignalIDsForDBFile(String iocID, String fileName) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("SELECT SGNL_ID FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".IOC_DB_FILE_ASGN_SGNL WHERE DVC_ID = ? AND EXT_SRC_FILE_NM = ?");
      PreparedStatement query = oracle.prepareStatement(sql.toString());
      try
      {
        query.setString(1, iocID);
        query.setString(2, fileName);
        ResultSet result = query.executeQuery();
        try
        {
          ArrayList ids = new ArrayList();
          while(result.next())
            ids.add(result.getString("SGNL_ID"));
          return ids;
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
      oracle.close();
    }
  }
  
//  public String findValidIOCID() throws java.sql.SQLException
//  {
//    Connection oracle = getConnection();
//    try
//    {
//      Statement query = oracle.createStatement();
//      try
//      {
//        StringBuffer sql = new StringBuffer("SELECT DVC_ID FROM ");
//        sql.append(Jeri.SCHEMA);
//        sql.append(".IOC_DVC");
//        ResultSet result = query.executeQuery(sql.toString());
//        try
//        {
//          if(! result.next())
//            return null;
//          else
//            return result.getString("DVC_ID");
//        }
//        finally
//        {
//          result.close();
//        }
//      }
//      finally
//      {
//        query.close();
//      }
//    }
//    finally
//    {
//      oracle.close();
//    }
//  }
//  
//  public ArrayList findDBFilesForIOC(String iocID) throws java.sql.SQLException
//  {
//    Connection oracle = getConnection();
//    try
//    {
//      StringBuffer sql = new StringBuffer("SELECT EXT_SRC_FILE_NM FROM ");
//      sql.append(Jeri.SCHEMA);
//      sql.append(".IOC_DB_FILE_ASGN WHERE DVC_ID = ?");
//      PreparedStatement query = oracle.prepareStatement(sql.toString());
//      try
//      {
//        query.setString(1, iocID);
//        ResultSet result = query.executeQuery();
//        try
//        {
//          ArrayList fileNames = new ArrayList();
//          while(result.next())
//            fileNames.add(result.getString("EXT_SRC_FILE_NM"));
//          return fileNames;
//        }
//        finally
//        {
//          result.close();
//        }
//      }
//      finally
//      {
//        query.close();
//      }
//    }
//    finally
//    {
//      oracle.close();
//    }
//  }
//  
//  public ArrayList loadIOCIDs() throws java.sql.SQLException
//  {
//    Connection oracle = getConnection();
//    try
//    {
//      Statement query = oracle.createStatement();
//      try
//      {
//        //Find a DB file with atleast one signal and field.
//        StringBuffer sql = new StringBuffer("SELECT DISTINCT DVC_ID FROM ");
//        sql.append(Jeri.SCHEMA);
//        sql.append(".IOC_DVC");
//        ResultSet result = query.executeQuery(sql.toString());
//        try
//        {
//          ArrayList ids = new ArrayList();
//          while(result.next())
//            ids.add(result.getString("DVC_ID"));
//          return ids;
//        }
//        finally
//        {
//          result.close();
//        }
//      }
//      finally
//      {
//        query.close();
//      }
//    }
//    finally
//    {
//      oracle.close();
//    }
//  }

  public ArrayList findRecordTypeIDForSignal(ArrayList signalIDs) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("SELECT REC_TYPE_ID FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".SGNL_REC WHERE SGNL_ID = ?");
      PreparedStatement query = oracle.prepareStatement(sql.toString());
      try
      {
        int signalCount = signalIDs.size();
        ArrayList recordTypeIDs = new ArrayList(signalCount);
        for(int i=0;i<signalCount;i++) 
        {
          query.setString(1, signalIDs.get(i).toString());
          ResultSet result = query.executeQuery();
          try
          {
            result.next();
            recordTypeIDs.add(result.getString("REC_TYPE_ID"));
          }
          finally
          {
            result.close();
          }
        }
        return recordTypeIDs;
      }
      finally
      {
        query.close();
      }
    }
    finally
    {
      
    }
  }
    
  public ArrayList findSignalIDsForDBFile(String fileName) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("SELECT DISTINCT SGNL_ID FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".IOC_DB_FILE_ASGN_SGNL WHERE EXT_SRC_FILE_NM = ?");
      PreparedStatement query = oracle.prepareStatement(sql.toString());
      try
      {
        query.setString(1, fileName);
        ResultSet result = query.executeQuery();
        try
        {
          ArrayList ids = new ArrayList();
          while(result.next())
            ids.add(result.getString("SGNL_ID"));
          return ids;
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
      oracle.close();
    }
  }
  
  public ArrayList findFieldIDsForSignal(String signalID) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("SELECT DISTINCT FLD_ID FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".SGNL_FLD, ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".SGNL_REC WHERE SGNL_REC.SGNL_ID = ? AND SGNL_FLD.SGNL_ID = SGNL_REC.SGNL_ID AND SGNL_FLD.REC_TYPE_ID = SGNL_FLD.REC_TYPE_ID");
      PreparedStatement query = oracle.prepareStatement(sql.toString());
      try
      {
        query.setString(1, signalID);
        ResultSet result = query.executeQuery();
        try
        {
          ArrayList ids = new ArrayList();
          while(result.next())
            ids.add(result.getString("FLD_ID"));
          return ids;
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
      oracle.close();
    }
  }
  
  public ArrayList findFieldIDsForDBFile(String fileName) throws java.sql.SQLException
  {
    ArrayList signalIDs = findSignalIDsForDBFile(fileName);
    ArrayList fieldIDs = new ArrayList();
    int signalCount = signalIDs.size();
    for(int i=0;i<signalCount;i++)
      fieldIDs.addAll(findFieldIDsForSignal(signalIDs.get(i).toString()));
    return fieldIDs;
  }
  
  public ArrayList findDBFilesWithFields(int limit) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      Statement query = oracle.createStatement();
      try
      {
        //Find a DB file with atleast one signal and field.
        StringBuffer sql = new StringBuffer("SELECT DISTINCT IOC_DB_FILE_ASGN_SGNL.EXT_SRC_FILE_NM FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".IOC_DB_FILE_ASGN_SGNL, ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".SGNL_REC, ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".SGNL_FLD WHERE IOC_DB_FILE_ASGN_SGNL.SGNL_ID = SGNL_REC.SGNL_ID AND SGNL_REC.SGNL_ID = SGNL_FLD.SGNL_ID AND SGNL_REC.REC_TYPE_ID = SGNL_FLD.REC_TYPE_ID");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          ArrayList fileNames = new ArrayList();
          int count = 0;
          while((limit == -1 || count++ < limit) && result.next())
          {
            String fileName = result.getString("EXT_SRC_FILE_NM");
            if(fileName != null)
              fileNames.add(fileName);
          }
          return fileNames;
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
      oracle.close();
    }
  }
  
  public ArrayList findIOCIDsForDBFile(String fileName) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("SELECT DISTINCT DVC_ID FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".IOC_DB_FILE_ASGN WHERE EXT_SRC_FILE_NM = ?");
      PreparedStatement query = oracle.prepareStatement(sql.toString());
      try
      {
        query.setString(1, fileName);
        ResultSet result = query.executeQuery();
        try
        {
          ArrayList ids = new ArrayList();
          while(result.next())
            ids.add(result.getString("DVC_ID"));
          return ids;
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
      oracle.close();
    }
  }
  
  public ArrayList findValidSignalIDsNotInDBFile(int limit, String fileName) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("SELECT SGNL_REC.SGNL_ID FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".SGNL_REC, ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".IOC_DB_FILE_ASGN_SGNL WHERE SGNL_REC.SGNL_ID = IOC_DB_FILE_ASGN_SGNL.SGNL_ID (+) AND (IOC_DB_FILE_ASGN_SGNL.EXT_SRC_FILE_NM IS NULL OR IOC_DB_FILE_ASGN_SGNL.EXT_SRC_FILE_NM != ?)");
      PreparedStatement query = oracle.prepareStatement(sql.toString());
      try
      {
        query.setString(1, fileName);
        ResultSet result = query.executeQuery();
        try
        {
          ArrayList ids = new ArrayList();
          int count = 0;
          while((limit == -1 || count++ < limit) && result.next())
            ids.add(result.getString("SGNL_ID"));
          return ids;
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
      oracle.close();
    }
  }
  
  public void deleteSignalsFromDBFile(ArrayList signalIDs, String dbFileName) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("DELETE FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".IOC_DB_FILE_ASGN_SGNL WHERE EXT_SRC_FILE_NM = ? AND SGNL_ID = ?");
      PreparedStatement signalDeleteQuery = oracle.prepareStatement(sql.toString());
      try
      {
        int signalCount = signalIDs.size();
        signalDeleteQuery.setString(1, dbFileName);
        for(int i=0;i<signalCount;i++) 
        {
          signalDeleteQuery.setString(2, signalIDs.get(i).toString());
          signalDeleteQuery.execute();
        }
      }
      finally
      {
        signalDeleteQuery.close();
      }
      oracle.commit();
    }
    finally
    {
      oracle.close();
    }
  }
  
  public ArrayList findDBFilesWithSignals(int limit) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      Statement query = oracle.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT EXT_SRC_FILE_NM FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".IOC_DB_FILE_ASGN_SGNL");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          ArrayList dbFileNames = new ArrayList();
          int count = 0;
          while((limit == -1 || count++ < limit) && result.next())
            dbFileNames.add(result.getString("EXT_SRC_FILE_NM"));
          return dbFileNames;
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
      oracle.close();
    }
  }
  
  public void insertSignalsIntoDBFile(ArrayList signalIDs, ArrayList dbFileNames) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("SELECT DVC_ID FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".IOC_DB_FILE_ASGN WHERE EXT_SRC_FILE_NM = ?");
      PreparedStatement iocLookupQuery = oracle.prepareStatement(sql.toString());
      try
      {
        sql = new StringBuffer("INSERT INTO ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".IOC_DB_FILE_ASGN_SGNL (DVC_ID, EXT_SRC_FILE_NM, SGNL_ID) VALUES (?, ?, ?)");
        PreparedStatement insertQuery = oracle.prepareStatement(sql.toString());
        try
        {
          int dbFileCount = dbFileNames.size();
          int signalCount = signalIDs.size();
          for(int i=0;i<dbFileCount;i++) 
          {
            String dbFileName = dbFileNames.get(i).toString();
            iocLookupQuery.setString(1, dbFileName);
            ResultSet result = iocLookupQuery.executeQuery();
            try
            {
              result.next();
              insertQuery.setString(1, result.getString("DVC_ID"));
              insertQuery.setString(2, dbFileName);
              for(int j=0;j<signalCount;j++) 
              {
                insertQuery.setString(3, signalIDs.get(j).toString());
                insertQuery.execute();
              }
            }
            finally
            {
              result.close();
            }
          }
        }
        finally
        {
          insertQuery.close();
        }
      }
      finally
      {
        iocLookupQuery.close();
      }
      oracle.commit();
    }
    finally
    {
      oracle.close();
    }
  }
  
  public ArrayList findDBFileNames(int limit) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      Statement query = oracle.createStatement();
      try
      {
        //Find a DB file with atleast one signal and field.
        StringBuffer sql = new StringBuffer("SELECT DISTINCT EXT_SRC_FILE_NM FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".IOC_DB_FILE_ASGN");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          ArrayList fileNames = new ArrayList();
          int count = 0;
          while((limit == -1 || count++ < limit) && result.next())
          {
            String fileName = result.getString("EXT_SRC_FILE_NM");
            if(fileName != null)
              fileNames.add(fileName);
          }
          return fileNames;
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
      oracle.close();
    }
  }
  
  public HashMap findValuesForFieldsInFile(String fileName) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("SELECT SGNL_FLD.SGNL_ID, FLD_ID, FLD_VAL FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".SGNL_FLD, ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".SGNL_REC, ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".IOC_DB_FILE_ASGN_SGNL WHERE IOC_DB_FILE_ASGN_SGNL.EXT_SRC_FILE_NM = ? AND IOC_DB_FILE_ASGN_SGNL.SGNL_ID = SGNL_REC.SGNL_ID AND SGNL_REC.SGNL_ID = SGNL_FLD.SGNL_ID AND SGNL_REC.REC_TYPE_ID = SGNL_FLD.REC_TYPE_ID");
      PreparedStatement query = oracle.prepareStatement(sql.toString());
      try
      {
        query.setString(1, fileName);
        ResultSet result = query.executeQuery();
        try
        {
          HashMap fileData = new HashMap();
          while(result.next())
          {
            StringBuffer hashKey = new StringBuffer(result.getString("SGNL_ID"));
            hashKey.append(" ");
            hashKey.append(result.getString("FLD_ID"));
            String fieldData = result.getString("FLD_VAL");
            fileData.put(hashKey.toString(), fieldData);
          }
          return fileData;
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
      oracle.close();
    }
  }
  
  public ArrayList findSignalIDsWithFields(int limit) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      Statement query = oracle.createStatement();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT SGNL_REC.SGNL_ID FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".SGNL_REC, ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".SGNL_FLD WHERE SGNL_REC.SGNL_ID = SGNL_FLD.SGNL_ID AND SGNL_REC.REC_TYPE_ID = SGNL_FLD.REC_TYPE_ID");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          ArrayList signalIDs = new ArrayList();
          int count = 0;
          while((limit == -1 || count++ < limit) && result.next())
            signalIDs.add(result.getString("SGNL_ID"));
          return signalIDs;
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
      oracle.close();
    }
  }
  
  public ArrayList findFieldIDsWithoutValuesForDBFile(int limit, String fileName) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("SELECT DISTINCT SGNL_FLD_DEF.FLD_ID FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".IOC_DB_FILE_ASGN_SGNL, ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".SGNL_REC, ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".SGNL_FLD_DEF, ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".SGNL_FLD WHERE IOC_DB_FILE_ASGN_SGNL.EXT_SRC_FILE_NM = ? AND IOC_DB_FILE_ASGN_SGNL.SGNL_ID = SGNL_REC.SGNL_ID AND SGNL_FLD_DEF.REC_TYPE_ID = SGNL_REC.REC_TYPE_ID AND SGNL_FLD.FLD_ID = SGNL_FLD_DEF.FLD_ID AND SGNL_FLD.FLD_ID NOT IN (SELECT FLD_ID FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".IOC_DB_FILE_ASGN_SGNL, ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".SGNL_FLD WHERE EXT_SRC_FILE_NM = ? AND IOC_DB_FILE_ASGN_SGNL.SGNL_ID = SGNL_FLD.SGNL_ID)");
      PreparedStatement query = oracle.prepareStatement(sql.toString());
      try
      {
        query.setString(1, fileName);
        query.setString(2, fileName);
        ResultSet result = query.executeQuery();
        try
        {
          ArrayList fieldIDs = new ArrayList();
          int count = 0;
          while((limit == -1 || count++ < limit) && result.next())
            fieldIDs.add(result.getString("FLD_ID"));
          return fieldIDs;
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
      oracle.close();
    }
  }
  
  public ArrayList findLocationsForArchiveGroups(ArrayList groupFileNames) throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      StringBuffer sql = new StringBuffer("SELECT ARCH_GRP_DIR_LOC FROM ");
      sql.append(MPSBrowserView.SCHEMA);
      sql.append(".ARCH_GRP WHERE ARCH_GRP_FILE_NM = ?");
      PreparedStatement query = oracle.prepareStatement(sql.toString());
      try
      {
        int groupCount = groupFileNames.size();
        ArrayList locations = new ArrayList(groupCount);
        for(int i=0;i<groupCount;i++)
        {
          query.setString(1, groupFileNames.get(i).toString());
          ResultSet result = query.executeQuery();
          try
          {
            result.next();
            locations.add(result.getString("ARCH_GRP_DIR_LOC"));
          }
          finally
          {
            result.close();
          }
        }
        return locations;
      }
      finally
      {
        query.close();
      }
    }
    finally
    {
      oracle.close();
    }
  }
  
  public ArrayList findArchiveGroupFileNames() throws java.sql.SQLException
  {
    Connection oracle = getConnection();
    try
    {
      Statement query = oracle.createStatement();
      try
      {
        //Find a DB file with atleast one signal and field.
        StringBuffer sql = new StringBuffer("SELECT ARCH_GRP_FILE_NM FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".ARCH_GRP");
        ResultSet result = query.executeQuery(sql.toString());
        try
        {
          ArrayList fileNames = new ArrayList();
          while(result.next())
            fileNames.add(result.getString("ARCH_GRP_FILE_NM"));
          return fileNames;
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
      oracle.close();
    }
  }
}