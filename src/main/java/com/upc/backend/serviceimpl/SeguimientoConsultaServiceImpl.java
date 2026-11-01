package com.upc.backend.serviceimpl;

import com.upc.backend.dtos.DashboardPsicologoDTO;
import com.upc.backend.entities.EventoWearable;
import com.upc.backend.entities.Paciente;
import com.upc.backend.entities.RegistroBienestar;
import com.upc.backend.entities.ResumenWearableDiario;
import com.upc.backend.entities.Seguimiento;
import com.upc.backend.enums.EstadoConfirmacionEventos;
import com.upc.backend.exceptions.ResourceNotFoundException;
import com.upc.backend.repositories.EventoWearableRepositorio;
import com.upc.backend.repositories.LecturaWearableRepositorio;
import com.upc.backend.repositories.PsicologoRepositorio;
import com.upc.backend.repositories.RegistroBienestarRepositorio;
import com.upc.backend.repositories.ResumenWearableDiarioRepositorio;
import com.upc.backend.repositories.SeguimientoRepositorio;
import com.upc.backend.services.SeguimientoConsultaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
// HU22-HU24 | END21-END23: centraliza novedades, actividad y métricas del seguimiento.
public class SeguimientoConsultaServiceImpl implements SeguimientoConsultaService {

    private static final ZoneId ZONA_NEGOCIO = ZoneId.of("America/Lima");
    // Un episodio confirmado es un evento que el paciente respondió con SI.
    private static final EstadoConfirmacionEventos CONFIRMADO = EstadoConfirmacionEventos.SI;
    private static final String NUEVOS_EPISODIOS = "NUEVOS_EPISODIOS";
    private static final String NUEVOS_REGISTROS = "NUEVOS_REGISTROS";
    private static final String SIN_NOVEDADES = "SIN_NOVEDADES";

    private final PsicologoRepositorio psicologoRepositorio;
    private final SeguimientoRepositorio seguimientoRepositorio;
    private final RegistroBienestarRepositorio registroRepositorio;
    private final EventoWearableRepositorio eventoRepositorio;
    private final LecturaWearableRepositorio lecturaRepositorio;
    private final ResumenWearableDiarioRepositorio resumenDiarioRepositorio;

