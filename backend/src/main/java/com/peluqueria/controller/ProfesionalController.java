package com.peluqueria.controller;

import com.peluqueria.dto.CreateProfesionalRequest;
import com.peluqueria.dto.ProfesionalDTO;
import com.peluqueria.dto.UpdateProfesionalRequest;
import com.peluqueria.service.ProfesionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profesionals")
public class ProfesionalController {
    
    @Autowired
    private ProfesionalService profesionalService;
    
    @GetMapping
    public ResponseEntity<List<ProfesionalDTO>> getAllProfesionals() {
        List<ProfesionalDTO> profesionals = profesionalService.getAllProfesionals();
        return ResponseEntity.ok(profesionals);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProfesionalDTO> getProfesionalById(@PathVariable Long id) {
        ProfesionalDTO profesional = profesionalService.getProfesionalById(id);
        return ResponseEntity.ok(profesional);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ProfesionalDTO> getProfesionalByUserId(@PathVariable Long userId) {
        ProfesionalDTO profesional = profesionalService.getProfesionalByUserId(userId);
        return ResponseEntity.ok(profesional);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<ProfesionalDTO> createProfesional(
            @Valid @RequestBody CreateProfesionalRequest request) {
        ProfesionalDTO profesional = profesionalService.createProfesional(request);
        return ResponseEntity.ok(profesional);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROFESIONAL') or hasRole('PROPIETARIO')")
    public ResponseEntity<ProfesionalDTO> updateProfesional(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfesionalRequest request) {
        ProfesionalDTO profesional = profesionalService.updateProfesional(id, request);
        return ResponseEntity.ok(profesional);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<?> deleteProfesional(@PathVariable Long id) {
        profesionalService.deleteProfesional(id);
        return ResponseEntity.ok(Map.of("message", "Profesional eliminado correctamente"));
    }
    
    @GetMapping("/peluqueria/{peluqueriaId}")
    public ResponseEntity<List<ProfesionalDTO>> getProfesionalsByPeluqueria(
            @PathVariable Long peluqueriaId) {
        List<ProfesionalDTO> profesionals = profesionalService.getProfesionalsByPeluqueria(peluqueriaId);
        return ResponseEntity.ok(profesionals);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<ProfesionalDTO>> getAvailableProfesionals() {
        List<ProfesionalDTO> profesionals = profesionalService.getAvailableProfesionals();
        return ResponseEntity.ok(profesionals);
    }
    
    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('PROFESIONAL') or hasRole('PROPIETARIO')")
    public ResponseEntity<ProfesionalDTO> updateAvailability(
            @PathVariable Long id,
            @RequestParam Boolean disponible) {
        UpdateProfesionalRequest request = new UpdateProfesionalRequest();
        request.setDisponible(disponible);
        
        ProfesionalDTO profesional = profesionalService.updateProfesional(id, request);
        return ResponseEntity.ok(profesional);
    }
}
