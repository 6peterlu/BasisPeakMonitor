package com.example.peter.basispeakmonitor;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.QuadEase;
import com.google.gson.Gson;

public class BarGraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_graph);
        String selected = getIntent().getStringExtra("selected");

        TextView chartTitle = (TextView) findViewById(R.id.chartTitle);
        chartTitle.setText(selected);

        Gson gson = new Gson();

        BarChartView barChart = (BarChartView)findViewById(R.id.barchart);


        //Getting the DataArray itself
        DataArray values = gson.fromJson(getIntent().getStringExtra("data"), DataArray.class);
        values.createSparseData(25);
        float[] data = values.getSparseData();

        String[] labels = values.getLabels();
//        LineSet dataset = new LineSet();
        BarSet dataset = new BarSet(labels, data);
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
        dataset.setColor(Color.RED);
         Paint p = new Paint();
        p.setColor(Color.BLACK);
        barChart.setGrid(ChartView.GridType.FULL, p);
        barChart.setBackgroundColor(Color.WHITE);
        barChart.addData(dataset);
        barChart.setAxisLabelsSpacing(5);
        barChart.setXAxis(true);
        barChart.setStep(10);
        barChart.setBorderSpacing(20);
        barChart.show(anim);
    }
}
