package com.example.liftmaintenance.scheduler;

import com.example.liftmaintenance.model.Lift;
import com.example.liftmaintenance.model.ReminderSetting;
import com.example.liftmaintenance.repository.LiftRepository;
import com.example.liftmaintenance.repository.ReminderSettingRepository;
import com.example.liftmaintenance.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;

@Component
@EnableScheduling
public class ReminderScheduler {
    @Autowired
    private LiftRepository liftRepository;

    @Autowired
    private ReminderSettingRepository reminderSettingRepository;

    @Autowired
    private NotificationService notificationService;

    // runs daily at 9:00 AM
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void checkAndNotify() {
        System.out.println("Running daily reminder check at " + LocalDate.now());
        
        List<Lift> lifts = liftRepository.findAll();
        LocalDate today = LocalDate.now();
        int notificationsSent = 0;
        
        for (Lift lift : lifts) {
            if (lift.getNextMaintenanceDate() == null) continue;
            
            List<ReminderSetting> reminders = reminderSettingRepository.findByLiftId(lift.getId());
            for (ReminderSetting r : reminders) {

                LocalDate notifyOn = lift.getNextMaintenanceDate().minusDays(r.getReminderTiming() == null ? 0 : r.getReminderTiming());
                if (notifyOn.equals(today)){
                    System.out.println("Sending notification for lift " + lift.getName() + " via " + r.getNotificationMethod());
                    notificationService.sendMaintenanceReminder(lift, r.getReminderTiming());
                    notificationsSent++;
                }
            }
        }
        
        System.out.println("Daily reminder check completed. Notifications sent: " + notificationsSent);
    }
}
