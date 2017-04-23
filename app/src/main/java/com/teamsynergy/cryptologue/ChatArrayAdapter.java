package com.teamsynergy.cryptologue;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for dynamically publishes ChatMessageBubbles to a chatroom
 */
public class ChatArrayAdapter extends ArrayAdapter<ChatMessageBubble> {

    /**
     * Message text
     */
    private TextView chatText;

    /**
     * Message sender's display name
     */
    private TextView displayNameText;

    /**
     * Message sender's avatar
     */
    private ImageView userImage;

    /**
     * List of ChatMessageBubbles
     */
    private List<ChatMessageBubble> ChatMessageBubbleList = new ArrayList<ChatMessageBubble>();

    /**
     * Provides application context for adapter
     */
    private Context context;

    /**
     * Inflates the appropriate ChatMessageBubble view
     */
    private LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    /**
     * Adds a newly sent ChatMessageBubble to chat
     * @param object    new ChatMessageBubble
     */
    @Override
    public void add(ChatMessageBubble object) {
        object.mView = null;

        ChatMessageBubbleList.add(object);
        super.add(object);
    }

    /**
     * Constructs an ChatArrayAdapter
     * @param context
     * @param textViewResourceId
     */
    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    /**
     * Provides the number of ChatMessageBubbles in chatroom
     * @return  the number of ChatMessageBubbles
     */
    public int getCount() {
        return this.ChatMessageBubbleList.size();
    }

    /**
     * Grabs a particular ChatMessageBubble for the purpose of positioning it correctly in chatroom
     * @param index the index of a ChatMessageBubble in the list that corresponds to the position of
     *              ChatMessageBubble in the chat
     * @return
     */
    public ChatMessageBubble getItem(int index) {
        return this.ChatMessageBubbleList.get(index);
    }

    /**
     * Gets an appropriate view for the object depending on who sent it
     * i.e. LHS and RHS bubble
     * @param position  position of ChatMessageBubble
     * @return
     */
    public View getView(int position, View row, ViewGroup parent) {
        ChatMessageBubble object = getItem(position);

        if (object.mView == null) {
            if (object.left) {
                object.mView = inflater.inflate(R.layout.right, parent, false);
            } else {
                object.mView = inflater.inflate(R.layout.left, parent, false);
            }

            if(object.mUserImage != null) {
                userImage = (ImageView) object.mView.findViewById(R.id.user_pic);
                userImage.setImageURI(Uri.fromFile(object.mUserImage));
            }

            displayNameText = (TextView) object.mView.findViewById(R.id.displayname);
            chatText = (TextView) object.mView.findViewById(R.id.msgr);
            displayNameText.setText(object.mUserDisplayName);
            chatText.setText(object.message);
        }

        return object.mView;
    }
}
