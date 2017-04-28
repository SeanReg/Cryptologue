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
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sean on 3/23/2017.
 */

/**
 * Class that extends User to manipulate the current account
 */
public class UserAccount extends User implements SecurityCheck {
    private boolean mIsValid = true;

    private final KeyManager mKeyManager;

    /**
     * Constructs a UserAccount
     * @param username
     * @param displayName
     * @param phonenumber
     * @param parseUser
     */
    public UserAccount(KeyManager kM, String username, String displayName, String phonenumber, ParseUser parseUser) {
        super(username, displayName, phonenumber, parseUser, kM.getPublicKey().getEncoded());

        mKeyManager = kM;
    }

    public PrivateKey getPrivateKey() {
        return mKeyManager.getPrivateKey();
    }

    public byte[] generateSymmetriccKey(String alias, boolean generate) throws KeyManager.KeyGenerationException {
        return mKeyManager.getSymmetricKey(alias, generate);
    }

    /**
     * Queries for the chatrooms user is a participant of
     * @param listener  listens for the list of chatrooms
     */
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

    /**
     * Class for callback that returns list of chatrooms
     */
    public static abstract class Callbacks {
        public void onGotChatrooms(List<Chatroom> rooms) {

        }
    }
}
