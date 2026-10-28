package com.upc.backend.serviceimpl;

import com.upc.backend.dtos.AnalisisIaResponseDTO;
import com.upc.backend.dtos.GenerarAnalisisIaRequestDTO;
import com.upc.backend.entities.EventoWearable;
import com.upc.backend.entities.LecturaWearable;
import com.upc.backend.entities.RegistroBienestar;
import com.upc.backend.entities.ResumenWearableDiario;
import com.upc.backend.entities.Seguimiento;
import com.upc.backend.exceptions.InvalidRequestException;
import com.upc.backend.exceptions.ResourceNotFoundException;
import com.upc.backend.repositories.EventoWearableRepositorio;
import com.upc.backend.repositories.LecturaWearableRepositorio;
import com.upc.backend.repositories.RegistroBienestarRepositorio;
import com.upc.backend.repositories.ResumenWearableDiarioRepositorio;
import com.upc.backend.repositories.SeguimientoRepositorio;
import com.upc.backend.services.AnalisisIaGenerador;
import com.upc.backend.services.AnalisisIaGenerador.DatosAgregados;
import com.upc.backend.services.AnalisisIaGenerador.ResumenDiario;
import com.upc.backend.services.AnalisisIaGenerador.ResumenRegistros;
import com.upc.backend.services.AnalisisIaGenerador.ResumenWearable;
import com.upc.backend.services.AnalisisIaGenerador.ResultadoGenerado;
import com.upc.backend.services.AnalisisIaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
// HU31 | END25: genera un análisis descriptivo por fuente y lo asocia al seguimiento.
public class AnalisisIaServiceImpl implements AnalisisIaService {
    private static final ZoneId ZONA_NEGOCIO = ZoneId.of("America/Lima");
    private static final String REGISTROS = "REGISTROS";
    private static final String WEARABLE = "WEARABLE";
    private static final String INTEGRADO = "INTEGRADO";

    private final SeguimientoRepositorio seguimientoRepositorio;
    private final RegistroBienestarRepositorio registroRepositorio;
    private final LecturaWearableRepositorio lecturaRepositorio;
    private final ResumenWearableDiarioRepositorio resumenDiarioRepositorio;
    private final EventoWearableRepositorio eventoRepositorio;
    private final AnalisisIaGenerador analisisIaGenerador;

    public AnalisisIaServiceImpl(SeguimientoRepositorio seguimientoRepositorio,
                                 RegistroBienestarRepositorio registroRepositorio,
                                 LecturaWearableRepositorio lecturaRepositorio,
                                 ResumenWearableDiarioRepositorio resumenDiarioRepositorio,
                                 EventoWearableRepositorio eventoRepositorio,
                                 AnalisisIaGenerador analisisIaGenerador) {
        this.seguimientoRepositorio = seguimientoRepositorio;
        this.registroRepositorio = registroRepositorio;
        this.lecturaRepositorio = lecturaRepositorio;
        this.resumenDiarioRepositorio = resumenDiarioRepositorio;
        this.eventoRepositorio = eventoRepositorio;
        this.analisisIaGenerador = analisisIaGenerador;
    }

