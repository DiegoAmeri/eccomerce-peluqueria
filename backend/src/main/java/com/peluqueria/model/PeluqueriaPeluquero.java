package com.peluqueria.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "peluqueria_peluqueros")
@Data
public class PeluqueriaPeluquero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peluqueria_id", nullable = false)
    private Peluqueria peluqueria;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peluquero_id", nullable = false)
    private Peluquero peluquero;
    
    @Column(nullable = false)
    private String rol; // OWNER, EMPLOYEE
    
    private Boolean activo = true;
    
    @Column(name = "fecha_union")
    private LocalDateTime fechaUnion;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        fechaUnion = LocalDateTime.now();
        createdAt = LocalDateTime.now();
    }
}