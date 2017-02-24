package avi.avi.contacts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.avi.contacts.ContactQuery;
import com.avi.contacts.ContactsFetchManager;
import com.avi.contacts.model.ContactInfo;
import com.avi.contacts.model.ContactQueryType;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity implements ContactQuery.ContactsResponseListener<ArrayList<ContactInfo>> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        //Fetch all contacts
        ContactsFetchManager.getInstance().fetchContacts(new ContactQuery
                .Builder(ContactQueryType.COMBINED_EMAIL_PHONE)
                .withResponseListener(this)
                .build(this));
    }

    /**
     * Method will be fire when their is RESULT which need to
     * be pass on the UI which implement for the type
     * @param data Result which need to pass on UI
     */
    @Override
    public void onContactsResponse(ArrayList<ContactInfo> data) {

    }
}
