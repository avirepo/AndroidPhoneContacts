package com.avi.contacts;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.avi.contacts.contactentitytype.PhoneContact;
import com.avi.contacts.contactentitytype.Email;
import com.avi.contacts.contactentitytype.Phone;
import com.avi.contacts.core.OnDataListener;
import com.avi.contacts.core.logs.Logger;
import com.avi.contacts.core.logs.LoggerFactory;
import com.avi.contacts.core.multithreading.BackgroundManager;
import com.avi.contacts.model.ContactInfo;
import com.avi.contacts.model.ContactQueryType;
import com.avi.contacts.model.LabeledData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import static android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;


/**
 * Class ContactsFetchManager created on 16/05/16 - 3:05 PM.
 * All copyrights reserved to the Zoomvy.
 * Class behaviour is to provide utility method for Contacts related functionality
 */
@SuppressWarnings("unused")
public class ContactsFetchManager {

    public static final Logger LOGGER = LoggerFactory.createLogger(ContactsFetchManager.class);

    private static ContactsFetchManager sInstance;

    /**
     * All contacts with there mapped raw contact id will be keep in the Map
     */
    private HashMap<String, LinkedHashSet<LabeledData>> mLabeledDataContactList = new HashMap<>();

    /**
     * Add register contact query in collection so on response it can pass the valid result to the
     * response listener
     */
    private ArrayList<ContactQuery> mContactQueries = new ArrayList<>();

    /**
     * Keep state of contact fetch query if it is already running it will be true else false
     */
    private boolean mIsRunning = false;

    /**
     * Keep state of contact fetch query if it is already fetch the contact from phone book
     * then it will be true else it will be false.
     */
    private boolean mIsFetched = false;

    /**
     * Private constructor for creating singleton instance
     */
    private ContactsFetchManager() {
    }

    /**
     * Create singleton instance
     */
    public static ContactsFetchManager getInstance() {
        if (null == sInstance) {
            synchronized (ContactsFetchManager.class) {
                sInstance = new ContactsFetchManager();
            }
        }
        return sInstance;
    }

    /**
     * Method to Fetch all phone book contacts
     * if any contacts is having multiple number then
     * create a new contact with same name for every number
     * @param contactQuery Contact Fetch query
     * @return true if a fresh fetch contact action is performing else return false
     */
    public synchronized boolean fetchContacts(ContactQuery contactQuery) {
        if (null == contactQuery) {
            throw new IllegalArgumentException("ContactQuery can't be null for the contact fetching");
        }

        if (!mContactQueries.contains(contactQuery)) {
            mContactQueries.add(contactQuery);
        }

        //If there is no running contact query or contact query required to fetch latest result or
        // there is no fetch perform in manager life cycle then call for fetch contacts
        if (!mIsRunning && (contactQuery.isRequiredLatestResult() || !mIsFetched)) {
            //Get Content resolver from the provided context
            final ContentResolver cr = contactQuery.getContext().getContentResolver();
            if (null == cr) {//If unable to retrieve content resolver then return from here
                return false;
            }
            //Update the state of flags to prevent multiple query for same operation
            mIsRunning = true;
            mIsFetched = false;
            BackgroundManager.getInstance().runInBackground(new ContactFetchingRunnable(cr
                    , new ContactFetchCallback()));
            return true;
        } else if (mIsFetched) {//If data is fetched then pass the result from here
            //On Completion of contact fetching pass the resulted data to the register ContactQuery
            passFetchedContactsToRegisterQuery();
            return false;
        }
        return true;
    }

