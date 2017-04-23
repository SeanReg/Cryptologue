package com.teamsynergy.cryptologue;

import android.view.View;
import android.view.ViewGroup;

import java.io.File;

/**
 * Created by nadeen on 3/26/17.
 */

public class ChatMessageBubble {
    public boolean left;
    public String message;
    public View mView;
    public File mUserImage;
    public String mUserDisplayName;

    public ChatMessageBubble(boolean left, String message, File userImage, String displayName) {
        super();
        this.left = left;
        this.message = message;
        this.mUserImage = userImage;
        this.mUserDisplayName = displayName;
    }
}
