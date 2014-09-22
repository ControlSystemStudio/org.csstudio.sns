package org.csstudio.mps.sns.tools.data;

/**
 * Holds a record from the MPS_INTFC_DVC RDB table.
 * 
 * @author Chris Fowlkes
 */
public class MPSInterface extends ChannelDevice
{
  /**
   * holds the <CODE>IOC</CODE> for the <CODE>MPSInterface</CODE>.
   */
  private DetectorIOC ioc;
  /**
   * Holds the <CODE>IPCarrier</CODE> for the <CODE>MPSInterface</CODE>.
   */
  private DAC dac;
//  /**
//   * Holds the MIOC to which the <CODE>MPSInterface</CODE> is connected.
//   */
//  private IOC mioc;
//  /**
//   * The MIOC channel to which the <CODE>MPSInterface</CODE> is connected.
//   */
//  private int miocChannel;
  
  /**
   * Creates a new <CODE>MPSInterface</CODE>.
   */
  public MPSInterface()
  {
    super();
    setChannelCount(16);
  }
  
  /**
   * Creates a new <CODE>MPSInterface</CODE>.
   * 
   * @param id The ID of the <CODE>MPSInterface</CODE>.
   */
  public MPSInterface(String id)
  {
    super(id);
    setChannelCount(16);
  }

  /**
   * Sets the <CODE>DetectorIOC</CODE> for the <CODE>MPSInterface</CODE>.
   * 
   * @param ioc The <CODE>DetectorIOC</CODE> for the <CODE>MPSInterface</CODE>.
   */
  public void setIOC(DetectorIOC ioc)
  {
    DetectorIOC oldValue = getIOC();
    if(oldValue != ioc)
    {
      this.ioc = ioc;
      int index = getIOCIndex();
      if(oldValue != null && oldValue.getMPSInterfaceAtIndex(index) == this)
        oldValue.setMPSInterfaceAtIndex(index, null);
      if(ioc != null && ioc.getMPSInterfaceAtIndex(index) != this)
        ioc.setMPSInterfaceAtIndex(index, this);
      firePropertyChange("ioc", oldValue, ioc);
    }
  }
  
  /**
   * Gets the index in the <CODE>DetectorIOC</CODE> that the 
   * <CODE>MPSInterface</CODE> should be at.
   * 
   * @return The index the <CODE>MPSInterface</CODE> will be at in the <CODE>DetectorIOC</CODE>.
   */
  public int getIOCIndex()
  {
    String id = getID();
    String index = "";
    for(int i=id.length()-1;i>=0;i--)
    {
      char character = id.charAt(i);
      if(Character.isDigit(character))
        index = character + index;
      else
        break;
    }
    return Integer.parseInt(index);
  }

  /**
   * Gets the <CODE>DetectorIOC</CODE> for the <CODE>MPSInterface</CODE>.
   * 
   * @return The <CODE>DetectorIOC</CODE> for the <CODE>MPSInterface</CODE>.
   */
  public DetectorIOC getIOC()
  {
    return ioc;
  }

  /**
   * Gets the <CODE>DAC</CODE> for the <CODE>MPSInterface</CODE>.
   * 
   * @return The <CODE>DAC</CODE> for the <CODE>MPSInterface</CODE>.
   */
  public DAC getDAC()
  {
    return dac;
  }

  /**
   * Sets the <CODE>DAC</CODE> and slot number for the 
   * <CODE>MPSInterface</CODE>.
   * 
   * @param dac The <CODE>DAC</CODE> for the <CODE>MPSInterface</CODE>.
   */
  public void setDAC(DAC dac)
  {
    DAC oldDAC = getDAC();
    if(dac != oldDAC)
    {
      this.dac = dac;
      if(dac.getMPSInterface(getID()) == null)
        dac.addMPSInterface(this);
      firePropertyChange("dac", oldDAC, dac);
//      if(oldDAC != null && (oldDAC.getMPSInterface() == this))
//        oldCarrier.setMPSInterface(null);
//      if(ipCarrier != null && ipCarrier.getMPSInterface() != this)
//        ipCarrier.setMPSInterface(this);
    }
  }
  
