package com.peluqueria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peluqueria.model.Propietario;

@Repository
public interface PropietarioRepository extends JpaRepository<Propietario, Long> {
}
