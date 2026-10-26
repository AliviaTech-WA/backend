package com.upc.backend.repositories;

import com.upc.backend.entities.ContactoEmergencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// HU21 | END28 y END30: persistencia del contacto único del paciente.
public interface ContactoEmergenciaRepositorio extends JpaRepository<ContactoEmergencia, Long> {

    // HU21: permite consultar o actualizar el contacto asociado al paciente.
    Optional<ContactoEmergencia> findByIdPaciente_Id(Long pacienteId);
}
