package org.csstudio.mps.sns.tools.data;

/**
 * Holds a record from the <CODE>IPCarrier</CODE> RDB table.
 * 
 * @author Chris Fowlkes
 */
public class IPCarrier extends Device
{
  /**
   * Holds the <CODE>DetectorIOC</CODE> for the <CODE>IPCarrier</CODE>.
   */
  private DetectorIOC ioc;
  private int slotCount = 2;
  /**
   * Holds the instances of <CODE>DAC</CODE> in slots of the 
   * <CODE>IPCarrier</CODE>. Each <CODE>IPCarrier</CODE> has two slots.
   */
  private DAC[] dacs = new DAC[slotCount];
//  /**
//   * Holds the <CODE>MPSInterface</CODE> for the <CODE>IPCarrier</CODE>.
//   */
//  private MPSInterface mpsInterface;
  
  /**
   * Creates a new <CODE>IPCarrier</CODE>.
   */
  public IPCarrier()
  {
  }
  
  /**
   * Creates a new <CODE>IPCarrier</CODE>.
   * 
   * @param id The ID of the <CODE>IPCarrier</CODE>.
   */
  public IPCarrier(String id)
  {
    super(id);
  }

  /**
   * Adds the goven <CODE>DAC</CODE> to the given slot of the 
   * <CODE>IPCarrier</CODE>.
   * 
   * @param dac The <CODE>DAC</CODE> to add to the <CODE>IPCarrier</CODE>.
   * @param slot The slot to which to add the <CODE>DAC</CODE>.
   */
  public void setDACAtSlot(DAC dac, int slot)
  {
    DAC oldValue = getDACAtSlot(slot);
    if(oldValue != dac)
    {
      dacs[slot] = dac;
      fireIndexedPropertyChange("dacAtSlot", slot, oldValue, dac);
      if(oldValue != null && (oldValue.getIPCarrier() == this && oldValue.getIPCarrierSlot() == slot))
        oldValue.setIPCarrier(null, -1);
      if(dac != null && (dac.getIPCarrier() != this || dac.getIPCarrierSlot() != slot))
        dac.setIPCarrier(this, slot);
    }
  }
  
  /**
   * Gets the <CODE>DAC</CODE> in the given slot.
   * 
   * @param slot The slot of the <CODE>DAC</CODE> to return.
   * @return The <CODE>DAC</CODE> in the given slot.
   */
  public DAC getDACAtSlot(int slot)
  {
    return dacs[slot];
  }

//  /**
//   * Sets the <CODE>MPSInterface</CODE> for the <CODE>IPCarrier</CODE>.
//   * 
//   * @param mpsInterface The <CODE>MPSInterface</CODE> for the <CODE>IPCarrier</CODE>.
//   */
//  public void setMPSInterface(MPSInterface mpsInterface)
//  {
//    MPSInterface oldValue = getMPSInterface();
//    if(oldValue != mpsInterface)
//    {
//      this.mpsInterface = mpsInterface;
//      firePropertyChange("mpsInterface", oldValue, mpsInterface);
//      if(oldValue != null && oldValue.getIPCarrier() == this)
//        oldValue.setIPCarrier(null);
//      if(mpsInterface != null && mpsInterface.getIPCarrier() != this)
//        mpsInterface.setIPCarrier(this);
//    }
//  }
//
//  /**
//   * Gets the <CODE>MPSInterface</CODE> for the <CODE>IPCarrier</CODE>.
//   * 
//   * @return The <CODE>MPSInterface</CODE> for the <CODE>IPCarrier</CODE>.
//   */
//  public MPSInterface getMPSInterface()
//  {
//    return mpsInterface;
//  }
  
  /**
   * Returns the number of slots in the <CODE>IPCarrier</CODE>.
   * 
   * @return The number of slots in the <CODE>IPCarrier</CODE>.
   */
  public int getSlotCount()
  {
    return slotCount;
  }

  /**
   * Sets the <CODE>DetectorIOC</CODE> for the <CODE>IPCarrier</CODE>.
   * 
   * @param ioc The <CODE>DetectorIOC</CODE> for the <CODE>IPCarrier</CODE>.
   */
  public void setIOC(DetectorIOC ioc)
  {
    DetectorIOC oldValue = getIOC();
    if(oldValue != ioc)
    {
      this.ioc = ioc;
      if(oldValue != null && oldValue.getIPCarrier() == this)
        oldValue.setIPCarrier(null);
      if(ioc != null && ioc.getIPCarrier() != this)
        ioc.setIPCarrier(this);
      firePropertyChange("ioc", oldValue, ioc);
    }
  }

  /**
   * Gets the <CODE>DetectorIOC</CODE> for the <CODE>IPCarrier</CODE>.
   * 
   * @return The <CODE>DetectorIOC</CODE> for the <CODE>IPCarrier</CODE>.
   */
  public DetectorIOC getIOC()
  {
    return ioc;
  }
}
