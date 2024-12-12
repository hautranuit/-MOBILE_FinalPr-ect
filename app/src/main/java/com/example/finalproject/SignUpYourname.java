package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpYourname extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_yourname);

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up btnClose click listener
        ImageView btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close this activity and open ActivityLoginScreen
                Intent intent = new Intent(SignUpYourname.this, LoginScreen.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });

        // Set up btnRightArrow click listener
        ImageView btnRightArrow = findViewById(R.id.btnrightarrow);
        btnRightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextName = findViewById(R.id.edit_text_name); // Giả sử ô nhập tên có ID này
                String fullName = editTextName.getText().toString().trim();

                if (fullName.isEmpty()) {
                    Toast.makeText(SignUpYourname.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(SignUpYourname.this, SignUpEmail.class);
                    intent.putExtra("fullName", fullName); // Truyền tên đầy đủ
                    startActivity(intent);
                    finish(); // Đóng Activity hiện tại
                }
            }
        });

    }
}
