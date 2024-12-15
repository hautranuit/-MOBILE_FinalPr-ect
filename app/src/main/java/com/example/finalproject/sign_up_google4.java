package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class sign_up_google4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_google4);

        // Xử lý Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tìm các View cần thiết
        EditText emailInput = findViewById(R.id.email_input);
        Button nextButton = findViewById(R.id.next_button);

        // Xử lý khi nhấn nút "Tiếp theo"
        nextButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();

            if (isValidEmail(email)) {
                // Email hợp lệ -> Chuyển sang activity_sign_up_google2
                Intent intent = new Intent(sign_up_google4.this, sign_up_google3.class);
                intent.putExtra("email_key", email);
                startActivity(intent);
                finish(); // Đóng SignUp_Google4
            } else {
                // Email không hợp lệ -> Hiển thị thông báo lỗi
                Toast.makeText(sign_up_google4.this, "Email không hợp lệ. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức kiểm tra email có hợp lệ không
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
