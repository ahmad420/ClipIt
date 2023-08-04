package com.example.clipit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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
    private static final int PERMISSION_REQUEST_CODE = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        appointmentsLayout = findViewById(R.id.appointmentsLayout);

        // Hide the top of the app
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        checkLocationPermissions();
        fetchUserAppointments();

        // Locate the "Navigate to our shop" button
        Button btnOpenMap = findViewById(R.id.btnOpenMap);
        btnOpenMap.setOnClickListener(view -> {
            // Call the method to open the map location
            openShopLocation();
        });
    }


    private void completeAppointment(String appointmentId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle user not logged in
            return;
        }

        // Create a confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Completion");
        builder.setMessage("Are you sure you want to mark this appointment as completed?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Proceed with marking the appointment as completed
            completeAppointmentConfirmed(appointmentId);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // User canceled the action
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void completeAppointmentConfirmed(String appointmentId) {
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
            return;
        }

        // Create a confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this appointment?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Proceed with deleting the appointment
            deleteAppointmentConfirmed(appointmentId);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // User canceled the action
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteAppointmentConfirmed(String appointmentId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
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
                                    "Time: " + appointment.getTime() + "\nstatus: "+appointment.getStatus());
                            float textSizeSp = 20; // Set the desired text size in sp
                            appointmentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);

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


    private void checkLocationPermissions() {
        try {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSION_REQUEST_CODE);
            } else {
                // Permissions already granted, start getting the location
                getCurrentLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location permissions granted, start getting the location
                    getCurrentLocation();
                } else {
                    // Location permissions denied, handle accordingly (e.g., show an error message)
                    Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCurrentLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopLocationUpdates() {
        try {
            locationManager.removeUpdates(locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openMapLocation(double latitude, double longitude) {
        try {
            // Create a Uri object with the location coordinates
            Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?z=15");

            // Create an Intent with the action and Uri
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            // Check if the Google Maps app is installed
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                // Open the location in Google Maps
                startActivity(mapIntent);
            } else {
                // Google Maps app is not installed, handle accordingly
                Toast.makeText(this, "Google Maps app is not installed.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void openShopLocation() {
        double latitude = 12.1311;
        double longitude = 13.1313;
        openMapLocation(latitude, longitude);
    }


    private void navigateToHomeFragment() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
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
