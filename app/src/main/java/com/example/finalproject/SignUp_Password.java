package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUp_Password extends AppCompatActivity {

    ImageView btnClose, btnrightarrow,iconEye;
    EditText edit_text_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_password);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String username = intent.getStringExtra("username");
        String fullName = intent.getStringExtra("fullName"); // Nếu có fullName từ bước trước

        btnClose = findViewById(R.id.btnClose);
        btnrightarrow = findViewById(R.id.btnrightarrow);
        edit_text_password = findViewById(R.id.edit_text_password);
        edit_text_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        iconEye = findViewById(R.id.icon_eye);

        iconEye.setOnClickListener(v -> {
            if (edit_text_password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                edit_text_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconEye.setImageResource(R.drawable.ic_eye);
            } else {
                edit_text_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                iconEye.setImageResource(R.drawable.ic_eye_close);
            }
            edit_text_password.setSelection(edit_text_password.getText().length());
        });

        // Xử lý sự kiện nút Close
        btnClose.setOnClickListener(view -> {
            Intent closeIntent = new Intent(SignUp_Password.this, LoginScreen.class);
            startActivity(closeIntent);
        });

        // Xử lý sự kiện nút RightArrow
        btnrightarrow.setOnClickListener(view -> {
            String password = edit_text_password.getText().toString().trim();

            if (password.isEmpty()) {
                Toast.makeText(SignUp_Password.this, "Ô Password không được để trống", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(SignUp_Password.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            } else {
                // Chuyển sang màn hình tiếp theo (SignUp_Avt)
                Intent nextIntent = new Intent(SignUp_Password.this, SignUp_Avt.class);
                nextIntent.putExtra("email", email);
                nextIntent.putExtra("username", username);
                nextIntent.putExtra("fullName", fullName); // Nếu có
                nextIntent.putExtra("password", password);

                try {
                    startActivity(nextIntent);
                } catch (Exception e) {
                    Toast.makeText(SignUp_Password.this, "Lỗi khi chuyển Activity: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        // Điều chỉnh padding để phù hợp với system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
