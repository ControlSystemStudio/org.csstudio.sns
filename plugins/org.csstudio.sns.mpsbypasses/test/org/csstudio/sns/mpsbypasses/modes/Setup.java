package org.csstudio.sns.mpsbypasses.modes;

import org.csstudio.vtype.pv.PVFactory;
import org.csstudio.vtype.pv.PVPool;
import org.csstudio.vtype.pv.jca.JCA_PVFactory;

/** JUnit test setup
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Setup
{
    public static void setup()
    {
        final String addr_list = "127.0.0.1 webopi.sns.gov:5066 160.91.228.17";
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", addr_list);
        System.setProperty("gov.aps.jca.jni.JNIContext.addr_list", addr_list);

        final PVFactory jca = new JCA_PVFactory();
        PVPool.addPVFactory(jca);
        PVPool.setDefaultType(jca.getType());
    }
}
