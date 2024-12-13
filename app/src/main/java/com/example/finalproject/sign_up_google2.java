package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class sign_up_google2 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText passwordInput;
    private Button btnNext;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_google2);

        mAuth = FirebaseAuth.getInstance();
        emailTextView = findViewById(R.id.email_text);
        passwordInput = findViewById(R.id.password_input);
        btnNext = findViewById(R.id.next_button);

        // Retrieve the email from the Intent passed from sign_up_google4
        Intent intent = getIntent();
        String email = intent.getStringExtra("email_key");
        if (email != null) {
            emailTextView.setText(email);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });

        btnNext.setOnClickListener(v -> {
            String emailFromView = emailTextView.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (!emailFromView.isEmpty() && !password.isEmpty()) {
                signInWithGoogle(emailFromView ,password);
            } else {
                Toast.makeText(this, "Please enter all the required information!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithGoogle(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Check if the account is a Google account
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.getProviderData().stream()
                                .anyMatch(authProvider -> authProvider.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID))) {
                            Toast.makeText(this, "Successfully logged in with Google account!", Toast.LENGTH_SHORT).show();
                            // Handle post-login logic
                        } else {
                            Toast.makeText(this, "This is not a Google account.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(sign_up_google2.this, sign_up_google3.class); // Replace with your next activity
            startActivity(intent);
            finish();
        }
    }
}
