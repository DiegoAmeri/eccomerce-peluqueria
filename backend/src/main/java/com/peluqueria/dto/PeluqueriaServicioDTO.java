package com.peluqueria.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeluqueriaServicioDTO {
    private Long id;
    private Long peluqueriaId;
    private String peluqueriaNombre;
    private ServicioDTO servicio;
    private BigDecimal precio;
    private Integer duracionMinutos;
    private Boolean activo;
}