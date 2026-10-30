package com.upc.backend.serviceimpl;

import com.upc.backend.entities.Dispositivo;
import com.upc.backend.entities.EventoWearable;
import com.upc.backend.entities.LecturaWearable;
import com.upc.backend.repositories.EventoWearableRepositorio;
import com.upc.backend.repositories.LecturaWearableRepositorio;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
// HU13: centraliza la deteccion de episodios para END10 y para el simulador programado.
public class WearableEventoDetector {
    private static final short UMBRAL_ALTO = 110;
    private static final String DESCRIPCION =
            "Variacion de frecuencia cardiaca superior a 110 lpm; pendiente de confirmacion.";

    private final LecturaWearableRepositorio lecturaRepositorio;
    private final EventoWearableRepositorio eventoRepositorio;

    public WearableEventoDetector(LecturaWearableRepositorio lecturaRepositorio,
                                  EventoWearableRepositorio eventoRepositorio) {
        this.lecturaRepositorio = lecturaRepositorio;
        this.eventoRepositorio = eventoRepositorio;
    }

    // HU13: crea un evento al iniciar el tramo alto y solo extiende el mismo mientras permanezca alto.
    public void procesar(Dispositivo dispositivo, List<LecturaWearable> lecturasNuevas) {
        if (lecturasNuevas.isEmpty()) {
            return;
        }
        List<LecturaWearable> ordenadas = lecturasNuevas.stream()
                .sorted(Comparator.comparing(LecturaWearable::getFechaHora))
                .toList();
        LecturaWearable anterior = lecturaRepositorio
                .findFirstByIdDispositivo_IdAndFechaHoraBeforeOrderByFechaHoraDesc(
                        dispositivo.getId(), ordenadas.getFirst().getFechaHora())
                .orElse(null);
        EventoWearable episodio = resolverEpisodioAbierto(dispositivo, anterior);

        for (LecturaWearable lectura : ordenadas) {
            boolean alta = lectura.getFrecuenciaCardiaca() > UMBRAL_ALTO;
            boolean anteriorAlta = anterior != null
                    && anterior.getFrecuenciaCardiaca() > UMBRAL_ALTO;
            if (alta && (!anteriorAlta || episodio == null)) {
                episodio = crearEvento(dispositivo, lectura);
            } else if (alta) {
                episodio.setFechaHoraFin(lectura.getFechaHora());
                eventoRepositorio.save(episodio);
            } else {
                episodio = null;
            }
            anterior = lectura;
        }
    }

    private EventoWearable resolverEpisodioAbierto(Dispositivo dispositivo,
                                                   LecturaWearable anterior) {
        if (anterior == null || anterior.getFrecuenciaCardiaca() <= UMBRAL_ALTO) {
            return null;
        }
        return eventoRepositorio.findFirstByIdDispositivo_IdOrderByFechaHoraInicioDesc(dispositivo.getId())
                .filter(evento -> !evento.getFechaHoraFin().isBefore(anterior.getFechaHora()))
                .orElse(null);
    }

    private EventoWearable crearEvento(Dispositivo dispositivo, LecturaWearable lectura) {
        EventoWearable evento = new EventoWearable();
        evento.setIdDispositivo(dispositivo);
        evento.setFechaHoraInicio(lectura.getFechaHora());
        evento.setFechaHoraFin(lectura.getFechaHora());
        evento.setDescripcionVariacion(DESCRIPCION);
        return eventoRepositorio.save(evento);
    }
}
