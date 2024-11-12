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

public class SignUp_Password extends AppCompatActivity {

    ImageView btnClose, btnrightarrow;
    EditText edit_text_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_password);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String username = intent.getStringExtra("username");

        btnClose = findViewById(R.id.btnClose);
        btnrightarrow = findViewById(R.id.btnrightarrow);
        edit_text_password = findViewById(R.id.edit_text_password);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp_Password.this, SignUpUsername.class);
                startActivity(intent);
            }
        });

        btnrightarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = edit_text_password.getText().toString().trim();

                if (password.isEmpty()) {
                    Toast.makeText(SignUp_Password.this, "Ô Password không được để trống", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(SignUp_Password.this, DetailSignUp.class);
                    intent.putExtra("email", email);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    startActivity(intent);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}