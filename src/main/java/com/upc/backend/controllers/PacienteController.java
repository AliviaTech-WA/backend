package com.upc.backend.controllers;
import com.upc.backend.dtos.RegistrarSesionRequestDTO;
import com.upc.backend.dtos.SesionCreadaDTO;
import com.upc.backend.services.SesionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pacientes")
// Controlador compartido por las HU de bienestar, wearable, inicio, sesiones y perfil del paciente.

public class PacienteController {

    private final SesionService sesionService;

    public PacienteController(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    @PostMapping("/{pacienteId}/sesiones")
    @PreAuthorize("hasRole('PSICOLOGO') and @autorizacionService.puedeAccederPaciente(#pacienteId)")
    // HU28 | END18: registra una sesión en el seguimiento activo del paciente.
    public ResponseEntity<SesionCreadaDTO> registrarSesion(
            @PathVariable Long pacienteId, @Valid @RequestBody RegistrarSesionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sesionService.registrar(pacienteId, request));
    }
}

