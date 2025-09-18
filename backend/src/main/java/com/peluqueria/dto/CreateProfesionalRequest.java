package com.peluqueria.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProfesionalRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;
    private Long peluqueriaId;
    private String especialidad;
    private Integer anosExperiencia;
}
