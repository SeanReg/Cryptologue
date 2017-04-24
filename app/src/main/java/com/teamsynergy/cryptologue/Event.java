package com.teamsynergy.cryptologue;

import android.graphics.Bitmap;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nadeen on 4/21/17.
 */

public class Event extends ChatFunction {
/*    private Date mEventStart = null;
    private Date mEventEnd = null;*/
    private Bitmap mEventPhoto = null;
    private String mLocationAddress = "";

    public Event() {

    }
    public void participate(String decision) {
        // going or not going
    }

    public String getAddress() {
        return mLocationAddress;
    }

    public static class Builder extends ChatFunction.Builder {
        private final Event mEvent;

        public Builder() {
            mCFunction = mEvent = new Event();
        }

        public void setAddress(String address) {
            if (mCFunction != null)
                mEvent.mLocationAddress = address;
        }

        public Event build(boolean isNew, final Chatroom chatroom) {
            if (isNew) {
                final ParseObject event = new ParseObject("Events");
                event.put("name", mCFunction.mName);
                event.put("startDate", mCFunction.mStartDate);
                event.put("endDate", mCFunction.mEndDate);
                event.put("description", mCFunction.mDescritpion);
                event.put("address", mEvent.mLocationAddress);

                event.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        mCFunction.mParseObj = event;

                        ParseObject pRoom = ParseObject.createWithoutData("Chatrooms", chatroom.getId());
                        pRoom.getRelation("events").add(event);
                        pRoom.saveInBackground();
                    }
                });
            } else {

            }

            return mEvent;
        }

        public static ArrayList<Event> buildFromParseObjects(ArrayList<ParseObject> objects, Chatroom room) {
            UserAccount curAcc = AccountManager.getInstance().getCurrentAccount();
            ArrayList<Event> events = new ArrayList<>();
            for (ParseObject obj : objects) {
                Event.Builder event = new Builder();
                event.setParseObject(obj);
                event.setName(obj.getString("name"));
                event.setDescription(obj.getString("description"));
                event.setStartDate(obj.getDate("startDate"));
                event.setEndDate(obj.getDate("endDate"));
                event.setAddress(obj.getString("address"));
                events.add(event.build(false, room));
            }

            return events;
        }
    }
}