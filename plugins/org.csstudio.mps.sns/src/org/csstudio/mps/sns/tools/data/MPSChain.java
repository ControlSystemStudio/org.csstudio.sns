package org.csstudio.mps.sns.tools.data;
import java.util.*;

import org.csstudio.mps.sns.tools.data.MPSBoard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MPSChain 
{
  private String id;
  private String name;
  private ArrayList boards = new ArrayList();
  /**
   * Holds the number of the <CODE>MPSChain</CODE>.
   */
  private int number;
  /**
   * Holds the instances of <CODE>Detector</CODE> that are added to the chain.
   */
  private ArrayList<Detector> detectors = new ArrayList();
  private PropertyChangeSupport propertyChangeSupport;
  
  public MPSChain()
  {
    propertyChangeSupport = new PropertyChangeSupport(this);
  }

  public MPSChain(String id)
  {
    this();
    setID(id);
  }

  public MPSChain(String id, String name)
  {
    this(id);
    setName(name);
  }

  public void setID(String id)
  {
    this.id = id;
  }

  public String getID()
  {
    return id;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public String toString()
  {
    String stringValue = getName();
    if(stringValue == null)
      stringValue = "";
    return stringValue;
  }

  public void addBoard(MPSBoard boardToAdd)
  {
    boards.add(boardToAdd);
    boardToAdd.setChain(this);
  }

  public int getBoardCount()
  {
    return boards.size();
  }

  public MPSBoard getBoardAt(int boardIndex)
  {
    return (MPSBoard)boards.get(boardIndex);
  }

  public void clear()
  {
    boards.clear();
  }

  /**
   * Sets the number of the chain.
   * 
   * @param number The chain number.
   */
  public void setNumber(int number)
  {
    this.number = number;
  }

  /**
   * Gets the number of the chain.
   * 
   * @return The chain number.
   */
  public int getNumber()
  {
    return number;
  }
  
  /**
   * Adds the <CODE>Detector</CODE> to the <CODE>MPSChain</CODE>.
   * 
   * @param detector The <CODE>Detector</CODE> to add to the <CODE>MPSChain</CODE>.
   */
  public void addDetector(Detector detector)
  {
    detectors.add(detector);
    if(detector.getChain() != this)
      detector.setChain(this);
    propertyChangeSupport.fireIndexedPropertyChange("detector", detectors.size() - 1, null, detector);
  }

  /**
   * Gets the number of instances of <CODE>Detector</CODE> that have been added to the <CODE>MPSChain</CODE>.
   * 
   * @return The number of instances of <CODE>Detector</CODE> in the <CODE>MPSChain</CODE>.
   */
  public int getDetectorCount()
  {
    return detectors.size();
  }
  
  /**
   * Gets the <CODE>Detector</CODE> at the given index.
   *
   * @param index The index of the <CODE>Detector</CODE> to return.
   * @return The <CODE>Detector</CODE> at the given index.
   */
  public Detector getDetectorAt(int index)
  {
    return detectors.get(index);
  }
  
  /**
   * Adds a listener to the class.
   * 
   * @param listener The <CODE>PropertyChangelistener</CODE> to add to the class.
   */
  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }
  
  /**
   * Adds a listener to the class.
   * 
   * @param propertyName The name of the property for which to listen for changes.
   * @param listener The <CODE>PropertyChangelistener</CODE> to add to the class.
   */
  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
  }
  
  /**
   * Looks for the specific instance of <CODE>Detector</CODE> in the 
   * <CODE>MPSChain</CODE>.
   * 
   * @param detector The <CODE>Detector</CODE> for which to look.
   * @return The index of the <CODE>Detector</CODE>, or <CODE>-1</CODE> if the specific <CODE>Detector</CODE> instance is not in the <CODE>MPSChain</CODE>.
   */
  public int findDetectorInstance(Detector detector)
  {
    int detectorCount = getDetectorCount();
    for(int i=0;i<detectorCount;i++) 
      if(getDetectorAt(i) == detector)
        return i;
    return -1;
  }
}
