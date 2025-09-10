package com.peluqueria.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peluqueria.service.TurnoService;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private TurnoService turnoService;
    
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        
        try {
            // Implementar lógica real para obtener estadísticas
            Map<String, Object> estadisticas = new HashMap<>();
            
            // Ejemplo de datos - reemplazar con lógica real
            estadisticas.put("turnosHoy", turnoService.contarTurnosHoy());
            estadisticas.put("ingresosHoy", turnoService.calcularIngresosHoy());
            estadisticas.put("clientesNuevos", turnoService.contarClientesNuevosEsteMes());
            estadisticas.put("servicioPopular", turnoService.obtenerServicioPopular());
            estadisticas.put("turnosPendientes", turnoService.contarTurnosPendientes());
            
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            // En caso de error, devolver datos por defecto
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("turnosHoy", 0);
            estadisticas.put("ingresosHoy", 0.0);
            estadisticas.put("clientesNuevos", 0);
            estadisticas.put("servicioPopular", "No disponible");
            estadisticas.put("turnosPendientes", 0);
            
            return ResponseEntity.ok(estadisticas);
        }
    }
}