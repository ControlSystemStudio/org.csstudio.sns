package org.csstudio.mps.sns.tools.data;

/**
 * Holds the data for items in the BLM table.
 * 
 * @author Chris Fowlkes
 */
public class Detector extends Device
{
  /**
   * Holds the SSA <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  private SSA ssa;
  /**
   * Holds the channel for the SSA <CODE>Device</CODE> for the 
   * <CODE>Detector</CODE>.
   */
  private int ssaChannel = -1;
//  /**
//   * Holds the AFE <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   */
//  private Device afe;
  /**
   * Holds the HVPS <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  private HVPSDevice hvps;
  /**
   * Holds the channel for the HVPS <CODE>Device</CODE> for the 
   * <CODE>Detector</CODE>.
   */
  private int hvpsChannel = -1;
  /**
   * Holds the ADC slow <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  private ADC adcSlow;
  /**
   * Holds the number for the ADC slow <CODE>Device</CODE> for the 
   * <CODE>Detector</CODE>.
   */
  private int adcSlowNumber = -1;
  /**
   * Holds the ADC fast <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  private ADC adcFast;
  /**
   * Holds the number for the ADC fast <CODE>Device</CODE> for the 
   * <CODE>Detector</CODE>.
   */
  private int adcFastNumber = -1;
  /**
   * Holds the MPSIF <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  private MPSInterface mpsif;
//  /**
//   * Holds the IPCARR <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   */
//  private Device ipCARR;
//  /**
//   * Holds the slot for the IPCARR <CODE>Device</CODE> for the 
//   * <CODE>Detector</CODE>.
//   */
//  private int ipCARRSlot;
//  /**
//   * Holds the DAC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   */
//  private Device dac;
  /**
   * Holds the MIOC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  private Device mioc;
  /**
   * Holds the MIOC channel.
   */
  private int miocChannel = -1;
  /**
   * Holds the <CODE>MPSChain</CODE> to which the <CODE>Detector</CODE> belongs.
   */
  private MPSChain chain;
  
  /**
   * Creates a new <CODE>Detector</CODE>.
   */
  public Detector()
  {
  }

  /**
   * Creates a new <CODE>Detector</CODE> with the given ID.
   * 
   * @param id The ID of the <CODE>Detector</CODE>.
   */
  public Detector(String id)
  {
    super(id);
  }

  /**
   * Sets the <CODE>SSA</CODE> for the <CODE>Detector</CODE>.
   * 
   * @param ssa The <CODE>SSA</CODE> for the <CODE>Detector</CODE>.
   */
  public void setSSA(SSA ssa)
  {
    setSSA(ssa, getSSAChannel());
  }

  /**
   * Sets the <CODE>SSA</CODE> for the <CODE>Detector</CODE>.
   * 
   * @param ssa The <CODE>SSA</CODE> for the <CODE>Detector</CODE>.
   * @param ssaChannel The channel for the <CODE>SSA</CODE> for the <CODE>Detector</CODE>.
   */
  public void setSSA(SSA ssa, int ssaChannel)
  {
    SSA oldSSA = this.ssa;
    int oldSSAChannel = this.ssaChannel;
    if(ssa != oldSSA || ssaChannel != oldSSAChannel)
    {
      if(ssa != oldSSA)
      {
        this.ssa = ssa;
        markFieldChanged("ssa");
        firePropertyChange("ssa", oldSSA, ssa);
      }
      if(ssaChannel != oldSSAChannel)
      {
        this.ssaChannel = ssaChannel;
        markFieldChanged("ssaChannel");
        firePropertyChange("ssaChannel", oldSSAChannel, ssaChannel);
      }
      updateDevicesChannel(oldSSA, oldSSAChannel, ssa, ssaChannel);
    }
  }

  /**
   * Updates the <CODE>Device</CODE> for the given instances of 
   * <CODE>ChannelDevice</CODE>. It removes <CODE>this</CODE> from the old 
   * <CODE>ChannelDevice</CODE> and adds it to the new 
   * <CODE>ChannelDevice</CODE>.
   * 
   * @param oldDevice The old <CODE>ChannelDevice</CODE>.
   * @param oldChannel The old channel number.
   * @param newDevice The new <CODE>ChannelDevice</CODE>.
   * @param newChannel The new channel number.
   */
  private void updateDevicesChannel(ChannelDevice oldDevice, int oldChannel, ChannelDevice newDevice, int newChannel)
  {
    if(oldDevice != null && oldChannel >= 0 && oldDevice.getDeviceAtChannel(oldChannel) == this)
      oldDevice.setDeviceAtChannel(oldChannel, null);
    if(newDevice != null && newChannel >= 0 && newDevice.getDeviceAtChannel(newChannel) != this)
      newDevice.setDeviceAtChannel(newChannel, this);
  }

