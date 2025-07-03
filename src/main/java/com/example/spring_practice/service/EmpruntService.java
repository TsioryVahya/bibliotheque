package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.AdherentEntity;
import com.example.spring_practice.model.entities.EmpruntEntity;
import com.example.spring_practice.model.entities.StatutEmpruntEntity;
import com.example.spring_practice.model.entities.MvtEmpruntEntity;
import com.example.spring_practice.repository.EmpruntRepository;
import com.example.spring_practice.repository.StatutEmpruntRepository;
import com.example.spring_practice.repository.MvtEmpruntRepository;
import com.example.spring_practice.repository.AdherentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmpruntService {
    @Autowired
    private EmpruntRepository empruntRepository;
    @Autowired
    private StatutEmpruntRepository statutEmpruntRepository;
    @Autowired
    private MvtEmpruntRepository mvtEmpruntRepository;
    @Autowired
    private AdherentRepository adherentRepository;

    public List<EmpruntEntity> findAll() {
        return empruntRepository.findAll();
    }

    public Optional<EmpruntEntity> findById(Long id) {
        return empruntRepository.findById(id);
    }

    public EmpruntEntity save(EmpruntEntity emprunt) {
        // Recharge l'adhérent pour avoir le profil complet
        AdherentEntity adherent = adherentRepository.findById(emprunt.getAdherent().getId())
            .orElseThrow(() -> new IllegalStateException("Adhérent introuvable"));
        int quota = adherent.getProfil().getQuotaEmpruntsSimultanes();
        long actifs = empruntRepository.countByAdherentIdAndDateRetourPrevueAfter(adherent.getId(), LocalDateTime.now());
        if (actifs >= quota) {
            throw new IllegalStateException("L'adhérent a déjà atteint son quota d'emprunts simultanés.");
        }
        emprunt.setAdherent(adherent); // pour que l'emprunt ait l'adhérent complet
        EmpruntEntity savedEmprunt = empruntRepository.save(emprunt);
        // Ajout du mouvement d'emprunt avec statut 'En cours'
        StatutEmpruntEntity statutEnCours = statutEmpruntRepository.findByCodeStatut("En cours")
            .orElseThrow(() -> new IllegalStateException("Statut 'En cours' introuvable"));
        MvtEmpruntEntity mvt = new MvtEmpruntEntity();
        mvt.setEmprunt(savedEmprunt);
        mvt.setStatutNouveau(statutEnCours);
        mvt.setDateMouvement(LocalDateTime.now());
        mvtEmpruntRepository.save(mvt);
        return savedEmprunt;
    }

    public void deleteById(Long id) {
        empruntRepository.deleteById(id);
    }

    public void returnEmprunt(Long empruntId) {
        EmpruntEntity emprunt = empruntRepository.findById(empruntId)
            .orElseThrow(() -> new IllegalArgumentException("Emprunt non trouvé"));
        StatutEmpruntEntity statutRendu = statutEmpruntRepository.findByCodeStatut("Rendu")
            .orElseThrow(() -> new IllegalStateException("Statut 'Rendu' introuvable"));
        MvtEmpruntEntity mvt = new MvtEmpruntEntity();
        mvt.setEmprunt(emprunt);
        mvt.setStatutNouveau(statutRendu);
        mvt.setDateMouvement(LocalDateTime.now());
        mvtEmpruntRepository.save(mvt);
    }

    public String getLastStatutForEmprunt(Long empruntId) {
        return mvtEmpruntRepository.findTopByEmpruntIdOrderByDateMouvementDesc(empruntId)
            .map(mvt -> mvt.getStatutNouveau().getCodeStatut())
            .orElse("Inconnu");
    }
} 