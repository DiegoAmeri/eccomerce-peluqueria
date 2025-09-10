package com.peluqueria.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.peluqueria.model.PeluqueriaPeluquero;

public interface PeluqueriaPeluqueroRepository extends JpaRepository<PeluqueriaPeluquero, Long> {
    
    List<PeluqueriaPeluquero> findByPeluqueroId(Long peluqueroId);
    List<PeluqueriaPeluquero> findByPeluqueriaId(Long peluqueriaId);
    
    @Query("SELECT pp FROM PeluqueriaPeluquero pp WHERE pp.peluqueria.id = :peluqueriaId AND pp.peluquero.id = :peluqueroId")
    Optional<PeluqueriaPeluquero> findByPeluqueriaAndPeluquero(Long peluqueriaId, Long peluqueroId);
    
    boolean existsByPeluqueriaIdAndPeluqueroId(Long peluqueriaId, Long peluqueroId);
}