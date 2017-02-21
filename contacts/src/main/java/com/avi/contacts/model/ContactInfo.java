package com.avi.contacts.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class ContactInfo created on 16/05/16 - 2:17 PM.
 * All copyrights reserved to the Zoomvy.
 * Class behaviour is to keep information about contact for sync related behaviour
 */
public class ContactInfo implements Searchable {

    private String mFirstName;

    private String mLastName;

    private String mPhoneNumber;

    private String mProfilePhotoUrl;

    private String mProfileThumbnailUrl;

    @Exclude
    private String mEmailId;

    @Exclude
    private String mLabel;

    @Exclude
    private String mIdentity;

    @Exclude
    private ArrayList<LabeledData> mLabeledDataList;


    public ContactInfo(String firstName, String lastName, String phoneNumber) {
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mPhoneNumber = phoneNumber;
        this.mIdentity = phoneNumber;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
        mIdentity = mPhoneNumber;
    }

    public String getProfilePhotoUrl() {
        return mProfilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.mProfilePhotoUrl = profilePhotoUrl;
    }

    public String getProfileThumbnailUrl() {
        return mProfileThumbnailUrl;
    }

    public void setProfileThumbnailUrl(String profileThumbnailUrl) {
        this.mProfileThumbnailUrl = profileThumbnailUrl;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public String getEmailId() {
        return mEmailId;
    }

    public void setEmailId(String emailId) {
        mEmailId = emailId;
        if (!TextUtils.isEmpty(mEmailId) && (TextUtils.isEmpty(mIdentity)
                || (null != mPhoneNumber
                && !mIdentity.equals(mPhoneNumber)))) {
            mIdentity = mEmailId;
        }

        if (TextUtils.isEmpty(mIdentity)) {
            mIdentity = mEmailId;
        }
    }


    public String getIdentity() {
        return mIdentity != null ? mIdentity : (null != mPhoneNumber ? mPhoneNumber
                : null != mEmailId ? mEmailId : null);
    }

    @Override
    public String toString() {
        return mFirstName + ", " + mPhoneNumber;
    }

    public void setLabeledDataList(LabeledData... labeledDataList) {
        setLabeledDataList(new ArrayList<>(Arrays.asList(labeledDataList)));
    }

    public void setLabeledDataList(ArrayList<LabeledData> labeledDataList) {
        StringBuilder identity = new StringBuilder();
        if (!labeledDataList.isEmpty()) {
            mLabeledDataList = labeledDataList;
            identity.append(mLabeledDataList.get(0).mData);
            for (int i = 0; i < labeledDataList.size(); i++) {
                LabeledData labeledData = labeledDataList.get(i);
                identity.append(", ").append(labeledData.mData);
            }
            mIdentity = String.valueOf(identity);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactInfo contactInfo = (ContactInfo) o;

        return getIdentity() != null ? getIdentity().equals(contactInfo.getIdentity())
                : contactInfo.getIdentity() == null;

    }

    @Override
    public int hashCode() {
        return mIdentity != null ? mIdentity.hashCode() : 0;
    }

    @Override
    public boolean contain(CharSequence searchString) {
        boolean found = false;
        if (null != mFirstName) {
            found = mFirstName.toLowerCase().contains(String.valueOf(searchString).toLowerCase());
        }
        if (!found && null != mLastName) {
            found = mLastName.toLowerCase().contains(String.valueOf(searchString).toLowerCase());
        }
        return found;
    }
}
