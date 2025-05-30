package com.example.smartchief;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.text.InputType;
import android.widget.ImageView;

//
//public class Login extends AppCompatActivity {
//    TextInputEditText editTextEmail, editTextPassword;
//    Button buttonLogin;
//    FirebaseAuth mAuth;
//    TextView textView;
//
//    private static final String TAG = "LoginActivity"; // Tag for logging
//    ImageView showHidePassword;
//    boolean passwordVisible = false;
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.d(TAG, "onStart: Checking if user is logged in");
//
////        // Check if user is signed in
////        FirebaseUser currentUser = mAuth.getCurrentUser();
////        if (currentUser != null) {
////            Log.d(TAG, "onStart: User is already logged in, redirecting to MainActivity");
////            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
////            startActivity(intent);
////            finish();
////        } else {
////            Log.d(TAG, "onStart: No user logged in");
////        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_login);
//
//        Log.d(TAG, "onCreate: Login Activity started");
//
//        mAuth = FirebaseAuth.getInstance();
//
//        editTextEmail = findViewById(R.id.email);
//        editTextPassword = findViewById(R.id.password);
//        buttonLogin = findViewById(R.id.btn_login);
//        textView = findViewById(R.id.registerNow);
//
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: Register Now clicked");
//                Intent intent = new Intent(getApplicationContext(), Register.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        buttonLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email, password;
//                email = String.valueOf(editTextEmail.getText());
//                password = String.valueOf(editTextPassword.getText());
//
//                if (TextUtils.isEmpty(email)) {
//                    Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "Login failed: Email is empty");
//                    return;
//                }
//                if (TextUtils.isEmpty(password)) {
//                    Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "Login failed: Password is empty");
//                    return;
//                }
//
//                Log.d(TAG, "Attempting to log in with email: " + email);
//
////                mAuth.signInWithEmailAndPassword(email, password)
////                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
////                            @Override
////                            public void onComplete(@NonNull Task<AuthResult> task) {
////                                if (task.isSuccessful()) {
////                                    Log.d(TAG, "Login successful");
////                                    Toast.makeText(Login.this, "login successful", Toast.LENGTH_SHORT).show();
////
////                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
////                                    if (user != null) {
////                                        Log.d(TAG, "User email: " + user.getEmail());
////                                    }
////
////                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
////                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                    if (!isFinishing()) {
////                                        startActivity(intent);
////                                    }
////
////                                    finish();
////                                } else {
////                                    Log.e(TAG, "Authentication failed", task.getException());
////                                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
////                                }
////                            }
//                                    mAuth.signInWithEmailAndPassword(email, password)
//                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    if (task.isSuccessful()) {
//                                        Log.d(TAG, "Login successful");
//
//                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                                        if (user != null) {
//                                            Log.d(TAG, "User email: " + user.getEmail());
//                                        }
//
//                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        Log.d(TAG, "Starting MainActivity");
//                                        startActivity(intent);
//                                        Log.d(TAG, "MainActivity started, finishing LoginActivity");
//                                        finish();
//                                    } else {
//                                        Log.e(TAG, "Authentication failed", task.getException());
//                                        Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//
//            }
//        });
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//}


public class Login extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    TextView textView;
    ImageView showHidePassword;
    boolean passwordVisible = false;

    private static final String TAG = "LoginActivity";

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Checking if user is logged in");

    }

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false); // optional
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate: Login Activity started");

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        textView = findViewById(R.id.registerNow);
        showHidePassword = findViewById(R.id.show_hide_password);
        Button buttonAutoLogin = findViewById(R.id.btn_auto_login);

        textView.setOnClickListener(v -> {
            Log.d(TAG, "Register Now clicked");
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        });

        showHidePassword.setOnClickListener(v -> {
            if (passwordVisible) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showHidePassword.setImageResource(R.drawable.ic_visibility_off);
                passwordVisible = false;
            } else {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showHidePassword.setImageResource(R.drawable.ic_visibility);
                passwordVisible = true;
            }
            editTextPassword.setSelection(editTextPassword.getText().length());
        });

        buttonLogin.setOnClickListener(v -> {
            String email = String.valueOf(editTextEmail.getText()).trim();
            String password = String.valueOf(editTextPassword.getText()).trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Login failed: Email is empty");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Login failed: Password is empty");
                return;
            }

            Log.d(TAG, "Attempting to log in with email: " + email);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // ✅ Auto-login button logic
        buttonAutoLogin.setOnClickListener(v -> {
            String autoEmail = "individualproject2025@gmail.com";
            String autoPassword = "Samsung2025";

            editTextEmail.setText(autoEmail);
            editTextPassword.setText(autoPassword);

            // Simulate login click
            buttonLogin.performClick();
        });
    }

}
