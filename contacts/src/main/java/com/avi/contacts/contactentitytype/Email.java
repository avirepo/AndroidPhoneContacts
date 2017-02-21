package com.avi.contacts.contactentitytype;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.avi.contacts.model.LabeledData;
import com.avi.contacts.ContactsFetchManager;
import com.avi.contacts.model.ContactsLabel;

/**
 * Class Email created on 16/10/16 - 5:19 PM.
 * All copyrights reserved to the Zoomvy.
 * Class behaviour is is to handle Email fetching
 */

public class Email {
    /**
     * Extract Email for the provided id
     * @param cursor Cursor
     * @return LabelData
     */
    public static LabeledData extractEmail(Cursor cursor) {
        LabeledData labeledData = null;
        if (null != cursor) {
            String name = null;

            int emailType = ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM;
            int index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE);
            //If type index is valid then get the value
            if (ContactsFetchManager.isValidIndex(index)) {
                emailType = cursor.getInt(index);
            }

            index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            //If name index is valid then get the value
            if (ContactsFetchManager.isValidIndex(index)) {
                name = cursor.getString(index);
            }

            index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            String email;
            if (ContactsFetchManager.isValidIndex(index) && !TextUtils.isEmpty(email = cursor.getString(index))) {
                labeledData = new LabeledData(PhoneContact.EMAIL, email, resolveLabel(cursor, emailType), name);
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
        if (labelType == ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM) {
            int labelIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL);
            if (ContactsFetchManager.isValidIndex(labelIndex)) {
                label = cursor.getString(labelIndex);
            }
        }
        return null != label ? label : ContactsLabel.LabelResolver
                .resolvePhoneLabel(labelType);
    }
}
