package com.upc.backend.controllers;

import com.upc.backend.dtos.NotificacionResponseDTO;
import com.upc.backend.services.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    // GET /api/v1/notificaciones/usuario/{usuarioId}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarNotificaciones(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.listarPorUsuario(usuarioId));
    }

    // GET /api/v1/notificaciones/usuario/{usuarioId}/no-leidas/conteo
    @GetMapping("/usuario/{usuarioId}/no-leidas/conteo")
    public ResponseEntity<Long> contarNoLeidas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.contarNoLeidas(usuarioId));
    }

    // PATCH /api/v1/notificaciones/{notificacionId}/leida?usuarioId=1
    @PatchMapping("/{notificacionId}/leida")
    public ResponseEntity<Void> marcarComoLeida(
            @PathVariable Long notificacionId,
            @RequestParam Long usuarioId) {
        notificacionService.marcarComoLeida(notificacionId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}