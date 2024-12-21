package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.finalproject.api.ApiClient;
import com.example.finalproject.api.restful_api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword2 extends AppCompatActivity {
    TextView instruction, resendCode;
    Button submitCodeButton;
    EditText edt1, edt2, edt3, edt4, edt5, edt6;
    restful_api restfulApi;
    ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password2);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        resendCode = findViewById(R.id.resendCode);
        submitCodeButton = findViewById(R.id.submitCodeButton);
        edt1 = findViewById(R.id.edt1);
        edt2 = findViewById(R.id.edt2);
        edt3 = findViewById(R.id.edt3);
        edt4 = findViewById(R.id.edt4);
        edt5 = findViewById(R.id.edt5);
        edt6 = findViewById(R.id.edt6);
        restfulApi = ApiClient.getRetrofitInstance().create(restful_api.class);
        backButton = findViewById(R.id.backButton);

        instruction = findViewById(R.id.instruction);
        instruction.setText("We just sent you a verification code via:" + email);

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendOtp(email);
                startCountdownTimer(); // Reset lại thời gian khi gửi mã OTP mới
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassword2.this, ForgotPassword1.class);
                startActivity(intent);
            }
        });

        submitCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String o1 = edt1.getText().toString().trim();
                String o2 = edt2.getText().toString().trim();
                String o3 = edt3.getText().toString().trim();
                String o4 = edt4.getText().toString().trim();
                String o5 = edt5.getText().toString().trim();
                String o6 = edt6.getText().toString().trim();

                // Kiểm tra các ô không trống
                if (o1.isEmpty() || o2.isEmpty() || o3.isEmpty() || o4.isEmpty() || o5.isEmpty() || o6.isEmpty()) {
                    Toast.makeText(ForgotPassword2.this, "Please fill all OTP fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ghép OTP và chuyển sang màn hình ForgotPassword3
                Intent intent = new Intent(ForgotPassword2.this, ForgotPassword3.class);
                intent.putExtra("o1", o1);
                intent.putExtra("o2", o2);
                intent.putExtra("o3", o3);
                intent.putExtra("o4", o4);
                intent.putExtra("o5", o5);
                intent.putExtra("o6", o6);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Áp dụng TextWatcher
        setupOtpInputs();
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
                        Toast.makeText(ForgotPassword2.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPassword2.this, "Unexpected response format!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPassword2.this, "Failed to send OTP. Try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(ForgotPassword2.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupOtpInputs() {
        EditText[] otpFields = {edt1, edt2, edt3, edt4, edt5, edt6};

        for (int i = 0; i < otpFields.length; i++) {
            int currentIndex = i;

            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Chuyển focus khi người dùng nhập
                    if (s.length() == 1) {
                        if (currentIndex < otpFields.length - 1) {
                            otpFields[currentIndex + 1].requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            otpFields[i].setOnKeyListener((v, keyCode, event) -> {
                // Kiểm tra nếu người dùng bấm phím Backspace và ô hiện tại trống
                if (event.getAction() == android.view.KeyEvent.ACTION_DOWN &&
                        keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                        otpFields[currentIndex].getText().length() == 0 &&
                        currentIndex > 0) {
                    otpFields[currentIndex - 1].requestFocus();
                }
                return false;
            });
        }
    }

    // Hàm đếm ngược
    private void startCountdownTimer() {
        TextView countdownTimer = findViewById(R.id.expiryInfo); // Đảm bảo bạn có TextView trong layout với ID này

        new CountDownTimer(59000, 1000) { // 1 phút, cập nhật mỗi giây
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                countdownTimer.setText("The verify code will expire in 00:" + secondsRemaining + " s");
            }

            public void onFinish() {
                countdownTimer.setText("The verification code has expired. Please request a new one.");
                resendCode.setEnabled(true); // Bật lại nút gửi lại mã
            }
        }.start();

        resendCode.setEnabled(false); // Vô hiệu hóa nút gửi lại mã trong khi đếm ngược
    }



}