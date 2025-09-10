package com.peluqueria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracionDTO {
    private Long id;
    private String nombrePeluqueria;
    private String horaApertura;
    private String horaCierre;
    private Integer intervaloEntreTurnos;
    private String numeroWhatsapp;
    private String mensajeConfirmacion;
    private String mensajeRecordatorio;
    
}
