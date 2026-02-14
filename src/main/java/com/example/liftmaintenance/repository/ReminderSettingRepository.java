package com.example.liftmaintenance.repository;

import com.example.liftmaintenance.model.ReminderSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderSettingRepository extends JpaRepository<ReminderSetting, Long> {
    List<ReminderSetting> findByLiftId(Long liftId);
}
