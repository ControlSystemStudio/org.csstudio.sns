package org.csstudio.mps.sns.tools.data;

import java.sql.*;
import java.util.*;
import java.math.*;

import org.csstudio.mps.sns.tools.data.Device;
import org.csstudio.mps.sns.tools.data.MPSChain;
import org.csstudio.mps.sns.tools.data.MPSChannel;
import org.csstudio.mps.sns.tools.data.MPSChassis;

/**
 * Provides a class to represent the data in the MPS_SGNL_PARAM table.
 * 
 * @author Chris Fowlkes
 */
public class MPSBoard extends Device implements Cloneable
{
  /**
   * Holds the chassis associated with the board. This represents the value in 
   * the IOC_DVC_ID field.
   * @attribute 
   */
  private MPSChassis chassis;
  /**
   * Holds the approve dat for the board. This represents the value in the 
   * APPR_DTE field.
   */
  private java.sql.Date approveDate;
  /**
   * Holds the serial number of the board. This represents the data in the 
   * SERIAL_NBR field.
   */
  private String serialNumber;
  /**
   * Holds the software version of the board. This represents the data in the 
   * SFT_VER field.
   */
  private String softwareVersion;
  /**
   * Holds the fast protect latch total of the board. This represents the data 
   * in the FPL_TOTAL field.
   */
  private String fastProtectLatchTotal;
  /**
   * Holds the fpar total of the board. This represents the data in the 
   * FPAR_TOTAL field.
   */
  private String fparTotal;
  /**
   * Holds the chassis configuration jumpers of the board. This represents the 
   * data in the CHASSIS_CONFIG_JMP field.
   */
  private int chassisConfigurationJumpers;
  /**
   * Holds the heartbeat indicator of the board. This represents the data in the 
   * HEARTBEAT_IND field.
   */
  private String heartbeatIndicator;
  /**
   * Holds the software jumper of the board. This represents the data in the 
   * SW_JMP field.
   */
  private String swJumper;
  /**
   * This represents the data in the MPS_IN field.
   */
  private int mpsIn;
  /**
   * Holds the fpar fpl configuration of the board. This represents the data in 
   * the FPAR_FPL_CONFIG field.
   */
  private String fparFastProtectLatchConfiguration;
  /**
   * Holds the date code for the board. This represents the data in the DTE_CDE
   * field.
   */
  private java.sql.Date dateCode;
  /**
   * Holds the chain end indicator for the board. This represents the data in 
   * the CHAIN_END_IND field.
   */
  private String chainEndIndicator;
  /**
   * Holds the data for the channels associated with this board. The channel 
   * data is the data stored in the MACHINE_MODE table. There are always 16
   * channels per board.
   * @attribute 
   */
  private MPSChannel[] channels = new MPSChannel[(16)];
  /**
   * Holds the <CODE>MPSChain</CODE> associated with this board.
   * @attribute 
   */
  private MPSChain chain;
  /**
   * Holds the PMC number for the board. This represents the data stored in the
   * PMC_NBR field.
   */
  private int pmcNumber;
  /**
   * Holds the FPL total for the board. This represents the data stored in the 
   * FPL_TOTAL field.
   */
  private String fplTotal;

  /**
   * Creates a new <CODE>MPSBoard</CODE>.
   */
  public MPSBoard()
  {
    super();
    for(int i=0;i<channels.length;i++)
      addChannel(new MPSChannel(i));
  }

  /**
   * Creates a new <CODE>MPSBoard</CODE> and sets the value of the device ID 
   * property.
   * 
   * @param deviceID The device ID of the <CODE>MPSBoiard</CODE>.
   */
  public MPSBoard(String deviceID)
  {
    this();
    setID(deviceID);
  }

  /**
   * Sets the <CODE>MPSChassis</CODE> this board is associated with. This method
   * is called by the <CODE>addBoard</CODE> method in <CODE>MPSChassis</CODE>
   * and does not have to be called directly.
   * 
   * @param chassis The <CODE>MPSChassis</CODE> the <CODE>MPSBoard</CODE> has been added to.
   */
  public void setChassis(MPSChassis chassis)
  {
    this.chassis = chassis;
  }

