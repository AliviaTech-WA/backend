package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import com.upc.backend.enums.EstadoConfirmacionEventos;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
// HU16 | END14: confirma el estado y la fecha de respuesta del evento.
public class EventoWearableResponseDTO {
    private Long eventoId;
    private EstadoConfirmacionEventos estadoConfirmacion;
    private Long tiempoVariacion; //tiempo de variacion de tus datos de la medicion del wearable, en minutos, para el evento que se esta respondiendo.
    private String comentario;
    private OffsetDateTime fechaRespuesta;
}
