package com.example.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.api.ApiClient;
import com.example.finalproject.api.UserResponse;
import com.example.finalproject.api.restful_api;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainComponent extends AppCompatActivity {
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_component);

        //Nhận dữ liệu từ intent
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        // Gán TextView fullname
        TextView fullname = findViewById(R.id.fullName);
        // Gán TextView email
        TextView email = findViewById(R.id.Email);
        // Gọi API để lấy thông tin người dùng và hiển thị fullname
        fetchAndDisplayUserInfo(userEmail, fullname, email);

        CircleImageView avatarImageView = findViewById(R.id.circleImageView);
        fetchAndDisplayAvatar(userEmail, avatarImageView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(MainComponent.this, BoxMaps.class);
                 String email = getIntent().getStringExtra("USER_EMAIL");
                 intent.putExtra("USER_EMAIL", email);
                 startActivity(intent);
             }
        });

        TextView dashboard = findViewById(R.id.dashboard);
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainComponent.this, DashBoard.class);
                String email = getIntent().getStringExtra("USER_EMAIL");
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);
            }
        });

        TextView setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainComponent.this, SettingsScreen.class);
                String email = getIntent().getStringExtra("USER_EMAIL");
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);
            }
        });

        TextView customize = findViewById(R.id.Customize);
        customize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainComponent.this, Customize.class);
                startActivity(intent);
            }
        });
    }
    // Phương thức gọi API và hiển thị fullname
    private void fetchAndDisplayUserInfo(String email, TextView fullnameTextView, TextView emailTextView) {
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy email!", Toast.LENGTH_SHORT).show();
            return;
        }

        restful_api apiService = ApiClient.getRetrofitInstance().create(restful_api.class);

        Call<UserResponse> call = apiService.getUserByEmail(email);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String fullname = response.body().getFullname();
                    String email = response.body().getEmail();
                    if (fullname != null) {
                        // Hiển thị fullname lên TextView
                        fullnameTextView.setText(fullname);
                        // Hiển thị email lên TextView
                        emailTextView.setText(email);
                    } else {
                        fullnameTextView.setText("Không có tên đầy đủ.");
                        emailTextView.setText("Không có email đầy đủ.");
                    }
                } else {
                    Log.e("API_ERROR", "Error: " + response.message());
                    Toast.makeText(MainComponent.this, "Lỗi khi lấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
                Toast.makeText(MainComponent.this, "Không thể kết nối tới máy chủ.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAndDisplayAvatar(String email, CircleImageView avatarImageView) {
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy email!", Toast.LENGTH_SHORT).show();
            return;
        }

        restful_api apiService = ApiClient.getRetrofitInstance().create(restful_api.class);
        Call<ResponseBody> call = apiService.getAvatarByEmail(email);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Convert the response to a Bitmap
                    Bitmap avatarBitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    // Set the Bitmap to CircleImageView
                    avatarImageView.setImageBitmap(avatarBitmap);
                } else {
                    Log.e("API_ERROR", "Error: " + response.message());
                    Toast.makeText(MainComponent.this, "Không tìm thấy ảnh đại diện.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
                Toast.makeText(MainComponent.this, "Không thể tải ảnh đại diện.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}