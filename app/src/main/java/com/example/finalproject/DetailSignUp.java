package com.example.finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.api.ApiClient;
import com.example.finalproject.api.ApiResponse;
import com.example.finalproject.api.SignupRequest;
import com.example.finalproject.api.restful_api;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailSignUp extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView btnClose, btnShowPassword, imgAvatar;
    Button btnConfirm;
    CircleImageView btnEditAvatar;
    EditText edit_text_name, edit_text_password;
    TextView text_email, text_username;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_sign_up);

        // Khởi tạo View
        btnClose = findViewById(R.id.btnClose);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnShowPassword = findViewById(R.id.btnShowPassword);
        btnEditAvatar = findViewById(R.id.btnEditAvatar);
        imgAvatar = findViewById(R.id.imgAvatar);
        edit_text_name = findViewById(R.id.edit_text_name);
        text_email = findViewById(R.id.text_email);
        edit_text_password = findViewById(R.id.edit_text_password);
        text_username = findViewById(R.id.text_username);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String repassword = intent.getStringExtra("repassword");
        String fullName = intent.getStringExtra("fullName"); // Lấy thêm fullName
        Uri avatarUri = intent.getParcelableExtra("avatarUri");

        // Hiển thị thông tin
        text_email.setText(email);
        text_username.setText(username);
        edit_text_password.setText(password);

        // Hiển thị fullName nếu có
        if (fullName != null && !fullName.isEmpty()) {
            edit_text_name.setText(fullName); // Gán giá trị vào ô nhập tên
        }

        // Nếu có ảnh đại diện từ Intent thì hiển thị
        if (avatarUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), avatarUri);
                imgAvatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Sự kiện nút btnClose
        btnClose.setOnClickListener(view -> {
            Intent loginIntent = new Intent(DetailSignUp.this, LoginScreen.class);
            startActivity(loginIntent);
        });

        // Sự kiện nút btnShowPassword
        btnShowPassword.setOnClickListener(view -> {
            if (isPasswordVisible) {
                edit_text_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                isPasswordVisible = false;
            } else {
                edit_text_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                isPasswordVisible = true;
            }
        });

        // Sự kiện nút btnEditAvatar
        btnEditAvatar.setOnClickListener(view -> openImageChooser());

        // Sự kiện nút btnConfirm
        btnConfirm.setOnClickListener(view -> {
            String name = edit_text_name.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(DetailSignUp.this, "Ô Name không được để trống", Toast.LENGTH_SHORT).show();
            } else {
                // Chuyển đổi các giá trị sang RequestBody
                RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email != null ? email : "");
                RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), password);
                RequestBody repasswordBody = RequestBody.create(MediaType.parse("text/plain"), repassword);
                RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), name);
                RequestBody fullnameBody = RequestBody.create(MediaType.parse("text/plain"), fullName);

                // Tạo MultipartBody.Part cho avatar
                File avatarFile = new File(getRealPathFromURI(avatarUri)); // Chuyển đổi URI thành đường dẫn file
                RequestBody avatarBody = RequestBody.create(MediaType.parse("image/*"), avatarFile);
                MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("avatar", avatarFile.getName(), avatarBody);

                // Tạo API instance
                restful_api restfulApi = ApiClient.getRetrofitInstance().create(restful_api.class);

                // Gọi API
                restfulApi.signup(emailBody, passwordBody, repasswordBody, usernameBody, fullnameBody, avatarPart).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(DetailSignUp.this, "Signup successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DetailSignUp.this, "Signup failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(DetailSignUp.this, "Signup failed", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(DetailSignUp.this, LoginScreen.class);
                        startActivity(loginIntent);
                    }
                });
            }
        });

        // Xử lý Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tính năng làm mờ cho EditText tên người dùng
        setHintColorOnFocus(edit_text_name, "Enter Your Full Name");
    }

    // Mở hộp thoại chọn ảnh
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        } else {
            return uri.getPath(); // Trường hợp URI không phải từ media store
        }
    }

    // Xử lý kết quả chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgAvatar.setImageBitmap(bitmap); // Cập nhật ảnh đại diện
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setHintColorOnFocus(EditText editText, String hintText) {
        // Set default hint và màu chữ
        editText.setHint(hintText);
        editText.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        editText.setTextColor(getResources().getColor(android.R.color.darker_gray)); // Màu chữ mặc định là đen

        // Lắng nghe sự kiện khi EditText nhận hoặc mất focus
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editText.setHint(""); // Xóa hint khi người dùng bắt đầu nhập
                editText.setHintTextColor(getResources().getColor(android.R.color.transparent)); // Ẩn hint
                editText.setTextColor(getResources().getColor(android.R.color.white)); // Màu chữ sau khi bắt đầu nhập là màu trắng
            } else {
                if (editText.getText().toString().isEmpty()) {
                    editText.setHint(hintText); // Hiển thị lại hint nếu người dùng không nhập gì
                    editText.setHintTextColor(getResources().getColor(android.R.color.darker_gray)); // Màu mờ cho hint
                    editText.setTextColor(getResources().getColor(android.R.color.black)); // Đặt lại màu chữ thành đen khi mất focus và không có văn bản
                }
            }
        });
    }
}
