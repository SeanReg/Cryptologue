package com.teamsynergy.cryptologue.UI;

/**
 * Created by MatthewRedington on 3/26/17.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamsynergy.cryptologue.R;

import java.util.List;
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private List<String> mChatRoomNameList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
        }
    }

    public RecyclerAdapter(List<String> CRnamesList) {
        this.mChatRoomNameList = CRnamesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_homepage, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String name = mChatRoomNameList.get(position);
        holder.name.setText(name);
    }

    @Override
    public int getItemCount() {
        return mChatRoomNameList.size();
    }

}



