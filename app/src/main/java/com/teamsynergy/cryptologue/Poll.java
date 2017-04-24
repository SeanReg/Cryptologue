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

    /**
     * Queries if the current user has voted in this poll
     * @param listener the callback listener to notify
     */
    public void checkUserVoted(final UserVotedListener listener) {
        ParseQuery query = new ParseQuery("PollVotes");
        query.whereEqualTo("voter", AccountManager.getInstance().getCurrentAccount().getParseUser());
        query.whereEqualTo("poll", mParseObj);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                //Must have voted if size > 0
                if (objects.size() == 0)
                    mUserVoted = false;
                else
                    mUserVoted = true;

                listener.onGotUserVoted(mUserVoted);
            }
        });
    }

    /**
     * Gets the voting options for the poll
     * @param listener the callback listener to notify
     */
    public void getPollOptions(final PollOptionsListener listener) {
        ParseQuery query = mParseObj.getRelation("options").getQuery();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null)
                    return;

                //Create PollOption objects
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

    /**
     * Votes on a PollOption
     * @param option the PollOption to vote on
     */
    public void voteOn(PollOption option) {
        if (mUserVoted)
            return;

        //Update database with the new vote
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

        /**
         * Constructs a PollOption used to contain an voting option in a poll
         * @param desc the description of the option
         * @param votes the number of votes that the option has
         * @param pObj the ParseObject associated with the option
         */
        private PollOption(String desc, int votes, ParseObject pObj) {
            mDesc = desc;
            mVotes = votes;
            mParseObj = pObj;
        }

        /**
         * Gets the ParseObject for the PollOption
         * @return the ParseObject for the PollOption
         */
        private ParseObject getParseObject() {
            return mParseObj;
        }

        /**
         * Gets the description for the PollOption
         * @return a String containing the description for the PollOption
         */
        public String getDescription() {
            return mDesc;
        }

        /**
         * Gets the current vote count
         * @return gets the amount of votes for the PollOption
         */
        public int getVotes() {
            return mVotes;
        }
    }

    public static class Builder extends ChatFunction.Builder {
        private final Poll mPoll;

        public Builder() {
            mCFunction = mPoll = new Poll();
        }

        /**
         * Adds a voting option to the poll
         * @param option the description of the voting option
         */
        public void addVotingOption(String option) {
            if (option.trim().length() == 0)
                return;

            mPoll.mOptions.add(option);
        }

        /**
         * Constructs a Poll object
         * @param isNew if true, a new Poll is added to the database
         * @param chatroom the Chatroom object associated with the Poll
         * @return the newly built Poll object
         */
        public Poll build(boolean isNew, final Chatroom chatroom) {
            if (isNew) {
                //Insert the data for the poll into the ParseObject
                final ParseObject poll = new ParseObject("Polls");
                poll.put("name", mCFunction.mName);
                poll.put("endAt", mCFunction.mEndDate);
                ParseRelation options = poll.getRelation("options");

                mPoll.mUserVoted = false;

                //Add each of the PollOptions
                ArrayList<ParseObject> optionsList = new ArrayList<>();
                for (String op : mPoll.mOptions) {
                    ParseObject pObjOption  = new ParseObject("PollOptions");
                    pObjOption.put("description", op);
                    pObjOption.put("votes", 0);
                    optionsList.add(pObjOption);
                    options.add(pObjOption);
                }

                //Save the PollOptions
                ParseObject.saveAllInBackground(optionsList, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            //Associate PollOptions and Poll
                            //Save the Poll
                            poll.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    mCFunction.mParseObj = poll;

                                    //Update Chatroom relation and save
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

        /**
         * Builds a list of Polls from an array of ParseObjects
         * @param objects a List of ParseObjects to reference
         * @param room the room associated with the Polls
         * @return a list of newly built Polls
         */
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
