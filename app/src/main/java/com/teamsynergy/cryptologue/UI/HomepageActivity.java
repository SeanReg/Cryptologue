package com.teamsynergy.cryptologue.UI;

/**
 * Created by MatthewRedington on 3/26/17.
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.teamsynergy.cryptologue.R;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity {
    private List<String> mChatroomNameList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new RecyclerAdapter(mChatroomNameList);

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        prepChatroomData();
    }

    private void prepChatroomData(){
        String name = "test1";
        mChatroomNameList.add(name);
        name= "test2";
        mChatroomNameList.add(name);
        name= "test3";
        mChatroomNameList.add(name);
        name= "test4";
        mChatroomNameList.add(name);
        name= "test5";
        mChatroomNameList.add(name);
        name= "test6";
        mChatroomNameList.add(name);


    }




}
