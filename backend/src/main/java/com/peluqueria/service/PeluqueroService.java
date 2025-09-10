package com.peluqueria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peluqueria.dto.PeluqueroDTO;
import com.peluqueria.model.Peluquero;
import com.peluqueria.repository.PeluqueroRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PeluqueroService {
    
    @Autowired
    private PeluqueroRepository peluqueroRepository;
    
    public List<PeluqueroDTO> obtenerTodosPeluqueros() {
        return peluqueroRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<PeluqueroDTO> obtenerPeluquerosActivos() {
        return peluqueroRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public Optional<PeluqueroDTO> obtenerPeluqueroPorId(Long id) {
        return peluqueroRepository.findById(id)
                .map(this::convertirADTO);
    }
    
    public Optional<PeluqueroDTO> obtenerPeluqueroPorUsuarioId(Long usuarioId) {
        return peluqueroRepository.findByUsuarioId(usuarioId)
                .map(this::convertirADTO);
    }
    
    public Optional<PeluqueroDTO> actualizarPeluquero(Long id, PeluqueroDTO peluqueroDTO) {
        return peluqueroRepository.findById(id)
                .map(peluquero -> {
                    peluquero.setNombre(peluqueroDTO.getNombre());
                    peluquero.setApellido(peluqueroDTO.getApellido());
                    peluquero.setTelefono(peluqueroDTO.getTelefono());
                    peluquero.setEspecialidad(peluqueroDTO.getEspecialidad());
                    peluquero.setExperiencia(peluqueroDTO.getExperiencia());
                    peluquero.setDescripcion(peluqueroDTO.getDescripcion());
                    peluquero.setFotoUrl(peluqueroDTO.getFotoUrl());
                    
                    Peluquero peluqueroActualizado = peluqueroRepository.save(peluquero);
                    return convertirADTO(peluqueroActualizado);
                });
    }
    
    public boolean desactivarPeluquero(Long id) {
        return peluqueroRepository.findById(id)
                .map(peluquero -> {
                    peluquero.setActivo(false);
                    peluqueroRepository.save(peluquero);
                    return true;
                })
                .orElse(false);
    }
    
    private PeluqueroDTO convertirADTO(Peluquero peluquero) {
        PeluqueroDTO dto = new PeluqueroDTO();
        dto.setId(peluquero.getId());
        dto.setUsuarioId(peluquero.getUsuario().getId());
        dto.setNombre(peluquero.getNombre());
        dto.setApellido(peluquero.getApellido());
        dto.setTelefono(peluquero.getTelefono());
        dto.setEspecialidad(peluquero.getEspecialidad());
        dto.setExperiencia(peluquero.getExperiencia());
        dto.setDescripcion(peluquero.getDescripcion());
        dto.setFotoUrl(peluquero.getFotoUrl());
        dto.setActivo(peluquero.getActivo());
        return dto;
    }
}