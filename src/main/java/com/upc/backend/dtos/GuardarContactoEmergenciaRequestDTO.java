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
// HU21 | END30: datos requeridos para crear o actualizar el contacto de emergencia.
public class GuardarContactoEmergenciaRequestDTO {
    @NotBlank @Size(max = 200)
    private String nombreCompleto;
    @NotBlank @Size(max = 50)
    private String relacion;
    @NotBlank @Size(max = 20)
    private String telefono;
    @Email @Size(max = 254)
    private String correo;
}
