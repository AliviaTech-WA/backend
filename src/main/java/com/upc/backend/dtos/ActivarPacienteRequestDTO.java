package com.upc.backend.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU03 | END02: código recibido por correo, contraseña definitiva y consentimiento del paciente.
public class ActivarPacienteRequestDTO {

    @NotBlank
    @Email
    @Size(max = 254)
    private String correo;

    @NotBlank
    @Pattern(regexp = "\\d{6}", message = "debe contener 6 dígitos")
    private String codigoActivacion;

    @NotBlank
    @Size(min = 8, max = 72)
    private String contrasena;

    @NotNull
    @AssertTrue(message = "debe aceptarse para activar la cuenta")
    private Boolean aceptaTratamientoDatos;
}
