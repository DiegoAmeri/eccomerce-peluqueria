package com.peluqueria.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peluqueria.model.Favorite;
import com.peluqueria.model.Peluqueria;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByClienteId(Long clienteId);
    Optional<Favorite> findByClienteIdAndPeluqueriaId(Long clienteId, Long peluqueriaId);
    boolean existsByClienteIdAndPeluqueriaId(Long clienteId, Long peluqueriaId);
    long countByClienteId(Long clienteId);
    
    @Query("SELECT f.peluqueria FROM Favorite f WHERE f.cliente.id = :clienteId")
    List<Peluqueria> findPeluqueriasFavoritasByClienteId(@Param("clienteId") Long clienteId);
}
