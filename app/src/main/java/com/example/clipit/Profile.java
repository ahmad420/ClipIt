package com.example.clipit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class Profile extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;

    private TextView textViewEmail;
    private TextView textViewName;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textViewEmail = findViewById(R.id.textViewEmail);
        textViewName = findViewById(R.id.textViewName);
        buttonLogout = findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(this);

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
        }
    }

}