package com.example.peter.basispeakmonitor;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;

import com.db.chart.model.LineSet;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.QuadEase;
import com.google.gson.Gson;



public class GraphActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        String selected = getIntent().getStringExtra("selected");

        TextView chartTitle = (TextView) findViewById(R.id.chartTitle);
        chartTitle.setText(selected);

        Gson gson = new Gson();


        LineChartView chart = (LineChartView)findViewById(R.id.linechart);

        //Getting the DataArray itself
        DataArray values = gson.fromJson(getIntent().getStringExtra("data"), DataArray.class);
        values.createSparseData(25);
        float[] data = values.getSparseData();

        String[] labels = values.getLabels();
//        LineSet dataset = new LineSet();
        LineSet dataset = new LineSet(labels, data);
//
//        for (int i = 0; i < data.length; i++){
//            if(data[i] != -1){
//                dataset.addPoint(labels[i], data[i]);
//            }
//        }

        //Cosmetics below

        Animation anim = new Animation(1000);
        QuadEase ease = new QuadEase();
        anim.setEasing(ease);
        dataset.setDotsColor(Color.RED);
        dataset.setThickness(0);
        dataset.setColor(Color.WHITE);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        chart.setGrid(ChartView.GridType.FULL, p);
        chart.setBackgroundColor(Color.WHITE);
        chart.addData(dataset);
        chart.setAxisLabelsSpacing(5);
        chart.setXAxis(true);
        chart.setStep(10);
        chart.setBorderSpacing(20);
        chart.show(anim);
    }


}
