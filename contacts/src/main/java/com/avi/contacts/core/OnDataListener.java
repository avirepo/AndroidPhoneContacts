package com.avi.contacts.core;

/**
 * Class OnDataListener created on 01/06/16 - 9:19 PM.
 * All copyrights reserved to the Zoomvy.
 * Class behaviour is to provide data on UI view for the RESULT type
 */
public interface OnDataListener<RESULT> {
    /**
     * Method will be fire when their is RESULT which need to
     * be pass on the UI which implement for the type
     * @param data Result which need to pass on UI
     */
    void onResult(RESULT data);
}
