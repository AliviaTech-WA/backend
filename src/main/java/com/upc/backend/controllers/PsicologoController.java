package com.upc.backend.controllers;

import com.upc.backend.dtos.ActualizarPsicologoPerfilRequestDTO;
import com.upc.backend.dtos.CrearPsicologoRequestDTO;
import com.upc.backend.dtos.DashboardPsicologoDTO;
import com.upc.backend.dtos.PacienteSeguimientoListaDTO;
import com.upc.backend.dtos.PsicologoIdResponseDTO;
import com.upc.backend.dtos.PsicologoPerfilDTO;
import com.upc.backend.dtos.RegistrarPacienteRequestDTO;
import com.upc.backend.dtos.RegistrarPacienteResponseDTO;
import com.upc.backend.services.PsicologoService;
import com.upc.backend.services.SeguimientoConsultaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/psicologos")
// HU34: gestión del perfil profesional del psicólogo.
public class PsicologoController {
    private final PsicologoService psicologoService;
    private final SeguimientoConsultaService seguimientoConsultaService;

    public PsicologoController(PsicologoService psicologoService,
                               SeguimientoConsultaService seguimientoConsultaService) {
        this.psicologoService = psicologoService;
        this.seguimientoConsultaService = seguimientoConsultaService;
    }

    @PostMapping
    // HU01 | END01: crea la cuenta y el perfil profesional del psicólogo.
    public ResponseEntity<PsicologoIdResponseDTO> crear(
            @Valid @RequestBody CrearPsicologoRequestDTO request) {
        PsicologoIdResponseDTO response = psicologoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

    @PostMapping("/{psicologoId}/pacientes")
    @PreAuthorize("@autorizacionService.puedeAccederPsicologo(#psicologoId)")
    // HU02 | END24: registra al paciente en el seguimiento del psicólogo autenticado y envía la invitación.
    public ResponseEntity<RegistrarPacienteResponseDTO> registrarPaciente(
            @PathVariable Long psicologoId,
            @Valid @RequestBody RegistrarPacienteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(psicologoService.registrarPaciente(psicologoId, request));
    }

    @GetMapping("/{psicologoId}/resumen-seguimiento")
    @PreAuthorize("@autorizacionService.puedeAccederPsicologo(#psicologoId)")
    // HU22 | END21: consulta los indicadores del dashboard sin modificar revisiones.
    public ResponseEntity<DashboardPsicologoDTO> consultarDashboard(@PathVariable Long psicologoId) {
        return ResponseEntity.ok(seguimientoConsultaService.consultarDashboard(psicologoId));
    }

    @GetMapping("/{psicologoId}/pacientes")
    @PreAuthorize("@autorizacionService.puedeAccederPsicologo(#psicologoId)")
    // HU23 | END22: lista los pacientes y sus novedades sin actualizar la última revisión.
    public ResponseEntity<List<PacienteSeguimientoListaDTO>> listarPacientes(
            @PathVariable Long psicologoId,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String novedad,
            @RequestParam(required = false) String orden,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEpisodioDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEpisodioHasta) {
        return ResponseEntity.ok(seguimientoConsultaService.listarPacientes(
                psicologoId, busqueda, novedad, orden, fechaEpisodioDesde, fechaEpisodioHasta));
    }
}
