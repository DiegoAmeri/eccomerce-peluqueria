package com.peluqueria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.peluqueria.dto.PeluqueroDTO;
import com.peluqueria.service.PeluqueroService;

import java.util.List;

@RestController
@RequestMapping("/api/peluqueros")
@CrossOrigin(origins = "*")
public class PeluqueroController {
    
    @Autowired
    private PeluqueroService peluqueroService;
    
    @GetMapping
    public ResponseEntity<List<PeluqueroDTO>> obtenerTodosPeluqueros() {
        return ResponseEntity.ok(peluqueroService.obtenerTodosPeluqueros());
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<PeluqueroDTO>> obtenerPeluquerosActivos() {
        return ResponseEntity.ok(peluqueroService.obtenerPeluquerosActivos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PeluqueroDTO> obtenerPeluqueroPorId(@PathVariable Long id) {
        return peluqueroService.obtenerPeluqueroPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<PeluqueroDTO> obtenerPeluqueroPorUsuarioId(@PathVariable Long usuarioId) {
        return peluqueroService.obtenerPeluqueroPorUsuarioId(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PeluqueroDTO> actualizarPeluquero(
            @PathVariable Long id,
            @RequestBody PeluqueroDTO peluqueroDTO) {
        return peluqueroService.actualizarPeluquero(id, peluqueroDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarPeluquero(@PathVariable Long id) {
        if (peluqueroService.desactivarPeluquero(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}