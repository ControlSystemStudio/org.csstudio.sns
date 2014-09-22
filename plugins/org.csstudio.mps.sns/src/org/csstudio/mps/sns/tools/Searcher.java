package org.csstudio.mps.sns.tools;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 * Provides a super class for searching utilities.
 * 
 * @author Chris Fowlkes
 */
public abstract class Searcher 
{
  /**
   * Holds the last value searched for.
   */
  private SearchCriteria lastSearch;
  /**
   * Holds the properties of the application.
   */
  private Properties applicationProperties;
  /**
   * Passes the parent of the dialog to the concrete subclass.
   * 
   * @param parent The parent of the dialog.
   */
  abstract protected void setDialogParent(Dialog parent);
  /**
   * Passes the parent of the dialog to the concrete subclass.
   * 
   * @param parent The parent of the dialog.
   */
  abstract protected void setDialogParent(Frame parent);
  /**
   * This method should prompt the user for the search text.
   * 
   * @param replace Pass as <CODE>true</CODE> if the replace option should be selected by default.
   * @return <CODE>false</CODE> if the user cancels the search, <CODE>true</CODE> otherwise.
   */
  abstract protected SearchCriteria promptUser(boolean replace);
  /**
   * Should return the index at which the search should start. For a 
   * <CODE>JTable</CODE> searcher, the index of the selected row in the table
   * would be returned. The search will start at the index after the one 
   * returned. If <CODE>-1</CODE> is returned, the search will start from the 
   * begining.
   * 
   * @return The index before the one with whcich to start the search.
   */
  abstract protected int getStartIndex();
  /**
   * Gets the last searchable index. For a <CODE>JTable</CODE> this would be the 
   * index of the last row in the table.
   * 
   * @return The last searchable index.
   */
  abstract protected int getLastIndex();
  /**
   * Gets the <CODE>Component</CODE> used as the parent for dialogs.
   * 
   * @return The <CODE>Component</CODE> used as the parent fopr dialogs.
   */
  abstract protected Component getDialogParent();
  /**
   * Replaces the value at the given index.
   * 
   * @param index The index of the value to replace.
   * @param replaceValue The value with which to replace the value at the given index.
   */
  abstract protected void replace(int index, Object replaceValue);
  /**
   * Searches starting at the index following the one given.
   * 
   * @param startIndex The index after which to start.
   * @return The index found, or -1 if no instance is found.
   */
  abstract public int findNext(int startIndex);
  /**
   * Searches the first, ast, and in between the given indexes.
   * 
   * @param firstIndex The first index to search.
   * @param lastIndex The last index to search.
   * @return The index where the value was found, or <CODE>-1</CODE> if the value was not found.
   */
  abstract public int find(int firstIndex, int lastIndex);

  /**
   * Creates a new <CODE>TableSearcher</CODE>.
   */
  public Searcher()
  {
  }

  /**
   * This method shows the find dialog and then searches for the value supplied 
   * by the user via the <CODE>findNext()</CODE> method.   
   * 
   * @param replace Pass as <CODE>true</CODE> if the replace option should be selected in the dialog by default, <CODE>false</CODE> if not.
   * @param parent The parent window , used to show modal dialogs.
   */
  public void searchReplace(boolean replace, Dialog parent)
  {
    setDialogParent(parent);
    searchReplace(replace);
  }

  /**
   * This method shows the find dialog and then searches for the value supplied 
   * by the user via the <CODE>findNext()</CODE> method.   
   * 
   * @param replace Pass as <CODE>true</CODE> if the replace option should be selected in the dialog by default, <CODE>false</CODE> if not.
   * @param parent The parent window , used to show modal dialogs.
   */
  public void searchReplace(boolean replace, Frame parent)
  {
    setDialogParent(parent);
    searchReplace(replace);
  }

