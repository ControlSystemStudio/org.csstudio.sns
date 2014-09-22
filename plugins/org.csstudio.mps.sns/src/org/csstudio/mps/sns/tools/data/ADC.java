package org.csstudio.mps.sns.tools.data;

/**
 * Holds a record from the ADC_DVC RDB table.
 * 
 * @author Chris Fowlkes
 */
public class ADC extends ChannelDevice
{
  /**
   * The <CODE>IOC</CODE> for the <CODE>ChannelDevice</CODE>.
   */
  private IOC ioc;
  /**
   * Indicator that determines if the 
   */
  private String fastSlowIndicator;
  
  /**
   * Creates a new <CODE>ADC</CODE>.
   */
  public ADC()
  {
  }
  
  /**
   * creates a new <CODE>ADC</CODE>.
   * 
   * @param id The ID of the <CODE>ADC</CODE>.
   */
  public ADC(String id)
  {
    super(id);
  }

  /**
   * Sets the <CODE>IOC</CODE> for the <CODE>ADC</CODE>.
   * 
   * @param ioc The <CODE>IOC</CODE> or the <CODE>ADC</CODE>.
   */
  public void setIOC(IOC ioc)
  {
    IOC oldValue = getIOC();
    if(ioc != oldValue)
    {
      this.ioc = ioc;
      firePropertyChange("ioc", oldValue, ioc);
    }
  }

  /**
   * Gets the <CODE>IOC</CODE> for the <CODE>ADC</CODE>.
   * 
   * @return The <CODE>IOC</CODE> for the <CODE>ADC</CODE>.
   */
  public IOC getIOC()
  {
    return ioc;
  }

  /**
   * Sets the fast / slow indicator for the <CODE>ADC</CODE>.
   * 
   * @param fastSlowIndicator The fast slow indicator for the <CODE>ADC</CODE>.
   */
  public void setFastSlowIndicator(String fastSlowIndicator)
  {
    String oldValue = getFastSlowIndicator();
    this.fastSlowIndicator = fastSlowIndicator;
    firePropertyChange("fastSlowIndicator", oldValue, fastSlowIndicator);
  }

  /**
   * Gets the fast / slow indicator for the <CODE>ADC</CODE>.
   * 
   * @return The fast slow indicator for the <CODE>ADC</CODE>.
   */
  public String getFastSlowIndicator()
  {
    return fastSlowIndicator;
  }
}
