package com.peluqueria.dto.request;

import java.math.BigDecimal;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeluqueriaRequest {
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;
    private String descripcion;
    private String imagenUrl;
    private Boolean activo;
}