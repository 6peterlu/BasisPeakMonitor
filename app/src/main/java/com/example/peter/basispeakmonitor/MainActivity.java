package com.example.peter.basispeakmonitor;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    private String lastRecievedData;
    private String[] heartrate;
    private String[] calories;
    private String[] steps;
    private String[] skin_temp;
    private String[] gsr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new DownloadFilesTask().execute();
        TextView text = (TextView) findViewById(R.id.raw_data);
        text.setText("Loading data...");

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
    private void asyncCompleted(String s){
        lastRecievedData = s;
        updateAll();
    }

    private void updateAll(){
        TextView text = (TextView) findViewById(R.id.raw_data);
        text.setText(lastRecievedData);
        heartrate = getData("heartrate", lastRecievedData);
        calories = getData("calories", lastRecievedData);
        steps = getData("steps", lastRecievedData);
        skin_temp = getData("skin_temp", lastRecievedData);
        gsr = getData("gsr", lastRecievedData);
    }

    private String getDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate.substring(0,10);
    }
}
