package com.example.peter.basispeakmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.google.gson.Gson;

public class AddPatient extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
    }

    public void submitCredentials(){
        //Need to add password and user checking here, Ashley, you know how to do this

        Intent intent = new Intent(this, MainActivity.class);

        EditText nameInput = (EditText) findViewById(R.id.editText);
        EditText emailInput = (EditText) findViewById(R.id.emailInput);
        EditText passwordInput = (EditText) findViewById(R.id.password);

        Patient newPatient = new Patient(emailInput.getText().toString(),
                passwordInput.getText().toString(),
                nameInput.getText().toString(),
                null);

        Gson gson = new Gson();

        intent.putExtra("patient", gson.toJson(newPatient));
        startActivity(intent);
    }
}
