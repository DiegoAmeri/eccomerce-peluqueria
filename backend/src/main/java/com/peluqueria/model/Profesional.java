package com.peluqueria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "profesionales")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
public class Profesional extends User {
    @ManyToOne
    @JoinColumn(name = "peluqueria_id")
    private Peluqueria peluqueria;
    
    private String especialidad;
    
    private Integer anosExperiencia;
    
    private boolean disponible = true;
    
    public Profesional() {
        super();
    }
}
