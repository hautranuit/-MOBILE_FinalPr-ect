package com.example.finalproject.api;

public class SignupRequest {
    private String username;


    private String password;


    private String email;


    private String repassword;

    public SignupRequest() {}
    public SignupRequest(String username, String password, String email, String repassword) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.repassword = repassword;
    }

    public String getRepassword() {
        return repassword;
    }

    public void setRepassword(String repassword) {
        this.repassword = repassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
