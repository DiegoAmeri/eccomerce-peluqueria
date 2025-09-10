package com.peluqueria.mapper;

import com.peluqueria.dto.PeluqueriaDTO;
import com.peluqueria.model.Peluqueria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PeluqueriaMapper {
    PeluqueriaMapper INSTANCE = Mappers.getMapper(PeluqueriaMapper.class);
    
    @Mapping(target = "servicios", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    @Mapping(target = "distancia", ignore = true)
    PeluqueriaDTO toDTO(Peluqueria peluqueria);
    
    @Mapping(target = "servicios", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Peluqueria toEntity(PeluqueriaDTO peluqueriaDTO);
}