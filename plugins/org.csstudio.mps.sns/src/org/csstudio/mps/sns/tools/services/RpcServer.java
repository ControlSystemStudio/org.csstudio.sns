/*
 * RpcServer.java
 *
 * Created on July 18, 2003, 10:23 AM
 *
 * Copyright 2003, Spallation Neutron Source
 * Oak Ridge National Laboratory
 * Oak Ridge, TN 37830
 */

package org.csstudio.mps.sns.tools.services;

import java.net.*;
import java.util.logging.*;

import org.apache.xmlrpc.WebServer;


/**
 * RpcServer extends XML-RPC WebServer to find and use an open port rather than taking a port as an argument. 
 * 
 * @author  tap
 */
public class RpcServer extends WebServer {
    /** Creates a new instance of RemoteServer */
    public RpcServer() throws java.io.IOException {
        super(0);
    }
    
    
    /**
     * Get the port used by the web server.
	 * @return The port used by the web server.
     */
    public int getPort() {
        return serverSocket.getLocalPort();
    }
    
    
    /**
     * Get the host address used for the web server.
	 * @return The host address used for the web server.
     */
    public String getHost() {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch(UnknownHostException exception) {
			final String message = "Error getting the host name of the RPC Server.";
			Logger.getLogger("global").log( Level.SEVERE, message, exception );
            System.err.println(exception);
            return null;
        }
    }
}
