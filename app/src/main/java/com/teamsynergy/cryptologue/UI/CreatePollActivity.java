package com.teamsynergy.cryptologue.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.teamsynergy.cryptologue.Chatroom;
import com.teamsynergy.cryptologue.ObjectPasser;
import com.teamsynergy.cryptologue.Poll;
import com.teamsynergy.cryptologue.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nadeen on 4/22/17.
 */

public class CreatePollActivity extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        Spinner spinner = (Spinner) findViewById(R.id.end_time);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.poll_times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        findViewById(R.id.btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Poll.Builder builder = new Poll.Builder();
                builder.setName(((EditLatoText)findViewById(R.id.topic)).getText().toString());

                builder.addVotingOption(((EditLatoText)findViewById(R.id.A)).getText().toString());
                builder.addVotingOption(((EditLatoText)findViewById(R.id.B)).getText().toString());
                builder.addVotingOption(((EditLatoText)findViewById(R.id.C)).getText().toString());
                builder.addVotingOption(((EditLatoText)findViewById(R.id.D)).getText().toString());

                Chatroom room = (Chatroom)ObjectPasser.popObject("chatroomPoll");
                builder.build(true, room);
                finish();
            }
        });
    }
}

