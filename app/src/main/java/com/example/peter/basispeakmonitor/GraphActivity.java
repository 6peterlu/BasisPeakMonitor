package com.example.peter.basispeakmonitor;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        String selected = getIntent().getStringExtra("selected");
        //So Double types are weird, can't pass an array list of them directly.
        //this serializable thing seems to work.
        //I debated converting the ArrayList<Double> to a double[] but that's O(n) so nah.
        ArrayList<Double> data = (ArrayList<Double>) getIntent().getSerializableExtra("data");
        System.out.println("In Graph Activity: " + selected + " " + data);
        switch(selected){
            case("heartrate"):
                List<PointValue> hrValues = dataToPV(data);
                graphHR(hrValues);
                break;
            case("calories"):
                List<SubcolumnValue> calValues = dataToSV(data);
                graphCal(calValues);
                break;
            case("steps"):
                List<SubcolumnValue> stepValues = dataToSV(data);
                graphSteps(stepValues);
                break;
            case("skin temp"):
                List<PointValue> stValues = dataToPV(data);
                graphSkinTemp(stValues);
                break;
            case("gsr"):
                List<PointValue> gsrValues = dataToPV(data);
                graphGSR(gsrValues);
                break;
            default:
                Toast.makeText(getApplicationContext(),
                        "abort abort what happened", Toast.LENGTH_SHORT).show();
                //tfw you forget how to make a toast...
                return;
        }
    }

    private List<SubcolumnValue> dataToSV(ArrayList<Double> data) {
        List<SubcolumnValue> values = new ArrayList<SubcolumnValue>();
        for(int i =0; i<data.size(); i++){
            if (data.get(i) != -1){
                float val = data.get(i).floatValue();
                values.add(new SubcolumnValue(val));
            }
        }
        return values;
    }

    private List<PointValue> dataToPV(ArrayList<Double> data) {
        List<PointValue> values = new ArrayList<PointValue>();
        for(int i = 0; i < data.size(); i++){
            if(data.get(i) != -1){
                float val = data.get(i).floatValue();
                values.add(new PointValue(i, val));

            }
        }
        return values;
    }

    //Line graph this one
    private void graphGSR(List<PointValue> vals) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        LineChartView lChart = new LineChartView(getApplicationContext());
        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < vals.size(); i++){
            Line line = new Line(vals);
            line.setColor(ChartUtils.COLOR_GREEN);
            line.setShape(ValueShape.CIRCLE);
            line.setCubic(true);
            line.setFilled(false);
            lines.add(line);
        }

        LineChartData lData = new LineChartData(lines);
        lChart.setLineChartData(lData);
        ll.addView(lChart);
    }

    //Line graph this one
    private void graphSkinTemp(List<PointValue> vals) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        LineChartView lChart = new LineChartView(getApplicationContext());
        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < vals.size(); i++){
            Line line = new Line(vals);
            line.setColor(ChartUtils.COLOR_BLUE);
            line.setShape(ValueShape.CIRCLE);
            line.setCubic(true);
            line.setFilled(false);
            lines.add(line);
        }

        LineChartData lData = new LineChartData(lines);
        lChart.setLineChartData(lData);
        ll.addView(lChart);
    }

    //Bar graph this one
    private void graphSteps(List<SubcolumnValue> vals) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        ColumnChartView bChart = new ColumnChartView(getApplicationContext());
        List<Column> columns = new ArrayList<Column>();
        for(int i = 0; i < vals.size(); i++){
            Column col = new Column(vals);
            columns.add(col);
        }

        ColumnChartData bData = new ColumnChartData(columns);
        bChart.setColumnChartData(bData);
        ll.addView(bChart);
    }

    //Bar graph this one
    private void graphCal(List<SubcolumnValue> vals) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        ColumnChartView bChart = new ColumnChartView(getApplicationContext());
        List<Column> columns = new ArrayList<Column>();
        for(int i = 0; i < vals.size(); i++){
            Column col = new Column(vals);
            columns.add(col);
        }
        ColumnChartData bData = new ColumnChartData(columns);
        bChart.setColumnChartData(bData);
        ll.addView(bChart);
    }

    //Line graph this one
    private void graphHR(List<PointValue> vals) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        LineChartView lChart = new LineChartView(getApplicationContext());

        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < vals.size(); i++){
            Line line = new Line(vals);
            line.setColor(ChartUtils.COLOR_RED);
            line.setShape(ValueShape.CIRCLE);
            line.setCubic(true);
            line.setFilled(false);
            lines.add(line);
        }

        LineChartData lData = new LineChartData(lines);
        lChart.setLineChartData(lData);
        ll.addView(lChart);
    }
}