  /**
   * Gets the <CODE>SSA</CODE> for the <CODE>Detector</CODE>.
   * 
   * @return The <CODE>SSA</CODE> for the <CODE>Detector</CODE>.
   */
  public SSA getSSA()
  {
    return ssa;
  }

  /**
   * Sets the SSA channel for the <CODE>Detector</CODE>.
   * 
   * @param ssaChannel The channel for the SSA <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setSSAChannel(int ssaChannel)
  {
    setSSA(getSSA(), ssaChannel);
  }

  /**
   * Gets the SSA channel for the <CODE>Detector</CODE>.
   * 
   * @return The channel for the SSA <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public int getSSAChannel()
  {
    return ssaChannel;
  }

//  /**
//   * Sets the AFE <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   * 
//   * @param afe The AFE <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   */
//  public void setAFE(Device afe)
//  {
//    Device oldValue = this.afe;
//    if(afe != oldValue)
//    {
//      this.afe = afe;
//      markFieldChanged("afe");
//      firePropertyChange("afe", oldValue, afe);
//    }
//  }
//  
//  /**
//   * Sets the AFE <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   * 
//   * @return The AFE <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   */
//  public Device getAFE()
//  {
//    return afe;
//  }

  /**
   * Sets the HVPS <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @param hvps The HVPS <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setHVPS(HVPSDevice hvps)
  {
    setHVPS(hvps, getHVPSChannel());
  }

  /**
   * Sets the HVPS <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @param hvps The HVPS <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * @param hvpsChannel The channel for the HVPS <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setHVPS(HVPSDevice hvps, int hvpsChannel)
  {
    HVPSDevice oldHVPS = this.hvps;
    int oldHVPSChannel = this.hvpsChannel;
    if(hvps != oldHVPS || hvpsChannel != oldHVPSChannel)
    {
      if(hvps != oldHVPS)
      {
        this.hvps = hvps;
        markFieldChanged("hvps");
        firePropertyChange("hvps", oldHVPS, hvps);
      }
      if(hvpsChannel != oldHVPSChannel)
      {
        this.hvpsChannel = hvpsChannel;
        markFieldChanged("hvpsChannel");
        firePropertyChange("hvpsChannel", oldHVPSChannel, hvpsChannel);
      }
      updateDevicesChannel(oldHVPS, oldHVPSChannel, hvps, hvpsChannel);
    }
  }

  /**
   * Gets the HVPS <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @return The HVPS <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public HVPSDevice getHVPS()
  {
    return hvps;
  }

  /**
   * Sets the channel for the HVPS <CODE>Device</CODE> for the 
   * <CODE>Detector</CODE>.
   * 
   * @param hvpsChannel The channel for the HVPS <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setHVPSChannel(int hvpsChannel)
  {
    setHVPS(getHVPS(), hvpsChannel);
  }

  /**
   * Gets the channel for the HVPS <CODE>Device</CODE> for the 
   * <CODE>Detector</CODE>.
   */
  public int getHVPSChannel()
  {
    return hvpsChannel;
  }