    public SeguimientoConsultaServiceImpl(PsicologoRepositorio psicologoRepositorio,
                                           SeguimientoRepositorio seguimientoRepositorio,
                                           RegistroBienestarRepositorio registroRepositorio,
                                           EventoWearableRepositorio eventoRepositorio,
                                           LecturaWearableRepositorio lecturaRepositorio,
                                           ResumenWearableDiarioRepositorio resumenDiarioRepositorio) {
        this.psicologoRepositorio = psicologoRepositorio;
        this.seguimientoRepositorio = seguimientoRepositorio;
        this.registroRepositorio = registroRepositorio;
        this.eventoRepositorio = eventoRepositorio;
        this.lecturaRepositorio = lecturaRepositorio;
        this.resumenDiarioRepositorio = resumenDiarioRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    // HU22 | END21: consolida el dashboard sin avanzar la revisión de ningún paciente.
    public DashboardPsicologoDTO consultarDashboard(Long psicologoId) {
        validarPsicologo(psicologoId);
        OffsetDateTime ahora = OffsetDateTime.now(ZONA_NEGOCIO);
        List<EstadoPaciente> estados = seguimientoRepositorio
                .findByIdPsicologo_IdAndFechaFinIsNull(psicologoId).stream()
                .map(seguimiento -> calcularEstado(seguimiento, ahora, true, false))
                .toList();

        long pendientes = estados.stream()
                .filter(estado -> estado.paciente().getUsuario().getFechaActivacion() == null)
                .count();
        long promedioRevision = estados.isEmpty() ? 0L : Math.round(estados.stream()
                .mapToLong(estado -> diasDesdeRevision(estado, ahora)).average().orElse(0.0));

        List<DashboardPsicologoDTO.UltimaNovedadDTO> novedades = estados.stream()
                .filter(estado -> !SIN_NOVEDADES.equals(estado.novedad()))
                .filter(estado -> estado.fechaNovedad() != null)
                .sorted(Comparator.comparing(EstadoPaciente::fechaNovedad).reversed())
                .map(estado -> new DashboardPsicologoDTO.UltimaNovedadDTO(
                        estado.paciente().getId(), nombreCompleto(estado.paciente()),
                        estado.novedad(), estado.fechaNovedad()))
                .toList();

        long conEpisodios = estados.stream().filter(e -> NUEVOS_EPISODIOS.equals(e.novedad())).count();
        long conRegistros = estados.stream().filter(e -> NUEVOS_REGISTROS.equals(e.novedad())).count();
        long sinNovedades = estados.size() - conEpisodios - conRegistros;

        return new DashboardPsicologoDTO(
                estados.size(), pendientes, promedioRevision, novedades,
                construirActividadSemanal(estados, ahora),
                new DashboardPsicologoDTO.DistribucionNovedadesDTO(
                        conEpisodios, conRegistros, sinNovedades));
    }

    // HU22-HU24: aplica una única definición de revisión anterior y prioridad de novedades.
    private EstadoPaciente calcularEstado(Seguimiento seguimiento,
                                           OffsetDateTime ahora,
                                           boolean incluirPeriodo,
                                           boolean incluirLecturas) {
        Paciente paciente = seguimiento.getIdPaciente();
        Long pacienteId = paciente.getId();
        OffsetDateTime desdeRevision = inicioComparacion(seguimiento);
        List<RegistroBienestar> registrosNuevos = desdeRevision.isBefore(ahora)
                ? registroRepositorio.findByIdPaciente_IdAndFechaHoraBetweenOrderByFechaHoraDesc(
                pacienteId, desdeRevision, ahora).stream()
                .filter(registro -> registro.getFechaHora().isAfter(desdeRevision))
                .toList()
                : List.of();
        OffsetDateTime ultimoRegistro = registroRepositorio
                .findFirstByIdPaciente_IdOrderByFechaHoraDesc(pacienteId)
                .map(RegistroBienestar::getFechaHora).orElse(null);
        List<EventoWearable> episodios = eventoRepositorio
                .findByIdDispositivo_IdPaciente_IdAndEstadoConfirmacionOrderByFechaHoraInicioDesc(
                        pacienteId, CONFIRMADO);
        List<EventoWearable> episodiosNuevos = episodios.stream()
                .filter(evento -> posteriorYNoFuturo(instanteNovedad(evento), desdeRevision, ahora))
                .toList();
        OffsetDateTime ultimoEpisodio = episodios.stream()
                .map(EventoWearable::getFechaHoraInicio)
                .max(Comparator.naturalOrder()).orElse(null);

        String novedad;
        OffsetDateTime fechaNovedad;
        if (!episodiosNuevos.isEmpty()) {
            novedad = NUEVOS_EPISODIOS;
            fechaNovedad = episodiosNuevos.stream().map(this::instanteNovedad)
                    .max(Comparator.naturalOrder()).orElse(null);
        } else if (!registrosNuevos.isEmpty()) {
            novedad = NUEVOS_REGISTROS;
            fechaNovedad = registrosNuevos.stream().map(RegistroBienestar::getFechaHora)
                    .max(Comparator.naturalOrder()).orElse(null);
        } else {
            novedad = SIN_NOVEDADES;
            fechaNovedad = null;
        }

        LocalDate inicioPeriodo = ahora.toLocalDate().minusDays(6);
        OffsetDateTime desdePeriodo = inicioPeriodo.atStartOfDay(ZONA_NEGOCIO).toOffsetDateTime();
        List<RegistroBienestar> registrosPeriodo = incluirPeriodo
                ? registroRepositorio.findByIdPaciente_IdAndFechaHoraBetweenOrderByFechaHoraDesc(
                pacienteId, desdePeriodo, ahora)
                : List.of();
        List<ResumenWearableDiario> resumenesPeriodo = incluirPeriodo
                ? resumenDiarioRepositorio
                .findByIdDispositivo_IdPaciente_IdAndFechaBetweenOrderByFechaAsc(
                        pacienteId, inicioPeriodo, ahora.toLocalDate())
                : List.of();
        long lecturasNuevas = incluirLecturas && desdeRevision.isBefore(ahora)
                ? lecturaRepositorio
                .countByIdDispositivo_IdPaciente_IdAndFechaHoraAfterAndFechaHoraLessThanEqual(
                        pacienteId, desdeRevision, ahora)
                : 0L;

        return new EstadoPaciente(seguimiento, paciente, desdeRevision, novedad, fechaNovedad,
                registrosNuevos.size(), lecturasNuevas, episodiosNuevos.size(), ultimoRegistro,
                ultimoEpisodio, registrosPeriodo, resumenesPeriodo, episodios);
    }

    // HU22 | END21: genera una serie diaria real; los ceros representan ausencia de actividad.
    private List<DashboardPsicologoDTO.ActividadDiaDTO> construirActividadSemanal(
            List<EstadoPaciente> estados, OffsetDateTime ahora) {
        LocalDate hoy = fechaLocal(ahora);
        List<DashboardPsicologoDTO.ActividadDiaDTO> actividad = new ArrayList<>();
        for (int desplazamiento = 6; desplazamiento >= 0; desplazamiento--) {
            LocalDate fecha = hoy.minusDays(desplazamiento);
            long registros = estados.stream().flatMap(e -> e.registrosPeriodo().stream())
                    .filter(registro -> fecha.equals(fechaLocal(registro.getFechaHora()))).count();
            long episodios = estados.stream().flatMap(e -> e.episodiosConfirmados().stream())
                    .filter(evento -> !evento.getFechaHoraInicio().isAfter(ahora))
                    .filter(evento -> fecha.equals(fechaLocal(evento.getFechaHoraInicio()))).count();
            actividad.add(new DashboardPsicologoDTO.ActividadDiaDTO(fecha, registros, episodios));
        }
        return actividad;
    }

    private void validarPsicologo(Long psicologoId) {
        if (!psicologoRepositorio.existsById(psicologoId)) {
            throw new ResourceNotFoundException("No se encontró el psicólogo con ID " + psicologoId + ".");
        }
    }

    private OffsetDateTime inicioComparacion(Seguimiento seguimiento) {
        return seguimiento.getFechaUltimaConsulta() != null
                ? seguimiento.getFechaUltimaConsulta()
                : seguimiento.getFechaInicio().atStartOfDay(ZONA_NEGOCIO).toOffsetDateTime();
    }

    private OffsetDateTime instanteNovedad(EventoWearable evento) {
        return evento.getFechaRespuesta() != null ? evento.getFechaRespuesta() : evento.getFechaHoraInicio();
    }

    private boolean posteriorYNoFuturo(OffsetDateTime fecha,
                                       OffsetDateTime desde,
                                       OffsetDateTime hasta) {
        return fecha != null && fecha.isAfter(desde) && !fecha.isAfter(hasta);
    }

    private long diasDesdeRevision(EstadoPaciente estado, OffsetDateTime ahora) {
        long dias = ChronoUnit.DAYS.between(fechaLocal(estado.desdeRevision()), fechaLocal(ahora));
        return Math.max(0L, dias);
    }

    private LocalDate fechaLocal(OffsetDateTime fecha) {
        return fecha.atZoneSameInstant(ZONA_NEGOCIO).toLocalDate();
    }

    private String nombreCompleto(Paciente paciente) {
        return paciente.getUsuario().getNombres() + " " + paciente.getUsuario().getApellidos();
    }

    // HU22-HU24: resultado interno común; evita repetir reglas entre los tres endpoints.
    private record EstadoPaciente(Seguimiento seguimiento,
                                  Paciente paciente,
                                  OffsetDateTime desdeRevision,
                                  String novedad,
                                  OffsetDateTime fechaNovedad,
                                  long registrosNuevos,
                                  long lecturasNuevas,
                                  long episodiosNuevos,
                                  OffsetDateTime fechaUltimoRegistro,
                                  OffsetDateTime fechaUltimoEpisodio,
                                  List<RegistroBienestar> registrosPeriodo,
                                  List<ResumenWearableDiario> resumenesPeriodo,
                                  List<EventoWearable> episodiosConfirmados) {
    }
}
