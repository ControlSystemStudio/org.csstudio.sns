package org.csstudio.sns.mpsbypasses.modes;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListener;
import org.csstudio.vtype.pv.PVListenerAdapter;
import org.csstudio.vtype.pv.PVPool;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VType;

/** Read machine mode from MPS PVs
 *
 *  @author Delphy Armstrong - Original RTDL_Switch_Modes
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MachineModeMonitor
{
    private final Logger logger = Logger.getLogger(getClass().getName());

    private final MachineMode[] modes = MachineMode.values();
    private final MachineModeListener listener;

    private final PV[] rtdl_pv = new PV[modes.length-1];
    private final PV[] switch_pv = new PV[modes.length-1];

    private final PVListener[] rtdl_listener = new PVListener[modes.length-1];
    private final PVListener[] switch_listener = new PVListener[modes.length-1];

    private final VEnum[] rtdl_value = new VEnum[modes.length-1];
    private final VEnum[] switch_value = new VEnum[modes.length-1];

	private volatile MachineMode rtdl_mode = null;
	private volatile MachineMode switch_mode = null;

	/** Initialize
	 *  @throws Exception on error
	 */
	public MachineModeMonitor(final MachineModeListener listener)
	{
		this.listener = listener;
	}

	/** Connect PVs
	 *  @throws Exception on error
	 */
	public void start() throws Exception
	{
        // Handle 'RTDL' PVs
        for (int i=1; i<modes.length; ++i)
        {
            final int pv_i = i-1;
            rtdl_listener[pv_i] = new PVListenerAdapter()
            {
                @Override
                public void valueChanged(PV pv, VType value)
                {
                    if (value instanceof VEnum)
                    {
                        rtdl_value[pv_i] = (VEnum) value;
                        final MachineMode mode = getSelectedMode(rtdl_value, 1);
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
            rtdl_pv[pv_i] = PVPool.getPV("ICS_MPS:RTDL_MachMd:" + modes[i].name());
            rtdl_pv[pv_i].addListener(rtdl_listener[pv_i]);
        }

        // Handle 'Switch' PVs
        for (int i=1; i<modes.length; ++i)
        {
            final int pv_i = i-1;
            switch_listener[pv_i] = new PVListenerAdapter()
            {
                @Override
                public void valueChanged(PV pv, VType value)
                {
                    if (value instanceof VEnum)
                    {
                        switch_value[pv_i] = (VEnum) value;
                        final MachineMode mode = getSelectedMode(switch_value, 0);
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
            switch_pv[pv_i] = PVPool.getPV("ICS_MPS:Switch_MachMd:" + modes[i].name());
            switch_pv[pv_i].addListener(switch_listener[pv_i]);
        }
	}

	/** Determine which of the values indicates an active mode
     *  @param values Values of the mode PVs
     *  @param active_value Value that indicates the active mode
     *  @return Selected {@link MachineMode} or <code>null</code>
     */
    private MachineMode getSelectedMode(final VEnum[] values, final int active_value)
    {
        if (values == null)
            return null;

        if (values.length != modes.length-1)
            throw new IllegalStateException();

        int active = -1;
        for (int i=1; i<modes.length; ++i)
        {
            final VEnum value = values[i-1];
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
	private void updateModes(final MachineMode new_rtdl_mode, final MachineMode new_switch_mode)
    {
		if (new_rtdl_mode == rtdl_mode  &&  new_switch_mode == switch_mode)
			return;
		rtdl_mode = new_rtdl_mode;
		switch_mode = new_switch_mode;
		listener.machineModeUpdate(new_rtdl_mode, new_switch_mode);
    }
}
