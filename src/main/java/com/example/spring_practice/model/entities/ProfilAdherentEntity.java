package com.example.spring_practice.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Profils_Adherent")
public class ProfilAdherentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profil")
    private Long id;

    @Column(name = "nom_profil", nullable = false, unique = true)
    private String nomProfil;

    @Column(name = "quota_emprunts_simultanes", nullable = false)
    private int quotaEmpruntsSimultanes = 3;

    @Column(name = "duree_pret_domicile_jours", nullable = false)
    private int dureePretDomicileJours = 21;

    @Column(name = "duree_pret_sur_place_heures", nullable = false)
    private int dureePretSurPlaceHeures = 3;

    @Column(name = "peut_prolonger_pret", nullable = false)
    private boolean peutProlongerPret = true;

    @Column(name = "jours_penalite_par_retard", nullable = false)
    private int joursPenaliteParRetard = 1;

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomProfil() {
        return nomProfil;
    }

    public void setNomProfil(String nomProfil) {
        this.nomProfil = nomProfil;
    }

    public int getQuotaEmpruntsSimultanes() {
        return quotaEmpruntsSimultanes;
    }

    public void setQuotaEmpruntsSimultanes(int quotaEmpruntsSimultanes) {
        this.quotaEmpruntsSimultanes = quotaEmpruntsSimultanes;
    }

    public int getDureePretDomicileJours() {
        return dureePretDomicileJours;
    }

    public void setDureePretDomicileJours(int dureePretDomicileJours) {
        this.dureePretDomicileJours = dureePretDomicileJours;
    }

    public int getDureePretSurPlaceHeures() {
        return dureePretSurPlaceHeures;
    }

    public void setDureePretSurPlaceHeures(int dureePretSurPlaceHeures) {
        this.dureePretSurPlaceHeures = dureePretSurPlaceHeures;
    }

    public boolean isPeutProlongerPret() {
        return peutProlongerPret;
    }

    public void setPeutProlongerPret(boolean peutProlongerPret) {
        this.peutProlongerPret = peutProlongerPret;
    }

    public int getJoursPenaliteParRetard() {
        return joursPenaliteParRetard;
    }

    public void setJoursPenaliteParRetard(int joursPenaliteParRetard) {
        this.joursPenaliteParRetard = joursPenaliteParRetard;
    }
}