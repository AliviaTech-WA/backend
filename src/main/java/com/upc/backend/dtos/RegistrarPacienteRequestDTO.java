package com.upc.backend.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU02 | END24: datos personales y del wearable para registrar e invitar a un paciente.
public class RegistrarPacienteRequestDTO {

    @NotBlank
    @Pattern(regexp = "\\d{8}", message = "debe contener 8 dígitos")
    private String dni;

    @NotBlank
    @Size(max = 100)
    private String nombres;

    @NotBlank
    @Size(max = 100)
    private String apellidos;

    @NotNull
    @Past
    private LocalDate fechaNacimiento;

    @NotBlank
    @Email
    @Size(max = 254)
    private String correo;

    @NotBlank
    @Size(max = 20)
    private String telefono;

    @NotNull
    @Valid
    private DispositivoRequestDTO dispositivo;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    // HU02 | END24: características iniciales del dispositivo asignado al paciente.
    public static class DispositivoRequestDTO {

        @NotBlank
        @Size(max = 100)
        private String modelo;

        @NotBlank
        @Size(max = 100)
        private String alias;
    }
}
