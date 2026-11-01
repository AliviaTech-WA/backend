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
// HU22 | END21: indicadores consolidados del dashboard del psicólogo.
public class DashboardPsicologoDTO {
    private long pacientesActivos;
    private long invitacionesPendientes;
    private long tiempoMedioDesdeRevisionDias;
    private List<UltimaNovedadDTO> ultimasNovedades;
    private List<ActividadDiaDTO> actividadUltimos7Dias;
    private DistribucionNovedadesDTO distribucionNovedades;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UltimaNovedadDTO {
        private Long pacienteId;
        private String nombreCompleto;
        private String tipo;
        private OffsetDateTime fechaHora;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActividadDiaDTO {
        private LocalDate fecha;
        private long registrosManuales;
        private long episodiosConfirmados;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistribucionNovedadesDTO {
        private long conNuevosEpisodios;
        private long conNuevosRegistros;
        private long sinNovedades;
    }
}
