package com.example.liftmaintenance.controller;

import com.example.liftmaintenance.model.ReminderSetting;
import com.example.liftmaintenance.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lifts/{liftId}/reminders")
@Tag(name = "Reminder Management", description = "APIs for managing lift reminders")
public class ReminderController {
    @Autowired
    private ReminderService reminderService;

    @PostMapping
    @Operation(summary = "Create a reminder", description = "Creates a new reminder for a specific lift")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reminder created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Lift not found")
    })
    public ResponseEntity<ReminderSetting> createReminder(@Parameter(description = "ID of the lift") @PathVariable Long liftId, @RequestBody ReminderSetting reminderSetting) {
        ReminderSetting saved = reminderService.createReminder(liftId, reminderSetting);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping
    @Operation(summary = "Get reminders for lift", description = "Retrieves all reminders for a specific lift")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved reminders"),
        @ApiResponse(responseCode = "404", description = "Lift not found")
    })
    public ResponseEntity<List<ReminderSetting>> getReminders(@Parameter(description = "ID of the lift") @PathVariable Long liftId) {
        return ResponseEntity.ok(reminderService.getRemindersForLift(liftId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update reminder", description = "Updates an existing reminder")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reminder updated successfully"),
        @ApiResponse(responseCode = "404", description = "Reminder not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ReminderSetting> updateReminder(@Parameter(description = "ID of the lift") @PathVariable Long liftId, @Parameter(description = "ID of the reminder") @PathVariable Long id, @RequestBody ReminderSetting reminderSetting) {
        ReminderSetting updated = reminderService.updateReminder(id, reminderSetting);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reminder", description = "Deletes a specific reminder")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reminder deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Reminder not found")
    })
    public ResponseEntity<Void> deleteReminder(@Parameter(description = "ID of the lift") @PathVariable Long liftId, @Parameter(description = "ID of the reminder") @PathVariable Long id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build();
    }
}
