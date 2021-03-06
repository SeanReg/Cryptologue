package com.teamsynergy.cryptologue.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.teamsynergy.cryptologue.Chatroom;
import com.teamsynergy.cryptologue.Message;
import com.teamsynergy.cryptologue.MessagingService;
import com.teamsynergy.cryptologue.ObjectPasser;
import com.teamsynergy.cryptologue.R;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by MatthewRedington on 3/27/17.
 */

public class ChatroomListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Chatroom> mDataSource;
    private HashMap<String, Bitmap> mDataBitmaps = new HashMap<>();
    private HashMap<String, String> mLastMsg     = new HashMap<>();


    public ChatroomListAdapter(Context context, ArrayList<Chatroom> rooms){
        mContext = context;
        mDataSource = new ArrayList<>(rooms);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateAdapter(ArrayList<Chatroom> rooms) {
        mLastMsg.clear();
        mDataSource = new ArrayList<>(rooms);
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
        View rowView = mInflater.inflate(R.layout.cardlayout_chatroom, parent, false);
        TextView nameTextView = (TextView) rowView.findViewById(R.id.roomName);
        final TextView messagePreview= (TextView) rowView.findViewById(R.id.messagePreview);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.chatAvatar);
        final Chatroom room = (Chatroom) getItem(position);
        final String name = room.getName();

        if (!mLastMsg.containsKey(room.getId())) {
            room.getCachedMessages(new MessagingService.MessageListener() {
                @Override
                public void onMessageRecieved(Message s) {
                    mLastMsg.put(room.getId(), s.getText());
                    messagePreview.setText(s.getText());
                }
            }, 1);
        } else {
            messagePreview.setText(mLastMsg.get(room.getId()));
        }


        if (!mDataBitmaps.containsKey(room.getId())) {
            room.getImage(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e != null)
                        return;

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    mDataBitmaps.put(room.getId(), bitmap);
                    imageView.setImageBitmap(bitmap);
                }
            });
        } else {
            imageView.setImageBitmap(mDataBitmaps.get(room.getId()));
        }


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
