package com.example.liftmaintenance.controller;

import com.example.liftmaintenance.model.AlertRecipient;
import com.example.liftmaintenance.service.AlertRecipientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alert-recipients")
@CrossOrigin(origins = "http://localhost:4200")
public class AlertRecipientController {

    @Autowired
    private AlertRecipientService alertRecipientService;

    @GetMapping
    public List<AlertRecipient> getAllRecipients() {
        return alertRecipientService.getAllRecipients();
    }

    @GetMapping("/active")
    public List<AlertRecipient> getActiveRecipients() {
        return alertRecipientService.getActiveRecipients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertRecipient> getRecipientById(@PathVariable Long id) {
        return alertRecipientService.getRecipientById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AlertRecipient createRecipient(@RequestBody AlertRecipient recipient) {
        return alertRecipientService.createRecipient(recipient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertRecipient> updateRecipient(@PathVariable Long id, @RequestBody AlertRecipient recipient) {
        try {
            return ResponseEntity.ok(alertRecipientService.updateRecipient(id, recipient));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipient(@PathVariable Long id) {
        alertRecipientService.deleteRecipient(id);
        return ResponseEntity.ok().build();
    }
}
