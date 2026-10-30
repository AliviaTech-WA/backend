package com.upc.backend.controllers;
import com.upc.backend.dtos.*;
import com.upc.backend.services.AnalisisIaService;
import com.upc.backend.services.PacienteService;
import com.upc.backend.services.RegistroBienestarService;
import com.upc.backend.services.SesionService;

import com.upc.backend.services.DispositivoService;

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
    private final AnalisisIaService analisisIaService;
    private final RegistroBienestarService registroBienestarService;
    private final PacienteService pacienteService;

    public PacienteController(
            SesionService sesionService,
            AnalisisIaService analisisIaService,
            RegistroBienestarService registroBienestarService,
            DispositivoService dispositivoService,
            PacienteService pacienteService) {

        this.sesionService = sesionService;
        this.analisisIaService = analisisIaService;
        this.registroBienestarService = registroBienestarService;
        this.dispositivoService = dispositivoService;
        this.pacienteService = pacienteService;
    }

    @PostMapping("/{pacienteId}/registros-bienestar")
    @PreAuthorize("@autorizacionService.esMismoUsuario(#pacienteId)")
    // HU09 | END08: registra el bienestar informado por el propio paciente.
    public ResponseEntity<RegistroBienestarResponseDTO> registrarBienestar(
            @PathVariable Long pacienteId,
            @Valid @RequestBody RegistrarBienestarRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registroBienestarService.registrar(pacienteId, request));
    }

    @GetMapping("/{pacienteId}/registros-bienestar")
    @PreAuthorize("@autorizacionService.puedeAccederPaciente(#pacienteId)")
    // HU10 y HU25 | END09: consulta los registros de bienestar del paciente con filtros opcionales.
    public ResponseEntity<List<RegistroBienestarDetalleDTO>> listarBienestar(
            @PathVariable Long pacienteId,
            @RequestParam(required = false) OffsetDateTime fechaDesde,
            @RequestParam(required = false) OffsetDateTime fechaHasta) {
        return ResponseEntity.ok(registroBienestarService.listar(pacienteId, fechaDesde, fechaHasta));
    }

    @GetMapping("/{pacienteId}/inicio")
    @PreAuthorize("@autorizacionService.esMismoUsuario(#pacienteId)")
    // HU08 | END16: compone el resumen mostrado al paciente al ingresar.
    public ResponseEntity<InicioPacienteDTO> consultarInicio(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(pacienteService.consultarInicio(pacienteId));
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

    //HU14: Consultar dispositivo asignado al paciente
    @GetMapping("/{pacienteId}/dispositivo")
    @PreAuthorize("@autorizacionService.puedeAccederPaciente(#pacienteId)")
    public ResponseEntity<ConsultaDispositivoAsignado> consultarDispositivoAsignado(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(dispositivoService.consultarDispositivoAsignado(pacienteId));
    }

    @PostMapping("/{pacienteId}/analisis-ia")
    @PreAuthorize("hasRole('PSICOLOGO') and @autorizacionService.puedeAccederPaciente(#pacienteId)")
    // HU31 | END25: genera el análisis bajo demanda sin almacenar el resultado.
    public ResponseEntity<AnalisisIaResponseDTO> generarAnalisis(
            @PathVariable Long pacienteId,
            @Valid @RequestBody GenerarAnalisisIaRequestDTO request) {
            return ResponseEntity.ok(analisisIaService.generar(pacienteId, request));
    }

    //HU15: Renombrar dispositivo asignado al paciente
    @PatchMapping("/{pacienteId}/dispositivo/alias")
    @PreAuthorize("@autorizacionService.esMismoUsuario(#pacienteId)")
    public ResponseEntity<ConsultaDispositivoAsignado> renombrarDispositivoAsignado(
            @PathVariable Long pacienteId,
            @Valid @RequestBody RenombrarDispositivoDesignado request) {
        return ResponseEntity.ok(dispositivoService.renombrarDispositivoAsignado(request.getAlias(), pacienteId));
    }
}

