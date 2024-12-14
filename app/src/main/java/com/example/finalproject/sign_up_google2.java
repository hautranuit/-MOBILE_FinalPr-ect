package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleScope;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.credentials.GetCredentialRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.playservices.auth.GoogleIdTokenCredential;
import androidx.window.layout.WindowCompat;
import androidx.window.layout.WindowInsetsCompat;
import androidx.window.layout.WindowInsetsControllerCompat;

import java.util.concurrent.Executor;

public class sign_up_google2 extends AppCompatActivity {

    private static final String TAG = "GoogleSignIn";

    private FirebaseAuth auth;
    private Button googleSignInButton;
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_google2);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize Credential Manager
        credentialManager = CredentialManager.create(this);

        googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE);
            controller.hide(WindowInsetsCompat.Type.systemBars());
        }
    }

    private void signInWithGoogle() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        Executor executor = ContextCompat.getMainExecutor(this);
        credentialManager.getCredentialAsync(request, executor, result -> {
            if (result.getCredential() instanceof GoogleIdTokenCredential) {
                GoogleIdTokenCredential credential = (GoogleIdTokenCredential) result.getCredential();
                String idToken = credential.getIdToken();
                firebaseAuthWithGoogle(idToken);
            } else {
                Log.e(TAG, "Invalid credential type");
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        updateUI(auth.getCurrentUser());
                    } else {
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(sign_up_google2.this, sign_up_google3.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Sign-In failed.", Toast.LENGTH_SHORT).show();
        }
    }
}
