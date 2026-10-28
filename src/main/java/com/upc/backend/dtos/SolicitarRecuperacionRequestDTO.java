package com.upc.backend.dtos;

import jakarta.validation.constraints.Email;
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
// HU05 | END04: correo de la cuenta para solicitar o reenviar un código de recuperación.
public class SolicitarRecuperacionRequestDTO {

    @NotBlank
    @Email
    @Size(max = 254)
    private String correo;
}
