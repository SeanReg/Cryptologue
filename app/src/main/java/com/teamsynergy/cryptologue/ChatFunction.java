package com.teamsynergy.cryptologue;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;

/**
 * Created by nadeen on 4/21/17.
 */

public class ChatFunction {

    /**
     * Function start date
     */
    protected Date mStartDate = new Date();

    /**
     * Function end date
     */
    protected Date mEndDate = new Date();

    /**
     * Function name
     */
    protected String mName = "";

    /**
     * Function description
     */
    protected String mDescritpion = "";

    /**
     * Function creator
     */
    protected User mCreator = null;

    /**
     * Function type, event or poll
     */
    protected String mType = null;

    /**
     * Provides ParseObject to do Parse functions, i.e. setting Parse variables
     */
    protected ParseObject mParseObj = null;

    /**
     * Constructs a ChatFunction object
     */
    public ChatFunction() {

    };

    // TESTER
    public ChatFunction(String name) {
        mName = name;
    };

    /**
     * Calculates the function's time remaining based on mStartDate and
     * mEndDate
     * @return  ChatFunction's time remaining
     */
    public String getTimeRemaining() {
        long diff = mEndDate.getTime() - mStartDate.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        int diffInDays = (int) diff / (1000 * 60 * 60 * 24);

        return diffInDays + "  days, " + diffHours + "  hours, " + diffMinutes+"  min(s) , " + diffSeconds + "  sec(s)";
    }

    /**
     * Gets the creator of the ChatFunction
     * @return  creator of ChatFunction
     */
    public User getCreator() {
        return mCreator;
    }

    /**
     * Gets the type of the ChatFunction
     * @return type of the ChatFunction
     */
    public String getType() {
        return mType;
    }

    /**
     * Participate in a ChatFunction
     * @param decision  polls choice/event attendance
     */
    public void participate(String decision) {

    }

    /**
     * Gets the name of ChatFunction
     * @return  name of ChatFunction
     */
    public String getName() {
        return mName;
    }

    public String getDescritpion() {
        return mDescritpion;
    }

    public Date getStart() {
        return mStartDate;
    }

    public Date getEnd() {
        return mEndDate;
    }
    /**
     * Class that constructs a ChatFunction using the appropriate parameters
     */
    public static class Builder {
        /**
         * The constructed ChatFunction
         */
        protected ChatFunction mCFunction;

        /**
         * Constructs a Builder with a null ChatFunction
         */
        public Builder() {
            mCFunction = null;
        }


        /**
         * Set ChatFunction start date
         * @param date  start date
         */
        public void setStartDate(Date date) {
            if (mCFunction != null)
                mCFunction.mStartDate = date;
        }

        /**
         * Set ChatFunction end date
         * @param date  end date
         */
        public void setEndDate(Date date) {
            if (mCFunction != null)
                mCFunction.mEndDate = date;
        }

        /**
         * Set ChatFunction name
         * @param name  name
         */
        public void setName(String name) {
            if (mCFunction != null)
                mCFunction.mName = name;
        }

        /**
         * Set ChatFunction description
         * @param desc  description
         */
        public void setDescription(String desc) {
            if (mCFunction != null)
                mCFunction.mDescritpion = desc;
        }

        /**
         * Set ChatFunction Parse object
         * @param parseObject   Parse object
         */
        public void setParseObject(ParseObject parseObject) {
            if (mCFunction != null)
                mCFunction.mParseObj = parseObject;
        }
    }
}
