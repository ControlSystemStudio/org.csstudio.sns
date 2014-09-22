package org.csstudio.mps.sns.sql;
import org.csstudio.mps.sns.sql.WhereClauseItem;
import java.util.ArrayList;
import java.io.Serializable;
import org.csstudio.mps.sns.sql.TableJoin;

/**
 * Encapsulates the text for a SQL statement.
 * 
 * @author Chris Fowlkes
 */
public class SelectStatement
{
  /**
   * Holds the names of the tables to which the columns belong.
   */
  private ArrayList tableNames = new ArrayList();
  /**
   * Holds the names of the columns being selected.
   */
  private ArrayList columnNames = new ArrayList();
  /**
   * Holds the schema to which the tables belong.
   */
  private String schema;
  /**
   * Holds the information pertaining to any table being joined in the 
   * statement.
   */
  private ArrayList tableJoins = new ArrayList();
  /**
   * Holds any where clause items for the statement.
   */
  private ArrayList whereClauseItems = new ArrayList();
  /**
   * Holds the names of any columns in the order by clause.
   */
  private ArrayList orderByColumns = new ArrayList();
  /**
   * Holds the names of any columns in the group by clause.
   */
  private ArrayList groupByColumns = new ArrayList();
  /**
   * Holds the from clause. If this is not <CODE>null</CODE> the value in it
   * overides the calculated value for the clause.
   */
  private String fromClause;
  /**
   * Holds the where clause. If this is not <CODE>null</CODE> the value in it
   * overides the calculated value for the clause.
   */
  private String whereClause;
  
  /**
   * Creates a new <CODE>SelectStatement</CODE>.
   */
  public SelectStatement()
  {
    
  }
  
  /**
   * Creates and initializes a new <CODE>SelectStatement</CODE>.
   * 
   * @param tableName The name of the table to which the given column belongs.
   * @param columnName The name of the first column to select.
   */
  public SelectStatement(String tableName, String columnName)
  {
    this();
    addColumn(tableName, columnName);
  }
  
  /**
   * Creates and initializes a new <CODE>SelectStatement</CODE>.
   * 
   * @param tableName The name of the table to which the given column belongs.
   * @param columnName The name of the first column to select.
   * @param schema The schema to which all tables referenced belong.
   */
  public SelectStatement(String tableName, String columnName, String schema)
  {
    this(tableName, columnName);
    setSchema(schema);
  }

  /**
   * Adds information needed to join two tables in the statement.
   * 
   * @param table The information to join two tables.
   */
  public void addTableJoin(TableJoin table)
  {
    tableJoins.add(table);
  }
  
  /**
   * Adds a column to the select statement.
   * 
   * @param tableName The name of the table to which the given column belings.
   * @param columnName The name of the column to select.
   */
  public void addColumn(String tableName, String columnName)
  {
    tableNames.add(tableName);
    columnNames.add(columnName);
  }

  /**
   * Generates the SQL statement.
   * 
   * @return The text for the SQL statement.
   */
  public String toString()
  {
    //Build the select clause.
    StringBuffer sql = new StringBuffer("SELECT ");
    int columnCount = columnNames.size();
    for(int i=0;i<columnCount;i++) 
    {
      if(i > 0)
        sql.append(", ");
      sql.append(tableNames.get(i));
      sql.append(".");
      sql.append(columnNames.get(i));
    }
    sql.append(" FROM ");
    sql.append(getFromClause());
    String whereClause = getWhereClause();
    if(! whereClause.trim().equals(""))
    {
      sql.append(" WHERE ");
      sql.append(whereClause);
    }
    int groupByColumnCount = groupByColumns.size();
    if(groupByColumnCount > 0)
    {
      sql.append(" GROUP BY ");
      for(int i=0;i<groupByColumnCount;i++)
      {
        if(i > 0)
          sql.append(", ");
        sql.append(groupByColumns.get(i));
      }
    }
    int orderByColumnCount = orderByColumns.size();
    if(orderByColumnCount > 0)
    {
      sql.append(" ORDER BY ");
      for(int i=0;i<orderByColumnCount;i++)
      {
        if(i > 0)
          sql.append(", ");
        sql.append(orderByColumns.get(i));
      }
    }
    return sql.toString();
  }

  /**
   * Adds the given table to the text of the statement. The table name will only 
   * be added if it has not already been added. This method is to be used to 
   * build the from clause of the statement.
   * 
   * @param sql The text of the statement so far.
   * @param tableName The name of the table referenced.
   * @param tablesUsed The names of the tables used so far.
   */
  private void addTableToStatement(StringBuffer sql, String tableName, ArrayList tablesUsed)
  {
    if(tablesUsed == null || ! tablesUsed.contains(tableName))
    {
      if(tablesUsed != null && tablesUsed.size() > 0)
        sql.append(", ");
      String schema = getSchema();
      if(schema != null)
      {
        sql.append(schema);
        sql.append(".");
      }
      sql.append(tableName);
      if(tablesUsed != null)
        tablesUsed.add(tableName);
    }
  }

