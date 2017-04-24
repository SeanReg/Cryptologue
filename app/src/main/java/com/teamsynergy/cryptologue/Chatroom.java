package com.teamsynergy.cryptologue;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sean on 3/29/2017.
 */

/**
 * Class that represents a a Chatroom
 */
public class Chatroom implements SecurityCheck { //, Parcelable {
    /**
     * Chatroom's name
     */
    private String mName = "";

    /**
     * Chatroom database object
     */
    private ParseObject mParseObj = null;

    /**
     * Chatroom database file object
     */
    private ParseFile mImage = null;

    /**
     * Chatroom list of members
     */
    private ArrayList<User> mMembers = new ArrayList<>();

    /**
     * Chatroom default constructor
     */
    private Chatroom() {

    }

    /**
     * Accessor for Chatroom name
     * @return Chatroom name
     */
    public String getName() {
        return mName;
    }

    /**
     * Accessor for downloading an image from the database
     * @param listener notifies that the image is ready
     */
    public void getImage(GetDataCallback listener) {
        if (mParseObj.getParseFile("icon") == null)
            return;

        mParseObj.getParseFile("icon").getDataInBackground(listener);
    }

    /**
     * Sends a message to another user
     * @param msg Message object to be sent
     */
    public void sendMessage(Message msg) {
        msg.setSender(AccountManager.getInstance().getCurrentAccount().getParseUser().getObjectId());

        
        cacheMessage(msg);
        MessagingService.getInstance().socketSendMessage(msg.getText(), mParseObj.getObjectId());
    }

    /**
     * Sets the call back for the messenger
     * @param listener notifies that a message has been received
     */
    public void setMessageListener(final MessagingService.MessageListener listener) {
        MessagingService.getInstance().setMessagingListener(new MessagingService.MessageListener() {
            @Override
            public void onMessageRecieved(Message s) {
                cacheMessage(s);

                listener.onMessageRecieved(s);
            }
        });
    }

    /**
     * Locally stores messages a user has received
     * @param msg Message object to cache
     */
    private void cacheMessage(Message msg) {
        ParseObject msgObject = new ParseObject("Messages");
        msgObject.put("userId", msg.getSender());
        msgObject.put("chatroomId", mParseObj.getObjectId());
        msgObject.put("text", msg.getText());
        msgObject.pinInBackground();
    }

    /**
     * Accessor that retrieves cached messages
     * @param listener MessagingService listener
     * @param limit Limit
     */
    public void getCachedMessages(final MessagingService.MessageListener listener, Integer limit) {
        ParseQuery query = new ParseQuery("Messages");
        if (limit == null || limit == 0)
            query.orderByAscending("createdAt");
        else
            query.orderByDescending("createdAt");
        query.whereEqualTo("chatroomId", mParseObj.getObjectId());
        if (limit != null) query.setLimit(limit);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject obj : objects) {
                    Message msg = new Message(obj.getString("text"));
                    msg.setSender(obj.getString("userId"));
                    listener.onMessageRecieved(msg);
                }
            }
        });
    }

    /**
     * Invites a single user to a chatroom
     * @param inv User
     */
    public void inviteUser(User inv) {
        inviteUsers(Arrays.asList(inv));
    }

    /**
     * Invites multiple users to a chatroom
     * @param inv List of users
     */
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

    /**
     * Nested class for managing database saving chatroom details
     */
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

    public void getMembers(final GotMembersListener listener) {
        ParseQuery query = new ParseQuery("RoomLookup");
        query.whereEqualTo("chatroom", mParseObj);
        query.include("user");
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e != null) return;
                mMembers.clear();
                for(ParseObject room : objects) {
                    ParseUser usr = (ParseUser) room.get("user");
                    mMembers.add(new User(usr.getUsername(), usr.getString(AccountManager.FIELD_DISPLAY_NAME),
                            usr.getString(AccountManager.FIELD_PHONE_NUMBER), usr));
                }

                if(listener != null) listener.onGotMembers(mMembers);
            }
        });
    }

    public void getChatFunctions(final Class<? extends ChatFunction> funcClass, final GotChatFunctionsListener listener) {
        String dbName = (funcClass.getSimpleName() + "s").toLowerCase();
        ParseQuery query = mParseObj.getRelation(dbName).getQuery();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null)
                    return;

                try {
                    Class[] classes = funcClass.getDeclaredClasses();
                    Class builderC = null;
                    for (Class c : classes) {
                        if (c.getSuperclass() == ChatFunction.Builder.class) {
                            builderC = c;
                            break;
                        }
                    }
                    Method build = builderC.getMethod("buildFromParseObjects",
                            new Class[]{ArrayList.class, Chatroom.class});
                    ArrayList<ChatFunction> functions = (ArrayList<ChatFunction>)build.invoke(null, objects, Chatroom.this);
                    listener.onGotChatFuntions(functions);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    /**
     * Class that represents a chatroom Builder object
     */
    public static class Builder {
        /**
         * Chatroom object
         */
        private Chatroom mChatroom = new Chatroom();

        /**
         * Boolean flag for building a chatroom
         */
        private boolean mIsBuilt = false;

        /**
         * Sets name to the chatroom object
         * @param name String
         */
        public void setName(String name) {
            if (!mIsBuilt)
                mChatroom.mName = name;
        }

        /**
         * Sets the ParseObject to the chatroom object.
         * @param parseObj ParseObject
         */
        public void setParseObj(ParseObject parseObj) {
            if (!mIsBuilt)
                mChatroom.mParseObj = parseObj;
        }

        /**
         * Sets the image file to the chatroom object.
         * @param image File
         */
        public void setImage(File image) {
            if (!mIsBuilt)
                mChatroom.mImage = new ParseFile(image);
        }

        /**
         * Adds a member to the chatroom object.
         * @param member User
         */
        public void addMember(User member) {
            if (!mIsBuilt) {
                for (User usr : mChatroom.mMembers) {
                    if (usr.getUsername().equals(member.getUsername()))
                        return;
                }
                mChatroom.mMembers.add(member);
            }
        }

        /**
         * Build a chatroom
         * @param isNew flag for communicating with the database
         * @param listener handles the callback for a built chatroom
         * @return chatroom object
         */
        public Chatroom build(boolean isNew, final BuiltListener listener) {
            if (isNew && !mIsBuilt) {
                final UserAccount curUser = AccountManager.getInstance().getCurrentAccount();

                addMember(curUser);

                // save and get callback
                final ParseFile f = mChatroom.mImage;
                final ParseObject room = new ParseObject("Chatrooms");

                if(mChatroom.mImage == null) {
                        saveChatroom(curUser, room, f, listener);
                } else {
                    f.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            saveChatroom(curUser, room, f, listener);
                        }
                    });
                }


                mIsBuilt = true;
            }

            return mChatroom;
        }

        public void saveChatroom(UserAccount currentUser, ParseObject room, ParseFile f, final BuiltListener listener) {
            room.saveInBackground(new ChatroomSaved(mChatroom, listener));

            mChatroom.mParseObj = room;
            room.put("name", mChatroom.mName);
            if(f != null) { room.put("icon", f); }
            room.getRelation("members").add(currentUser.getParseUser());
            Log.i("Order", "Image Uploaded");
        }
    }

    public interface BuiltListener {
        void onChatroomBuilt(Chatroom room);
    }

    public interface GotMembersListener {
        void onGotMembers(List<User> members);
    }

    public interface GotChatFunctionsListener {
        void onGotChatFuntions(List<ChatFunction> functions);
    }
}
