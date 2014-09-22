package org.csstudio.mps.sns.tools.data;
import java.util.ArrayList;

/**
 * Provides a class to hold the data on the PSC_DVC table.
 * 
 * @author Chris Fowlkes
 */
public class PowerSupplyController extends Device 
{
  /**
   * Holds the instances of <CODE>PowerSupllyInterfaces</CODE> that have been 
   * associated with the <CODE>PowerSupplyController</CODE>.
   */
  private ArrayList powerSupplyInterfaces = new ArrayList();

  /**
   * Creates a new <CODE>PowerSupplyCOntroller</CODE>.
   */
  public PowerSupplyController()
  {
  }
  
  /**
   * Creates a new <CODE>PowerSupplyController</CODE> with the given ID.
   * 
   * @param id The ID of the new <CODE>PowerSupplyController</CODE>.
   */
  public PowerSupplyController(String id)
  {
    super(id);
  }

  /**
   * Adds a <CODE>PowerSupplyInterface</CODE> to the 
   * <CODE>PowerSupplyController</CODE>.
   * 
   * @param newPowerSupplyController The <CODE>PowerSupplyInterface</CODE> to add to the <CODE>PowerSupplyController</CODE>.
   */
  public void addPowerSupplyInterface(PowerSupplyInterface newPowerSupplyInterface)
  {
    powerSupplyInterfaces.add(newPowerSupplyInterface);
  }
  
  /**
   * Gets the number of instances of <CODE>PowerSupplyInterface</CODE> that 
   * have been added to the <CODE>PowerSupplyController</CODE>.
   * 
   * @return The number of instances of <CODE>PowerSupplyInterface</CODE> added to the <CODE>PowerSupplyController</CODE>.
   */
  public int getPowerSupplyInterfaceCount()
  {
    return powerSupplyInterfaces.size();
  }
  
  /**
   * Gets the <CODE>PowerSupplyInterface</CODE> at the given index.
   * 
   * @param index The index of the <CODE>PowerSupplyInterface</CODE> to return.
   * @return The <CODE>PowerSupplyInterface</CODE> at the given index.
   */
  public PowerSupplyInterface getPowerSupplyInterfaceAt(int index)
  {
    return (PowerSupplyInterface)powerSupplyInterfaces.get(index);
  }
  
  /**
   * Gets the instance of <CODE>PowerSupplyInterface</CODE> with the given ID.
   * 
   * @param id The ID of the <CODE>PowerSupplyInterface</CODE> to return.
   * @return The <CODE>PowerSupplyInterface</CODE> with the given ID, <CODE>null</CODE> if no mach is found.
   */
  public PowerSupplyInterface getPowerSupplyInterface(String id)
  {
    int interfaceCount = getPowerSupplyInterfaceCount();
    for(int i=0;i<interfaceCount;i++) 
    {
      PowerSupplyInterface currentInterface = getPowerSupplyInterfaceAt(i);
      if(id.equals(currentInterface.getID()))
        return currentInterface;
    }
    return null;
  }
}