  /**
   * Gets the schema to which all tables in the statement belong.
   * 
   * @return The schema used by the statement.
   */
  public String getSchema()
  {
    return schema;
  }

  /**
   * Sets the name of the schema to which all tables belong. This text will be 
   * prepended to the table names, seperated by a '.', in the from clause.
   * 
   * @param schema The schema to which the tables in the statement belong.
   */
  public void setSchema(String schema)
  {
    this.schema = schema;
  }
  
  /**
   * Adds an item to the where clause. All items will be seperated by ands.
   * 
   * @param item An item to add to the where clause.
   */
  public void addWhereClauseItem(WhereClauseItem item)
  {
    whereClauseItems.add(item);
  }

  /**
   * Joins two tables in the statement. The tables will be joined even if only
   * one of them is selected from.
   * 
   * @param joins The information needed to join two tables in the statement.
   */
  public void addAllTableJoins(TableJoin[] joins)
  {
    for(int i=0;i<joins.length;i++) 
      addTableJoin(joins[i]);
  }
  
  /**
   * Adds a group of items to the where clause.
   * 
   * @param items The items to add to the where clause.
   */
  public void addAllWhereClauseItems(WhereClauseItem[] items)
  {
    for(int i=0;i<items.length;i++) 
      addWhereClauseItem(items[i]);
  }

  /**
   * Adds a column to the order by clause.
   * 
   * @param tableName The name of the table to which the given column belongs.
   * @param columnName The name of the column to add to the clause.
   */
  public void addOrderByColumn(String tableName, String columnName)
  {
    StringBuffer name = new StringBuffer(tableName);
    name.append(".");
    name.append(columnName);
    orderByColumns.add(name.toString());
  }

  /**
   * Adds a column to the group by clause.
   * 
   * @param tableName The name of the table to which the given column belongs.
   * @param columnName The name of the column to add to the clause.
   */
  public void addGroupByColumn(String tableName, String columnName)
  {
    StringBuffer name = new StringBuffer(tableName);
    name.append(".");
    name.append(columnName);
    groupByColumns.add(name.toString());
  }  

  /**
   * Gets the text that makes up the from clause portion of the statement.
   * 
   * @return The text that makes up the from clause.
   */
  public String getFromClause()
  {
    if(fromClause != null)
      return fromClause;
    //Build the from clause.
    StringBuffer sql = new StringBuffer("");
    int tableJoinCount = tableJoins.size();
    if(tableJoinCount > 0)
    {
      ArrayList tablesUsed = new ArrayList();
      for(int i=0;i<tableJoinCount;i++) 
      {
        TableJoin currentJoin = (TableJoin)tableJoins.get(i);
        addTableToStatement(sql, currentJoin.getMainTableName(), tablesUsed);
        addTableToStatement(sql, currentJoin.getJoinTableName(), tablesUsed);
      }
    }
    else
      if(tableNames.size() > 0)
        addTableToStatement(sql, tableNames.get(0).toString(), null);
    return sql.toString();
  }
  
  /**
   * Gets the text that makes up the where clause portion of the statement.
   * 
   * @return The text that makes up the where clause.
   */
  public String getWhereClause()
  {
    if(whereClause != null)
      return whereClause;
    //Build the where clause.
    int tableJoinCount = tableJoins.size();
    int whereClauseCount = whereClauseItems.size();
    StringBuffer sql = new StringBuffer();
    if(tableJoinCount > 0 || whereClauseCount > 0)
    {
      if(tableJoinCount > 0)
      {
        sql.append(" ( ");
        for(int i=0;i<tableJoinCount;i++) 
        {
          if(i > 0)
            sql.append(" AND ");
          sql.append(tableJoins.get(i));
        }
        sql.append(" ) ");
      }
      if(whereClauseCount > 0)
      {
        if(tableJoinCount > 0)
          sql.append(" AND ");
        sql.append(" ( ");
        for(int i=0;i<whereClauseCount;i++) 
        {
          if(i > 0)
            sql.append(" AND ");
          sql.append(" ( ");
          sql.append(whereClauseItems.get(i));
          sql.append(" ) ");
        }
        sql.append(" ) ");
      }
    }
    return sql.toString();
  }

  /**
   * Overides the from clause. If this method is called with a non-null value,
   * the from clause will not be built dynamically. Instead this text will be 
   * inserted. This text should not include the from keyword.
   * 
   * @param fromClause The text to insert as the from clause.
   */
  public void setFromClause(String fromClause)
  {
    this.fromClause = fromClause;
  }
  
  /**
   * Overides the where clause. If this method is called with a non-null value,
   * the where clause will not be built dynamically. Instead this text will be 
   * inserted. This text should not include the where keyword.
   * 
   * @param whereClause The text to insert as the where clause.
   */
  public void setWhereClause(String whereClause)
  {
    this.whereClause = whereClause;
  }
}