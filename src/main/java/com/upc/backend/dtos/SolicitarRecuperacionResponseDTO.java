package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU05 | END04: respuesta uniforme que evita revelar si el correo corresponde a una cuenta habilitada.
public class SolicitarRecuperacionResponseDTO {

    private UUID recuperacionId;
    private String mensaje;
    private int expiraEnSegundos;
}
