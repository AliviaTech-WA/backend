package com.upc.backend.repositories;

import com.upc.backend.entities.EventoWearable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
// HU08, HU12, HU13, HU16-HU18, HU27 y HU32: persistencia y consulta de eventos wearable.
public interface EventoWearableRepositorio extends JpaRepository<EventoWearable, Long> {

    // HU13: recupera el evento mas reciente para prolongar un episodio alto sin duplicarlo.
    Optional<EventoWearable> findFirstByIdDispositivo_IdOrderByFechaHoraInicioDesc(Long dispositivoId);

    // HU12 y HU17 | END15: historial completo del paciente.
    List<EventoWearable> findByIdDispositivo_IdPaciente_IdOrderByFechaHoraInicioDesc(Long pacienteId);

    // HU16: Busca el evento puntual asegurando que pertenezca al paciente y esté pendiente (null)
    Optional<EventoWearable> findByIdAndIdDispositivo_IdPaciente_IdAndEstadoConfirmacionIsNull(Long id_evento, Long pacienteId);

    // HU16: recupera eventos pendientes para el paciente.
    List<EventoWearable> findByIdDispositivo_IdPaciente_IdAndEstadoConfirmacionIsNullOrderByFechaHoraInicioDesc(Long pacienteId);

    // HU17: recupera lista de eventos respondidos del paciente.
    List<EventoWearable> findByIdDispositivo_IdPaciente_IdAndEstadoConfirmacionIsNotNullOrderByFechaHoraInicioDesc(Long pacienteId);

    // HU18 y HU27 | END15-END17: filtra eventos por estado de confirmación.
    List<EventoWearable> findByIdDispositivo_IdPaciente_IdAndEstadoConfirmacionOrderByFechaHoraInicioDesc(
            Long pacienteId, String estadoConfirmacion);

    // HU08 | END16: selecciona el evento pendiente más reciente.
    Optional<EventoWearable> findFirstByIdDispositivo_IdPaciente_IdAndEstadoConfirmacionOrderByFechaHoraInicioDesc(
            Long pacienteId, String estadoConfirmacion);

    // HU08 | END16: cuenta las novedades pendientes mostradas al paciente.
    long countByIdDispositivo_IdPaciente_IdAndEstadoConfirmacion(Long pacienteId, String estadoConfirmacion);

    // HU32 | END26: recupera eventos del periodo para los indicadores del reporte.
    List<EventoWearable> findByIdDispositivo_IdPaciente_IdAndFechaHoraInicioBetween(
            Long pacienteId, OffsetDateTime desde, OffsetDateTime hasta);
}
