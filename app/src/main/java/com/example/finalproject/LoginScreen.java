package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.api.ApiClient;
import com.example.finalproject.api.LoginRequest;
import com.example.finalproject.api.LoginResponse;
import com.example.finalproject.api.restful_api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends AppCompatActivity {

    ImageButton img_bt_signup_email;
    EditText email_input, password_input;
    Button button_login;
    TextView forgot_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);

        img_bt_signup_email = findViewById(R.id.img_bt_signup_email);
        email_input = findViewById(R.id.email_input);
        password_input = findViewById(R.id.password_input);
        button_login = findViewById(R.id.button_login);
        forgot_password = findViewById(R.id.forgot_password);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_input.getText().toString().trim();
                String password = password_input.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginScreen.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    LoginRequest loginRequest = new LoginRequest(email, password);
                    restful_api restfulApi = ApiClient.getRetrofitInstance().create(restful_api.class);

                    restfulApi.login(loginRequest).enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                String email = email_input.getText().toString().trim(); // Get the email from input
                                String token = response.body().getToken();
                                Toast.makeText(LoginScreen.this,"Login successful", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginScreen.this, BoxMaps.class);
                                intent.putExtra("USER_EMAIL", email); // Pass the email to BoxMaps activity
                                startActivity(intent);
                                finish();
                            } else {
                                String errorMessage = "Login failed: " + (response.message() != null ? response.message() : "Unknown error");
                                Toast.makeText(LoginScreen.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            Toast.makeText(LoginScreen.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        img_bt_signup_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreen.this, SignUpEmail.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreen.this, ForgotPassword1.class);
                startActivity(intent);
            }
        });
    }
}