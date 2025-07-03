package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.ReservationEntity;
import com.example.spring_practice.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public String listReservations(Model model) {
        List<ReservationEntity> reservations = reservationService.findAll();
        model.addAttribute("reservations", reservations);
        return "pages/admin/reservation_list";
    }

    @GetMapping("/delete/{id}")
    public String deleteReservation(@PathVariable Long id) {
        reservationService.deleteById(id);
        return "redirect:/reservations";
    }
} 