  /**
   * Gets the <CODE>MPSChassis</CODE> this board is associated with. 
   * 
   * @return chassis The <CODE>MPSChassis</CODE> the <CODE>MPSBoard</CODE> has been added to.
   */
  public MPSChassis getChassis()
  {
    return chassis;
  }

  /**
   * Sets the approve date of the <CODE>MPSBoard</CODE>. This property reflects 
   * the value of the APPR_DTE field in the database.
   * 
   * @param approveDate The approve date of the <CODE>MPSBoard</CODE>.
   */
  public void setApproveDate(java.sql.Date approveDate)
  {
    this.approveDate = approveDate;
  }

  /**
   * Gets the approve date of the <CODE>MPSBoard</CODE>. This property reflects 
   * the value of the APPR_DTE field in the database.
   * 
   * @return The approve date of the <CODE>MPSBoard</CODE>.
   */
  public java.sql.Date getApproveDate()
  {
    return approveDate;
  }

  /**
   * Sets the serial number of the <CODE>MPSBoard</CODE>. This property reflects 
   * the value of the SERIAL_NBR field in the database.
   * 
   * @param serialNumber The serial number of the <CODE>MPSBoard</CODE>.
   */
  public void setSerialNumber(String serialNumber)
  {
    this.serialNumber = serialNumber;
  }

  /**
   * Gets the serial number of the <CODE>MPSBoard</CODE>. This property reflects 
   * the value of the SERIAL_NBR field in the database.
   * 
   * @return The serial number of the <CODE>MPSBoard</CODE>.
   */
  public String getSerialNumber()
  {
    return serialNumber;
  }

  /**
   * Gets the software version of the <CODE>MPSBoard</CODE>. This property 
   * reflects the value of the SFT_VER field in the database.
   * 
   * @return The software version of the <CODE>MPSBoard</CODE>.
   */
  public String getSoftwareVersion()
  {
    return softwareVersion;
  }

  /**
   * Sets the software version of the <CODE>MPSBoard</CODE>. This property 
   * reflects the value of the SFT_VER field in the database.
   * 
   * @param softwareVersion The software version of the <CODE>MPSBoard</CODE>.
   */
  public void setSoftwareVersion(String softwareVersion)
  {
    this.softwareVersion = softwareVersion;
  }

  /**
   * Sets the FPL total of the <CODE>MPSBoard</CODE>. This property reflects the 
   * value of the FPL_TOTAL field in the database.
   * 
   * @param newFastProtectLatchTotal The FPL total of the <CODE>MPSBoard</CODE>.
   */
  public void setFastProtectLatchTotal(String newFastProtectLatchTotal)
  {
    fastProtectLatchTotal = newFastProtectLatchTotal;
  }

  /**
   * Gets the FPL total of the <CODE>MPSBoard</CODE>. This property reflects the 
   * value of the FPL_TOTAL field in the database.
   * 
   * @return The FPL total of the <CODE>MPSBoard</CODE>.
   */
  public String getFastProtectLatchTotal()
  {
    return fastProtectLatchTotal;
  }

  /**
   * Sets the FPAR total of the <CODE>MPSBoard</CODE>. This property reflects 
   * the value of the FPAR_TOTAL field in the database.
   * 
   * @param fparTotal The FPAR total of the <CODE>MPSBoard</CODE>.
   */
  public void setFPARTotal(String fparTotal)
  {
    this.fparTotal = fparTotal;
  }

  /**
   * Gets the FPAR total of the <CODE>MPSBoard</CODE>. This property reflects 
   * the value of the FPAR_TOTAL field in the database.
   * 
   * @return The FPAR total of the <CODE>MPSBoard</CODE>.
   */
  public String getFPARTotal()
  {
    return fparTotal;
  }

  /**
   * Sets the chassis configuration jumpers of the <CODE>MPSBoard</CODE>. This 
   * property reflects the value of the CHASSIS_CONFIG_JMP field in the 
   * database.
   * 
   * @param chassisConfigurationJumpers The chassis configuration jumpers of the <CODE>MPSBoard</CODE>.
   */
  public void setChassisConfigurationJumpers(int chassisConfigurationJumpers)
  {
    this.chassisConfigurationJumpers = chassisConfigurationJumpers;
  }

