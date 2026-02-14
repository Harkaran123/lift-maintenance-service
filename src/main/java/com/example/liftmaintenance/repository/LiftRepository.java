package com.example.liftmaintenance.repository;

import com.example.liftmaintenance.model.Lift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiftRepository extends JpaRepository<Lift, Long> {
}
