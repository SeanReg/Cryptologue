package com.teamsynergy.cryptologue.UI;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.ListView;

import com.teamsynergy.cryptologue.R;

import java.util.ArrayList;

/**
 * Created by MatthewRedington on 3/31/17.
 */

public class CreateChatroomActivity extends AppCompatActivity {
    private ListView mListView;
    private ArrayList<Pair<String, String>> mContactList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createchatroom);

        mListView = (ListView) findViewById(R.id.create_chatroom);



        final CreateChatroomAdapter adapter = new CreateChatroomAdapter(this, mContactList);
        mListView.setAdapter(adapter);

        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

        while(cursor.moveToNext()){
            String id= cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name= cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.i("MY INFO",id + " =" + name);


            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{ id }, null);
            while(phoneCursor.moveToNext()){
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mContactList.add(new Pair<String, String>(name, phoneNumber));
                Log.i("MY INFO", phoneNumber);
            }
        }

        mListView.setAdapter(adapter);

    }



}
