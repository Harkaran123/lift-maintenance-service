package com.example.liftmaintenance.service;

import com.example.liftmaintenance.model.AlertRecipient;
import com.example.liftmaintenance.repository.AlertRecipientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertRecipientService {

    @Autowired
    private AlertRecipientRepository alertRecipientRepository;

    public List<AlertRecipient> getAllRecipients() {
        return alertRecipientRepository.findAll();
    }

    public List<AlertRecipient> getActiveRecipients() {
        return alertRecipientRepository.findByActiveTrue();
    }

    public Optional<AlertRecipient> getRecipientById(Long id) {
        return alertRecipientRepository.findById(id);
    }

    public AlertRecipient createRecipient(AlertRecipient recipient) {
        return alertRecipientRepository.save(recipient);
    }

    public AlertRecipient updateRecipient(Long id, AlertRecipient recipientDetails) {
        AlertRecipient recipient = alertRecipientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alert recipient not found"));
        recipient.setName(recipientDetails.getName());
        recipient.setEmail(recipientDetails.getEmail());
        recipient.setPhone(recipientDetails.getPhone());
        recipient.setActive(recipientDetails.isActive());
        return alertRecipientRepository.save(recipient);
    }

    public void deleteRecipient(Long id) {
        alertRecipientRepository.deleteById(id);
    }
}
