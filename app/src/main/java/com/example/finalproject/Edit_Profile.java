package com.example.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.api.ApiClient;
import com.example.finalproject.api.UserResponse;
import com.example.finalproject.api.restful_api;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Edit_Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String email = getIntent().getStringExtra("USER_EMAIL");

        Log.d("DEBUG", "Email được truyền: " + email);

        //Lấy avatar
        CircleImageView avatarImageView = findViewById(R.id.imgAvatar);
        fetchAndDisplayAvatar(email, avatarImageView);

        //Gán EditText fullname
        EditText FullName = findViewById(R.id.edit_text_fullname);
        EditText UserName = findViewById(R.id.edit_text_username);
        TextView Text_Email = findViewById(R.id.text_email);

        // Gọi API để lấy thông tin người dùng và hiển thị fullname
        fetchAndDisplayUserInfo(email, FullName, UserName, Text_Email);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = FullName.getText().toString().trim();
                String username = UserName.getText().toString().trim();


                if (fullname.isEmpty() || username.isEmpty()) {
                    Toast.makeText(Edit_Profile.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateUserDetails(email, username, fullname);
            }
        });

        ImageView back = findViewById(R.id.imageBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Edit_Profile.this, SettingsScreen.class);
                String email = getIntent().getStringExtra("USER_EMAIL");
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);
            }
        });

    }

    //Phương thức gọi API để chỉnh sửa trang cá nhân
    private void updateUserDetails(String email, String username, String fullname) {
        restful_api api = ApiClient.getRetrofitInstance().create(restful_api.class);
        Call<ResponseBody> call = api.updateUserDetails(email, username, fullname);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Edit_Profile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Edit_Profile.this, "Update failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Edit_Profile.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức gọi API và hiển thị fullname, username, email
    private void fetchAndDisplayUserInfo(String email, EditText fullnameEditText, EditText usernameEditText, TextView TextEmail) {
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

                    UserResponse user = response.body();
                    Log.d("DEBUG", "Fullname: " + user.getFullname());
                    Log.d("DEBUG", "Username: " + user.getUsername());
                    Log.d("API_RAW_RESPONSE", response.raw().toString());
                    Log.d("API_BODY", new Gson().toJson(response.body()));

                    String fullname = response.body().getFullname();
                    String username = user.getUsername();
                    String email = user.getEmail();

                    if (fullname != null) {
                        // Hiển thị fullname lên EditText
                        fullnameEditText.setText(fullname);
                    } else {
                        fullnameEditText.setText("Không có tên đầy đủ.");
                    }
                    // Hiển thị username lên EditText
                    if (username != null && !username.isEmpty()) {
                        usernameEditText.setText(username);
                    } else {
                        usernameEditText.setText("Không có tên người dùng.");
                    }
                    // Hiển thị email
                    if (email != null && !email.isEmpty()) {
                        TextEmail.setText(email);
                    } else {
                        TextEmail.setText("Không có email.");
                    }
                } else {
                    Log.e("API_ERROR", "Error: " + response.message());
                    Toast.makeText(Edit_Profile.this, "Lỗi khi lấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
                Toast.makeText(Edit_Profile.this, "Không thể kết nối tới máy chủ.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Edit_Profile.this, "Không tìm thấy ảnh đại diện.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
                Toast.makeText(Edit_Profile.this, "Không thể tải ảnh đại diện.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}