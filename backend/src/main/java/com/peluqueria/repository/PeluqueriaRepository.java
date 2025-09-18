package com.peluqueria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peluqueria.model.Peluqueria;

@Repository
public interface PeluqueriaRepository extends JpaRepository<Peluqueria, Long> {
    List<Peluqueria> findByActivaTrue();
}
