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

    public Message(String text) {
        setText(text);
    }

    public void setText(String str) {
        mMsgText = str;
    }

    public void setAttachment(File file) {

    }

    public void setSender(String senderId) {
        mSender = senderId;
    }

    public String getSender() {
        return mSender;
    }

    public String getText() {
        return  mMsgText;
    }

    public File getAttachment() {
        return null;
    }

    public void setTag(User tag){
        mTag=tag;
    }

    public User getTag(){
        return mTag;
    }


}
