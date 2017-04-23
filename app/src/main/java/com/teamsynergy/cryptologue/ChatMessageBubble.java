package com.teamsynergy.cryptologue;

import android.view.View;
import android.view.ViewGroup;

import java.io.File;

/**
 * Created by nadeen on 3/26/17.
 */

/**
 * Class that represents a ChatMessageBubble
 */
public class ChatMessageBubble {
    /**
     * Left or right ChatMessageBubble flag
     */
    public boolean left;

    /**
     * Message text
     */
    public String message;

    /**
     * The associated view based on ChatMessageBubble flag
     */
    public View mView;

    /**
     * User's avatar
     */
    public File mUserImage;

    /**
     * User's display name
     */
    public String mUserDisplayName;

    /**
     * Constructs a ChatMessageBubble
     * @param left
     * @param message
     * @param userImage
     * @param displayName
     */
    public ChatMessageBubble(boolean left, String message, File userImage, String displayName) {
        super();
        this.left = left;
        this.message = message;
        this.mUserImage = userImage;
        this.mUserDisplayName = displayName;
    }
}
