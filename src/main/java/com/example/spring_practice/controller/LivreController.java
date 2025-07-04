package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.LivreEntity;
import com.example.spring_practice.service.LivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
public class LivreController {
    @Autowired
    private LivreService livreService;

    @GetMapping("/livres")
    public String listLivres(@RequestParam(value = "titre", required = false) String titre, Model model) {
        List<LivreEntity> livres;
        if (titre != null && !titre.isBlank()) {
            livres = livreService.findByTitreContainingIgnoreCase(titre);
        } else {
            livres = livreService.findAll();
        }
        model.addAttribute("livres", livres);
        model.addAttribute("titre", titre);
        return "pages/client/livre_list";
    }

    @GetMapping("/livres/reserver/{id}")
    public String reserverLivre(@PathVariable Long id, Model model) {
        LivreEntity livre = livreService.findById(id);
        model.addAttribute("livre", livre);
        return "pages/client/reserver_client";
    }
} 