package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU02 | END24: confirma las entidades creadas y el estado pendiente de activación.
public class RegistrarPacienteResponseDTO {

    private Long pacienteId;
    private Long seguimientoId;
    private Long dispositivoId;
    private String estadoCuenta;
    private boolean invitacionGenerada;
}
