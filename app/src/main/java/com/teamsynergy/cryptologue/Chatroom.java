package com.teamsynergy.cryptologue;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
        cacheMessage(msg);
        MessagingService.getInstance().socketSendMessage(msg.getText(), mParseObj.getObjectId());
    }

    public void setMessageListener(final MessagingService.MessageListener listener) {
        MessagingService.getInstance().setMessagingListener(new MessagingService.MessageListener() {
            @Override
            public void onMessageRecieved(Message s) {
                cacheMessage(s);

                listener.onMessageRecieved(s);
            }
        });
    }

    private void cacheMessage(Message msg) {
        ParseObject msgObject = new ParseObject("Messages");
        msgObject.put("userId", "");
        msgObject.put("chatroomId", mParseObj.getObjectId());
        msgObject.put("text", msg.getText());
        msgObject.pinInBackground();
    }

    public void getCachedMessages(final MessagingService.MessageListener listener) {
        ParseQuery query = new ParseQuery("Messages");
        query.orderByAscending("createdAt");
        query.whereEqualTo("chatroomId", mParseObj.getObjectId());
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject obj : objects) {
                    Message msg = new Message(obj.getString("text"));
                    listener.onMessageRecieved(msg);
                }
            }
        });
    }

    public void inviteUser(User inv) {
        inviteUsers(Arrays.asList(inv));
    }

    public void inviteUsers(List<User> inv) {
        ArrayList<ParseObject> invObjs = new ArrayList<>();
        for (int i = 0; i < inv.size(); ++i) {
            User usr = inv.get(i);

            if (mMembers != inv) {
                if (mMembers.contains(usr)) continue;
                else mMembers.add(usr);
            }

            ParseObject obj = new ParseObject("RoomLookup");
            obj.put("user", usr.getParseUser());
            obj.put("chatroom", mParseObj);
            //obj.saveInBackground();
            invObjs.add(obj);
        }
        ParseObject.saveAllInBackground(invObjs);
    }

    private static class ChatroomSaved implements SaveCallback {
        private final BuiltListener mBuiltListener;
        private final Chatroom mChatroom;

        public ChatroomSaved(Chatroom room, BuiltListener listener) {
            mBuiltListener = listener;
            mChatroom = room;
        }

        @Override
        public void done(ParseException e) {
            if (e != null) {
                mChatroom.invalidate();
                e.printStackTrace();
            } else {
                mChatroom.inviteUsers(mChatroom.mMembers);
                if (mBuiltListener != null)
                    mBuiltListener.onChatroomBuilt(mChatroom);
            }
        }
    }


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
        in.readList(mMembers, getClass().getClassLoader());

        mParseObj = ParseObject.createWithoutData("Chatrooms", in.readString());
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

        public Chatroom build(boolean isNew, BuiltListener listener) {
            if (isNew && mChatroom != null) {
                UserAccount curUser = AccountManager.getInstance().getCurrentAccount();

                addMember(curUser);

                ParseObject room = new ParseObject("Chatrooms");
                mChatroom.mParseObj = room;
                room.put("name", mChatroom.mName);
                room.getRelation("members").add(curUser.getParseUser());
                room.saveInBackground(new ChatroomSaved(mChatroom, listener));
            }

            Chatroom chat = mChatroom;
            mChatroom = null;

            return chat;
        }
    }

    public interface BuiltListener {
        void onChatroomBuilt(Chatroom room);
    }
}
