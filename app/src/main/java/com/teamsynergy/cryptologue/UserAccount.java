package com.teamsynergy.cryptologue;

import com.parse.FindCallback;
import com.parse.GetFileCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sean on 3/23/2017.
 */

public class UserAccount extends User implements SecurityCheck {
    private boolean mIsValid = true;

    public UserAccount(String username, String displayName, String phonenumber, ParseUser parseUser) {
        super(username, displayName, phonenumber, parseUser);
    }

    public void getChatrooms(final UserAccount.Callbacks listener) {
        if (mIsValid) {
            ParseQuery query = new ParseQuery("RoomLookup");
            query.whereEqualTo("user", getParseUser());
            query.include("chatroom");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        ArrayList<Chatroom> roomList = new ArrayList<Chatroom>();
                        for (ParseObject lookup : objects) {
                            ParseObject room = lookup.getParseObject("chatroom");

                            Chatroom.Builder builder = new Chatroom.Builder();
                            builder.setName(room.getString("name"));
                            builder.setParseObj(room);
                            roomList.add(builder.build(false, null));
                        }

                        if (listener != null) {
                            listener.onGotChatrooms(roomList);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void invalidate() {
        mIsValid = false;
    }

    public void getImage(final UserAccount.Callbacks listener) {
        getParseUser().getParseFile(AccountManager.FIELD_AVATAR)
                .getFileInBackground(new GetFileCallback() {
                    @Override
                    public void done(File file, ParseException e) {
                        if (e != null) return;
                        listener.onGotProfilePicture(file);
                    }
                });
    }

    public static abstract class Callbacks {
        public void onGotChatrooms(List<Chatroom> rooms) {

        }

        public void onGotProfilePicture(File image) {

        }
    }
}
