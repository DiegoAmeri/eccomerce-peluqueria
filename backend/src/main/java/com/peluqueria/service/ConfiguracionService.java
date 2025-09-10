package com.peluqueria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peluqueria.dto.ConfiguracionDTO;
import com.peluqueria.model.Configuracion;
import com.peluqueria.repository.ConfiguracionRepository;

@Service
public class ConfiguracionService {
    
    @Autowired
    private ConfiguracionRepository configuracionRepository;
    
    public ConfiguracionDTO obtenerConfiguracion() {
        Configuracion configuracion = configuracionRepository.findFirstByOrderById();
        
        // Si no existe configuración, crear una por defecto
        if (configuracion == null) {
            configuracion = new Configuracion();
            configuracion.setMensajeConfirmacion("Hola [nombre], tu turno en StyleCut está confirmado para el [fecha] a las [hora]. Te esperamos!");
            configuracion.setMensajeRecordatorio("Hola [nombre], te recordamos que tenés tu turno en StyleCut mañana a las [hora]. Esperamos verte!");
            configuracion = configuracionRepository.save(configuracion);
        }
        
        return convertirADTO(configuracion);
    }
    
    public ConfiguracionDTO actualizarConfiguracion(ConfiguracionDTO configuracionDTO) {
        Configuracion configuracion = configuracionRepository.findFirstByOrderById();
        
        if (configuracion == null) {
            configuracion = new Configuracion();
        }
        
        configuracion.setNombrePeluqueria(configuracionDTO.getNombrePeluqueria());
        configuracion.setHoraApertura(configuracionDTO.getHoraApertura());
        configuracion.setHoraCierre(configuracionDTO.getHoraCierre());
        configuracion.setIntervaloEntreTurnos(configuracionDTO.getIntervaloEntreTurnos());
        configuracion.setNumeroWhatsapp(configuracionDTO.getNumeroWhatsapp());
        configuracion.setMensajeConfirmacion(configuracionDTO.getMensajeConfirmacion());
        configuracion.setMensajeRecordatorio(configuracionDTO.getMensajeRecordatorio());
        
        Configuracion configuracionActualizada = configuracionRepository.save(configuracion);
        return convertirADTO(configuracionActualizada);
    }
    
    private ConfiguracionDTO convertirADTO(Configuracion configuracion) {
        ConfiguracionDTO dto = new ConfiguracionDTO();
        dto.setId(configuracion.getId());
        dto.setNombrePeluqueria(configuracion.getNombrePeluqueria());
        dto.setHoraApertura(configuracion.getHoraApertura());
        dto.setHoraCierre(configuracion.getHoraCierre());
        dto.setIntervaloEntreTurnos(configuracion.getIntervaloEntreTurnos());
        dto.setNumeroWhatsapp(configuracion.getNumeroWhatsapp());
        dto.setMensajeConfirmacion(configuracion.getMensajeConfirmacion());
        dto.setMensajeRecordatorio(configuracion.getMensajeRecordatorio());
        
        return dto;
    }
}