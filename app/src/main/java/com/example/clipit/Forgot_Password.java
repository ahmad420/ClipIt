package com.example.clipit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Forgot_Password extends AppCompatActivity {
    private EditText emailEditText;
    private Button resetPasswordButton;
    private TextView goToLoginTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        resetPasswordButton = findViewById(R.id.resetpass);
        goToLoginTextView = findViewById(R.id.goToLogin);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == null) {
                    return;
                }
                String email = emailEditText.getText().toString().trim();
                try {
                    if (isValidEmail(email)) {
                        sendPasswordResetEmail(email);
                    } else {
                        showToast("Please enter a valid email address", true);
                    }
                } catch (Exception e) {
                    showToast("An error occurred. Please try again.", true);
                    e.printStackTrace(); // Print the error details for debugging
                }
            }
        });

        goToLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToLogin();
            }
        });
    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        if (isNewUser) {
                            showToast("This email is not registered. Please enter a valid email address.", true);
                        } else {
                            mAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(resetTask -> {
                                        if (resetTask.isSuccessful()) {
                                            showToast("Password reset email sent to " + email, false);
                                        } else {
                                            showToast("Failed to send reset email. Please try again.", true);
                                        }
                                    });
                        }
                    } else {
                        showToast("An error occurred. Please try again.", true);
                    }
                });
    }


    private void navigateToLogin() {
        // Navigate back to the login page (Login activity)
        startActivity(new Intent(Forgot_Password.this, Login.class));
        finish(); // Finish the current activity
    }

    private void showToast(String message, boolean isError) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        if (isError) {
            View toastView = toast.getView();
            toastView.setBackgroundResource(android.R.drawable.toast_frame); // Use default error color
        }

        toast.show();
    }

}
