package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings_englishLanguage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings_english_language);

        String email = getIntent().getStringExtra("USER_EMAIL");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView vietnamese = findViewById(R.id.vietnamese);

        vietnamese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings_englishLanguage.this, Settings_vietnamLanguage.class);
                String email = getIntent().getStringExtra("USER_EMAIL");
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);
            }
        });

        ImageView backbutton = findViewById(R.id.backButton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings_englishLanguage.this, SettingsScreen.class);
                String email = getIntent().getStringExtra("USER_EMAIL");
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);
            }
        });

    }
}