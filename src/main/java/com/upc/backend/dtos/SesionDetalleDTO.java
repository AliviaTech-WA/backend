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
// HU20 y HU30 | END20: detalle cuya nota interna se controla según el rol.
public class SesionDetalleDTO {
    private Long sesionId;
    private OffsetDateTime fechaSesion;
    private String notaInterna;
    private String resumenCompartido;
    private String recomendacionCompartida;
}
