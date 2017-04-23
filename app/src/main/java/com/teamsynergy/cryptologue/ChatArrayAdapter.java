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

public class ChatArrayAdapter extends ArrayAdapter<ChatMessageBubble> {

    private TextView chatText;
    private TextView displayNameText;
    private ImageView userImage;
    private List<ChatMessageBubble> ChatMessageBubbleList = new ArrayList<ChatMessageBubble>();
    private Context context;

    private LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    @Override
    public void add(ChatMessageBubble object) {
        object.mView = null;

        ChatMessageBubbleList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.ChatMessageBubbleList.size();
    }

    public ChatMessageBubble getItem(int index) {
        return this.ChatMessageBubbleList.get(index);
    }

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
