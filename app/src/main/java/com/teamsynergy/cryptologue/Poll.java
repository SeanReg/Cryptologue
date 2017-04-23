package com.teamsynergy.cryptologue;

import android.util.Pair;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nadeen on 4/21/17.
 */

public class Poll extends ChatFunction {
    private List<Pair<String,Integer>> mVotingChoices = null;
    private String mTopic = "";
    private String mResults = "";

    private boolean mUserVoted = true;

    private final ArrayList<String> mOptions = new ArrayList<>();

    public interface PollOptionsListener {
        public void onGotPollOptions(List<PollOption> options);
    }

    public interface UserVotedListener {
        public void onGotUserVoted(boolean voted);
    }

    private Poll() {

    }

    public void checkUserVoted(final UserVotedListener listener) {
        ParseQuery query = new ParseQuery("PollVotes");
        query.whereEqualTo("voter", AccountManager.getInstance().getCurrentAccount().getParseUser());
        query.whereEqualTo("poll", mParseObj);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() == 0)
                    mUserVoted = false;
                else
                    mUserVoted = true;

                listener.onGotUserVoted(mUserVoted);
            }
        });
    }

    public void getPollOptions(final PollOptionsListener listener) {
        ParseQuery query = mParseObj.getRelation("options").getQuery();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null)
                    return;

                ArrayList<PollOption> options = new ArrayList<PollOption>();
                for (ParseObject obj : objects) {
                    options.add(new PollOption(obj.getString("description"), obj.getInt("votes"), obj));
                }

                listener.onGotPollOptions(options);
            }
        });
    }

    public void participate(String decision) {

    }

    public void voteOn(PollOption option) {
        if (mUserVoted)
            return;

        option.getParseObject().increment("votes");

        ParseObject vote = new ParseObject("PollVotes");
        vote.put("poll", mParseObj);
        vote.put("voter", AccountManager.getInstance().getCurrentAccount().getParseUser());

        ParseObject.saveAllInBackground(Arrays.asList(option.getParseObject(), vote));
    }

    public static class PollOption {
        private String mDesc = "";
        private int mVotes = 0;
        private ParseObject mParseObj = null;

        private PollOption(String desc, int votes, ParseObject pObj) {
            mDesc = desc;
            mVotes = votes;
            mParseObj = pObj;
        }

        private ParseObject getParseObject() {
            return mParseObj;
        }

        public String getDescription() {
            return mDesc;
        }

        public int getVotes() {
            return mVotes;
        }
    }

    public static class Builder extends ChatFunction.Builder {
        private final Poll mPoll;

        public Builder() {
            mCFunction = mPoll = new Poll();
        }

        public void addVotingOption(String option) {
            if (option.trim().length() == 0)
                return;

            mPoll.mOptions.add(option);
        }

        public Poll build(boolean isNew, final Chatroom chatroom) {
            if (isNew) {
                final ParseObject poll = new ParseObject("Polls");
                poll.put("name", mCFunction.mName);
                poll.put("endAt", mCFunction.mEndDate);
                ParseRelation options = poll.getRelation("options");

                mPoll.mUserVoted = false;

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
                        if (e == null) {
                            poll.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    mCFunction.mParseObj = poll;

                                    ParseObject pRoom = ParseObject.createWithoutData("Chatrooms", chatroom.getId());
                                    pRoom.getRelation("polls").add(poll);
                                    pRoom.saveInBackground();
                                }
                            });
                        }
                    }
                });
            } else {

            }

            return mPoll;
        }

        public static ArrayList<Poll> buildFromParseObjects(ArrayList<ParseObject> objects, Chatroom room) {
            UserAccount curAcc = AccountManager.getInstance().getCurrentAccount();
            ArrayList<Poll> polls = new ArrayList<>();
            for (ParseObject obj : objects) {
                Poll.Builder poll = new Builder();
                poll.setParseObject(obj);
                poll.setName(obj.getString("name"));
                polls.add(poll.build(false, room));
            }

            return polls;
        }
    }
}
