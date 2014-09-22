package org.csstudio.mps.sns.tools.data;

import java.util.ArrayList;

/**
 * Provides a <CODE>IOC</CODE> class for the BLM module.
 *
 * @author Chris Fowlkes
 */
public class DetectorIOC extends IOC
{
  /**
   * Holds the <CODE>IPCarrier</CODE> for the <CODE>DetectorIOC</CODE>.
   */
  private IPCarrier ipCarrier;
  /**
   * Holds the instances of <CODE>MPSInterface</CODE> for the 
   * <CODE>DetectorIOC</CODE>.
   */
  private MPSInterface[] mpsInterfaces = new MPSInterface[2];
  /**
   * Holds the instances of <CODE>MPSInterface</CODE> associated with the 
   * <CODE>DetectorIOC</CODE>.
   */
  private ArrayList<AFE> afes = new ArrayList();

  /**
   * Creates a new <CODE>DetectorIOC</CODE>.
   */
  public DetectorIOC()
  {
  }
  
  /**
   * Creates a new <CODE>DetectorIOC</CODE> with the given ID.
   * 
   * @param id The ID of the <CODE>DetectorIOC</CODE>.
   */
  public DetectorIOC(String id)
  {
    super(id);
  }

  /**
   * Sets the <CODE>IPCarrier</CODE> for the <CODE>DetectorIOC</CODE>.
   * 
   * @param ipCarrier The <CODE>IPCarrier</CODE> for the <CODE>DetectorIOC</CODE>.
   */
  public void setIPCarrier(IPCarrier ipCarrier)
  {
    IPCarrier oldValue = getIPCarrier();
    if(oldValue != ipCarrier)
    {
      this.ipCarrier = ipCarrier;
      if(oldValue != null && oldValue.getIOC() == this)
        oldValue.setIOC(null);
      if(ipCarrier != null && ipCarrier.getIOC() != this)
        ipCarrier.setIOC(this);
      firePropertyChange("ipCarrier", oldValue, ipCarrier);
    }
  }

  /**
   * Gets the <CODE>IPCarrier</CODE> for the <CODE>DetectorIOC</CODE>.
   * 
   * @return The <CODE>IPCarrier</CODE> for the <CODE>DetectorIOC</CODE>.
   */
  public IPCarrier getIPCarrier()
  {
    return ipCarrier;
  }

  /**
   * Sets the <CODE>MPSInterface</CODE> for the <CODE>DetectorIOC</CODE> at the 
   * given index.
   * 
   * @param index The index of the <CODE>MPSInterface</CODE> to change.
   * @param mpsInterface The first <CODE>MPSInterface</CODE> for the <CODE>DetectorIOC</CODE>.
   */
  public void setMPSInterfaceAtIndex(int index, MPSInterface mpsInterface)
  {
    MPSInterface oldValue = getMPSInterfaceAtIndex(index);
    if(oldValue != mpsInterface)
    {
      this.mpsInterfaces[index - 1] = mpsInterface;
      if(oldValue != null && oldValue.getIOC() == this)
        oldValue.setIOC(null);
      if(mpsInterface != null && mpsInterface.getIOC() != this)
        mpsInterface.setIOC(this);
      fireIndexedPropertyChange("mpsInterfaces", index, oldValue, mpsInterface);
    }
  }

  /**
   * Gets the first <CODE>MPSInterface</CODE> for the <CODE>DetectorIOC</CODE>.
   * The lowest valid value for the index is <CODE>1</CODE>.
   * 
   * @param index The index of the <CODE>MPSInterface</CODE> to return.
   * @return The <CODE>MPSinterface</CODE> at the given index for the <CODE>DetectorIOC</CODE>.
   */
  public MPSInterface getMPSInterfaceAtIndex(int index)
  {
    return mpsInterfaces[index - 1];
  }
  
  /**
   * Determines the appropriate <CODE>MPSInterface</CODE> for the incoming 
   * channel.
   * 
   * @param channel The channel for which to return the <CODE>MPSInterface</CODE>.
   * @return The <CODE>MPSInterface</CODE> for the given channel.
   */
  public MPSInterface getMPSInterfaceForChannel(int channel)
  {
    return getMPSInterfaceAtIndex(getMPSInterfaceIndexForChannel(channel));
  }
  
  /**
   * Determines the appropriate <CODE>MPSInterface</CODE> index for the incoming 
   * channel.
   * 
   * @param channel The channel for which to return the <CODE>MPSInterface</CODE>.
   * @return The <CODE>MPSInterface</CODE> index for the given channel.
   */
  static public int getMPSInterfaceIndexForChannel(int channel)
  {
    return (int)Math.floor(channel / 16) + 1;
  }

  /**
   * Gets the number of instances of <CODE>AFE</CODE> that have been added 
   * to the <CODE>DetectorIOC</CODE>.
   * 
   * @return The number of instances of <CODE>AFE</CODE> for the <CODE>DetectorIOC</CODE>.
   */
  public int getAFECount()
  {
    return afes.size();
  }

  /**
   * Gets the <CODE>AFE</CODE> at the given index.
   * 
   * @param index The index of the <CODE>AFE</CODE> to return.
   * @return The <CODE>AFE</CODE> at the given index.
   */
  public AFE getAFEAt(int index)
  {
    return afes.get(index);
  }
  
  /**
   * Gets the <CODE>AFE</CODE> with the given ID. If an  <CODE>AFE</CODE> with 
   * the given ID has not been added to the <CODE>AFE</CODE>, <CODE>null</CODE> 
   * is returned.
   * 
   * @param id The ID of the <CODE>AFE</CODE> to return.
   * @return The <CODE>AFE</CODE> with the given ID or <CODE>null</CODE> if a matching <CODE>AFE</CODE> is not found.
   */
  public AFE getAFE(String id)
  {
    int afeCount = getAFECount();
    for(int i=0;i<afeCount;i++) 
    {
      AFE afe = getAFEAt(i);
      if(id.equals(afe.getID()))
        return afe;
    }
    return null;
  }
  
  /**
   * Adds the <CODE>AFE</CODE> to the <CODE>DetectorIOC</CODE>.
   * 
   * @param afe The <CODE>AFE</CODE> to add to the <CODE>DetectorIOC</CODE>.
   */
  public void addAFE(AFE afe)
  {
    if(afe != null && getAFE(afe.getID()) == null)
    {
      afes.add(afe);
      if(afe.getIOC() != this)
        afe.setIOC(this);
      fireIndexedPropertyChange("afes", getAFECount() - 1, null, afe);
    }
  }
}
