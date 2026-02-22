package com.example.liftmaintenance.repository;

import com.example.liftmaintenance.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByUserIdAndEmailAndVerifiedFalse(Long userId, String email);

    Optional<EmailVerification> findByUserIdAndEmailAndOtpCodeAndVerifiedFalse(Long userId, String email, String otpCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerification e WHERE e.expiresAt < :now OR e.verified = true")
    void deleteExpiredOrVerified(LocalDateTime now);
}
