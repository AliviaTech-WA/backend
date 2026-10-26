package com.upc.backend.repositories;

import com.upc.backend.entities.Consentimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// HU03 | END02: conserva la aceptación y la versión de términos aplicada al activar la cuenta.
public interface ConsentimientoRepositorio extends JpaRepository<Consentimiento, Long> {
}
