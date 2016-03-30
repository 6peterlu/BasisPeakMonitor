package com.example.peter.basispeakmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;


//UPDATE THIS CLASS
public class PatientActivity extends Activity {
    private Patient user;
    private DataPacket packet;
    private Gson gson = new Gson();
    private ListView metrics;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        user = gson.fromJson(getIntent().getStringExtra("patient"), Patient.class);
        packet = user.getDataPacket();
        metrics = (ListView) findViewById(R.id.metricList);
        updateList();
        metrics.setAdapter(adapter);

        //Classic android studio
        metrics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent lineIntent = new Intent(getParent(), GraphActivity.class);//have no idea what getParent does
                Intent barIntent = new Intent(getParent(), BarGraphActivity.class);

                Gson gson = new Gson();//This helps package custom classes

                switch (position) {
                    case (0):
                        lineIntent.putExtra("selected", "Heart Rate");
                        System.out.println("clicked item 0");
                        lineIntent.putExtra("data", gson.toJson(packet.getHeartrate()));
                        startActivity(lineIntent);
                        break;
                    case (1):
                        barIntent.putExtra("selected", "Calories");
                        barIntent.putExtra("data", gson.toJson(packet.getCalories()));
                        startActivity(barIntent);
                        break;
                    case (2):
                        barIntent.putExtra("selected", "Steps");
                        barIntent.putExtra("data", gson.toJson(packet.getSteps()));
                        startActivity(barIntent);
                        break;
                    case (3):
                        lineIntent.putExtra("selected", "Skin Temp");
                        lineIntent.putExtra("data", gson.toJson(packet.getSkinTemp()));
                        startActivity(lineIntent);
                        break;
                    case (4):
                        lineIntent.putExtra("selected", "Galvanic Skin Response");
                        lineIntent.putExtra("data", gson.toJson(packet.getGsr()));
                        startActivity(lineIntent);
                        break;
                    case (5):
                        return;

                    //also god bless intents overwrite. if they didn't then lord...
                }
            }
        });
    }

    private void reload(){
        new DownloadFilesTask().execute();
    }

    //helper method
    private void updateList(){
        String[] displayedData = new String[6];
        displayedData[0] = "last recorded heartrate: "+packet.getHeartrate().getLastValue();
        displayedData[1] = "last recorded calories: "+packet.getCalories().getLastValue();
        displayedData[2] = "last recorded steps: "+packet.getSteps().getLastValue();
        displayedData[3] = "last recorded skin temp: "+packet.getSkinTemp().getLastValue();
        displayedData[4] = "last recorded gsr: "+packet.getGsr().getLastValue();
        displayedData[5] = "alerts: none";
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, displayedData);
        adapter.notifyDataSetChanged();
    }
//Async download below
    private class DownloadFilesTask extends AsyncTask<Void, Integer, DataPacket> {
        protected DataPacket doInBackground(Void... voids){
            user.refreshData();
            packet = user.getDataPacket();
            return packet;
        }
        protected void onPostExecute(){
            updateList();
        }
    }
}