  /**
   * Sets the ADC slow <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @param adcSlow The ADC slow <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setADCSlow(ADC adcSlow)
  {    
    setADCSlow(adcSlow, getADCSlowNumber());
  }

  /**
   * Sets the ADC slow <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @param adcSlow The HVPS <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * @param adcSlowNumber The number for the ADC slow <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setADCSlow(ADC adcSlow, int adcSlowNumber)
  {
    ADC oldADCSlow = this.adcSlow;
    int oldADCSlowNumber = this.adcSlowNumber;
    if(adcSlow != oldADCSlow || adcSlowNumber != oldADCSlowNumber)
    {
      if(adcSlow != oldADCSlow)
      {
        this.adcSlow = adcSlow;
        markFieldChanged("adcSlow");
        firePropertyChange("adcSlow", oldADCSlow, adcSlow);
      }
      if(adcSlowNumber != oldADCSlowNumber)
      {
        this.adcSlowNumber = adcSlowNumber;
        markFieldChanged("adcSlowNumber");
        firePropertyChange("adcSlowNumber", oldADCSlowNumber, adcSlowNumber);
      }
      updateDevicesChannel(oldADCSlow, oldADCSlowNumber, adcSlow, adcSlowNumber);
    }
  }

  /**
   * Gets the ADC slow <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @return The HVPS <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public ADC getADCSlow()
  {
    return adcSlow;
  }

  /**
   * Sets the number for the ADC slow <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @param adcSlowNumber The number for the ADC slow <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setADCSlowNumber(int adcSlowNumber)
  {
    setADCSlow(getADCSlow(), adcSlowNumber);
  }

  /**
   * Gets the number for the ADC slow <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @return The number for the ADC slow <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public int getADCSlowNumber()
  {
    return adcSlowNumber;
  }

  /**
   * Sets the ADC fast <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @param adcFast The ADC fast <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setADCFast(ADC adcFast)
  {
    setADCFast(adcFast, getADCFastNumber());
  }

  /**
   * Sets the ADC fast <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @param adcFast The ADC fast <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * @param adcFastNumber The number for the ADC fast <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setADCFast(ADC adcFast, int adcFastNumber)
  {
    ADC oldADCFast = this.adcFast;
    int oldADCFastNumber = this.adcFastNumber;
    if(adcFast != oldADCFast || adcFastNumber != oldADCFastNumber)
    {
      if(adcFast != oldADCFast)
      {
        this.adcFast = adcFast;
        markFieldChanged("adcFast");
        firePropertyChange("adcFast", oldADCFast, adcFast);
      }
      if(adcFastNumber != oldADCFastNumber)
      {
        this.adcFastNumber = adcFastNumber;
        markFieldChanged("adcFastNumber");
        firePropertyChange("adcFastNumber", oldADCFastNumber, adcFastNumber);
      }
      updateDevicesChannel(oldADCFast, oldADCFastNumber, adcFast, adcFastNumber);
    }
  }

  /**
   * Gets the ADC fast <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @return The ADC fast <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public ADC getADCFast()
  {
    return adcFast;
  }

  /**
   * Sets the number for the ADC fast <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @param adcFastNumber The number for the ADC fast <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setADCFastNumber(int adcFastNumber)
  {
    setADCFast(getADCFast(), adcFastNumber);
  }

  /**
   * Gets the number for the ADC fast <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @return The number for the ADC fast <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public int getADCFastNumber()
  {
    return adcFastNumber;
  }

  /**
   * Sets the MPSIF <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @param mpsif The MPSIF <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setMPSIF(MPSInterface mpsif)
  {
    Device oldValue = this.mpsif;
    if(mpsif != oldValue)
    {
      this.mpsif = mpsif;
      markFieldChanged("mpsif");
      firePropertyChange("mpsif", oldValue, mpsif);
    }
  }
  
  /**
   * Gets the MPS IF Channel for the <CODE>Detector</CODE>. This value depends 
   * on the value of the SSA channel, so that property must be set to give a 
   * valid value.
   * 
   * @return The MPS IF channel for the <CODE>Detector</CODE>.
   */
  public int getMPSIFChannel()
  {
    int ssaChannel = getSSAChannel();
    if(ssaChannel <= 15)
      return ssaChannel;
    else
      return ssaChannel - 16;
  }

