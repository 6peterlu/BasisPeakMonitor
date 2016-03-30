package com.example.peter.basispeakmonitor;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Peter on 3/29/2016.
 */
public class Patient {
    private String user;
    private String pass;
    private String name;

    private DataPacket data;

    public Patient(String username, String password, String patientName, SharedPreferences mPrefs){
        user = username;
        pass = password;
        name = patientName;
        Gson gson = new Gson();

        if(mPrefs!=null) {
            System.out.println("Shared preferences found");
            data = gson.fromJson(mPrefs.getString("packet",""), DataPacket.class);

        } else {
            System.out.println("Shared Preferences not found");
            refreshData();
        }
    }


    //Edit the string representation of the patient here
    public String toString(){
        return name;
    }

    public DataPacket getDataPacket(){
        return data;
    }

    public DataPacket refreshData(){
        try {
            data = new DataPacket(new DownloadFilesTask().execute().get());
            return data;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Helper methods below this line

    private String getDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate.substring(0,10);
    }

    //Async stuff below this line

    private class DownloadFilesTask extends AsyncTask<URL, Integer, String> {
        public Document doc;

        protected String doInBackground(URL... urls) {

            try {
                Connection.Response res = Jsoup.connect("https://app.mybasis.com/login")
                        .data("username", user)
                        .data("password", pass)
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
            return doc.text();
        }
        protected void onPostExecute(Document d) {
            System.out.println("Download completed in Patient class");
        }
    }
}
