package com.upc.backend.repositories;

import com.upc.backend.entities.ResumenWearableDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
// HU11-HU13 y HU26: persistencia del resumen diario de sueño y pasos.
public interface ResumenWearableDiarioRepositorio extends JpaRepository<ResumenWearableDiario, Long> {

    // HU11 y HU12: actualiza el resumen de la fecha o consulta el sueño previo al evento.
    Optional<ResumenWearableDiario> findByIdDispositivo_IdAndFecha(Long dispositivoId, LocalDate fecha);

    // HU26 | END11: muestra el ultimo resumen previo sin atribuirlo a una fecha nueva.
    Optional<ResumenWearableDiario> findFirstByIdDispositivo_IdAndFechaBeforeOrderByFechaDesc(
            Long dispositivoId, LocalDate fecha);

    // HU26 | END11: recupera la evolución diaria del periodo.
    List<ResumenWearableDiario> findByIdDispositivo_IdAndFechaBetweenOrderByFechaAsc(
            Long dispositivoId, LocalDate desde, LocalDate hasta);

    // HU24 | END23: reúne el sueño diario del paciente aunque haya cambiado de dispositivo.
    List<ResumenWearableDiario> findByIdDispositivo_IdPaciente_IdAndFechaBetweenOrderByFechaAsc(
            Long pacienteId, LocalDate desde, LocalDate hasta);
}
