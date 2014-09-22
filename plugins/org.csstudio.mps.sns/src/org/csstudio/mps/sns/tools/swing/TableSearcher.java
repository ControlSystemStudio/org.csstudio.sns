package org.csstudio.mps.sns.tools.swing;
import org.csstudio.mps.sns.tools.Searcher;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.swing.JTable;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Component;

/**
 * Contains search replace routines for a <CODE>JTable</CODE>.
 * 
 * @author Chris Fowlkes
 */
public class TableSearcher extends Searcher
{
  /**
   * Holds the table searched.
   */
  private JTable table;
  /**
   * Holds the dialog used for search and replace.
   */
  private ColumnFindDialog findDialog;
  /**
   * Holds the last column searched.
   */
  private int searchColumnIndex;
  /**
   * Holds the names of the columns that should not be available for replace
   * operations.
   */
  private String[] readOnlyColumns;
  /**
   * Holds the value of the replace visible property.
   */
  private boolean replaceVisible = true;

  /**
   * Gets the <CODE>JTable</CODE> on which the search will be performed.
   * 
   * @return The <CODE>JTable</CODE> used in the search routines.
   */
  public JTable getTable()
  {
    return table;
  }

  /**
   * Sets the <CODE>JTable</CODE> on which to perform the search.
   * 
   * @param newTable The table on which to perform the search.
   */
  public void setTable(JTable newTable)
  {
    table = newTable;
  }

  /**
   * Makes sure the parent of the find dialog is the one given.
   * 
   * @param parent The parent for the find dialog.
   */
  protected void setDialogParent(Dialog parent)
  {
    if(findDialog == null || findDialog.getParent() != parent)
    {
      findDialog = new ColumnFindDialog(parent, "Search/Replace", true);
      setupFindDialog();
    }
  }

  /**
   * Makes sure the parent of the find dialog is the one given.
   * 
   * @param parent The parent for the find dialog.
   */
  protected void setDialogParent(Frame parent)
  {
    if(findDialog == null || findDialog.getParent() != parent)
    {
      findDialog = new ColumnFindDialog(parent, "Search/Replace", true);
      setupFindDialog();
    }
  }

  /**
   * Sets up the find dialog. The dialog needs to be constructed before calling
   * this method.
   */
  private void setupFindDialog()
  {
    findDialog.setReadOnlyColumns(getReadOnlyColumns());
    findDialog.setReplaceVisible(isReplaceVisible());
    Properties settings = getApplicationProperties();
    if(settings != null)
      findDialog.setApplicationProperties(settings);
  }

  /**
   * Displays the search dialog for the user.
   * 
   * @param replace Pass as <CODE>true</CODE> if the replace option should be selected by default.
   * @return <CODE>null</CODE> if the user cancels the search, otherwise the value for which to search is returned.
   */
  protected SearchCriteria promptUser(boolean replace)
  {
    if(findDialog == null)
    setDialogParent((Frame)null);
    JTable table = getTable();
    int columnCount = table.getColumnCount();
    String[] columnNames = new String[columnCount];
    ArrayList booleanColumns = new ArrayList();
    for(int i=0;i<columnCount;i++) 
    {
      columnNames[i] = table.getColumnName(i);
      if(table.getColumnClass(i) == Boolean.class)
        booleanColumns.add(columnNames[i]);
    }
    findDialog.setColumnNames(columnNames);
    String[] booleanColumnArray = new String[booleanColumns.size()];
    booleanColumns.toArray(booleanColumnArray);
    findDialog.setBooleanColumns(booleanColumnArray);
    String searchColumn = table.getColumnName(getSearchColumnIndex());
    if(searchColumn != null)
      findDialog.setSelectedColumn(searchColumn);
    findDialog.setReplace(replace);
    findDialog.center();
    findDialog.setVisible(true);
    if(findDialog.getResult() == ColumnFindDialog.OK)
    {
      searchColumn = findDialog.getSelectedColumn();
      int searchColumnIndex = findColumnIndex(searchColumn);
      setSearchColumnIndex(searchColumnIndex);
      Object searchValue = findDialog.getSearchValue();
      if(findDialog.isReplace())
        return new SearchCriteria(searchValue, findDialog.getReplaceValue());
      else
        return new SearchCriteria(searchValue);
    }
    else
      return null;
  }

  /**
   * Returns the index prior to where searching should begin.
   * 
   * @return The index of the row selected in the table.
   */
  protected int getStartIndex()
  {
    return table.getSelectionModel().getAnchorSelectionIndex();
  }

  /**
   * Returns the index of the last row in the table.
   * 
   * @return The last searchable index.
   */
  protected int getLastIndex()
  {
    return table.getRowCount() - 1;
  }

