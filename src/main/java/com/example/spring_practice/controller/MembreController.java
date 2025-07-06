package com.example.spring_practice.controller;

import com.example.spring_practice.service.AdherentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MembreController {

    private final AdherentService adherentService;

    public MembreController(AdherentService adherentService) {
        this.adherentService = adherentService;
    }

    @GetMapping("/membres")
    public String listMembres(Model model) {
        model.addAttribute("membres", adherentService.findAll());
        model.addAttribute("activePage", "membres");
        return "pages/admin/membre_list";
    }
}
