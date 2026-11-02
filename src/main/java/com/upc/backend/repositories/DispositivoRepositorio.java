package com.upc.backend.repositories;

import com.upc.backend.entities.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// HU11, HU14, HU15, HU21 y HU26: persistencia y consulta del wearable asignado.
public interface DispositivoRepositorio extends JpaRepository<Dispositivo, Long> {

    // HU11: selecciona solo pulseras vinculadas y no retiradas para la simulacion automatica.
    List<Dispositivo> findByFechaRetiroIsNullAndEstadoIgnoreCase(String estado);

    //HU13: Devuelve el dispositivo VINCULADO del paciente.
    Optional<Dispositivo>
    findFirstByIdPaciente_IdAndEstadoIgnoreCaseAndFechaRetiroIsNull(
        Long pacienteId,
        String estado
    );

    // HU14, HU15, HU21 y HU26 | END12-END13: localiza el dispositivo activo.
    Optional<Dispositivo> findFirstByIdPaciente_IdAndFechaRetiroIsNull(Long pacienteId);

    // Soporte de consulta histórica de dispositivos del paciente.
    List<Dispositivo> findByIdPaciente_Id(Long pacienteId);
}
