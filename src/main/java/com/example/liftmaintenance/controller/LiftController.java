package com.example.liftmaintenance.controller;

import com.example.liftmaintenance.model.Lift;
import com.example.liftmaintenance.service.LiftService;
import com.example.liftmaintenance.service.NotificationService;
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
@RequestMapping("/api/lifts")
@Tag(name = "Lift Management", description = "APIs for managing lifts")
public class LiftController {
    @Autowired
    private LiftService liftService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/test-notification/{id}")
    @Operation(summary = "Test notification for a lift", description = "Manually triggers email notification for testing")
    public ResponseEntity<String> testNotification(@PathVariable Long id) {
        Lift lift = liftService.getLiftById(id);
        if (lift == null) {
            return ResponseEntity.notFound().build();
        }
        notificationService.sendMaintenanceReminder(lift, 7);
        return ResponseEntity.ok("Test notification sent for lift: " + lift.getName());
    }

    @PostMapping
    @Operation(summary = "Create a new lift", description = "Creates a new lift with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Lift created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Lift> createLift(@RequestBody Lift lift) {
        try {
            return ResponseEntity.status(201).body(liftService.createLift(lift));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all lifts", description = "Retrieves a list of all lifts")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of lifts")
    public ResponseEntity<List<Lift>> getAllLifts() {
        return ResponseEntity.ok(liftService.getAllLifts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lift by ID", description = "Retrieves a specific lift by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lift found"),
        @ApiResponse(responseCode = "404", description = "Lift not found")
    })
    public ResponseEntity<Lift> getLiftById(@Parameter(description = "ID of the lift to retrieve") @PathVariable Long id) {
        Lift lift = liftService.getLiftById(id);
        if (lift == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(lift);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update lift", description = "Updates an existing lift with new details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lift updated successfully"),
        @ApiResponse(responseCode = "404", description = "Lift not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Lift> updateLift(@Parameter(description = "ID of the lift to update") @PathVariable Long id, @RequestBody Lift lift) {
        Lift updated = liftService.updateLift(id, lift);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete lift", description = "Deletes a lift by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Lift deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Lift not found")
    })
    public ResponseEntity<Void> deleteLift(@Parameter(description = "ID of the lift to delete") @PathVariable Long id) {
        liftService.deleteLift(id);
        return ResponseEntity.noContent().build();
    }
}
