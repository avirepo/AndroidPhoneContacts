package com.avi.contacts.contactentitytype;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.avi.contacts.model.LabeledData;
import com.avi.contacts.ContactsFetchManager;
import com.avi.contacts.model.ContactsLabel;

/**
 * Class Phone created on 16/10/16 - 5:14 PM.
 * All copyrights reserved to the Zoomvy.
 * Class behaviour is to handle phone number fetching
 */

public class Phone {
    /**
     * Extract Phone number for the provided id
     * @param cursor Cursor
     * @return LabelData
     */
    public static LabeledData extractPhoneNumber(Cursor cursor) {
        LabeledData labeledData = null;
        if (null != cursor) {
            String name = null;
            int phoneType = ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM;
            int index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            //If type index is valid then get the value
            if (ContactsFetchManager.isValidIndex(index)) {
                phoneType = cursor.getInt(index);
            }

            index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            //If name index is valid then get the value
            if (ContactsFetchManager.isValidIndex(index)) {
                name = cursor.getString(index);
            }

            index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
            String phoneNumber;
            if (ContactsFetchManager.isValidIndex(index) && !TextUtils.isEmpty(phoneNumber = cursor.getString(index))) {
                labeledData = new LabeledData(PhoneContact.NUMBER, phoneNumber, resolveLabel(cursor, phoneType), name);
            }
        }
        return labeledData;
    }

    /**
     * Resolve the label type of fetch contact if it is custom then get the value of custom label
     * @param cursor Cursor on which label need to be fetch if type is custom
     * @param labelType labelType int value
     * @return resolved label string value
     */
    private static String resolveLabel(Cursor cursor, int labelType) {
        String label = null;
        if (labelType == ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM) {
            int labelIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL);
            if (ContactsFetchManager.isValidIndex(labelIndex)) {
                label = cursor.getString(labelIndex);
            }
        }
        return null != label ? label : ContactsLabel.LabelResolver
                .resolvePhoneLabel(labelType);
    }
}
