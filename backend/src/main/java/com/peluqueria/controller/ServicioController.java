package com.peluqueria.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peluqueria.dto.ServicioDTO;
import com.peluqueria.service.ServicioService;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*")
public class ServicioController {
    
    @Autowired
    private ServicioService servicioService;
    
    @GetMapping
    public ResponseEntity<List<ServicioDTO>> obtenerTodosServicios() {
        return ResponseEntity.ok(servicioService.obtenerTodosServicios());
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<ServicioDTO>> obtenerServiciosActivos() {
        return ResponseEntity.ok(servicioService.obtenerServiciosActivos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ServicioDTO> obtenerServicioPorId(@PathVariable Long id) {
        Optional<ServicioDTO> servicio = servicioService.obtenerServicioPorId(id);
        return servicio.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<ServicioDTO>> buscarServiciosPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(servicioService.buscarServiciosPorNombre(nombre));
    }
    
    @PostMapping
    public ResponseEntity<ServicioDTO> crearServicio(@RequestBody ServicioDTO servicioDTO) {
        return ResponseEntity.ok(servicioService.crearServicio(servicioDTO));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ServicioDTO> actualizarServicio(@PathVariable Long id, @RequestBody ServicioDTO servicioDTO) {
        Optional<ServicioDTO> servicioActualizado = servicioService.actualizarServicio(id, servicioDTO);
        return servicioActualizado.map(ResponseEntity::ok)
                                 .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarServicio(@PathVariable Long id) {
        if (servicioService.desactivarServicio(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
