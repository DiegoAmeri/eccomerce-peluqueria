package com.peluqueria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peluqueria.dto.PeluqueriaDTO;
import com.peluqueria.model.Peluqueria;
import com.peluqueria.model.Peluquero;
import com.peluqueria.model.PeluqueriaPeluquero;
import com.peluqueria.repository.PeluqueriaRepository;
import com.peluqueria.repository.PeluqueroRepository;
import com.peluqueria.repository.PeluqueriaPeluqueroRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PeluqueriaService {
    
    @Autowired
    private PeluqueriaRepository peluqueriaRepository;
    
    @Autowired
    private PeluqueroRepository peluqueroRepository;
    
    @Autowired
    private PeluqueriaPeluqueroRepository peluqueriaPeluqueroRepository;
    
    public List<PeluqueriaDTO> obtenerTodasPeluquerias() {
        return peluqueriaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<PeluqueriaDTO> obtenerPeluqueriasActivas() {
        return peluqueriaRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public Optional<PeluqueriaDTO> obtenerPeluqueriaPorId(Long id) {
        return peluqueriaRepository.findById(id)
                .map(this::convertirADTO);
    }
    
    public PeluqueriaDTO crearPeluqueria(PeluqueriaDTO peluqueriaDTO) {
        Peluqueria peluqueria = new Peluqueria();
        peluqueria.setNombre(peluqueriaDTO.getNombre());
        peluqueria.setDireccion(peluqueriaDTO.getDireccion());
        peluqueria.setTelefono(peluqueriaDTO.getTelefono());
        peluqueria.setEmail(peluqueriaDTO.getEmail());
        peluqueria.setLatitud(peluqueriaDTO.getLatitud());
        peluqueria.setLongitud(peluqueriaDTO.getLongitud());
        peluqueria.setHorarioApertura(peluqueriaDTO.getHorarioApertura());
        peluqueria.setHorarioCierre(peluqueriaDTO.getHorarioCierre());
        peluqueria.setDescripcion(peluqueriaDTO.getDescripcion());
        peluqueria.setImagenUrl(peluqueriaDTO.getImagenUrl());
        peluqueria.setActivo(true);
        
        Peluqueria peluqueriaGuardada = peluqueriaRepository.save(peluqueria);
        return convertirADTO(peluqueriaGuardada);
    }
    
    public Optional<PeluqueriaDTO> actualizarPeluqueria(Long id, PeluqueriaDTO peluqueriaDTO) {
        return peluqueriaRepository.findById(id)
                .map(peluqueria -> {
                    peluqueria.setNombre(peluqueriaDTO.getNombre());
                    peluqueria.setDireccion(peluqueriaDTO.getDireccion());
                    peluqueria.setTelefono(peluqueriaDTO.getTelefono());
                    peluqueria.setEmail(peluqueriaDTO.getEmail());
                    peluqueria.setLatitud(peluqueriaDTO.getLatitud());
                    peluqueria.setLongitud(peluqueriaDTO.getLongitud());
                    peluqueria.setHorarioApertura(peluqueriaDTO.getHorarioApertura());
                    peluqueria.setHorarioCierre(peluqueriaDTO.getHorarioCierre());
                    peluqueria.setDescripcion(peluqueriaDTO.getDescripcion());
                    peluqueria.setImagenUrl(peluqueriaDTO.getImagenUrl());
                    
                    Peluqueria peluqueriaActualizada = peluqueriaRepository.save(peluqueria);
                    return convertirADTO(peluqueriaActualizada);
                });
    }
    
    public boolean desactivarPeluqueria(Long id) {
        return peluqueriaRepository.findById(id)
                .map(peluqueria -> {
                    peluqueria.setActivo(false);
                    peluqueriaRepository.save(peluqueria);
                    return true;
                })
                .orElse(false);
    }
    
    public Object obtenerEmpleados(Long peluqueriaId) {
        return peluqueriaPeluqueroRepository.findByPeluqueriaId(peluqueriaId);
    }
    
    public Object agregarEmpleado(Long peluqueriaId, Long peluqueroId, String rol) {
        Peluqueria peluqueria = peluqueriaRepository.findById(peluqueriaId)
                .orElseThrow(() -> new RuntimeException("Peluquería no encontrada"));
        
        Peluquero peluquero = peluqueroRepository.findById(peluqueroId)
                .orElseThrow(() -> new RuntimeException("Peluquero no encontrado"));
        
        PeluqueriaPeluquero relacion = new PeluqueriaPeluquero();
        relacion.setPeluqueria(peluqueria);
        relacion.setPeluquero(peluquero);
        relacion.setRol(rol);
        relacion.setActivo(true);
        
        return peluqueriaPeluqueroRepository.save(relacion);
    }
    
    public Object actualizarRolEmpleado(Long peluqueriaId, Long peluqueroId, String rol) {
        return peluqueriaPeluqueroRepository.findByPeluqueriaAndPeluquero(peluqueriaId, peluqueroId)
                .map(relacion -> {
                    relacion.setRol(rol);
                    return peluqueriaPeluqueroRepository.save(relacion);
                })
                .orElseThrow(() -> new RuntimeException("Relación no encontrada"));
    }
    
    public boolean removerEmpleado(Long peluqueriaId, Long peluqueroId) {
        return peluqueriaPeluqueroRepository.findByPeluqueriaAndPeluquero(peluqueriaId, peluqueroId)
                .map(relacion -> {
                    peluqueriaPeluqueroRepository.delete(relacion);
                    return true;
                })
                .orElse(false);
    }
    
    private PeluqueriaDTO convertirADTO(Peluqueria peluqueria) {
        PeluqueriaDTO dto = new PeluqueriaDTO();
        dto.setId(peluqueria.getId());
        dto.setNombre(peluqueria.getNombre());
        dto.setDireccion(peluqueria.getDireccion());
        dto.setTelefono(peluqueria.getTelefono());
        dto.setEmail(peluqueria.getEmail());
        dto.setLatitud(peluqueria.getLatitud());
        dto.setLongitud(peluqueria.getLongitud());
        dto.setHorarioApertura(peluqueria.getHorarioApertura());
        dto.setHorarioCierre(peluqueria.getHorarioCierre());
        dto.setDescripcion(peluqueria.getDescripcion());
        dto.setImagenUrl(peluqueria.getImagenUrl());
        dto.setCalificacionPromedio(peluqueria.getCalificacionPromedio());
        dto.setTotalCalificaciones(peluqueria.getTotalCalificaciones());
        dto.setActivo(peluqueria.getActivo());
        return dto;
    }

    public List<PeluqueriaDTO> obtenerPeluqueriasCercanas(Double latitud, Double longitud, Double radioKm) {
        // Implementación básica - devolver todas las activas
        return peluqueriaRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<PeluqueriaDTO> buscarPeluquerias(String nombre, List<Long> servicios) {
        // Si hay servicios filtramos, sino por nombre
        if (servicios != null && !servicios.isEmpty()) {
            return peluqueriaRepository.findByServiciosIds(servicios).stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
        } else {
            return peluqueriaRepository.buscarPorNombre(nombre).stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
        }
    }
}