package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Customize extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customize);

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.iconBack);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Find views
        SeekBar seekBar = findViewById(R.id.seekBarSensitivity);
        TextView currentValue = findViewById(R.id.tvminSensitivityValue);

        // Set initial value
        int initialProgress = seekBar.getProgress();
        currentValue.setText(initialProgress + "m");

        // Listener for SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the current value text
                currentValue.setText(progress + "m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Add logic if needed when user starts interacting
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Add logic if needed when user stops interacting
            }
        });
    }
}
