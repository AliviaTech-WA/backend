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
// HU19 y HU29 | END19: resumen compartido de cada sesión del historial.
public class SesionResumenDTO {
    private Long sesionId;
    private OffsetDateTime fechaSesion;
    private String resumenCompartido;
    private String recomendacionCompartida;

}
