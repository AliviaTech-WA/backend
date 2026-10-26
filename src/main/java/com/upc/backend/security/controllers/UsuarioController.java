package com.upc.backend.security.controllers;

import com.upc.backend.dtos.MensajeResponseDTO;
import com.upc.backend.security.dtos.CambiarContrasenaRequestDTO;
import com.upc.backend.security.services.UsuarioSecurityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
// HU06: contiene operaciones de seguridad sobre el usuario autenticado.
public class UsuarioController {

    private final UsuarioSecurityService usuarioSecurityService;

    public UsuarioController(UsuarioSecurityService usuarioSecurityService) {
        this.usuarioSecurityService = usuarioSecurityService;
    }

    @PutMapping("/{usuarioId}/contrasena")
    @PreAuthorize("@autorizacionService.esMismoUsuario(#usuarioId)")
    // HU06 | END07: cambia la contraseña después de validar la contraseña actual.
    public ResponseEntity<MensajeResponseDTO> cambiarContrasena(
            @PathVariable Long usuarioId,
            @Valid @RequestBody CambiarContrasenaRequestDTO request) {
        return ResponseEntity.ok(usuarioSecurityService.cambiarContrasena(usuarioId, request));
    }
}
