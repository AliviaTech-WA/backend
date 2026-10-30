package com.upc.backend.serviceimpl;

import com.upc.backend.dtos.ConsultaDispositivoAsignado;
import com.upc.backend.dtos.SincronizacionWearableRequestDTO;
import com.upc.backend.dtos.SincronizacionWearableResponseDTO;
import com.upc.backend.entities.Dispositivo;
import com.upc.backend.entities.LecturaWearable;
import com.upc.backend.entities.ResumenWearableDiario;
import com.upc.backend.exceptions.ResourceNotFoundException;
import com.upc.backend.repositories.DispositivoRepositorio;
import com.upc.backend.repositories.LecturaWearableRepositorio;
import com.upc.backend.repositories.ResumenWearableDiarioRepositorio;
import com.upc.backend.services.DispositivoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DispositivoServiceImpl implements DispositivoService {
    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");

    private final DispositivoRepositorio dispositivoRepositorio;
    private final LecturaWearableRepositorio lecturaRepositorio;
    private final ResumenWearableDiarioRepositorio resumenRepositorio;
    private final WearableEventoDetector eventoDetector;
    private final ModelMapper modelMapper;

    public DispositivoServiceImpl(DispositivoRepositorio dispositivoRepositorio,
                                  LecturaWearableRepositorio lecturaRepositorio,
                                  ResumenWearableDiarioRepositorio resumenRepositorio,
                                  WearableEventoDetector eventoDetector,
                                  ModelMapper modelMapper) {
        this.dispositivoRepositorio = dispositivoRepositorio;
        this.lecturaRepositorio = lecturaRepositorio;
        this.resumenRepositorio = resumenRepositorio;
        this.eventoDetector = eventoDetector;
        this.modelMapper = modelMapper;
    }

    // Infraestructura compartida de wearable: resuelve un dispositivo o produce una respuesta 404.
    private Dispositivo buscar(Long dispositivoId) {
        return dispositivoRepositorio.findById(dispositivoId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el dispositivo con ID " + dispositivoId + "."));
    }

    @Override
    @Transactional
    // HU11 y apoyo parcial a HU13 | END10: evita lecturas duplicadas por fecha, actualiza el resumen y la última sincronización.
    public SincronizacionWearableResponseDTO sincronizar(Long dispositivoId, SincronizacionWearableRequestDTO request) {
        Dispositivo dispositivo = buscar(dispositivoId);
        OffsetDateTime minimo = request.getLecturas().stream().map(SincronizacionWearableRequestDTO.LecturaDTO::getFechaHora)
                .min(Comparator.naturalOrder()).orElseThrow();
        OffsetDateTime maximo = request.getLecturas().stream().map(SincronizacionWearableRequestDTO.LecturaDTO::getFechaHora)
                .max(Comparator.naturalOrder()).orElseThrow();
        Set<OffsetDateTime> existentes = lecturaRepositorio
                .findByIdDispositivo_IdAndFechaHoraBetween(dispositivoId, minimo, maximo).stream()
                .map(LecturaWearable::getFechaHora).collect(Collectors.toSet());
        List<LecturaWearable> nuevas = request.getLecturas().stream()
                .filter(dto -> !existentes.contains(dto.getFechaHora()))
                .map(dto -> {
                    LecturaWearable lectura = modelMapper.map(dto, LecturaWearable.class);
                    lectura.setIdDispositivo(dispositivo);
                    return lectura;
                }).toList();
        lecturaRepositorio.saveAll(nuevas);
        eventoDetector.procesar(dispositivo, nuevas);

        SincronizacionWearableRequestDTO.ResumenDiarioDTO resumenDto = request.getResumenDiario();
        ResumenWearableDiario resumen = resumenRepositorio
                .findByIdDispositivo_IdAndFecha(dispositivoId, resumenDto.getFecha())
                .orElseGet(ResumenWearableDiario::new);
        resumen.setIdDispositivo(dispositivo);
        resumen.setFecha(resumenDto.getFecha());
        resumen.setPasos(resumenDto.getPasos());
        resumen.setMinutosSueno(resumenDto.getMinutosSueno());
        resumenRepositorio.save(resumen);

        OffsetDateTime ahora = OffsetDateTime.now(ZONA_LIMA);
        dispositivo.setFechaUltimaSincronizacion(ahora);
        dispositivoRepositorio.save(dispositivo);
        return new SincronizacionWearableResponseDTO(nuevas.size(), true, ahora);
    }
    // HU14: Consultar dispositivo asignado
    @Override
    public ConsultaDispositivoAsignado consultarDispositivoAsignado(Long pacienteId) {

        Dispositivo dispositivo = dispositivoRepositorio
                .findFirstByIdPaciente_IdAndEstadoIgnoreCaseAndFechaRetiroIsNull(
                        pacienteId, "ACTIVO")
                .orElseThrow(() -> new RuntimeException(
                        "El paciente no tiene un dispositivo asignado."
                ));

        String nombrePaciente = dispositivo.getIdPaciente()
                .getUsuario()
                .getNombres();

        ConsultaDispositivoAsignado dto = new ConsultaDispositivoAsignado();

        dto.setNombrePaciente(nombrePaciente);
        dto.setCodigoDispositivo(dispositivo.getCodigoDispositivo());
        dto.setModelo(dispositivo.getModelo());
        dto.setEstado(dispositivo.getEstado());
        dto.setAlias(dispositivo.getAlias());
        dto.setFechaAsignacion(dispositivo.getFechaAsignacion());
        dto.setFechaUltimaSincronizacion(dispositivo.getFechaUltimaSincronizacion());

        return dto;
    }

    // HU15: Renombrar dispositivo asignado
    @Override
    @Transactional
    public ConsultaDispositivoAsignado renombrarDispositivoAsignado(
            String alias, Long pacienteId) {

        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("El alias no puede estar vacío.");
        }

        String aliasLimpio = alias.trim();

        if (aliasLimpio.length() > 100) {
            throw new IllegalArgumentException(
                    "El alias no puede superar 100 caracteres.");
        }

        Dispositivo dispositivo = dispositivoRepositorio
                .findFirstByIdPaciente_IdAndEstadoIgnoreCaseAndFechaRetiroIsNull(
                        pacienteId, "ACTIVO")
                .orElseThrow(() -> new RuntimeException(
                        "El paciente no tiene un dispositivo asignado."
                ));

        dispositivo.setAlias(aliasLimpio);

        Dispositivo actualizado = dispositivoRepositorio.save(dispositivo);

        ConsultaDispositivoAsignado dto = new ConsultaDispositivoAsignado();

        dto.setNombrePaciente(actualizado.getIdPaciente().getUsuario().getNombres());
        dto.setCodigoDispositivo(actualizado.getCodigoDispositivo());
        dto.setModelo(actualizado.getModelo());
        dto.setEstado(actualizado.getEstado());
        dto.setAlias(actualizado.getAlias());
        dto.setFechaAsignacion(actualizado.getFechaAsignacion());
        dto.setFechaUltimaSincronizacion(actualizado.getFechaUltimaSincronizacion());

        return dto;
    }
}
