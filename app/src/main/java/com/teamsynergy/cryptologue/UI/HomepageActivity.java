package com.teamsynergy.cryptologue.UI;

/**
 * Created by MatthewRedington on 3/26/17.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.teamsynergy.cryptologue.AccountManager;
import com.teamsynergy.cryptologue.Chatroom;
import com.teamsynergy.cryptologue.MessagingService;
import com.teamsynergy.cryptologue.ParseInit;
import com.teamsynergy.cryptologue.R;
import com.teamsynergy.cryptologue.UserAccount;

import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getActivity;

public class HomepageActivity extends AppCompatActivity {
    private final static int RESULT_LOGGED_IN = 1;
    private final static int RESULT_CHATROOM_CREATED = 2;

    private ListView mListView;
    private ArrayList<Chatroom> mChatroomList = new ArrayList<>();

    private ChatroomListAdapter mChatroomAdapter = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        ParseInit.start(this);

        Intent sIntent = new Intent(getApplicationContext(), MessagingService.class);
        startService(sIntent);

        mListView = (ListView) findViewById(R.id.chatroom_list);


        mChatroomAdapter = new ChatroomListAdapter(this, mChatroomList);
        mListView.setAdapter(mChatroomAdapter);

        if (AccountManager.getInstance().getCurrentAccount() == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, RESULT_LOGGED_IN);
        } else {
            updateChatrooms();
        }


/*        Chatroom.Builder cB = new Chatroom.Builder();
        cB.setName("Tetst");
        cB.build(true);*/



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.newChat:
                Intent intentNewChat = new Intent(this, CreateChatroomActivity.class);
                startActivityForResult(intentNewChat, RESULT_CHATROOM_CREATED);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == RESULT_LOGGED_IN) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                updateChatrooms();
            }
        } else if (requestCode == RESULT_CHATROOM_CREATED) {
            if (resultCode == RESULT_OK) {
                Intent Intent = new Intent(this, ChatroomActivity.class);
                Intent.putExtra("chatroom", data.getParcelableExtra("chatroom"));
                startActivity(Intent);
            }
        }
    }

    public void updateChatrooms() {
        UserAccount curAcc = AccountManager.getInstance().getCurrentAccount();
        curAcc.getChatrooms(new UserAccount.Callbacks() {
            @Override
            public void onGotChatrooms(List<Chatroom> rooms) {
                for (Chatroom room : rooms) {
                    Log.d("Room", room.getName());
                    mChatroomList.add(room);
                }
                mListView.setAdapter(mChatroomAdapter);
            }
        });
    }
}
