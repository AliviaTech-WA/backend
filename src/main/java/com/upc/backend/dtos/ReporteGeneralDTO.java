package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU32 y HU33 | END26 y END27: indicadores consolidados reutilizados en consulta y PDF.
public class ReporteGeneralDTO {
    private long pacientesActivos;
    private long registrosRecibidos;
    private long episodiosConfirmados;
    private double promedioRegistrosPorPaciente;
    private List<ActividadSemanalDTO> actividadSemanal;
    private List<PacienteActividadDTO> pacientesMayorActividad;
    private long pacientesConEpisodios;
    private CoberturaDTO cobertura;
    private List<PacientesActivosSemanaDTO> pacientesActivosPorSemana;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ActividadSemanalDTO { private int semana; private long registros; private long episodios; }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PacienteActividadDTO {
        private Long pacienteId; private String nombreCompleto; private long registros; private long episodios;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CoberturaDTO { private long pacientesConActividad; private long pacientesActivos; private double porcentaje; }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PacientesActivosSemanaDTO { private int semana; private long cantidad; }
}