  /**
   * Gets the chassis configuration jumpers of the <CODE>MPSBoard</CODE>. This 
   * property reflects the value of the CHASSIS_CONFIG_JMP field in the 
   * database.
   */
  public int getChassisConfigurationJumpers()
  {
    return chassisConfigurationJumpers;
  }

  /**
   * Sets the heartbeat indicator of the <CODE>MPSBoard</CODE>. This property 
   * reflects the value of the HEARTBEAT_IND field in the database.
   * 
   * @param heartbeatIndicator The heartbeat indicator of the <CODE>MPSBoard</CODE>.
   */
  public void setHeartbeatIndicator(String heartbeatIndicator)
  {
    this.heartbeatIndicator = heartbeatIndicator;
  }

  /**
   * Gets the heartbeat indicator of the <CODE>MPSBoard</CODE>. This property 
   * reflects the value of the HEARTBEAT_IND field in the database.
   * 
   * @return The heartbeat indicator of the <CODE>MPSBoard</CODE>.
   */
  public String getHeartbeatIndicator()
  {
    return heartbeatIndicator;
  }

  /**
   * Sets the sw jumper of the <CODE>MPSBoard</CODE>. This property reflects the 
   * value of the SW_JUMP field in the database.
   * 
   * @param swJumper The SW jumper of the <CODE>MPSBoard</CODE>.
   */
  public void setSWJumper(String swJumper)
  {
    this.swJumper = swJumper;
  }

  /**
   * Gets the sw jumper of the <CODE>MPSBoard</CODE>. This property reflects the 
   * value of the SW_JUMP field in the database.
   * 
   * @return The SW jumper of the <CODE>MPSBoard</CODE>.
   */
  public String getSWJumper()
  {
    return swJumper;
  }

  /**
   * Sets the MPS in for the <CODE>MPSBoard</CODE>. This property reflects the 
   * value of the MPS_IN field in the database.
   * 
   * @param mpsIn The MPS in for the <CODE>MPSBoard</CODE>.
   */
  public void setMPSIn(int mpsIn)
  {
    this.mpsIn = mpsIn;
  }

  /**
   * Gets the MPS in for the <CODE>MPSBoard</CODE>. This property reflects the 
   * value of the MPS_IN field in the database.
   * 
   * @return The MPS in for the <CODE>MPSBoard</CODE>.
   */
  public int getMPSIn()
  {
    return mpsIn;
  }

  /**
   * Sets the FPAR FPL configuration for the <CODE>MPSBoard</CODE>. This 
   * property reflects the value of the FPAR_FPL_CONFIG field in the database.
   * 
   * @param fparFastProtectLatchConfiguration The FPAR FPL configuration for the <CODE>MPSBoard</CODE>.
   */
  public void setFPARFastProtectLatchConfig(String fparFastProtectLatchConfiguration)
  {
    this.fparFastProtectLatchConfiguration = fparFastProtectLatchConfiguration;
  }

  /**
   * Gets the FPAR FPL configuration for the <CODE>MPSBoard</CODE>. This 
   * property reflects the value of the FPAR_FPL_CONFIG field in the database.
   * 
   * @return The FPAR FPL configuration for the <CODE>MPSBoard</CODE>.
   */
  public String getFPARFastProtectLatchConfiguration()
  {
    return fparFastProtectLatchConfiguration;
  }

  /**
   * Sets the date code for the <CODE>MPSBoard</CODE>. This property reflects 
   * the value of the DTE_CDE field in the database.
   * 
   * @param dateCode The date code for the <CODE>MPSBoard</CODE>.
   */
  public void setDateCode(java.sql.Date dateCode)
  {
    this.dateCode = dateCode;
  }

  /**
   * Gets the date code for the <CODE>MPSBoard</CODE>. This property reflects 
   * the value of the DTE_CDE field in the database.
   * 
   * @return The date code for the <CODE>MPSBoard</CODE>.
   */
  public java.sql.Date getDateCode()
  {
    return dateCode;
  }

  /**
   * Sets the chain end indicator for the <CODE>MPSBoard</CODE>. This property 
   * reflects the value of the CHAIN_END_IND field in the database.
   * 
   * @param chainEndIndicator The chain end indicator for the <CODE>MPSBoard</CODE>.
   */
  public void setChainEndIndicator(String chainEndIndicator)
  {
    this.chainEndIndicator = chainEndIndicator;
  }

