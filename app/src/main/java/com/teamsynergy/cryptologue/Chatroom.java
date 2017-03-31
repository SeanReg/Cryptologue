package com.teamsynergy.cryptologue;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Sean on 3/29/2017.
 */

public class Chatroom implements SecurityCheck, Parcelable {
    private String mName = "";
    private ParseObject mParseObj = null;

    private Chatroom() {

    }

    public String getName() {
        return mName;
    }

    public String getId() {
        return mParseObj.getObjectId();
    }

    private final SaveCallback mCreatedCallback = new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e != null) {
                invalidate();
                e.printStackTrace();
            } else {
                ParseUser curUser = AccountManager.getInstance().getCurrentAccount().getParseUser();

                ParseObject lookup = new ParseObject("RoomLookup");
                lookup.put("user", curUser);
                lookup.put("chatroom", mParseObj);
                lookup.saveInBackground();
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
        dest.writeString(mParseObj.getObjectId());
    }

    public static final Parcelable.Creator<Chatroom> CREATOR
            = new Parcelable.Creator<Chatroom>() {
        public Chatroom createFromParcel(Parcel in) {
            return new Chatroom(in);
        }

        public Chatroom[] newArray(int size) {
            return new Chatroom[size];
        }
    };

    private Chatroom(Parcel in) {
        mName = in.readString();
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

        public Chatroom build(boolean isNew) {
            if (isNew && mChatroom != null) {
                ParseUser curUser = AccountManager.getInstance().getCurrentAccount().getParseUser();

                ParseObject room = new ParseObject("Chatrooms");
                mChatroom.mParseObj = room;
                room.put("name", mChatroom.mName);
                room.getRelation("members").add(curUser);
                room.saveInBackground(mChatroom.mCreatedCallback);
            }

            Chatroom chat = mChatroom;
            mChatroom = null;

            return chat;
        }
    }
}
