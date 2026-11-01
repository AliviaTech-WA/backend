package com.upc.backend.dtos;

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
// HU26 | END11: métricas, evolución y lecturas recientes consultadas por el psicólogo.
public class DatosWearableDTO {
    private Double frecuenciaCardiacaPromedio;
    private Double horasSuenoPromedio;
    private Double pasosPromedio;
    private List<EvolucionDiaDTO> evolucionUltimos7Dias;
    private List<LecturaRecienteDTO> lecturasRecientes;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class EvolucionDiaDTO {
        private LocalDate fecha;
        private Double frecuenciaCardiacaPromedio;
        private Double horasSueno;
        private Integer pasos;
        private LocalDate fechaResumenWearable;
        private boolean resumenAnterior;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LecturaRecienteDTO {
        private OffsetDateTime fechaHora;
        private Short frecuenciaCardiaca;
    }
}