  /**
   * Gets the MPSIF <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @return The MPSIF <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public MPSInterface getMPSIF()
  {
    return mpsif;
  }

//  /**
//   * Sets the IPCARR <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   * 
//   * @param ipCARR The IP CARR <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   */
//  public void setIPCARR(Device ipCARR)
//  {
//    Device oldValue = this.ipCARR;
//    if(ipCARR != oldValue)
//    {
//      this.ipCARR = ipCARR;
//      markFieldChanged("ipCARR");
//      firePropertyChange("ipCARR", oldValue, ipCARR);
//    }
//  }
//
//  /**
//   * Sets the IPCARR <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   * 
//   * @param ipCARR The IP CARR <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   * @param ipCARRSlot The slot for the IP CARR <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   */
//  public void setIPCARR(Device ipCARR, int ipCARRSlot)
//  {
//    setIPCARR(ipCARR);
//    setIPCARRSlot(ipCARRSlot);
//  }
//
//  /**
//   * Gets the IPCARR <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   * 
//   * @return The IP CARR <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   */
//  public Device getIPCARR()
//  {
//    return ipCARR;
//  }
//
//  /**
//   * Sets the slot for the IP CARR <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   * 
//   * @param ipCARRSlot The slot for the IP CARR <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   */
//  public void setIPCARRSlot(int ipCARRSlot)
//  {
//    int oldValue = this.ipCARRSlot;
//    if(ipCARRSlot != oldValue)
//    {
//      this.ipCARRSlot = ipCARRSlot;
//      markFieldChanged("ipCARRSlot");
//      firePropertyChange("ipCARRSlot", oldValue, ipCARRSlot);
//    }
//  }
//
//  /**
//   * Gets the slot for the IP CARR <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   * 
//   * @return The slot for the IP CARR <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   */
//  public int getIPCARRSlot()
//  {
//    return ipCARRSlot;
//  }
//
//  /**
//   * Sets the DAC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   * 
//   * @param dac The DAC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   */
//  public void setDAC(Device dac)
//  {
//    Device oldValue = this.dac;
//    if(oldValue != dac)
//    {
//      this.dac = dac;
//      markFieldChanged("dac");
//      firePropertyChange("dac", oldValue, dac);
//    }
//  }
//
//  /**
//   * Gets the DAC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   * 
//   * @return The DAC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
//   */
//  public Device getDAC()
//  {
//    return dac;
//  }
  
  /**
   * Gets the DAC channel for the <CODE>Detector</CODE>. This property relies on 
   * the SSA channel, so that property must be set to give this one a valid 
   * value.
   * 
   * @return The DAC channel for the <CODE>Detector</CODE>.
   */
  public int getDACChannel()
  {
    return getMPSIFChannel();
  }

  /**
   * Sets the MIOC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @param mioc The MIOC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setMIOC(Device mioc)
  {
    Device oldValue = this.mioc;
    if(mioc != oldValue)
    {
      this.mioc = mioc;
      markFieldChanged("mioc");
      firePropertyChange("mioc", oldValue, mioc);
    }
  }

  /**
   * Sets the MIOC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @param mioc The MIOC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * @param miocChannel The channel for the MIOC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setMIOC(Device mioc, int miocChannel)
  {
    setMIOC(mioc);
    setMIOCChannel(miocChannel);
  }
  
  /**
   * Gets the MIOC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   * 
   * @return The MIOC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public Device getMIOC()
  {
    return mioc;
  }

  /**
   * Sets the MIOC channel for the <CODE>Detector</CODE>.
   * 
   * @param miocChannel The channel for the MIOC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public void setMIOCChannel(int miocChannel)
  {
    int oldMIOCChannel = this.miocChannel;
    if(miocChannel != oldMIOCChannel)
    {
      this.miocChannel = miocChannel;
      markFieldChanged("miocChannel");
      firePropertyChange("miocChannel", oldMIOCChannel, miocChannel);
    }
  }
  
  /**
   * Gets the MIOC channel for the <CODE>Detector</CODE>.
   * 
   * @return The channel for the MIOC <CODE>Device</CODE> for the <CODE>Detector</CODE>.
   */
  public int getMIOCChannel()
  {
    return miocChannel;
  }
  
  /**
   * The <CODE>MPSChain</CODE> to which the <CODE>Detector</CODE> belongs.
   * 
   * @param chain The <CODE>MPSChain</CODE> to which to add the <CODE>Detector</CODE>.
   */
  public void setChain(MPSChain chain)
  {
    MPSChain oldValue = this.chain;
    if(chain != oldValue)
    {
      this.chain = chain;
      if(chain.findDetectorInstance(this) == -1)
        chain.addDetector(this);
      firePropertyChange("chain", oldValue, chain);
    }
  }

  /**
   * Gets the <CODE>MPSChain</CODE> to which the <CODE>Detector</CODE> belongs.
   * 
   * @return The <CODE>Detector</CODE> to which the chain belongs.
   */
  public MPSChain getChain()
  {
    return chain;
  }
}
