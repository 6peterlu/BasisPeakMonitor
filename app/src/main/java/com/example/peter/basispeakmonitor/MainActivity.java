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
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadFilesTask d = new DownloadFilesTask();
        Document received = null;
        try {
            received = d.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //**************TESTING OVER THIS LINE

        TextView test = (TextView)findViewById(R.id.raw_data);
        test.setText(received.toString());
//        Scanner s = new Scanner(getResources().openRawResource(R.raw.basis));
//        String data="";
//        try {
//            while (s.hasNext()) {
//                data = data+s.next();
//            }
//        } finally {
//            s.close();
//        }
//
//        String[] heartrate = getData("heartrate", data);
//        String[] calories = getData("calories", data);
//        String[] steps = getData("steps", data);
//        String[] gsr = getData("gsr", data);
//        String[] skin_temp = getData("skin_temp", data);
//
//        String gsrString = "";
//        for(String sample: gsr){
//            gsrString = gsrString+sample;
//        }
//        test.setText(gsrString);

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
            int count = urls.length;
            long totalSize = 0;
            try {
                Connection.Response res = Jsoup.connect("https://app.mybasis.com/login")
                        .data("username", "peterlu6@stanford.edu")
                        .data("password", "basispeaktest")
                        .method(Connection.Method.POST)
                        .execute();

                Map<String, String> loginCookies = res.cookies();

//Here you parse the page that you want. Put the url that you see when you have logged in
                doc = Jsoup.connect("https://app.mybasis.com/api/v1/metricsday/me?day=2016-03-23&padding=10800&heartrate=true&steps=true&calories=true&gsr=true&skin_temp=true&bodystates=true")
                        .cookies(loginCookies)
                        .ignoreContentType(true)
                        .get();
            } catch (IOException e) {
                System.out.println("failed.");
                e.printStackTrace();
            }
            return doc;
        }

        protected void onPostExecute(Long result) {
            System.out.println("Doc status: "+doc.toString());
        }
    }



}
