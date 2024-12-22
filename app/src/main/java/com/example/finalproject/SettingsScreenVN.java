package com.example.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

public class SettingsScreenVN extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings_screen_vn);

        String email = getIntent().getStringExtra("USER_EMAIL");

        //Lấy avatar
        CircleImageView avatarImageView = findViewById(R.id.circleImageView);
        fetchAndDisplayAvatar(email, avatarImageView);


        // Gán TextView fullname
        TextView fullname = findViewById(R.id.fullName);
        // Gọi API để lấy thông tin người dùng và hiển thị fullname
        fetchAndDisplayUserInfo(email, fullname);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView language = findViewById(R.id.languages);
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsScreenVN.this, Settings_vietnamLanguage.class);
                String email = getIntent().getStringExtra("USER_EMAIL");
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);
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
                    Toast.makeText(SettingsScreenVN.this, "Không tìm thấy ảnh đại diện.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
                Toast.makeText(SettingsScreenVN.this, "Không thể tải ảnh đại diện.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức gọi API và hiển thị fullname
    private void fetchAndDisplayUserInfo(String email, TextView fullnameTextView) {
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
                    if (fullname != null) {
                        // Hiển thị fullname lên TextView
                        fullnameTextView.setText(fullname);
                    } else {
                        fullnameTextView.setText("Không có tên đầy đủ.");
                    }
                } else {
                    Log.e("API_ERROR", "Error: " + response.message());
                    Toast.makeText(SettingsScreenVN.this, "Lỗi khi lấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
                Toast.makeText(SettingsScreenVN.this, "Không thể kết nối tới máy chủ.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}