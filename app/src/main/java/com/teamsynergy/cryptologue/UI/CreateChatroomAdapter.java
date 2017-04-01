package com.teamsynergy.cryptologue.UI;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.teamsynergy.cryptologue.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MatthewRedington on 3/31/17.
 */

public class CreateChatroomAdapter extends BaseAdapter {
    private final Context mContext;
    private LayoutInflater mInflater;
    private List<Pair<String, String>> mDataSource = new ArrayList<>();

    public CreateChatroomAdapter(Context context, List<Pair<String, String>> list) {
        mContext = context;
        mDataSource=list;
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
        View rowView = mInflater.inflate(R.layout.createchatroom_cardlayout, parent, false);
        TextView nameTextView = (TextView) rowView.findViewById(R.id.contactName);
        TextView numberTextView = (TextView) rowView.findViewById(R.id.contactNumber);
        final Pair<String, String> contact = (Pair<String, String>) getItem(position);
        final String name = contact.first;
        final String number=contact.second;
        nameTextView.setText(name);
        numberTextView.setText(number);

        return rowView;
    }
}
