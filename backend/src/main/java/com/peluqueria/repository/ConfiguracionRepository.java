package com.peluqueria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peluqueria.model.Configuracion;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, Long> {
    // Solo habrá una configuración en el sistema
    Configuracion findFirstByOrderById();
}
