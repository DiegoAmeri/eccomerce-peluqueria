package com.peluqueria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeluqueroDTO {
    private Long id;
    private Long usuarioId;
    private String nombre;
    private String apellido;
    private String telefono;
    private String especialidad;
    private Integer experiencia;
    private String descripcion;
    private String fotoUrl;
    private Boolean activo;
}