package com.example.peter.basispeakmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;


//UPDATE THIS CLASS
public class PatientActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent lineIntent = new Intent(this, GraphActivity.class);
        Intent barIntent = new Intent(this, BarGraphActivity.class);

        Gson gson = new Gson();//This helps package custom classes

        switch(position) {
            case (0):
                lineIntent.putExtra("selected", "Heart Rate");
                System.out.println("clicked item 0");
                lineIntent.putExtra("data", gson.toJson(heartrate));
                startActivity(lineIntent);
                break;
            case (1):
                barIntent.putExtra("selected", "Calories");
                barIntent.putExtra("data", gson.toJson(calories));
                startActivity(barIntent);
                break;
            case (2):
                barIntent.putExtra("selected", "Steps");
                barIntent.putExtra("data", gson.toJson(steps));
                startActivity(barIntent);
                break;
            case (3):
                lineIntent.putExtra("selected", "Skin Temp");
                lineIntent.putExtra("data", gson.toJson(skin_temp));
                startActivity(lineIntent);
                break;
            case (4):
                lineIntent.putExtra("selected", "Galvanic Skin Response");
                lineIntent.putExtra("data", gson.toJson(gsr));
                startActivity(lineIntent);
                break;
            case (5):
                return;

            //also god bless intents overwrite. if they didn't then lord...
        }
    }
}
