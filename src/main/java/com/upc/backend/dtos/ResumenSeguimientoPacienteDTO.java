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
// HU24 | END23: resumen clínico permitido para el psicólogo responsable.
public class ResumenSeguimientoPacienteDTO {
    private Long pacienteId;
    private Long seguimientoId;
    private String nombreCompleto;
    private String dni;
    private LocalDate fechaNacimiento;
    private String estadoSeguimiento;
    private LocalDate fechaInicioSeguimiento;
    private OffsetDateTime fechaUltimaRevision;
    private NovedadesDTO novedades;
    private OffsetDateTime ultimaSesion;
    private OffsetDateTime ultimoEpisodio;
    private List<UltimoRegistroDTO> ultimosRegistros;
    private ResumenPeriodoDTO resumenPeriodo;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NovedadesDTO {
        private long registros;
        private long lecturasRelevantes;
        private long episodiosConfirmados;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UltimoRegistroDTO {
        private OffsetDateTime fechaHora;
        private Short nivelAnsiedad;
        private String estadoAnimo;
        private String comentario;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenPeriodoDTO {
        private Double ansiedadPromedio;
        private Double horasSuenoPromedio;
        private List<DiaResumenDTO> dias;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaResumenDTO {
        private LocalDate fecha;
        private Double ansiedadPromedio;
        private Double horasSueno;
    }
}
