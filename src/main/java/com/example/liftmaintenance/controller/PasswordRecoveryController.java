package com.example.liftmaintenance.controller;

import com.example.liftmaintenance.dto.*;
import com.example.liftmaintenance.model.User;
import com.example.liftmaintenance.repository.UserRepository;
import com.example.liftmaintenance.service.PasswordRecoveryService;
import com.example.liftmaintenance.service.PasswordRecoveryService.RecoveryResponse;
import com.example.liftmaintenance.service.PasswordRecoveryService.RecoveryResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Password Recovery", description = "APIs for password recovery and email verification")
public class PasswordRecoveryController {

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password/username recovery", description = "Sends recovery email with password and/or username. Email must match the registered email.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recovery email sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or email not found")
    })
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse("Email is required"));
        }

        if (!request.isForgotPassword() && !request.isForgotUsername()) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse("Please select at least one recovery option"));
        }

        try {
            RecoveryResponse result = passwordRecoveryService.sendRecoveryEmail(
                    request.getEmail(),
                    request.isForgotPassword(),
                    request.isForgotUsername()
            );

            if (result.getResult() == RecoveryResult.SUCCESS) {
                Map<String, String> response = new HashMap<>();
                response.put("message", result.getMessage() + ". Please check your inbox.");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(new AuthController.ErrorResponse(result.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse("Failed to send recovery email: " + e.getMessage()));
        }
    }

    @PostMapping("/send-verification-otp")
    @Operation(summary = "Send email verification OTP", description = "Sends a 6-digit OTP to the provided email for verification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> sendVerificationOtp(@RequestBody EmailVerificationRequest request, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthController.ErrorResponse("Unauthorized"));
        }

        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AuthController.ErrorResponse("User not found"));
        }

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse("Email is required"));
        }

        User user = userOpt.get();

        try {
            String otp = passwordRecoveryService.generateAndSendOtp(user.getId(), request.getEmail());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Verification code sent to your email. Please check your inbox.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse("Failed to send verification code: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-email-otp")
    @Operation(summary = "Verify email OTP", description = "Verifies the 6-digit OTP sent to email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired OTP"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> verifyEmailOtp(@RequestBody VerifyOtpRequest request, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthController.ErrorResponse("Unauthorized"));
        }

        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AuthController.ErrorResponse("User not found"));
        }

        if (request.getEmail() == null || request.getEmail().isEmpty() ||
            request.getOtpCode() == null || request.getOtpCode().isEmpty()) {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse("Email and verification code are required"));
        }

        User user = userOpt.get();

        boolean verified = passwordRecoveryService.verifyOtp(user.getId(), request.getEmail(), request.getOtpCode());

        if (verified) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email verified successfully");
            response.put("email", request.getEmail());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(new AuthController.ErrorResponse("Invalid or expired verification code"));
        }
    }
}
