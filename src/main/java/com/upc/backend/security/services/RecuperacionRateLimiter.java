package com.upc.backend.security.services;

import com.upc.backend.exceptions.RateLimitExceededException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
// HU05 | END04: limitador en memoria para la instancia única actual, sin incorporar Redis.
public class RecuperacionRateLimiter {

    private static final Duration VENTANA_CORREO = Duration.ofSeconds(60);
    private static final Duration VENTANA_IP = Duration.ofMinutes(15);
    private static final int MAXIMO_POR_IP = 5;

    private final Map<String, Instant> ultimaSolicitudPorCorreo = new HashMap<>();
    private final Map<String, Deque<Instant>> solicitudesPorIp = new HashMap<>();

    // HU05 | END04: registra una solicitud solo si respeta ambos límites y limpia entradas vencidas.
    public synchronized void verificarYRegistrar(String correoNormalizado, String direccionIp) {
        Instant ahora = Instant.now();
        limpiarVencidos(ahora);

        Instant ultimaSolicitud = ultimaSolicitudPorCorreo.get(correoNormalizado);
        if (ultimaSolicitud != null && ultimaSolicitud.plus(VENTANA_CORREO).isAfter(ahora)) {
            throw new RateLimitExceededException(
                    "Debes esperar 60 segundos antes de solicitar otro código para este correo.");
        }

        Deque<Instant> solicitudes = solicitudesPorIp.computeIfAbsent(direccionIp, key -> new ArrayDeque<>());
        if (solicitudes.size() >= MAXIMO_POR_IP) {
            throw new RateLimitExceededException(
                    "Se alcanzó el límite de solicitudes desde esta dirección IP. Intenta más tarde.");
        }

        ultimaSolicitudPorCorreo.put(correoNormalizado, ahora);
        solicitudes.addLast(ahora);
    }

    // HU05 | END04: evita que el limitador acumule correos o direcciones IP fuera de sus ventanas.
    private void limpiarVencidos(Instant ahora) {
        ultimaSolicitudPorCorreo.entrySet().removeIf(entry ->
                !entry.getValue().plus(VENTANA_CORREO).isAfter(ahora));

        Iterator<Map.Entry<String, Deque<Instant>>> iterator = solicitudesPorIp.entrySet().iterator();
        while (iterator.hasNext()) {
            Deque<Instant> solicitudes = iterator.next().getValue();
            while (!solicitudes.isEmpty()
                    && !solicitudes.peekFirst().plus(VENTANA_IP).isAfter(ahora)) {
                solicitudes.removeFirst();
            }
            if (solicitudes.isEmpty()) {
                iterator.remove();
            }
        }
    }
}
