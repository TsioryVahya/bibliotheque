package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.AdherentEntity;
import com.example.spring_practice.repository.AdherentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdherentService {

    private final AdherentRepository adherentRepository;

    public AdherentService(AdherentRepository adherentRepository) {
        this.adherentRepository = adherentRepository;
    }

    public List<AdherentEntity> findAll() {
        return adherentRepository.findAll();
    }
}
