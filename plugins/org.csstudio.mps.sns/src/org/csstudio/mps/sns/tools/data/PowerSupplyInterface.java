package org.csstudio.mps.sns.tools.data;
import java.util.ArrayList;

/**
 * Provides a class to hold the data on the PSI_DVC table.
 * 
 * @author Chris Fowlkes
 */
public class PowerSupplyInterface extends Device 
{
  private ArrayList powerSupplies = new ArrayList();
  /**
   * Creates a new <CODE>PowerSupplyInterface</CODE>.
   */
  public PowerSupplyInterface()
  {
  }
  
  /**
   * Creates a new <CODE>PowerSupplyInterface</CODE> with the given ID.
   * 
   * @param id The ID of the new <CODE>PowerSupplyInterface</CODE>.
   */
  public PowerSupplyInterface(String id)
  {
    super(id);
  }

  /**
   * Adds a <CODE>PowerSupply</CODE> to the <CODE>PowerSupplyInterface</CODE>.
   * 
   * @param newPowerSupplyController The <CODE>PowerSupply</CODE> to add to the <CODE>PowerSupplyInterface</CODE>.
   */
  public void addPowerSupply(PowerSupply newPowerSupply)
  {
    powerSupplies.add(newPowerSupply);
  }
  
  /**
   * Gets the number of instances of <CODE>PowerSupply</CODE> that have been 
   * added to the <CODE>PowerSupplyInterface</CODE>.
   * 
   * @return The number of instances of <CODE>PowerSupply</CODE> added to the <CODE>PowerSupplyInterface</CODE>.
   */
  public int getPowerSupplyCount()
  {
    return powerSupplies.size();
  }
  
  /**
   * Gets the <CODE>PowerSupply</CODE> at the given index.
   * 
   * @param index The index of the <CODE>PowerSupply</CODE> to return.
   * @return The <CODE>PowerSupply</CODE> at the given index.
   */
  public PowerSupply getPowerSupplyAt(int index)
  {
    return (PowerSupply)powerSupplies.get(index);
  }
  
  /**
   * Gets the instance of <CODE>PowerSupply</CODE> with the given ID.
   * 
   * @param id The ID of the <CODE>PowerSupply</CODE> to return.
   * @return The <CODE>PowerSupply</CODE> with the given ID, <CODE>null</CODE> if no mach is found.
   */
  public PowerSupply getPowerSupply(String id)
  {
    int supplyCount = getPowerSupplyCount();
    for(int i=0;i<supplyCount;i++) 
    {
      PowerSupply currentSupply = getPowerSupplyAt(i);
      if(id.equals(currentSupply.getID()))
        return currentSupply;
    }
    return null;
  }
}