/*
 * GreetingService.java
 *
 * Created on Wed Aug 27 11:38:29 EDT 2003
 *
 * Copyright (c) 2003 Spallation Neutron Source
 * Oak Ridge National Laboratory
 * Oak Ridge, TN 37830
 */

package org.csstudio.mps.sns.tools.services.samples;

import org.csstudio.mps.sns.tools.services.*;


/**
 * GreetingService is a sample service provider implementing the Greeting service.
 *
 * @author  tap
 */
public class GreetingService implements Greeting {
	final protected String _identity;
	
	
	/**
	 * GreetingService constructor registers the greeting service.
	 * @param identity The identity of the this greeting service provider.
	 */
	public GreetingService(String identity) {
		_identity = identity;
		ServiceDirectory.defaultDirectory().registerService(Greeting.class, _identity, this);
	}
	
	
	/**
	 * Generate a greeting incorporating the receiver.
	 * @param receiver The receiver to address in the greeting.
	 * @return A greeting.
	 */
	public String sayHelloTo(String receiver) {
		return "Hello to " + receiver + " from " + _identity + "!";
	}
	
	
	/**
	 * Add two numbers and return the result.
	 * @param x An addend
	 * @param y An augend
	 * @return The sum of x and y.
	 */
	public double add(double x, double y) {
		return x + y;
	}
	
	
	/**
	 * Launch the Greeting service.
	 * @param args The identities of each service to provide.
	 */
	static public void main(String[] args) {
		for ( int index = 0 ; index < args.length ; index++ ) {
			new GreetingService(args[index]);
		}
	}
}

