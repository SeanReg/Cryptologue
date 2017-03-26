package com.teamsynergy.cryptologue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseInit.start(this);
    }
}
