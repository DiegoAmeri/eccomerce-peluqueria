package com.peluqueria.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.peluqueria.model.enums.EstadoTurno;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurnoDTO {
    private Long id;
    private LocalDateTime fechaHora;
    private Long clienteId;
    private String clienteNombre;
    private Long servicioId;
    private String servicioNombre;
    private EstadoTurno estado;
    private String notas;
    private BigDecimal precio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
