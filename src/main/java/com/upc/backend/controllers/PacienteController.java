package com.upc.backend.controllers;
import com.upc.backend.dtos.*;
import com.upc.backend.services.AnalisisIaService;
import com.upc.backend.services.RegistroBienestarService;
import com.upc.backend.services.SesionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pacientes")
// Controlador compartido por las HU de bienestar, wearable, inicio, sesiones y perfil del paciente.

public class PacienteController {

    private final SesionService sesionService;
    private final AnalisisIaService analisisIaService;
    private final RegistroBienestarService registroBienestarService;

    public PacienteController(SesionService sesionService,
                              AnalisisIaService analisisIaService,
                              RegistroBienestarService registroBienestarService ) {
        this.sesionService = sesionService;
        this.analisisIaService = analisisIaService;
        this.registroBienestarService = registroBienestarService;
    }

    @PostMapping("/{pacienteId}/registros-bienestar")
    @PreAuthorize("@autorizacionService.esMismoUsuario(#pacienteId)")
    public ResponseEntity<RegistroBienestarResponseDTO> registrarBienestar(
            @PathVariable Long pacienteId,
            @Valid @RequestBody RegistrarBienestarRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registroBienestarService.registrar(pacienteId, request));
    }

    @PostMapping("/{pacienteId}/sesiones")
    @PreAuthorize("hasRole('PSICOLOGO') and @autorizacionService.puedeAccederPaciente(#pacienteId)")
    // HU28 | END18: registra una sesión en el seguimiento activo del paciente.
    public ResponseEntity<SesionCreadaDTO> registrarSesion(
            @PathVariable Long pacienteId, @Valid @RequestBody RegistrarSesionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sesionService.registrar(pacienteId, request));
    }

    @GetMapping("/{pacienteId}/sesiones")
    @PreAuthorize("@autorizacionService.puedeAccederPaciente(#pacienteId)")
    // HU19 y HU29 | END19: lista las sesiones compartidas del seguimiento activo.
    public ResponseEntity<List<SesionResumenDTO>> listarSesiones(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(sesionService.listar(pacienteId));
    }

    @PostMapping("/{pacienteId}/analisis-ia")
    @PreAuthorize("hasRole('PSICOLOGO') and @autorizacionService.puedeAccederPaciente(#pacienteId)")
    // HU31 | END25: genera el análisis bajo demanda sin almacenar el resultado.
    public ResponseEntity<AnalisisIaResponseDTO> generarAnalisis(
            @PathVariable Long pacienteId,
            @Valid @RequestBody GenerarAnalisisIaRequestDTO request) {
            return ResponseEntity.ok(analisisIaService.generar(pacienteId, request));
    }

}

