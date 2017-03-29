package com.teamsynergy.cryptologue;

import com.parse.ParseUser;

/**
 * Created by Sean on 3/23/2017.
 */

public class UserAccount extends User implements SecurityCheck {
    private boolean mIsValid = true;

    public UserAccount(String username, String displayName, String phonenumber, ParseUser parseUser) {
        super(username, displayName, phonenumber, parseUser);
    }

    @Override
    public void invalidate() {
        mIsValid = false;
    }
}
