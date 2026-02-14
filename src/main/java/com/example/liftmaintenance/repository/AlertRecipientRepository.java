package com.example.liftmaintenance.repository;

import com.example.liftmaintenance.model.AlertRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRecipientRepository extends JpaRepository<AlertRecipient, Long> {
    List<AlertRecipient> findByActiveTrue();
}
