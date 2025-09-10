package com.peluqueria.dto.request;

import lombok.Data;

@Data
public class RegistroRequest {
    private String tipoUsuario; // client, barber, owner
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String password;
    private String confirmPassword;
    
    // Campos específicos para barber/owner
    private Long peluqueriaId;
    private String especialidad;
    private Integer experiencia;
    private String nombrePeluqueria;
    private String direccion;
    private String descripcion;
    private String horarioApertura;
    private String horarioCierre;
    
    private Boolean aceptoTerminos;
} 

