package com.teamsynergy.cryptologue;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.teamsynergy.cryptologue.UI.ChatroomActivity;
import com.teamsynergy.cryptologue.UI.HomepageActivity;
import com.teamsynergy.cryptologue.UI.LoginActivity;
import com.teamsynergy.cryptologue.UI.SignupActivity;

import static android.R.id.button1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseInit.start(this);

        if (AccountManager.getInstance().getCurrentAccount() == null) {
            Intent Intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(Intent);
        }

        Button button = ((Button)findViewById(R.id.signupButton));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(Intent);}
        });

        Button button2 = ((Button)findViewById(R.id.loginButton));
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(Intent);
            }
        });

        Button button3 = ((Button)findViewById(R.id.chatroomButton));
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Intent = new Intent(getApplicationContext(), ChatroomActivity.class);
                startActivity(Intent);}
        });

        Button button4 = ((Button)findViewById(R.id.HomepageButton));
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Intent = new Intent(getApplicationContext(), HomepageActivity.class);
                startActivity(Intent);}
        });

    }
}
