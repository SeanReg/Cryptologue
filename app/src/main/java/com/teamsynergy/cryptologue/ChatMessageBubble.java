package com.teamsynergy.cryptologue;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nadeen on 3/26/17.
 */

public class ChatMessageBubble {
    public boolean left;
    public String message;
    public View mView;

    public ChatMessageBubble(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }
}
