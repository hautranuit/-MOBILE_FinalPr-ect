package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
import com.example.finalproject.api.ResponseModel;
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
        password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

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

            updatePassword(email, newPassword1, newPassword1, otp);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updatePassword(String email, String newPassword, String rePassword, String otp) {
        restfulApi.updatePassword(email, newPassword, rePassword, otp).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lấy thông báo thành công hoặc lỗi từ server
                    String message = response.body().getMessage();
                    String error = response.body().getError();

                    // Hiển thị thông báo từ server
                    if (message != null) {
                        Toast.makeText(ForgotPassword3.this, message, Toast.LENGTH_SHORT).show();
                    } else if (error != null) {
                        Toast.makeText(ForgotPassword3.this, error, Toast.LENGTH_SHORT).show();
                    }

                    // Điều hướng đến màn hình đăng nhập nếu thành công
                    if (message != null && message.equals("Password updated successfully")) {
                        Intent intent = new Intent(ForgotPassword3.this, LoginScreen.class);
                        startActivity(intent);
                        finish(); // Đóng activity hiện tại
                    }

                } else {
                    // Khi server không trả về nội dung hoặc gặp lỗi
                    Toast.makeText(ForgotPassword3.this, "Failed to update password. Try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                // Xử lý khi request gặp lỗi
                Toast.makeText(ForgotPassword3.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}