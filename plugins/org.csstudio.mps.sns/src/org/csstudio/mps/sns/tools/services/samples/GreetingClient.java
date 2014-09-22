/*
 * GreetingClient.java
 *
 * Created on Wed Aug 27 10:51:37 EDT 2003
 *
 * Copyright (c) 2003 Spallation Neutron Source
 * Oak Ridge National Laboratory
 * Oak Ridge, TN 37830
 */

package org.csstudio.mps.sns.tools.services.samples;

import org.csstudio.mps.sns.tools.services.*;


/**
 * GreetingClient is a sample client that demonstrates how to use the ServiceDirectory
 * to lookup a service and send messages to it.
 *
 * @author  tap
 */
public class GreetingClient {
	/**
	 * Launch a Greeting client to find greeting services and make requests on them.  This method demonstrates two
	 * approaches to finding services.  You can either request services and wait or you can become a listener of service
	 * availability.
	 * @param args The arguments to the client (currently ignored)
	 */
	static public void main( final String[] args ) {
		GreetingClient client = new GreetingClient();
		
		// First approach is to wait for a second to see what services are presently available.
		// This is the quick and simple approach.
		ServiceRef[] services = ServiceDirectory.defaultDirectory().findServicesWithType( Greeting.class, 1000 );
		for ( int index = 0 ; index < services.length ; index++ ) {
			System.out.println( "found: " + services[index].getRawName() );
			Greeting proxy = (Greeting)ServiceDirectory.defaultDirectory().getProxy( Greeting.class, services[index] );
			System.out.println( proxy.sayHelloTo( "client" ) );
		}
		
		// Second approach is to listen for services being added and removed.
		// This approach is more robust since we are constantly monitoring the availability of services.
		System.out.println( "\nMonitor availability of services..." );
		ServiceDirectory.defaultDirectory().addServiceListener( Greeting.class, new ServiceListener() {
			public void serviceAdded( final ServiceDirectory directory, final ServiceRef serviceRef ) {
				Greeting proxy = (Greeting)directory.getProxy( Greeting.class, serviceRef );
				System.out.println( proxy.sayHelloTo( "client" ) );
				System.out.println( proxy.add( Double.NaN, 1.0 ) );
				System.out.println( proxy.add( Double.NEGATIVE_INFINITY, 2.0 ) );
				System.out.println( proxy.add( Double.POSITIVE_INFINITY, 3.0 ) );
				System.out.println( proxy.add( 5.1, 7.4 ) );
			}
			
			public void serviceRemoved( final ServiceDirectory directory, final String type, final String name ) {
				System.out.println( "Service provider, \"" + name + "\", has been removed..." );
			}
		});
	}
}

