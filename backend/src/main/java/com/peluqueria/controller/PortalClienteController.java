package com.peluqueria.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.peluqueria.dto.PeluqueriaDTO;
import com.peluqueria.dto.ServicioDTO;
import com.peluqueria.service.PeluqueriaService;
import com.peluqueria.service.ServicioService;

@RestController
@RequestMapping("/api/portal")
@CrossOrigin(origins = "*")
public class PortalClienteController {
    
    @Autowired
    private PeluqueriaService peluqueriaService;
    
    @Autowired
    private ServicioService servicioService;
    
    @GetMapping("/peluquerias")
    public ResponseEntity<List<PeluqueriaDTO>> obtenerPeluqueriasCercanas(
            @RequestParam(required = false) Double latitud,
            @RequestParam(required = false) Double longitud,
            @RequestParam(required = false) Double radioKm) {
        return ResponseEntity.ok(peluqueriaService.obtenerPeluqueriasCercanas(latitud, longitud, radioKm));
    }
    
    @GetMapping("/peluquerias/{id}")
    public ResponseEntity<PeluqueriaDTO> obtenerPeluqueriaPorId(@PathVariable Long id) {
        return peluqueriaService.obtenerPeluqueriaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/servicios")
    public ResponseEntity<List<ServicioDTO>> obtenerServiciosActivos() {
        return ResponseEntity.ok(servicioService.obtenerServiciosActivos());
    }
    
    @GetMapping("/peluquerias/buscar")
    public ResponseEntity<List<PeluqueriaDTO>> buscarPeluquerias(
            @RequestParam String nombre,
            @RequestParam(required = false) List<Long> servicios) {
        return ResponseEntity.ok(peluqueriaService.buscarPeluquerias(nombre, servicios));
    }
}