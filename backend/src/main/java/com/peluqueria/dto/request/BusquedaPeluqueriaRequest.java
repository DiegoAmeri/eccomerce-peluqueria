package com.peluqueria.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusquedaPeluqueriaRequest {
    private Double latitud;
    private Double longitud;
    private Double radioKm;
    private String nombre;
    private List<Long> servicios;
    private Integer calificacionMinima;
    private String ordenarPor;
}