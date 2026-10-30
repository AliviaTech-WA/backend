package com.upc.backend.services;

import com.upc.backend.dtos.EventoWearableResponseDTO;
import com.upc.backend.enums.EstadoConfirmacionEventos;
import java.util.List;

// HU16 y apoyo a HU13: contrato para responder eventos wearable ya existentes.
public interface EventoWearableService {

    EventoWearableResponseDTO responderEventoWearable(Long pacienteId, Long eventoId, EstadoConfirmacionEventos estadoConfirmacion, String contexto);

    List<EventoWearableResponseDTO> obtenerEventosRespondidos(Long pacienteId);

    List<EventoWearableResponseDTO> obtenerEventosPendientes(Long pacienteId);
}
