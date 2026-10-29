package com.upc.backend.serviceimpl;

import com.upc.backend.entities.Dispositivo;
import com.upc.backend.entities.LecturaWearable;
import com.upc.backend.entities.ResumenWearableDiario;
import com.upc.backend.exceptions.ResourceNotFoundException;
import com.upc.backend.repositories.DispositivoRepositorio;
import com.upc.backend.repositories.LecturaWearableRepositorio;
import com.upc.backend.repositories.ResumenWearableDiarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

@Service
// HU11 y HU13: genera datos automaticos del wearable sin crear pacientes ni registros manuales.
public class WearableSimuladorService {
    private static final int HORA_INICIO_DIA = 6;
    private static final int HORA_INICIO_NOCHE = 22;
    private static final double PROBABILIDAD_ALTA_DIA = 0.02;
    private static final double PROBABILIDAD_ALTA_NOCHE = 0.005;

    private final DispositivoRepositorio dispositivoRepositorio;
    private final LecturaWearableRepositorio lecturaRepositorio;
    private final ResumenWearableDiarioRepositorio resumenRepositorio;
    private final WearableEventoDetector eventoDetector;
    private final Random random;

    @Autowired
    public WearableSimuladorService(DispositivoRepositorio dispositivoRepositorio,
                                    LecturaWearableRepositorio lecturaRepositorio,
                                    ResumenWearableDiarioRepositorio resumenRepositorio,
                                    WearableEventoDetector eventoDetector) {
        this(dispositivoRepositorio, lecturaRepositorio, resumenRepositorio, eventoDetector, new Random());
    }

    WearableSimuladorService(DispositivoRepositorio dispositivoRepositorio,
                             LecturaWearableRepositorio lecturaRepositorio,
                             ResumenWearableDiarioRepositorio resumenRepositorio,
                             WearableEventoDetector eventoDetector,
                             Random random) {
        this.dispositivoRepositorio = dispositivoRepositorio;
        this.lecturaRepositorio = lecturaRepositorio;
        this.resumenRepositorio = resumenRepositorio;
        this.eventoDetector = eventoDetector;
        this.random = random;
    }

    @Transactional
    // HU11 y HU13: persiste una lectura del intervalo y aplica la regla de episodio alto.
    public boolean generarLectura(Long dispositivoId, OffsetDateTime fechaHora) {
        Dispositivo dispositivo = buscarActivo(dispositivoId);
        if (lecturaRepositorio.existsByIdDispositivo_IdAndFechaHora(dispositivoId, fechaHora)) {
            return false;
        }
        Short frecuencia = (short) generarFrecuencia(fechaHora);
        LecturaWearable lectura = new LecturaWearable();
        lectura.setIdDispositivo(dispositivo);
        lectura.setFechaHora(fechaHora);
        lectura.setFrecuenciaCardiaca(frecuencia);
        lectura = lecturaRepositorio.save(lectura);
        eventoDetector.procesar(dispositivo, List.of(lectura));
        dispositivo.setFechaUltimaSincronizacion(fechaHora);
        dispositivoRepositorio.save(dispositivo);
        return true;
    }

    @Transactional
    // HU11: crea una sola vez el resumen del dia local anterior usando los campos existentes.
    public boolean generarResumen(Long dispositivoId, LocalDate fecha) {
        Dispositivo dispositivo = buscarActivo(dispositivoId);
        if (resumenRepositorio.findByIdDispositivo_IdAndFecha(dispositivoId, fecha).isPresent()) {
            return false;
        }
        ResumenWearableDiario resumen = new ResumenWearableDiario();
        resumen.setIdDispositivo(dispositivo);
        resumen.setFecha(fecha);
        resumen.setPasos(random.nextInt(1_500, 14_001));
        resumen.setMinutosSueno((short) random.nextInt(300, 541));
        resumenRepositorio.save(resumen);
        return true;
    }

    // HU11 y HU13: aplica una mezcla ponderada y limita siempre el resultado a 60-220 lpm.
    int generarFrecuencia(OffsetDateTime fechaHora) {
        int hora = fechaHora.getHour();
        boolean dia = hora >= HORA_INICIO_DIA && hora < HORA_INICIO_NOCHE;
        double probabilidadAlta = dia ? PROBABILIDAD_ALTA_DIA : PROBABILIDAD_ALTA_NOCHE;
        if (random.nextDouble() < probabilidadAlta) {
            return random.nextInt(111, 221);
        }
        double centro = dia ? 78.0 : 68.0;
        int frecuencia = (int) Math.round(centro + random.nextGaussian() * 8.0);
        return Math.max(60, Math.min(110, frecuencia));
    }

    private Dispositivo buscarActivo(Long dispositivoId) {
        Dispositivo dispositivo = dispositivoRepositorio.findById(dispositivoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el dispositivo con ID " + dispositivoId + "."));
        if (dispositivo.getFechaRetiro() != null || !"VINCULADA".equalsIgnoreCase(dispositivo.getEstado())) {
            throw new IllegalStateException("El dispositivo no esta activo para simulacion.");
        }
        return dispositivo;
    }
}