    /**
     * Fetch contacts for the provided contact type
     * @param cr Context
     * @param phoneContact PhoneContact which contact need to be fetched
     * @param labeledDataList Labeled data collection map
     */
    private void executeContactsTypeQuery(ContentResolver cr, PhoneContact phoneContact, HashMap<String
            , LinkedHashSet<LabeledData>> labeledDataList) {

        Uri contentUri = getContentUri(phoneContact);
        if (null == contentUri) {
            // content uri will be only supported if feature is not supported by module
            return;
        }

        //Initialize local variables
        boolean hasContacts = true;
        Cursor cursor = null;
        int offset = 0;
        int limit = ContactQuery.LIMIT;
        while (hasContacts) {
            int count = 0;
            try {
                cursor = cr.query(contentUri, null, null, null
                        , DISPLAY_NAME + String.format(" ASC LIMIT %s OFFSET %s", limit, offset));
            } catch (Exception e) {
                //Possible user didn't grant the permission need to show message in such case as currently designs are not available
                LOGGER.error("Contacts fetching error %s", e.getMessage());
            } finally {
                //If cursor is null move on and try again Get fetched contacts count
                if (null != cursor && cursor.moveToFirst() && (count = cursor.getCount()) > 0) {
                    do {
                        int indexRawId = cursor.getColumnIndex(getRawIndex(phoneContact));
                        //Check whether the raw id index is valid or not if not the move to the next index
                        if (!isValidIndex(indexRawId)) {
                            continue;
                        }
                        String rawId = cursor.getString(indexRawId);
                        //fetch data for type from the cursor
                        LabeledData data = extractData(cursor, phoneContact);
                        //populate label list with newly fetch data
                        populateWithLabeledData(labeledDataList, rawId, data);
                        LOGGER.info("Id :- %s ", rawId);
                    } while (cursor.moveToNext());
                }
                //Close the cursor when work is done
                if (null != cursor) {
                    cursor.close();
                }
            }

            //To fetch next batch of paginated date set the limit and offset
            if (count >= limit) {
                offset = limit;
                limit = limit + ContactQuery.LIMIT;
            } else {
                hasContacts = false;
            }
        }
    }

    /**
     * Runnable will all the contact fetching work in background and on completion pass the result to
     * in callback method of {@link ContactFetchingRunnable#mListener}
     */
    private class ContactFetchingRunnable implements Runnable {

        private final ContentResolver mContentResolver;
        private final OnDataListener<HashMap<String, LinkedHashSet<LabeledData>>> mListener;

        public ContactFetchingRunnable(ContentResolver cr, OnDataListener<HashMap<String, LinkedHashSet<LabeledData>>> listener) {
            mContentResolver = cr;
            mListener = listener;
        }

        @Override
        public void run() {
            //Iterate on supported contact type to fetch the data
            HashMap<String, LinkedHashSet<LabeledData>> labeledDataContactList = new HashMap<>();
            for (PhoneContact phoneContact : PhoneContact.values()) {
                //Execute the query for iterated contact type and populate label data list with the data
                executeContactsTypeQuery(mContentResolver, phoneContact, labeledDataContactList);
            }

            BackgroundManager.getInstance().postOnMainThread(mListener, labeledDataContactList);
        }
    }

    private class ContactFetchCallback implements OnDataListener<HashMap<String, LinkedHashSet<LabeledData>>> {
        /**
         * Method will be fire when their is RESULT which need to
         * be pass on the UI which implement for the type
         * @param labelData Result which need to pass on UI
         */
        @Override
        public void onResult(HashMap<String, LinkedHashSet<LabeledData>> labelData) {
            if (!labelData.isEmpty()) {
                //Clear the existing label labelData list before fetching the contacts again
                mLabeledDataContactList.clear();
                mLabeledDataContactList.putAll(labelData);
            }
            mIsRunning = false;
            mIsFetched = true;
            //On Completion of contact fetching pass the resulted data to the register ContactQuery
            passFetchedContactsToRegisterQuery();
        }
    }

    /**
     * Method will extract contacts map from labeled data list and pass the result to the register
     * query listener for contacts
     */
    private void passFetchedContactsToRegisterQuery() {
        //Extract contacts map from the fetched label labelData list for each contact type.
        HashMap<ContactQueryType, LinkedHashSet<ContactInfo>> contactsMap
                = extractContactsMap(mLabeledDataContactList);
        //Iterate on ContactQuery and pass the data
        for (ContactQuery contactQuery : mContactQueries) {
            LinkedHashSet<ContactInfo> contactInfos = contactsMap.get(contactQuery.getQueryType());
            ContactQuery.ContactsResponseListener<ArrayList<ContactInfo>> listener = contactQuery.getListener();
            if (null != listener && null != contactInfos) {
                listener.onContactsResponse(new ArrayList<>(contactInfos));
            }
        }
        //As method is called on fetching completion clear the query collection
        mContactQueries.clear();
    }

