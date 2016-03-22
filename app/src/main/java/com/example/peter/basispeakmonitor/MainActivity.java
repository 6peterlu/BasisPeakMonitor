package com.example.peter.basispeakmonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyPair;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Login attempt here: doesn't work
        JsonObject json = new JsonObject();
        json.addProperty("username", "peterlu6@stanford.edu");
        json.addProperty("password", "basispeaktest");

        Ion.with(this)
                .load("https://app.mybasis.com/login")
                .setJsonObjectBody(json)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        System.out.println("OUTPUT HERE: " + result);
                    }
                });

        //Getting data attempt here: does work
        Ion.with(this)
                .load("https://app.mybasis.com/api/v1/metricsday/me?day=2016-03-20&padding=10800&heartrate=true&steps=true&calories=true&gsr=true&skin_temp=true&bodystates=true")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    public void onCompleted(Exception e,
                                            String result) {
                        processResult(result);
                    }
                });



    }
    private void processResult(String data){
        TextView test = (TextView)findViewById(R.id.raw_data);
        test.setText(data);
    }
}
