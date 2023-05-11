package com.example.clipit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // hide the top of the app
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}