package com.teamsynergy.cryptologue;

import android.content.Context;
import android.content.Intent;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.teamsynergy.cryptologue.UI.LoginActivity;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

/**
 * Created by Sean on 3/23/2017.
 */

/**
 * Class that handles security and authentication for UserAccounts
 */
public class AccountManager {
    /**
     * Single instance of AccountManager
     */
    private static final AccountManager mInstance = new AccountManager();

    private final ArrayList<SecurityCheck> mSecurityObjs = new ArrayList<>();

    /**
     * The constant FIELD_USERNAME_CASE.
     */
    public static final String FIELD_USERNAME_CASE  = "usernameCase";

    /**
     * The constant FIELD_USERNAME.
     */
    public static final String FIELD_USERNAME       = "username";

    /**
     * The constant FIELD_PHONE_NUMBER.
     */
    public static final String FIELD_PHONE_NUMBER   = "phone";

    /**
     * The constant FIELD_DISPLAY_NAME.
     */
    public static final String FIELD_DISPLAY_NAME   = "displayName";

    /**
     * The constant FIELD_AVATAR.
     */
    public static final String FIELD_AVATAR   = "avatar";

    public static final String FIELD_PUBLIC_KEY = "publicKey";

    /**
     * Current UserAccount
     */
    private UserAccount mCurAccount = null;

    /**
     * Constructs an AccountManager
     */
    private AccountManager() {

    }

    public static void initialize(Context context) {
        if (mInstance.mCurAccount == null && ParseUser.getCurrentUser() != null) {
            try {
                String alias = ParseUser.getCurrentUser().getUsername();
                mInstance.constructCurrentAccount(new KeyManager(context, alias));
            } catch (KeyManager.KeyGenerationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Provides an instance of the AccountManager
     * @return  Singleton instance of AccountManager
     */
    public static AccountManager getInstance() {
        return mInstance;
    }

    /**
     * Provides the current UserAccount logged in
     * @return  Current UserAccount logged in
     */
    public UserAccount getCurrentAccount() {
        return mCurAccount;
    }

    /**
     * Registers a new user with the Parse server. A successful registration
     * will produce a onRegistered callback. If there were errors while registering then
     * a onRegistrationError callback will occur
     * @param username  the username of the new account - an error will be produced if this
     * username is already taken
     * @param password  the password of the account - must meet a length requirement of 4
     * @param displayName   the display name for the account
     * @param phone the phone number for the account allowing a user to be searched
     * by their phone number
     * @param callback  successfully registers and logs user in or produces a onRegistrationError callback
     *                  that returns error from registration validation
     */
    public void register(Context context, String username, String displayName, String password, String phone, final onAccountStatus callback) {
        KeyManager kM = null;
        try {
            kM = new KeyManager(context, username.toLowerCase());
        } catch (KeyManager.KeyGenerationException e) {
            e.printStackTrace();
            if (callback != null)
                callback.onRegistrationError(new ParseException(e.getCause()));
        }
        final KeyManager manager = kM;

        ParseUser user = new ParseUser();
        //user.setEmail(username);
        //Lower case the username so that usernames for login are not case sensistive
        user.setUsername(username.toLowerCase());
        user.setPassword(password);
        user.put(FIELD_DISPLAY_NAME, displayName);
        user.put(FIELD_PHONE_NUMBER, phone);
        //Save a correctly cased version of the username for display purposes
        user.put(FIELD_USERNAME_CASE, username);
        user.put(FIELD_PUBLIC_KEY, Base64.encodeToString(kM.getPublicKey().getEncoded(), 0));

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (callback != null) {
                    if (e == null) {
                        constructCurrentAccount(manager);
                        callback.onRegistered(mCurAccount);
                    } else {
                        try {
                            manager.destroyKey();
                        } catch (KeyManager.KeyGenerationException e1) {
                            e1.printStackTrace();
                        }
                        callback.onRegistrationError(e);
                    }
                }
            }
        });
    }

