package com.peluqueria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peluqueria.model.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByPeluqueriaId(Long peluqueriaId);
    List<Service> findByProfesionalId(Long profesionalId);
    List<Service> findByPeluqueriaIdAndActivoTrue(Long peluqueriaId);
    
    @Query("SELECT s FROM Service s WHERE s.peluqueria.id = :peluqueriaId AND LOWER(s.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Service> searchByPeluqueriaIdAndName(@Param("peluqueriaId") Long peluqueriaId, 
                                             @Param("searchTerm") String searchTerm);
}
