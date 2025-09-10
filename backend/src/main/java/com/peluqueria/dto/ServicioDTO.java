package com.peluqueria.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicioDTO {
    private Long id;
    private String nombre;
    private Integer duracionMinutos;
    private BigDecimal precio;
    private String descripcion;
    private Boolean activo;
}
