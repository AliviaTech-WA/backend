package com.upc.backend.repositories;

import com.upc.backend.entities.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// HU08, HU19, HU20 y HU28-HU30: persistencia y consulta de sesiones.
public interface SesionRepositorio extends JpaRepository<Sesion, Long> {

    // HU19 y HU29 | END19: historial descendente del seguimiento.
    List<Sesion> findByIdSeguimiento_IdOrderByFechaSesionDesc(Long seguimientoId);

    // HU08 | END16: selecciona la sesión más reciente para el inicio del paciente.
    Optional<Sesion> findFirstByIdSeguimiento_IdOrderByFechaSesionDesc(Long seguimientoId);
}
