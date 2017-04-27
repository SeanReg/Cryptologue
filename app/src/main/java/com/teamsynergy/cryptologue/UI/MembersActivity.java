package com.teamsynergy.cryptologue.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.widget.TextView;

import com.teamsynergy.cryptologue.ObjectPasser;
import com.teamsynergy.cryptologue.R;
import com.teamsynergy.cryptologue.User;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by jasonpinlac on 4/11/17.
 */

public class MembersActivity extends AppCompatActivity {

    private TextLatoView memberList = null;
    private ArrayList<Pair<User, File>> members = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        members = (ArrayList) ObjectPasser.popObject("members");
        StringBuilder sb = new StringBuilder();
        for(Pair<User, File> member : members) {
            sb.append("• " + member.first.getUsername() + "\n");
        }
        memberList = (TextLatoView) findViewById(R.id.members);
        memberList.setText(sb.toString());
    }
}
