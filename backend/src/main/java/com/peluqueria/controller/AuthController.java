package com.peluqueria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.peluqueria.dto.request.JwtResponse;
import com.peluqueria.dto.request.LoginRequest;
import com.peluqueria.dto.request.RegistroRequest;
import com.peluqueria.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
    
    @PostMapping("/registro")
    public ResponseEntity<JwtResponse> registro(@RequestBody RegistroRequest registroRequest) {
        return ResponseEntity.ok(authService.registro(registroRequest));
    }
    
    @PostMapping("/solicitar-union-peluqueria/{peluqueriaId}")
    public ResponseEntity<Void> solicitarUnionPeluqueria(
            @PathVariable Long peluqueriaId,
            @RequestHeader("Authorization") String token) {
        authService.solicitarUnionPeluqueria(peluqueriaId, token);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/mis-peluquerias")
    public ResponseEntity<?> obtenerMisPeluquerias(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.obtenerMisPeluquerias(token));
    }
}