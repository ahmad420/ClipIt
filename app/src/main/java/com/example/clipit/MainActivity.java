package com.example.clipit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clipit.model.Appointment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private TextView appointmentsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        appointmentsTextView = findViewById(R.id.appointmentsTextView);

        fetchUserAppointments();
    }

    private void fetchUserAppointments() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String appointmentsCollection = "appointments";

            // Query appointments collection to get user's appointments
            db.collection(appointmentsCollection)
                    .whereEqualTo("userId", userId) // userId is the ID of the logged-in user
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<Appointment> userAppointments = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            // Convert each document to an Appointment object and add to the list
                            Appointment appointment = documentSnapshot.toObject(Appointment.class);
                            appointment.setId(documentSnapshot.getId()); // Set the document ID in the Appointment object
                            userAppointments.add(appointment);
                        }

                        // Display user appointments
                        StringBuilder appointmentsText = new StringBuilder();
                        for (Appointment appointment : userAppointments) {
                            appointmentsText.append("Appointment").append("\n");
                            appointmentsText.append("Date: ").append(appointment.getDate()).append("\n");
                            appointmentsText.append("Time: ").append(appointment.getTime()).append("\n");

                            // Add a delete button for each appointment
                            Button deleteButton = new Button(this);
                            deleteButton.setText("\nDelete Appointment");
                            deleteButton.setOnClickListener(view -> deleteAppointment(appointment));

                            appointmentsText.append(deleteButton.getText()).append("\n\n");
                        }
                        appointmentsTextView.setText(appointmentsText.toString());
                    })
                    .addOnFailureListener(e -> {
                        // Handle error while fetching user appointments
                        e.printStackTrace();
                    });
        }
    }

    private void deleteAppointment(Appointment appointment) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show(); // Show "Clicked" message
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String appointmentsCollection = "appointments";
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();

        // Delete the appointment from Firestore using its ID
        db.collection(appointmentsCollection)
                .document(appointment.getId()) // Using the retrieved document ID
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Appointment deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchUserAppointments(); // Refresh the displayed appointments
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete appointment", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
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
        finish();
    }
}
