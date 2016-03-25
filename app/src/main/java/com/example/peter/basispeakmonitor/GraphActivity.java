package com.example.peter.basispeakmonitor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.LinearEase;
import com.db.chart.view.animation.easing.QuadEase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        String selected = getIntent().getStringExtra("selected");
        //So Double types are weird, can't pass an array list of them directly.
        //this serializable thing seems to work.
        //I debated converting the ArrayList<Double> to a double[] but that's O(n) so nah.
        Gson gson = new Gson();


        LineChartView chart = (LineChartView)findViewById(R.id.linechart);
        DataArray values = gson.fromJson(getIntent().getStringExtra("data"), DataArray.class);
        values.createSparseData(25);
        float[] data = values.getSparseData();

        String[] labels = values.getLabels();
        LineSet dataset = new LineSet(labels, data);



        //Cosmetics below

        Animation anim = new Animation(1000);
        QuadEase ease = new QuadEase();
        anim.setEasing(ease);
        dataset.setDotsColor(Color.BLUE);
        dataset.setColor(Color.MAGENTA);
        dataset.setSmooth(true);
        chart.addData(dataset);
        chart.setAxisLabelsSpacing(5);
        chart.setXAxis(true);
        chart.setStep(10);
        chart.setBorderSpacing(20);
        chart.show(anim);
    }

}
