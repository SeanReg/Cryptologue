package com.teamsynergy.cryptologue.UI;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.teamsynergy.cryptologue.Chatroom;
import com.teamsynergy.cryptologue.ObjectPasser;
import com.teamsynergy.cryptologue.R;

import java.util.ArrayList;


/**
 * Created by MatthewRedington on 3/27/17.
 */

public class ChatroomListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Chatroom> mDataSource;


    public ChatroomListAdapter(Context context, ArrayList<Chatroom> rooms){
        mContext = context;
        mDataSource = rooms;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.cardlayout, parent, false);
        TextView nameTextView = (TextView) rowView.findViewById(R.id.roomName);
        final Chatroom room = (Chatroom) getItem(position);
        final String name = room.getName();
        nameTextView.setText(name);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectPasser.putObject(room.getId(), room);

                Intent Intent = new Intent(mContext, ChatroomActivity.class);
                Intent.putExtra("chatroom", room.getId());
                mContext.startActivity(Intent);
            }
        });

        return rowView;
    }
}
