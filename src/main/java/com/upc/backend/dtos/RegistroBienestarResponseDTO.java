package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
// HU09 | END08: confirma el registro de bienestar creado.
public class RegistroBienestarResponseDTO {
    private Long registroId;
    private OffsetDateTime fechaHora;
    private Short nivelAnsiedad;
    private String estadoAnimo;
}
