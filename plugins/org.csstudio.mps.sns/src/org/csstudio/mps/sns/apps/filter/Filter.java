package org.csstudio.mps.sns.apps.filter;

import org.csstudio.mps.sns.application.JeriDataModule;
import org.csstudio.mps.sns.sql.SelectStatement;
import org.csstudio.mps.sns.sql.TableJoin;
import org.csstudio.mps.sns.sql.WhereClauseItem;
import org.csstudio.mps.sns.tools.data.DeviceType;
import org.csstudio.mps.sns.tools.data.EpicsRecordType;
import org.csstudio.mps.sns.tools.data.EpicsSubsystem;
import org.csstudio.mps.sns.tools.data.EpicsSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.sql.DataSource;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;

import org.csstudio.mps.sns.view.MPSBrowserView;

/**
 * Holds the data and logic for the <CODE>FilterFrame</CODE> class.
 * 
 * @author Chris Fowlkes
 */
public class Filter extends JeriDataModule
{
  /**
   * Holds the name of the table being filtered. This is used to populate the 
   * fields combo box.
   */
  private String tableName;
  /**
   * Holds the owner or schema of the table in the database.
   */
  private String schema = MPSBrowserView.SCHEMA;
  /**
   * Holds the names of the filters and the property codes used to determine if
   * they are public or not.
   */
  private String[][] filters;

  /**
   * Creates a new <CODE>Filter</CODE>.
   */
  public Filter()
  {
  }

  /**
   * Loads the column names into the fields combo. This is called when both the 
   * data source and table name properties have been set.
   *
   * @return The names of all columns in the table.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public ArrayList loadColumnNames() throws java.sql.SQLException
  {
    try
    {
      setMessage("Loading column names.");
      setProgressIndeterminate(true);
      Connection oracleConnection = getConnection();
      try
      {
        Statement query = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        try
        {
          StringBuffer sql = new StringBuffer("SELECT COLUMN_NAME FROM ALL_TAB_COLUMNS WHERE TABLE_NAME = '");
          sql.append(getTableName());
          sql.append("' AND OWNER = '");
          sql.append(getSchema());
          sql.append("' ORDER BY COLUMN_NAME");
          ResultSet result = query.executeQuery(sql.toString());
          try
          {
            ArrayList names = new ArrayList();
            if(result.last())
            {
              setProgressMaximum(result.getRow());
              setProgressValue(0);
              int progress = 0;
              result.beforeFirst();
              setProgressIndeterminate(false);
              while(result.next())
              {
                names.add(result.getString("COLUMN_NAME"));
                setProgressValue(++progress);
              }
            }
            return names;
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
      finally
      {
        oracleConnection.close();
      }//finally
    }
    finally
    {
      setMessage(" ");
      setProgressValue(0);
      setProgressIndeterminate(false);
    }
  }

  /**
   * Gets the name of the table being filtered.
   * 
   * @return The value of the table name property.
   */
  public String getTableName()
  {
    return tableName;
  }
  
  /**
   * Sets the name of the table being filtered. This is needed to populate the 
   * field combo box. The field combo box is populated when the table name and
   * data source properties have been set.
   *
   * @param tableName The name of the table being filtered.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void setTableName(String tableName) throws java.sql.SQLException
  {
    this.tableName = tableName;
  }
  

  /**
   * Sets the owner, or schema, of the table in the database. This is used when 
   * retrieving the columns for the table. Defaults to <CODE>Jeri.SCHEMA</CODE>.
   * 
   * @param schema The owner or schema of the table in the database.
   */
  public void setSchema(String schema)
  {
    this.schema = schema;
  }

  /**
   * Gets the owner, or schema, of the table in the database.
   * 
   * @return The owner or schema of the table in the database.
   */
  public String getSchema()
  {
    return schema;
  }

