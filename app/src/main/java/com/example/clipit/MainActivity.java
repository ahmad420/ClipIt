package com.example.clipit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private LinearLayout appointmentsLayout;
    private List<Appointment> userAppointments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        appointmentsLayout = findViewById(R.id.appointmentsLayout);
        fetchUserAppointments();
    }

    private void completeAppointment(String appointmentId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle user not logged in
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String appointmentsCollection = "appointments";

        // Update the status of the appointment to "Completed"
        db.collection(appointmentsCollection)
                .document(appointmentId)
                .update("status", "Completed")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Appointment marked as completed", Toast.LENGTH_SHORT).show();
                    fetchUserAppointments();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to mark appointment as completed", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void deleteAppointment(String appointmentId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle user not logged in
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String appointmentsCollection = "appointments";

        // Delete the appointment from Firestore using its ID
        db.collection(appointmentsCollection)
                .document(appointmentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Appointment deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchUserAppointments();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete appointment", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void fetchUserAppointments() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String appointmentsCollection = "appointments";

            // Query appointments collection to get user's appointments
            db.collection(appointmentsCollection)
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        appointmentsLayout.removeAllViews();
                        userAppointments = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            Appointment appointment = documentSnapshot.toObject(Appointment.class);
                            appointment.setId(documentSnapshot.getId());
                            userAppointments.add(appointment);
                        }

                        for (Appointment appointment : userAppointments) {
                            TextView appointmentTextView = new TextView(this);
                            appointmentTextView.setText("Appointment\n" +
                                    "Date: " + appointment.getDate() + "\n" +
                                    "Time: " + appointment.getTime());

                            Button deleteButton = new Button(this);
                            deleteButton.setText("Delete Appointment");
                            deleteButton.setOnClickListener(view -> deleteAppointment(appointment.getId()));

                            Button completeButton = new Button(this);
                            completeButton.setText("Complete Appointment");
                            completeButton.setOnClickListener(view -> completeAppointment(appointment.getId()));

                            appointmentsLayout.addView(appointmentTextView);
                            appointmentsLayout.addView(deleteButton);
                            appointmentsLayout.addView(completeButton);
                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                    });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                navigateToHomeFragment();
                return true;
            case R.id.contact:
                navigateToContactFragment();
                return true;
            case R.id.profile:
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
