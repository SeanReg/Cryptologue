package com.teamsynergy.cryptologue;

import java.util.Date;

/**
 * Created by nadeen on 4/21/17.
 */

public class ChatFunction {

    private Date mStartDate = null;
    private Date mEndDate = null;
    private String mName = "";
    private String mDescritpion = "";
    private User mCreator = null;
    protected String mType = null;

    public ChatFunction() {

    };

    // test
    public ChatFunction(String name) {
        mName = name;
    };

    public String getTimeRemaining() {
        long diff = mEndDate.getTime() - mStartDate.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        int diffInDays = (int) diff / (1000 * 60 * 60 * 24);

        return diffInDays + "  days, " + diffHours + "  hours, " + diffMinutes+"  min(s) , " + diffSeconds + "  sec(s)";
    }

    public User getCreator() {
        return mCreator;
    }

    public String getType() {
        return mType;
    }

    public void participate(String decision) {

    }

    public String getName() {
        return mName;
    }
}
