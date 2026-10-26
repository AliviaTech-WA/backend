package com.upc.backend.repositories;

import com.upc.backend.entities.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// HU01, HU32-HU34: persistencia del perfil profesional y sus reportes.
public interface PsicologoRepositorio extends JpaRepository<Psicologo, Long> {

    // HU01 | END01: evita números de colegiatura duplicados.
    boolean existsByNumeroColegiatura(String numeroColegiatura);
}
