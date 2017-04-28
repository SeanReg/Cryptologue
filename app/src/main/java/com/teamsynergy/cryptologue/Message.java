package com.teamsynergy.cryptologue;

import java.io.File;

/**
 * Created by Sean on 3/31/2017.
 */

public class Message {
    private String mMsgText = "";
    private String mSender = "";
    private User mTag;

    public Message() {

    }

    /**
     * Constructs a Message object used to manage chat messages
     * @param text the text for the message
     */
    public Message(String text) {
        setText(text);
    }

    /**
     * Sets the text for the message
     * @param str the String containing the message text
     */
    public void setText(String str) {
        mMsgText = str;
    }

    public void setAttachment(File file) {

    }

    /**
     * Sets the person who sent the message
     * @param senderId the sender of the message
     */
    public void setSender(String senderId) {
        mSender = senderId;
    }

    /**
     * Gets the sender of the message
     * @return the Id of the sender of the message
     */
    public String getSender() {
        return mSender;
    }

    /**
     * Gets the text of the Message
     * @return a String containing the Text from the Message
     */
    public String getText() {
        return  mMsgText;
    }

    public File getAttachment() {
        return null;
    }

    /**
     * Tags a user in the Message
     * @param tag the User tagged in the message
     */
    public void setTag(User tag){
        mTag=tag;
    }

    /**
     * Gets the User that was tagged in the message
     * @return the User tagged in the message
     */
    public User getTag(){
        return mTag;
    }

    @Override
    public Message clone() {
        Message clone = new Message();
        clone.mMsgText = mMsgText;
        clone.mSender = mSender;
        clone.mTag = mTag;

        return clone;
    }

}
