package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU27 | END15: episodio confirmado con el contexto que necesita el psicólogo.
public class EventoPacienteDTO {
    private Long eventoId;
    private OffsetDateTime fechaHoraInicio;
    private OffsetDateTime fechaHoraFin;
    private Long duracionMinutos;
    private Short frecuenciaCardiacaMaxima;
    private Double horasSuenoPrevio;
    private String nivelActividad;
    private String estadoConfirmacion;
    private String contextoPaciente;
}
