package com.peluqueria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peluqueria.dto.CreateAppointmentRequest;
import com.peluqueria.model.Appointment;
import com.peluqueria.security.UserPrincipal;
import com.peluqueria.service.ClientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/client/appointments")
public class AppointmentController {
    
    @Autowired
    private ClientService clientService;
    
    @GetMapping
    public List<Appointment> getAppointments(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return clientService.getClientAppointments(userPrincipal.getId());
    }
    
    @GetMapping("/upcoming")
    public List<Appointment> getUpcomingAppointments(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return clientService.getUpcomingAppointments(userPrincipal.getId());
    }
    
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        @Valid @RequestBody CreateAppointmentRequest request) {
        Appointment appointment = clientService.createAppointment(userPrincipal.getId(), request);
        return ResponseEntity.ok(appointment);
    }
    
    @PostMapping("/{appointmentId}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        @PathVariable Long appointmentId) {
        Appointment appointment = clientService.cancelAppointment(userPrincipal.getId(), appointmentId);
        return ResponseEntity.ok(appointment);
    }
}
