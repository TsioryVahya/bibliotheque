package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.ReservationEntity;
import com.example.spring_practice.model.entities.MvtReservationEntity;
import com.example.spring_practice.model.entities.StatutReservationEntity;
import com.example.spring_practice.model.entities.PenaliteEntity;
import com.example.spring_practice.repository.ReservationRepository;
import com.example.spring_practice.repository.MvtReservationRepository;
import com.example.spring_practice.repository.StatutReservationRepository;
import com.example.spring_practice.repository.PenaliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private MvtReservationRepository mvtReservationRepository;
    
    @Autowired
    private StatutReservationRepository statutReservationRepository;

    @Autowired
    private PenaliteRepository penaliteRepository;

    public List<ReservationEntity> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<ReservationEntity> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public ReservationEntity save(ReservationEntity reservation) {
        // Vérification des pénalités pour la date de réservation
        Long adherentId = reservation.getAdherent().getId();
        List<PenaliteEntity> penalites = penaliteRepository.findByAdherentId(adherentId);
        for (PenaliteEntity p : penalites) {
            java.time.LocalDate debut = p.getDateDebut();
            java.time.LocalDate fin = debut.plusDays(p.getJour() - 1);
            java.time.LocalDate dateResa = reservation.getDateAReserver();
            if ((dateResa.isEqual(debut) || dateResa.isAfter(debut)) && dateResa.isBefore(fin.plusDays(1))) {
                throw new IllegalStateException("L'adhérent a une pénalité couvrant la date de réservation et ne peut pas réserver.");
            }
        }
        // Sauvegarde de la réservation
        ReservationEntity savedReservation = reservationRepository.save(reservation);
        
        // Création du mouvement de réservation avec statut "En attente"
        StatutReservationEntity statutEnAttente = statutReservationRepository.findByCodeStatut("En attente")
            .orElseThrow(() -> new IllegalStateException("Statut 'En attente' introuvable"));
        
        MvtReservationEntity mvt = new MvtReservationEntity();
        mvt.setReservation(savedReservation);
        mvt.setStatutNouveau(statutEnAttente);
        mvt.setDateMouvement(LocalDateTime.now());
        mvtReservationRepository.save(mvt);
        
        return savedReservation;
    }

    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }
} 