  /**
   * Converts the SSA channel to the channel number for the 
   * <CODE>MPSInterface</CODE>.
   * 
   * @param ssaChannel The SSA channel for which to find the MPS channel.
   */
  static public int getMPSInterfaceChannel(int ssaChannel)
  {
    int channel = ssaChannel;
    while(channel > 15)
      channel -= 16;
    return channel;
  }
  
  /**
   * Maps the channels of the <CODE>SSA</CODE> to the <CODE>MPSInterface</CODE>.
   * The the values returned will be the <CODE>SSA</CODE> channel that 
   * corresponds to the channel on the <CODE>MPSInterface</CODE>, where the 
   * <CODE>MPSInterface</CODE> channel is the index of the array.
   * 
   * @return The channels for the <CODE>SSA</CODE>, mapped to the <CODE>MPSInterface</CODE>.
   */
  public int[] mapChannels()
  {
    int[] channels = new int[getChannelCount()];
    int ssaChannelCount = 32;
    int iocIndex = getIOCIndex();
    for(int i=0;i<ssaChannelCount;i++) 
      if(DetectorIOC.getMPSInterfaceIndexForChannel(i) == iocIndex)
        channels[MPSInterface.getMPSInterfaceChannel(i)] = i;
    return channels;
  }

//  /**
//   * Sets the MIOC to which the <CODE>MPSInterface</CODE> is 
//   * connected.
//   * 
//   * @param mioc The MIOC to whcih the <CODE>MPSInterface</CODE> is connected.
//   */
//  public void setMIOC(IOC mioc)
//  {
//    setMIOC(mioc, getMIOCChannel());
//  }
//
//  /**
//   * Gets the MIOC to which the <CODE>MPSInterface</CODE> is 
//   * connected.
//   * 
//   * @return The MIOC to whcih the <CODE>MPSInterface</CODE> is  connected.
//   */
//  public IOC getMIOC()
//  {
//    return mioc;
//  }
//
//  /**
//   * Sets the MIOC channel to which the <CODE>MPSInterface</CODE> is connected.
//   * 
//   * @param miocChannel The MIOC channel to which the <CODE>MPSInterface</CODE> is connected.
//   */
//  public void setMIOCChannel(int miocChannel)
//  {
//    setMIOC(getMIOC(), miocChannel);
//  }
//
//  /**
//   * Gets the MIOC channel to which the <CODE>MPSInterface</CODE> is connected.
//   * 
//   * @return The MIOC channel to which the <CODE>MPSInterface</CODE> is connected.
//   */
//  public int getMIOCChannel()
//  {
//    return miocChannel;
//  }
//
//  /**
//   * Sets the MIOC and channel for the <CODE>MPSInterface</CODE>.
//   * 
//   * @param mioc The MIOC for the <CODE>MPSInterface</CODE>.
//   * @param miocChannel The MIOC channel for the <CODE>MPSInterface</CODE>.
//   */
//  public void setMIOC(IOC mioc, int miocChannel)
//  {
//    IOC oldMIOC = this.mioc;
//    int oldMIOCChannel = this.miocChannel;
//    if(mioc != oldMIOC || miocChannel != oldMIOCChannel)
//    {
//      if(mioc != oldMIOC)
//      {
//        this.mioc = mioc;
//        markFieldChanged("mioc");
//        firePropertyChange("mioc", oldMIOC, mioc);
//      }
//      if(miocChannel != oldMIOCChannel)
//      {
//        this.miocChannel = miocChannel;
//        markFieldChanged("miocChannel");
//        firePropertyChange("miocChannel", oldMIOCChannel, miocChannel);
//      }
//    }
//  }
}
