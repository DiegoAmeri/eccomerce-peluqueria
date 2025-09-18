package com.peluqueria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peluqueria.model.Peluqueria;
import com.peluqueria.repository.PeluqueriaRepository;

@RestController
@RequestMapping("/api/peluquerias")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PeluqueriaController {
    @Autowired
    PeluqueriaRepository peluqueriaRepository;

    @GetMapping
    public List<Peluqueria> getPeluquerias() {
        return peluqueriaRepository.findByActivaTrue();
    }
}
