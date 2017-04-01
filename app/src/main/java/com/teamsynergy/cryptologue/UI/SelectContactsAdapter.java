package com.teamsynergy.cryptologue.UI;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.teamsynergy.cryptologue.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MatthewRedington on 3/31/17.
 */

public class SelectContactsAdapter extends BaseAdapter {
    private final Context mContext;
    private LayoutInflater mInflater;
    private List<Pair<String, String>> mDataSource = new ArrayList<>();
    private List<Pair<String, String>> mSelectedContacts = new ArrayList<>();
    private HashMap<String, Boolean> mSelectedMap = new HashMap<>();

    public SelectContactsAdapter(Context context, List<Pair<String, String>> list) {
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
        View rowView = view;

        if (view == null) {
            rowView = mInflater.inflate(R.layout.selectcontacts_cardlayout, parent, false);
        }

        rowView.setOnClickListener(mContactClicked);
        TextView nameTextView = (TextView) rowView.findViewById(R.id.contactName);
        TextView numberTextView = (TextView) rowView.findViewById(R.id.contactNumber);
        CheckBox cBox = ((CheckBox)rowView.findViewById(R.id.checkBox));
        cBox.setOnClickListener(mContactClicked);
        final Pair<String, String> contact = (Pair<String, String>) getItem(position);
        final String name = contact.first;
        final String number = contact.second;
        nameTextView.setText(name);
        numberTextView.setText(number);
        if (!mSelectedMap.containsKey(contact.second)) {
            cBox.setChecked(false);
        } else {
            cBox.setChecked(mSelectedMap.get(contact.second));
        }

        rowView.setTag(contact);
        cBox.setTag(contact);

        return rowView;
    }

    public List<Pair<String, String>> getCheckedContacts() {
        return new ArrayList<>(mSelectedContacts);
    }

    private final ViewGroup.OnClickListener mContactClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox cBox = ((CheckBox)v.findViewById(R.id.checkBox));
            if (cBox != v) cBox.setChecked(!cBox.isChecked());

            Pair<String, String> contact = ((Pair<String, String>)v.getTag());
            mSelectedMap.put(contact.second, cBox.isChecked());
            if (cBox.isChecked()) {
                mSelectedContacts.add(contact);
            } else {
                mSelectedContacts.remove(contact);
            }
        }
    };
}
