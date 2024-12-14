package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.credentials.Credential;
import com.google.android.gms.credentials.Credentials;
import com.google.android.gms.credentials.CredentialsOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class sign_up_google2 extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001; // Request code for Credential Manager
    private static final String TAG = "GoogleSignIn";

    private FirebaseAuth mAuth;
    private Button googleSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_google2);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In using Credential Manager
        CredentialsOptions options = new CredentialsOptions.Builder()
                .setShowSaveDialog(true)
                .build();

        googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        Intent signInIntent = Credentials.getClient(this).getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<Credential> task = Credentials.getClient(this).getCredentialFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                Credential credential = task.getResult(Status.class).getCredential();
                if (credential != null) {
                    firebaseAuthWithGoogle(credential.getId());
                }
            } catch (Exception e) {
                Log.e(TAG, "Google Sign-In failed", e);
                Toast.makeText(this, "Google Sign-In failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI
                        updateUI(mAuth.getCurrentUser());
                    } else {
                        // Sign in failed
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Navigate to the next activity
            Intent intent = new Intent(sign_up_google2.this, sign_up_google3.class); // Replace with your activity
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Sign-In failed.", Toast.LENGTH_SHORT).show();
        }
    }
}
