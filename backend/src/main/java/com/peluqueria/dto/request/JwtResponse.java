package com.peluqueria.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private Long id;
    private String email;
    private String rol;
    private String tipoToken = "Bearer";
    
    public JwtResponse(String token, Long id, String email, String rol) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.rol = rol;
    }
}