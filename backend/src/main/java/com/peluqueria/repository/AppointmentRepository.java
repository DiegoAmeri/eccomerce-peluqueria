package com.peluqueria.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peluqueria.model.Appointment;
import com.peluqueria.model.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByClienteId(Long clienteId);
    List<Appointment> findByClienteIdAndEstado(Long clienteId, AppointmentStatus estado);
    List<Appointment> findByProfesionalId(Long profesionalId);
    List<Appointment> findByPeluqueriaId(Long peluqueriaId);
    
    @Query("SELECT a FROM Appointment a WHERE a.cliente.id = :clienteId AND a.fechaHora >= :startDate ORDER BY a.fechaHora ASC")
    List<Appointment> findUpcomingByClienteId(@Param("clienteId") Long clienteId, 
                                             @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.cliente.id = :clienteId AND a.estado = 'COMPLETADO'")
    Long countCompletedByClienteId(@Param("clienteId") Long clienteId);
    
    @Query("SELECT AVG(a.calificacion) FROM Appointment a WHERE a.cliente.id = :clienteId AND a.calificacion IS NOT NULL")
    Double findAverageRatingByClienteId(@Param("clienteId") Long clienteId);
    
    @Query("SELECT a FROM Appointment a WHERE a.profesional.id = :profesionalId AND a.fechaHora BETWEEN :start AND :end")
    List<Appointment> findByProfesionalIdAndDateRange(@Param("profesionalId") Long profesionalId,
                                                     @Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);

    // Profesional

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.profesional.id = :profesionalId " +
       "AND DATE(a.fechaHora) = CURRENT_DATE")
    Long countTodayAppointmentsByProfesional(@Param("profesionalId") Long profesionalId);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.profesional.id = :profesionalId " +
        "AND a.estado = 'COMPLETADO' AND a.fechaHora BETWEEN :start AND :end")
    Long countWeeklyCompletedAppointments(@Param("profesionalId") Long profesionalId,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query("SELECT AVG(a.calificacion) FROM Appointment a WHERE a.profesional.id = :profesionalId " +
        "AND a.calificacion IS NOT NULL")
    Double getAverageRatingByProfesional(@Param("profesionalId") Long profesionalId);

    @Query("SELECT SUM(a.precio) FROM Appointment a WHERE a.profesional.id = :profesionalId " +
        "AND a.estado = 'COMPLETADO' AND a.fechaHora BETWEEN :start AND :end")
    Double getWeeklyEarnings(@Param("profesionalId") Long profesionalId,
                            @Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.profesional.id = :profesionalId " +
        "AND DATE(a.fechaHora) = CURRENT_DATE ORDER BY a.fechaHora")
    List<Appointment> findTodayAppointmentsByProfesional(@Param("profesionalId") Long profesionalId);

    @Query("SELECT a FROM Appointment a WHERE a.profesional.id = :profesionalId " +
        "AND a.fechaHora > CURRENT_TIMESTAMP ORDER BY a.fechaHora")
    List<Appointment> findUpcomingAppointmentsByProfesional(@Param("profesionalId") Long profesionalId);
}