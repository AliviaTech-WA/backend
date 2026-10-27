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
// HU28 | END18: identifica la sesión recién registrada y sus fechas.
public class SesionCreadaDTO {
    private Long sesionId;
    private OffsetDateTime fechaSesion;
    private OffsetDateTime fechaRegistro;

}
