package com.example.liftmaintenance.dto;

public class ForgotPasswordRequest {

    private String email;
    private boolean forgotPassword;
    private boolean forgotUsername;

    public ForgotPasswordRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isForgotPassword() {
        return forgotPassword;
    }

    public void setForgotPassword(boolean forgotPassword) {
        this.forgotPassword = forgotPassword;
    }

    public boolean isForgotUsername() {
        return forgotUsername;
    }

    public void setForgotUsername(boolean forgotUsername) {
        this.forgotUsername = forgotUsername;
    }
}
