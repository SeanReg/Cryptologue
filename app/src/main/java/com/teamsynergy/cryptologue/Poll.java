package com.teamsynergy.cryptologue;

import android.util.Pair;

import java.util.List;

/**
 * Created by nadeen on 4/21/17.
 */

public class Poll extends ChatFunction {
    private List<Pair<String,Integer>> mVotingChoices = null;
    private String mTopic = "";
    private String mResults = "";

    public Poll() {

    }

    public List<Pair<String,Integer>> getPollResults() {
        return mVotingChoices;
    }

    public void participate(String decision) {
        // choice
    }
}
