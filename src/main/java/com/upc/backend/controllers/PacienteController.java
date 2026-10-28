package com.upc.backend.controllers;
import com.upc.backend.dtos.ConsultaDispositivoAsignado;
import com.upc.backend.dtos.RegistrarSesionRequestDTO;
import com.upc.backend.dtos.SesionCreadaDTO;
import com.upc.backend.services.SesionService;
import com.upc.backend.services.DispositivoService;
import com.upc.backend.dtos.SesionResumenDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/v1/pacientes")
// Controlador compartido por las HU de bienestar, wearable, inicio, sesiones y perfil del paciente.

public class PacienteController {

    private final SesionService sesionService;
    private final DispositivoService dispositivoService;

    public PacienteController(SesionService sesionService, DispositivoService dispositivoService) {
        this.sesionService = sesionService;
        this.dispositivoService = dispositivoService;
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

    @GetMapping("/{pacienteId}/dispositivo")
    @PreAuthorize("@autorizacionService.puedeAccederPaciente(#pacienteId)")
    public ResponseEntity<ConsultaDispositivoAsignado> consultarDispositivoAsignado(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(dispositivoService.consultarDispositivoAsignado(pacienteId));
    } 
    

}

