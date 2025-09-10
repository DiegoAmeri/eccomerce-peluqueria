package com.peluqueria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.peluqueria.dto.PeluqueriaDTO;
import com.peluqueria.service.PeluqueriaService;

import java.util.List;

@RestController
@RequestMapping("/api/peluquerias")
@CrossOrigin(origins = "*")
public class PeluqueriaController {
    
    @Autowired
    private PeluqueriaService peluqueriaService;
    
    @GetMapping
    public ResponseEntity<List<PeluqueriaDTO>> obtenerTodasPeluquerias() {
        return ResponseEntity.ok(peluqueriaService.obtenerTodasPeluquerias());
    }
    
    @GetMapping("/activas")
    public ResponseEntity<List<PeluqueriaDTO>> obtenerPeluqueriasActivas() {
        return ResponseEntity.ok(peluqueriaService.obtenerPeluqueriasActivas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PeluqueriaDTO> obtenerPeluqueriaPorId(@PathVariable Long id) {
        return peluqueriaService.obtenerPeluqueriaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<PeluqueriaDTO> crearPeluqueria(@RequestBody PeluqueriaDTO peluqueriaDTO) {
        return ResponseEntity.ok(peluqueriaService.crearPeluqueria(peluqueriaDTO));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PeluqueriaDTO> actualizarPeluqueria(
            @PathVariable Long id,
            @RequestBody PeluqueriaDTO peluqueriaDTO) {
        return peluqueriaService.actualizarPeluqueria(id, peluqueriaDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarPeluqueria(@PathVariable Long id) {
        if (peluqueriaService.desactivarPeluqueria(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // Endpoints para gestión de empleados
    @GetMapping("/{id}/empleados")
    public ResponseEntity<?> obtenerEmpleados(@PathVariable Long id) {
        return ResponseEntity.ok(peluqueriaService.obtenerEmpleados(id));
    }
    
    @PostMapping("/{id}/empleados/{peluqueroId}")
    public ResponseEntity<?> agregarEmpleado(
            @PathVariable Long id,
            @PathVariable Long peluqueroId,
            @RequestParam String rol) {
        return ResponseEntity.ok(peluqueriaService.agregarEmpleado(id, peluqueroId, rol));
    }
    
    @PutMapping("/{id}/empleados/{peluqueroId}")
    public ResponseEntity<?> actualizarRolEmpleado(
            @PathVariable Long id,
            @PathVariable Long peluqueroId,
            @RequestParam String rol) {
        return ResponseEntity.ok(peluqueriaService.actualizarRolEmpleado(id, peluqueroId, rol));
    }
    
    @DeleteMapping("/{id}/empleados/{peluqueroId}")
    public ResponseEntity<Void> removerEmpleado(
            @PathVariable Long id,
            @PathVariable Long peluqueroId) {
        if (peluqueriaService.removerEmpleado(id, peluqueroId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}