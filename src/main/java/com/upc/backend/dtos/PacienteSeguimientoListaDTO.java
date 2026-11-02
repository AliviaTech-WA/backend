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
// HU23 | END22: fila resumida de un paciente dentro de la lista de seguimiento.
public class PacienteSeguimientoListaDTO {
    private Long pacienteId;
    private Long seguimientoId;
    private String nombres;
    private String apellidos;
    private String dni;
    private String novedad;
    private OffsetDateTime fechaUltimoRegistro;
    private OffsetDateTime fechaUltimoEpisodio;
}
