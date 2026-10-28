package com.upc.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
// HU01 | END01: datos personales, credenciales y datos profesionales para crear al psicólogo.
public class CrearPsicologoRequestDTO {

    @NotBlank
    @Size(max = 8)
    private String dni;

    @NotBlank
    @Size(max = 100)
    private String nombres;

    @NotBlank
    @Size(max = 100)
    private String apellidos;

    @NotBlank
    @Email
    @Size(max = 254)
    private String correo;

    @Size(max = 20)
    private String telefono;

    @NotBlank
    private String contrasena;

    @NotBlank
    @Size(max = 30)
    private String numeroColegiatura;

    @NotBlank
    @Size(max = 100)
    private String especialidad;
}
