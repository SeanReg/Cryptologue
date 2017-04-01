package com.teamsynergy.cryptologue;

import java.io.File;

/**
 * Created by Sean on 3/31/2017.
 */

public class Message {
    private String mMsgText = "";

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

    public String getText() {
        return  mMsgText;
    }

    public File getAttachment() {
        return null;
    }
}
