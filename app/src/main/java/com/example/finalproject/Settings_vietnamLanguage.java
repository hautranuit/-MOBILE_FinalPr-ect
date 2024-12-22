package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class Settings_vietnamLanguage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings_vietnam_language);


        // Áp dụng thay đổi Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Gán sự kiện nhấn cho TextView Tiếng Việt
        ImageView backbutton = findViewById(R.id.backButton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings_vietnamLanguage.this, SettingsScreenVN.class);
                String email = getIntent().getStringExtra("USER_EMAIL");
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);
            }
        });

        TextView english = findViewById(R.id.english);

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings_vietnamLanguage.this, Settings_englishLanguage.class);
                String email = getIntent().getStringExtra("USER_EMAIL");
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);
            }
        });


    }

    private void changeLanguage(String languageCode) {
        // Thiết lập cấu hình ngôn ngữ mới
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Lưu ngôn ngữ đã chọn vào SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("App_Lang", languageCode);
        editor.apply();

        // Khởi động lại Activity để áp dụng thay đổi ngôn ngữ
        Intent refresh = new Intent(this, Settings_vietnamLanguage.class);
        startActivity(refresh);
        finish();
    }

    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("App_Lang", "en"); // Mặc định là tiếng Anh
        changeLanguage(language);
    }
}
