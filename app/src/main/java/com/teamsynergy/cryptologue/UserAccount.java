package com.teamsynergy.cryptologue;

/**
 * Created by Sean on 3/23/2017.
 */

public class UserAccount extends User implements SecurityCheck {
    private boolean mIsValid = true;

    @Override
    public void invalidate() {
        mIsValid = false;
    }
}
