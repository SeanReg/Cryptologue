package com.teamsynergy.cryptologue;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by nadeen on 4/21/17.
 */

public class Event extends ChatFunction {
    private Date mEventStart = null;
    private Date mEventEnd = null;
    private Bitmap mEventPhoto = null;
    private String mLocationAddress = null;

    public Event() {

    }
    public void participate(String decision) {
        // going or not going
    }
}
