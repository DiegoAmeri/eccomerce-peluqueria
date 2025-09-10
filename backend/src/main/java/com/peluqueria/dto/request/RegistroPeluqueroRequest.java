package com.peluqueria.dto.request;

import lombok.Data;

@Data
public class RegistroPeluqueroRequest {
    private String email;
    private String password;
    private String nombre;
    private String apellido;
    private String telefono;
    private String especialidad;
    private Integer experiencia;
    private String descripcion;
    private String fotoUrl;
    
    // Opcional: si quiere crear peluquería directamente
    private String nombrePeluqueria;
    private String direccionPeluqueria;
    private String telefonoPeluqueria;
}