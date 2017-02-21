package com.avi.contacts;

import android.content.Context;

import com.avi.contacts.model.ContactInfo;
import com.avi.contacts.model.ContactQueryType;

import java.util.ArrayList;

/**
 * Class ContactQuery created on 16/10/16 - 4:01 PM.
 * All copyrights reserved to the Zoomvy.
 * Class behaviour is to provide contact fetch query
 * The response will be pass in {@link ContactsResponseListener} for provided query type.
 * <br/>
 * <br/>
 * <b> ContactsResponseListener </b>
 * ContactsResponseListener will be called on success of query with the result data
 * if {@link ContactQuery#mIsPaginatedRequired} is provided true then response listener will called
 * with every time when {@link ContactQuery#LIMIT} size plus already fetch result available
 * and on completion empty collection will be pass. and
 */
@SuppressWarnings("unused")
public class ContactQuery {
    public static final int LIMIT = 500;

    /**
     * Context to fetch result from Content Resolver
     */
    private Context mContext;

    /**
     * Contact query define which type of data required in response
     * for the contacts all the type are define enum in {@link ContactQueryType}
     */
    private ContactQueryType mQueryType;

    /**
     * If response of query required in paginated for then
     * it will be true else it will be false
     */
    private boolean mIsPaginatedRequired;

    /**
     * If true then ignore the existing fetch result and a new contact fetching
     * operation will be fire and it will fetch new available result fo contacts
     */
    private boolean mIsRequiredLatestResult;

    /**
     * The response listener which will be called on result
     */
    private ContactsResponseListener<ArrayList<ContactInfo>> mContactsResponseListener;


    public Context getContext() {
        return mContext;
    }

    public ContactQueryType getQueryType() {
        return mQueryType;
    }

    public boolean isPaginatedRequired() {
        return mIsPaginatedRequired;
    }

    public boolean isRequiredLatestResult() {
        return mIsRequiredLatestResult;
    }

    public ContactsResponseListener<ArrayList<ContactInfo>> getListener() {
        return mContactsResponseListener;
    }

    private ContactQuery(ContactQueryType queryType) {
        mQueryType = queryType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactQuery that = (ContactQuery) o;

        return !(mIsPaginatedRequired != that.mIsPaginatedRequired
                || mIsRequiredLatestResult != that.mIsRequiredLatestResult
                || mQueryType != that.mQueryType)
                && (mContactsResponseListener != null ? mContactsResponseListener.equals(that.mContactsResponseListener)
                : that.mContactsResponseListener == null);

    }

    @Override
    public int hashCode() {
        int result = mQueryType.hashCode();
        result = 31 * result + (mIsPaginatedRequired ? 1 : 0);
        result = 31 * result + (mIsRequiredLatestResult ? 1 : 0);
        result = 31 * result + (mContactsResponseListener != null ? mContactsResponseListener.hashCode() : 0);
        return result;
    }

    /**
     * Builder for building reference of Content query
     */
    public static class Builder {
        private final ContactQuery mContactQuery;

        /**
         * Create reference of query builder with Non Null QueryType
         * @param queryType QueryType
         */
        public Builder(ContactQueryType queryType) {
            if (null == queryType) {
                throw new IllegalArgumentException("Contact Query Type can't be null");
            }
            mContactQuery = new ContactQuery(queryType);
        }

        /**
         * Provide is pagination required for query or not
         * @param isPaginated true if required else false
         */
        public Builder withPaginationRequired(boolean isPaginated) {
            mContactQuery.mIsPaginatedRequired = isPaginated;
            return this;
        }

        /**
         * Provide Required latest contact result or not
         * @param isRequiredLatestResult true if required latest result else false
         */
        public Builder withRequiredLatestResult(boolean isRequiredLatestResult) {
            mContactQuery.mIsRequiredLatestResult = isRequiredLatestResult;
            return this;
        }

        /**
         * Provide response listener
         * @param listener ContactsResponseListener
         */
        public Builder withResponseListener(ContactsResponseListener<ArrayList<ContactInfo>> listener) {
            mContactQuery.mContactsResponseListener = listener;
            return this;
        }

        /**
         * Build reference of ContactQuery for the provided data
         * @param context Context
         * @return ContactQuery
         */
        public ContactQuery build(Context context) {
            if (null == context) {
                throw new IllegalArgumentException("Context can't be null");
            }
            mContactQuery.mContext = context;
            return mContactQuery;
        }
    }


    public interface ContactsResponseListener<RESULT> {
        /**
         * Method will be fire when their is RESULT which need to
         * be pass on the UI which implement for the type
         * @param data Result which need to pass on UI
         */
        void onContactsResponse(RESULT data);
    }
}
