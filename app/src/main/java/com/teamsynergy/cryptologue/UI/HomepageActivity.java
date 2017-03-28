package com.teamsynergy.cryptologue.UI;

/**
 * Created by MatthewRedington on 3/26/17.
 */
import android.content.Context;
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

        mListView = (ListView) findViewById(R.id.chatroom_list);
        prepChatroomData();


        ChatroomListAdapter adapter = new ChatroomListAdapter(this, mChatroomNameList);
        mListView.setAdapter(adapter);

    }

    private void prepChatroomData(){
        String name = "test1";
        mChatroomNameList.add(name);
        name= "test2";
        mChatroomNameList.add(name);
        name= "test3";
        mChatroomNameList.add(name);
        name= "test4";
        mChatroomNameList.add(name);
        name= "test5";
        mChatroomNameList.add(name);
        name= "test6";
        mChatroomNameList.add(name);
        name= "test7";
        mChatroomNameList.add(name);
        name= "test8";
        mChatroomNameList.add(name);
        name= "test9";
        mChatroomNameList.add(name);
        name= "test10";
        mChatroomNameList.add(name);
        name= "test11";
        mChatroomNameList.add(name);
        name = "test12";
        mChatroomNameList.add(name);
        name= "test13";
        mChatroomNameList.add(name);
        name= "test14";
        mChatroomNameList.add(name);
        name= "test15";
        mChatroomNameList.add(name);
        name= "test16";
        mChatroomNameList.add(name);
        name= "test17";
        mChatroomNameList.add(name);
        name= "test18";
    }


}
