package com.peluqueria.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfesionalDTO {
    private Long id;
    private Long userId;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private Long peluqueriaId;
    private String peluqueriaNombre;
    private String especialidad;
    private Integer anosExperiencia;
    private Boolean disponible;
    private Double ratingPromedio;
    private Integer totalServicios;
    private List<ServiceDTO> servicios;
}