  /**
   * Gets the parent for the dialog used.
   * 
   * @return The parent of the search dialog.
   */
  protected Component getDialogParent()
  {
    if(findDialog == null)
      return null;
    else
      return findDialog.getParent();
  }

  /**
   * Should return <CODE>true</CODE> if the last search initiated by the user is
   * a search and replace.
   * 
   * @return <CODE>true</CODE> if doing a search and replace, <CODE>false</CODE> if only searching.
   */
  protected boolean isReplace()
  {
    return findDialog.isReplace();
  }

  /**
   * Replaces the value at the given index with the given value.
   * 
   * @param index The index of the value to replace.
   * @param replaceValue The value with which to replace the value at the given index.
   */
  protected void replace(int index, Object replaceValue)
  {
    table.setValueAt(replaceValue, index, searchColumnIndex);
  }

  /**
   * Finds the column in the table with the given name and returns it's index.
   * 
   * @param columnName The name of the column to find.
   * @return The index of the given column.
   */
  public int findColumnIndex(String columnName)
  {
    JTable table = getTable();
    int columnCount = table.getColumnCount();
    for(int i=0;i<columnCount;i++) 
    {
      if(table.getColumnName(i).equals(columnName))
        return i;
    }
    return -1;
  }

  /**
   * Sets the columns that should not be available for replace operations. This
   * would be read only columns, or columns that rely on special methods to set 
   * thier value.
   * 
   * @param newReadOnlyColumns The names of the columns in the table that should not be available for replace operations.
   */
  public void setReadOnlyColumns(String[] newReadOnlyColumns)
  {
    readOnlyColumns = newReadOnlyColumns;
    if(findDialog != null)
      findDialog.setReadOnlyColumns(readOnlyColumns);
  }
  
  /**
   * This method finds the next occurance of the value last searched for. If a 
   * matching row is found it is selected and shown.
   * 
   * @param lastRowToCheck The last row to search before returning. Pass as -1 to search all rows and wrap around if necessary.
   * @return The index of the next matching row, -1 if no match is found.
   */
  public int findNext(int lastRowToCheck)
  {
    int startRow = getStartIndex();
    int rowCount = table.getRowCount();
    if(++startRow >= rowCount)
      startRow = 0;
    int searchColumnIndex = getSearchColumnIndex();
    int rowFound;
    Object searchValue = getSearchValue();
    if(lastRowToCheck >= startRow && lastRowToCheck < rowCount)
      rowFound = searchColumnRange(startRow, lastRowToCheck + 1, searchColumnIndex, searchValue, table);
    else
    {
      rowFound = searchColumnRange(startRow, rowCount, searchColumnIndex, searchValue, table);
      if(rowFound < 0)
        if(lastRowToCheck >= 0 && lastRowToCheck < startRow)
          rowFound = searchColumnRange(0, lastRowToCheck + 1, searchColumnIndex, searchValue, table);
        else
          rowFound = searchColumnRange(0, startRow, searchColumnIndex, searchValue, table);
    }
    if(rowFound >= 0)
      selectAndShowRow(rowFound);
    return rowFound;
  }
  
  /**
   * This method finds the next occurance of the value last searched for. If a 
   * matching row is found it is selected and shown.
   * 
   * @param firstRowToCheck The first row to check. Must be a valid row number.
   * @param lastRowToCheck The last row to search before returning. Must be a valid row number.
   * @return The index of the next matching row, -1 if no match is found.
   */
  public int find(int firstRowToCheck, int lastRowToCheck)
  {
    return searchColumnRange(firstRowToCheck, lastRowToCheck + 1, searchColumnIndex, getSearchValue(), table);
  }

  /**
   * Selects the given row in the table. This method selects the given row and 
   * makes sure it is visible by scrolling the table if needed. The row passed 
   * in needs to be the row in the table, not the row in the model. Model row 
   * numbers can be converted to display row numbers with the 
   * <CODE>convertModelRowToDisplay</CODE> method.
   *
   * @param row The number of the row to select in the table.
   * @param selectedTable The table in which to select the row.
   */
  private void selectAndShowRow(int row)
  {
    JTable selectedTable = getTable();
    selectedTable.getSelectionModel().setSelectionInterval(row, row);
    Rectangle visible = selectedTable.getVisibleRect();
    Rectangle firstCell = selectedTable.getCellRect(row, 0, true);
    Rectangle scrollTo = new Rectangle(visible.x, firstCell.y, visible.width, firstCell.height);
    selectedTable.scrollRectToVisible(scrollTo);
  }
  
  /**
   * Sets the value of the replace visible property. <CODE>true</CODE> by 
   * default.
   * 
   * @param replaceVisible Pass as <CODE>false</CODE> to hide the replace controls.
   */
  public void setReplaceVisible(boolean replaceVisible)
  {
    this.replaceVisible = replaceVisible;
    if(findDialog != null)
      findDialog.setReplaceVisible(replaceVisible);
  }

