package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.api.ApiClient;
import com.example.finalproject.api.ApiResponse;
import com.example.finalproject.api.restful_api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {

    private boolean isPasswordVisibleCurrent = false;
    private boolean isPasswordVisibleNew = false;
    private boolean isPasswordVisibleConfirm = false;
    private EditText edtPassword, NewPassword, ConfirmPassword;
    private Button button_changepassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        String email = getIntent().getStringExtra("USER_EMAIL");
        if (email != null) {
            // Sử dụng email trong ChangePassword
            Toast.makeText(ChangePassword.this, email, Toast.LENGTH_SHORT).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Find Views
        EditText edtPassword = findViewById(R.id.edtPassword);
        EditText new_Password = findViewById(R.id.NewPassword);
        EditText confirm_Password = findViewById(R.id.ConfirmPassword);
        button_changepassword = findViewById(R.id.button_changepassword);

        button_changepassword.setOnClickListener(view -> {
            String oldPassword = edtPassword.getText().toString().trim();
            String newPassword = new_Password.getText().toString().trim();
            String confirmPassword = confirm_Password.getText().toString().trim();

            // Kiểm tra các điều kiện
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi API đổi mật khẩu
            changePassword(email, oldPassword, newPassword);
        });


        ImageView eyeIconCurrent = findViewById(R.id.eye_icon_current);
        ImageView eyeIconNew = findViewById(R.id.eye_icon_new);
        ImageView eyeIconConfirm = findViewById(R.id.eye_icon_confirm);

        ImageView statusIcon = findViewById(R.id.status_icon);

        // Set default password visibility
        edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        new_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirm_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // Toggle password visibility function
        View.OnClickListener togglePasswordVisibility = v -> {
            ImageView clickedIcon = (ImageView) v;
            EditText targetEditText;

            boolean isVisible;
            if (clickedIcon == eyeIconCurrent) {
                targetEditText = edtPassword;
                isVisible = isPasswordVisibleCurrent = !isPasswordVisibleCurrent;
            } else if (clickedIcon == eyeIconNew) {
                targetEditText = new_Password;
                isVisible = isPasswordVisibleNew = !isPasswordVisibleNew;
            } else {
                targetEditText = confirm_Password;
                isVisible = isPasswordVisibleConfirm = !isPasswordVisibleConfirm;
            }

            // Toggle logic
            if (isVisible) {
                targetEditText.setTransformationMethod(null);
                clickedIcon.setImageResource(R.drawable.ic_eye_black_close);
            } else {
                targetEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                clickedIcon.setImageResource(R.drawable.ic_eye_black);
            }
            targetEditText.setSelection(targetEditText.getText().length()); // Move cursor to end
        };

        // Assign click listeners to eye icons
        eyeIconCurrent.setOnClickListener(togglePasswordVisibility);
        eyeIconNew.setOnClickListener(togglePasswordVisibility);
        eyeIconConfirm.setOnClickListener(togglePasswordVisibility);

        // Validate passwords
        confirm_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newPass = new_Password.getText().toString();
                String confirmPass = confirm_Password.getText().toString();

                if (!newPass.isEmpty() && confirmPass.equals(newPass)) {
                    statusIcon.setImageResource(R.drawable.ic_greentick); // Green tick
                    statusIcon.setVisibility(View.VISIBLE);
                } else if (!confirmPass.isEmpty()) {
                    statusIcon.setImageResource(R.drawable.ic_red_x); // Red cross
                    statusIcon.setVisibility(View.VISIBLE);
                } else {
                    statusIcon.setVisibility(View.GONE); // Hide icon
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Initially hide the status icon
        statusIcon.setVisibility(View.GONE);
    }

    //Thay đổi mật khẩu
    private void changePassword(String email, String oldPassword, String newPassword) {
        Log.d("ChangePassword", "Preparing to call API"); // Log trước khi gọi API

        restful_api apiService = ApiClient.getRetrofitInstance().create(restful_api.class);

        Call<ApiResponse> call = apiService.changePassword(email, oldPassword, newPassword);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("ChangePassword", "API called successfully"); // Log khi kết nối API thành công

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ChangePassword", "Response received: " + response.body().toString());
                    Toast.makeText(ChangePassword.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                    // Chuyển đến màn hình LoginScreen
                    Intent intent = new Intent(ChangePassword.this, LoginScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // Đóng ChangePassword

                } else {
                    Log.d("ChangePassword", "Response failed: " + response.errorBody());
                    Toast.makeText(ChangePassword.this, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("ChangePassword", "Failed to connect to API: " + t.getMessage());
                Toast.makeText(ChangePassword.this, "Có lỗi xảy ra: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
