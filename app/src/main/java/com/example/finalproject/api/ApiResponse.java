package com.example.finalproject.api;

public class ApiResponse {
    private String message;
    private String avatarUrl;
    private boolean success;

    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public ApiResponse(String message, String avatarUrl, boolean success) {
        this.message = message;
        this.avatarUrl = avatarUrl;
        this.success = success;
    }

    // Getters và Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
