package com.upc.backend.repositories;

import com.upc.backend.entities.RegistroBienestar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
// HU08-HU10, HU25 y HU32: persistencia y consulta cronológica de bienestar.
public interface RegistroBienestarRepositorio extends JpaRepository<RegistroBienestar, Long> {

    // HU10 y HU25 | END09: filtra el historial por paciente y periodo.
    List<RegistroBienestar> findByIdPaciente_IdAndFechaHoraBetweenOrderByFechaHoraDesc(
            Long pacienteId, OffsetDateTime desde, OffsetDateTime hasta);

    // HU08 | END16: obtiene registros recientes para seleccionar el último.
    List<RegistroBienestar> findByIdPaciente_IdOrderByFechaHoraDesc(Long pacienteId);

    // HU22-HU24 | END21-END23: obtiene la última fecha y cuenta novedades desde la revisión.
    Optional<RegistroBienestar> findFirstByIdPaciente_IdOrderByFechaHoraDesc(Long pacienteId);

}
