package org.csstudio.mps.sns.gui;


import org.csstudio.mps.sns.apps.mpsbrowser.MPSBrowser;
import org.csstudio.mps.sns.model.MPSModel;
import org.csstudio.mps.sns.model.MPSModelListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

	/**
	 * 
	 */
@SuppressWarnings("nls")
public class GUI  implements MPSModelListener
	
{
    private MPSModel mpsControl;
	private Display display = Display.getDefault();
    
	
	  public GUI(Composite shell, final MPSModel control) {

		this.mpsControl = control;
		shell.setLayout(new FillLayout());
		
		Composite mpsWindow = new Composite(shell, SWT.NONE );
		
	    ToolBar bar = new ToolBar(shell, SWT.BORDER);
	    for (int i = 0; i < 8; i++) {
	      ToolItem item = new ToolItem(bar, SWT.PUSH);
	      item.setText("Item " + i);
	    }
	    bar.pack();

	    Display display = shell.getDisplay();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    display.dispose();
	  }



	    
	    public void setFocus()
	    {
	        // TODO Set focus somewhere?
	    	
	    }

    
	    
		public void mpsBrowserChanged() {
			// TODO Auto-generated method stub
			
		}


	
}	  	  
	  
