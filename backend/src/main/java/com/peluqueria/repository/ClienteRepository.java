package com.peluqueria.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peluqueria.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    // Método para buscar clientes por nombre o apellido (ignore case)
    List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);
    
    // Método derivado - cuenta clientes donde createdAt está entre dos fechas
    long countByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Método alternativo usando JPQL
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.createdAt BETWEEN :inicio AND :fin")
    int countClientesEntreFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    // Método para contar por año y mes usando JPQL
    @Query("SELECT COUNT(c) FROM Cliente c WHERE YEAR(c.createdAt) = :year AND MONTH(c.createdAt) = :month")
    int countByYearAndMonth(@Param("year") int year, @Param("month") int month);
    
    // Método para obtener clientes entre fechas
    List<Cliente> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Métodos adicionales útiles para búsquedas
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
    
    List<Cliente> findByApellidoContainingIgnoreCase(String apellido);
    
    Optional<Cliente> findByTelefono(String telefono);
    
    Optional<Cliente> findByEmail(String email);
    
    // Método para buscar por nombre y apellido exactos (ignore case)
    List<Cliente> findByNombreIgnoreCaseAndApellidoIgnoreCase(String nombre, String apellido);
}