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

    /**
     * Gets the address of the event
     * @return a String containing the Event location
     */
    public String getAddress() {
        return mLocationAddress;
    }

    public static class Builder extends ChatFunction.Builder {
        private final Event mEvent;

        public Builder() {
            mCFunction = mEvent = new Event();
        }

        /**
         * Sets the address for the event
         * @param address the address of the event
         */
        public void setAddress(String address) {
            if (mCFunction != null)
                mEvent.mLocationAddress = address;
        }

        /**
         * Builds and uploads a new Event
         * @param isNew if true a new Event is created in the database
         * @param chatroom the Chatroom object associated with the Event
         * @return the newly built Event
         */
        public Event build(boolean isNew, final Chatroom chatroom) {
            if (isNew) {
                //Add Parse details
                final ParseObject event = new ParseObject("Events");
                event.put("name", mCFunction.mName);
                event.put("startDate", mCFunction.mStartDate);
                event.put("endDate", mCFunction.mEndDate);
                event.put("description", mCFunction.mDescritpion);
                event.put("address", mEvent.mLocationAddress);

                //Save to database
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

        /**
         * Builds a list of Events from an array of ParseObjects
         * @param objects a List of ParseObjects to reference
         * @param room the room associated with the event
         * @return a list of newly built Events
         */
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