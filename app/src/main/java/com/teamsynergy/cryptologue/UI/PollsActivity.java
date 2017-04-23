package com.teamsynergy.cryptologue.UI;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.teamsynergy.cryptologue.ObjectPasser;
import com.teamsynergy.cryptologue.Poll;
import com.teamsynergy.cryptologue.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jasonpinlac on 4/11/17.
 */

public class PollsActivity extends AppCompatActivity{
    private LayoutInflater mInflater;
    private ArrayList<Pair<LinearLayout, Poll.PollOption>> optionsArray = new ArrayList<>();
    private int mOptionSelected;
    private Button buttonSubmit;
    private Poll mPoll;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls);

        final GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setScrollable(true);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        //get number of choice options available
        staticLabelsFormatter.setHorizontalLabels(new String[] {"A", "B", "C", "D"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        mPoll = (Poll)ObjectPasser.popObject("chatfunction");
        mPoll.checkUserVoted(new Poll.UserVotedListener() {
            @Override
            public void onGotUserVoted(final boolean voted) {
                mPoll.getPollOptions(new Poll.PollOptionsListener() {
                    @Override
                    public void onGotPollOptions(List<Poll.PollOption> options) {
                        LinearLayout parent = (LinearLayout)findViewById(R.id.poll_result_layout);

                        ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
                        for(int i = 0; i < options.size(); ++i) {
                            LinearLayout optionLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.poll_option, null);
                            ((TextView)optionLayout.findViewById(R.id.option_texts)).setText(options.get(i).getDescription());
                            if (voted)
                                ((CheckBox)optionLayout.findViewById(R.id.option)).setVisibility(View.GONE);
                            parent.addView(optionLayout);
                            optionsArray.add(new Pair<LinearLayout, Poll.PollOption>(optionLayout, options.get(i)));
                            dataPoints.add(new DataPoint(i, options.get(i).getVotes()));
                        }

                        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints.toArray(new DataPoint[dataPoints.size()]));

                        graph.addSeries(series);
                        graph.getViewport().setMinY(0);
                        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                            @Override
                            public int get(DataPoint data) {
                                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
                            }
                        });

                        series.setSpacing(30);

                        series.setDrawValuesOnTop(true);
                        series.setValuesOnTopColor(Color.WHITE);
                    }
                });
            }
        });





        buttonSubmit = (Button) findViewById(R.id.submit_button);
        buttonSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
               // mOptionSelected    //this is the selected option
                mPoll.voteOn(optionsArray.get(mOptionSelected).second);
                finish();
            }
        });

    }



    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if(!checked)
            return;
        View parent= (View) view.getParent();

        for(int i=0; i <optionsArray.size(); ++i){
            if(parent == optionsArray.get(i).first){
                mOptionSelected=i;
            }
            else{
                CheckBox checkBox = (CheckBox)optionsArray.get(i).first.findViewById(R.id.option);
                checkBox.setChecked(false);
            }

        }

    }
}