  /**
   * Gets the chain end indicator for the <CODE>MPSBoard</CODE>. This property 
   * reflects the value of the CHAIN_END_IND field in the database.
   * 
   * @return The chain end indicator for the <CODE>MPSBoard</CODE>.
   */
  public String getChainEndIndicator()
  {
    return chainEndIndicator;
  }

  /**
   * Adds the given channel to the board. This method also invokes the 
   * <CODE>setBoard</CODE> method in the <CODE>MPSChannel</CODE> so that it does 
   * not have to be done seperatley.
   * 
   * @param channel The <CODE>MPSChannel</CODE> to add to the board.
   */
  public void addChannel(MPSChannel channel)
  {
    channels[channel.getNumber()] = channel;
    channel.setBoard(this);
  }

  /**
   * Gets the <CODE>MPSChannel</CODE> associated with the given channel number.
   * Channels are numbered 0 - 15.
   * 
   * @param channelNumber The channel number of the <CODE>MPSChannel</CODE> to return.
   * @return The <CODE>MPSChannel</CODE> at the given channel number.
   */
  public MPSChannel channelAt(int channelNumber)
  {
    return channels[channelNumber];
  }

  /**
   * Returns a <CODE>String</CODE> representation of the <CODE>MPSBoard</CODE>.
   * The <CODE>String</CODE> returned is the value of the id property.
   * 
   * @return A <CODE>String</CODE> representation of the <CODE>MPSBoard</CODE>.
   */
  public String toString()
  {
    String id = getID();
    if(id == null)
      return "";
    else
      return id;
  }

  /**
   * Gets the mode mask values for the board. The mode mask values are returned 
   * as an integer array with two values, the first value is for the first eight
   * channels, and the second is for the last eight.
   * 
   * @param fieldName The name of the field to return the mode mask values for.
   * @return The mode mask values for the given field.
   */
  public int[] getModeMask(String fieldName)
  {
    int[] maskValues = new int[2];
    maskValues[0] = calculateModeMask(fieldName, 0);
    maskValues[1] = calculateModeMask(fieldName, 8);
    return maskValues;
  }

  /**
   * Calculates the mode mask of the given field. The mode mask is calculated
   * for 8 channels at a time, beginning with the specified channel number. The 
   * mask value is calculated by taking the inverse of the channel values and
   * appending the values. If the values were 10111001, the inverse of which is 
   * 01000110, the returned value would be 0100011010111001.
   * 
   * @param fieldName The name of the field to calculate the mode mask values for.
   * @param startChannel The number of the first channel to calculate. Normally 0 or 8.
   * @return The calculated mode mask value.
   */
  private int calculateModeMask(String fieldName, int startChannel)
  {
    StringBuffer buffer = new StringBuffer();
    for(int i=startChannel+7;i>=startChannel;i--)
    {
      BigInteger currentBit = channelAt(i).getBinaryValue(fieldName);
      if(currentBit == null)
        buffer.append(1);
      else
        buffer.append(currentBit);
    }//for(int i=startChannel+7;i>=startChannel;i--)
    int intValue = Integer.parseInt(buffer.toString(), 2);
    return (intValue^255)<<8|intValue;
  }

  /**
   * Sets the <CODE>MPSChain</CODE> this board belongs to. This method is called
   * by the <CODE>addBoard</CODE> method in <CODE>MPSChain</CODE>.
   * 
   * @param chain The <CODE>MPSChain</CODE> this <CODE>MPSBoard</CODE> has been added to.
   */
  public void setChain(MPSChain chain)
  {
    this.chain = chain;
  }

  /**
   * Gets the <CODE>MPSChain</CODE> that this <CODE>MPSBoard</CODE> belongs to.
   * 
   * @return The <CODE>MPSChain</CODE> the <CODE>MPSBoard</CODE> belongs to.
   */
  public MPSChain getChain()
  {
    return chain;
  }

  /**
   * Sets the PMC number of the board. This property reflects the value of the 
   * PMC_NBR field in the database. This refers to the slot in the chassis 
   * (IOC).
   * 
   * @param pmcNumber The PMC number for the board.
   */
  public void setPMCNumber(int pmcNumber)
  {
    this.pmcNumber = pmcNumber;
  }

