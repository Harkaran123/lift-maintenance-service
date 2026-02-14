package com.example.liftmaintenance.service;

import com.example.liftmaintenance.model.ReminderSetting;
import com.example.liftmaintenance.model.Lift;
import com.example.liftmaintenance.repository.ReminderSettingRepository;
import com.example.liftmaintenance.repository.LiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderService {
    @Autowired
    private ReminderSettingRepository reminderSettingRepository;

    @Autowired
    private LiftRepository liftRepository;

    public ReminderSetting createReminder(Long liftId, ReminderSetting reminderSetting) {
        Lift lift = liftRepository.findById(liftId).orElse(null);
        if (lift == null) throw new IllegalArgumentException("Lift not found");
        reminderSetting.setLift(lift);
        return reminderSettingRepository.save(reminderSetting);
    }

    public List<ReminderSetting> getRemindersForLift(Long liftId) {
        return reminderSettingRepository.findByLiftId(liftId);
    }

    public ReminderSetting updateReminder(Long id, ReminderSetting reminderSetting) {
        ReminderSetting existing = reminderSettingRepository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setReminderTiming(reminderSetting.getReminderTiming());
        existing.setNotificationMethod(reminderSetting.getNotificationMethod());
        return reminderSettingRepository.save(existing);
    }

    public void deleteReminder(Long id) { reminderSettingRepository.deleteById(id); }
}
