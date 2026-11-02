package com.upc.backend.services;

import com.upc.backend.dtos.NotificacionResponseDTO;
import java.util.List;

public interface NotificacionService {
    List<NotificacionResponseDTO> listarPorUsuario(Long usuarioId);
    long contarNoLeidas(Long usuarioId);
    void marcarComoLeida(Long notificacionId, Long usuarioId);
    void crearNotificacion(Long usuarioId, String titulo, String mensaje, String tipo);
}