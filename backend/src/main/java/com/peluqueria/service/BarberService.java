package com.peluqueria.service;

import com.peluqueria.model.Appointment;
import com.peluqueria.model.Profesional;
import com.peluqueria.repository.AppointmentRepository;
import com.peluqueria.repository.ProfesionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BarberService {
    
    @Autowired
    private ProfesionalRepository profesionalRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    public Map<String, Object> getDashboardStats(Long profesionalId) {
        Map<String, Object> stats = new HashMap<>();
        
        Profesional profesional = profesionalRepository.findByUserId(profesionalId)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        
        LocalDateTime startOfWeek = LocalDate.now().atStartOfDay().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);
        
        // Estadísticas
        long todayAppointments = appointmentRepository.countTodayAppointmentsByProfesional(profesional.getId());
        long weeklyCompleted = appointmentRepository.countWeeklyCompletedAppointments(profesional.getId(), startOfWeek, endOfWeek);
        Double averageRating = appointmentRepository.getAverageRatingByProfesional(profesional.getId());
        Double weeklyEarnings = appointmentRepository.getWeeklyEarnings(profesional.getId(), startOfWeek, endOfWeek);
        
        stats.put("todayAppointments", todayAppointments);
        stats.put("weeklyCompleted", weeklyCompleted);
        stats.put("averageRating", averageRating != null ? averageRating : 0.0);
        stats.put("weeklyEarnings", weeklyEarnings != null ? weeklyEarnings : 0.0);
        stats.put("profesional", profesional);
        
        return stats;
    }
    
    public List<Appointment> getTodayAppointments(Long profesionalId) {
        Profesional profesional = profesionalRepository.findByUserId(profesionalId)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        
        return appointmentRepository.findTodayAppointmentsByProfesional(profesional.getId());
    }
    
    public List<Appointment> getUpcomingAppointments(Long profesionalId) {
        Profesional profesional = profesionalRepository.findByUserId(profesionalId)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        
        return appointmentRepository.findUpcomingAppointmentsByProfesional(profesional.getId());
    }
}
