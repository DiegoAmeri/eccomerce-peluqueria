package com.peluqueria.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "peluqueros")
@Data
public class Peluquero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String apellido;
    
    private String telefono;
    private String especialidad;
    private Integer experiencia = 0;
    private String descripcion;
    
    @Column(name = "foto_url")
    private String fotoUrl;
    
    private Boolean activo = true;
    
    @OneToMany(mappedBy = "peluquero", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PeluqueriaPeluquero> peluquerias = new ArrayList<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}