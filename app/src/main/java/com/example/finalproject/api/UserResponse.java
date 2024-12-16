package com.example.finalproject.api;

public class UserResponse {
    private String email;
    private String username;
    private String avatarUrl;
    private String message;
    private String fullname;

    // Constructor cho phản hồi thành công
    public UserResponse(String email, String username, String avatarUrl, String fullname) {
        this.email = email;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.fullname = fullname;
        this.message = "Thành công";
    }

    // Constructor cho phản hồi lỗi
    public UserResponse(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
