package com.teamsynergy.cryptologue.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.teamsynergy.cryptologue.ChatFunction;
import com.teamsynergy.cryptologue.ObjectPasser;
import com.teamsynergy.cryptologue.R;

import java.util.ArrayList;

/**
 * Created by nadeen on 4/22/17.
 */

public class ChatFunctionAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<ChatFunction> mDataSource = new ArrayList<>();


    private Class mOpenActivity;

    public ChatFunctionAdapter(Context context, ArrayList<ChatFunction> chatFunctions, Class activity){
        mOpenActivity = activity;
        mContext = context;
        mDataSource = chatFunctions;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setChatFunctions(ArrayList<ChatFunction> chatFunctions) {
        mDataSource = chatFunctions;
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
    public View getView(final int position, View view, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.cardlayout_chatfunction, parent, false);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectPasser.putObject("chatfunction", getItem(position));
                mContext.startActivity(new Intent(mContext.getApplicationContext(), mOpenActivity));
            }
        });
        TextLatoView nameTextView = (TextLatoView) rowView.findViewById(R.id.function);
        final ChatFunction chatFunction = (ChatFunction) getItem(position);

        nameTextView.setText(chatFunction.getName());

        return rowView;
    }
}
