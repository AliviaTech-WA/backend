package com.upc.backend.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU04 | END03: identidad, rol y token entregados después de autenticar.
public class AuthResponseDTO {

    private Long usuarioId;
    private String rol;
    private String tokenAcceso;
}