    @Override
    @Transactional
    // HU31 | END25: genera el análisis bajo demanda sin persistir el resultado.
    public AnalisisIaResponseDTO generar(Long pacienteId, GenerarAnalisisIaRequestDTO request) {
        validarPeriodo(request.getPeriodoDesde(), request.getPeriodoHasta());
        Seguimiento seguimiento = seguimientoRepositorio
                .findFirstByIdPaciente_IdAndFechaFinIsNull(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró un seguimiento activo para el paciente con ID " + pacienteId + "."));

        boolean usaRegistros = REGISTROS.equals(request.getFuente()) || INTEGRADO.equals(request.getFuente());
        boolean usaWearable = WEARABLE.equals(request.getFuente()) || INTEGRADO.equals(request.getFuente());
        List<RegistroBienestar> registros = usaRegistros
                ? registroRepositorio.findByIdPaciente_IdAndFechaHoraBetweenOrderByFechaHoraDesc(
                pacienteId, request.getPeriodoDesde(), request.getPeriodoHasta())
                : List.of();
        List<LecturaWearable> lecturas = usaWearable
                ? lecturaRepositorio.findByIdDispositivo_IdPaciente_IdAndFechaHoraBetweenOrderByFechaHoraAsc(
                pacienteId, request.getPeriodoDesde(), request.getPeriodoHasta())
                : List.of();
        LocalDate desde = fechaLocal(request.getPeriodoDesde());
        LocalDate hasta = fechaLocal(request.getPeriodoHasta());
        List<ResumenWearableDiario> resumenes = usaWearable
                ? resumenDiarioRepositorio
                .findByIdDispositivo_IdPaciente_IdAndFechaBetweenOrderByFechaAsc(pacienteId, desde, hasta)
                : List.of();
        List<EventoWearable> eventos = usaWearable
                ? eventoRepositorio.findByIdDispositivo_IdPaciente_IdAndFechaHoraInicioBetween(
                        pacienteId, request.getPeriodoDesde(), request.getPeriodoHasta()).stream()
                .filter(evento -> "CONFIRMADO".equals(evento.getEstadoConfirmacion()))
                .toList()
                : List.of();

        DatosAnalisis datos = new DatosAnalisis(registros, lecturas, resumenes, eventos);
        validarDatosSuficientes(request.getFuente(), datos);
        DatosAgregados datosAgregados = construirDatosAgregados(request, datos);
        ResultadoGenerado resultado = analisisIaGenerador.generar(datosAgregados);

        return new AnalisisIaResponseDTO(
                request.getFuente(),
                resultado.resumen(),
                resultado.coincidencias(),
                resultado.puntosRevision()
        );
    }

    // HU31: evita generar resultados cuando la fuente seleccionada no aporta información en el periodo.
    private void validarDatosSuficientes(String fuente, DatosAnalisis datos) {
        boolean hayRegistros = !datos.registros().isEmpty();
        boolean hayWearable = !datos.lecturas().isEmpty()
                || !datos.resumenes().isEmpty()
                || !datos.eventos().isEmpty();
        boolean suficiente = switch (fuente) {
            case REGISTROS -> hayRegistros;
            case WEARABLE -> hayWearable;
            case INTEGRADO -> hayRegistros && hayWearable;
            default -> false;
        };
        if (!suficiente) {
            throw new InvalidRequestException(
                    "No existe información suficiente para generar el análisis con la fuente seleccionada.");
        }
    }

    // HU31 | END25: reduce las fuentes a métricas anónimas antes de enviarlas al modelo.
    private DatosAgregados construirDatosAgregados(GenerarAnalisisIaRequestDTO request, DatosAnalisis datos) {
        ResumenRegistros registros = datos.registros().isEmpty() ? null : resumirRegistros(datos.registros());
        boolean hayWearable = !datos.lecturas().isEmpty()
                || !datos.resumenes().isEmpty()
                || !datos.eventos().isEmpty();
        ResumenWearable wearable = hayWearable ? resumirWearable(datos) : null;
        return new DatosAgregados(
                request.getFuente(),
                request.getPeriodoDesde(),
                request.getPeriodoHasta(),
                registros,
                wearable,
                resumirPorDia(datos));
    }

    private ResumenRegistros resumirRegistros(List<RegistroBienestar> registros) {
        Map<String, Long> estadosAnimo = registros.stream()
                .map(RegistroBienestar::getEstadoAnimo)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return new ResumenRegistros(
                registros.size(),
                redondear(registros.stream().mapToInt(RegistroBienestar::getNivelAnsiedad)
                        .average().orElse(0.0)),
                registros.stream().mapToInt(RegistroBienestar::getNivelAnsiedad).max().orElse(0),
                registros.stream().filter(registro -> registro.getNivelAnsiedad() >= 7).count(),
                estadosAnimo);
    }

    private ResumenWearable resumirWearable(DatosAnalisis datos) {
        Double frecuenciaPromedio = datos.lecturas().isEmpty() ? null
                : redondear(datos.lecturas().stream()
                .mapToInt(LecturaWearable::getFrecuenciaCardiaca).average().orElse(0.0));
        Integer frecuenciaMaxima = datos.lecturas().stream()
                .map(LecturaWearable::getFrecuenciaCardiaca)
                .mapToInt(Short::intValue)
                .max().stream().boxed().findFirst().orElse(null);
        Double horasSuenoPromedio = datos.resumenes().isEmpty() ? null
                : redondear(datos.resumenes().stream()
                .mapToInt(ResumenWearableDiario::getMinutosSueno).average().orElse(0.0) / 60.0);
        Double pasosPromedio = datos.resumenes().isEmpty() ? null
                : redondear(datos.resumenes().stream()
                .mapToInt(ResumenWearableDiario::getPasos).average().orElse(0.0));
        return new ResumenWearable(
                datos.lecturas().size(),
                frecuenciaPromedio,
                frecuenciaMaxima,
                datos.resumenes().size(),
                horasSuenoPromedio,
                pasosPromedio,
                datos.eventos().size(),
                diasDeEventos(datos.eventos()).size());
    }

    private List<ResumenDiario> resumirPorDia(DatosAnalisis datos) {
        Map<LocalDate, List<RegistroBienestar>> registrosPorDia = datos.registros().stream()
                .collect(Collectors.groupingBy(registro -> fechaLocal(registro.getFechaHora())));
        Map<LocalDate, List<LecturaWearable>> lecturasPorDia = datos.lecturas().stream()
                .collect(Collectors.groupingBy(lectura -> fechaLocal(lectura.getFechaHora())));
        Map<LocalDate, List<ResumenWearableDiario>> resumenesPorDia = datos.resumenes().stream()
                .collect(Collectors.groupingBy(ResumenWearableDiario::getFecha));
        Map<LocalDate, List<EventoWearable>> eventosPorDia = datos.eventos().stream()
                .collect(Collectors.groupingBy(evento -> fechaLocal(evento.getFechaHoraInicio())));

        Set<LocalDate> fechas = new LinkedHashSet<>();
        fechas.addAll(registrosPorDia.keySet());
        fechas.addAll(lecturasPorDia.keySet());
        fechas.addAll(resumenesPorDia.keySet());
        fechas.addAll(eventosPorDia.keySet());

        return fechas.stream().sorted().map(fecha -> {
            List<RegistroBienestar> registros = registrosPorDia.getOrDefault(fecha, List.of());
            List<LecturaWearable> lecturas = lecturasPorDia.getOrDefault(fecha, List.of());
            List<ResumenWearableDiario> resumenes = resumenesPorDia.getOrDefault(fecha, List.of());
            List<EventoWearable> eventos = eventosPorDia.getOrDefault(fecha, List.of());
            return new ResumenDiario(
                    fecha,
                    registros.size(),
                    registros.isEmpty() ? null : redondear(registros.stream()
                            .mapToInt(RegistroBienestar::getNivelAnsiedad).average().orElse(0.0)),
                    lecturas.size(),
                    lecturas.isEmpty() ? null : redondear(lecturas.stream()
                            .mapToInt(LecturaWearable::getFrecuenciaCardiaca).average().orElse(0.0)),
                    lecturas.stream().map(LecturaWearable::getFrecuenciaCardiaca)
                            .mapToInt(Short::intValue).max().stream().boxed().findFirst().orElse(null),
                    resumenes.isEmpty() ? null : redondear(resumenes.stream()
                            .mapToInt(ResumenWearableDiario::getMinutosSueno).average().orElse(0.0) / 60.0),
                    resumenes.isEmpty() ? null : redondear(resumenes.stream()
                            .mapToInt(ResumenWearableDiario::getPasos).average().orElse(0.0)),
                    eventos.size());
        }).toList();
    }

    private void validarPeriodo(OffsetDateTime desde, OffsetDateTime hasta) {
        if (hasta.isBefore(desde)) {
            throw new InvalidRequestException("periodoHasta no puede ser anterior a periodoDesde.");
        }
    }

    private Set<LocalDate> diasDeEventos(List<EventoWearable> eventos) {
        return eventos.stream().map(EventoWearable::getFechaHoraInicio)
                .map(this::fechaLocal).collect(Collectors.toSet());
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private LocalDate fechaLocal(OffsetDateTime fecha) {
        return fecha.atZoneSameInstant(ZONA_NEGOCIO).toLocalDate();
    }

    private record DatosAnalisis(List<RegistroBienestar> registros,
                                 List<LecturaWearable> lecturas,
                                 List<ResumenWearableDiario> resumenes,
                                 List<EventoWearable> eventos) {
    }

}
