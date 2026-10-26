package com.upc.backend.repositories;

import com.upc.backend.entities.InvitacionActivacion;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// HU02 y HU03 | END24 y END02: persiste, reemplaza y consume invitaciones de un solo uso.
public interface InvitacionActivacionRepositorio extends JpaRepository<InvitacionActivacion, Long> {

    // HU03 | END02: bloquea la invitación mientras se valida y consume en la misma transacción.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InvitacionActivacion>
    findFirstByIdPaciente_Usuario_CorreoIgnoreCaseAndFechaUsoIsNullOrderByFechaCreacionDesc(String correo);

    // HU02: recupera los códigos pendientes para invalidarlos antes de un reenvío.
    List<InvitacionActivacion> findByIdPaciente_IdAndFechaUsoIsNull(Long pacienteId);
}
