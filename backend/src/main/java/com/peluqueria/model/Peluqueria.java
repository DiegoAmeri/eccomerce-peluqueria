package com.peluqueria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "peluquerias")
@Getter
@Setter
public class Peluqueria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String nombre;

    @NotBlank
    private String direccion;

    @Enumerated(EnumType.STRING)
    private CategoriaPeluqueria categoria;

    private String descripcion;

    private String instagram;

    private LocalTime horarioApertura;

    private LocalTime horarioCierre;

    private boolean activa = true;

    @ManyToOne
    @JoinColumn(name = "propietario_id")
    private Propietario propietario;
}