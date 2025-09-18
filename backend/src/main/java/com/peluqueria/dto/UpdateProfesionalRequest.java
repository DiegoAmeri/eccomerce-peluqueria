package com.peluqueria.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfesionalRequest {
    private String especialidad;
    private Integer anosExperiencia;
    private Boolean disponible;
    private Long peluqueriaId;
}
