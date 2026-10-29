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
// HU10 y HU25 | END09: detalle de cada registro consultado por paciente o psicólogo.
public class RegistroBienestarDetalleDTO {
    private Long registroId;
    private OffsetDateTime fechaHora;
    private Short nivelAnsiedad;
    private String estadoAnimo;
    private String comentario;
}
