package com.upc.backend.repositories;

import com.upc.backend.entities.LecturaWearable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
// HU11-HU13 y HU26: persistencia y consulta de mediciones del wearable.
public interface LecturaWearableRepositorio extends JpaRepository<LecturaWearable, Long> {

    // HU11: evita repetir la lectura programada de un mismo intervalo.
    boolean existsByIdDispositivo_IdAndFechaHora(Long dispositivoId, OffsetDateTime fechaHora);

    // HU13: obtiene el estado inmediatamente anterior para delimitar episodios consecutivos.
    Optional<LecturaWearable> findFirstByIdDispositivo_IdAndFechaHoraBeforeOrderByFechaHoraDesc(
            Long dispositivoId, OffsetDateTime fechaHora);

    // HU26 | END11: recupera lecturas ordenadas para métricas y evolución.
    List<LecturaWearable> findByIdDispositivo_IdAndFechaHoraBetweenOrderByFechaHoraDesc(
            Long dispositivoId, OffsetDateTime desde, OffsetDateTime hasta);

    // HU11 y HU12: detecta lecturas existentes y completa datos de un evento.
    List<LecturaWearable> findByIdDispositivo_IdAndFechaHoraBetween(
            Long dispositivoId, OffsetDateTime desde, OffsetDateTime hasta);

    // HU31 | END25: reúne las lecturas de todos los dispositivos del paciente en el periodo.
    List<LecturaWearable> findByIdDispositivo_IdPaciente_IdAndFechaHoraBetweenOrderByFechaHoraAsc(
            Long pacienteId, OffsetDateTime desde, OffsetDateTime hasta);

    // HU24 | END23: cuenta las lecturas recibidas desde la revisión anterior.
    long countByIdDispositivo_IdPaciente_IdAndFechaHoraAfterAndFechaHoraLessThanEqual(
            Long pacienteId, OffsetDateTime desde, OffsetDateTime hasta);
}
