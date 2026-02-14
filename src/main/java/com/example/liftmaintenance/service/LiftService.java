package com.example.liftmaintenance.service;

import com.example.liftmaintenance.model.Lift;
import com.example.liftmaintenance.repository.LiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LiftService {
    @Autowired
    private LiftRepository liftRepository;

    public Lift createLift(Lift lift) {
        // compute next maintenance date if possible
        if (lift.getInstallationDate() != null && lift.getMaintenanceInterval() != null) {
            lift.setNextMaintenanceDate(lift.getInstallationDate().plusMonths(lift.getMaintenanceInterval()));
        }
        return liftRepository.save(lift);
    }

    public List<Lift> getAllLifts() { return liftRepository.findAll(); }

    public Lift getLiftById(Long id) { return liftRepository.findById(id).orElse(null); }

    public Lift updateLift(Long id, Lift lift) {
        Lift existing = liftRepository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setName(lift.getName());
        existing.setArea(lift.getArea());
        existing.setBuilding(lift.getBuilding());
        existing.setInstallationDate(lift.getInstallationDate());
        existing.setClientName(lift.getClientName());
        existing.setMaintenanceInterval(lift.getMaintenanceInterval());
        if (existing.getInstallationDate() != null && existing.getMaintenanceInterval() != null) {
            existing.setNextMaintenanceDate(existing.getInstallationDate().plusMonths(existing.getMaintenanceInterval()));
        }
        return liftRepository.save(existing);
    }

    public void deleteLift(Long id) { liftRepository.deleteById(id); }
}
