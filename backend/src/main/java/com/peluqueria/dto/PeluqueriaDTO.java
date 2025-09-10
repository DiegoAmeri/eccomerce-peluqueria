package com.peluqueria.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeluqueriaDTO {
    private Long id;
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
    private BigDecimal calificacionPromedio;
    private Integer totalCalificaciones;
    private Boolean activo;
    private Double distancia; // Para calculos de proximidad
    private List<ServicioDTO> servicios;
    private List<CalificacionDTO> calificaciones;
    private List<String> imagenes;
}