package com.upc.backend.serviceimpl;

import com.upc.backend.dtos.*;
import com.upc.backend.entities.*;
import com.upc.backend.enums.EstadoConfirmacionEventos;
import com.upc.backend.exceptions.ConflictException;
import com.upc.backend.exceptions.InvalidRequestException;
import com.upc.backend.exceptions.ResourceNotFoundException;
import com.upc.backend.repositories.*;
import com.upc.backend.security.services.AutorizacionService;
import com.upc.backend.services.PacienteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteServiceImpl implements PacienteService {

    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");

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

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ContactoEmergenciaRepositorio contactoRepositorio;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private LecturaWearableRepositorio lecturaRepositorio;

    @Autowired
    private ResumenWearableDiarioRepositorio resumenRepositorio;

    @Autowired
    private AutorizacionService autorizacionService;

    @Override
    @Transactional(readOnly = true)
    // HU08 | END16: reúne el último registro, evento pendiente, sesión, recomendación y dispositivo.
    public InicioPacienteDTO consultarInicio(Long pacienteId) {
        Paciente paciente = buscarPaciente(pacienteId);
        List<RegistroBienestar> registros = registroRepositorio.findByIdPaciente_IdOrderByFechaHoraDesc(pacienteId);
        Optional<EventoWearable> pendiente = eventoRepositorio
                .findFirstByIdDispositivo_IdPaciente_IdAndEstadoConfirmacionIsNullOrderByFechaHoraInicioDesc(
                        pacienteId);
        long cantidadPendientes = eventoRepositorio
                .countByIdDispositivo_IdPaciente_IdAndEstadoConfirmacionIsNull(pacienteId);
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
                d.getAlias(), d.getModelo(), "ACTIVO", d.getFechaUltimaSincronizacion())));
        return dto;
    }

    @Override
    @Transactional
    // HU21 | END29: actualiza correo y teléfono sin modificar los demás datos personales.
    public PacientePerfilActualizadoDTO actualizarPerfil(Long pacienteId, ActualizarPacientePerfilRequestDTO request) {
        validarCamposActualizacion(request);
        Paciente paciente = buscarPaciente(pacienteId);
        Usuario usuario = paciente.getUsuario();

        if (request.getCorreo() != null) {
            boolean correoDuplicado = usuarioRepositorio.findByCorreoIgnoreCase(request.getCorreo())
                    .filter(existente -> !existente.getId().equals(usuario.getId()))
                    .isPresent();
            if (correoDuplicado) {
                throw new ConflictException("El correo ya está asociado a otra cuenta.");
            }
            usuario.setCorreo(request.getCorreo().trim());
        }
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono().trim());
        }

        usuarioRepositorio.save(usuario);
        return new PacientePerfilActualizadoDTO(paciente.getId(), usuario.getCorreo(), usuario.getTelefono());
    }

    @Override
    @Transactional(readOnly = true)
    // HU21 | END28: combina Usuario, Paciente y el contacto de emergencia cuando existe.
    public PacientePerfilDTO buscarPerfil(Long pacienteId) {
        Paciente paciente = buscarPaciente(pacienteId);
        Usuario usuario = paciente.getUsuario();
        PacientePerfilDTO dto = modelMapper.map(usuario, PacientePerfilDTO.class);
        dto.setPacienteId(paciente.getId());
        dto.setFechaNacimiento(paciente.getFechaNacimiento());
        contactoRepositorio.findByIdPaciente_Id(pacienteId).ifPresent(contacto ->
                dto.setContactoEmergencia(modelMapper.map(contacto, ContactoEmergenciaDTO.class)));
        return dto;
    }

    @Override
    @Transactional
    // HU21 | END30: crea el contacto si no existe o actualiza el registro único existente.
    public ContactoEmergenciaResponseDTO guardarContacto(Long pacienteId,
                                                          GuardarContactoEmergenciaRequestDTO request) {
        Paciente paciente = buscarPaciente(pacienteId);
        ContactoEmergencia contacto = contactoRepositorio.findByIdPaciente_Id(pacienteId)
                .orElseGet(ContactoEmergencia::new);
        modelMapper.map(request, contacto);
        contacto.setIdPaciente(paciente);
        contacto = contactoRepositorio.save(contacto);
        ContactoEmergenciaResponseDTO response = modelMapper.map(contacto, ContactoEmergenciaResponseDTO.class);
        response.setContactoId(contacto.getId());
        return response;
    }

    // HU21: exige al menos un campo editable y rechaza cadenas vacías.
    private void validarCamposActualizacion(ActualizarPacientePerfilRequestDTO request) {
        if (request.getCorreo() == null && request.getTelefono() == null) {
            throw new InvalidRequestException("Debe enviar al menos un campo para actualizar.");
        }
        if (request.getCorreo() != null && request.getCorreo().isBlank()) {
            throw new InvalidRequestException("El correo no puede estar vacío.");
        }
        if (request.getTelefono() != null && request.getTelefono().isBlank()) {
            throw new InvalidRequestException("El teléfono no puede estar vacío.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    // HU12, HU16, HU17 y HU27: el paciente ve todos los eventos; el psicologo solo los confirmados.
    public List<EventoPacienteDTO> listarEventosWearable(Long pacienteId) {
        buscarPaciente(pacienteId);
        boolean psicologo = "PSICOLOGO".equals(autorizacionService.principalActual().getRol());
        List<EventoWearable> eventos = psicologo
                ? eventoRepositorio.findByIdDispositivo_IdPaciente_IdAndEstadoConfirmacionOrderByFechaHoraInicioDesc(
                        pacienteId, EstadoConfirmacionEventos.SI)
                : eventoRepositorio.findByIdDispositivo_IdPaciente_IdOrderByFechaHoraInicioDesc(pacienteId);
        return eventos.stream().map(this::convertirEvento).toList();
    }

    // HU27: agrega la frecuencia máxima del episodio y el sueño de la noche anterior.
    private EventoPacienteDTO convertirEvento(EventoWearable evento) {
        List<LecturaWearable> lecturas = lecturaRepositorio.findByIdDispositivo_IdAndFechaHoraBetween(
                evento.getIdDispositivo().getId(), evento.getFechaHoraInicio(), evento.getFechaHoraFin());
        Short maxima = lecturas.stream().map(LecturaWearable::getFrecuenciaCardiaca)
                .max(Short::compareTo).orElse(null);
        LocalDate fechaEventoLima = evento.getFechaHoraInicio()
                .atZoneSameInstant(ZONA_LIMA)
                .toLocalDate();
        ResumenWearableDiario resumenPrevio = resumenRepositorio
                .findByIdDispositivo_IdAndFecha(
                        evento.getIdDispositivo().getId(),
                        fechaEventoLima.minusDays(1))
                .orElse(null);
        Double horasSueno = resumenPrevio == null
                ? null
                : resumenPrevio.getMinutosSueno() / 60.0;
        Long dispositivoId = evento.getIdDispositivo().getId();
        ResumenWearableDiario resumenActividad = resumenRepositorio
                .findByIdDispositivo_IdAndFecha(dispositivoId, fechaEventoLima)
                .orElseGet(() -> resumenRepositorio
                        .findFirstByIdDispositivo_IdAndFechaBeforeOrderByFechaDesc(dispositivoId, fechaEventoLima)
                        .orElse(null));
        Long duracion = Duration.between(evento.getFechaHoraInicio(), evento.getFechaHoraFin()).toMinutes();
        EventoPacienteDTO dto = new EventoPacienteDTO();
        dto.setEventoId(evento.getId());
        dto.setFechaHoraInicio(evento.getFechaHoraInicio());
        dto.setFechaHoraFin(evento.getFechaHoraFin());
        dto.setDuracionMinutos(duracion);
        dto.setTiempoVariacion(duracion);
        dto.setFrecuenciaCardiacaMaxima(maxima);
        dto.setHorasSuenoPrevio(horasSueno);
        if (resumenActividad != null) {
            int pasos = resumenActividad.getPasos();
            dto.setNivelActividad(pasos < 5000 ? "BAJA" : pasos < 10000 ? "MEDIA" : "ALTA");
            dto.setFechaResumenActividad(resumenActividad.getFecha());
        }
        dto.setEstadoConfirmacion(evento.getEstadoConfirmacion() == null
                ? null : evento.getEstadoConfirmacion().name());
        dto.setContextoPaciente(evento.getContextoPaciente());
        dto.setFechaRespuesta(evento.getFechaRespuesta());
        dto.setDescripcionVariacion(evento.getDescripcionVariacion());
        return dto;
    }

    // Infraestructura compartida de paciente: centraliza la búsqueda y la respuesta 404.
    private Paciente buscarPaciente(Long pacienteId) {
        return pacienteRepositorio.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el paciente con ID " + pacienteId + "."));
    }
}
