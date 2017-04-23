package com.teamsynergy.cryptologue.UI;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.teamsynergy.cryptologue.AccountManager;
import com.teamsynergy.cryptologue.ChatMessageBubble;
import com.teamsynergy.cryptologue.Chatroom;
import com.teamsynergy.cryptologue.Message;
import com.teamsynergy.cryptologue.MessagingService;
import com.teamsynergy.cryptologue.ObjectPasser;
import com.teamsynergy.cryptologue.R;
import com.teamsynergy.cryptologue.ChatArrayAdapter;
import com.teamsynergy.cryptologue.UserAccount;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.inputmethod.InputMethodManager;

public class ChatroomActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    public ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private Toolbar toolbar;
    private boolean side = false;

    private Button buttonChatRoomName;
    private Button buttonCreateEvent;
    private Button buttonPolls;
    private Button buttonMembers;
    private Button buttonLeaveChat;
    private Button buttonEvents;


    private Chatroom mChatroom = null;
    private ParseFile mChatroomImage = null;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chatroom);

        buttonSend = (Button) findViewById(R.id.send);

        buttonChatRoomName = (Button) findViewById(R.id.chatroomname_button);
        buttonCreateEvent = (Button) findViewById(R.id.create_events_button);
        buttonPolls = (Button) findViewById(R.id.polls_button);
        buttonMembers = (Button) findViewById(R.id.members_button);
        buttonLeaveChat = (Button) findViewById(R.id.leave_chat_button);
        buttonEvents=(Button) findViewById(R.id.event_button);


        listView = (ListView) findViewById(R.id.msgview);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setupDrawerListener();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        Intent roomIntent = getIntent();
        mChatroom = (Chatroom)ObjectPasser.popObject(roomIntent.getStringExtra("chatroom"));

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

        buttonChatRoomName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChatRoomNameActivity.class));
            }
        });

        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateEventActivity.class));
            }
        });

        buttonPolls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PollsActivity.class));
            }
        });

        buttonMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MembersActivity.class));
            }
        });

        buttonLeaveChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LeaveChatActivity.class));
            }
        });
        buttonEvents.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EventActivity.class));
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

        mChatroom.getCachedMessages(mMessageRecieved);
        mChatroom.setMessageListener(mMessageRecieved);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_overflow, menu);
        inflater.inflate(R.menu.menu_chatroom_icon, menu);


        addToActionBar(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.newChat:
                Intent intentNewChat = new Intent(this, CreateChatroomActivity.class);
                startActivity(intentNewChat);
                return true;
            case R.id.settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                return true;
            case R.id.logout:
                AccountManager.getInstance().logout();
                Intent intentLogout = new Intent(this, LoginActivity.class);
                startActivity(intentLogout);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void addToActionBar(final Menu menu) {
        mChatroom.getImage(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                Bitmap bmp = null;
                Drawable d = null;
                if (e == null) {
                    bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    d = new BitmapDrawable(getResources(), bmp);
                    menu.findItem(R.id.action_icon).setIcon(d);
                } else {
                    Log.d("test",
                            "Problem load image the data.");
                }
            }
        });

        getSupportActionBar().setTitle(mChatroom.getName());

    }

    private final MessagingService.MessageListener mMessageRecieved = new MessagingService.MessageListener() {
        @Override
        public void onMessageRecieved(Message msg) {
            UserAccount curAcc = AccountManager.getInstance().getCurrentAccount();
            String curUsrId = curAcc.getParseUser().getObjectId();
            
            addChatMessage(msg.getText(), curUsrId.equals(msg.getSender()));
        }
    };

    private void setupDrawerListener() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
    }

}
