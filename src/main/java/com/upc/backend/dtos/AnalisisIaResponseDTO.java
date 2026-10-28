package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU31 | END25: resultado generado bajo demanda sin persistencia.
public class AnalisisIaResponseDTO {
    private String fuente;
    private String resumen;
    private String coincidencias;
    private String puntosRevision;
}
