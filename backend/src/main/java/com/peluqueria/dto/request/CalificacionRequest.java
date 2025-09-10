package com.peluqueria.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalificacionRequest {
    private Long peluqueriaId;
    private Long clienteId;
    private Integer calificacion;
    private String comentario;
}