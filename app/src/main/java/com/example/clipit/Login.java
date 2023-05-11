package com.example.clipit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
      // hide the top of the app
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

    }
}