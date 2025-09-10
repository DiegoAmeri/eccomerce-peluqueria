package com.peluqueria.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peluqueria.dto.ServicioDTO;
import com.peluqueria.model.Servicio;
import com.peluqueria.repository.ServicioRepository;

@Service
public class ServicioService {
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    public List<ServicioDTO> obtenerTodosServicios() {
        return servicioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<ServicioDTO> obtenerServiciosActivos() {
        return servicioRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public Optional<ServicioDTO> obtenerServicioPorId(Long id) {
        return servicioRepository.findById(id)
                .map(this::convertirADTO);
    }
    
    public List<ServicioDTO> buscarServiciosPorNombre(String nombre) {
        return servicioRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public ServicioDTO crearServicio(ServicioDTO servicioDTO) {
        Servicio servicio = new Servicio();
        servicio.setNombre(servicioDTO.getNombre());
        servicio.setDuracionMinutos(servicioDTO.getDuracionMinutos());
        servicio.setPrecio(servicioDTO.getPrecio());
        servicio.setDescripcion(servicioDTO.getDescripcion());
        servicio.setActivo(true);
        
        Servicio servicioGuardado = servicioRepository.save(servicio);
        return convertirADTO(servicioGuardado);
    }
    
    public Optional<ServicioDTO> actualizarServicio(Long id, ServicioDTO servicioDTO) {
        return servicioRepository.findById(id)
                .map(servicioExistente -> {
                    servicioExistente.setNombre(servicioDTO.getNombre());
                    servicioExistente.setDuracionMinutos(servicioDTO.getDuracionMinutos());
                    servicioExistente.setPrecio(servicioDTO.getPrecio());
                    servicioExistente.setDescripcion(servicioDTO.getDescripcion());
                    
                    Servicio servicioActualizado = servicioRepository.save(servicioExistente);
                    return convertirADTO(servicioActualizado);
                });
    }
    
    public boolean desactivarServicio(Long id) {
        return servicioRepository.findById(id)
                .map(servicio -> {
                    servicio.setActivo(false);
                    servicioRepository.save(servicio);
                    return true;
                })
                .orElse(false);
    }
    
    private ServicioDTO convertirADTO(Servicio servicio) {
        return new ServicioDTO(
            servicio.getId(),
            servicio.getNombre(),
            servicio.getDuracionMinutos(),
            servicio.getPrecio(),
            servicio.getDescripcion(),
            servicio.getActivo()
        );
    }
}
