package com.teamsynergy.cryptologue.UI;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
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
import com.teamsynergy.cryptologue.ChatFunction;
import com.teamsynergy.cryptologue.ChatMessageBubble;
import com.teamsynergy.cryptologue.Chatroom;
import com.teamsynergy.cryptologue.Event;
import com.teamsynergy.cryptologue.Message;
import com.teamsynergy.cryptologue.MessagingService;
import com.teamsynergy.cryptologue.ObjectPasser;
import com.teamsynergy.cryptologue.Poll;
import com.teamsynergy.cryptologue.R;
import com.teamsynergy.cryptologue.ChatArrayAdapter;
import com.teamsynergy.cryptologue.User;
import com.teamsynergy.cryptologue.UserAccount;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatroomActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    public ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private Toolbar toolbar;
    private boolean side = false;
    private String tag;

    private Button buttonChatRoomName;
    private Button buttonCreateEvent;
    private Button buttonCreatePoll;
    private Button buttonPolls;
    private Button buttonMembers;
    private Button buttonLeaveChat;
    private Button buttonEvents;


    private Chatroom mChatroom = null;
    private ParseFile mChatroomImage = null;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private ArrayList<Pair<User, File>> mMembers = new ArrayList<>();
    private Thread mMemberTrackerThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chatroom);

        buttonSend = (Button) findViewById(R.id.send);

        buttonChatRoomName = (Button) findViewById(R.id.chatroomname_button);
        buttonCreateEvent = (Button) findViewById(R.id.create_events_button);
        buttonCreatePoll = (Button) findViewById(R.id.create_poll_button);
        buttonEvents = (Button) findViewById(R.id.events_button);
        buttonPolls = (Button) findViewById(R.id.polls_button);
        buttonMembers = (Button) findViewById(R.id.members_button);
        buttonLeaveChat = (Button) findViewById(R.id.leave_chat_button);


        listView = (ListView) findViewById(R.id.msgview);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setupDrawerListener();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        Intent roomIntent = getIntent();
        mChatroom = (Chatroom)ObjectPasser.popObject(roomIntent.getStringExtra("chatroom"));

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        mChatroom.getMembers(new Chatroom.GotMembersListener() {
            @Override
            public void onGotMembers(final List<User> members) {
                for(final User member : members) {
                    member.getImage(new User.PictureDownloadedListener() {
                        @Override
                        public void onGotProfilePicture(File image) {
                            mMembers.add(new Pair<>(member, image));
                            if(mMembers.size() == members.size()) {
                                mChatroom.getCachedMessages(mMessageRecieved, null);
                            }
                        }
                    });
                }

            }
        });

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

        buttonCreatePoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectPasser.putObject("chatroomPoll", mChatroom);
                startActivity(new Intent(getApplicationContext(), CreatePollActivity.class));
            }
        });

        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectPasser.putObject("chatroomEvent", mChatroom);
                startActivity(new Intent(getApplicationContext(), CreateEventActivity.class));
            }
        });

        buttonPolls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChatroom.getChatFunctions(Poll.class, ChatFunctionActivity.chatFunctionsListener());
                ChatFunctionActivity.setOpenActivity(PollsActivity.class);
                startActivity(new Intent(getApplicationContext(), ChatFunctionActivity.class));
            }
        });

        buttonMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<String> members = new ArrayList<String>();
                Intent intent = new Intent(getApplicationContext(), MembersActivity.class);
//                for(Pair<User, File> member : mMembers ) {
//                    members.add(member.first.getUsername());
//                }
//                intent.putStringArrayListExtra("members", members);
                ObjectPasser.putObject("members", mMembers);
                startActivity(intent);
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
                mChatroom.getChatFunctions(Event.class, ChatFunctionActivity.chatFunctionsListener());
                ChatFunctionActivity.setOpenActivity(EventActivity.class);
                startActivity(new Intent(getApplicationContext(), ChatFunctionActivity.class));
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
        String msgTxt = chatText.getText().toString();
        Message msg = new Message(msgTxt);
        msg.setSender(AccountManager.getInstance().getCurrentAccount().getParseUser().getObjectId());
        if(msgTxt.contains("@")) {
            String text = msgTxt.toLowerCase();
            for (int i = 0; i < mMembers.size(); ++i) {     //loops thru members of chatroom and see if their display name was in the string
                if (text.indexOf(mMembers.get(i).first.getDisplayName().toLowerCase()) != -1) {
                    if (text.indexOf("@") == text.indexOf(mMembers.get(i).first.getDisplayName().toLowerCase()) - 1) {
                        msg.setTag(mMembers.get(i).first);
                        break;
                    }
                }
            }
        }
        mChatroom.sendMessage(msg);
        addChatMessage(msg);
        chatText.setText("");
    }

    private void addChatMessage(Message msg) {
        UserAccount curAcc = AccountManager.getInstance().getCurrentAccount();
        String curUsrId = curAcc.getParseUser().getObjectId();
        File userImage = null;
        String displayName = null;

        for(Pair<User, File> member : mMembers) {
            if(member.first.getParseUser().getObjectId().equals(msg.getSender())) {
                userImage = member.second;
                displayName = member.first.getDisplayName();
                chatArrayAdapter.add(new ChatMessageBubble(curUsrId.equals(msg.getSender()), msg.getText(), userImage, displayName));
                break;
            }
        }

        //compare users from members array list and set picture
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
            addChatMessage(msg);
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
