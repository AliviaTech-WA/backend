package com.upc.backend.serviceimpl;

import com.upc.backend.dtos.EventoWearableResponseDTO;
import com.upc.backend.services.EventoWearableService;

import jakarta.persistence.EntityNotFoundException;

import com.upc.backend.entities.EventoWearable;
import com.upc.backend.repositories.EventoWearableRepositorio;
import com.upc.backend.enums.EstadoConfirmacionEventos;


import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service 
public class EventoWearableServiceImpl implements EventoWearableService {

    private final EventoWearableRepositorio eventoWearableRepositorio;

    public EventoWearableServiceImpl(EventoWearableRepositorio eventoWearableRepositorio) {
        this.eventoWearableRepositorio = eventoWearableRepositorio;
    }


    //HU16 y apoyo para HU17: aqui lo que devuelve es un Dto con la informacion actualizada de la respuesta del evento 
    //y devuelve la lista de eventos respondidos del paciente, para que el front pueda actualizar la lista de eventos respondidos.
    @Override
    public EventoWearableResponseDTO responderEventoWearable(Long pacienteId, Long eventoId, EstadoConfirmacionEventos estadoConfirmacion, String contexto) {
        
       EventoWearable evento = eventoWearableRepositorio
            .findByIdAndIdDispositivo_IdPaciente_IdAndEstadoConfirmacionIsNull(eventoId, pacienteId)
            .orElseThrow(() -> new EntityNotFoundException(
                    "El evento con ID " + eventoId + " no existe, ya fue respondido o no pertenece al paciente"));
                    
        evento.setEstadoConfirmacion(estadoConfirmacion);
        evento.setContextoPaciente(contexto);
        evento.setFechaRespuesta(OffsetDateTime.now());

        eventoWearableRepositorio.save(evento);

        EventoWearableResponseDTO EventoresponseDTO = new EventoWearableResponseDTO();
        EventoresponseDTO.setEventoId(evento.getId());
        EventoresponseDTO.setEstadoConfirmacion(evento.getEstadoConfirmacion());
        EventoresponseDTO.setTiempoVariacion(
            (evento.getFechaHoraFin().toEpochSecond() - evento.getFechaHoraInicio().toEpochSecond()) / 60
        );
        EventoresponseDTO.setComentario(evento.getContextoPaciente());
        EventoresponseDTO.setFechaRespuesta(evento.getFechaRespuesta());
        return EventoresponseDTO;
    }

    //HU17: aqui lo que devuelve es la lista de eventos respondidos del paciente, para que el front pueda actualizar la lista de eventos respondidos.
    @Override
    @Transactional(readOnly = true)
    public List<EventoWearableResponseDTO> obtenerEventosRespondidos(Long pacienteId) {
        List<EventoWearable> eventos = eventoWearableRepositorio
                .findByIdDispositivo_IdPaciente_IdAndEstadoConfirmacionIsNotNullOrderByFechaHoraInicioDesc(pacienteId);

        return eventos.stream()
                .map(this::mapearADTO)
                .toList();
    }

    // Método privado para mapear EventoWearable a EventoWearableResponseDTO
    private EventoWearableResponseDTO mapearADTO(EventoWearable evento) {
        Long minutos = null;
        if (evento.getFechaHoraInicio() != null && evento.getFechaHoraFin() != null) {
            minutos = java.time.Duration.between(evento.getFechaHoraInicio(), evento.getFechaHoraFin()).toMinutes();
        }

        return new EventoWearableResponseDTO(
            evento.getId(),
            evento.getEstadoConfirmacion(),
            minutos,
            evento.getContextoPaciente(),
            evento.getFechaRespuesta()
        );
    }
    
    //HU16: aqui lo que devuelve es la lista de eventos pendientes del paciente, para que el front pueda actualizar la lista de eventos pendientes.
    @Override
    @Transactional(readOnly = true)
    public List<EventoWearableResponseDTO> obtenerEventosPendientes(Long pacienteId) {
        List<EventoWearable> eventos = eventoWearableRepositorio
                .findByIdDispositivo_IdPaciente_IdAndEstadoConfirmacionIsNullOrderByFechaHoraInicioDesc(pacienteId);

        return eventos.stream()
                .map(this::mapearADTO)
                .toList();
    }
}
