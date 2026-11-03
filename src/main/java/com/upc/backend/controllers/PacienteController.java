package com.upc.backend.controllers;
import com.upc.backend.dtos.*;
import com.upc.backend.services.AnalisisIaService;
import com.upc.backend.services.PacienteService;
import com.upc.backend.services.RegistroBienestarService;
import com.upc.backend.services.SesionService;
import com.upc.backend.services.SeguimientoConsultaService;
import com.upc.backend.services.EventoWearableService;

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
    private final EventoWearableService eventoWearableService;
    private final SeguimientoConsultaService seguimientoConsultaService;

    public PacienteController(
            SesionService sesionService,
            AnalisisIaService analisisIaService,
            RegistroBienestarService registroBienestarService,
            DispositivoService dispositivoService,
            PacienteService pacienteService,
            EventoWearableService eventoWearableService,
            SeguimientoConsultaService seguimientoConsultaService) {

        this.sesionService = sesionService;
        this.analisisIaService = analisisIaService;
        this.registroBienestarService = registroBienestarService;
        this.dispositivoService = dispositivoService;
        this.pacienteService = pacienteService;
        this.eventoWearableService = eventoWearableService;
        this.seguimientoConsultaService = seguimientoConsultaService;
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

    //HU16: Responder evento wearable del paciente
    @PatchMapping("/{pacienteId}/eventos/{eventoId}/respuesta")
    @PreAuthorize("@autorizacionService.esPacienteDelEvento(#pacienteId)")
    public ResponseEntity<EventoWearableResponseDTO> responderEvento(
            @PathVariable Long pacienteId,
            @PathVariable Long eventoId,
            @Valid @RequestBody EventoWearableRequestDTO request) {

        EventoWearableResponseDTO response = eventoWearableService.responderEventoWearable(
                pacienteId,
                eventoId,
                request.getEstadoConfirmacion(),
                request.getContexto()
        );

        return ResponseEntity.ok(response);
    }
    
    //HU17: Consultar eventos respondidos del paciente
    @GetMapping("/{pacienteId}/eventos/historial")
    @PreAuthorize("@autorizacionService.puedeAccederPaciente(#pacienteId)")
    public ResponseEntity<List<EventoWearableResponseDTO>> obtenerEventosRespondidos(
            @PathVariable Long pacienteId) {

        List<EventoWearableResponseDTO> response = eventoWearableService.obtenerEventosRespondidos(pacienteId);
        return ResponseEntity.ok(response);
    }

    //HU16: Una vez respondido el evento se consulta los eventos pendientes del paciente, para actualizar el front.
    @GetMapping("/{pacienteId}/eventos/pendientes")
    @PreAuthorize("@autorizacionService.esPacienteDelEvento(#pacienteId)")
    public ResponseEntity<List<EventoWearableResponseDTO>> obtenerEventosPendientes(
            @PathVariable Long pacienteId) {

        List<EventoWearableResponseDTO> response = eventoWearableService.obtenerEventosPendientes(pacienteId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{pacienteId}/perfil")
    @PreAuthorize("@autorizacionService.esMismoUsuario(#pacienteId)")
    // HU21 | END28: consulta los datos personales y el contacto de emergencia.
    public ResponseEntity<PacientePerfilDTO> buscarPerfil(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(pacienteService.buscarPerfil(pacienteId));
    }

    @PutMapping("/{pacienteId}/perfil")
    @PreAuthorize("@autorizacionService.esMismoUsuario(#pacienteId)")
    // HU21 | END29: actualiza correo y teléfono del paciente autenticado.
    public ResponseEntity<PacientePerfilActualizadoDTO> actualizarPerfil(
            @PathVariable Long pacienteId,
            @Valid @RequestBody ActualizarPacientePerfilRequestDTO request) {
        return ResponseEntity.ok(pacienteService.actualizarPerfil(pacienteId, request));
    }

    @PutMapping("/{pacienteId}/contacto-emergencia")
    @PreAuthorize("@autorizacionService.esMismoUsuario(#pacienteId)")
    // HU21 | END30: crea o actualiza el contacto de emergencia único del paciente.
    public ResponseEntity<ContactoEmergenciaResponseDTO> guardarContacto(
            @PathVariable Long pacienteId,
            @Valid @RequestBody GuardarContactoEmergenciaRequestDTO request) {
        return ResponseEntity.ok(pacienteService.guardarContacto(pacienteId, request));
    }

    @GetMapping("/{pacienteId}/resumen-seguimiento")
    @PreAuthorize("hasRole('PSICOLOGO') and @autorizacionService.puedeAccederPaciente(#pacienteId)")
    // HU24 | END23: devuelve el resumen al psicólogo y registra la revisión después del cálculo.
    public ResponseEntity<ResumenSeguimientoPacienteDTO> consultarResumenSeguimiento(
            @PathVariable Long pacienteId) {
        return ResponseEntity.ok(seguimientoConsultaService.consultarResumen(pacienteId));
    }

    @GetMapping("/{pacienteId}/eventos/confirmados")
    @PreAuthorize("hasRole('PSICOLOGO') and @autorizacionService.puedeAccederPaciente(#pacienteId)")
    // HU27 | END15: lista los episodios confirmados del paciente para el psicólogo responsable.
    public ResponseEntity<List<EventoPacienteDTO>> listarEpisodiosConfirmados(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(pacienteService.listarEpisodiosConfirmados(pacienteId));
    }
}
