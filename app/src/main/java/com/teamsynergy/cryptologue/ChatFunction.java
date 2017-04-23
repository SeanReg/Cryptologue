package com.teamsynergy.cryptologue;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;

/**
 * Created by nadeen on 4/21/17.
 */

public class ChatFunction {

    protected Date mStartDate = new Date();
    protected Date mEndDate = new Date();
    protected String mName = "";
    protected String mDescritpion = "";
    protected User mCreator = null;
    protected String mType = null;

    protected ParseObject mParseObj = null;

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

    public static class Builder {
        protected ChatFunction mCFunction;

        public Builder() {
            mCFunction = null;
        }

        public void setStartDate(Date date) {
            if (mCFunction != null)
                mCFunction.mStartDate = date;
        }

        public void setEndDate(Date date) {
            if (mCFunction != null)
                mCFunction.mEndDate = date;
        }

        public void setName(String name) {
            if (mCFunction != null)
                mCFunction.mName = name;
        }

        public void setDescription(String desc) {

        }

        public void setParseObject(ParseObject parseObject) {
            if (mCFunction != null)
                mCFunction.mParseObj = parseObject;
        }
    }
}
