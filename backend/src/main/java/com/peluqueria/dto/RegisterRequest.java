package com.peluqueria.dto;

import com.peluqueria.model.UserType;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    private String telefono;

    @NotNull
    private UserType tipoUsuario;

    // Campos específicos para profesionales
    private Long peluqueriaId;
    private String especialidad;
    private Integer experiencia;

    // Campos específicos para propietarios
    private String nombrePeluqueria;
    private String categoria;
    private String direccion;
    private String instagram;
    private String descripcion;
    private String horarioApertura;
    private String horarioCierre;

    @AssertTrue
    private boolean aceptoTerminos;
}
