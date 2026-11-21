package com.upc.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ActualizarSesionRequestDTO {
    @NotNull
    private OffsetDateTime fechaSesion;
    private String notaInterna;
    private String resumenCompartido;
    private String recomendacionCompartida;
}
