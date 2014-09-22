package org.csstudio.mps.sns.model;


/** Listener interface to MapModel */
public interface MPSModelListener {

    /** Something in the model changed: New device */
    public void mpsBrowserChanged();
}