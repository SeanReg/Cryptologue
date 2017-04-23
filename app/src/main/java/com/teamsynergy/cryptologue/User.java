package com.teamsynergy.cryptologue;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.FindCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by jasonpinlac on 3/26/17.
 */

public class User implements Parcelable {
    private   String    mUsername     = "";
    private   String    mDisplayName  = "";
    private   String    mPhoneNumber  = "";
    protected ParseUser mParseUser    = null;


    public User(){

    }

    public User(String username, String displayName, String phonenumber) {
        mDisplayName = displayName;
        mUsername    = username;
        mPhoneNumber = phonenumber;
    }

    public User(String username, String displayName, String phonenumber, ParseUser parseUser) {
        mDisplayName = displayName;
        mUsername    = username;
        mPhoneNumber = phonenumber;
        mParseUser   = parseUser;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public ParseUser getParseUser() {
        return mParseUser;
    }

    public String getPhoneNumber() { return mPhoneNumber; }


    public void getImage(final PictureDownloadedListener listener) {
        ParseFile avatar = mParseUser.getParseFile(AccountManager.FIELD_AVATAR);

        if (avatar == null) {
            listener.onGotProfilePicture(null);
         return;
        }

        avatar.getFileInBackground(new GetFileCallback() {
            @Override
            public void done(File file, ParseException e) {
                if (e != null) return;
                listener.onGotProfilePicture(file);
            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDisplayName);
        dest.writeString(mUsername);
        dest.writeString(mPhoneNumber);
        dest.writeString(mParseUser.getObjectId());
    }

    public static final Parcelable.Creator<User> CREATOR  = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        mDisplayName = in.readString();
        mUsername = in.readString();
        mPhoneNumber = in.readString();
        mParseUser = new ParseUser();
        mParseUser.setObjectId(in.readString());
    }

    @Override
    public boolean equals(Object usr) {
        if (usr instanceof User) {
            return getUsername().equals(usr);
        }
        return super.equals(usr);
    }

    public interface PictureDownloadedListener {
        public void onGotProfilePicture(File image);
    }

    public interface UsersFoundListener {
        public void onUsersFound(List<User> users);
    }

    public static void findByPhoneNumber(List<String> numbers, final UsersFoundListener listener) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn(AccountManager.FIELD_PHONE_NUMBER, numbers);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (listener !=  null && e == null) {
                    ArrayList<User> users = new ArrayList<User>();
                    for (ParseUser usr : objects) {
                        users.add(new User(usr.getUsername(), usr.getString(AccountManager.FIELD_DISPLAY_NAME),
                                usr.getString(AccountManager.FIELD_PHONE_NUMBER), usr));
                    }

                    listener.onUsersFound(users);
                }
            }
        });
    }
}
