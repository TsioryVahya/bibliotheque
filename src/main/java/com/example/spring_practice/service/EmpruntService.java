package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.AdherentEntity;
import com.example.spring_practice.model.entities.EmpruntEntity;
import com.example.spring_practice.repository.EmpruntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmpruntService {
    @Autowired
    private EmpruntRepository empruntRepository;

    public List<EmpruntEntity> findAll() {
        return empruntRepository.findAll();
    }

    public Optional<EmpruntEntity> findById(Long id) {
        return empruntRepository.findById(id);
    }

    public EmpruntEntity save(EmpruntEntity emprunt) {
        // Vérification du quota d'emprunts actifs
        AdherentEntity adherent = emprunt.getAdherent();
        int quota = adherent.getProfil().getQuotaEmpruntsSimultanes();
        long actifs = empruntRepository.countByAdherentIdAndDateRetourPrevueAfter(adherent.getId(), LocalDateTime.now());
        if (actifs >= quota) {
            throw new IllegalStateException("L'adhérent a déjà atteint son quota d'emprunts simultanés.");
        }
        return empruntRepository.save(emprunt);
    }

    public void deleteById(Long id) {
        empruntRepository.deleteById(id);
    }
} 