package com.example.liftmaintenance.dto;

public class LoginResponse {

    private String token;
    private String type;
    private String username;
    private String message;

    public LoginResponse() {
        this.type = "Bearer";
    }

    public LoginResponse(String token, String username, String message) {
        this.token = token;
        this.type = "Bearer";
        this.username = username;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
