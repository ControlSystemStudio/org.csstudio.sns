package org.csstudio.mps.sns.model;

import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.mps.sns.Main;
import org.csstudio.mps.sns.application.Application;




/** Map Indication of a Device
 *  <p>
 *  Uses MapDataAPI to obtain coordinates and other 
 *  info in background threads
 *  
 *  @author Dave Purcell
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MPSModel 
{
    /** Initialize via registry lookup for implementation of RackDataAPI.
     *  @throws Exception when not found
     */
	   public MPSModel() throws Exception
	   {
	       System.out.println("MPS Model Started.");
		   Application.launch(new Main());
	   }

	   public static void main(String[] args)
	   {
           Application.launch(new Main());
	   }
}
