package com.peluqueria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "propietarios")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
public class Propietario extends User {
    // Los propietarios pueden tener campos específicos
    // como información de negocio adicional
    
    public Propietario() {
        super();
    }
}
