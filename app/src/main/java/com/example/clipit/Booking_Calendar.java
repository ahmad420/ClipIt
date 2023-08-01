package com.example.clipit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.clipit.model.Appointment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Booking_Calendar extends AppCompatActivity  implements BottomNavigationView.OnNavigationItemSelectedListener{

    private BottomNavigationView bottomNavigationView;
    private Appointment appointment = new Appointment(); // Initialize the appointment object
    private String selectedTime;
    private String userId; // User ID of the signed-in user
    private String userName; // User name of the signed-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_calendar);
        CalendarView calendarView = findViewById(R.id.calendarView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // hide the top of the app
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Set the current date as the default date for the calendar view
        calendarView.setDate(calendar.getTimeInMillis());

        // Set the date change listener to update the current date
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                try {
                    calendar.set(year, month, dayOfMonth);
                    Toast.makeText(Booking_Calendar.this, year + " " + month + " " + dayOfMonth, Toast.LENGTH_SHORT).show();
                    // Example: Create an appointment for the selected date
                    appointment.setDate(formatDate(year, month, dayOfMonth));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Get the user ID and user name of the signed-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            try {
                userId = currentUser.getUid();
                userName = currentUser.getDisplayName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showTimePickerDialog(View view) {
        try {
            Calendar currentTime = Calendar.getInstance();
            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            int minute = currentTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            // Update the selected time in the Appointment object
                            selectedTime = formatTime(selectedHour, selectedMinute);

                            // Update the time shown on the button
                            Button timePickerButton = findViewById(R.id.timePickerButton);
                            timePickerButton.setText(selectedTime);
                        }
                    }, hour, minute, false);

            timePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatDate(int year, int month, int dayOfMonth) {
        try {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            Date date = selectedDate.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatTime(int hourOfDay, int minute) {
        try {
            return String.format(Locale.getDefault(), "%02d:%02d %s",
                    hourOfDay % 12 == 0 ? 12 : hourOfDay % 12, minute,
                    hourOfDay < 12 ? "AM" : "PM");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createAppointment(View view) {
        try {
            if (appointment.getDate() == null || selectedTime == null) {
                Toast.makeText(this, "Please select date and time first.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Set the selected time in the Appointment object
            appointment.setTime(selectedTime);

            // Set the user ID and user name in the Appointment object
            appointment.setUserId(userId);
            appointment.setUserName(userName);

            // Add the appointment to the database
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("appointments")
                    .add(appointment)
                    .addOnSuccessListener(documentReference -> {
                        // Appointment successfully saved to the database
                        Toast.makeText(Booking_Calendar.this, "Appointment created for " + appointment.getUserName() + " on " + appointment.getDate() + " at " + appointment.getTime(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Error occurred while saving the appointment
                        Toast.makeText(Booking_Calendar.this, "Failed to create appointment", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Intent intent = new Intent(Booking_Calendar.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToContactFragment() {
        Intent intent = new Intent(Booking_Calendar.this, Booking_Calendar.class);
        startActivity(intent);
        finish();
    }

    private void navigateToProfileFragment() {
        Intent intent = new Intent(Booking_Calendar.this, Profile.class);
        startActivity(intent);
        finish();
    }
}