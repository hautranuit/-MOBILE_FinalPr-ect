package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUp_Google1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_google1);

        // Xử lý Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tìm TextView và thiết lập sự kiện click
        TextView useAnotherAccount = findViewById(R.id.use_another_account);
        useAnotherAccount.setOnClickListener(view -> {
            // Đóng activity hiện tại và chuyển sang SignUp_Google2
            Intent intent = new Intent(SignUp_Google1.this, sign_up_google4.class);
            startActivity(intent);
            finish(); // Đóng SignUp_Google1 để không quay lại được
        });
    }
}