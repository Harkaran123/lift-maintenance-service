package com.example.liftmaintenance.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Lift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String area;
    private String building;
    private LocalDate installationDate;
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private Integer maintenanceInterval; // months
    private LocalDate nextMaintenanceDate;
    
    @OneToMany(mappedBy = "lift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReminderSetting> reminderSettings = new ArrayList<>();

    public Lift() {}

    public Lift(Long id) { this.id = id; }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
    public LocalDate getInstallationDate() { return installationDate; }
    public void setInstallationDate(LocalDate installationDate) { this.installationDate = installationDate; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }
    public Integer getMaintenanceInterval() { return maintenanceInterval; }
    public void setMaintenanceInterval(Integer maintenanceInterval) { this.maintenanceInterval = maintenanceInterval; }
    public LocalDate getNextMaintenanceDate() { return nextMaintenanceDate; }
    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) { this.nextMaintenanceDate = nextMaintenanceDate; }
    public List<ReminderSetting> getReminderSettings() { return reminderSettings; }
    public void setReminderSettings(List<ReminderSetting> reminderSettings) { this.reminderSettings = reminderSettings; }
}
