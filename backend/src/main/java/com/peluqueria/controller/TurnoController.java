package com.peluqueria.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peluqueria.dto.TurnoDTO;
import com.peluqueria.dto.request.NuevoTurnoRequest;
import com.peluqueria.service.TurnoService;

@RestController
@RequestMapping("/api/turnos")
@CrossOrigin(origins = "*")
public class TurnoController {
    
    @Autowired
    private TurnoService turnoService;
    
    @GetMapping
    public ResponseEntity<List<TurnoDTO>> obtenerTodosTurnos() {
        return ResponseEntity.ok(turnoService.obtenerTodosTurnos());
    }
    
    @GetMapping("/hoy")
    public ResponseEntity<List<TurnoDTO>> obtenerTurnosHoy() {
        return ResponseEntity.ok(turnoService.obtenerTurnosHoy());
    }
    
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<TurnoDTO>> obtenerTurnosPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(turnoService.obtenerTurnosPorFecha(fecha));
    }
    
    @GetMapping("/rango")
    public ResponseEntity<List<TurnoDTO>> obtenerTurnosEntreFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(turnoService.obtenerTurnosEntreFechas(inicio, fin));
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<TurnoDTO>> obtenerTurnosPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(turnoService.obtenerTurnosPorCliente(clienteId));
    }
    
    @GetMapping("/horarios-disponibles")
    public ResponseEntity<List<String>> obtenerHorariosDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam Long servicioId) {
        return ResponseEntity.ok(turnoService.obtenerHorariosDisponibles(fecha, servicioId));
    }
    
    @PostMapping
    public ResponseEntity<TurnoDTO> crearTurno(@RequestBody NuevoTurnoRequest nuevoTurnoRequest) {
        try {
            return ResponseEntity.ok(turnoService.crearTurno(nuevoTurnoRequest));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarTurno(@PathVariable Long id) {
        if (turnoService.cancelarTurno(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Void> confirmarTurno(@PathVariable Long id) {
        if (turnoService.confirmarTurno(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/completar")
    public ResponseEntity<Void> completarTurno(@PathVariable Long id) {
        if (turnoService.completarTurno(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}