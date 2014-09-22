/*
 * RemoteMessageException.java
 *
 * Created on Thu Oct 09 15:51:49 EDT 2003
 *
 * Copyright (c) 2003 Spallation Neutron Source
 * Oak Ridge National Laboratory
 * Oak Ridge, TN 37830
 */

package org.csstudio.mps.sns.tools.services;

import org.apache.xmlrpc.*;


/**
 * RemoteMessageException wraps the lower level exeptions thrown during exectution of a remote message.
 *
 * @author  tap
 */
public class RemoteMessageException extends RuntimeException {
	public RemoteMessageException(Throwable cause) {
		super(cause);
	}
}

