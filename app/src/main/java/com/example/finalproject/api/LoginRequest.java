package com.example.finalproject.api;

public class LoginRequest {
    private String email;
    private String password;
    //private String username;

    public String getEmail() {
        return email;
    }

    public LoginRequest(String email, String password) {
        this.password = password;
        this.email = email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
}
