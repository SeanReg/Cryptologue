package com.teamsynergy.cryptologue;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sean on 3/29/2017.
 */

public class Chatroom implements SecurityCheck { //, Parcelable {
    private String mName = "";
    private ParseObject mParseObj = null;
    private ParseFile mImage = null;
    private ArrayList<User> mMembers = new ArrayList<>();

    private Chatroom() {

    }

    public String getName() {
        return mName;
    }

    public ParseFile getImage() {
        return mParseObj.getParseFile("icon");
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


    public String getId() {
        return mParseObj.getObjectId();
    }

    @Override
    public void invalidate() {

    }

/*    @Override
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
    }*/

    public static class Builder {
        private Chatroom mChatroom = new Chatroom();
        private boolean mIsBuilt = false;

        public void setName(String name) {
            if (!mIsBuilt)
                mChatroom.mName = name;
        }

        public void setParseObj(ParseObject parseObj) {
            if (!mIsBuilt)
                mChatroom.mParseObj = parseObj;
        }

        public void setImage(File image) {
            if (!mIsBuilt)
                mChatroom.mImage = new ParseFile(image);
        }

        public void addMember(User member) {
            if (!mIsBuilt) {
                for (User usr : mChatroom.mMembers) {
                    if (usr.getUsername().equals(member.getUsername()))
                        return;
                }
                mChatroom.mMembers.add(member);
            }
        }

        public Chatroom build(boolean isNew, final BuiltListener listener) {
            if (isNew && !mIsBuilt) {
                mIsBuilt = true;
                final UserAccount curUser = AccountManager.getInstance().getCurrentAccount();

                addMember(curUser);

                // save and get callback
                final ParseFile f = mChatroom.mImage;

                f.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        ParseObject room = new ParseObject("Chatrooms");
                        mChatroom.mParseObj = room;
                        room.put("name", mChatroom.mName);
                        room.put("icon", f);
                        room.getRelation("members").add(curUser.getParseUser());
                        Log.i("Order", "Image Uploaded");
                        room.saveInBackground(new ChatroomSaved(mChatroom, listener));
                    }
                });

            }

            return mChatroom;
        }
    }

    public interface BuiltListener {
        void onChatroomBuilt(Chatroom room);
    }
}
