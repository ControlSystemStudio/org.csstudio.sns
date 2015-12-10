package org.csstudio.sns.mpsbypasses.modes;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListener;
import org.csstudio.vtype.pv.PVListenerAdapter;
import org.csstudio.vtype.pv.PVPool;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VType;

/** Read beam mode from MPS PVs
 *
 *  <p>This is convoluted because instead of one PV to indicate the current mode,
 *  there are N PVs to reflect the on/off state of the possible modes,
 *  with only one PV supposed to be active at a given time.
 *
 *  <p>Additionally, the sense of 'active' differs for the RDTL vs. Switch mode PVs.
 *
 *  @author Delphy Armstrong - Original RTDL_Switch_Modes
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BeamModeMonitor
{
    final private Logger logger = Logger.getLogger(getClass().getName());

    final private BeamMode[] modes = BeamMode.values();
    final private BeamModeListener listener;

    private final PV[] rtdl_pv = new PV[modes.length];
    private final PV[] switch_pv = new PV[modes.length];

    private final PVListener[] rtdl_listener = new PVListener[modes.length];
    private final PVListener[] switch_listener = new PVListener[modes.length];

    private final VEnum[] rtdl_value = new VEnum[modes.length];
    private final VEnum[] switch_value = new VEnum[modes.length];

	private volatile BeamMode rtdl_mode = null;
	private volatile BeamMode switch_mode = null;

	/** Initialize
	 *  @param listener
	 */
	public BeamModeMonitor(final BeamModeListener listener)
	{
	    this.listener = listener;
	}

	/** Connect PVs
	 *  @throws Exception on error
	 */
	public void start() throws Exception
	{
        // Handle 'RTDL' PVs
        for (int i=0; i<modes.length; ++i)
        {
            final int pv_i = i;
            rtdl_listener[i] = new PVListenerAdapter()
            {
                @Override
                public void valueChanged(PV pv, VType value)
                {
                    if (value instanceof VEnum)
                    {
                        rtdl_value[pv_i] = (VEnum) value;
                        final BeamMode mode = getSelectedMode(rtdl_value, 1);
                        logger.log(Level.FINE, "RTDL Mode: {0}", mode);
                        updateModes(mode, switch_mode);
                    }
                }

                @Override
                public void disconnected(PV pv)
                {
                    logger.log(Level.WARNING, "Disconnected: ", pv.getName());
                    rtdl_value[pv_i] = null;
                    updateModes(null, switch_mode);
                }
            };
            rtdl_pv[i] = PVPool.getPV("ICS_MPS:RTDL_BmMd:" + modes[i].getSignal());
            rtdl_pv[i].addListener(rtdl_listener[i]);
        }

        // Handle 'Switch' PVs
        for (int i=0; i<modes.length; ++i)
        {
            final int pv_i = i;
            switch_listener[i] = new PVListenerAdapter()
            {
                @Override
                public void valueChanged(PV pv, VType value)
                {
                    if (value instanceof VEnum)
                    {
                        switch_value[pv_i] = (VEnum) value;
                        final BeamMode mode = getSelectedMode(switch_value, 0);
                        logger.log(Level.FINE, "Switch Mode: {0}", mode);
                        updateModes(rtdl_mode, mode);
                    }
                }

                @Override
                public void disconnected(PV pv)
                {
                    logger.log(Level.WARNING, "Disconnected: ", pv.getName());
                    switch_value[pv_i] = null;
                    updateModes(rtdl_mode, null);
                }
            };
            switch_pv[i] = PVPool.getPV("ICS_MPS:Switch_BmMd:" + modes[i].getSignal());
            switch_pv[i].addListener(switch_listener[i]);
        }

	}

	/** Determine which of the values indicates an active mode
	 *  @param values Values of the mode PVs
	 *  @param active_value Value that indicates the active mode
	 *  @return Selected {@link BeamMode} or <code>null</code>
	 */
	private BeamMode getSelectedMode(final VEnum[] values, final int active_value)
	{
	    if (values == null)
	        return null;

	    if (values.length != modes.length)
	        throw new IllegalStateException();

	    int active = -1;
	    for (int i=0; i<modes.length; ++i)
	    {
	        final VEnum value = values[i];
            // At least one disconnected PV -> state not known
            if (value == null)
                return null;
            if (value.getIndex() == active_value)
	        {
	            if (active >= 0)
	            {
	                Logger.getLogger(getClass().getName()).
	                    log(Level.WARNING,
	                        "Both {0} and {1} active at the same time",
	                        new Object[] { modes[active], modes[i] });
	                return null;
	            }
	            active = i;
	        }
	    }

	    if (active >= 0)
	        return modes[active];
	    return null;
	}

	/** Disconnect PVs */
	public void stop()
	{
        for (int i=0; i < switch_pv.length; ++i)
        {
            switch_pv[i].removeListener(switch_listener[i]);
            PVPool.releasePV(switch_pv[i]);

            rtdl_pv[i].removeListener(rtdl_listener[i]);
            PVPool.releasePV(rtdl_pv[i]);
        }
		updateModes(null, null);
	}

	/** Update modes and notify listeners on change
	 *  @param new_rtdl_mode
	 *  @param new_switch_mode
	 */
	private void updateModes(final BeamMode new_rtdl_mode, final BeamMode new_switch_mode)
    {
		if (new_rtdl_mode == rtdl_mode  &&  new_switch_mode == switch_mode)
			return;
		rtdl_mode = new_rtdl_mode;
		switch_mode = new_switch_mode;
		listener.beamModeUpdate(new_rtdl_mode, new_switch_mode);
    }
}