  /**
   * Gets the index of the column to search. This is the index in the table, 
   * not necissarily the index in the model.
   * 
   * @return The index of the column to be searched.
   */
  public int getSearchColumnIndex()
  {
    return searchColumnIndex;
  }

  /**
   * Sets the table index of the column to be searched. 
   * 
   * @param newSearchColumn The index of the column to be searched.
   */
  public void setSearchColumnIndex(int newSearchColumnIndex)
  {
    searchColumnIndex = newSearchColumnIndex;
  }
  
  /**
   * Gets the names of the columns that should not be available for replace
   * operations. Can be <CODE>null</CODE>.
   * 
   * @return The names of the columns that are read only.
   */
  public String[] getReadOnlyColumns()
  {
    return readOnlyColumns;
  }

  /**
   * Searches a range of rows in a column for a value. This method compares the 
   * <CODE>String</CODE> values in a column starting at, and including, 
   * <CODE>searchFrom</CODE> and going to, but not including, 
   * <CODE>searchTo</CODE>.
   * 
   * @param searchFrom The first index in the range to search, inclusive.
   * @param searchTo The last index in the range to search, noninclusive.
   * @param columnIndex The index of the column to search.
   * @param searchFor The value for which to search.
   * @param table The table in which to search.
   */
  private int searchColumnRange(int searchFrom, int searchTo, int columnIndex, Object searchFor, JTable table)
  {
    if(searchFor instanceof Boolean)
      return searchColumnRange(searchFrom, searchTo, columnIndex, (Boolean)searchFor, table);
    else
    {
      String stringValue;
      if(searchFor == null)
        stringValue = "";
      else
        stringValue = searchFor.toString().trim();
      Pattern pattern = createSearchPattern(stringValue);
      return searchColumnRange(searchFrom, searchTo, columnIndex, pattern, table);
    }
  }

  /**
   * Searches a range of rows in a column for a value. This method compares the 
   * <CODE>String</CODE> values in a column starting at, and including, 
   * <CODE>searchFrom</CODE> and going to, but not including, 
   * <CODE>searchTo</CODE>.
   * 
   * @param searchFrom The first index in the range to search, inclusive.
   * @param searchTo The last index in the range to search, noninclusive.
   * @param columnIndex The index of the column to search.
   * @param searchFor The value for which to search.
   * @param table The table in which to search.
   */
  private int searchColumnRange(int searchFrom, int searchTo, int columnIndex, Pattern searchFor, JTable table)
  {
    int rowFound = -1;
    for(int i=searchFrom;i<searchTo;i++)
    {
      Object currentValue = table.getValueAt(i, columnIndex);
      String currentString;
      if(currentValue == null)
        currentString = "";
      else
        currentString = currentValue.toString().trim();
      if(compare(searchFor, currentString))
      {
        rowFound = i;
        break;
      }
    }
    return rowFound;
  }

  /**
   * Searches a range of rows in a column for a value. This method compares the 
   * <CODE>String</CODE> values in a column starting at, and including, 
   * <CODE>searchFrom</CODE> and going to, but not including, 
   * <CODE>searchTo</CODE>.
   * 
   * @param searchFrom The first index in the range to search, inclusive.
   * @param searchTo The last index in the range to search, noninclusive.
   * @param columnIndex The index of the column to search.
   * @param searchFor The value for which to search.
   * @param table The table in which to search.
   */
  private int searchColumnRange(int searchFrom, int searchTo, int columnIndex, Boolean searchFor, JTable table)
  {
    int rowFound = -1;
    for(int i=searchFrom;i<searchTo;i++)
    {
      Object currentValue = table.getValueAt(i, columnIndex);
      if(currentValue instanceof Boolean && compare(searchFor, (Boolean)currentValue))
      {
        rowFound = i;
        break;
      }
    }
    return rowFound;
  }

  /**
   * Gets the value of the replace visible property.
   * 
   * @return The value of the replace visible property.
   */
  public boolean isReplaceVisible()
  {
    return replaceVisible;
  }
  
  /**
   * Creates a <CODE>Pattern</CODE> with which to do comparisons that use the 
   * <CODE>'*'</CODE> character as a wildcard.
   * 
   * @param searchFor The search value.
   * @return The <CODE>Pattern</CODE> to use to search for the given value.
   */
  protected Pattern createSearchPattern(String searchFor)
  {
    if(searchFor == null)
      searchFor = "";
    else
    {
      searchFor = searchFor.replaceAll("\\*", "\\\\E.*\\\\Q");
      searchFor = "\\Q" + searchFor + "\\E";
    }
    return Pattern.compile(searchFor);
  }
}