  /**
   * Returns the names of the filters for the current table.
   * 
   * @return The names of the public and user filters for the table.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public String[] getAllFilterNames() throws java.sql.SQLException
  {
    if(filters == null)
      loadFilterNames();
    return filters[0];
  }
  
  /**
   * Returns the names of the user's filters for the table. This method will not
   * return the names of any public filters.
   * 
   * @return The names of the user's filters.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public String[] getUserFilterNames() throws java.sql.SQLException
  {
    if(filters == null)
      loadFilterNames();
    String userID = ((OracleDataSource)getDataSource()).getUser().toUpperCase();
    ArrayList filterNames = new ArrayList();
    for(int i=0;i<filters[0].length;i++) 
      if(filters[1][i].equals(userID))
        filterNames.add(filters[0][i]);
    return (String[])filterNames.toArray(new String[filterNames.size()]);
  }
  
  /**
   * Reloads the names of the filters the user has saved for the current table
   * from the database.
   * 
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public void loadFilterNames() throws java.sql.SQLException
  {
    try
    {
      setMessage("Loading column names.");
      setProgressIndeterminate(true);
      OracleConnection oracleConnection = (OracleConnection)getConnection();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        StringBuffer whereClause = new StringBuffer(getSchema());
        //Possible values for the PROP_CD field:
        //'F'  - Legacy user defined editor filter (treated as 'FU' type).
        //'FU' - User defined editor filter.
        //'FP' - Public editor filter.
        //'BU' - User defined builder filter.
        //'BP' - Public builder filter.
        whereClause.append(".JERI_USR_PROP WHERE ((USERID = ? AND (PROP_CD = ? OR PROP_CD = ? OR PROP_CD = ?)) OR PROP_CD = ? OR PROP_CD = ?) AND FLTR_TBL = ?");
        sql.append(whereClause);
        String[] parameters = new String[7];
        parameters[0] = oracleConnection.getUserName();
        parameters[1] = "BU";
        parameters[2] = "FU";
        parameters[3] = "F";
        parameters[4] = "FP";
        parameters[5] = "BP";
        parameters[6] = getTableName();
        int expectedCount;
        PreparedStatement countQuery = oracleConnection.prepareStatement(sql.toString());
        try
        {
          for(int i=0;i<parameters.length;i++) 
            countQuery.setString(i + 1, parameters[i]);
          ResultSet countResult = countQuery.executeQuery();
          try
          {
            countResult.next();
            expectedCount = countResult.getInt(1);
            setProgressMaximum(expectedCount);
          }
          finally
          {
            countResult.close();
          }
        }
        finally
        {
          countQuery.close();
        }
        sql = new StringBuffer("SELECT FLTR_NM, USERID FROM ");
        sql.append(whereClause);
        sql.append(" ORDER BY FLTR_NM");
        PreparedStatement dataQuery = oracleConnection.prepareStatement(sql.toString());
        try
        {
          for(int i=0;i<parameters.length;i++) 
            dataQuery.setString(i + 1, parameters[i]);
          ResultSet result = dataQuery.executeQuery();
          try
          {
            int index = 0;
            setProgressValue(0);
            int progress = 0;
            setProgressIndeterminate(false);
            //The number of filters in the database could have changed between 
            //the count query and the data query. Therefore we need to be safe 
            //and stick the results in an array list and convert that into
            //arrays later.
            ArrayList filterNames = new ArrayList(expectedCount);
            ArrayList filterCodes = new ArrayList(expectedCount);
            while(result.next())
            {
              filterNames.add(result.getString("FLTR_NM"));
              filterCodes.add(result.getString("USERID"));
              setProgressValue(++progress);
            }
            filters = new String[2][progress];
            filters[0] = (String[])filterNames.toArray(filters[0]);
            filters[1] = (String[])filterCodes.toArray(filters[1]);
          }//try
          finally
          {
            result.close();
          }//finally
        }//try
        finally
        {
          dataQuery.close();
        }//finally
      }//try
      finally
      {
        oracleConnection.close();
      }//finally
    }
    finally
    {
      setMessage(" ");
      setProgressValue(0);
      setProgressIndeterminate(false);
    }
  }
  
  /**
   * Loads a saved filter from the database. This method takes the filter name 
   * and returns the type and the filter contents in an array. The first item in
   * the array is the value of the PROP_CD field and the second item is the
   * value of the FLTR_CONT field.
   * 
   * @param filterName The name of the filter to laod from the database.
   * @return The filter type and contents, <CODE>null</CODE> if not found.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public String[] loadFilter(String filterName) throws java.sql.SQLException
  {
    try
    {
      setMessage("Loading filter.");
      setProgressIndeterminate(true);
      OracleConnection oracleConnection = (OracleConnection)getConnection();
      try
      {
        Statement query = oracleConnection.createStatement();
        try
        {
          String[] filterData = null;
          if(filterName != null)
          {
            StringBuffer sql = new StringBuffer("SELECT FLTR_CONT, PROP_CD FROM " );
            sql.append(MPSBrowserView.SCHEMA);
            sql.append(".JERI_USR_PROP WHERE FLTR_NM = '");
            sql.append(filterName);
            sql.append("'");
            ResultSet result = query.executeQuery(sql.toString());
            try
            {
              if(result.next())
              {
                filterData = new String[2];
                filterData[0] = result.getString("PROP_CD");
                filterData[1] = result.getString("FLTR_CONT");
              }
            }//try
            finally
            {
              result.close();
            }//finally
          }//if(filterName != null)
          return filterData;
        }//try
        finally
        {
          query.close();
        }//finally
      }//try
      finally
      {
        oracleConnection.close();
      }//finally
    }
    finally
    {
      setMessage(" ");
      setProgressValue(0);
      setProgressIndeterminate(false);
    }
  }
  
  /**
   * Check to see if the given fliter name is in use by other users.
   * 
   * @param name The filter name to check.
   * @param publicOnly Pass as <CODE>true</CODE> to only consider public filters, <CODE>false</CODE> to consider public and user filters.
   * @param currentUser Pass as <CODE>true</CODE> to only check filters belonging to the current user, pass as <CODE>false</CODE> to check filters belonging to all users except the current user.
   * @return <CODE>true</CODE> if the filter is in use by another user, <CODE>false</CODE> if the filter is not in use by another user.
   * @throws java.sql.SQLException Thrown on sql error.
   */
  public boolean checkUsersFilterNames(String tableName, String filterName, boolean publicOnly, boolean currentUser) throws java.sql.SQLException
  {
    try
    {
      setMessage("Looking for existing filter.");
      setProgressIndeterminate(true);
      OracleConnection oracleConnection = (OracleConnection)getConnection();
      try
      {
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        sql.append(MPSBrowserView.SCHEMA);
        sql.append(".JERI_USR_PROP WHERE USERID ");
        if(currentUser)
          sql.append("=");
        else
          sql.append("<>");
        sql.append(" ? AND FLTR_TBL = ? AND FLTR_NM = ? AND (");
        sql.append("PROP_CD = ? OR PROP_CD = ?");
        if(! publicOnly)
          sql.append(" OR PROP_CD = ? OR PROP_CD = ? OR PROP_CD = ?");
        sql.append(")");
        PreparedStatement nameQuery = oracleConnection.prepareStatement(sql.toString());
        try
        {
          nameQuery.setString(1, oracleConnection.getUserName().toUpperCase());
          nameQuery.setString(2, tableName);
          nameQuery.setString(3, filterName);
          //Possible values for the PROP_CD field:
          //'F'  - Legacy user defined editor filter (treated as 'FU' type).
          //'FU' - User defined editor filter.
          //'FP' - Public editor filter.
          //'BU' - User defined builder filter.
          //'BP' - Public builder filter.
          nameQuery.setString(4, "FP");
          nameQuery.setString(5, "BP");
          if(! publicOnly)
          {
            nameQuery.setString(6, "F");
            nameQuery.setString(7, "FU");
            nameQuery.setString(8, "BU");
          }
          ResultSet nameResult = nameQuery.executeQuery();
          try
          {
            nameResult.next();
            return nameResult.getInt(1) > 0;
          }
          finally
          {
            nameResult.close();
          }
        }
        finally
        {
          nameQuery.close();
        }
      }
      finally
      {
        oracleConnection.close();
      }
    }
    finally
    {
      setMessage(" ");
      setProgressValue(0);
      setProgressIndeterminate(false);
    }
  }
  
