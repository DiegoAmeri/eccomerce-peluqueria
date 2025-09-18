package com.peluqueria.dto;

import com.peluqueria.model.UserType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private UserType tipoUsuario;

    public AuthResponse(String token, Long id, String nombre, String apellido, String email, UserType tipoUsuario) {
        this.token = token;
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
    }
}