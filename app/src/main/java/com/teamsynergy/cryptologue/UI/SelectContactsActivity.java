package com.teamsynergy.cryptologue.UI;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.ListView;

import com.teamsynergy.cryptologue.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by MatthewRedington on 3/31/17.
 */

public class SelectContactsActivity extends AppCompatActivity {
    private ListView mListView;
    private ArrayList<Pair<String, String>> mContactList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcontacts);

        mListView = (ListView) findViewById(R.id.create_chatroom);



        final SelectContactsAdapter adapter = new SelectContactsAdapter(this, mContactList);
        mListView.setAdapter(adapter);

        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

        HashMap<String, String> contactHash = new HashMap<>();
        while(cursor.moveToNext()){
            String id= cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name= cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{ id }, null);
            while(phoneCursor.moveToNext()){
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumber = PhoneNumberUtils.formatNumberToE164(phoneNumber, "US");
                if (!contactHash.containsKey(phoneNumber))
                    contactHash.put(phoneNumber, name);
            }
        }

        for (String phoneNumber : contactHash.keySet()) {
            mContactList.add(new Pair<String, String>(contactHash.get(phoneNumber), phoneNumber));
        }

        Collections.sort(mContactList, new Comparator<Pair<String, String>>() {
            @Override
            public int compare(Pair<String, String> o1, Pair<String, String> o2) {
                return o1.first.compareTo(o2.first);
            }
        });


        mListView.setAdapter(adapter);

    }



}
