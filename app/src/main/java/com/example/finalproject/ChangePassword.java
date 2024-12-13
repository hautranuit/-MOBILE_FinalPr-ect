package com.example.finalproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChangePassword extends AppCompatActivity {

    private boolean isPasswordVisibleCurrent = false;
    private boolean isPasswordVisibleNew = false;
    private boolean isPasswordVisibleConfirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Find Views
        EditText edtPassword = findViewById(R.id.edtPassword);
        EditText newPassword = findViewById(R.id.NewPassword);
        EditText confirmPassword = findViewById(R.id.ConfirmPassword);

        ImageView eyeIconCurrent = findViewById(R.id.eye_icon_current);
        ImageView eyeIconNew = findViewById(R.id.eye_icon_new);
        ImageView eyeIconConfirm = findViewById(R.id.eye_icon_confirm);

        ImageView statusIcon = findViewById(R.id.status_icon);

        // Set default password visibility
        edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // Toggle password visibility function
        View.OnClickListener togglePasswordVisibility = v -> {
            ImageView clickedIcon = (ImageView) v;
            EditText targetEditText;

            boolean isVisible;
            if (clickedIcon == eyeIconCurrent) {
                targetEditText = edtPassword;
                isVisible = isPasswordVisibleCurrent = !isPasswordVisibleCurrent;
            } else if (clickedIcon == eyeIconNew) {
                targetEditText = newPassword;
                isVisible = isPasswordVisibleNew = !isPasswordVisibleNew;
            } else {
                targetEditText = confirmPassword;
                isVisible = isPasswordVisibleConfirm = !isPasswordVisibleConfirm;
            }

            // Toggle logic
            if (isVisible) {
                targetEditText.setTransformationMethod(null);
                clickedIcon.setImageResource(R.drawable.ic_eye_black_close);
            } else {
                targetEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                clickedIcon.setImageResource(R.drawable.ic_eye_black);
            }
            targetEditText.setSelection(targetEditText.getText().length()); // Move cursor to end
        };

        // Assign click listeners to eye icons
        eyeIconCurrent.setOnClickListener(togglePasswordVisibility);
        eyeIconNew.setOnClickListener(togglePasswordVisibility);
        eyeIconConfirm.setOnClickListener(togglePasswordVisibility);

        // Validate passwords
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newPass = newPassword.getText().toString();
                String confirmPass = confirmPassword.getText().toString();

                if (!newPass.isEmpty() && confirmPass.equals(newPass)) {
                    statusIcon.setImageResource(R.drawable.ic_greentick); // Green tick
                    statusIcon.setVisibility(View.VISIBLE);
                } else if (!confirmPass.isEmpty()) {
                    statusIcon.setImageResource(R.drawable.ic_red_x); // Red cross
                    statusIcon.setVisibility(View.VISIBLE);
                } else {
                    statusIcon.setVisibility(View.GONE); // Hide icon
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Initially hide the status icon
        statusIcon.setVisibility(View.GONE);
    }
}
