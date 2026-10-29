package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU11 | END10: resultado de persistir una sincronización del wearable.
public class SincronizacionWearableResponseDTO {
    private int lecturasRegistradas;
    private boolean resumenDiarioActualizado;
    private OffsetDateTime fechaUltimaSincronizacion;
}
