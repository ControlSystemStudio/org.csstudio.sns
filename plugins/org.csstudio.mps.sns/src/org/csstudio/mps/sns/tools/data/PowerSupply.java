package org.csstudio.mps.sns.tools.data;
import java.util.ArrayList;

import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.Magnet;

/**
 * This class holds the data represented in the PS_DVC table.
 * 
 * @author Chris Fowlkes
 */
public class PowerSupply extends Device 
{
  /**
   * Holds the instances of <CODE>Magnet</CODE> associated with the 
   * <CODE>PowerSupply</CODE>.
   */
  private ArrayList magnets = new ArrayList();
  
  /**
   * Creates a new <CODE>PowerSupply</CODE>.
   */
  public PowerSupply()
  {
    super();
  }
  
  /**
   * Creates a new <CODE>PowerSupply</CODE>.
   * 
   * @param id The ID of the <CODE>PowerSupply</CODE>.
   */
  public PowerSupply(String id)
  {
    super(id);
  }
  
  /**
   * Creates a new <CODE>PowerSupply</CODE>.
   * 
   * @param id The ID of the <CODE>PowerSupply</CODE>.
   * @param description The description of the <CODE>PowerSupply</CODE>.
   */
  public PowerSupply(String id, String description)
  {
    super(id, description);
  }
  
  /**
   * Adds the given <CODE>Magnet</CODE> to the <CODE>PowerSupply</CODE>.
   * 
   * @param newMagnet The <CODE>Magnet</CODE> to add.
   */
  public void addMagnet(Magnet newMagnet)
  {
    magnets.add(newMagnet);
    if(newMagnet.getPowerSupply() != this)
      newMagnet.setPowerSupply(this);
  }

  /**
   * Gets the number of instances of <CODE>Magnet</CODE> that have been added.
   * 
   * @return The number of instances of <CODE>Magnet</CODE> that have been added.
   */
  public int getMagnetCount()
  {
    return magnets.size();
  }

  /**
   * Gets the instance of <CODE>Magnet</CODE> at the given index.
   * 
   * @param index The index of the <CODE>Magnet</CODE> to return.
   * @return The <CODE>Magnet</CODE> at the given index.
   */
  public Magnet getMagnetAt(int index)
  {
    return (Magnet)magnets.get(index);
  }

  /**
   * Gets the <CODE>Magnet</CODE> with the given ID. If no <CODE>Magnet</CODE>
   * with the ID has been added, <CODE>null</CODE> is returned.
   * 
   * @param id The ID of the <CODE>Magnet</CODE> to return.
   * @return The <CODE>Magnet</CODE> with the given ID, or <CODE>null</CODE> if none was found.
   */
  public Magnet getMagnet(String id)
  {
    int magnetCount = getMagnetCount();
    for(int i=0;i<magnetCount;i++) 
    {
      Magnet currentMagnet = getMagnetAt(i);
      if(currentMagnet.getID().equals(id))
        return currentMagnet;
    }
    return null;
  }
}