package com.peluqueria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peluqueria.dto.ConfiguracionDTO;
import com.peluqueria.service.ConfiguracionService;

@RestController
@RequestMapping("/api/configuracion")
@CrossOrigin(origins = "*")
public class ConfiguracionController {
    
    @Autowired
    private ConfiguracionService configuracionService;
    
    @GetMapping
    public ResponseEntity<ConfiguracionDTO> obtenerConfiguracion() {
        return ResponseEntity.ok(configuracionService.obtenerConfiguracion());
    }
    
    @PutMapping
    public ResponseEntity<ConfiguracionDTO> actualizarConfiguracion(@RequestBody ConfiguracionDTO configuracionDTO) {
        return ResponseEntity.ok(configuracionService.actualizarConfiguracion(configuracionDTO));
    }
}
