package com.example.finalproject.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface restful_api {
    @POST("/authentication/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // Endpoint đăng ký
    @POST("/authentication/signup")
    Call<String> signup(@Body SignupRequest signupRequest);

    // Endpoint đăng xuất
    @POST("/authentication/logout")
    Call<String> logout(@Header("Authorization") String token);

    // Endpoint lưu người dùng
    @POST("/authentication/accounts")
    Call<String> saveUser(@Body User user);

    //Gửi mã OTP đến email
    @POST("/authentication/send-otp")
    Call<Void> sendOtp(@Query("email") String email);

    //Xác nhận OTP
    @POST("/authentication/verify-otp")
    Call<ResponseMessage> verifyOtp(
            @Query("email") String email,
            @Query("otp") String otp,
            @Query("newpassword") String newpassword
    );
}
