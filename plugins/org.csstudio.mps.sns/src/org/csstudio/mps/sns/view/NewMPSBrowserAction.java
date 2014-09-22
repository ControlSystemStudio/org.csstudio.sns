package org.csstudio.mps.sns.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.mps.sns.model.MPSModel;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/** Action connected to workbench menu action set for opening a new editor.
 *  @author Dave Purcell stolen from Kay Kasemir
 */
public class NewMPSBrowserAction implements IWorkbenchWindowActionDelegate
{
    public void init(IWorkbenchWindow window)
    { /* NOP */
    }

    public void selectionChanged(IAction action, ISelection selection)
    { /* NOP */
    }

    public void run(IAction action)
    {
        try
        {
        	new MPSModel();
    	    /**  This code is used to implement view as tabbed thing in editor space when
    	     * 	 the view is activated from the main tool bar.
			IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            page.showView(MPSBrowserView.ID);
			*/
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Model error", ex);
        }
    }

    public void dispose()
    { /* NOP */
    }
}
