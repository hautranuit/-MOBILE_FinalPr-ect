package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    ImageView iconEye;
    EditText email_input, password_input;
    Button button_login;
    TextView forgot_password, create_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);

        img_bt_signup_email = findViewById(R.id.img_bt_signup_email);
        iconEye = findViewById(R.id.icon_eye);

        email_input = findViewById(R.id.email_input);
        password_input = findViewById(R.id.password_Input);
        button_login = findViewById(R.id.button_login);
        forgot_password = findViewById(R.id.forgot_password);
        create_account = findViewById(R.id.createAccount);

        iconEye.setOnClickListener(v -> {
            if (password_input.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                password_input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconEye.setImageResource(R.drawable.ic_eye);
            } else {
                password_input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                iconEye.setImageResource(R.drawable.ic_eye_close);
            }
            password_input.setSelection(password_input.getText().length());
        });

        create_account.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreen.this, SignUpYourname.class);
            startActivity(intent);
        });

        button_login.setOnClickListener(view -> {
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
                            String email = email_input.getText().toString().trim();
                            String token = response.body().getToken();
                            Toast.makeText(LoginScreen.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Lưu email vào SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("USER_EMAIL", email);
                            editor.apply();

                            // Truyền email sang DashboardActivity
                            Intent intent = new Intent(LoginScreen.this,BoxMaps.class); // Đổi tên từ BoxMaps thành DashboardActivity
                            intent.putExtra("USER_EMAIL", email); // Truyền email vào Intent
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
        });

        img_bt_signup_email.setOnClickListener(view -> {
            Intent intent = new Intent(LoginScreen.this, Google_SignUp.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        forgot_password.setOnClickListener(view -> {
            Intent intent = new Intent(LoginScreen.this, ForgotPassword1.class);
            startActivity(intent);
        });

        EditText emailEditText = findViewById(R.id.email_input);

        // Sau khi người dùng đăng nhập thành công, lấy giá trị email từ EditText
        String email = emailEditText.getText().toString();

        Intent intent = new Intent(LoginScreen.this, SettingsScreen.class);
        intent.putExtra("userEmail", email); // Gửi email qua Intent
        //startActivity(intent);
    }
}
