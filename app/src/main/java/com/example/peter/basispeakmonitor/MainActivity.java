package com.example.peter.basispeakmonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    //Data variables
    private DataArray heartrate;
    private DataArray calories;
    private DataArray steps;
    private DataArray skin_temp;
    private DataArray gsr;
    private String[] displayedData;
    private ArrayAdapter<String> adapter;
    private ListView mainList;

    //Timers: I hope you understand this code cause I don't
    public Handler handler = new Handler();
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            refresh();
            handler.postDelayed(mRunnable, 100000);
        }
    };

    //Saved states
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isInternetConnected(getApplicationContext())) {

            Toast.makeText(getApplicationContext(), "No Internet Connected!", Toast.LENGTH_LONG).show();
            TextView tx = new TextView(this);
            tx.setText("Please close the app, connect to the internet, and then start the app again");
            RelativeLayout main = (RelativeLayout) findViewById(R.id.main);
            main.addView(tx);
            return;
        } else {
            Toast.makeText(getApplicationContext(), "Internet Connected!", Toast.LENGTH_SHORT).show();
        }
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/ostrich-regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        initializeDisplayedData();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, displayedData);
        mainList.setOnItemClickListener(this);
        mainList.setAdapter(adapter);

        mRunnable.run();
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    //Saving data
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        String hr = gson.toJson(heartrate);
        prefsEditor.putString("heartrate", hr);

        String st = gson.toJson(steps);
        prefsEditor.putString("steps", st);

        String sk = gson.toJson(skin_temp);
        prefsEditor.putString("skin_temp", sk);

        String ca = gson.toJson(calories);
        prefsEditor.putString("calories", ca);

        String gs = gson.toJson(gsr);
        prefsEditor.putString("gsr", gs);

        prefsEditor.apply();
    }

    private void initializeDisplayedData(){
        mainList = (ListView)findViewById(R.id.hubListView);
        Gson gson = new Gson();
        mPrefs = getPreferences(MODE_PRIVATE);
        displayedData = new String[6];
        if(mPrefs.contains("heartrate")) {

            System.out.println("Shared preferences found");

            String hr = mPrefs.getString("heartrate", "");
            heartrate = gson.fromJson(hr, DataArray.class);

            String sk = mPrefs.getString("skin_temp", "");
            skin_temp = gson.fromJson(sk, DataArray.class);

            String gs = mPrefs.getString("gsr", "");
            gsr = gson.fromJson(gs, DataArray.class);

            String ca = mPrefs.getString("calories", "");
            calories = gson.fromJson(ca, DataArray.class);

            String st = mPrefs.getString("steps", "");
            steps = gson.fromJson(st, DataArray.class);

            displayedData[0] = "last recorded heartrate: "+heartrate.getLastValue();
            displayedData[1] = "last recorded calories: "+calories.getLastValue();
            displayedData[2] = "last recorded steps: "+steps.getLastValue();
            displayedData[3] = "last recorded skin temp: "+skin_temp.getLastValue();
            displayedData[4] = "last recorded gsr: "+gsr.getLastValue();
            displayedData[5] = "alerts: none";
        } else {
            System.out.println("Shared Preferences not found");
            displayedData = new String[6];
            displayedData[0] = "last recorded heartrate: none";
            displayedData[1] = "last recorded calories: none";
            displayedData[2] = "last recorded steps: none";
            displayedData[3] = "last recorded skin temp: none";
            displayedData[4] = "last recorded gsr: none";
            displayedData[5] = "alerts: none";
        }
    }
    //Helper methods

    /**
     * Parses string into a string array.
     * @return String array containing parsed data
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
            if(metric.equals("gsr")){
                for(int i = 0; i<parsed.length; i++){
                    if(!parsed[i].equals("null")) {
                        parsed[i] = convertFromScientific(parsed[i]);
                    }
                }
            }
            return parsed;
        }
    }

    //format: "5.07e-05" -> "0.0000507"
    private static String convertFromScientific(String input){
        String output = "0.";
        int eLoc = input.indexOf('e');
        int powerTen = Integer.parseInt(input.substring(eLoc+1));
        for(int i = 1; i<-powerTen; i++){
            output = output+"0";
        }
        for(int i = 0; i<eLoc; i++){
            if(input.charAt(i)=='.') continue;
            output = output+input.charAt(i);
        }
        return output;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent lineIntent = new Intent(this, GraphActivity.class);
        Intent barIntent = new Intent(this, BarGraphActivity.class);

        Gson gson = new Gson();//This helps package custom classes

        switch(position){
            case(0): lineIntent.putExtra("selected", "Heart Rate");
                System.out.println("clicked item 0");
                lineIntent.putExtra("data", gson.toJson(heartrate));
                startActivity(lineIntent); break;
            case(1): barIntent.putExtra("selected", "Calories");
                barIntent.putExtra("data", gson.toJson(calories));
                startActivity(barIntent); break;
            case(2): barIntent.putExtra("selected", "Steps");
                barIntent.putExtra("data", gson.toJson(steps));
                startActivity(barIntent); break;
            case(3): lineIntent.putExtra("selected", "Skin Temp");
                lineIntent.putExtra("data", gson.toJson(skin_temp));
                startActivity(lineIntent); break;
            case(4): lineIntent.putExtra("selected", "Galvanic Skin Response");
                lineIntent.putExtra("data", gson.toJson(gsr));
                startActivity(lineIntent);break;
            case(5): return;

            //also god bless intents overwrite. if they didn't then lord...
        }


    }


    //ASync stuff below this line-----------------------------------------

    private class DownloadFilesTask extends AsyncTask<URL, Integer, Document> {
        public Document doc;

        protected Document doInBackground(URL... urls) {

            try {
                Connection.Response res = Jsoup.connect("https://app.mybasis.com/login")
                        .data("username", "peterlu6@stanford.edu")
                        .data("password", "basispeaktest")
                        .method(Connection.Method.POST)
                        .execute();

                Map<String, String> loginCookies = res.cookies();

//Here you parse the page that you want. Put the url that you see when you have logged in
                doc = Jsoup.connect("https://app.mybasis.com/api/v1/metricsday/me?day="+getDate()+"&padding=10800&heartrate=true&steps=true&calories=true&gsr=true&skin_temp=true&bodystates=true")
                        .cookies(loginCookies)
                        .ignoreContentType(true)
                        .get();
            } catch (IOException e) {
                System.out.println("failed.");
                e.printStackTrace();
            }
            return doc;
        }

        protected void onPostExecute(Document d) {
            System.out.println("What's up buttercup");
            asyncCompleted(doc.text());
        }
    }

    //Anything you want to update every time data is pulled, add it in this method
    private void asyncCompleted(String received){
        heartrate = new DataArray(getData("heartrate", received));
        calories = new DataArray(getData("calories", received));
        steps = new DataArray(getData("steps", received));
        skin_temp = new DataArray(getData("skin_temp", received));
        gsr = new DataArray(getData("gsr", received));

        displayedData[0] = "last recorded heartrate: "+heartrate.getLastValue();
        displayedData[1] = "last recorded calories: "+calories.getLastValue();
        displayedData[2] = "last recorded steps: "+steps.getLastValue();
        displayedData[3] = "last recorded skin temp: "+skin_temp.getLastValue();
        displayedData[4] = "last recorded gsr: "+gsr.getLastValue();
        displayedData[5] = "alerts: none";
        adapter.notifyDataSetChanged();
    }

    //This method repulls data from basis's website.
    private void refresh(){
        new DownloadFilesTask().execute();
        displayedData[5] = "alerts: loading data!";
        adapter.notifyDataSetChanged();
    }

    private String getDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate.substring(0,10);
    }
}
