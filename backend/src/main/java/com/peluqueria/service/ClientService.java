package com.peluqueria.service;

import com.peluqueria.dto.CreateAppointmentRequest;
import com.peluqueria.model.*;
import com.peluqueria.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClientService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private PeluqueriaRepository peluqueriaRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    // Métodos para Favoritos
    public List<Peluqueria> getFavorites(Long clienteId) {
        return favoriteRepository.findPeluqueriasFavoritasByClienteId(clienteId);
    }
    
    @Transactional
    public Favorite addFavorite(Long clienteId, Long peluqueriaId) {
        // Verificar si ya existe
        if (favoriteRepository.existsByClienteIdAndPeluqueriaId(clienteId, peluqueriaId)) {
            throw new RuntimeException("La peluquería ya está en favoritos");
        }
        
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        Peluqueria peluqueria = peluqueriaRepository.findById(peluqueriaId)
                .orElseThrow(() -> new RuntimeException("Peluquería no encontrada"));
        
        Favorite favorite = new Favorite();
        favorite.setCliente(cliente);
        favorite.setPeluqueria(peluqueria);
        
        return favoriteRepository.save(favorite);
    }
    
    @Transactional
    public void removeFavorite(Long clienteId, Long peluqueriaId) {
        Favorite favorite = favoriteRepository.findByClienteIdAndPeluqueriaId(clienteId, peluqueriaId)
                .orElseThrow(() -> new RuntimeException("Favorito no encontrado"));
        
        favoriteRepository.delete(favorite);
    }
    
    public boolean isFavorite(Long clienteId, Long peluqueriaId) {
        return favoriteRepository.existsByClienteIdAndPeluqueriaId(clienteId, peluqueriaId);
    }
    
    // Métodos para Turnos/Citas
    public List<Appointment> getClientAppointments(Long clienteId) {
        return appointmentRepository.findByClienteId(clienteId);
    }
    
    public List<Appointment> getUpcomingAppointments(Long clienteId) {
        return appointmentRepository.findUpcomingByClienteId(clienteId, LocalDateTime.now());
    }
    
    public Map<String, Object> getDashboardStats(Long clienteId) {
        Long upcomingCount = (long) getUpcomingAppointments(clienteId).size();
        Long completedCount = appointmentRepository.countCompletedByClienteId(clienteId);
        Double averageRating = appointmentRepository.findAverageRatingByClienteId(clienteId);
        Long favoritesCount = favoriteRepository.countByClienteId(clienteId);
        
        return Map.of(
            "upcomingAppointments", upcomingCount,
            "completedAppointments", completedCount,
            "averageRating", averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0,
            "favoriteSalons", favoritesCount,
            "appointments", getUpcomingAppointments(clienteId)
        );
    }
    
    @Transactional
    public Appointment createAppointment(Long clienteId, CreateAppointmentRequest request) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        Profesional profesional = null;
        if (request.getProfesionalId() != null) {
            // Buscar profesional si se especificó
        }
        
        Peluqueria peluqueria = peluqueriaRepository.findById(request.getPeluqueriaId())
                .orElseThrow(() -> new RuntimeException("Peluquería no encontrada"));
        
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        
        // Validar disponibilidad
        if (!isTimeSlotAvailable(request.getProfesionalId(), request.getFechaHora(), service.getDuracionMinutos())) {
            throw new RuntimeException("El horario no está disponible");
        }
        
        Appointment appointment = new Appointment();
        appointment.setCliente(cliente);
        appointment.setProfesional(profesional);
        appointment.setPeluqueria(peluqueria);
        appointment.setService(service);
        appointment.setFechaHora(request.getFechaHora());
        appointment.setDuracionMinutos(service.getDuracionMinutos());
        appointment.setPrecio(service.getPrecio());
        appointment.setEstado(AppointmentStatus.PENDIENTE);
        appointment.setNotas(request.getNotas());
        
        return appointmentRepository.save(appointment);
    }
    
    @Transactional
    public Appointment cancelAppointment(Long clienteId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        
        if (!appointment.getCliente().getId().equals(clienteId)) {
            throw new RuntimeException("No tienes permiso para cancelar este turno");
        }
        
        if (appointment.getEstado() != AppointmentStatus.PENDIENTE && 
            appointment.getEstado() != AppointmentStatus.CONFIRMADO) {
            throw new RuntimeException("No se puede cancelar el turno en su estado actual");
        }
        
        appointment.setEstado(AppointmentStatus.CANCELADO);
        return appointmentRepository.save(appointment);
    }
    
    private boolean isTimeSlotAvailable(Long profesionalId, LocalDateTime startTime, Integer duration) {
        LocalDateTime endTime = startTime.plusMinutes(duration);
        
        // Verificar si hay turnos que se superpongan
        List<Appointment> conflictingAppointments = appointmentRepository
                .findByProfesionalIdAndDateRange(profesionalId, startTime, endTime);
        
        return conflictingAppointments.isEmpty();
    }
    
    // Métodos para búsqueda y filtrado de peluquerías
    public List<Peluqueria> searchSalons(String searchTerm, String category, Integer minRating, 
                                       String priceRange, List<String> services, Boolean openNow) {
        // Implementar lógica de búsqueda y filtrado
        return peluqueriaRepository.findByActivaTrue().stream()
                .filter(peluqueria -> matchesFilters(peluqueria, searchTerm, category, minRating, priceRange, services, openNow))
                .collect(Collectors.toList());
    }
    
    private boolean matchesFilters(Peluqueria peluqueria, String searchTerm, String category, 
                                 Integer minRating, String priceRange, List<String> services, Boolean openNow) {
        // Implementar lógica de filtrado
        return true; // Placeholder
    }
}
