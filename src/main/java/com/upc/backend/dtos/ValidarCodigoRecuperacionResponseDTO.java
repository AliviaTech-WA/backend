package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU05 | END05: token temporal de un solo uso para continuar con el restablecimiento.
public class ValidarCodigoRecuperacionResponseDTO {

    private String tokenRestablecimiento;
    private int expiraEnSegundos;
}
