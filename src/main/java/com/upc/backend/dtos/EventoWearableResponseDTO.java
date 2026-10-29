package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
// HU16 | END14: confirma el estado y la fecha de respuesta del evento.
public class EventoWearableResponseDTO {
    private Long eventoId;
    private String estadoConfirmacion;
    private OffsetDateTime fechaRespuesta;
}