    /**
     * Method will extract contacts map from labeled data list and pass the result to the register
     * query listener for contacts
     */
    private void passFetchedPaginatedContactsToRegisterQuery() {
        //Extract contacts map from the fetched label labelData list for each contact type.
        HashMap<ContactQueryType, LinkedHashSet<ContactInfo>> contactsMap
                = extractContactsMap(mLabeledDataContactList);
        //Iterate on ContactQuery and pass the data
        for (ContactQuery contactQuery : mContactQueries) {
            LinkedHashSet<ContactInfo> contactInfos = contactsMap.get(contactQuery.getQueryType());
            ContactQuery.ContactsResponseListener<ArrayList<ContactInfo>> listener = contactQuery.getListener();
            if (null != listener && null != contactInfos) {
                listener.onContactsResponse(new ArrayList<>(contactInfos));
            }
        }
    }

    /**
     * Extract the data from the cursor for the provided PhoneContact
     * @param cursor Cursor
     * @param phoneContact Contact type
     * @return LabeledData
     */
    private LabeledData extractData(Cursor cursor, PhoneContact phoneContact) {
        LabeledData data = null;
        switch (phoneContact) {
            case EMAIL:
                data = Email.extractEmail(cursor);
                break;
            case NUMBER:
                data = Phone.extractPhoneNumber(cursor);
                break;
        }
        return data;
    }

    /**
     * Check provided label in map if exist then update the collection
     * else create collection for key and set label data into it
     * @param labeledDataList label data list
     * @param rawId Contact Raw id
     * @param data LabeledData
     */
    private void populateWithLabeledData(HashMap<String, LinkedHashSet<LabeledData>> labeledDataList, String rawId, LabeledData data) {
        if (null != data) {
            LinkedHashSet<LabeledData> labeledData = labeledDataList.get(rawId);
            if (null == labeledData) {
                labeledData = new LinkedHashSet<>();
                labeledDataList.put(rawId, labeledData);
            }
            labeledData.add(data);
        }
    }

    /**
     * Get key for raw index for the provided contact type
     * @param phoneContact PhoneContact
     * @return column name for the contact type
     */
    private String getRawIndex(PhoneContact phoneContact) {
        switch (phoneContact) {
            case EMAIL:
                return ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID;
            case NUMBER:
                return ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID;
        }
        return ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID;
    }

    /**
     * Get content Uri for the contact type
     * @param phoneContact PhoneContact
     * @return Uri of the content type
     */
    private Uri getContentUri(PhoneContact phoneContact) {
        switch (phoneContact) {
            case EMAIL:
                return ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            case NUMBER:
                return ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        }
        return null;
    }

    /**
     * Add a new label data in existing collection
     * @param labeledData labeled data
     */
    public void addLabelData(String key, LabeledData labeledData) {
        LinkedHashSet<LabeledData> labels = mLabeledDataContactList.get(key);
        if (null == labels) {
            labels = new LinkedHashSet<>();
            mLabeledDataContactList.put(key, labels);
        }
        labels.add(labeledData);
    }

