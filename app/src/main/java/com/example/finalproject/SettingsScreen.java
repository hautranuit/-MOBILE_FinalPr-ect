package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.api.ApiClient;
import com.example.finalproject.api.ApiResponse;
import com.example.finalproject.api.restful_api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsScreen extends AppCompatActivity {
    private Switch darkModeSwitch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings_screen);

        Intent intent = getIntent();
        email = intent.getStringExtra("userEmail"); // Nhận email từ Intent

        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Email không hợp lệ hoặc trống!", Toast.LENGTH_SHORT).show();
            return;
        }


        // Xử lý khi người dùng nhấn vào TextView để xóa tài khoản
        TextView removeAccount = findViewById(R.id.removeAccount);  // Lấy TextView

        removeAccount.setOnClickListener(view -> {
            // Gọi hàm xóa tài khoản
            deleteAccount(email);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsScreen.this, MainComponent.class);
                startActivity(intent);
            }
        });

        TextView languages = findViewById(R.id.languages);

        languages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang Activity SettingsEnglishLanguageActivity
                Intent intent = new Intent(SettingsScreen.this, Settings_englishLanguage.class);
                startActivity(intent);
            }
        });

        TextView helpCenter = findViewById(R.id.helpCenter);

        helpCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsScreen.this, Settings_helpCenter.class);
                startActivity(intent);
            }
        });

        TextView logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsScreen.this, LoginScreen.class);
                startActivity(intent);
            }
        });

        TextView editprofile = findViewById(R.id.editProfile);
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsScreen.this, DetailSignUp.class);
                startActivity(intent);
            }
        });

        TextView changePassword = findViewById(R.id.editChangePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsScreen.this, ChangePassword.class);
                startActivity(intent);
            }
        });

        TextView helpcenter = findViewById(R.id.helpCenter);
        helpcenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsScreen.this, Settings_helpCenter.class);
                startActivity(intent);
            }
        });

        TextView Aboutus = findViewById(R.id.aboutUs);
        Aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsScreen.this, SettingsAboutUs.class);
                startActivity(intent);
            }
        });


        // Kiểm tra chế độ từ SharedPreferences
        sharedPreferences = getSharedPreferences("AppSettingsPrefs", 0);
        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);

        // Đặt chế độ ban đầu
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // Gán switch
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        darkModeSwitch.setChecked(isDarkMode);

        // Lắng nghe sự kiện bấm switch
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Lưu trạng thái vào SharedPreferences
            editor = sharedPreferences.edit();
            editor.putBoolean("DarkMode", isChecked);
            editor.apply();
        });

    }

    // Phương thức xóa tài khoản
    private void deleteAccount(String email) {
        restful_api restfulApi = ApiClient.getRetrofitInstance().create(restful_api.class);
        Call<ApiResponse> call = restfulApi.deleteUser(email);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SettingsScreen.this, "Tài khoản đã được xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsScreen.this, "Không thể xóa tài khoản. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(SettingsScreen.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}