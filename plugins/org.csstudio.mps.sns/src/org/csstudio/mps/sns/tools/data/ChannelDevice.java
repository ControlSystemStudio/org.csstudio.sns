package org.csstudio.mps.sns.tools.data;

/**
 * Provides a class for and <CODE>Device</CODE> that has channels to which other 
 * instances of <CODE>Device</CODE> can be assigned.
 * 
 * @author Chris Fowlkes
 */
public class ChannelDevice extends Device
{
  /**
   * Holds the instances of <CODE>Device</CODE> that have been added to certain 
   * channels.
   */
  private Device[] channelDevices = new Device[32];
  
  /**
   * Creates a new <CODE>ChannelDevice</CODE>.
   */
  public ChannelDevice()
  {
  }
  
  /**
   * Creates a new <CODE>ChannelDevice</CODE>.
   * 
   * @param id The id of the <CODE>ChannelDevice</CODE>.
   */
  public ChannelDevice(String id)
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
    return channelDevices.length;
  }

  /**
   * Sets the number of channels for the <CODE>ChannelDevice</CODE>. 
   * <CODE>32</CODE> by default.
   * 
   * @param channelCount The number of channels for the <CODE>ChannelDevice</CODE>.
   */
  public void setChannelCount(int channelCount)
  {
    Device[] newChannels = new Device[channelCount];
    int oldChannelCount = getChannelCount();
    int copyCount = Math.min(channelCount, oldChannelCount);
    System.arraycopy(channelDevices, 0, newChannels, 0, copyCount);
    channelDevices = newChannels;
    firePropertyChange("channelCount", oldChannelCount, channelCount);
  }
  
  /**
   * Assigns the given <CODE>Device</CODE> to the given channel.
   * 
   * @param channel The channel to which to assign the <CODE>Device</CODE>.
   * @param device The <CODE>Device</CODE> to assign to the channel.
   */
  public void setDeviceAtChannel(int channel, Device device)
  {
    Object oldValue = channelDevices[channel];
    channelDevices[channel] = device;
    fireIndexedPropertyChange("deviceAtChannel", channel, oldValue, device);
  }
  
  /**
   * Gets the <CODE>Device</CODE> at the given channel.
   * 
   * @param channel The channel for which to get the assigned <CODE>Device</CODE>.
   * @return The <CODE>Device</CODE> assigned to the given channel.
   */
  public Device getDeviceAtChannel(int channel)
  {
    return channelDevices[channel];
  }
}
