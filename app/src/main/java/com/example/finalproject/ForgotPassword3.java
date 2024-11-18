package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.api.ApiClient;
import com.example.finalproject.api.ResponseMessage;
import com.example.finalproject.api.restful_api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword3 extends AppCompatActivity {
    ImageView backButton;
    EditText password1, password2;
    CheckBox showpassword;
    Button verifyButton;
    restful_api restfulApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password3);

        Intent intent = getIntent();
        String o1 = intent.getStringExtra("o1");
        String o2 = intent.getStringExtra("o2");
        String o3 = intent.getStringExtra("o3");
        String o4 = intent.getStringExtra("o4");
        String o5 = intent.getStringExtra("o5");
        String o6 = intent.getStringExtra("o6");

        if (o1 == null || o2 == null || o3 == null || o4 == null || o5 == null || o6 == null) {
            Toast.makeText(this, "Invalid OTP format", Toast.LENGTH_SHORT).show();
            return;
        }
        String otp = o1 + o2 + o3 + o4 + o5 + o6;
        String email = intent.getStringExtra("email");

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassword3.this, ForgotPassword2.class);
                startActivity(intent);
            }
        });

        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
        showpassword = findViewById(R.id.showpassword);

        showpassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                password1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                password2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            password1.setSelection(password1.getText().length());
            password2.setSelection(password2.getText().length());
        });

        restfulApi = ApiClient.getRetrofitInstance().create(restful_api.class);
        verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(view -> {
            String newPassword1 = password1.getText().toString().trim();
            String newPassword2 = password2.getText().toString().trim();

            if (newPassword1.isEmpty() || newPassword2.isEmpty()) {
                Toast.makeText(ForgotPassword3.this, "Password can not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword1.equals(newPassword2)) {
                Toast.makeText(ForgotPassword3.this, "Password do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyOtp(email, otp, newPassword1);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void verifyOtp(String email, String otp, String newpassword) {
        restfulApi.verifyOtp(email, otp, newpassword).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPassword3.this, "Password has been updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPassword3.this, LoginScreen.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ForgotPassword3.this, "Failed to verify OTP. Try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Toast.makeText(ForgotPassword3.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}