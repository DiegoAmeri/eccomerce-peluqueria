package com.peluqueria.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAppointmentRequest {
    @NotNull
    private Long peluqueriaId;
    
    @NotNull
    private Long serviceId;
    
    private Long profesionalId;
    
    @NotNull
    private LocalDateTime fechaHora;
    
    private String notas;
}
