package com.teamsynergy.cryptologue;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nadeen on 3/26/17.
 */

public class ChatMessage {
    public boolean left;
    public String message;
    public View mView;
    public ViewGroup parent;

    public ChatMessage(boolean left, String message, ViewGroup parentGroup) {
        super();
        this.left = left;
        this.message = message;
        this.parent = parentGroup;
    }
}
