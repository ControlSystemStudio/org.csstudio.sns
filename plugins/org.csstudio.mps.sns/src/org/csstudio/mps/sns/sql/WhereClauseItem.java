package org.csstudio.mps.sns.sql;
import java.util.ArrayList;

/**
 * Contains the information needed to generate a condition in a where clause.
 * 
 * @author Chris Fowlkes
 */
public class WhereClauseItem 
{
  /**
   * Holds the name of the table to which the condition applies.
   */
  private String tableName;
  /**
   * Holds the name of the column to which the condition applies.
   */
  private String columnName;
  /**
   * Holds the acceptable values for the filed.
   */
  private ArrayList values = new ArrayList();

  /**
   * Creates a <CODE>WhereClauseItem</CODE>.
   * 
   * @param tableName The name of the table to which the condition applies.
   * @param columnName The name of the column to which the condition applies.
   */
  public WhereClauseItem(String tableName, String columnName)
  {
    this.tableName = tableName;
    this.columnName = columnName;
  }
  
  /**
   * Gets the name of the table to which the condition applies.
   * 
   * @return The name of the table to which the condition applies.
   */
  public String getTableName()
  {
    return tableName;
  }
  
  /**
   * Gets the name of the column to which the condition applies.
   * 
   * @return The name of the column to which the condition applies.
   */
  public String getColumnName()
  {
    return columnName;
  }
  
  /**
   * Gets the number of acceptable values that have been added for the coumn.
   * 
   * @return The number of acceptable values for the condition.
   */
  public int getValueCount()
  {
    return values.size();
  }

  /**
   * Adds an acceptable value for the condition.
   * 
   * @param value An acceptable value.
   */
  public void addValue(String value)
  {
    values.add(value);
  }
  
  /**
   * Generates the portion of the where clause for the query that deals with the
   * given table and column. The given table and column are checked for the 
   * acceptable values added. Those checks are joined by ands and the whole 
   * condition is wrapped in parenthesis.
   * 
   * @return The portion of the where clause for the given condition.
   */
  public String toString()
  {
    int valueCount = getValueCount();
    if(valueCount <= 0)
      return "";
    String tableName = getTableName();
    String columnName = getColumnName();
    StringBuffer constraint = new StringBuffer(" (");
    for(int i=0;i<valueCount;i++)
    {
      if(i > 0)
        constraint.append(" OR ");
      constraint.append(tableName);
      constraint.append(".");
      constraint.append(columnName);
      Object currentValue = values.get(i);
      if(currentValue == null)
        constraint.append(" IS NULL ");
      else
      {
        constraint.append(" = '");
        constraint.append(currentValue);
        constraint.append("'");
      }
    }
    constraint.append(") ");
    return constraint.toString();
  }
}