  /**
   * Gets the PMC number of the board. This property reflects the value of the 
   * PMC_NBR field in the database. This refers to the slot in the chassis 
   * (IOC).
   * 
   * @return The PMC number for the board.
   */
  public int getPMCNumber()
  {
    return pmcNumber;
  }

  /**
   * Gets the fast protect latch total (FPL) of the board. This property 
   * reflects the value of the FPL_TOTAL field in the database.
   * 
   * @return The FPL total for the board.
   */
  public String getFPLTotal()
  {
    return fplTotal;
  }

  /**
   * Sets the fast protect latch total (FPL) of the board. This property 
   * reflects the value of the FPL_TOTAL field in the database.
   * 
   * @param fplTotal The FPL total for the board.
   */
  public void setFPLTotal(String fplTotal)
  {
    this.fplTotal = fplTotal;
  }

  /**
   * Sets the value of the in database proprty. Set to <CODE>true</CODE> if the
   * <CODE>MPSBoard</CODE> is in the database, <CODE>false</CODE> if not. If
   * <CODE>false</CODE> is passed into the method, this method sets the in 
   * database property of all of the channels to <CODE>false</CODE> also, since
   * if the board is not in the database, the channels can't be either. 
   * <CODE>true</CODE> by default.
   * 
   * @param inDatabase Pass as <CODE>true</CODE> if the board is in the database, <CODE>false</CODE> if not.
   */
  public void setInDatabase(boolean inDatabase)
  {
    super.setInDatabase(inDatabase);
    if(! inDatabase)
      setChannelsInDatabase(false);
  }

  /**
   * Used to mark all channels as in or not in the databse. This method sets the
   * value of the in database flag for all of the channels associated with the 
   * board. By default the value of this property is <CODE>true</CODE>.
   * 
   * @param inDatabase Pass as <CODE>true</CODE> to mark all channels as in the database, <CODE>false</CODE> to mark as not in the database.
   */
  public void setChannelsInDatabase(boolean inDatabase)
  {
    for(int i=0;i<16;i++)
      channelAt(i).setInDatabase(inDatabase);
  }

  /**
   * Creates a copy of the <CODE>MPSBoard</CODE>. This method does not copy the
   * value of the chain or chassis properties.
   * 
   * @return A clone of the <CODE>MPSBoard</CODE>.
   */
  public Object clone()
  {
    MPSBoard clone = new MPSBoard(getID());
    clone.setApproveDate(getApproveDate());
    clone.setChainEndIndicator(getChainEndIndicator());
    clone.setChassisConfigurationJumpers(getChassisConfigurationJumpers());
    clone.setDateCode(getDateCode());
    clone.setFastProtectLatchTotal(getFastProtectLatchTotal());
    clone.setFPARFastProtectLatchConfig(getFPARFastProtectLatchConfiguration());
    clone.setFPARTotal(getFPARTotal());
    clone.setFPLTotal(getFPLTotal());
    clone.setHeartbeatIndicator(getHeartbeatIndicator());
    clone.setMPSIn(getMPSIn());
    clone.setPMCNumber(getPMCNumber());
    clone.setSerialNumber(getSerialNumber());
    clone.setSoftwareVersion(getSoftwareVersion());
    clone.setSWJumper(getSWJumper());
    MPSChain chain = getChain();
    if(chain != null)
      getChain().addBoard(clone);
    MPSChassis chassis = getChassis();
    if(chassis != null)
      chassis.addBoard(clone);
    for(int i=0;i<16;i++) 
      clone.addChannel((MPSChannel)channelAt(i).clone());
    return clone;
  }

  /**
   * Gets the mask jump value for the board.
   * 
   * @return The mask jump value for the board.
   */
  public int getMaskJump()
  {
    StringBuffer maskJump = new StringBuffer();
    for(int i=15;i>=0;i--)
    {
      MPSChannel currentChannel = channelAt(i);
      if(currentChannel.getSWJumper().equals("Y"))
      {
        BigInteger currentValue = currentChannel.getBinaryValue("CHASSIS_CONFIG_SW_JUMP");
        if(currentValue == null)
          maskJump.append(1);
        else
          maskJump.append(currentValue);
      }//if(currentChannel.getSWJumper().equals("Y"))
      else
        maskJump.append(0);
    }
    return Integer.parseInt(maskJump.toString(), 2);
  }
}