package com.example.finalproject.api;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface restful_api {
    // Endpoint đăng nhập
    @POST("/authentication/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // Endpoint đăng ký
    @POST("/authentication/signup")
    Call<String> signup(@Body SignupRequest signupRequest);

    // Endpoint đăng xuất
    @POST("/authentication/logout")
    Call<String> logout(@Header("Authorization") String token);

    // Endpoint gửi OTP
    @POST("/authentication/send-otp")
    Call<Map<String, String>> sendOtp(@Query("email") String email);

    // Endpoint xác nhận OTP
    @POST("/authentication/verify-otp")
    Call<String> verifyOtp(
            @Query("email") String email,
            @Query("otp") String otp
    );

    @POST("/authentication/update-password")
    Call<ResponseModel> updatePassword(
            @Query("email") String email,
            @Query("newpassword") String newPassword,
            @Query("repassword") String rePassword,
            @Query("otp") String otp
    );

    // Làm mới token
    @POST("/authentication/refresh-token")
    Call<LoginResponse> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);

    // Endpoint lấy tất cả các ổ gà
    @GET("/map/potholes")
    Call<List<Pothole>> getAllPotholes();

    // Endpoint lấy ổ gà theo email
    @GET("/map/potholes/email")
    Call<List<Pothole>> getPotholesByEmail(@Query("email") String email);

    // Endpoint lấy ổ gà theo khoảng thời gian
    @GET("/map/potholes/time")
    Call<List<Pothole>> getPotholesByTime(@Query("startTime") String startTime, @Query("endTime") String endTime);

    // Endpoint lấy ổ gà theo size
    @GET("/map/potholes/size")
    Call<List<Pothole>> getPotholesBySize(@Query("size") String size);

    // Endpoint thêm ổ gà mới
    @POST("/map/addpotholes")
    Call<Map<String, Object>> addPothole(@Body Pothole pothole);

    // Endpoint cập nhật ổ gà
    @PUT("/map/potholes/{id}")
    Call<Map<String, Object>> updatePothole(@Path("id") Long id, @Body Pothole updatedPothole);

    // Endpoint đếm số lượng ổ gà theo email
    @GET("/map/potholes/count-by-email")
    Call<Long> countPotholesByEmail(@Query("email") String email);

    // Endpoint đếm số lượng ổ gà theo khoảng thời gian
    @GET("/map/potholes/count-by-time")
    Call<Long> countPotholesByDate(@Query("startTime") String startTime, @Query("endTime") String endTime);

    // Endpoint đếm số lượng ổ gà theo size
    @GET("/map/potholes/count-by-size")
    Call<Long> countPotholesBySize(@Query("size") String size);

    // Endpoint đếm số lượng ổ gà theo size và email
    @GET("/map/potholes/count-by-size-and-email")
    Call<Map<String, Long>> countPotholesBySizeAndEmail(@Query("email") String email);
}
