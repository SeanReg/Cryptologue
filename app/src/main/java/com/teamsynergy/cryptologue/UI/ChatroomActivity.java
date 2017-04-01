package com.teamsynergy.cryptologue.UI;

import android.app.ActionBar;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.teamsynergy.cryptologue.ChatMessageBubble;
import com.teamsynergy.cryptologue.Chatroom;
import com.teamsynergy.cryptologue.Message;
import com.teamsynergy.cryptologue.MessagingService;
import com.teamsynergy.cryptologue.R;
import com.teamsynergy.cryptologue.ChatArrayAdapter;

public class ChatroomActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    public ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private Toolbar toolbar;
    private boolean side = false;

    private Chatroom mChatroom = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chatroom);

        buttonSend = (Button) findViewById(R.id.send);

        listView = (ListView) findViewById(R.id.msgview);

        Intent roomIntent = getIntent();
        mChatroom = (Chatroom)roomIntent.getParcelableExtra("chatroom");

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    sendChatMessage();
                    return true;
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        getSupportActionBar().setTitle(mChatroom.getName());

        mChatroom.setMessageListener(new MessagingService.MessageListener() {
            @Override
            public void onMessageRecieved(Message msg) {
                addChatMessage(msg.getText(), false);
            }
        });
    }

    private void sendChatMessage() {
        String msg = chatText.getText().toString();
        mChatroom.sendMessage(new Message(msg));
        addChatMessage(msg, true);
        chatText.setText("");
    }

    private void addChatMessage(String msg, boolean isSender) {
        chatArrayAdapter.add(new ChatMessageBubble(isSender, msg));
    }
}
