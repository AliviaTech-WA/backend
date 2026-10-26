package com.upc.backend.repositories;

import com.upc.backend.entities.Seguimiento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// Repositorio compartido por las HU de autorización, sesiones y reportes del seguimiento.
public interface SeguimientoRepositorio extends JpaRepository<Seguimiento, Long> {

    // HU08, HU19 y HU28-HU29: localiza el seguimiento activo del paciente.
    Optional<Seguimiento> findFirstByIdPaciente_IdAndFechaFinIsNull(Long pacienteId);

    // HU32 | END26: obtiene los seguimientos activos del psicólogo.
    List<Seguimiento> findByIdPsicologo_IdAndFechaFinIsNull(Long psicologoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // HU24 | END23: serializa la lectura y actualización de la última revisión del paciente.
    Optional<Seguimiento> findFirstByIdPaciente_IdAndFechaFinIsNullOrderByIdDesc(Long pacienteId);

    // Infraestructura de seguridad para las HU consultadas por el psicólogo responsable.
    boolean existsByIdPaciente_IdAndIdPsicologo_IdAndFechaFinIsNull(Long pacienteId, Long psicologoId);
}
