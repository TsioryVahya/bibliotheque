package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.MvtEmpruntEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MvtEmpruntRepository extends JpaRepository<MvtEmpruntEntity, Long> {
    // Méthodes personnalisées si besoin
} 