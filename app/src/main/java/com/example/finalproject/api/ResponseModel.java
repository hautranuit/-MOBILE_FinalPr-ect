package com.example.finalproject.api;

public class ResponseModel {
    private String message;
    private String error;

    // Getter và Setter cho các trường
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

