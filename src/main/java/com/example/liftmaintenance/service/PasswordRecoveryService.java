package com.example.liftmaintenance.service;

import com.example.liftmaintenance.model.EmailVerification;
import com.example.liftmaintenance.model.User;
import com.example.liftmaintenance.repository.EmailVerificationRepository;
import com.example.liftmaintenance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordRecoveryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int OTP_EXPIRY_MINUTES = 15;

    public enum RecoveryResult {
        SUCCESS,
        EMAIL_NOT_FOUND
    }

    public static class RecoveryResponse {
        private final RecoveryResult result;
        private final String message;

        public RecoveryResponse(RecoveryResult result, String message) {
            this.result = result;
            this.message = message;
        }

        public RecoveryResult getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }
    }

    public RecoveryResponse sendRecoveryEmail(String email, boolean forgotPassword, boolean forgotUsername) {
        // Find user by email - must match exactly
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return new RecoveryResponse(RecoveryResult.EMAIL_NOT_FOUND,
                    "Email not found. Please enter the email registered with your account.");
        }

        User user = userOpt.get();

        if (forgotPassword && forgotUsername) {
            return sendBothRecoveryEmail(email, user);
        } else if (forgotPassword) {
            return sendPasswordRecoveryEmail(email, user);
        } else if (forgotUsername) {
            return sendUsernameRecoveryEmail(email, user);
        }

        return new RecoveryResponse(RecoveryResult.EMAIL_NOT_FOUND, "Please select at least one recovery option");
    }

    private RecoveryResponse sendPasswordRecoveryEmail(String email, User user) {
        // Generate new random password
        String newPassword = generateRandomPassword();

        // Update user's password in database - old password is now invalid
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        String username = user.getUsername();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Recovery - Lift Maintenance System");
        message.setText("Hello,\n\nYour password has been reset.\n\nUsername: " + username + "\nNew Password: " + newPassword + "\n\nYour old password is no longer valid. Please login with your new password and change it immediately.\n\nRegards,\nLift Maintenance System");

        mailSender.send(message);
        return new RecoveryResponse(RecoveryResult.SUCCESS, "Recovery email sent successfully");
    }

    private RecoveryResponse sendUsernameRecoveryEmail(String email, User user) {
        String username = user.getUsername();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Username Recovery - Lift Maintenance System");
        message.setText("Hello,\n\nYour username is: " + username + "\n\nRegards,\nLift Maintenance System");

        mailSender.send(message);
        return new RecoveryResponse(RecoveryResult.SUCCESS, "Recovery email sent successfully");
    }

    private RecoveryResponse sendBothRecoveryEmail(String email, User user) {
        // Generate new random password
        String newPassword = generateRandomPassword();

        // Update user's password in database - old password is now invalid
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        String username = user.getUsername();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Account Recovery - Lift Maintenance System");
        message.setText("Hello,\n\nYour account details are:\n\nUsername: " + username + "\nNew Password: " + newPassword + "\n\nYour old password is no longer valid. Please login with your new password and change it immediately.\n\nRegards,\nLift Maintenance System");

        mailSender.send(message);
        return new RecoveryResponse(RecoveryResult.SUCCESS, "Recovery email sent successfully");
    }

    public String generateAndSendOtp(Long userId, String email) {
        // Clean up old OTPs
        emailVerificationRepository.deleteExpiredOrVerified(LocalDateTime.now());

        // Generate 6-digit OTP
        String otp = generateOtp();

        // Save OTP to database
        EmailVerification verification = new EmailVerification();
        verification.setUserId(userId);
        verification.setEmail(email);
        verification.setOtpCode(otp);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        emailVerificationRepository.save(verification);

        // Send OTP email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification - Lift Maintenance System");
        message.setText("Hello,\n\nYour email verification code is: " + otp + "\n\nThis code will expire in " + OTP_EXPIRY_MINUTES + " minutes.\n\nRegards,\nLift Maintenance System");

        mailSender.send(message);

        return otp;
    }

    public boolean verifyOtp(Long userId, String email, String otpCode) {
        Optional<EmailVerification> verificationOpt = emailVerificationRepository
                .findByUserIdAndEmailAndOtpCodeAndVerifiedFalse(userId, email, otpCode);

        if (verificationOpt.isEmpty()) {
            return false;
        }

        EmailVerification verification = verificationOpt.get();

        if (verification.isExpired()) {
            return false;
        }

        verification.setVerified(true);
        emailVerificationRepository.save(verification);

        return true;
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
