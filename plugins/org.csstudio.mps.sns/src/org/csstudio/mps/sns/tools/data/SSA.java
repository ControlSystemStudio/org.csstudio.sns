package org.csstudio.mps.sns.tools.data;

/**
 * Provides a class that represents a record in the SSA_DVC table.
 * 
 * @author Chris Fowlkes
 */
public class SSA extends ChannelDevice
{
  /**
   * Holds the <CODE>AFE</CODE> for the <CODE>SSA</CODE>.
   */
  private AFE afeDevice;
//  private MPSInterface mpsInterface;
  
  /**
   * Creates a <CODE>SSA</CODE>.
   */
  public SSA()
  {
  }

  /**
   * Creates a <CODE>SSA</CODE>.
   * 
   * @param id The ID of the <CODE>SSA</CODE>.
   */
  public SSA(String id)
  {
    super(id);
  }

  /**
   * Gets the number of channels for the <CODE>ChannelDevice</CODE>.
   * <CODE>32</CODE> by default.
   *
   * @return The number of channels for the <CODE>ChannelDevice</CODE>.
   */
  public int getChannelCount()
  {
    return super.getChannelCount();
  }

  /**
   * Assigns the given <CODE>Device</CODE> to the given channel.
   *
   * @param channel The channel to which to assign the <CODE>Device</CODE>.
   * @param device The <CODE>Detector</CODE> to assign to the channel.
   */
  public void setDeviceAtChannel(int channel, Detector device)
  {
    super.setDeviceAtChannel(channel, device);
    if(device != null && device.getSSA() != this)
      device.setSSA(this, channel);
  }

  /**
   * Sets the <CODE>AFE</CODE> for the <CODE>SSA</CODE>.
   * 
   * @param afeDevice The <CODE>AFE</CODE> for the <CODE>SSA</CODE>.
   */
  public void setAFE(AFE afeDevice)
  {
    AFE oldValue = getAFE();
    this.afeDevice = afeDevice;
    if(afeDevice.getSSADevice(getID()) == null)
      afeDevice.addSSADevice(this);
    firePropertyChange("afe", oldValue, afeDevice);
  }

  /**
   * Gets the <CODE>AFE</CODE> for the <CODE>SSA</CODE>.
   * 
   * @return The <CODE>AFE</CODE> for the <CODE>SSA</CODE>.
   */
  public AFE getAFE()
  {
    return afeDevice;
  }

//  /**
//   * Sets the <CODE>MPSinterface</CODE> for the <CODE>SSA</CODE>.
//   * 
//   * @param mpsInterface The <CODE>MPSInterface</CODE> for the <CODE>SSA</CODE>.
//   */
//  public void setMPSInterface(MPSInterface mpsInterface)
//  {
//    MPSInterface oldValue = getMPSInterface();
//    this.mpsInterface = mpsInterface;
//    firePropertyChange("mpsInterface", oldValue, mpsInterface);
//  }
//
//  /**
//   * Gets the <CODE>MPSInterface</CODE> for the <CODE>MPSInterface</CODE>.
//   * 
//   * @return The <CODE>MPSInterface</CODE> for the <CODE>SSA</CODE>.
//   */
//  public MPSInterface getMPSInterface()
//  {
//    return mpsInterface;
//  }
}
