package com.avi.contacts.model;

import com.avi.contacts.contactentitytype.PhoneContact;

/**
 * Class LabeledData created on 13/10/16 - 1:14 PM.
 * All copyrights reserved to the Zoomvy.
 * Class behaviour is to keep information about single number
 * or email information
 */

public class LabeledData {

    public PhoneContact mDataType;
    public String mName;
    public String mLabel;
    public String mData;

    public LabeledData(PhoneContact dataType, String data, String label) {
        mDataType = dataType;
        mLabel = label;
        mData = data;
    }

    public LabeledData(PhoneContact dataType, String data, String label, String name) {
        mDataType = dataType;
        mLabel = label;
        mData = data;
        mName = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LabeledData that = (LabeledData) o;

        return mData != null ? mData.equals(that.mData) : that.mData == null;

    }

    @Override
    public int hashCode() {
        return mData != null ? mData.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "LabeledData{" +
                "mDataType=" + mDataType +
                ", mName='" + mName + '\'' +
                ", mLabel='" + mLabel + '\'' +
                ", mData='" + mData + '\'' +
                '}';
    }
}
