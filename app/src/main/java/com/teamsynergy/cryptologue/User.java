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
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by jasonpinlac on 3/26/17.
 */

/**
 * Class representing a User
 */
public class User implements Parcelable {

    private   String    mUsername     = "";
    private   String    mDisplayName  = "";
    private   String    mPhoneNumber  = "";
    protected ParseUser mParseUser    = null;

    private PublicKey mPublicKey = null;

    /**
     * Default Constructor for a User object
     */
    public User(){

    }

    /**
     * Constructor for a User object
     * @param username String
     * @param displayName String
     * @param phonenumber String
     */
    public User(String username, String displayName, String phonenumber) {
        mDisplayName = displayName;
        mUsername    = username;
        mPhoneNumber = phonenumber;
    }

    /**
     * Constructor for a User object
     * @param username String
     * @param displayName String
     * @param phonenumber String
     * @param parseUser Database object
     */
    public User(String username, String displayName, String phonenumber, ParseUser parseUser, String pubKey) {
        mDisplayName = displayName;
        mUsername    = username;
        mPhoneNumber = phonenumber;
        mParseUser   = parseUser;

        String[] strings = pubKey.replace("[", "").replace("]", "").split(", ");
        byte[] arr = new byte[strings.length];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = Byte.parseByte(strings[i]);
        }
        try {
            mPublicKey = KeyManager.bytesToPublicKey(arr);
        } catch (KeyManager.KeyGenerationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Accessor for User username
     * @return String username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Accessor for User displayName
     * @return String displayName
     */
    public String getDisplayName() {
        return mDisplayName;
    }

    /**
     * Accessor for User parseUser
     * @return ParseUser parseUser
     */
    public ParseUser getParseUser() {
        return mParseUser;
    }

    /**
     * Accessor for User phoneNumber
     * @return String phoneNumber
     */
    public String getPhoneNumber() { return mPhoneNumber; }

    public PublicKey getPublicKey() {
        return mPublicKey;
    }

    /**
     * Accessor for getting a picture
     * @param listener handles the callback for getting a picture
     */
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
        dest.writeSerializable(mPublicKey);
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
        mPublicKey = (PublicKey) in.readSerializable();
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

    /**
     * Finds a list of User by their phone number.
     * @param numbers List of String phone numbers
     * @param listener Handles the callback after finding the Users
     */
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
                                usr.getString(AccountManager.FIELD_PHONE_NUMBER), usr, usr.getString(AccountManager.FIELD_PUBLIC_KEY)));
                    }

                    listener.onUsersFound(users);
                }
            }
        });
    }
}
