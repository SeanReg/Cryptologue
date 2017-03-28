package com.teamsynergy.cryptologue;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;

    private LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    @Override
    public void add(ChatMessage object) {
        object.mView = null;

        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View row, ViewGroup parent) {
        ChatMessage object = getItem(position);

        if (object.mView == null) {
            if (object.left) {
                object.mView = inflater.inflate(R.layout.right, parent, false);
            } else {
                object.mView = inflater.inflate(R.layout.left, parent, false);
            }
            chatText = (TextView) object.mView.findViewById(R.id.msgr);
            chatText.setText(object.message);
        }

        return object.mView;
    }
}
