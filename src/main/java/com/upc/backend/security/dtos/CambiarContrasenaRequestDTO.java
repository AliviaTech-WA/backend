package com.upc.backend.security.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU06 | END07: contraseña actual y nueva contraseña del usuario autenticado.
public class CambiarContrasenaRequestDTO {

    @NotBlank
    private String contrasenaActual;

    @NotBlank
    @Size(min = 8, max = 72)
    private String nuevaContrasena;
}
