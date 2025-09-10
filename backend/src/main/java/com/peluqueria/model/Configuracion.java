package com.peluqueria.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "configuraciones")
@Data
public class Configuracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombrePeluqueria;
    
    @Column(nullable = false)
    private String horaApertura; // Formato HH:mm
    
    @Column(nullable = false)
    private String horaCierre; // Formato HH:mm
    
    @Column(nullable = false)
    private Integer intervaloEntreTurnos; // en minutos
    
    private String numeroWhatsapp;
    private String mensajeConfirmacion;
    private String mensajeRecordatorio;
    

}
