package com.teamsynergy.cryptologue.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.teamsynergy.cryptologue.Event;
import com.teamsynergy.cryptologue.ObjectPasser;
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

        final Event event = (Event)ObjectPasser.popObject("chatfunction");
        startDate = (TextView)findViewById(R.id.start_date);
        startDate.setText("Start Date: " + event.getStart().toString());

        endDate = (TextView)findViewById(R.id.end_date);
        endDate.setText("End Date:" + event.getEnd().toString());

        startTime = (TextView)findViewById(R.id.start_time);
        startTime.setText("Start Time:");
        startTime.setVisibility(View.GONE);

        endTime = (TextView)findViewById(R.id.end_time);
        endTime.setText("End Time:");
        endTime.setVisibility(View.GONE);

        description = (TextView)findViewById(R.id.description);
        description.setText("Description : " + event.getDescritpion());

        locationName = (TextView)findViewById(R.id.location);
        locationName.setText("Location : " + event.getAddress());
        locationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (mSelectedPlace == null)
                  //  return;

               // Uri gmmIntentUri = Uri.parse("geo:" + mSelectedPlace.getLatLng().latitude + "," +
                 //       mSelectedPlace.getLatLng().longitude + "?q=" + mSelectedPlace.getAddress().toString());
                Uri gmmIntentUri = Uri.parse("geo:" + event.getCoordinates() + "?q=" + event.getAddress());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });



    }

}
