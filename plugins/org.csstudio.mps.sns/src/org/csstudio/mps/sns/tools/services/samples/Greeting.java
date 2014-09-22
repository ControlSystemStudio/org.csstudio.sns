/*
 * Greeting.java
 *
 * Created on Wed Aug 27 10:48:25 EDT 2003
 *
 * Copyright (c) 2003 Spallation Neutron Source
 * Oak Ridge National Laboratory
 * Oak Ridge, TN 37830
 */

package org.csstudio.mps.sns.tools.services.samples;


/**
 * Greeting is a sample service demonstrating a simple interface that can be served using the ServiceDirectory.
 *
 * @author  tap
 */
public interface Greeting {
	/**
	 * Generate a greeting incorporating the receiver.
	 * @param receiver The receiver to address in the greeting.
	 * @return A greeting.
	 */
	public String sayHelloTo(String receiver);
	
	
	/**
	 * Add two numbers and return the result.
	 * @param x An addend
	 * @param y An augend
	 * @return The sum of x and y.
	 */
	public double add(double x, double y);
}

