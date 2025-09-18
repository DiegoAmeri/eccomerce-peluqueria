package com.peluqueria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peluqueria.model.Peluqueria;
import com.peluqueria.service.ClientService;

@RestController
@RequestMapping("/api/salons")
public class SalonController {
    
    @Autowired
    private ClientService clientService;
    
    @GetMapping("/search")
    public List<Peluqueria> searchSalons(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) List<String> services,
            @RequestParam(required = false) Boolean openNow) {
        
        return clientService.searchSalons(search, category, minRating, priceRange, services, openNow);
    }
}
