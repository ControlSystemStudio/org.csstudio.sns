package org.csstudio.sns.mpsbypasses.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListener;
import org.csstudio.vtype.pv.PVListenerAdapter;
import org.csstudio.vtype.pv.PVPool;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VType;

/** Info about one Bypass
 *
 *  <p>Combines the 'live' info from PVs with the 'static'
 *  info from the RDB
 *
 *  <p>Given a base PV name like 'Ring_Vac:SGV_AB:FPL_Ring',
 *  it will connect to the PVs 'Ring_Vac:SGV_AB:FPL_Ring_sw_jump_status'
 *  and 'Ring_Vac:SGV_AB:FPL_Ring_swmask'
 *  to determine if the bypass is possible (jumper)
 *  and actually masked (software mask),
 *  summarizing that as the live {@link BypassState}
 *
 *  @author Delphy Armstrong - Original MPSBypassInfo
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Bypass
{
    final private static Logger logger = Logger.getLogger(Bypass.class.getName());

    final private String pv_basename;
	final private String name;
	final private String chain;
	final private Request request;
	final private BypassListener listener;

	private PV jumper_pv, mask_pv;
    private volatile VType jumper, mask;
	private volatile BypassState state = BypassState.Disconnected;

	/** Initialize
	 *  @param pv_basename Base name, e.g. "Ring_Vac:SGV_AB:FPL_Ring"
	 *  @param request Who requested the bypass? <code>null</code> if not requested.
	 *  @param listener {@link BypassListener}
	 *  @throws Exception on error
	 */
	public Bypass(final String pv_basename, final Request request,
			final BypassListener listener) throws Exception
	{
	    this.pv_basename = pv_basename;
		// Given a name like "Ring_Vac:SGV_AB:FPL_Ring",
		// extract the bypass name "Ring_Vac:SGV_AB"
		// and the MPS chain "FPL Ring"
		final int last_sep = pv_basename.lastIndexOf(":");
		if (last_sep > 0)
		{
			name = pv_basename.substring(0, last_sep);
			chain = pv_basename.substring(last_sep+1).replace('_', ' ');
		}
		else
		{
			name = pv_basename;
			chain = "?";
		}

		this.request = request;
		this.listener = listener;
	}

	/** Create a pseudo-Bypass that is used to display
	 *  messages in the bypass table
	 *  @param message Message
	 *  @param detail Detail that will show in ()
	 */
	public Bypass(final String message, final String detail)
	{
	    pv_basename = null;
		name = message;
		chain = detail;
		request = null;
		listener = null;
		jumper_pv = null;
		mask_pv = null;
	}

	/** @return Bypass name, for example "Ring_Vac:SGV_AB" */
	public String getName()
	{
		return name;
	}

	/** @return MPS chain, for example "FPL_Ring" */
	public String getMPSChain()
	{
		return chain;
	}

	/** @return MPS "signal" name used in RDB or <code>null</code> */
	public String getRDBSignalName()
	{
	    if (listener == null)
	        return null; // No real bypass, only message to display
	    // Re-construct the RDB signal name
	    return name + ":" + chain.replace(' ', '_') + "_mm";
	}

	/** @return Bypass name and chain, for example "Ring_Vac:SGV_AB (FPL Ring)" */
	public String getFullName()
	{
		return name + " (" + chain + ")";
	}

    /** @return Name of the Jumper PV, for example "Ring_Vac:SGV_AB:FPL_Ring_sw_jump_status" */
    public String getJumperPVName()
    {
        return jumper_pv.getName();
    }

    /** @return Name of the Mask PV, for example "Ring_Vac:SGV_AB:FPL_Ring_swmask" */
    public String getMaskPVName()
    {
        return mask_pv.getName();
    }

	/** @return Request for this bypass or <code>null</code> */
	public Request getRequest()
	{
		return request;
	}

	/** @return Bypass state */
	public BypassState getState()
	{
		return state;
	}

	PVListener jumper_pv_listener = new PVListenerAdapter()
    {
        @Override
        public void valueChanged(PV pv, VType value)
        {
            updateState(value, mask);
        }

        @Override
        public void disconnected(PV pv)
        {
            logger.log(Level.WARNING, "Jumper PV Disconnected: " + pv.getName());
            updateState(null, mask);
        }
    };

    PVListener mask_pv_listener = new PVListenerAdapter()
    {
        @Override
        public void valueChanged(PV pv, VType value)
        {
            updateState(jumper, value);
        }

        @Override
        public void disconnected(PV pv)
        {
            logger.log(Level.WARNING, "Mask PV Disconnected: " + pv.getName());
            updateState(jumper, null);
        }
    };

	/** Connect to PVs */
	public void start() throws Exception
	{
		if (pv_basename == null)
			return;

		jumper_pv = PVPool.getPV(pv_basename + "_sw_jump_status");
		jumper_pv.addListener(jumper_pv_listener);
		mask_pv = PVPool.getPV(pv_basename + "_swmask");
		mask_pv.addListener(mask_pv_listener);
	}

	/** Disconnect PVs */
	public void stop()
	{
		if (pv_basename == null)
			return;
		mask_pv.removeListener(mask_pv_listener);
		jumper_pv.removeListener(jumper_pv_listener);
		PVPool.releasePV(jumper_pv);
		PVPool.releasePV(mask_pv);

		state = BypassState.Disconnected;
		// Does NOT notify listener
		// because the way this is used the listener
		// will soon see a different list of bypasses
		// or close down.
		// Either way, no update needed.
	}

	/** Update alarm state from current values of PVs
	 *  @param jumper Value of jumper PV
	 *  @param mask Value of mask PV
	 */
	private void updateState(final VType jumper, final VType mask)
    {
	    this.jumper = jumper;
	    this.mask = mask;
		// Anything unknown?
		if (mask == null  ||  jumper == null)
			state = BypassState.Disconnected;
		else
		{	// Determine state
			final boolean jumpered = getNumber(jumper) > 0;
			final boolean masked = getNumber(mask) > 0;

			if (jumpered)
			{
				if (masked)
					state = BypassState.Bypassed;
				else
					state = BypassState.Bypassable;
			}
			else
			{
				if (masked)
					state = BypassState.InError;
				else
					state = BypassState.NotBypassable;
			}
		}

	    // send update
		listener.bypassChanged(this);
    }

	/** @param value {@link VType}
	 *  @return Integer extracted from VType
	 */
	private int getNumber(final VType value)
    {
	    if (value instanceof VNumber)
	        return ((VNumber)value).getValue().intValue();
	    else if (value instanceof VEnum)
	        return ((VEnum)value).getIndex();
        return -1;
    }

    /** @return Debug representation */
	@Override
    public String toString()
    {
	    return "Bypass " + name + ", state " + state +
	        ", requested by " + (request != null ? request : "nobody");
    }
}
