package com.teamsynergy.cryptologue;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.lang.reflect.Array;
import java.security.Security;
import java.util.ArrayList;

/**
 * Created by Sean on 3/23/2017.
 */

public class AccountManager {
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


    private AccountManager() {

    }

    public static AccountManager getInstance() {
        return mInstance;
    }

    /**
     * Registers a new user with the Parse server. A successful registration
     * will produce a onRegistered callback. If there were errors while registering then
     * a onRegistrationError callback will occur
     * @param username the username of the new account - an error will be produced if this
     * username is already taken
     * @param password    the password  the password of the account - must meet a length requirement
     * of 8
     * @param displayName the display name for the account
     * @param phone       (optional) the phonenumber for the account allowing a user to be searched
     * by their phone number
     */
    public void register(String username, String displayName, String password, String phone, final onAccountStatus callback) {
        ParseUser user = new ParseUser();
        //user.setEmail(username);
        //Lower case the username so that usernames for login are not case sensistive
        user.setUsername(username.toLowerCase());
        user.setPassword(password);
        user.put(FIELD_DISPLAY_NAME, displayName);
        user.put(FIELD_PHONE_NUMBER, phone);
        //Save a correctly cased version of the username for display purposes
        user.put(FIELD_USERNAME_CASE, username);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (callback != null) {
                    if (e == null) {
                        UserAccount account = new UserAccount();
                        callback.onRegistered(account);
                    } else {
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
     */
    public void login(String username, String password, final onAccountStatus callback) {
        // if (isSignedIn()) logOut();

        try {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (callback != null) {
                        if (user != null) {
                            // Hooray! The user is logged in
                            UserAccount account = new UserAccount();
                            callback.onLogin(account);
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
    public abstract class onAccountStatus {
        /**
         * Called when a user has successfully been
         * authenticated with the Parse server
         * @param account the Account of the newly logged in user
         */
        public abstract void onLogin(UserAccount account);

        /**
         * Called when a user has successfully been
         * registered with the Parse server
         * @param account the Account of the newly registered in user
         */
        public abstract void onRegistered(UserAccount account);

        /**
         * Called when an authentication error has occured
         * with the Parse server while attempting to log a user
         * in
         * @param e contains the ParseException and reason that the the login failed
         */
        public abstract void onLoginError(ParseException e);

        /**
         * Called when a signup error has occured
         * with the Parse server while attempting to register a
         * new user
         * @param e contains the ParseException and reason that the the registration failed
         */
        public abstract void onRegistrationError(ParseException e);
    }
}
