package com.upc.backend.serviceimpl;

import com.upc.backend.dtos.InicioPacienteDTO;
import com.upc.backend.entities.*;
import com.upc.backend.exceptions.ResourceNotFoundException;
import com.upc.backend.repositories.*;
import com.upc.backend.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteServiceImpl implements PacienteService {

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private EventoWearableRepositorio eventoRepositorio;

    @Autowired
    private RegistroBienestarRepositorio registroRepositorio;

    @Autowired
    private SeguimientoRepositorio seguimientoRepositorio;

    @Autowired
    private SesionRepositorio sesionRepositorio;

    @Autowired
    private DispositivoRepositorio dispositivoRepositorio;

    @Override
    @Transactional(readOnly = true)
    // HU08 | END16: reúne el último registro, evento pendiente, sesión, recomendación y dispositivo.
    public InicioPacienteDTO consultarInicio(Long pacienteId) {
        Paciente paciente = buscarPaciente(pacienteId);
        List<RegistroBienestar> registros = registroRepositorio.findByIdPaciente_IdOrderByFechaHoraDesc(pacienteId);
        Optional<EventoWearable> pendiente = eventoRepositorio
                .findFirstByIdDispositivo_IdPaciente_IdAndEstadoConfirmacionOrderByFechaHoraInicioDesc(
                        pacienteId, "PENDIENTE");
        long cantidadPendientes = eventoRepositorio
                .countByIdDispositivo_IdPaciente_IdAndEstadoConfirmacion(pacienteId, "PENDIENTE");
        Optional<Seguimiento> seguimiento = seguimientoRepositorio
                .findFirstByIdPaciente_IdAndFechaFinIsNull(pacienteId);
        Optional<Sesion> ultimaSesion = seguimiento.flatMap(s ->
                sesionRepositorio.findFirstByIdSeguimiento_IdOrderByFechaSesionDesc(s.getId()));
        Optional<Dispositivo> dispositivo = dispositivoRepositorio
                .findFirstByIdPaciente_IdAndFechaRetiroIsNull(pacienteId);

        InicioPacienteDTO dto = new InicioPacienteDTO();
        dto.setNombres(paciente.getUsuario().getNombres());
        dto.setNotificacionesPendientes(cantidadPendientes);
        if (!registros.isEmpty()) {
            dto.setUltimoRegistro(new InicioPacienteDTO.UltimoRegistroDTO(registros.get(0).getFechaHora()));
        }
        pendiente.ifPresent(evento -> dto.setEventoPendiente(
                new InicioPacienteDTO.EventoPendienteDTO(evento.getId(), evento.getFechaHoraInicio())));
        ultimaSesion.ifPresent(sesion -> {
            dto.setUltimaSesion(new InicioPacienteDTO.UltimaSesionDTO(
                    sesion.getId(), sesion.getFechaSesion(),
                    sesion.getResumenCompartido() != null, sesion.getRecomendacionCompartida() != null));
            if (sesion.getRecomendacionCompartida() != null) {
                Usuario psicologo = sesion.getIdSeguimiento().getIdPsicologo().getUsuario();
                dto.setUltimaRecomendacion(new InicioPacienteDTO.UltimaRecomendacionDTO(
                        sesion.getRecomendacionCompartida(),
                        psicologo.getNombres() + " " + psicologo.getApellidos(),
                        sesion.getFechaSesion().toLocalDate()));
            }
        });
        dispositivo.ifPresent(d -> dto.setDispositivo(new InicioPacienteDTO.DispositivoInicioDTO(
                d.getAlias(), d.getModelo(), "VINCULADA", d.getFechaUltimaSincronizacion())));
        return dto;
    }

    // Infraestructura compartida de paciente: centraliza la búsqueda y la respuesta 404.
    private Paciente buscarPaciente(Long pacienteId) {
        return pacienteRepositorio.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el paciente con ID " + pacienteId + "."));
    }
}
