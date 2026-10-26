package com.upc.backend.repositories;

import com.upc.backend.entities.RecuperacionContrasena;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
// HU05 | END04-END06: persistencia PostgreSQL y bloqueo de recuperaciones de contraseña.
public interface RecuperacionContrasenaRepositorio extends JpaRepository<RecuperacionContrasena, UUID> {

    // HU05 | END05-END06: bloquea la fila durante la validación o consumo del secreto.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RecuperacionContrasena> findWithLockById(UUID recuperacionId);

    // HU05 | END06: obtiene el usuario antes de bloquearlo sin dejar la recuperación en el contexto JPA.
    @Query("select r.idUsuario.id from RecuperacionContrasena r where r.id = :recuperacionId")
    Optional<Long> findUsuarioIdById(@Param("recuperacionId") UUID recuperacionId);

    // HU05 | END04: invalida cualquier recuperación anterior del mismo usuario.
    void deleteByIdUsuario_Id(Long usuarioId);
}
