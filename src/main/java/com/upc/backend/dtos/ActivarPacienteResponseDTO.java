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
// HU03 | END02: identifica la cuenta activada y el instante efectivo de activación.
public class ActivarPacienteResponseDTO {

    private Long pacienteId;
    private OffsetDateTime fechaActivacion;
}
