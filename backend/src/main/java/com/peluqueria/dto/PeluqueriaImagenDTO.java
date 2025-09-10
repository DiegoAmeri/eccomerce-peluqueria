package com.peluqueria.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeluqueriaImagenDTO {
    private Long id;
    private Long peluqueriaId;
    private String imagenUrl;
    private Boolean esPrincipal;
    private Integer orden;
    private LocalDateTime createdAt;
}