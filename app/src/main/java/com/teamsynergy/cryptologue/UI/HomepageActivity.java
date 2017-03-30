package com.teamsynergy.cryptologue.UI;

/**
 * Created by MatthewRedington on 3/26/17.
 */
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import com.teamsynergy.cryptologue.AccountManager;
import com.teamsynergy.cryptologue.MessagingService;
import com.teamsynergy.cryptologue.ParseInit;
import com.teamsynergy.cryptologue.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getActivity;

public class HomepageActivity extends AppCompatActivity {
    private ListView mListView;
    private ArrayList<String> mChatroomNameList = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        ParseInit.start(this);

        Intent sIntent = new Intent(getApplicationContext(), MessagingService.class);
        startService(sIntent);

        if (AccountManager.getInstance().getCurrentAccount() == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }


        mListView = (ListView) findViewById(R.id.chatroom_list);
        prepChatroomData();


        ChatroomListAdapter adapter = new ChatroomListAdapter(this, mChatroomNameList);
        mListView.setAdapter(adapter);
    }

    private void prepChatroomData(){
        String name = "test";
        for (int i = 1; i <= 18; ++i) {
            mChatroomNameList.add(name + Integer.toString(i));
        }
    }


}
