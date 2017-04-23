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
import com.teamsynergy.cryptologue.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasonpinlac on 4/11/17.
 */

public class PollsActivity extends AppCompatActivity{
    private LayoutInflater mInflater;
    private LinearLayout[] optionsArray= new LinearLayout[4];
    private int mOptionSelected;
    private Button buttonSubmit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setScrollable(true);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        //get number of choice options available
        staticLabelsFormatter.setHorizontalLabels(new String[] {"A", "B", "C", "D"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(1, 2),
                new DataPoint(2, 6),
                new DataPoint(3, 12),
                new DataPoint(4, 12)
        });
        graph.addSeries(series);

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series.setSpacing(30);

        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.WHITE);


        LinearLayout parent = (LinearLayout)findViewById(R.id.poll_result_layout);
        for(int i = 0; i < 4; ++i) {
            LinearLayout optionLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.poll_option, null);
            ((TextView)optionLayout.findViewById(R.id.option_texts)).setText("testing " + i);
            parent.addView(optionLayout);
            optionsArray[i]=optionLayout;
        }

        buttonSubmit = (Button) findViewById(R.id.submit_button);
        buttonSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
               // mOptionSelected    //this is the selected option
                finish();
            }
        });

    }



    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if(!checked)
            return;
        View parent= (View) view.getParent();

        for(int i=0; i <optionsArray.length; ++i){
            if(parent == optionsArray[i]){
                mOptionSelected=i+1;
            }
            else{
                CheckBox checkBox = (CheckBox)optionsArray[i].findViewById(R.id.option);
                checkBox.setChecked(false);
            }

        }

    }
}
