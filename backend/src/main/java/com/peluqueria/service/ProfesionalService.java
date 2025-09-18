package com.peluqueria.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peluqueria.dto.CreateProfesionalRequest;
import com.peluqueria.dto.ProfesionalDTO;
import com.peluqueria.dto.UpdateProfesionalRequest;
import com.peluqueria.model.Profesional;
import com.peluqueria.model.User;
import com.peluqueria.model.UserType;
import com.peluqueria.repository.ProfesionalRepository;
import com.peluqueria.repository.UserRepository;

@Service
public class ProfesionalService {
    
    @Autowired
    private ProfesionalRepository profesionalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<ProfesionalDTO> getAllProfesionals() {
        return profesionalRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ProfesionalDTO getProfesionalById(Long id) {
        return profesionalRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
    }
    
    public ProfesionalDTO getProfesionalByUserId(Long userId) {
        return profesionalRepository.findByUserId(userId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
    }
    
    @Transactional
    public ProfesionalDTO createProfesional(CreateProfesionalRequest request) {
        // Verificar si el email ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Crear usuario
        User user = new User();
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setTelefono(request.getTelefono());
        user.setTipoUsuario(UserType.PROFESIONAL);
        user.setActivo(true);
        
        User savedUser = userRepository.save(user);
        
        // Crear profesional
        Profesional profesional = new Profesional();
        profesional.setUser(savedUser);
        profesional.setEspecialidad(request.getEspecialidad());
        profesional.setAnosExperiencia(request.getAnosExperiencia());
        profesional.setDisponible(true);
        
        Profesional savedProfesional = profesionalRepository.save(profesional);
        
        return convertToDTO(savedProfesional);
    }
    
    @Transactional
    public ProfesionalDTO updateProfesional(Long id, UpdateProfesionalRequest request) {
        Profesional profesional = profesionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        
        if (request.getEspecialidad() != null) {
            profesional.setEspecialidad(request.getEspecialidad());
        }
        
        if (request.getAnosExperiencia() != null) {
            profesional.setAnosExperiencia(request.getAnosExperiencia());
        }
        
        if (request.getDisponible() != null) {
            profesional.setDisponible(request.getDisponible());
        }
        
        Profesional updatedProfesional = profesionalRepository.save(profesional);
        return convertToDTO(updatedProfesional);
    }
    
    @Transactional
    public void deleteProfesional(Long id) {
        Profesional profesional = profesionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        
        // Desactivar el usuario en lugar de eliminarlo
        User user = profesional.getUser();
        user.setActivo(false);
        userRepository.save(user);
        
        profesionalRepository.delete(profesional);
    }
    
    public List<ProfesionalDTO> getProfesionalsByPeluqueria(Long peluqueriaId) {
        return profesionalRepository.findByPeluqueriaId(peluqueriaId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProfesionalDTO> getAvailableProfesionals() {
        return profesionalRepository.findByDisponibleTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private ProfesionalDTO convertToDTO(Profesional profesional) {
        ProfesionalDTO dto = new ProfesionalDTO();
        dto.setId(profesional.getId());
        dto.setUserId(profesional.getUser().getId());
        dto.setNombre(profesional.getUser().getNombre());
        dto.setApellido(profesional.getUser().getApellido());
        dto.setEmail(profesional.getUser().getEmail());
        dto.setTelefono(profesional.getUser().getTelefono());
        
        if (profesional.getPeluqueria() != null) {
            dto.setPeluqueriaId(profesional.getPeluqueria().getId());
            dto.setPeluqueriaNombre(profesional.getPeluqueria().getNombre());
        }
        
        dto.setEspecialidad(profesional.getEspecialidad());
        dto.setAnosExperiencia(profesional.getAnosExperiencia());
        dto.setDisponible(profesional.getDisponible());
        
        // Aquí podrías calcular estadísticas como rating promedio, etc.
        // dto.setRatingPromedio(calculateRating(profesional));
        
        return dto;
    }
}
