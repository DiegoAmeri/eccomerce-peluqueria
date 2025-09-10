package com.peluqueria.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalificacionDTO {
    private Long id;
    private Long peluqueriaId;
    private Long clienteId;
    private String clienteNombre;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime createdAt;
}