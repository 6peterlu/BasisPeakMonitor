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

        test.setText(data);
    }
}
