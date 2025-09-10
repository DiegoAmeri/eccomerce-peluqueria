package com.peluqueria.repository;

import com.peluqueria.model.Peluqueria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeluqueriaRepository extends JpaRepository<Peluqueria, Long> {
    
    List<Peluqueria> findByActivoTrue();
    
    List<Peluqueria> findByNombreContainingIgnoreCase(String nombre);
    
    Optional<Peluqueria> findByTelefono(String telefono);
    
    @Query("SELECT p FROM Peluqueria p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Peluqueria> buscarPorNombre(@Param("nombre") String nombre);
    
    @Query("SELECT p FROM Peluqueria p WHERE p.activo = true ORDER BY p.calificacionPromedio DESC")
    List<Peluqueria> findTopRated();
    
    @Query("SELECT COUNT(p) FROM Peluqueria p WHERE p.activo = true")
    long countActive();
    
    // Para búsqueda por servicios (necesita join con peluqueria_servicios)
    @Query("SELECT DISTINCT p FROM Peluqueria p " +
           "JOIN p.servicios ps " +
           "WHERE ps.servicio.id IN :serviciosIds AND p.activo = true")
    List<Peluqueria> findByServiciosIds(@Param("serviciosIds") List<Long> serviciosIds);
}