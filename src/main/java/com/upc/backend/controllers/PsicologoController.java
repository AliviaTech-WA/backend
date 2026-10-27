package com.upc.backend.controllers;

import com.upc.backend.dtos.ActualizarPsicologoPerfilRequestDTO;
import com.upc.backend.dtos.PsicologoPerfilDTO;
import com.upc.backend.services.PsicologoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/psicologos")
// HU34: gestión del perfil profesional del psicólogo.
public class PsicologoController {
    private final PsicologoService psicologoService;

    public PsicologoController(PsicologoService psicologoService) {
        this.psicologoService = psicologoService;
    }

    @GetMapping("/{psicologoId}/perfil")
    @PreAuthorize("@autorizacionService.puedeAccederPsicologo(#psicologoId)")
    // HU34 | END31: consulta el perfil del psicólogo autenticado.
    public ResponseEntity<PsicologoPerfilDTO> buscarPerfil(@PathVariable Long psicologoId) {
        return ResponseEntity.ok(psicologoService.buscarPerfilPorId(psicologoId));
    }

    @PutMapping("/{psicologoId}/perfil")
    @PreAuthorize("@autorizacionService.puedeAccederPsicologo(#psicologoId)")
    // HU34 | END32: actualiza los datos editables del perfil profesional.
    public ResponseEntity<PsicologoPerfilDTO> actualizarPerfil(@PathVariable Long psicologoId, @Valid @RequestBody ActualizarPsicologoPerfilRequestDTO request) {
        return ResponseEntity.ok(psicologoService.actualizarPerfil(psicologoId, request));
    }

}