    /**
     * Attempts to log in the user with the given username and password. If a login is successful
     * then a session token is returned, allowing the user to remain logged in until they sign out
     * @param username the username of the user's account
     * @param password the password of the user's account
     * @param callback  either logs user in order produces an onLoginError that returns ParseException
     *                  to user
     */
    public void login(final Context context, final String username, String password, final onAccountStatus callback) {
        // if (isSignedIn()) logOut();

        try {
            ParseUser.logInInBackground(username.toLowerCase(), password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (callback != null) {
                        if (user != null) {
                            // Hooray! The user is logged in
                            try {
                                constructCurrentAccount(new KeyManager(context, user.getUsername()));
                                callback.onLogin(mCurAccount);
                            } catch (KeyManager.KeyGenerationException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            // Login failed. Look at the ParseException to see what happened.
                            callback.onLoginError(e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param username  the updated username of the user's account
     * @param displayName   the updated display name of the user's account
     * @param password  the updated password of the user's account
     * @param phone     the phone number of the user's account - does not get updated
     * @param avatar    the profile picture of the user's account
     * @param callback  produces an onSave successful or onSaveError callback to denote what succeeded
     *                  and what went wrong upon update
     */
    public void updateAccount(final Context context, final String username, String displayName, String password, String phone, File avatar, final onAccountStatus callback) {
        final ParseUser user = ParseUser.getCurrentUser();
        //user.setEmail(username);
        //Lower case the username so that usernames for login are not case sensistive
        user.setUsername(username.toLowerCase());
        if(!password.equals("")) { user.setPassword(password); };
        user.put(FIELD_DISPLAY_NAME, displayName);
        user.put(FIELD_PHONE_NUMBER, phone);
        //Save a correctly cased version of the username for display purposes
        user.put(FIELD_USERNAME_CASE, username);
        if(avatar != null) { user.put(FIELD_AVATAR, new ParseFile(avatar)); }
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (callback != null) {
                    if (e == null) {
                        try {
                            constructCurrentAccount(new KeyManager(context, user.getUsername()));
                            callback.onSave();
                        } catch (KeyManager.KeyGenerationException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        callback.onSaveError(e);
                    }
                }
            }
        });
    }

    /**
     * Constructs a UserAccount object from the current ParseUser
     */
    private void constructCurrentAccount(KeyManager manager) {
        ParseUser pUser = ParseUser.getCurrentUser();
        mCurAccount = new UserAccount(manager, pUser.getUsername(),
                (String)pUser.get("displayName"), (String)pUser.get("phone"), pUser);

        ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
        parseACL.setPublicReadAccess(true);
        pUser.setACL(parseACL);

        // Update Installation
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "389198639837");
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();
    }

    public <T extends SecurityCheck> T createSecurityObject(Class<T> create) {
        try {
            T obj = create.newInstance();
            mSecurityObjs.add(obj);

            return obj;
        } catch (Exception e) {
            throw new SecurityException(e.getMessage());
        }
    }

    /**
     * Logs out the current UserAccount
     */
    public void logout() {
        ParseUser.logOut();

        for (SecurityCheck s : mSecurityObjs) {
            s.invalidate();
        }
        mSecurityObjs.clear();
    }

    /**
     * OBSERVER design pattern
     * Notifies other parts of the application about the status of the user's
     * authentication
     *
     * The interface On account status.
     */
    public static abstract class onAccountStatus {
        /**
         * Called when a user has successfully been
         * authenticated with the Parse server
         * @param account the Account of the newly logged in user
         */
        public void onLogin(UserAccount account) {};

        /**
         * Called when a user has successfully been
         * registered with the Parse server
         * @param account the Account of the newly registered in user
         */
        public void onRegistered(UserAccount account) {};

        /**
         * Called when an authentication error has occured
         * with the Parse server while attempting to log a user
         * in
         * @param e contains the ParseException and reason that the login failed
         */
        public void onLoginError(ParseException e) {};

        /**
         * Called when a signup error has occured
         * with the Parse server while attempting to register a
         * new user
         * @param e contains the ParseException and reason that the the registration failed
         */
        public void onRegistrationError(ParseException e) {};

        /**
         * Called when a user has successfully updated their account settings
         */
        public void onSave() {};

        /**
         * Called when a validation error occured when attempting to save
         * account settings
         * @param e contains the ParseException and reason that the save failed
         */
        public void onSaveError(ParseException e) {};
    }
}
