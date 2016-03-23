package com.example.peter.basispeakmonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Scanner;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView test = (TextView)findViewById(R.id.raw_data);
        Scanner s = new Scanner(getResources().openRawResource(R.raw.basis));
        String data="";
        try {
            while (s.hasNext()) {
                data = data+s.next();
            }
        } finally {
            s.close();
        }

        String[] heartrate = getData("heartrate", data);
        String[] calories = getData("calories", data);
        String[] steps = getData("steps", data);
        String[] gsr = getData("gsr", data);
        String[] skin_temp = getData("skin_temp", data);
        
    }

    //Helper methods

    /**
     * Parses string into a string array.
     * @return
     */
    private static String[] getData(String metric, String raw){
        int index = raw.indexOf(metric);
        if(index==-1){
            System.out.println("Invalid metric.");
            return null;
        } else {
            int startBracket = raw.indexOf("[", index);
            int endBracket = raw.indexOf("]", index);
            String numbers = raw.substring(startBracket+1,endBracket);
            String[] parsed = numbers.split(",");
            return parsed;
        }
    }
}
