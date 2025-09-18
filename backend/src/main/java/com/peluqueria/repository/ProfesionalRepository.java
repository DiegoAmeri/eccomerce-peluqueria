package com.peluqueria.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peluqueria.model.Profesional;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {
    
    Optional<Profesional> findByUserId(Long userId);
    
    List<Profesional> findByPeluqueriaId(Long peluqueriaId);
    
    List<Profesional> findByDisponibleTrue();
    
    List<Profesional> findByEspecialidadContainingIgnoreCase(String especialidad);
    
    @Query("SELECT p FROM Profesional p WHERE p.peluqueria.id = :peluqueriaId AND p.disponible = true")
    List<Profesional> findAvailableByPeluqueriaId(@Param("peluqueriaId") Long peluqueriaId);
    
    @Query("SELECT p FROM Profesional p WHERE p.user.email = :email")
    Optional<Profesional> findByUserEmail(@Param("email") String email);
    
    @Query("SELECT COUNT(p) FROM Profesional p WHERE p.peluqueria.id = :peluqueriaId")
    Long countByPeluqueriaId(@Param("peluqueriaId") Long peluqueriaId);
    
    boolean existsByUserId(Long userId);
}
