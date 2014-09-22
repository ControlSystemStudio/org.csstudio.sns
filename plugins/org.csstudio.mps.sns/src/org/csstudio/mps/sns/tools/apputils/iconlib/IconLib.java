//
//  IconLib.java
//  xal
//
//  Created by Thomas Pelaia on 3/1/06.
//  Copyright 2006 Oak Ridge National Lab. All rights reserved.
//

package org.csstudio.mps.sns.tools.apputils.iconlib;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;


/** Fetch icons by name */
public class IconLib {
	public enum IconGroup {
		CUSTOM( "custom" ),
		DEVELOPMENT( "development" ), 
		GENERAL( "general" ), 
		MEDIA( "media" ), 
		NAVIGATION( "navigation" ),
		TABLE( "table" ),
		TEXT( "text" );
		
		private final String _name;
		
		IconGroup( final String name ) {
			_name = name;
		}
		
		public String getName() {
			return _name;
		}
		
		public String toString() {
			return getName();
		}
	}
	
	
    /**
	 * Get the path to the specified named icon which resides in the library.
	 * @param group the group of icon, i.e. one of DEVELOPMENT, GENERAL, MEDIA, NAVIGATION, TABLE, TEXT
	 * @param iconName the name of the icon to fetch, e.g. "Cut24.gif"
     * @return The path to the specified icon in classpath notation
     */
    static protected String getPathToIcon( final IconGroup group, final String iconName ) {
        return group + "/" + iconName;
    }
	
	
	/**
	 * Get the URL to the specified Icon.
	 * @param group the group of icon, i.e. one of DEVELOPMENT, GENERAL, MEDIA, NAVIGATION, TABLE, TEXT
	 * @param iconName the name of the icon to fetch, e.g. "Cut24.gif"
     * @return The URL to the specified icon
	 */
	static public URL getIconURL( final IconGroup group, final String iconName ) {
		return IconLib.class.getResource( getPathToIcon( group, iconName ) );
	}
	
	
	/**
	 * Get the URL to the specified Icon.
	 * @param group the group of icon, i.e. one of DEVELOPMENT, GENERAL, MEDIA, NAVIGATION, TABLE, TEXT
	 * @param iconName the name of the icon to fetch, e.g. "Cut24.gif"
     * @return The URL to the specified icon
	 */
	static public Icon getIcon( final IconGroup group, final String iconName ) {
		return new ImageIcon( getIconURL( group, iconName ) );
	}
	
    
    /**
	 * Get the path to the specified named icon which resides in the library.
	 * @param group the group of icon, i.e. one of "development", "general", "media", "navigation", "table", "text"
	 * @param iconName the name of the icon to fetch, e.g. "Cut24.gif"
     * @return The path to the specified icon in classpath notation
     */
    static protected String getPathToIcon( final String group, final String iconName ) {
        return group + "/" + iconName;
    }
	
	
	/**
	 * Get the URL to the specified Icon.
	 * @param group the group of icon, i.e. one of "development", "general", "media", "navigation", "table", "text"
	 * @param iconName the name of the icon to fetch, e.g. "Cut24.gif"
     * @return The URL to the specified icon
	 */
	static public URL getIconURL( final String group, final String iconName ) {
		return IconLib.class.getResource( getPathToIcon( group, iconName ) );
	}
	
	
	/**
	 * Get the URL to the specified Icon.
	 * @param group the group of icon, i.e. one of "development", "general", "media", "navigation", "table", "text"
	 * @param iconName the name of the icon to fetch, e.g. "Cut24.gif"
     * @return The URL to the specified icon
	 */
	static public Icon getIcon( final String group, final String iconName ) {
		return new ImageIcon( getIconURL( group, iconName ) );
	}
}
