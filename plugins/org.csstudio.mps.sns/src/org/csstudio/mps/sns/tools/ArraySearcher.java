package org.csstudio.mps.sns.tools;
import java.util.ArrayList;

/**
 * Holds methods for finding items in an array.
 * 
 * @author Chris Fowlkes
 */
public class ArraySearcher 
{
  /**
   * Provate constructor to prevent instances being created. All methods are 
   * static.
   */
  private ArraySearcher()
  {
  }
  
  /**
   * Looks for the items in one array in the other. The items that are not found
   * in the array searched are returned. This method does not use the equals 
   * method, but looks to see if the items are the same objects.
   * 
   * @param lookFor The items to look for in the other array.
   * @param lookIn The array in which to search for the items in the other array.
   * @return The items from the first array that are not in the second array.
   */
  public static Object[] findObjectsNotInArray(Object[] lookFor, Object[] lookIn)
  {
    ArrayList notFound = new ArrayList();
    for(int i=0;i<lookFor.length;i++) 
      if(ArraySearcher.findObjectIndex(lookFor[i], lookIn) == -1)
        notFound.add(lookFor[i]);
    Class classType = lookFor.getClass().getComponentType();
    int itemCount = notFound.size();
    Object[] items = (Object[])java.lang.reflect.Array.newInstance(classType, itemCount);
    return notFound.toArray(items);
  }
  
  /**
   * Finds and returns the index of the given <CODE>Object</CODE> in the array.
   * 
   * @param searchFor The <CODE>Object</CODE> to search for in the array.
   * @param searchIn The array in which to search for the <CODE>Object</CODE>.
   * @return The index of the <CODE>Object</CODE> in the array.
   */
  public static int findObjectIndex(Object searchFor, Object[] searchIn)
  {
    for(int i=0;i<searchIn.length;i++) 
      if(searchIn[i] == searchFor)
        return i;
    return -1;
  }
}