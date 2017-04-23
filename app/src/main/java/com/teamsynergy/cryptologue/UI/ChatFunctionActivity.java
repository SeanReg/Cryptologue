package com.teamsynergy.cryptologue.UI;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.teamsynergy.cryptologue.ChatFunction;
import com.teamsynergy.cryptologue.R;

import java.util.ArrayList;

/**
 * Created by nadeen on 4/22/17.
 */

public class ChatFunctionActivity extends AppCompatActivity {

    //private static ChatFunction instance;
    private ChatFunctionAdapter mChatFunctionAdapter = null;
    private ListView mListView;
    protected ArrayList<ChatFunction> mChatFunctionList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatfunctions);


        mChatFunctionList.add(new ChatFunction("this is a chat function"));
        mChatFunctionList.add(new ChatFunction("hey"));
        mChatFunctionList.add(new ChatFunction("they again"));

        mListView = (ListView) findViewById(R.id.chatfunctions_list);
        mChatFunctionAdapter = new ChatFunctionAdapter(this, mChatFunctionList);
        mListView.setAdapter(mChatFunctionAdapter);
    }

    public static void setFunctionList(ArrayList<ChatFunction> chatFunctionList) {

    }
}