  /**
   * Does a search replace operation.
   * 
   * @param replace Pass as <CODE>true</CODE> if the replace option should be selected in the dialog by default, <CODE>false</CODE> if not.
   */
  private void searchReplace(boolean replace)
  {
    //Prompt the user for the search.
    SearchCriteria searchCriteria = promptUser(replace);
    setLastSearch(searchCriteria);
    if(searchCriteria != null)//null means user canceled
    {
      Object searchValue = searchCriteria.getSearchValue();
      setSearchValue(searchValue);
      int startIndex = getStartIndex();
      if(startIndex < 0)
        startIndex = getLastIndex();
      int indexFound = findNext(startIndex);
      if(indexFound < 0)
      {
        StringBuffer messageBuffer = new StringBuffer("The search value '");
        messageBuffer.append(searchValue);
        messageBuffer.append("' was not found.");
        String message = messageBuffer.toString();
        JOptionPane.showMessageDialog(getDialogParent(),  message, "Find Error", JOptionPane.ERROR_MESSAGE);
      }
      else
        if(searchCriteria.isReplace())
        {
          int replaceOption = searchCriteria.getReplaceType();
          String options[] = new String[]{"Yes", "No", "All", "Cancel"};
          do
          {
            if(replaceOption == SearchCriteria.PROMPT)
            {
              int option = JOptionPane.showOptionDialog(getDialogParent(), "Replace this occurance?", "Confirm Replace", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
              if(option == 1)//No
              {
                indexFound = findNext(startIndex);
                continue;
              }
              else
                if(option == 2)//All
                  replaceOption = SearchCriteria.ALL;
                else
                  if(option == 3)//Cancel
                    break;
            }
            replace(indexFound, searchCriteria.getReplaceValue());
            if(replaceOption == SearchCriteria.SINGLE)
              indexFound = -1;
            else
              indexFound = findNext(startIndex);
          }while(indexFound >= 0);
        }
    }
  }

  /**
   * Compares two instances of <CODE>Boolean</CODE>.
   * 
   * @param searchFor The search for value.
   * @param compareTo The value to which to compare the search value.
   * @return <CODE>true</CODE> if the values are equal, <CODE>false</CODE> otherwise.
   */
  protected boolean compare(Boolean searchFor, Boolean compareTo)
  {
    return searchFor.equals(compareTo);
  }

  /**
   * Compares two instances of <CODE>String</CODE>. The search for value can 
   * contain wildcards.
   * 
   * @param searchFor The search for value.
   * @param compareTo The value to which to compare the search value.
   * @return 
   */
  protected boolean compare(Pattern searchFor, String compareTo)
  {
    if(compareTo == null)
      compareTo = "";
    else
      compareTo = compareTo.trim();
    return searchFor.matcher(compareTo).matches();
  }

  /**
   * Gets the text for which a search was performed.
   * 
   * @return The last search text.
   */
  protected SearchCriteria getLastSearch()
  {
    return lastSearch;
  }
  
  /**
   * Returns the value for which the search was last performed.
   * 
   * @return The last search value.
   */
  public Object getSearchValue()
  {
    SearchCriteria criteria = getLastSearch();
    if(criteria == null)
      return null;
    else
      return criteria.getSearchValue();
  }

  /**
   * Sets the search value to the given value. This can be used to search 
   * programatically without prompting the user by setting the value and 
   * invoking <CODE>findNext()</CODE>.
   * @param searchValue 
   */
  public void setSearchValue(Object searchValue)
  {
    setLastSearch(new SearchCriteria(searchValue));
  }
  
  /**
   * This method can be used to set the search value to a <CODE>String</CODE>.
   * The <CODE>findNext</CODE> method can then be invoked, causing a search for
   * a value without prompting the user.
   * 
   * @param The <CODE>String/CODE> for which to search the table.
   */
  protected void setLastSearch(SearchCriteria lastSearch)
  {
    this.lastSearch = lastSearch;
  }
  
  /**
   * Sets the properties stored in the applications properties file. These are 
   * read in before the user logs in by the main application class and passed to 
   * this class as an instance of <CODE>Properties</CODE>.
   * 
   * @param applicationProperties The applicationProperties application settings.
   */
  public void setApplicationProperties(Properties applicationProperties)
  {
    this.applicationProperties = applicationProperties;
  }

  /**
   * Gets the properties stored in the applications properties file.
   *
   * @return The settings for the application.
   */
  public Properties getApplicationProperties()
  {
    return applicationProperties;
  }
  
//  /**
//   * This method finds the next occurance of the value last searched for. If a 
//   * matching row is found it is selected and shown.
//   * 
//   * @param lastIndexToCheck The last row to search before returning. Pass as -1 to search all rows and wrap around if necessary.
//   * @return The index of the next match, -1 if no match is found.
//   */
//  public int findNext(int lastIndexToCheck)
//  {
//    int startIndex = getStartIndex();
//    int lastIndex = getLastIndex();
//    if(++startIndex > lastIndex)
//      startIndex = 0;
//    int indexFound;
//    Object searchValue = getSearchValue();
//    if(lastIndexToCheck >= startRow && lastIndexToCheck <= lastIndex)
//      indexFound = findNext(startIndex, lastIndexToCheck);
//    else
//    {
//      indexFound = findNext(startIndex, lastIndex);
//      if(indexFound < 0)
//        if(lastIndexToCheck >= 0 && lastIndexToCheck < startIndex)
//          rowFound = searchColumnRange(0, lastRowToCheck);
//        else
//          rowFound = searchColumnRange(0, startRow);
//    }
//    if(rowFound >= 0)
//      selectAndShowRow(rowFound);
//    return rowFound;
//  }

  /**
   * Holds the criteria needed to perform a search.
   * 
   * @author Chris Fowlkes
   */
  public class SearchCriteria
  {
    /**
     * Holds the value for which the search is to be performed.
     */
    private Object searchValue;
    /**
     * Holds the value to replace the search value.
     */
    private Object replaceValue;
    /**
     * Holds the floag that determines if the replace value is used.
     */
    private boolean replace = false;
    /**
     * Constant that denotes the replace single option was selected.
     */
    public final static int SINGLE = 2;
    /**
     * Constant that denotes the replace all option was selected.
     */
    public final static int ALL = 3;
    /**
     * Constant that denotes the prompt for replace option was selected.
     */
    public final static int PROMPT = 4;
    /**
     * Holds the replace type for the search replace operation.
     */
    public int replaceType;
    
    /**
     * Creates a new <CODE>SearchCriteria</CODE>.
     * 
     * @param searchValue The value for which to search.
     */
    public SearchCriteria(Object searchValue)
    {
      setSearchValue(searchValue);
    }
    
    /**
     * Creates a new <CODE>SearchCriteria</CODE>. Use this constructor for a 
     * search and replace operation.
     * 
     * @param searchValue The value for which to search.
     * @param replaceValue The value with which to replace the search value.
     */
    public SearchCriteria(Object searchValue, Object replaceValue)
    {
      this(searchValue);
      setReplaceValue(replaceValue);
      setReplace(true);
    }
    
    /**
     * Sets the search value for the criteria.
     * 
     * @param searchValue The value for which to search.
     */
    public void setSearchValue(Object searchValue)
    {
      this.searchValue = searchValue;
    }
    
    /**
     * Returns the value for which to search.
     * 
     * @return The value for which to search.
     */
    public Object getSearchValue()
    {
      return searchValue;
    }
    
    /**
     * Sets the replace flag value.
     * 
     * @param replace Pass as <CODE>true</CODE> for a replace operation, <CODE>false</CODE> for a regular search.
     */
    public void setReplace(boolean replace)
    {
      this.replace = replace;
    }
    
    /**
     * Returns the value of the replace flag.
     * 
     * @return <CODE>true</CODE> if the search is a replace operation, <CODE>false</CODE> otherwise.
     */
    public boolean isReplace()
    {
      return replace;
    }
    
    /**
     * Sets the replace value for the search.
     * 
     * @param replaceValue The value with which to replace the search value.
     */
    public void setReplaceValue(Object replaceValue)
    {
      this.replaceValue = replaceValue;
    }
    
    /**
     * Gets the value with which the search value will be replaced.
     * 
     * @return The value with which to replace the search value.
     */
    public Object getReplaceValue()
    {
      return replaceValue;
    }
    
    /**
     * Sets the replace type for the replace operation. This will be either 
     * <CODE>Searcher.ALL</CODE>, <CODE>Searcher.PROMPT</CODE>, or 
     * <CODE>Searcher.SINGLE</CODE>.
     * 
     * @param replaceType The replace operation.
     */
    public void setReplaceType(int replaceType)
    {
      this.replaceType = replaceType;
    }

    /**
     * Gets the replace option for the replace operation. This will be either 
     * <CODE>Searcher.ALL</CODE>, <CODE>Searcher.PROMPT</CODE>, or 
     * <CODE>Searcher.SINGLE</CODE>.
     * 
     * @return The option for the replace operation.
     */
    public int getReplaceType()
    {
      return replaceType;
    }
  }
}
