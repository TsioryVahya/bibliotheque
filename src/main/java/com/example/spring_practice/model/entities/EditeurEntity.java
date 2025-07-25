package com.example.spring_practice.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Editeurs")
public class EditeurEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_editeur")
    private Long id;

    @Column(name = "nom", nullable = false, unique = true)
    private String nom;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
} 