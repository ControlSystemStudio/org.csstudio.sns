/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.sns;

import java.io.BufferedReader;
import java.io.FileReader;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

@SuppressWarnings("nls")
public class Preferences
{
    public static String getURL() throws Exception
    {
        return getPreference("rdb_url");
    }

    public static String getUser() throws Exception
    {
        return getPreference("rdb_user");
    }

    public static String getPassword() throws Exception
    {
        return getPreference("rdb_password");
    }
    
    private static String getPreference(final String key) throws Exception
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service != null)
            return service.getString(Activator.ID, key, null, null);
        // During unit tests, directly read the preference file
        try
        (
            BufferedReader reader = new BufferedReader(new FileReader("preferences.ini"));
        )
        {
            final String entry = key + "=";
            String line;
            while ((line = reader.readLine()) != null)
                if (line.startsWith(entry))
                    return line.substring(entry.length()).trim();
        }
        throw new Exception("No preferences, and cannot read file directly, either");
    }
}
