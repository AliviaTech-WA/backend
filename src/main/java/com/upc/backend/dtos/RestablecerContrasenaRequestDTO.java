package com.upc.backend.dtos;

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
// HU05 | END06: token temporal y contraseña nueva validada con las reglas actuales de activación.
public class RestablecerContrasenaRequestDTO {

    @NotBlank
    @Size(max = 200)
    private String tokenRestablecimiento;

    @NotBlank
    @Size(min = 8, max = 72)
    private String nuevaContrasena;
}
