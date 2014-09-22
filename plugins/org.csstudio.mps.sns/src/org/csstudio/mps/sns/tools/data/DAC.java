package org.csstudio.mps.sns.tools.data;

import java.util.ArrayList;

/**
 * Holds a record from the DAC_DVC RDB table.
 *
 * @author Chris Fowlkes
 */
public class DAC extends ChannelDevice
{
  /**
   * Holds the instances of <CODE>MPSInterface</CODE> for the <CODE>DAC</CODE>.
   */
  private ArrayList<MPSInterface> mpsInterfaces = new ArrayList();
  /**
   * Holds the <CODE>IPCarrier</CODE> for the <CODE>DAC</CODE>.
   */
  private IPCarrier ipCarrier;
  /**
   * Holds the number of the slot for the <CODE>IPCarrier</CODE>.
   */
  private int ipCarrierSlot = -1;
  
  /**
   * Creates a new <CODE>DAC</CODE>.
   */
  public DAC()
  {
    setChannelCount(16);
  }
  
  /**
   * Creates a new <CODE>DAC</CODE>.
   * 
   * @param id The ID of the <CODE>DAC</CODE>.
   */
  public DAC(String id)
  {
    super(id);
  }

  /**
   * Sets the <CODE>IPCarrier</CODE> for the <CODE>DAC</CODE>.
   * 
   * @param ipCarrier The <CODE>IPCarrier</CODE> for the <CODE>DAC</CODE>.
   * @param ipCarrierSlot The slot number for the <CODE>IPCarrier</CODE>.
   */
  public void setIPCarrier(IPCarrier ipCarrier, int ipCarrierSlot)
  {
    IPCarrier oldIPCarrier = getIPCarrier();
    int oldIPCarrierSlot = getIPCarrierSlot();
    if(oldIPCarrier != ipCarrier || oldIPCarrierSlot != ipCarrierSlot)
    {
      if(ipCarrier != oldIPCarrier)
      {
        this.ipCarrier = ipCarrier;
        firePropertyChange("ipCarrier", oldIPCarrier, ipCarrier);
      }
      if(ipCarrierSlot != oldIPCarrierSlot)
      {
        this.ipCarrierSlot = ipCarrierSlot;
        firePropertyChange("ipCarrierSlot", oldIPCarrierSlot, ipCarrierSlot);
      }
      if(oldIPCarrier != null && oldIPCarrier.getDACAtSlot(oldIPCarrierSlot) == this)
        oldIPCarrier.setDACAtSlot(null, oldIPCarrierSlot);
      if(ipCarrier != null && ipCarrier.getDACAtSlot(ipCarrierSlot) != this)
        ipCarrier.setDACAtSlot(this, ipCarrierSlot);
    }
  }

  /**
   * Sets the <CODE>IPCarrier</CODE> for the <CODE>DAC</CODE>.
   * 
   * @param ipCarrier The <CODE>IPCarrier</CODE> for the <CODE>DAC</CODE>.
   */
  public void setIPCarrier(IPCarrier ipCarrier)
  {
    setIPCarrier(ipCarrier, getIPCarrierSlot());
  }

  /**
   * Gets the <CODE>IPCarrier</CODE> for the <CODE>DAC</CODE>.
   * 
   * @return The <CODE>IPCarrier</CODE> for the <CODE>DAC</CODE>.
   */
  public IPCarrier getIPCarrier()
  {
    return ipCarrier;
  }

  /**
   * Sets the slot for the <CODE>IPCarrier</CODE>.
   * 
   * @param ipCarrierSlot The slot number for the <CODE>IPCarrier</CODE>.
   */
  public void setIPCarrierSlot(int ipCarrierSlot)
  {
    setIPCarrier(getIPCarrier(), ipCarrierSlot);
  }

  /**
   * Gets the number of the slot for the <CODE>IPCarrier</CODE>.
   * 
   * @return The number of the <CODE>IPCarrier</CODE> slot.
   */
  public int getIPCarrierSlot()
  {
    return ipCarrierSlot;
  }

  /**
   * Gets the number of instances of <CODE>MPSInterface</CODE> that have been 
   * added to the <CODE>DAC</CODE>.
   * 
   * @return The number of instances of <CODE>MPSInterface</CODE> that have been added to the <CODE>DAC</CODE>.
   */
  public int getMPSInterfaceCount()
  {
    return mpsInterfaces.size();
  }

  /**
   * Gets the <CODE>MPSInterface</CODE> at the given index.
   * 
   * @param index The index of the <CODE>MPSInterface</CODE> to return.
   * @return The index of the <CODE>MPSInterface</CODE> to return.
   */
  public MPSInterface getMPSInterfaceAt(int index)
  {
    return mpsInterfaces.get(index);
  }
  
  /**
   * Gets the <CODE>MPSInterface</CODE> with the given ID. If an 
   * <CODE>MPSInterface</CODE> with the given ID has not been added to the 
   * <CODE>MPSInterface</CODE>, <CODE>null</CODE> is returned.
   * 
   * @param id The ID of the <CODE>MPSInterface</CODE> to return.
   * @return The <CODE>MPSInterface</CODE> with the given ID or <CODE>null</CODE> if a matching <CODE>MPSInterface</CODE> is not found.
   */
  public MPSInterface getMPSInterface(String id)
  {
    int mpsifCount = getMPSInterfaceCount();
    for(int i=0;i<mpsifCount;i++) 
    {
      MPSInterface mpsif = getMPSInterfaceAt(i);
      if(id.equals(mpsif.getID()))
        return mpsif;
    }
    return null;
  }
  
  /**
   * Adds the <CODE>MPSInterface</CODE> to the <CODE>DAC</CODE>.
   * 
   * @param mpsInterface The <CODE>MPSInterface</CODE> to add to the <CODE>DAC</CODE>.
   */
  public void addMPSInterface(MPSInterface mpsInterface)
  {
    if(mpsInterface != null && getMPSInterface(mpsInterface.getID()) == null)
    {
      mpsInterfaces.add(mpsInterface);
      if(mpsInterface.getDAC() != this)
        mpsInterface.setDAC(this);
      fireIndexedPropertyChange("mpsInterfaces", getMPSInterfaceCount() - 1, null, mpsInterface);
    }
  }
}
