package org.csstudio.mps.sns.tools.swing.treetable;

import org.csstudio.mps.sns.tools.swing.TableSearcher;
import org.csstudio.mps.sns.tools.swing.treetable.JTreeTable;
import org.csstudio.mps.sns.tools.swing.treetable.TreeTableModel;
import org.csstudio.mps.sns.tools.swing.treetable.TreeTableModelAdapter;

import java.awt.Rectangle;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Provides an <CODE>Object</CODE> that searches through a 
 * <CODE>TreeTable</CODE> for a value.
 * 
 * @author Chris Fowlkes
 */
public class TreeTableSearcher extends TableSearcher 
{
  /**
   * Holds the index for the data held in the tree.
   */
  private TreePath[] treeIndex;
  
  /**
   * Returns the index of the last row in the table.
   * 
   * @return The last searchable index.
   */
  protected int getLastIndex()
  {
    return getTreeIndex().length;
  }

  /**
   * Returns the index prior to where searching should begin.
   * 
   * @return The index of the row selected in the table.
   */
  protected int getStartIndex()
  {
    JTreeTable table = (JTreeTable)getTable();
    JTree tree = table.getTree();
    TreePath selectedPath = tree.getAnchorSelectionPath();
    if(selectedPath == null)
      return -1;
    return findIndex(selectedPath.getLastPathComponent());
  }
  
  /**
   * Finds the given <CODE>Object</CODE> in the <CODE>ArrayList</CODE>. This 
   * method compares object instances, looking for the exact instance.
   * 
   * @param searchIn The <CODE>ArrayList</CODE> in which to search.
   * @param searchFor The <CODE>Object</CODE> for which to search.
   * @return The index of the <CODE>Object</CODE> in the <CODE>ArrayList</CODE>.
   */
  private int findIndex(Object searchFor)
  {
    TreePath[] index = getTreeIndex();
    for(int i=0;i<index.length;i++) 
      if(index[i].getLastPathComponent() == searchFor)
        return i;
    return -1;
  }

  /**
   * Gets the index of tree nodes.
   * 
   * @return All nodes from the tree in an ordered list.
   */
  private TreePath[] getTreeIndex()
  {
    if(treeIndex == null)
    {
      ArrayList paths = new ArrayList();
      JTreeTable table = (JTreeTable)getTable();
      JTree tree = table.getTree();
      TreeModel model = tree.getModel();
      TreePath rootPath = new TreePath(model.getRoot());
      if(tree.isRootVisible())
        paths.add(rootPath);        
      addChildrenToIndex(rootPath, model, paths);
      treeIndex = (TreePath[])paths.toArray(new TreePath[paths.size()]);
    }
    return treeIndex;
  }
  
  /**
   * Adds the children for the given node to the index. This method is called 
   * recursivley to add the children to the index, making it a depth first 
   * index.
   * 
   * @param node The node for which to add the children.
   * @param model The model to which the node belongs.
   * @param paths The <CODE>ArrayList</CODE> to which to add the <CODE>TreePath</CODE>.
   */
  private void addChildrenToIndex(TreePath parentPath, TreeModel model, ArrayList paths)
  {
    Object parentNode = parentPath.getLastPathComponent();
    int childCount = model.getChildCount(parentNode);
    for(int i=0;i<childCount;i++) 
    {
      TreePath currentChildPath = parentPath.pathByAddingChild(model.getChild(parentNode, i));
      paths.add(currentChildPath);
      addChildrenToIndex(currentChildPath, model, paths);
    }
  }
  
