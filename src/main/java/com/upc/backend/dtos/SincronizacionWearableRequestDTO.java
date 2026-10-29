package com.upc.backend.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU11 y apoyo parcial a HU13 | END10: lote de lecturas y resumen diario recibido del wearable.
public class SincronizacionWearableRequestDTO {
    @NotEmpty
    @Valid
    private List<LecturaDTO> lecturas;
    @NotNull
    @Valid
    private ResumenDiarioDTO resumenDiario;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LecturaDTO {
        @NotNull private OffsetDateTime fechaHora;
        @NotNull @Positive
        private Short frecuenciaCardiaca;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ResumenDiarioDTO {
        @NotNull private LocalDate fecha;
        @NotNull @jakarta.validation.constraints.PositiveOrZero private Integer pasos;
        @NotNull @jakarta.validation.constraints.Min(0) @jakarta.validation.constraints.Max(1440)
        private Short minutosSueno;
    }
}
