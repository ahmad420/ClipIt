package com.example.clipit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity implements View.OnClickListener  {


    EditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView, theMsg2, goRegister, goToResetPass;


    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), Admin.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initButtons();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                loginAction(view);
                break;
            case R.id.goRegieter:
                goToRegisterPage();
                break;
            case R.id.forgot_password_link:
                navToPage();
                break;
        }
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        buttonLogin = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.forgot_password_link);
        theMsg2 = findViewById(R.id.msg);
        goRegister = findViewById(R.id.goRegieter);
        goToResetPass = findViewById(R.id.forgot_password_link);
    }

    private void loginAction(View view) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        String email, password;
        if (editTextEmail != null && editTextPassword != null) {
            email = editTextEmail.getText().toString();
            password = editTextPassword.getText().toString();
        } else {
            // Handle the case where editTextEmail or editTextPassword is null
            theMsg2.setText("Error: Please try again later");
            return;
        }

        setProgressBar();

        if (TextUtils.isEmpty(email)) {
            if (theMsg2 != null) {
                theMsg2.setText("Email is empty");
            }
            Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            if (theMsg2 != null) {
                theMsg2.setText("Password is empty");
            }
            Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                checkIsAdmin(user);
                            } else {
                                Toast.makeText(Login.this, "User is null", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (theMsg2 != null) {
                                theMsg2.setText("Authentication failed");
                            }
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    private void checkIsAdmin(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = user.getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        boolean isAdmin = documentSnapshot.getBoolean("isAdmin");
                        if (isAdmin) {
                            Intent intent = new Intent(getApplicationContext(), Admin.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(Login.this, "User document does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Login.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    Log.e("Login", "Error fetching user data: " + e.getMessage());
                });
    }


    private void initButtons() {
        buttonLogin.setOnClickListener(this);
        textView.setOnClickListener(this);
        goRegister.setOnClickListener(this);
        goToResetPass.setOnClickListener(this);
    }

    public void goToRegisterPage() {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
        finish();
    }

    public void setProgressBar() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                theMsg2.setVisibility(View.GONE);
            }
        }, 8000);
    }

    public void navToPage() {
        Intent intent = new Intent(getApplicationContext(), Forgot_Password.class);
        startActivity(intent);
        finish();
    }
}