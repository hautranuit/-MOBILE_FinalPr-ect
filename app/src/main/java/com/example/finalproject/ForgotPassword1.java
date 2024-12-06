package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.api.ApiClient;
import com.example.finalproject.api.restful_api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword1 extends AppCompatActivity {
    Button cancelButton, resetPasswordButton;
    EditText emailInput;
    restful_api restfulApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password1);

        cancelButton = findViewById(R.id.cancelButton);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        emailInput = findViewById(R.id.emailInput);
        restfulApi = ApiClient.getRetrofitInstance().create(restful_api.class);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassword1.this, LoginScreen.class);
                startActivity(intent);
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(ForgotPassword1.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendOtp(email);
                Intent intent = new Intent(ForgotPassword1.this, ForgotPassword2.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void sendOtp(String email) {
        restfulApi.sendOtp(email).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    // Lấy JSON phản hồi từ API
                    Map<String, String> responseBody = response.body();
                    if (responseBody != null && responseBody.containsKey("message")) {
                        String message = responseBody.get("message"); // Lấy thông điệp từ API
                        Toast.makeText(ForgotPassword1.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPassword1.this, "Unexpected response format!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPassword1.this, "Failed to send OTP. Try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(ForgotPassword1.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}