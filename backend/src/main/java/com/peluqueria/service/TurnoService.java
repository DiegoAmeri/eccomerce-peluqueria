package com.peluqueria.service;

import com.peluqueria.dto.TurnoDTO;
import com.peluqueria.dto.request.NuevoTurnoRequest;
import com.peluqueria.model.Cliente;
import com.peluqueria.model.Servicio;
import com.peluqueria.model.Turno;
import com.peluqueria.model.enums.EstadoTurno;
import com.peluqueria.repository.ClienteRepository;
import com.peluqueria.repository.ServicioRepository;
import com.peluqueria.repository.TurnoRepository;
import com.peluqueria.service.TurnoService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    
    public List<TurnoDTO> obtenerTodosTurnos() {
        return turnoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    
    public List<TurnoDTO> obtenerTurnosPorFecha(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        return turnoRepository.findByFechaHoraBetween(inicio, fin).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    
    public List<TurnoDTO> obtenerTurnosHoy() {
        return obtenerTurnosPorFecha(LocalDate.now());
    }

    
    public List<TurnoDTO> obtenerTurnosEntreFechas(LocalDateTime inicio, LocalDateTime fin) {
        return turnoRepository.findByFechaHoraBetween(inicio, fin).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    
    public List<TurnoDTO> obtenerTurnosPorCliente(Long clienteId) {
        return turnoRepository.findByClienteId(clienteId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    
    public TurnoDTO crearTurno(NuevoTurnoRequest nuevoTurnoRequest) {
        // Validar que el cliente existe
        Cliente cliente = clienteRepository.findById(nuevoTurnoRequest.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        // Validar que el servicio existe
        Servicio servicio = servicioRepository.findById(nuevoTurnoRequest.getServicioId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        
        // Validar que no existe otro turno en la misma fecha y hora
        if (existeTurnoEnFechaHora(nuevoTurnoRequest.getFechaHora(), nuevoTurnoRequest.getClienteId())) {
            throw new RuntimeException("Ya existe un turno para este cliente en la fecha y hora seleccionada");
        }
        
        Turno turno = new Turno();
        turno.setFechaHora(nuevoTurnoRequest.getFechaHora());
        turno.setCliente(cliente);
        turno.setServicio(servicio);
        turno.setEstado(EstadoTurno.PENDIENTE);
        turno.setNotas(nuevoTurnoRequest.getNotas());
        
        Turno turnoGuardado = turnoRepository.save(turno);
        return convertirADTO(turnoGuardado);
    }

    
    public boolean cancelarTurno(Long id) {
        return turnoRepository.findById(id)
                .map(turno -> {
                    turno.setEstado(EstadoTurno.CANCELADO);
                    turnoRepository.save(turno);
                    return true;
                })
                .orElse(false);
    }

    
    public boolean confirmarTurno(Long id) {
        return turnoRepository.findById(id)
                .map(turno -> {
                    turno.setEstado(EstadoTurno.CONFIRMADO);
                    turnoRepository.save(turno);
                    return true;
                })
                .orElse(false);
    }

    
    public boolean completarTurno(Long id) {
        return turnoRepository.findById(id)
                .map(turno -> {
                    turno.setEstado(EstadoTurno.COMPLETADO);
                    turnoRepository.save(turno);
                    return true;
                })
                .orElse(false);
    }

    
    public List<String> obtenerHorariosDisponibles(LocalDate fecha, Long servicioId) {
        // Implementar lógica para obtener horarios disponibles
        // Esto es un ejemplo básico
        List<String> horarios = new ArrayList<>();
        LocalTime horaInicio = LocalTime.of(9, 0);
        LocalTime horaFin = LocalTime.of(18, 0);
        
        while (horaInicio.isBefore(horaFin)) {
            horarios.add(horaInicio.toString());
            horaInicio = horaInicio.plusMinutes(30);
        }
        
        return horarios;
    }

    
    public boolean existeTurnoEnFechaHora(LocalDateTime fechaHora, Long clienteId) {
        return turnoRepository.existsByFechaHoraAndClienteId(fechaHora, clienteId);
    }

    
    public int contarTurnosHoy() {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = LocalDate.now().atTime(LocalTime.MAX);
        return turnoRepository.countByFechaHoraBetween(inicio, fin);
    }

    
    public double calcularIngresosHoy() {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = LocalDate.now().atTime(LocalTime.MAX);
        List<Turno> turnosHoy = turnoRepository.findByFechaHoraBetween(inicio, fin);
        
        return turnosHoy.stream()
                .filter(turno -> turno.getEstado() != EstadoTurno.CANCELADO)
                .mapToDouble(turno -> turno.getServicio().getPrecio().doubleValue())
                .sum();
    }

    
    public Long contarClientesNuevosEsteMes() {
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return clienteRepository.countByCreatedAtBetween(
            inicioMes.atStartOfDay(),
            finMes.atTime(LocalTime.MAX)
        );
    }

    
    public String obtenerServicioPopular() {
        // Implementar lógica para obtener el servicio más popular
        return "Corte de Cabello"; // Ejemplo
    }

    
    public int contarTurnosPendientes() {
        return turnoRepository.countByEstado(EstadoTurno.PENDIENTE);
    }

    
    public int contarTurnosPorFecha(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        return turnoRepository.countByFechaHoraBetween(inicio, fin);
    }

    
    public double calcularIngresosPorFecha(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        List<Turno> turnos = turnoRepository.findByFechaHoraBetween(inicio, fin);
        
        return turnos.stream()
                .filter(turno -> turno.getEstado() != EstadoTurno.CANCELADO)
                .mapToDouble(turno -> turno.getServicio().getPrecio().doubleValue())
                .sum();
    }

    public Long contarClientesNuevosPorMes(int año, int mes) {
        LocalDate inicioMes = LocalDate.of(año, mes, 1);
        LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());
        return clienteRepository.countByCreatedAtBetween(
            inicioMes.atStartOfDay(),
            finMes.atTime(LocalTime.MAX)
        );
    }

    private TurnoDTO convertirADTO(Turno turno) {
        TurnoDTO dto = new TurnoDTO();
        dto.setId(turno.getId());
        dto.setFechaHora(turno.getFechaHora());
        dto.setClienteId(turno.getCliente().getId());
        dto.setClienteNombre(turno.getCliente().getNombre() + " " + turno.getCliente().getApellido());
        dto.setServicioId(turno.getServicio().getId());
        dto.setServicioNombre(turno.getServicio().getNombre());
        dto.setEstado(turno.getEstado());
        dto.setNotas(turno.getNotas());
        dto.setPrecio(turno.getServicio().getPrecio());
        dto.setCreatedAt(turno.getCreatedAt());
        return dto;
    }
}