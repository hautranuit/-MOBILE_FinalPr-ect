package com.example.finalproject.api;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface restful_api {
    // Endpoint đăng nhập
    @POST("/authentication/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // Endpoint đăng ký
//    @POST("/authentication/signup")
//    Call<String> signup(@Body SignupRequest signupRequest);
    @Multipart
    @POST("/authentication/signup")
    Call<ApiResponse> signup(
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("repassword") RequestBody repassword,
            @Part("username") RequestBody username,
            @Part("fullname") RequestBody fullname,
            @Part MultipartBody.Part avatar
    );

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


    @GET("/map/potholes/count-by-eachday")
    Call<Map<String, Long>> countPotholesByDay();

    // API lấy avatar dựa trên email
    @GET("/authentication/{email}/avatar")
    Call<ResponseBody> getAvatarByEmail(@Path("email") String email);

    // API xóa người dùng dựa trên email
    @DELETE("/authentication/{email}")
    Call<ApiResponse> deleteUser(@Path("email") String email);

    // API đổi mật khẩu dựa trên email
    @PUT("/authentication/{email}/change-password")
    Call<ApiResponse> changePassword(
            @Path("email") String email,
            @Query("oldPassword") String oldPassword,
            @Query("newPassword") String newPassword
    );

    // API lấy thông tin người dùng dựa trên email
    @GET("/authentication/{email}")
    Call<UserResponse> getUserByEmail(@Path("email") String email);

    //API lấy danh sách thông tin những ổ gà một người đã khai báo
    @GET("/map/potholes/user-reports")
    Call<List<Map<String, Object>>> getPotholesReportedByUser(@Query("email") String email);


}
