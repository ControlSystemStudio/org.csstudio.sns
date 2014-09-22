package org.csstudio.mps.sns.tools.data;

import java.util.ArrayList;

/**
 * Provides a class that represents a record in the AFE_DVC table.
 *
 * @author Chris Fowlkes
 */
public class AFE extends Device
{
  /**
   * Holds the instances of <CODE>SSA</CODE> for the <CODE>AFE</CODE>.
   */
  private ArrayList<SSA> ssaDevices = new ArrayList();
  /**
   * Holds the <CODE>IOC</CODE> for the <CODE>AFE</CODE>.
   */
  private DetectorIOC ioc;
  
  /**
   * Creates a new <CODE>AFE</CODE>.
   */
  public AFE()
  {
  }

  /**
   * Creates a new <CODE>AFE</CODE>.
   * 
   * @param id The ID of the <CODE>AFE</CODE>.
   */
  public AFE(String id)
  {
    super(id);
  }

  /**
   * Gets the number of instances of <CODE>SSA</CODE> that have been added 
   * to the <CODE>AFE</CODE>.
   * 
   * @return
   */
  public int getSSADeviceCount()
  {
    return ssaDevices.size();
  }

  /**
   * Gets the <CODE>SSA</CODE> at the given index.
   * 
   * @param index The index of the <CODE>SSA</CODE> to return.
   * @return The <CODE>SSAIndex</CODE> at the given index.
   */
  public SSA getSSADeviceAt(int index)
  {
    return ssaDevices.get(index);
  }
  
  /**
   * Gets the <CODE>SSA</CODE> with the given ID. If an 
   * <CODE>SSA</CODE> with the given ID has not been added to the 
   * <CODE>SSA</CODE>, <CODE>null</CODE> is returned.
   * 
   * @param id The ID of the <CODE>SSA</CODE> to return.
   * @return The <CODE>SSA</CODE> with the given ID or <CODE>null</CODE> if a matching <CODE>SSA</CODE> is not found.
   */
  public SSA getSSADevice(String id)
  {
    int ssaCount = getSSADeviceCount();
    for(int i=0;i<ssaCount;i++) 
    {
      SSA ssa = getSSADeviceAt(i);
      if(id.equals(ssa.getID()))
        return ssa;
    }
    return null;
  }
  
  /**
   * Adds the <CODE>SSA</CODE> to the <CODE>AFE</CODE>.
   * 
   * @param ssaDevice The <CODE>SSA</CODE> to add to the <CODE>AFE</CODE>.
   */
  public void addSSADevice(SSA ssaDevice)
  {
    if(ssaDevice != null && getSSADevice(ssaDevice.getID()) == null)
    {
      ssaDevices.add(ssaDevice);
      if(ssaDevice.getAFE() != this)
        ssaDevice.setAFE(this);
      fireIndexedPropertyChange("ssaDevices", getSSADeviceCount() - 1, null, ssaDevice);
    }
  }

  /**
   * Sets the <CODE>IOC</CODE> for the <CODE>AFE</CODE>.
   * 
   * @param ioc The <CODE>IOC</CODE> for the <CODE>AFE</CODE>.
   */
  public void setIOC(DetectorIOC ioc)
  {
    DetectorIOC oldValue = getIOC();
    this.ioc = ioc;
    if(ioc.getAFE(getID()) == null)
      ioc.addAFE(this);
    firePropertyChange("ioc", oldValue, ioc);
  }

  /**
   * Gets the <CODE>IOC</CODE> for the <CODE>AFE</CODE>.
   * 
   * @return The <CODE>IOC</CODE> for the <CODE>AFE</CODE>.
   */
  public DetectorIOC getIOC()
  {
    return ioc;
  }
}
