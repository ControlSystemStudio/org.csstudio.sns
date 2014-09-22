package org.csstudio.mps.sns.tools.data;

/**
 * Holds the data for a HVPS device, which is stored in the HVPS_DVC table.
 * 
 * @author Chris Fowlkes
 */
public class HVPSDevice extends ChannelDevice
{
  /**
   * Holds the value for the AM field.
   */
  private String am;
  /**
   * Holds the value for the base address field.
   */
  private String baseAddress;
  /**
   * Holds the value for the channel 0 field.
   */
  private String channel0PLRT;
  /**
   * Holds the value for the channel 1 field.
   */
  private String channel1PLRT;
  /**
   * Holds the alias for the <CODE>HVPSDevice</CODE>.
   */
  private String alias;
  /**
   * Holds the <CODE>IOC</CODE> for the <CODE>HVPSDevice</CODE>.
   */
  private IOC ioc;

  /**
   * Creates a new <CODE>HVPSDevice</CODE>.
   */
  public HVPSDevice()
  {
  }
  
  /**
   * Creates a new <CODE>HVPSDevice</CODE>.
   * 
   * @param id The ID of the <CODE>HVPSDevice</CODE>.
   */
  public HVPSDevice(String id)
  {
    super(id);
  }
  
  /**
   * Sets the AM for the <CODE>HVPSDevice</CODE>.
   * 
   * @param am The AM value for the <CODE>HVPSDevice</CODE>.
   */
  public void setAM(String am)
  {
    String oldValue = this.am;
    this.am = am;
    firePropertyChange("am", oldValue, am);
  }

  /**
   * Gets the AM for the <CODE>HVPSDevice</CODE>.
   * 
   * @return The AM value for the <CODE>HVPSDevice</CODE>.
   */
  public String getAM()
  {
    return am;
  }

  /**
   * Sets the base address for the <CODE>HVPSDevice</CODE>.
   * 
   * @param baseAddress The AM value for the <CODE>HVPSDevice</CODE>.
   */
  public void setBaseAddress(String baseAddress)
  {
    String oldValue = this.baseAddress;
    this.baseAddress = baseAddress;
    firePropertyChange("baseAddress", oldValue, baseAddress);
  }

  /**
   * Gets the base address for the <CODE>HVPSDevice</CODE>.
   * 
   * @return The base address value for the <CODE>HVPSDevice</CODE>.
   */
  public String getBaseAddress()
  {
    return baseAddress;
  }

  /**
   * Sets the channel 0 PLRT for the <CODE>HVPSDevice</CODE>.
   * 
   * @param channel0PLRT The channel 0 PLRT value for the <CODE>HVPSDevice</CODE>.
   */
  public void setChannel0PLRT(String channel0PLRT)
  {
    String oldValue = this.channel0PLRT;
    this.channel0PLRT = channel0PLRT;
    firePropertyChange("channel0PLRT", oldValue, channel0PLRT);
  }

  /**
   * Sets the channel 0 PLRT for the <CODE>HVPSDevice</CODE>.
   * 
   * @return The channel 0 PLRT value for the <CODE>HVPSDevice</CODE>.
   */
  public String getChannel0PLRT()
  {
    return channel0PLRT;
  }

  /**
   * Sets the channel 1 PLRT for the <CODE>HVPSDevice</CODE>.
   * 
   * @param channel1PLRT The channel 1 PLRT value for the <CODE>HVPSDevice</CODE>.
   */
  public void setChannel1PLRT(String channel1PLRT)
  {
    String oldValue = this.channel1PLRT;
    this.channel1PLRT = channel1PLRT;
    firePropertyChange("channel1PLRT", oldValue, channel1PLRT);
  }

  /**
   * Sets the channel 1 PLRT for the <CODE>HVPSDevice</CODE>.
   * 
   * @return The channel 1 PLRT value for the <CODE>HVPSDevice</CODE>.
   */
  public String getChannel1PLRT()
  {
    return channel1PLRT;
  }

  /**
   * Sets alias for the <CODE>HVPSDevice</CODE>.
   * 
   * @param alias The alias for the <CODE>HVPSDevice</CODE>.
   */
  public void setAlias(String alias)
  {
    String oldValue = this.alias;
    this.alias = alias;
    firePropertyChange("alias", oldValue, alias);
  }

  /**
   * Gets alias for the <CODE>HVPSDevice</CODE>.
   * 
   * @return The alias for the <CODE>HVPSDevice</CODE>.
   */
  public String getAlias()
  {
    return alias;
  }

  /**
   * Sets the <CODE>IOC</CODE> for the <CODE>HVPSDevice</CODE>.
   * 
   * @param ioc The <CODE>IOC</CODE> or the <CODE>HVPSDevice</CODE>.
   */
  public void setIOC(IOC ioc)
  {
    this.ioc = ioc;
  }

  /**
   * Gets the <CODE>IOC</CODE> for the <CODE>HVPSDevice</CODE>.
   * 
   * @return The <CODE>IOC</CODE> for the <CODE>HVPSDevice</CODE>.
   */
  public IOC getIOC()
  {
    return ioc;
  }
}
