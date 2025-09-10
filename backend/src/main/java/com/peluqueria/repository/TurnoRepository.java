package com.peluqueria.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peluqueria.model.Turno;
import com.peluqueria.model.enums.EstadoTurno;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    List<Turno> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Turno> findByClienteId(Long clienteId);
    List<Turno> findByEstado(EstadoTurno estado);
    
    @Query("SELECT t FROM Turno t WHERE CAST(t.fechaHora AS localdate) = CURRENT_DATE ORDER BY t.fechaHora")
    List<Turno> findTurnosHoy();
    
    @Query("SELECT t FROM Turno t WHERE t.fechaHora >= :inicio AND t.fechaHora <= :fin AND t.estado = 'COMPLETADO'")
    List<Turno> findTurnosCompletadosEntreFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT t FROM Turno t WHERE t.fechaHora BETWEEN :start AND :end")
    List<Turno> findTurnosEntreFechas(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    boolean existsByFechaHoraAndClienteId(LocalDateTime fechaHora, Long clienteId);
    
    @Query("SELECT t FROM Turno t WHERE t.fechaHora BETWEEN :startOfDay AND :endOfDay ORDER BY t.fechaHora")
    List<Turno> findTurnosHoyAlternativo(@Param("startOfDay") LocalDateTime startOfDay, 
                                        @Param("endOfDay") LocalDateTime endOfDay);

                                        int countByFechaHoraBetweenAndEstadoNot(LocalDateTime inicio, LocalDateTime fin, String estado);
    
    List<Turno> findByFechaHoraBetweenAndEstadoNot(LocalDateTime inicio, LocalDateTime fin, String estado);
    
    int countByFechaHoraAfterAndEstado(LocalDateTime fechaHora, String estado);
    
    
    @Query("SELECT COUNT(t) FROM Turno t WHERE t.fechaHora BETWEEN :inicio AND :fin")
    int countByFechaHoraBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COUNT(t) FROM Turno t WHERE t.estado = :estado")
    int countByEstado(@Param("estado") EstadoTurno estado);
}
