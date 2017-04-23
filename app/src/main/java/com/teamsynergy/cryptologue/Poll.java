package com.teamsynergy.cryptologue;

import android.util.Pair;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nadeen on 4/21/17.
 */

public class Poll extends ChatFunction {
    private List<Pair<String,Integer>> mVotingChoices = null;
    private String mTopic = "";
    private String mResults = "";

    private final ArrayList<String> mOptions = new ArrayList<>();

    public Poll() {

    }

    public List<Pair<String,Integer>> getPollResults() {
        return mVotingChoices;
    }

    public void participate(String decision) {

    }

    public static class Builder extends ChatFunction.Builder {
        private final Poll mPoll;

        public Builder() {
            mCFunction = mPoll = new Poll();
        }

        public void addVotingOption(String option) {
            mPoll.mOptions.add(option);
        }

        public Poll build(boolean isNew) {
            if (isNew) {
                final ParseObject poll = new ParseObject("Polls");
                poll.put("name", mCFunction.mName);
                poll.put("endAt", mCFunction.mEndDate);
                ParseRelation options = poll.getRelation("options");

                ArrayList<ParseObject> optionsList = new ArrayList<>();
                for (String op : mPoll.mOptions) {
                    ParseObject pObjOption  = new ParseObject("PollOptions");
                    pObjOption.put("description", op);
                    pObjOption.put("votes", 0);
                    optionsList.add(pObjOption);
                    options.add(pObjOption);
                }
                ParseObject.saveAllInBackground(optionsList, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                            poll.saveInBackground();
                    }
                });
            } else {

            }

            return mPoll;
        }
    }
}