    /**
     * Populate contact array with provided data
     * @param mappedContactLabeledList LabelData
     */
    private HashMap<ContactQueryType, LinkedHashSet<ContactInfo>> extractContactsMap(HashMap<String
            , LinkedHashSet<LabeledData>> mappedContactLabeledList) {
        String name = null;

        HashMap<ContactQueryType, LinkedHashSet<ContactInfo>> contacts = new HashMap<>();
        //Create a map for register ContactQueryType
        for (ContactQueryType contactQueryType : ContactQueryType.values()) {
            contacts.put(contactQueryType, new LinkedHashSet<ContactInfo>());
        }

        LinkedHashSet<ContactInfo> combinedEmail = contacts.get(ContactQueryType.COMBINED_EMAIL);
        LinkedHashSet<ContactInfo> combinedNumbers = contacts.get(ContactQueryType.COMBINED_PHONE);
        LinkedHashSet<ContactInfo> multipleNumbers = contacts.get(ContactQueryType.MULTIPLE_PHONE);
        LinkedHashSet<ContactInfo> multipleEmail = contacts.get(ContactQueryType.MULTIPLE_EMAIL);
        LinkedHashSet<ContactInfo> combinedContactInfos = contacts.get(ContactQueryType.COMBINED_EMAIL_PHONE);
        LinkedHashSet<ContactInfo> allPhoneEmail = contacts.get(ContactQueryType.ALL_PHONE_EMAIL);

        for (LinkedHashSet<LabeledData> labelDataList : mappedContactLabeledList.values()) {
            //A single contact info is extract from here
            ArrayList<LabeledData> emailOnlyData = new ArrayList<>();
            ArrayList<LabeledData> numberOnlyData = new ArrayList<>();

            for (LabeledData labeledData : labelDataList) {
                name = TextUtils.isEmpty(labeledData.mName) ? name : labeledData.mName;
                ContactInfo contactInfo = new ContactInfo(labeledData.mName, null, null);
                switch (labeledData.mDataType) {
                    case EMAIL:
                        emailOnlyData.add(labeledData);
                        contactInfo.setEmailId(labeledData.mData);
                        contactInfo.setLabel(labeledData.mLabel);

                        //Add the label data for single entity in contact info
                        contactInfo.setLabeledDataList(labeledData);
                        allPhoneEmail.add(contactInfo);
                        multipleEmail.add(contactInfo);
                        break;
                    case NUMBER:
                        numberOnlyData.add(labeledData);
                        contactInfo.setPhoneNumber(labeledData.mData);
                        contactInfo.setLabel(labeledData.mLabel);

                        //Add the label data for single entity in contact info
                        contactInfo.setLabeledDataList(labeledData);
                        allPhoneEmail.add(contactInfo);
                        multipleNumbers.add(contactInfo);
                        break;
                }
            }
            //Populate only email with collection in combined email contact info
            if (!emailOnlyData.isEmpty()) {
                ContactInfo combinedEmailInfo = new ContactInfo(name, null, null);
                combinedEmailInfo.setLabeledDataList(new ArrayList<>(emailOnlyData));
                combinedEmail.add(combinedEmailInfo);
            }

            //Populate only number with collection in combined number contact info
            if (!numberOnlyData.isEmpty()) {
                ContactInfo combinedNumberInfo = new ContactInfo(name, null, null);
                combinedNumberInfo.setLabeledDataList(new ArrayList<>(numberOnlyData));
                combinedNumbers.add(combinedNumberInfo);
            }

            //Populate number and email combined data in combined contact info
            ContactInfo combinedContactInfo = new ContactInfo(name, null, null);
            combinedContactInfo.setLabeledDataList(new ArrayList<>(labelDataList));
            combinedContactInfos.add(combinedContactInfo);
            //A single contact related data updation complete here
        }

        return contacts;
    }

    /**
     * Transfer fetched contacts to the register listener
     * @param contacts latest contacts object which is fetched from device
     * @param contactFetchListener Contact fetch listener
     */
    private void transferData(HashMap<ContactQueryType, LinkedHashSet<ContactInfo>> contacts
            , ContactQuery.ContactsResponseListener<HashMap<ContactQueryType
            , LinkedHashSet<ContactInfo>>> contactFetchListener) {
        //Pass all the contacts to the register listener
        if (null != contactFetchListener) {
            contactFetchListener.onContactsResponse(contacts);
        }
    }

    /**
     * Check whether the cursor index is valid or not
     * @param pIdIndex Cursor index
     * @return true if not equal to -1 else false
     */
    public static boolean isValidIndex(int pIdIndex) {
        return -1 != pIdIndex;
    }
}
