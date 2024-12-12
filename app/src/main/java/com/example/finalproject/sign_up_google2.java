package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.SignInCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class sign_up_google2 extends AppCompatActivity {

    private static final int REQ_ONE_TAP = 2;
    private FirebaseAuth mAuth;
    private EditText passwordInput;
    private Button nextButton;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_google2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        passwordInput = findViewById(R.id.password_input);
        nextButton = findViewById(R.id.next_button);
        emailTextView = findViewById(R.id.email_text);

        String email = getIntent().getStringExtra("email_key");
        if (email != null) {
            emailTextView.setText(email);
            signUpWithEmail(email);
        } else {
            Toast.makeText(this, "Không nhận được email!", Toast.LENGTH_SHORT).show();
        }

        nextButton.setOnClickListener(v -> {
            String emailInput = getIntent().getStringExtra("email_key");
            signUpWithEmail(emailInput);
        });
    }

    private void signUpWithEmail(String email) {
        String password = passwordInput.getText().toString().trim();

        if (password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(sign_up_google2.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(sign_up_google2.this, sign_up_google3.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(sign_up_google2.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential googleCredential = mAuth.getCredential(data);
                String idToken = googleCredential.getGoogleIdToken();
                if (idToken != null) {
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    Toast.makeText(this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            });
                }
            } catch (Exception e) {
                Log.e("SignInWithGoogle", "Google Sign In failed", e);
            }
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            emailTextView.setText(user.getEmail());
        } else {
            emailTextView.setText("User not signed in.");
        }
    }
}
