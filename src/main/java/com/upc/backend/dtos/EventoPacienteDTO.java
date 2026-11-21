package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU12, HU16, HU17 y HU27: datos del evento para el paciente y su psicologo.
public class EventoPacienteDTO {
    private Long eventoId;
    private OffsetDateTime fechaHoraInicio;
    private OffsetDateTime fechaHoraFin;
    private Long duracionMinutos;
    private Short frecuenciaCardiacaMaxima;
    private Double horasSuenoPrevio;
    private String nivelActividad;
    private LocalDate fechaResumenActividad;
    private String estadoConfirmacion;
    private String contextoPaciente;
    private Long tiempoVariacion;
    private String comentario;
    private OffsetDateTime fechaRespuesta;
    private String descripcionVariacion;
}
