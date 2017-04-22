package com.teamsynergy.cryptologue.UI;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.teamsynergy.cryptologue.AccountManager;
import com.teamsynergy.cryptologue.Chatroom;
import com.teamsynergy.cryptologue.Manifest;
import com.teamsynergy.cryptologue.R;
import com.teamsynergy.cryptologue.User;
import com.teamsynergy.cryptologue.UserAccount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MatthewRedington on 3/31/17.
 */

public class SelectContactsActivity extends AppCompatActivity {
    private static final int CONTACTS_REQUEST = 1;

    private Button mInviteButton;
    private ListView mListView;
    private ArrayList<Pair<String, String>> mContactList = new ArrayList<>();
    private UserAccount curAcc;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcontacts);

        mListView = (ListView) findViewById(R.id.create_chatroom);
        mInviteButton = (Button) findViewById(R.id.invite);


        mInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Pair<String, String>> selected = ((SelectContactsAdapter)mListView.getAdapter()).getCheckedContacts();

                String[] numbers = new String[selected.size()];
                for (int i = 0; i < numbers.length; ++i) {
                    numbers[i] = (selected.get(i).second);
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedNumbers", numbers);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();

            }
        });

        getSupportActionBar().setTitle("Selects Contacts");


        getContactsPermission();
    }

    private void fillWithContacts() {
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

    private void getContactsPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_REQUEST);
        } else {
            fillWithContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CONTACTS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fillWithContacts();
                } else {
                    finish();
                }
                break;
            }
        }
    }
}
