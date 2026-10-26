package com.upc.backend.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU04 | END03: credenciales utilizadas para iniciar sesión.
public class AuthRequestDTO {

    @NotBlank
    @Email
    private String correo;

    @NotBlank
    private String contrasena;
}
