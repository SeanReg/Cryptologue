package com.teamsynergy.cryptologue.UI;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.teamsynergy.cryptologue.ChatFunction;
import com.teamsynergy.cryptologue.Chatroom;
import com.teamsynergy.cryptologue.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nadeen on 4/22/17.
 */

public class ChatFunctionActivity extends AppCompatActivity {

    //private static ChatFunction instance;
    private ChatFunctionAdapter mChatFunctionAdapter = null;
    private ListView mListView;
    private static ArrayList<ChatFunction> mChatFunctionList = new ArrayList<>();
    private static ChatFunctionActivity mInstance = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatfunctions);

        mInstance = this;

        mListView = (ListView) findViewById(R.id.chatfunctions_list);
        mChatFunctionAdapter = new ChatFunctionAdapter(this, mChatFunctionList);
        mListView.setAdapter(mChatFunctionAdapter);
    }

    public static Chatroom.GotChatFunctionsListener chatFunctionsListener() {
        return mChatFunctionsListener;
    }

    private static final Chatroom.GotChatFunctionsListener mChatFunctionsListener = new Chatroom.GotChatFunctionsListener() {
        @Override
        public void onGotChatFuntions(List<ChatFunction> functions) {
            if (functions != null) {
                mChatFunctionList = new ArrayList<>(functions);
                if (mInstance.mListView != null) {
                    mInstance.mChatFunctionAdapter.setChatFunctions(mChatFunctionList);
                    mInstance.mListView.setAdapter(mInstance.mChatFunctionAdapter);
                }
            }
        }
    };
}
