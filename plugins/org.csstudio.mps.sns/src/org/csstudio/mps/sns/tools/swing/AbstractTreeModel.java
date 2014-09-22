package org.csstudio.mps.sns.tools.swing;
import org.csstudio.mps.sns.tools.ArraySearcher;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

/**
 * Provides a starting oint for building a custom model for a 
 * <CODE>JTree</CODE>.
 * 
 * @author Chris Fowlkes
 */
public abstract class AbstractTreeModel implements TreeModel 
{
  private transient ArrayList treeModelListeners = new ArrayList(2);

  /**
   * Creates an <CODE>AbstractTreeModel</CODE>.
   */
  public AbstractTreeModel()
  {
  }

  /**
   * Adds the given lisener to the model.
   * 
   * @param l The <CODE>TreeModelListener</CODE> to add to the model.
   */
  public synchronized void addTreeModelListener(TreeModelListener l)
  {
    if (!treeModelListeners.contains(l))
    {
      treeModelListeners.add(l);
    }
  }

  /**
   * Removes the given <CODE>TreeModelListener</CODE> from the model.
   * 
   * @param The <CODE>TreeModelListener</CODE> to remove from the model.
   */
  public synchronized void removeTreeModelListener(TreeModelListener l)
  {
    treeModelListeners.remove(l);
  }

  /**
   * Fires the tree nodes changed event.
   * 
   * @param e The event to fire.
   */
  public void fireTreeNodesChanged(TreeModelEvent e)
  {
    List listeners = (List)treeModelListeners.clone();
    int count = listeners.size();

    for (int i = 0;i < count;i++)
    {
      ((TreeModelListener)listeners.get(i)).treeNodesChanged(e);
    }
  }

  /**
   * Fires the tree nodes inserted event.
   * 
   * @param e The event to fire.
   */
  public void fireTreeNodesInserted(TreeModelEvent e)
  {
    List listeners = (List)treeModelListeners.clone();
    int count = listeners.size();

    for (int i = 0;i < count;i++)
    {
      ((TreeModelListener)listeners.get(i)).treeNodesInserted(e);
    }
  }

  /**
   * Fires the tree nodes removed event.
   * 
   * @param e The event to fire.
   */
  public void fireTreeNodesRemoved(TreeModelEvent e)
  {
    List listeners = (List)treeModelListeners.clone();
    int count = listeners.size();

    for (int i = 0;i < count;i++)
    {
      ((TreeModelListener)listeners.get(i)).treeNodesRemoved(e);
    }
  }

  /**
   * Fires the tree structure changed event.
   * 
   * @param e The event to fire.
   */
  public void fireTreeStructureChanged(TreeModelEvent e)
  {
    List listeners = (List)treeModelListeners.clone();
    int count = listeners.size();

    for (int i = 0;i < count;i++)
    {
      ((TreeModelListener)listeners.get(i)).treeStructureChanged(e);
    }
  }

  /**
   * Checks to see if items have been removed from the tree and fires the proper 
   * events. This method uses <CODE>SwingUtilities.invokeLater</CODE> to fire 
   * the events in a thread safe manner.
   * 
   * @param oldItems The old children of the changed node.
   * @param newItems The new children of the changed node.
   * @param path The path to the node changed.
   * @param source The event source.
   * @return The children removed from the node.
   */
  protected Object[] checkForRemovedItems(Object[] oldItems, Object[] newItems, Object[] path, Object source)
  {
    Object[] deletedItems = ArraySearcher.findObjectsNotInArray(oldItems, newItems);
    if(deletedItems.length > 0)
    {
      TreeModelEvent event = new TreeModelEvent(source, path);
      fireTreeStructureChanged(event);
    }
    return deletedItems;
  }
  
  /**
   * Checks to see if items have been added to the tree and fires the proper 
   * events. This method uses <CODE>SwingUtilities.invokeLater</CODE> to fire 
   * the events in a thread safe manner.
   * 
   * @param oldItems The old children of the changed node.
   * @param newItems The new children of the changed node.
   * @param path The path to the node changed.
   * @param source The event source.
   * @return The children added to the node.
   */
  protected Object[] checkForAddedItems(Object[] oldItems, Object[] newItems, Object[] path, Object source)
  {
    Object[] addedItems = ArraySearcher.findObjectsNotInArray(newItems, oldItems);
    if(addedItems.length > 0)
    {
      TreeModelEvent event = new TreeModelEvent(source, path);
      fireTreeStructureChanged(event);
    }
    return addedItems;
  }
}