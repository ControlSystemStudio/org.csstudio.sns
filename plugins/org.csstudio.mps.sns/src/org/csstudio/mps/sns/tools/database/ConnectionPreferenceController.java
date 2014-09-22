/*
 * ConnectionPreferenceController.java
 *
 * Created on Tue Dec 30 14:48:55 EST 2003
 *
 * Copyright (c) 2003 Spallation Neutron Source
 * Oak Ridge National Laboratory
 * Oak Ridge, TN 37830
 */

package org.csstudio.mps.sns.tools.database;

import org.csstudio.mps.sns.tools.PathPreferenceSelector;

import java.util.prefs.*;


/**
 * ConnectionPreferenceController is a convenience class for displaying a PathPreferenceSelector for 
 * a connection dictionary.
 *
 * @author  tap
 */
public class ConnectionPreferenceController {
	// constants
	final static private String URL_KEY;
	final static private Preferences DEFAULTS;
	final static private String SUFFIX = ".properties";
	final static private String DESCRIPTION = "Connection Dictionary";
	
	
	static {
		URL_KEY = ConnectionDictionary.DICTIONARY_URL_KEY;
		DEFAULTS = ConnectionDictionary.getDefaults();
	}
	
	
	/**
	 * Constructor which is hidden since this class only has static methods.
	 */
	protected ConnectionPreferenceController() {}
	
	
	/**
	 * Display the PathPreferenceSelector with the specified Frame as the owner.
	 * @param owner The owner of the PathPreferenceSelector dialog.
	 */
	static public void displayPathPreferenceSelector(java.awt.Frame owner) {
		final PathPreferenceSelector selector;
		selector = new PathPreferenceSelector(owner, DEFAULTS, URL_KEY, SUFFIX, DESCRIPTION);
		selector.setLocationRelativeTo(owner);
		selector.setVisible( true );
	}
	
	
	/**
	 * Display the PathPreferenceSelector with the specified Dialog as the owner.
	 * @param owner The owner of the PathPreferenceSelector dialog.
	 */
	static public void displayPathPreferenceSelector(java.awt.Dialog owner) {
		final PathPreferenceSelector selector;
		selector = new PathPreferenceSelector(owner, DEFAULTS, URL_KEY, SUFFIX, DESCRIPTION);
		selector.setLocationRelativeTo(owner);
		selector.setVisible( true );
	}
	
	
	/**
	 * Display the PathPreferenceSelector with no owner.
	 */
	static public void displayPathPreferenceSelector() {
		final PathPreferenceSelector selector;
		selector = new PathPreferenceSelector(DEFAULTS, URL_KEY, SUFFIX, DESCRIPTION);
		selector.setVisible( true );
	}
}