  /**
   * Sets the <CODE>JTable</CODE> on which to perform the search.
   * 
   * @param newTable The table on which to perform the search.
   */
  public void setTable(JTable newTable)
  {
    treeIndex = null;
    JTreeTable table = (JTreeTable)newTable;
    table.getTree().getModel().addTreeModelListener(new TreeModelListener()
    {
      public void treeNodesChanged(TreeModelEvent e)
      {
        treeIndex = null;
      }
      
      public void treeNodesInserted(TreeModelEvent e)
      {
        treeIndex = null;
      }
      
      public void treeNodesRemoved(TreeModelEvent e)
      {
        treeIndex = null;
      }
      
      public void treeStructureChanged(TreeModelEvent e)
      {
        treeIndex = null;
      }
    });
//    table.getModel().addTableModelListener(new TableModelListener()
//    {
//      public void tableChanged(TableModelEvent e)
//      {
//        treeIndex = null;
//      }
//    });
    super.setTable(newTable);
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
    TreePath[] index = getTreeIndex();
    if(++startRow >= index.length)
      startRow = 0;
    int searchColumnIndex = getSearchColumnIndex();
    int rowFound;
    Object searchValue = getSearchValue();
    if(lastRowToCheck >= startRow && lastRowToCheck < index.length)
      rowFound = searchColumnRange(startRow, lastRowToCheck + 1, searchColumnIndex, searchValue);
    else
    {
      rowFound = searchColumnRange(startRow, index.length, searchColumnIndex, searchValue);
      if(rowFound < 0)
        if(lastRowToCheck >= 0 && lastRowToCheck < startRow)
          rowFound = searchColumnRange(0, lastRowToCheck + 1, searchColumnIndex, searchValue);
        else
          rowFound = searchColumnRange(0, startRow, searchColumnIndex, searchValue);
    }
    if(rowFound >= 0)
      selectAndShowRow(rowFound);
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
   */
  private int searchColumnRange(int searchFrom, int searchTo, int columnIndex, Object searchFor)
  {
    if(searchFor instanceof Boolean)
      return searchColumnRange(searchFrom, searchTo, columnIndex, (Boolean)searchFor);
    else
    {
      String stringValue;
      if(searchFor == null)
        stringValue = "";
      else
        stringValue = searchFor.toString().trim();
      Pattern pattern = createSearchPattern(stringValue);
      return searchColumnRange(searchFrom, searchTo, columnIndex, pattern);
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
   */
  private int searchColumnRange(int searchFrom, int searchTo, int columnIndex, Pattern searchFor)
  {
    int rowFound = -1;
    TreeTableModel model = ((TreeTableModelAdapter)getTable().getModel()).getTreeTableModel();
    TreePath[] index = getTreeIndex();
    for(int i=searchFrom;i<searchTo;i++)
    {
      Object node = index[i].getLastPathComponent();
      Object currentValue = model.getValueAt(node, columnIndex);
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
   */
  private int searchColumnRange(int searchFrom, int searchTo, int columnIndex, Boolean searchFor)
  {
    int rowFound = -1;
    TreeTableModel model = (TreeTableModel)getTable().getModel();
    TreePath[] index = getTreeIndex();
    for(int i=searchFrom;i<searchTo;i++)
    {
      Object node = index[i].getLastPathComponent();
      Object currentValue = model.getValueAt(node, columnIndex);
      if(currentValue instanceof Boolean && compare(searchFor, (Boolean)currentValue))
      {
        rowFound = i;
        break;
      }
    }
    return rowFound;
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
    JTreeTable table = (JTreeTable)getTable();
    TreePath[] index = getTreeIndex();
    JTree tree = table.getTree();
    expandNode(index[row], tree);
    int rowToSelect = tree.getRowForPath(index[row]);
    table.getSelectionModel().setSelectionInterval(rowToSelect, rowToSelect);
    Rectangle visible = table.getVisibleRect();
    Rectangle firstCell = table.getCellRect(rowToSelect, 0, true);
    Rectangle scrollTo = new Rectangle(visible.x, firstCell.y, visible.width, firstCell.height);
    table.scrollRectToVisible(scrollTo);
  }
  
  /**
   * Completley expands a node from the root.
   * 
   * @param path The <CODE>TreePath</CODE> to expand.
   * @param tree The <CODE>JTree</CODE> containing the <CODE>TreePath</CODE> to expand.
   */
  private void expandNode(TreePath path, JTree tree)
  {
    TreePath[] paths = new TreePath[path.getPathCount()];
    for(int i=paths.length-1;i>=0;i--) 
    {
      paths[i] = path;
      path = path.getParentPath();
    }
    for(int i=0;i<paths.length;i++) 
      tree.expandPath(paths[i]);
  }
}