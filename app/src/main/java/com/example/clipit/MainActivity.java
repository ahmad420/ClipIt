package com.example.clipit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                // Handle Home menu item selection
                // Example: navigate to HomeFragment
                navigateToHomeFragment();
                return true;
            case R.id.contact:
                // Handle Contact Us menu item selection
                // Example: navigate to ContactFragment
                navigateToContactFragment();
                return true;
            case R.id.profile:
                // Handle Profile menu item selection
                // Example: navigate to ProfileFragment
                navigateToProfileFragment();
                return true;
            default:
                return false;
        }
    }

    private void navigateToHomeFragment() {
        Intent intent = new Intent(MainActivity.this, Booking_Calendar.class);
        startActivity(intent);
        finish();
    }

    private void navigateToContactFragment() {
        Intent intent = new Intent(MainActivity.this, Booking_Calendar.class);
        startActivity(intent);
        finish();
    }

    private void navigateToProfileFragment() {
        Intent intent = new Intent(MainActivity.this, Profile.class);
        startActivity(intent);
        finish();    }
}
