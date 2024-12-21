package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.finalproject.api.UserResponse;
import com.example.finalproject.api.restful_api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        String email = getIntent().getStringExtra("USER_EMAIL");
        /*if (email != null) {
            // Sử dụng email trong SettingsScreen
            Toast.makeText(SettingsScreen.this, email, Toast.LENGTH_SHORT).show();
        }*/

        // Gán TextView fullname
        TextView fullname = findViewById(R.id.fullName);
        // Gọi API để lấy thông tin người dùng và hiển thị fullname
        fetchAndDisplayUserInfo(email, fullname);

        // Xử lý khi người dùng nhấn vào TextView để xóa tài khoản
        TextView removeAccount = findViewById(R.id.removeAccount);

        removeAccount.setOnClickListener(view -> {
            //String loggedInEmail = "22520433@gm.uit.edu.vn"; // Lấy email từ session hoặc SharedPreferences
            deleteUser(email);

        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setUpNavigation();
    }

    // Phương thức gọi API và hiển thị fullname
    private void fetchAndDisplayUserInfo(String email, TextView fullnameTextView) {
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy email!", Toast.LENGTH_SHORT).show();
            return;
        }

        restful_api apiService = ApiClient.getRetrofitInstance().create(restful_api.class);

        Call<UserResponse> call = apiService.getUserByEmail(email);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String fullname = response.body().getFullname();
                    if (fullname != null) {
                        // Hiển thị fullname lên TextView
                        fullnameTextView.setText(fullname);
                    } else {
                        fullnameTextView.setText("Không có tên đầy đủ.");
                    }
                } else {
                    Log.e("API_ERROR", "Error: " + response.message());
                    Toast.makeText(SettingsScreen.this, "Lỗi khi lấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
                Toast.makeText(SettingsScreen.this, "Không thể kết nối tới máy chủ.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpNavigation() {
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsScreen.this, MainComponent.class);
            startActivity(intent);
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
                String email = getIntent().getStringExtra("USER_EMAIL");
                intent.putExtra("USER_EMAIL", email);
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

        TextView PrivacyPolicy = findViewById(R.id.privacyPolicy);
        PrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsScreen.this, SettingsPrivacyPolicy.class);
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
    private void deleteUser(String email) {
        restful_api apiService = ApiClient.getRetrofitInstance().create(restful_api.class);

        Call<ApiResponse> call = apiService.deleteUser(email);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    // Chuyển sang màn hình WelcomeScreen
                    Intent intent = new Intent(SettingsScreen.this, WelcomeScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Đóng màn hình SettingsScreen sau khi xóa thành công
                } else {
                    Log.e("API_ERROR", "Error: " + response.message());
                    Toast.makeText(getApplicationContext(), "Lỗi xóa người dùng: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Không thể kết nối tới máy chủ.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}