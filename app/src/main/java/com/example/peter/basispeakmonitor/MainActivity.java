package com.example.peter.basispeakmonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //***************TESTING UNDER THIS LINE
        String data = "nothing received";
        DownloadFilesTask d=  new DownloadFilesTask();

        try {
            URL u = new URL("https://google.com");
            d.execute(u);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        data = d.getData();









        //**************TESTING OVER THIS LINE

        TextView test = (TextView)findViewById(R.id.raw_data);
        test.setText(data);
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

    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
        String pageContent;
        boolean done = false;
        protected Long doInBackground(URL... urls) {

            long totalSize = 0;

            String data = "peterlu6@stanford.edu=login&basispeaktest=password";
            String loginUrl = "https://app.mybasis.com/js/lib/app/b2b3b385.login.js";
            String dataUrl = "https://app.mybasis.com/api/v1/metricsday/me?day=2016-03-21&padding=10800&heartrate=true&steps=true&calories=true&gsr=true&skin_temp=true&bodystates=true";
            String Login = POST_req(loginUrl, data, 1000000); /*last parameter is a limit of page content length*/

//And adter succcess login you can send second request:
            pageContent = POST_req(dataUrl, "", 1000000);
            return totalSize;
        }

        protected void onPostExecute(Long result) {
            System.out.println("Done!");
            System.out.println(pageContent);
            done = true;
        }
        public String getData(){
            System.out.println("Attempted to retrieve data: ");
            if(done) return pageContent;
            return "No data recieved(yet)";
        }

        //**HELPER METHODS FROM STACK OVERFLOW

        public String POST_req(String url, String post_data, int len) {
            URL addr = null;
            try {
                addr = new URL(url);
            } catch (MalformedURLException e) {
                return "Некорректный URL";
            }
            StringBuffer data = new StringBuffer();
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) addr.openConnection();
            } catch (IOException e) {
                return "Open connection error";
            }
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Accept-Language", "ru,en-GB;q=0.8,en;q=0.6");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.setRequestProperty("Cookie", "");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //conn.setInstanceFollowRedirects(true);
            set_cookie(conn);

            //POST data:
            String post_str = post_data;
            data.append(post_str);
            try {
                conn.connect();
            } catch (IOException e) {
                return "Connecting error";
            }
            DataOutputStream dataOS = null;
            try {
                dataOS = new DataOutputStream(conn.getOutputStream());
            } catch (IOException e2) {
                return "Out stream error";
            }
            try {
                ((DataOutputStream) dataOS).writeBytes(data.toString());
            } catch (IOException e) {
                return "Out stream error 1";
            }

        /*If redirect: */
            int status;
            try {
                status = conn.getResponseCode();
            } catch (IOException e2) {
                return "Response error";
            }
            if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
                String new_url = conn.getHeaderField("Location");
                String cookies = conn.getHeaderField("Set-Cookie");
                URL red_url;
                try {
                    red_url = new URL(new_url);
                } catch (MalformedURLException e) {
                    return "Redirect error";
                }
                try {
                    conn = (HttpURLConnection) red_url.openConnection();
                } catch (IOException e) {
                    return "Redirect connection error";
                }
                //conn.setRequestProperty("Content-type", "text/html");
                conn.setRequestProperty("Connection", "keep-alive");
                conn.setRequestProperty("Accept-Language", "ru,en-GB;q=0.8,en;q=0.6");
                conn.setRequestProperty("Accept-Charset", "utf-8");
                conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                conn.setRequestProperty("Cookie", cookies);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                //conn.setInstanceFollowRedirects(true);
            }

            java.io.InputStream in = null;
            try {
                in = (java.io.InputStream) conn.getInputStream();
            } catch (IOException e) {
                return "In stream error 1";
            }
            InputStreamReader reader = null;
            try {
                reader = new InputStreamReader(in, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return "In stream error 2";
            }
            char[] buf = new char[len];
            try {
                reader.read(buf);
            } catch (IOException e) {
                return "In stream error 3";
            }
            get_cookie(conn);

            return (new String(buf));
        }
        public void get_cookie(HttpURLConnection conn) {
            SharedPreferences sh_pref_cookie = getSharedPreferences("cookies", Context.MODE_PRIVATE);
            String cook_new;
            String COOKIES_HEADER;
            if (conn.getHeaderField("Set-Cookie") != null) {
                COOKIES_HEADER = "Set-Cookie";
            }
            else {
                COOKIES_HEADER = "Cookie";
            }
            cook_new = conn.getHeaderField(COOKIES_HEADER);
            if (cook_new.indexOf("sid", 0) >= 0) {
                SharedPreferences.Editor editor = sh_pref_cookie.edit();
                editor.putString("Cookie", cook_new);
                editor.commit();
            }
        }
        public void set_cookie(HttpURLConnection conn) {
            SharedPreferences sh_pref_cookie = getSharedPreferences("cookies", Context.MODE_PRIVATE);
            String COOKIES_HEADER = "Cookie";
            String cook = sh_pref_cookie.getString(COOKIES_HEADER, "no_cookie");
            if (!cook.equals("no_cookie")) {
                conn.setRequestProperty(COOKIES_HEADER, cook);
            }
        }
    }



}
