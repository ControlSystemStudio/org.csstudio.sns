/*
 * ConnectionDictionary.java
 *
 * Created on Thu Dec 18 11:01:11 EST 2003
 *
 * Copyright (c) 2003 Spallation Neutron Source
 * Oak Ridge National Laboratory
 * Oak Ridge, TN 37830
 */

package org.csstudio.mps.sns.tools.database;

import java.util.logging.*;
import java.util.Properties;
import java.util.prefs.*;
import java.io.*;
import java.net.*;


/**
 * ConnectionDictionary contains properties that can be used to establish a database connection.
 * It should contain at least the database URL, user name and password.  A default connection dictionary
 * can be fetched from a file specified in the user's preferences.
 *
 * @author  tap
 */
public class ConnectionDictionary extends Properties {
	// public dictionary keys
	static final public String USER_KEY = "user";
	static final public String PASSWORD_KEY = "password";
	static final public String URL_KEY = "url";
	static final public String DATABASE_ADAPTOR_KEY = "database_adaptor";
	
	// private constants
	static final protected String DICTIONARY_URL_KEY = "url";
	
	
	/**
	 * Constructor
	 */
	public ConnectionDictionary() {
		super();
	}
	
	
	/**
	 * Constructor
	 * @param url The URL of the connection dictionary properties
	 * @throws java.io.IOException if the properties file can't be loaded
	 */
	public ConnectionDictionary(URL url) throws IOException {
		this();
		loadFrom(url);
	}
	
	
	/**
	 * Constructor
	 * @param file The file of the connection dictionary properties
	 * @throws java.io.IOException if the properties file can't be loaded
	 * @throws java.net.MalformedURLException if the file cannot be converted to a URL
	 */
	public ConnectionDictionary(File file) throws IOException, MalformedURLException {
		this( file.toURL() );
	}
	
	
	/**
	 * Get the connection dictionary from the file specified in the user's preferences.
	 * @return the user's default connection dictionary
	 */
	static public ConnectionDictionary defaultDictionary() {
		try {
			URL url = getDefaultDictionaryURL();
			return new ConnectionDictionary(url);
		}
		catch(MalformedURLException exception) {
			return null;
		}
		catch(IOException exception) {
			return null;
		}
		catch(NullPointerException exception) {
			return null;
		}
	}
	
	
	/**
	 * Determine if the dictionary is sufficiently complete regardless whether the data is right or wrong.
	 * @return true if the dictionary contains the user name, password and database URL (database adaptor is optional)
	 */
	public boolean hasRequiredInfo() {
		return getUser() != null && getPassword() != null && getURLSpec() != null;
	}
	
	
	/**
	 * Get the user name for connecting
	 * @return the user name
	 */
	public String getUser() {
		return getProperty(USER_KEY);
	}
	
	
	/**
	 * Set the user ID
	 * @param userID The user ID
	 */
	public void setUser(String userID) {
		setProperty(USER_KEY, userID);
	}
	
	
	/**
	 * Get the user password for connecting
	 * @return the user password
	 */
	public String getPassword() {
		return getProperty(PASSWORD_KEY);
	}
	
	
	/**
	 * Set the password
	 * @param password the password
	 */
	public void setPassword(String password) {
		setProperty(PASSWORD_KEY, password);
	}
	
	
	/**
	 * Get the connection URL
	 * @return the connection URL
	 */
	public String getURLSpec() {
		return getProperty(URL_KEY);
	}
	
	
	/**
	 * Set the URL spec
	 * @param urlSpec The URL spec
	 */
	public void setURLSpec(String urlSpec) {
		setProperty(URL_KEY, urlSpec);
	}
	
	
	/**
	 * Get the database adaptor to use
	 * @return the database adaptor
	 */
	public DatabaseAdaptor getDatabaseAdaptor() {
		final String className = getProperty( DATABASE_ADAPTOR_KEY );
		if ( className == null || className.equals( "" ) )  return null;
		try {
			final Class databaseAdaptorClass = Class.forName( className );
			return (DatabaseAdaptor)databaseAdaptorClass.newInstance();
		}
		catch(Exception exception) {
			final String message = "Failed to instantiate database adaptor for class:  " + className;
			Logger.getLogger("global").log( Level.SEVERE, message, exception );
			throw new RuntimeException( message, exception );
		}
	}
	
	
	/**
	 * Set the database adaptor class
	 * @param databaseAdaptorClass the database adaptor class to use
	 */
	public void setDatabaseAdaptorClass(final Class databaseAdaptorClass) {
		setDatabaseAdaptorClass( databaseAdaptorClass.getName() );
	}
	
	
	/**
	 * Set the database adaptor class
	 * @param className the database adaptor class name to use
	 */
	public void setDatabaseAdaptorClass(final String className) {
		setProperty(DATABASE_ADAPTOR_KEY, className);
	}
	
	
	/**
	 * Save the connection dictionary to the dictionary file.
	 * @param url The url of the dictionary properties file.
	 * @throws java.io.IOException if the connection dictionary cannot be saved to its properties file
	 */
	public void saveTo(URL url) throws IOException {
		OutputStream stream = url.openConnection().getOutputStream();
		store(stream, "connection dictionary");
	}
	
	
	/**
	 * Load the connection dictionary from the dictionary file.
	 * @param url The url of the dictionary properties file.
	 * @throws java.io.IOException if the connection dictionary cannot be loaded from its properties file
	 */
	public void loadFrom(URL url) throws IOException {
		InputStream stream = url.openStream();
		load(stream);
	}
	
	
	/**
	 * Get the user preferences for this class
	 * @return the user preferences for this class
	 */
	static protected Preferences getDefaults() {
		return Preferences.userNodeForPackage( ConnectionDictionary.class );
	}
	
	
	/**
	 * Get the URL of the default connection dictionary properties file
	 * @return the URL of the default connection dictionary properties file
	 * @throws java.net.MalformedURLException if the default URL spec cannot form a valid URL
	 */
	static public URL getDefaultDictionaryURL() throws MalformedURLException {
		String urlSpec = getDefaultDictionaryURLSpec();
		
		if ( urlSpec == "" || urlSpec == null ) {
			return null;
		}
		else {
			return new URL(urlSpec);
		}
	}
	
	
	/**
	 * Get the URL Spec of the default connection dictionary's properties file
	 * @return the URL Spec of the connection dictionary properties file
	 */
	static public String getDefaultDictionaryURLSpec() {
		return getDefaults().get(DICTIONARY_URL_KEY, "");
	}
	
	
	/**
	 * Set the file path of the connection dictionary's default properties file.
	 * @param filePath The file path of the connection dictionary properties file.
	 * @throws java.net.MalformedURLException if the file path cannot form a valid URL
	 * @throws java.util.prefs.BackingStoreException if the path failed to be saved as a default
	 */
	static public void setDefaultDictionaryPath(String filePath) throws MalformedURLException, BackingStoreException {
		setDefaultDictionaryFile( new File(filePath) );
	}
	
	
	/**
	 * Set the connection dictionary's default properties file.
	 * @param file The connection dictionary properties file.
	 * @throws java.net.MalformedURLException if the file cannot form a valid URL
	 * @throws java.util.prefs.BackingStoreException if the file failed to be saved as a default
	 */
	static public void setDefaultDictionaryFile(File file) throws MalformedURLException, BackingStoreException {
		setDefaultDictionaryURL( file.toURL() );
	}
	
	
	/**
	 * Set the URL of the connection dictionary's default properties file.
	 * @param url The URL of the connection dictionary properties file.
	 * @throws java.util.prefs.BackingStoreException if the url failed to be saved as a default
	 */
	static public void setDefaultDictionaryURL(URL url) throws BackingStoreException {
		setDefaultDictionaryURLSpec( url.toString() );
	}
	
	
	/**
	 * Set the URL spec of the connection dictionary's default properties file.
	 * @param urlSpec The URL spec of the connection dictionary properties file.
	 * @throws java.util.prefs.BackingStoreException if the url spec failed to be saved as a default
	 */
	static public void setDefaultDictionaryURLSpec(String urlSpec) throws BackingStoreException {
		Preferences preferences = getDefaults();
		preferences.put(DICTIONARY_URL_KEY, urlSpec);
		preferences.flush();
	}
}

