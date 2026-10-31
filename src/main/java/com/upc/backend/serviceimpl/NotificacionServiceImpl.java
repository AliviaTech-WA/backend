package com.upc.backend.serviceimpl;

import com.upc.backend.dtos.NotificacionResponseDTO;
import com.upc.backend.entities.Notificacion;
import com.upc.backend.entities.Usuario;
import com.upc.backend.repositories.NotificacionRepository;
import com.upc.backend.repositories.UsuarioRepositorio;
import com.upc.backend.services.NotificacionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepositorio usuarioRepositorio;

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuario_IdOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(this::mapearADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarNoLeidas(Long usuarioId) {
        return notificacionRepository.countByUsuario_IdAndLeidoFalse(usuarioId);
    }

    @Override
    @Transactional
    public void marcarComoLeida(Long notificacionId, Long usuarioId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));

        // Seguridad: verificar que la notificación le pertenezca al usuario
        if (!notificacion.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tiene permisos para modificar esta notificación");
        }

        notificacion.setLeido(true);
        notificacionRepository.save(notificacion);
    }

    @Override
    @Transactional
    public void crearNotificacion(Long usuarioId, String titulo, String mensaje, String tipo) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Notificacion notificacion = Notificacion.builder()
                .usuario(usuario)
                .titulo(titulo)
                .mensaje(mensaje)
                .tipo(tipo)
                .leido(false)
                .fechaCreacion(OffsetDateTime.now())
                .build();

        notificacionRepository.save(notificacion);
    }

    private NotificacionResponseDTO mapearADTO(Notificacion n) {
        return new NotificacionResponseDTO(
                n.getId(),
                n.getTitulo(),
                n.getMensaje(),
                n.getTipo(),
                n.getLeido(),
                n.getFechaCreacion()
        );
    }
}