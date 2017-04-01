package com.teamsynergy.cryptologue;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sean on 3/29/2017.
 */

public class Chatroom implements SecurityCheck, Parcelable {
    private String mName = "";
    private ParseObject mParseObj = null;
    private ArrayList<User> mMembers = new ArrayList<>();

    private Chatroom() {

    }

    public String getName() {
        return mName;
    }

    public void sendMessage(Message msg) {
        MessagingService.getInstance().socketSendMessage(msg.getText(), mParseObj.getObjectId());
    }

    public void setMessageListener(MessagingService.MessageListener listener) {
        MessagingService.getInstance().setMessagingListener(listener);
    }

    public void inviteUser(User inv) {
        inviteUsers(Arrays.asList(inv));
    }

    public void inviteUsers(List<User> inv) {
        ArrayList<ParseObject> invObjs = new ArrayList<>();
        for (User usr : inv) {
            ParseObject obj = new ParseObject("RoomLookup");
            obj.put("user", usr.getParseUser());
            obj.put("chatroom", mParseObj);
            invObjs.add(obj);
        }
        ParseObject.saveAllInBackground(invObjs);
    }

    private final SaveCallback mCreatedCallback = new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e != null) {
                invalidate();
                e.printStackTrace();
            } else {
                inviteUsers(mMembers);
            }
        }
    };


    @Override
    public void invalidate() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeList(mMembers);
        dest.writeString(mParseObj.getObjectId());
    }

    public static final Parcelable.Creator<Chatroom> CREATOR = new Parcelable.Creator<Chatroom>() {
        public Chatroom createFromParcel(Parcel in) {
            return new Chatroom(in);
        }

        public Chatroom[] newArray(int size) {
            return new Chatroom[size];
        }
    };

    private Chatroom(Parcel in) {
        mName = in.readString();
        in.readList(mMembers, null);

        mParseObj = new ParseObject("Chatrooms");
        mParseObj.setObjectId(in.readString());
    }

    public static class Builder {
        private Chatroom mChatroom = new Chatroom();

        public void setName(String name) {
            if (mChatroom != null)
                mChatroom.mName = name;
        }

        public void setParseObj(ParseObject parseObj) {
            if (mChatroom != null)
                mChatroom.mParseObj = parseObj;
        }

        public void addMember(User member) {
            if (mChatroom != null) {
                for (User usr : mChatroom.mMembers) {
                    if (usr.getUsername().equals(member.getUsername()))
                        return;
                }
                mChatroom.mMembers.add(member);
            }
        }

        public Chatroom build(boolean isNew) {
            if (isNew && mChatroom != null) {
                UserAccount curUser = AccountManager.getInstance().getCurrentAccount();

                addMember(curUser);

                ParseObject room = new ParseObject("Chatrooms");
                mChatroom.mParseObj = room;
                room.put("name", mChatroom.mName);
                room.getRelation("members").add(curUser.getParseUser());
                room.saveInBackground(mChatroom.mCreatedCallback);
            }

            Chatroom chat = mChatroom;
            mChatroom = null;

            return chat;
        }
    }
}
