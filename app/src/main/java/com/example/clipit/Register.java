package com.example.clipit;
import com.example.clipit.model.User;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;


public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText editTextEmail,editTextPassword ,exitTextConfirmPass ,editTextName;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView, theMsg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_register);
        // hide the top of the app
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        initViews();
        initButtons();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Register_button:
                registerAction(view);
                break;
            case R.id.forgot_password_link:
                goToLogin();
                break;

        }

    }


    private void registerAction(View view) {
        progressBar.setVisibility(View.VISIBLE);
        theMsg.setVisibility(View.VISIBLE);
        String name, email, password, confirmPass;
        name = String.valueOf((editTextName.getText()));
        email = String.valueOf(editTextEmail.getText());
        password = String.valueOf(editTextPassword.getText());
        confirmPass = String.valueOf((exitTextConfirmPass.getText()));

        setProgressBar();


        if (TextUtils.isEmpty(name)) {
            Toast.makeText(Register.this, "Enter your name", Toast.LENGTH_SHORT).show();
            theMsg.setText("Name is empty");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
            theMsg.setText("Email is empty");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
            theMsg.setText("Password is empty");
            return;
        }
        if (!password.equals(confirmPass)) {
            Toast.makeText(Register.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            theMsg.setText("Passwords don't match");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                String userEmail = user.getEmail();

                                // Create a new user document in the "users" collection in Firestore
                                db.collection("users").document(userId)
                                        .set(new User(userEmail, name))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Register.this, "Account Created.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                theMsg.setText("Failed to store user data.");
                                            }
                                        });
                            }
                        } else {
                            theMsg.setText("Authentication failed");
                            Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        editTextName=findViewById(R.id.exitTextName);
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.password1);
        exitTextConfirmPass = findViewById(R.id.confirm_password);
        buttonReg = findViewById(R.id.Register_button);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.forgot_password_link);
        theMsg = findViewById(R.id.msg);
    }

    private void initButtons() {
        buttonReg.setOnClickListener(this);
        textView.setOnClickListener(this);
    }

    public void setProgressBar() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                theMsg.setVisibility(View.GONE);
            }
        }, 3000);
    }

    public void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }
}