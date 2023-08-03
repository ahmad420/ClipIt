package com.example.clipit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class Profile extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;
    private BottomNavigationView bottomNavigationView;
    private TextView textViewEmail;
    private TextView textViewName;
    private Button buttonLogout;
    private Button buttonResetPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // hide the top of the app
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textViewEmail = findViewById(R.id.textViewEmail);
        textViewName = findViewById(R.id.textViewName);
        buttonLogout = findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(this);


        buttonLogout = findViewById(R.id.buttonLogout);
        buttonResetPassword = findViewById(R.id.Rst_button); // Initialize the new button

        buttonResetPassword.setOnClickListener(this);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Fetch and display user data
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            getUserData(userId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If the user is not logged in, go back to the login page
            Intent intent = new Intent(Profile.this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    private void getUserData(String userId) {
        userListener = db.collection("users").document(userId)
                .addSnapshotListener(new com.google.firebase.firestore.EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle errors
                            return;
                        }

                        if (documentSnapshot.exists()) {
                            // Retrieve user data
                            String userEmail = documentSnapshot.getString("email");
                            String userName = documentSnapshot.getString("name");

                            // Display user data on the profile page
                            textViewEmail.setText(userEmail);
                            textViewName.setText(userName);
                        }
                    }
                });
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
    @Override
    protected void onStop() {
        super.onStop();
        // Remove the Firestore listener when the activity is stopped
        if (userListener != null) {
            userListener.remove();
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonLogout) {
            // Sign out the user and go back to the login page
            mAuth.signOut();
            Intent intent = new Intent(Profile.this, Login.class);
            startActivity(intent);
            finish();
        } else if (view.getId() == R.id.Rst_button) {
            // Reset the user's password using Firebase Auth
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                mAuth.sendPasswordResetEmail(user.getEmail())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Profile.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Profile.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }


    private void navigateToHomeFragment() {
        Intent intent = new Intent(Profile.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToContactFragment() {
        Intent intent = new Intent(Profile.this, Booking_Calendar.class);
        startActivity(intent);
        finish();
    }

    private void navigateToProfileFragment() {
        Intent intent = new Intent(Profile.this, Profile.class);
        startActivity(intent);
        finish();    }
}

