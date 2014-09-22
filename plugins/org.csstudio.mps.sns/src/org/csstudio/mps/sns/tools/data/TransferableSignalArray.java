package org.csstudio.mps.sns.tools.data;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.awt.datatransfer.Transferable;

import org.csstudio.mps.sns.tools.data.Signal;

/**
 * Provides a class that allows the user to transfer an array consisting of
 * instances of <CODE>Transferable</CODE>.
 * 
 * @author Chris Fowlkes
 */
public class TransferableSignalArray implements Transferable 
{
  /**
   * Holds the <CODE>DataFlavor</CODE> used to transfer multiple instances of 
   * <CODE>Signal</CODE> via drag and drop.
   */
  final static public DataFlavor arrayFlavor = new DataFlavor(org.csstudio.mps.sns.tools.data.Signal.class, "SignalArray");
  /**
   * Holds the instances of <CODE>Signal</CODE> being transfered.
   * @attribute 
   */
  private Signal[] signals;

  /**
   * Creates a new <CODE>TransferableSignalArray</CODE>.
   */
  public TransferableSignalArray()
  {
  }

  /**
   * Creates a new <CODE>TransferableSignalArray</CODE>.
   * 
   * @param The instances of <CODE>Signal</CODE> that make up the data being transfered.
   */
  public TransferableSignalArray(Signal[] newSignals)
  {
    setSignals(newSignals);
  }

  /**
   * Returns the instances of <CODE>DataFlavpr</CODE> supported. The only flavor
   * supported by this class is 
   * <CODE>TransferableSignalArray.arrayFlavor</CODE>.
   * 
   * @return The instances of <CODE>DataFLavor</CODE> supported.
   */
  public DataFlavor[] getTransferDataFlavors()
  {
    return new DataFlavor[]{arrayFlavor};
  }

  /**
   * Checks to see if the given <CODE>DataFlavor</CODE> is supported. The only 
   * flavor supported by this class is 
   * <CODE>TransferableSignalArray.arrayFlavor</CODE>.
   * 
   * @param flavor The <CODE>DataFlavor</CODE> to test.
   * @return <CODE>true</CODE> if the data flavor is supported, <CODE>false</CODE> if it is not supported.
   */
  public boolean isDataFlavorSupported(DataFlavor flavor)
  {
    return flavor.equals(arrayFlavor);
  }

  /**
   * Gets the data transfered.
   * 
   * @param flavor The flavor to use to return the data.
   * @return The data transfered.
   * @throws UnsupportedFlavorException Thrown if the <CODE>DataFlavor</CODE> passed in is not <CODE>TransferableSignalArray.arrayFlavor</CODE>.
   */
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
  {
    return signals;
  }

  /**
   * Sets the instances of <CODE>Signal</CODE> contained in the 
   * <CODE>TransferableSignalArray</CODE>.
   * 
   * @param newSignals The instances of <CODE>Signal</CODE> to transfer.
   */
  public void setSignals(Signal[] newSignals)
  {
    signals = newSignals;
  }

  /**
   * Gets the instances of <CODE>Signal</CODE> contained in the 
   * <CODE>TransferableSignalArray</CODE>.
   * 
   * @return The instances of <CODE>Signal</CODE> to transfer.
   */
  public Signal[] getSignals()
  {
    return signals;
  }
}