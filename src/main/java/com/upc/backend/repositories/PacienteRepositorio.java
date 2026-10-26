package com.upc.backend.repositories;

import com.upc.backend.entities.Paciente;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// Repositorio base compartido por las HU que consultan o modifican al paciente.
public interface PacienteRepositorio extends JpaRepository<Paciente, Long> {

    // HU02: serializa los reenvíos para no mantener dos invitaciones vigentes.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Paciente> findWithLockById(Long pacienteId);
}
