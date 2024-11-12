package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.finalproject.api.ApiClient;
import com.example.finalproject.api.SignupRequest;
import com.example.finalproject.api.restful_api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailSignUp extends AppCompatActivity {

    ImageView btnClose, btnShowPassword;
    Button btnConfirm;
    EditText edit_text_name, edit_text_password;
    TextView text_email, text_username;
    private boolean isPasswordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_sign_up);

        btnClose = findViewById(R.id.btnClose);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnShowPassword = findViewById(R.id.btnShowPassword);
        edit_text_name = findViewById(R.id.edit_text_name);
        text_email  = findViewById(R.id.text_email);
        edit_text_password = findViewById(R.id.edit_text_password);
        text_username = findViewById(R.id.text_username);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");

        text_email.setText(email);
        text_username.setText(username);
        edit_text_password.setText(password);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailSignUp.this, LoginScreen.class);
                startActivity(intent);
            }
        });

        btnShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible) {
                    edit_text_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isPasswordVisible = false;
                } else {
                    edit_text_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isPasswordVisible = true;
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edit_text_name.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(DetailSignUp.this, "Ô Name không được để trống", Toast.LENGTH_SHORT).show();
                } else {
                    SignupRequest signupRequest = new SignupRequest(username, password, email, password);
                    restful_api restfulApi = ApiClient.getRetrofitInstance().create(restful_api.class);

                    restfulApi.signup(signupRequest).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(DetailSignUp.this,"Signup successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DetailSignUp.this, "Signup failed" + response.message(), Toast.LENGTH_SHORT ).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(DetailSignUp.this,"Signup successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DetailSignUp.this, LoginScreen.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}