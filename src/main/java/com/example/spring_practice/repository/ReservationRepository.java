package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
} 