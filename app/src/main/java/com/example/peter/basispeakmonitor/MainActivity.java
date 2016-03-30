package com.example.peter.basispeakmonitor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    //Data variables
    private ArrayList<Patient> patientList;

    private ArrayList<String> patientNames;
    private ArrayAdapter<String> adapter;
    private ListView mainList;

    private Gson gson = new Gson();

    //enables automatic refreshing
//    public Handler handler = new Handler();
//    private Runnable mRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            refresh();
//            handler.postDelayed(mRunnable, 100000);
//        }
//    };

    //Saved states
    private SharedPreferences mPrefs;


    //MAIN ACTIVITY NEEDS UPDATING FROM THIS POINT ONWARD********************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeDisplayedData();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, patientNames);
        mainList.setOnItemClickListener(this);
        mainList.setAdapter(adapter);

        if(getIntent()!=null){
            System.out.println("Intent found");
            Intent incoming = getIntent();
            if(incoming.hasCategory("patient")){
                Patient p = gson.fromJson(incoming.getStringExtra("patient"), Patient.class);
                patientList.add(p);
                patientNames.add(p.toString());
                adapter.notifyDataSetChanged();
            }
        }

//        mRunnable.run();
    }



    //Saving data
    @Override
    protected void onPause() {
        super.onPause();
        if(mPrefs!=null) {
            SharedPreferences.Editor prefsEditor = mPrefs.edit();

            prefsEditor.putString("patient-list", gson.toJson(patientList));

            prefsEditor.apply();
        }
    }


    private void initializeDisplayedData(){

        mainList = (ListView) findViewById(R.id.hubListView);
        if(mPrefs!=null){
            patientList = gson.fromJson(mPrefs.getString("patient-list", ""), ArrayList.class);
        } else {
            patientList = new ArrayList<>();
        }
        patientNames = new ArrayList<>();
        for(int i = 0; i<patientList.size();  i++){
            patientNames.add(patientList.get(i).toString());
        }
    }

    //ListView click method
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, PatientActivity.class);

        Gson gson = new Gson();//This helps package custom classes

        intent.putExtra("patient", gson.toJson(patientList.get(position)));

        startActivity(intent);
    }
    public void addPatient(View view){
        Intent intent = new Intent(this, AddPatient.class);
        startActivity(intent);
    }
}
