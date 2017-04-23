package com.teamsynergy.cryptologue.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.teamsynergy.cryptologue.R;

/**
 * Created by MatthewRedington on 4/22/17.
 */

public class EventActivity extends AppCompatActivity{
    private TextView startDate;
    private TextView endDate;
    private TextView startTime;
    private TextView endTime;
    private TextView description;
    private TextView locationName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        startDate = (TextView)findViewById(R.id.start_date);
        startDate.setText("Start Date:");

        endDate = (TextView)findViewById(R.id.end_date);
        endDate.setText("End Date:");

        startTime = (TextView)findViewById(R.id.start_time);
        startTime.setText("Start Time:");

        endTime = (TextView)findViewById(R.id.end_time);
        endTime.setText("End Time:");

        description = (TextView)findViewById(R.id.description);
        description.setText("Description :");

        locationName = (TextView)findViewById(R.id.location);
        locationName.setText("Location :");
        locationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (mSelectedPlace == null)
                  //  return;

               // Uri gmmIntentUri = Uri.parse("geo:" + mSelectedPlace.getLatLng().latitude + "," +
                 //       mSelectedPlace.getLatLng().longitude + "?q=" + mSelectedPlace.getAddress().toString());
                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194?q=101+main+street");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });



    }

}
