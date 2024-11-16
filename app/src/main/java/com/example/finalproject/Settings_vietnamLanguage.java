package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
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

        // Khởi động lại ngôn ngữ theo cài đặt đã lưu (nếu có)
        loadLocale();

        // Gán sự kiện nhấn cho TextView Tiếng Việt
        TextView vietnamese = findViewById(R.id.vietnamese);
        vietnamese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage("vi"); // Thay đổi ngôn ngữ sang Tiếng Việt
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
