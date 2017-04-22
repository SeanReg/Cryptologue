package com.teamsynergy.cryptologue.UI;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import java.util.Calendar;
import android.widget.TimePicker;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.teamsynergy.cryptologue.R;

import org.w3c.dom.Text;

/**
 * Created by jasonpinlac on 4/11/17.
 */

public class EventsActivity extends AppCompatActivity {
    EditText mStartDate;
    EditText mEndDate;
    DatePickerDialog datePickerDialog;
    EditText mStartTime;
    EditText mEndTime;
    Button buttonOpenGoogleMaps;
    EditText mDescription;

    private Place mSelectedPlace = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);


        mStartDate = (EditText) findViewById(R.id.start_date);
        mStartDate.setOnClickListener(createDateListener());
        mEndDate=(EditText) findViewById(R.id.end_date);
        mEndDate.setOnClickListener(createDateListener());
        mDescription= (EditText) findViewById(R.id.description);


        mStartTime = (EditText) findViewById(R.id.start_time);
        // perform click event listener on edit text
        mStartTime.setOnClickListener(createClockListener());
        mEndTime = (EditText) findViewById(R.id.end_time);
        // perform click event listener on edit text
        mEndTime.setOnClickListener(createClockListener());

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mSelectedPlace = place;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });
        buttonOpenGoogleMaps= (Button) findViewById(R.id.googlemaps_button);
        buttonOpenGoogleMaps.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (mSelectedPlace == null)
                    return;

                Uri gmmIntentUri = Uri.parse("geo:" + mSelectedPlace.getLatLng().latitude + "," +
                    mSelectedPlace.getLatLng().longitude + "?q=" + mSelectedPlace.getAddress().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }

            }
        });




    }


    private View.OnClickListener createDateListener() {
        return new View.OnClickListener() {
            public void onClick(final View view) {
                view.clearFocus();
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(EventsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker datePicker, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                ((EditText) view).setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }

        };
    }




    private View.OnClickListener createClockListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.clearFocus();
                Calendar mcurrentTime = Calendar.getInstance();
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EventsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String mSelectedMinuteString=Integer.toString(selectedMinute);
                        if(selectedHour>12){
                            selectedHour-=12;
                        }
                        if(selectedMinute<10){
                            mSelectedMinuteString= "0" + Integer.toString(selectedMinute);
                        }
                        ((EditText)view).setText(selectedHour + ":" + mSelectedMinuteString);
                    }
                }, 12, 0, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        };
    }







}

