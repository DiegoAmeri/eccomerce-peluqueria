package com.peluqueria.dto.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NuevoTurnoRequest {
    private LocalDateTime fechaHora;
    private Long clienteId;
    private Long servicioId;
    private String notas;
    

}
