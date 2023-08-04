package com.example.clipit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.example.clipit.model.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Admin extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize UI elements
        TextView todayAppointmentsTitle = findViewById(R.id.textView2);
        LinearLayout appointmentsLayout = findViewById(R.id.appointmentsLayout);
        Button logoutButton = findViewById(R.id.button2);
        Button passResetButton = findViewById(R.id.button3);

        // Get the current date
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = dateFormat.format(currentDate);

        // Fetch appointments for today from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appointments")
                .whereEqualTo("date", todayDate)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Clear existing views
                    appointmentsLayout.removeAllViews();

                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        String documentId = documentSnapshot.getId();
                        // Convert document to Appointment object
                        Appointment appointment = documentSnapshot.toObject(Appointment.class);

                        // Create a LinearLayout to hold each appointment's details and buttons
                        LinearLayout appointmentLayout = new LinearLayout(this);
                        appointmentLayout.setOrientation(LinearLayout.VERTICAL);

                        // Create TextView to display appointment details
                        TextView appointmentTextView = new TextView(this);
                        appointmentTextView.setText(appointment.getTitle()+":\n"+ "Client :"+appointment.getUserName() + "\n" +
                                "Time: " + appointment.getTime()+"\n"+"Status: "+appointment.getStatus());

//                        // Create Delete Button
//                        Button deleteButton = new Button(this);
//                        deleteButton.setText("Delete");
//                        deleteButton.setOnClickListener(view -> deleteAppointment(documentId));
//
//                        // Create Complete Button
//                        Button completeButton = new Button(this);
//                        completeButton.setText("Complete");
//                        completeButton.setOnClickListener(view -> completeAppointment(documentId));

                        // Add appointment details TextView and buttons to the appointmentLayout
                        appointmentLayout.addView(appointmentTextView);
                        // Set the text size in scaled pixels (sp)
                        float textSizeSp = 20; // Set the desired text size in sp
                        appointmentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
//                        appointmentLayout.addView(deleteButton);
//                        appointmentLayout.addView(completeButton);

                        // Add the appointmentLayout to the appointmentsLayout
                        appointmentsLayout.addView(appointmentLayout);
                    }

                    // Update title with the number of appointments
                    int appointmentCount = querySnapshot.size();
                    todayAppointmentsTitle.setText("Appointments For Today (" + appointmentCount + ")");
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Toast.makeText(this, "Failed to fetch appointments", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });

        // Logout button click listener
        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            // Navigate to the login screen
            Intent intent = new Intent(Admin.this, Login.class);
            startActivity(intent);
            finish();
        });

        // Pass reset button click listener
        passResetButton.setOnClickListener(view -> {
            showResetPasswordDialog();
        });
    }

    private void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        // Create an EditText view to input the user's email
        EditText emailEditText = new EditText(this);
        emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(emailEditText);

        builder.setPositiveButton("Reset", (dialog, which) -> {
            String userEmail = emailEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(userEmail)) {
                resetUserPassword(userEmail);
            } else {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.create().show();
    }

    private void resetUserPassword(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteAppointment(String appointmentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String appointmentsCollection = "appointments";

        db.collection(appointmentsCollection)
                .document(appointmentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String userId = document.getString("userId");
                            if (userId != null && userId.equals(FirebaseAuth.getInstance().getUid())) {
                                // User has access to delete the appointment
                                db.collection(appointmentsCollection)
                                        .document(appointmentId)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Appointment deleted successfully", Toast.LENGTH_SHORT).show();
                                            // Refresh the displayed appointments
                                            recreate();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to delete appointment", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        });
                            } else {
                                Toast.makeText(this, "You don't have permission to delete this appointment", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Appointment not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error fetching appointment details", Toast.LENGTH_SHORT).show();
                        task.getException().printStackTrace();
                    }
                });
    }

    private void completeAppointment(String appointmentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String appointmentsCollection = "appointments";

        db.collection(appointmentsCollection)
                .document(appointmentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String userId = document.getString("userId");
                            if (userId != null && userId.equals(FirebaseAuth.getInstance().getUid())) {
                                // User has access to mark the appointment as completed
                                db.collection(appointmentsCollection)
                                        .document(appointmentId)
                                        .update("status", "Completed")
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Appointment marked as completed", Toast.LENGTH_SHORT).show();
                                            // Refresh the displayed appointments
                                            recreate();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to mark appointment as completed", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        });
                            } else {
                                Toast.makeText(this, "You don't have permission to mark this appointment as completed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Appointment not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error fetching appointment details", Toast.LENGTH_SHORT).show();
                        task.getException().printStackTrace();
                    }
                });
    }

}