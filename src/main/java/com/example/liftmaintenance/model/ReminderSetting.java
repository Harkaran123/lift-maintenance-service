package com.example.liftmaintenance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class ReminderSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lift_id")
    @JsonIgnore
    private Lift lift;

    // days before maintenance when reminder should be sent
    private Integer reminderTiming;

    // e.g. "Email", "WhatsApp"
    private String notificationMethod;

    public ReminderSetting() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Lift getLift() { return lift; }
    public void setLift(Lift lift) { this.lift = lift; }
    public Integer getReminderTiming() { return reminderTiming; }
    public void setReminderTiming(Integer reminderTiming) { this.reminderTiming = reminderTiming; }
    public String getNotificationMethod() { return notificationMethod; }
    public void setNotificationMethod(String notificationMethod) { this.notificationMethod = notificationMethod; }
}
