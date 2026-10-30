package com.upc.backend.serviceimpl;

import com.upc.backend.entities.Dispositivo;
import com.upc.backend.repositories.DispositivoRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Component
@ConditionalOnProperty(name = "app.wearable.simulador.enabled", havingValue = "true", matchIfMissing = true)
// HU11 y HU13: ejecuta automaticamente la simulacion sin exponer un endpoint adicional.
public class WearableSimuladorProgramado {
    private static final Logger log = LoggerFactory.getLogger(WearableSimuladorProgramado.class);
    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");
    private static final String ESTADO_VINCULADA = "ACTIVO";

    private final DispositivoRepositorio dispositivoRepositorio;
    private final WearableSimuladorService simuladorService;

    public WearableSimuladorProgramado(DispositivoRepositorio dispositivoRepositorio,
                                       WearableSimuladorService simuladorService) {
        this.dispositivoRepositorio = dispositivoRepositorio;
        this.simuladorService = simuladorService;
    }

    @Scheduled(cron = "${app.wearable.simulador.lecturas-cron:0 */15 * * * *}", zone = "America/Lima")
    // HU11 y HU13: genera una lectura alineada al cuarto de hora para cada pulsera activa.
    public void generarLecturas() {
        OffsetDateTime ahora = OffsetDateTime.now(ZONA_LIMA);
        OffsetDateTime intervalo = ahora.withMinute((ahora.getMinute() / 15) * 15)
                .withSecond(0).withNano(0);
        for (Dispositivo dispositivo : dispositivosActivos()) {
            try {
                simuladorService.generarLectura(dispositivo.getId(), intervalo);
            } catch (RuntimeException ex) {
                log.warn("HU11 | HU13: no se pudo simular la lectura del dispositivo {}: {}",
                        dispositivo.getId(), ex.getClass().getSimpleName());
            }
        }
    }

    @Scheduled(cron = "${app.wearable.simulador.resumenes-cron:0 5 0 * * *}", zone = "America/Lima")
    // HU11: genera el resumen del dia local anterior y conserva una sola fila por fecha.
    public void generarResumenes() {
        LocalDate fecha = LocalDate.now(ZONA_LIMA).minusDays(1);
        for (Dispositivo dispositivo : dispositivosActivos()) {
            try {
                simuladorService.generarResumen(dispositivo.getId(), fecha);
            } catch (RuntimeException ex) {
                log.warn("HU11: no se pudo simular el resumen del dispositivo {}: {}",
                        dispositivo.getId(), ex.getClass().getSimpleName());
            }
        }
    }

    private Iterable<Dispositivo> dispositivosActivos() {
        return dispositivoRepositorio.findByFechaRetiroIsNullAndEstadoIgnoreCase(ESTADO_VINCULADA);
    }

}
