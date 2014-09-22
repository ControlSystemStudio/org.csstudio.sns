package org.csstudio.mps.sns.tools.swing.autocompletecombobox;

import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.*;
import java.awt.*;

/**
 * Provides a popup for a <code>JComboBox</code>. This class provides a popup
 * for a <code>JComboBox</code> that is as wide as the longest item in the drop
 * down, instead of being as wide as the original combo box.
 *
 * @author Chris Fowlkes
 * @version 1.0
 */
public class WideComboPopup extends BasicComboPopup
{
    /**
     * Constructor. Constructs a new <code>BasicComboPopup</code>.
     *
     * @param combo the <code>JComboBox</code> the popup is to be used with.
     */
    public WideComboPopup(JComboBox combo)
    {
      super(combo);
    }

    /**
     * Shows the popup. This method shows the popup for the
     * <code>JComboBox</code>. Most of the code was copied directly from the
     * super class, <code>BasicComboPopup</code>, except the portion that sets
     * the width of the popup.
     */
    public void show()
    {
        Dimension popupSize = comboBox.getSize();
        //CBF 10/31/2001 Combo box drop down widens with use. Getting preffered
        //size of combo instead of drop down.
        //popupSize.setSize( Math.max(getPreferredSize().width, comboBox.getWidth()), getPopupHeightForRowCount( comboBox.getMaximumRowCount() ) );
        popupSize.setSize( Math.max(comboBox.getPreferredSize().width, comboBox.getWidth()), getPopupHeightForRowCount( comboBox.getMaximumRowCount() ) );
        //CBF 1/2/2002 If popup is against the right side of the screen, it
        //widens itself right off the users screen.
        //Rectangle popupBounds = computePopupBounds( 0, comboBox.getBounds().height,
        //                                            popupSize.width, popupSize.height);
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int xPos = 0, comboXPos = comboBox.getLocationOnScreen().x;
        if(popupSize.width + comboXPos > screenWidth)
          xPos -= popupSize.width + comboXPos - screenWidth;
        Rectangle popupBounds = computePopupBounds( xPos, comboBox.getBounds().height,
                                                    popupSize.width, popupSize.height);
        //If the drop down is going to go off the screen to the bottom, the
        //method called above flips it to appear on top of the drop down, but it
        //replaces the x coordinate you were using with 0. Resetting it.
        popupBounds.setLocation(xPos, popupBounds.y);
        scroller.setMaximumSize( popupBounds.getSize() );
        scroller.setPreferredSize( popupBounds.getSize() );
        scroller.setMinimumSize( popupBounds.getSize() );
        list.invalidate();
        int selectedIndex = comboBox.getSelectedIndex();

        if ( selectedIndex == -1 ) {
            list.clearSelection();
        }
        else {
            list.setSelectedIndex( selectedIndex );
        }
        list.ensureIndexIsVisible( list.getSelectedIndex() );

        setLightWeightPopupEnabled( comboBox.isLightWeightPopupEnabled() );

        show( comboBox, popupBounds.x, popupBounds.y );
    }
}