  /**
   * Checks for a filter for the user in the database. If a public filter or a 
   * filter belonging to the current user is found, the value of the PROP_CD
   * field is returned.
   * 
   * @param filterName The filter name for which to check.
   * @return The value of the PROP_CD field if the filter exists for the user, <CODE>null</CODE> otherwise.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public String checkForExistingFilter(String filterName) throws java.sql.SQLException
  {
    try
    {
      setMessage("Looking for existing filter.");
      setProgressIndeterminate(true);
      OracleConnection oracleConnection = (OracleConnection)getConnection();
      try
      {
        String tableName = getTableName();
        Statement nameQuery = oracleConnection.createStatement();
        try
        {
          StringBuffer sql = new StringBuffer("SELECT FLTR_NM, PROP_CD FROM ");
          sql.append(MPSBrowserView.SCHEMA);
          sql.append(".JERI_USR_PROP WHERE (USERID = '");
          sql.append(oracleConnection.getUserName());
          sql.append("' OR PROP_CD = 'FP' OR PROP_CD = 'BP' OR PROP_CD = 'EP') AND FLTR_TBL = '");
          sql.append(tableName);
          sql.append("' AND FLTR_NM = '");
          sql.append(filterName);
          sql.append("'");
          ResultSet nameResult = nameQuery.executeQuery(sql.toString());
          try
          {
            if(nameResult.next())
              return nameResult.getString("PROP_CD");
            else
              return null;
          }
          finally
          {
            nameResult.close();
          }
        }
        finally
        {
          nameQuery.close();
        }
      }
      finally
      {
        oracleConnection.close();
      }
    }
    finally
    {
      setMessage(" ");
      setProgressValue(0);
      setProgressIndeterminate(false);
    }
  }
  
  /**
   * Saves the given filter to the database.
   * 
   * @param newFilter Pass as <CODE>true</CODE> if the filter should be inserted into the database, <CODE>false</CODE> if it should be updated.
   * @param filterContents The filter to save.
   * @param filterType The type of the filter being saved.
   * @param filterName The name of the filter being saved.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void saveFilter(boolean newFilter, String filterContents, String filterType, String filterName) throws java.sql.SQLException
  {
    try
    {
      setMessage("Saving filter.");
      setProgressIndeterminate(true);
      OracleConnection oracleConnection = (OracleConnection)getConnection();
      try
      {
        oracleConnection.setAutoCommit(false);
        StringBuffer sql;
        if(newFilter)
        {
          sql = new StringBuffer("INSERT INTO ");
          sql.append(MPSBrowserView.SCHEMA);
          sql.append(".JERI_USR_PROP (USERID, PROP_CD, FLTR_NM, FLTR_CONT, FLTR_TBL) VALUES ('");
          sql.append(oracleConnection.getUserName());
          sql.append("', '");
          sql.append(filterType);
          sql.append("', '");
          sql.append(filterName);
          sql.append("', ?, '");
          sql.append(tableName);
          sql.append("')");
        }//if(overwrite)
        else
        {
          sql = new StringBuffer("UPDATE ");
          sql.append(MPSBrowserView.SCHEMA);
          sql.append(".JERI_USR_PROP SET FLTR_CONT = ?, PROP_CD = '");
          sql.append(filterType);
          sql.append("' WHERE USERID = '");
          sql.append(oracleConnection.getUserName());
          sql.append("' AND FLTR_TBL = '");
          sql.append(tableName);
          sql.append("' AND FLTR_NM = '");
          sql.append(filterName);
          sql.append("'");
        }//else
        PreparedStatement saveQuery = oracleConnection.prepareStatement(sql.toString());
        try
        {
          saveQuery.setString(1, filterContents);
          saveQuery.execute();
          oracleConnection.commit();
        }//try
        finally
        {
          saveQuery.close();
        }//finally
      }//try
      catch(java.sql.SQLException exc)
      {
        oracleConnection.rollback();
        throw exc;
      }//catch(java.sql.SQLException exc)
      finally
      {
        oracleConnection.close();
      }//finally
    }
    finally
    {
      setMessage(" ");
      setProgressValue(0);
      setProgressIndeterminate(false);
    }
  }
  
  /**
   * Deletes the given user filter.
   * 
   * @param filterName The name of the user filter to delete.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public void deleteFilter(String filterName) throws java.sql.SQLException
  {
    try
    {
      setMessage("Deleting filter.");
      setProgressIndeterminate(true);
      OracleConnection oracleConnection = (OracleConnection)getConnection();
      try
      {
        Statement query = oracleConnection.createStatement();
        try
        {
          if(filterName != null)
          {
            StringBuffer sql = new StringBuffer("DELETE FROM " );
            sql.append(MPSBrowserView.SCHEMA);
            sql.append(".JERI_USR_PROP WHERE USERID = '");
            sql.append(oracleConnection.getUserName());
            sql.append("' AND FLTR_NM = '");
            sql.append(filterName);
            sql.append("'");
            query.execute(sql.toString());
          }//if(filterName != null)
        }//try
        finally
        {
          query.close();
        }//finally
      }//try
      finally
      {
        oracleConnection.close();
      }//finally
    }
    finally
    {
      setMessage(" ");
      setProgressValue(0);
      setProgressIndeterminate(false);
    }
  }
    
  /**
   * Loads the contents for a list from the database. The table join data is 
   * passed in as a <CODE>String[][4]</CODE>. Each record passed in contains the 
   * name of the first table being joined, the name of the column in the first 
   * table on which to join, the name of the second table being joined, the name 
   * of the column in the second table on which to join. For example, to join 
   * the DVC and SGNL_REC table, this method would be invoked as follows:
   * <CODE>loadListContents("SGNL_REC", "SGNL_NM", new String[]{new String[]{"DVC", "DVC_ID", "SGNL_REC", "DVC_ID"}});</CODE>.
   * 
   * @param schema The schema to which the table belongs.
   * @param selectTableName The name of the table to which the column the list is displaying belongs.
   * @param selectColumnName The name of the column the list is displaying.
   * @param tableJoins The information for any tables to join with the select table.
   * @param whereClauseItems The where clause items to use.
   * @return The values returned from the database.
   * @throws java.sql.SQLException Thrown on SQL error.
   */
  public String[] loadListContents(String schema, String selectTableName, String selectColumnName, TableJoin[] tableJoins, WhereClauseItem[] whereClauseItems) throws java.sql.SQLException
  {
    ArrayList contents = new ArrayList();
    try
    {
      setProgressIndeterminate(true);
      setMessage("Loading list contents.");
      Connection oracleConnection = getConnection();
      try
      {
        Statement query = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        try
        {
          ArrayList tablesJoined = new ArrayList();
          tablesJoined.add(selectTableName);
          for(int i=0;i<tableJoins.length;i++)
          {
            String tableName = tableJoins[i].getMainTableName();
            if(! tablesJoined.contains(tableName))
              tablesJoined.add(tableName);
            tableName = tableJoins[i].getJoinTableName();
            if(! tablesJoined.contains(tableName))
              tablesJoined.add(tableName);
          }
          SelectStatement sql = new SelectStatement(selectTableName, selectColumnName, schema);
          sql.addAllTableJoins(tableJoins);
          sql.addAllWhereClauseItems(whereClauseItems);
          sql.addOrderByColumn(selectTableName, selectColumnName);
          sql.addGroupByColumn(selectTableName, selectColumnName);
          ResultSet result = query.executeQuery(sql.toString());
          try
          {
            if(result.last())
            {
              setProgressMaximum(result.getRow());
              result.beforeFirst();
              int progress = 0;
              setProgressIndeterminate(false);
              while(result.next())
              {
                String currentString = result.getString(selectColumnName);
                if(currentString == null)
                  contents.add(null);
                else
                  contents.add(currentString);
                setProgressValue(++progress);
              }
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
    finally
    {
      setProgressValue(0);
      setProgressIndeterminate(false);
      setMessage(" ");
    }
    return (String[])contents.toArray(new String[contents.size()]);
  }

  /**
   * Exposing the super classes data source property.
   * 
   * @param newDataSource The <CODE>DataSource</CODE> to use for database connections.
   */
  public void setDataSource(DataSource newDataSource)
  {
    super.setDataSource(newDataSource);
  }

  /**
   * Exposing the super classes data source property.
   * 
   * @return The <CODE>DataSource</CODE> to use for database connections.
   */
  public DataSource getDataSource()
  {
    return super.getDataSource();
  }
}