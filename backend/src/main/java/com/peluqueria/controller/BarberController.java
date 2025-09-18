package com.peluqueria.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peluqueria.security.UserPrincipal;
import com.peluqueria.service.BarberService;

@RestController
@RequestMapping("/api/barber")
public class BarberController {
    
    @Autowired
    private BarberService barberService;
    
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Map<String, Object> stats = barberService.getDashboardStats(userPrincipal.getId());
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/appointments/today")
    public ResponseEntity<?> getTodayAppointments(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(barberService.getTodayAppointments(userPrincipal.getId()));
    }
    
    @GetMapping("/appointments/upcoming")
    public ResponseEntity<?> getUpcomingAppointments(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(barberService.getUpcomingAppointments(userPrincipal.getId()));
    }
}