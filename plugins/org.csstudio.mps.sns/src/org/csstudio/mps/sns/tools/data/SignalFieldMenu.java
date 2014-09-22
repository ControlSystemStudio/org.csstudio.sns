package org.csstudio.mps.sns.tools.data;

import java.util.*;

public class SignalFieldMenu implements Cloneable 
{
  private String id;
  private ArrayList menuItems = new ArrayList();
  private ArrayList menuItemInDatabase = new ArrayList();
  private boolean inDatabase = true;
  
  public SignalFieldMenu()
  {    
  }

  public SignalFieldMenu(String id)
  {    
    this();
    setID(id);
  }

  public void setID(String id)
  {
    this.id = id;
  }

  public String getID()
  {
    return id;
  }
  
  public void addMenuItem(String menuItem)
  {
    menuItems.add(menuItem);
    menuItemInDatabase.add(Boolean.TRUE);
  }

  public boolean isMenuItemInDatabase(int menuItemIndex)
  {
    if(! isInDatabase())
      return false;//If the menu's not there the items can't be either.
    else
      return ((Boolean)menuItemInDatabase.get(menuItemIndex)).booleanValue();
  }

  public void setMenuItemInDatabase(int menuItemIndex, boolean inDatabase)
  {
    menuItemInDatabase.set(menuItemIndex, Boolean.valueOf(inDatabase));
  }
  
  public int getSize()
  {
    return menuItems.size();
  }

  public String getMenuItemAt(int index)
  {
    return menuItems.get(index).toString();
  }

  public Object clone()
  {
    SignalFieldMenu clone = new SignalFieldMenu(getID());
    int menuItemCount = getSize();
    for(int i=0;i<menuItemCount;i++)
      clone.addMenuItem(getMenuItemAt(i));
    return clone;
  }

  public boolean isInDatabase()
  {
    return inDatabase;
  }

  public void setInDatabase(boolean inDatabase)
  {
    this.inDatabase = inDatabase;
  }

  public boolean containsItem(String menuItem)
  {
    return menuItems.contains(menuItem);
  }
}