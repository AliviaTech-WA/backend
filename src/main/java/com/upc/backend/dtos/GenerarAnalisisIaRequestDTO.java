package com.upc.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU31 | END25: fuente y periodo que el psicólogo desea analizar.
public class GenerarAnalisisIaRequestDTO {
    @NotBlank
    @Pattern(regexp = "REGISTROS|WEARABLE|INTEGRADO",
            message = "debe ser REGISTROS, WEARABLE o INTEGRADO")
    private String fuente;

    @NotNull
    private OffsetDateTime periodoDesde;

    @NotNull
    private OffsetDateTime periodoHasta;
}
