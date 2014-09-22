package org.csstudio.mps.sns.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import org.csstudio.mps.sns.gui.GUI;
import org.csstudio.mps.sns.model.MPSModel;
import org.csstudio.mps.sns.tools.database.LoginDialog;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MPSBrowserView extends ViewPart
	{
	    final public static String ID = MPSBrowserView.class.getName();
	    public static final String SCHEMA = "EPICS";
	    // LOGINRESULT indicates status of LoginDialog - 0 is CANCEL.
	    public static int LOGINRESULT = LoginDialog.OK;

	    private MPSModel control = null;
	    private GUI gui;

	    public MPSBrowserView()
	    {
	    	try
	        {
	    		//this is only implemented if a new MPSBrowserView is called.
	    		new MPSModel();
	    	}
	        catch (Exception ex)
	        {
	            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Model error", ex);
	        }
    	}

	    @Override
	    public void createPartControl(Composite parent)
	    {
	    	 if (control == null)
		        {
	    		 new Label(parent, 0).setText("Cannot initialize.  Probably hit 'Cancel'."); //$NON-NLS-1$
	             return;
		        }
		     gui = new GUI(parent, control);
	    }


		@Override
		public void setFocus() {
			// TODO Auto-generated method stub

		}

	    /**
	     * Compares two objects with the <CODE>equals</CODE> method. This method also
	     * considers two <CODE>null</CODE> objects equal as well.
	     *
	     * @param object1 The first <CODE>Object</CODE> to compare.
	     * @param object2 The <CODE>Object</CODE> to compare to.
	     * @return <CODE>true</CODE> if the two items are equal.
	     */
	    public static boolean compare(Object object1, Object object2)
	    {
	      //Sometimes if a null is passed in as a string, we end up here...
	      if((object1 == null || object2 == null) && (object1 instanceof String || object2 instanceof String))
	        return compare((String)object1, (String)object2);
	      if(object1 == null)
	      {
	        if(object2 != null)
	          return false;
	      }
	      else
	        if(! object1.equals(object2))
	          return false;
	      return true;
	    }

	    /**
	     * Compares two instances of <CODE>String</CODE>. This method considers
	     * <CODE>null</CODE> and empty string to be equal and does not consider
	     * leading or trailing whitespace.
	     *
	     * @param string1 The first <CODE>String</CODE> to compare.
	     * @param string2 The <CODE>String</CODE> to compare to.
	     * @return <CODE>true</CODE> if the two items are equal.
	     */
	    public static boolean compare(String string1, String string2)
	    {
	      if(string1 == null)
	        string1 = "";
	      if(string2 == null)
	        string2 = "";
	      return string1.trim().equals(string2.trim());
	    }


	    /**
	     * Returns <CODE>true</CODE> if the given exception was caused by a closed
	     * connection.
	     *
	     * @param ex The <CODE>SQLException</CODE> to check.
	     * @return <CODE>true</CODE> if the connection has closed, <CODE>false</CODE> otherwise.
	     */
	    public static boolean checkConnection(SQLException ex)
	    {
	      int errorCode = ex.getErrorCode();
	      return errorCode == 2396 || errorCode == 1012;
	    }


}



