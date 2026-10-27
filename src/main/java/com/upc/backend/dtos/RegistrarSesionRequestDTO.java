package com.upc.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU28 | END18: fecha y contenidos interno/compartidos de una sesión.
public class RegistrarSesionRequestDTO {
    @NotNull
    private OffsetDateTime fechaSesion;
    private String notaInterna;
    private String resumenCompartido;
    private String recomendacionCompartida;
}
