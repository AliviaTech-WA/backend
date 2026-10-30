package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU08 | END16: resumen compuesto de la pantalla de inicio del paciente.
public class InicioPacienteDTO {
    private String nombres;
    private long notificacionesPendientes;
    private UltimoRegistroDTO ultimoRegistro;
    private EventoPendienteDTO eventoPendiente;
    private UltimaRecomendacionDTO ultimaRecomendacion;
    private UltimaSesionDTO ultimaSesion;
    private DispositivoInicioDTO dispositivo;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UltimoRegistroDTO { private OffsetDateTime fechaHora; }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class EventoPendienteDTO { private Long eventoId; private OffsetDateTime fechaHoraInicio; }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UltimaRecomendacionDTO { private String texto; private String psicologo; private LocalDate fecha; }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UltimaSesionDTO {
        private Long sesionId; private OffsetDateTime fechaSesion; private boolean tieneResumen; private boolean tieneRecomendacion;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class DispositivoInicioDTO {
        private String alias; private String modelo; private String estado; private OffsetDateTime fechaUltimaSincronizacion;
    }
}
