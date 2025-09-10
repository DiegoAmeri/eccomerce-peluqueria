package com.peluqueria.model;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "peluquerias")
@Data
public class Peluqueria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    private String direccion;
    private String telefono;
    private String email;
    private BigDecimal latitud;
    private BigDecimal longitud;
    
    @Column(name = "horario_apertura")
    private LocalTime horarioApertura;
    
    @Column(name = "horario_cierre")
    private LocalTime horarioCierre;
    
    private String descripcion;
    
    @Column(name = "imagen_url")
    private String imagenUrl;
    
    @Column(name = "calificacion_promedio", precision = 3, scale = 2)
    private BigDecimal calificacionPromedio = BigDecimal.ZERO;
    
    @Column(name = "total_calificaciones")
    private Integer totalCalificaciones = 0;
    
    private Boolean activo = true;
    
    @OneToMany(mappedBy = "peluqueria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PeluqueriaServicio> servicios = new ArrayList<>();
    
    @OneToMany(mappedBy = "peluqueria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Calificacion> calificaciones = new ArrayList<>();
    
    @OneToMany(mappedBy = "peluqueria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PeluqueriaImagen> imagenes = new ArrayList<>();
    
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Peluquero owner;

    @OneToMany(mappedBy = "peluqueria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PeluqueriaPeluquero> empleados = new ArrayList<>();
}