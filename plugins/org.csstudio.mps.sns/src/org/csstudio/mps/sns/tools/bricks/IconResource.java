//
//  IconResource.java
//  xal
//
//  Created by Thomas Pelaia on 9/6/06.
//  Copyright 2006 Oak Ridge National Lab. All rights reserved.
//

package org.csstudio.mps.sns.tools.bricks;

import javax.swing.ImageIcon;
import java.net.URL;

import org.csstudio.mps.sns.tools.apputils.iconlib.IconLib;


/** Icon identified by a URL */
public class IconResource extends ImageIcon {
	/** group of icons */
	final protected String GROUP;
	
	/** icon name */
	final protected String ICON_NAME;
	
	
	/** Constructor */
	public IconResource( final String group, final String iconName ) {
		super( IconLib.getIconURL( group, iconName ) );
		
		GROUP = group;
		ICON_NAME = iconName;
	}
	
	
	/** 
	 * Get the icon's URL 
	 * @return the icon's URL
	 */
	public URL getURL() {
		return IconLib.getIconURL( GROUP, ICON_NAME );
	}
	
	
	/**
	 * Get the icon's group
	 * @return the icon's group
	 */
	public String getGroup() {
		return GROUP;
	}
	
	
	/**
	 * Get the icon's name
	 * @return the icon's name
	 */
	public String getIconName() {
		return ICON_NAME;
	}
	
	
	/**
	 * Description of this icon
	 * @return the description of this icon
	 */
	public String toString() {
		return "group:  "  + GROUP + ", icon name:  " + ICON_NAME;
	}
}
