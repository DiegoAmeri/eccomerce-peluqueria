package com.peluqueria.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peluqueria.security.UserPrincipal;
import com.peluqueria.service.ClientService;

@RestController
@RequestMapping("/api/client/dashboard")
public class ClientDashboardController {
    
    @Autowired
    private ClientService clientService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Map<String, Object> dashboardData = clientService.getDashboardStats(userPrincipal.getId());
        return ResponseEntity.ok(dashboardData);
    }
}
