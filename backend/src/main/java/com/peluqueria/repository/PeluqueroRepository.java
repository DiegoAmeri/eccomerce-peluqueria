package com.peluqueria.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.peluqueria.model.Peluquero;

public interface PeluqueroRepository extends JpaRepository<Peluquero, Long> {
    Optional<Peluquero> findByUsuarioEmail(String email);
    List<Peluquero> findByActivoTrue();
    
    @Query("SELECT p FROM Peluquero p WHERE p.usuario.id = :usuarioId")
    Optional<Peluquero> findByUsuarioId(Long usuarioId);
}