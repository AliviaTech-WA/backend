package com.upc.backend.dtos;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDispositivoAsignado {
    private String nombrePaciente;
    private String codigoDispositivo;
    private String modelo;
    private String estado;
    private String alias;
    private OffsetDateTime fechaAsignacion;
    private OffsetDateTime fechaUltimaSincronizacion;
}