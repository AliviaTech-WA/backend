package com.upc.backend.controllers;

import com.upc.backend.dtos.ActualizarSesionRequestDTO;
import com.upc.backend.dtos.SesionDetalleDTO;
import com.upc.backend.security.entities.UsuarioPrincipal;
import com.upc.backend.security.services.AutorizacionService;
import com.upc.backend.services.SesionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sesiones")
// HU20 y HU30: expone el detalle de sesión con visibilidad diferenciada por rol.
public class SesionController {
    private final SesionService sesionService;
    private final AutorizacionService autorizacionService;

    public SesionController(SesionService sesionService, AutorizacionService autorizacionService) {
        this.sesionService = sesionService;
        this.autorizacionService = autorizacionService;
    }

    @GetMapping("/{sesionId}")
    @PreAuthorize("@autorizacionService.puedeAccederSesion(#sesionId)")
    // HU20 y HU30 | END20: omite la nota interna para pacientes y la incluye para psicólogos.
    public ResponseEntity<SesionDetalleDTO> buscarDetalle(@PathVariable Long sesionId) {
        UsuarioPrincipal principal = autorizacionService.principalActual();
        boolean incluirNotaInterna = principal != null && "PSICOLOGO".equals(principal.getRol());
        return ResponseEntity.ok(sesionService.buscarDetalle(sesionId, incluirNotaInterna));
    }

    @PutMapping("/{sesionId}")
    @PreAuthorize("@autorizacionService.puedeModificarSesion(#sesionId)")
    public ResponseEntity<SesionDetalleDTO> actualizar(@PathVariable Long sesionId,
                                                       @Valid @RequestBody ActualizarSesionRequestDTO request) {
        return ResponseEntity.ok(sesionService.actualizar(sesionId, request));
    }

    @DeleteMapping("/{sesionId}")
    @PreAuthorize("@autorizacionService.puedeModificarSesion(#sesionId)")
    public ResponseEntity<Void> eliminar(@PathVariable Long sesionId) {
        sesionService.eliminar(sesionId);
        return ResponseEntity.noContent().build();
    }
}
