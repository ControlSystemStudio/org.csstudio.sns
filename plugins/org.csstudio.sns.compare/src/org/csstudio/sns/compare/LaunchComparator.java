/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.csstudio.sns.compare;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/** Launches an external command with all the currently selected *.pvs files
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LaunchComparator implements IObjectActionDelegate
{
    private final String command = "pvs_compare";
    private final List<String> selected_files = new ArrayList<>();

    @Override
    public void setActivePart(final IAction action, final IWorkbenchPart targetPart)
    {
        // NOP
    }

    @Override
    public void selectionChanged(final IAction action, final ISelection selection)
    {
        selected_files.clear();
        if (selection instanceof IStructuredSelection)
        {
            final Iterator<?> iter = ((IStructuredSelection) selection).iterator();
            while (iter.hasNext())
            {
                final Object obj = iter.next();
                if (obj instanceof IResource)
                {
                    final IResource resource = (IResource) obj;
                    final String path = resource.getLocation().toOSString();
                    selected_files.add(path);
                }
            }
        }
    }

    @Override
    public void run(final IAction action)
    {
        final ProcessBuilder builder = new ProcessBuilder();
        try
        {
            builder.command().add(command);
            builder.command().addAll(selected_files);
            builder.start();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(null, "Error", "Cannot execute " + builder.command() + ":\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
