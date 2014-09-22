package org.csstudio.mps.sns.apps.userproperties;
import org.csstudio.mps.sns.view.MPSBrowserView;
import org.csstudio.mps.sns.application.JeriDataModule;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import javax.sql.DataSource;

/**
 * Holds the logic for the <CODE>UserPropertiesInterface</CODE>.
 * 
 * @author Chris Fowlkes
 */
public class UserProperties extends JeriDataModule
{
  /**
   * Creates a new <CODE>UserProperties</CODE>.
   */
  public UserProperties()
  {
  }

  /**
   * Exposing <CODE>DataSource</CODE> property.
   * 
   * @param newDataSource The <CODE>DataSource</CODE> used to connect to the database.
   */
  public void setDataSource(DataSource newDataSource)
  {
    super.setDataSource(newDataSource);
  }
  
  /**
   * Loads the IDs of users that have a property file loaded in the database.
   * 
   * @return The IDs of users who have a property file in the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public ArrayList loadUserIDs() throws java.sql.SQLException
  {
    try
    {
      setProgressIndeterminate(true);
      setMessage("Loading user IDs from RDB.");
      Connection oracleConnection = getConnection();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".JERI_USR_PROP WHERE PROP_CD = 'P'");
        PreparedStatement countQuery = oracleConnection.prepareStatement(sql.toString());
        try
        {
          int count = runCountQuery(countQuery);
          setProgressMaximum(count);
          sql = new StringBuffer("SELECT USERID FROM ");
          sql.append(MPSBrowserView.SCHEMA);
          sql.append(".JERI_USR_PROP WHERE PROP_CD = 'P' ORDER BY USERID");
          PreparedStatement query = oracleConnection.prepareStatement(sql.toString());
          try
          {
            ResultSet result = query.executeQuery();
            setProgressValue(0);
            int progress = 0;
            setProgressIndeterminate(false);
            ArrayList names = new ArrayList(count);
            while(result.next())
            {
              names.add(result.getString("USERID"));
              setProgressValue(++progress);
            }
            return names;
          }
          finally
          {
            query.close();
          }
        }
        finally
        {
          countQuery.close();
        }
      }
      finally
      {
        oracleConnection.close();
      }
    }
    finally
    {
      clearProgress();
    }
  }
  
  /**
   * Loads and returns the application properties for the given user.
   * 
   * @param userID The ID of the user.
   * @return The <CODE>Properties</CODE> containing the user's settings.
   * @throws java.sql.QSLException Thrown on SQL error.
   * @throws java.io.IOException Thrown on IO error.
   */
  public Properties loadPropertiesForUser(String userID) throws java.sql.SQLException, java.io.IOException
  {
    try
    {
      setProgressIndeterminate(true);
      setMessage("Loading user's properties.");
      Connection oracleConnection = getConnection();
      try
      {
        StringBuffer sqlBuffer = new StringBuffer("SELECT JAVA_PROP FROM ");
        sqlBuffer.append(MPSBrowserView.SCHEMA);
        sqlBuffer.append(".JERI_USR_PROP WHERE USERID = ? AND PROP_CD = 'P'");
        String sql = sqlBuffer.toString();
        PreparedStatement query = oracleConnection.prepareStatement(sql);
        try
        {
          query.setString(1, userID);
          ResultSet result = query.executeQuery();
          try
          {
            if(result.next())
            {
              Properties applicationProperties = new Properties();
              InputStream inStream = result.getClob("JAVA_PROP").getAsciiStream();
              applicationProperties.load(inStream);
              return applicationProperties;
            }//if(result.next())
            else 
              return null;
          }//try
          finally
          {
            result.close();
          }//finally
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
    finally
    {
      clearProgress();
    }
  }
}