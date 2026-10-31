package com.upc.backend.repositories;

import com.upc.backend.entities.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Listar las notificaciones de un usuario ordenadas de la más reciente a la más antigua
    List<Notificacion> findByUsuario_IdOrderByFechaCreacionDesc(Long usuarioId);

    // Contar cuántas no leídas tiene (para la insignia roja o badge en la campana)
    long countByUsuario_IdAndLeidoFalse(Long usuarioId);
}