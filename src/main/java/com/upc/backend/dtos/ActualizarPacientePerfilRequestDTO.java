package com.upc.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
// HU21 | END29: correo y teléfono opcionales permitidos para actualización.
public class ActualizarPacientePerfilRequestDTO {

    @Email
    @Size(max = 254)
    private String correo;

    @Size(max = 20)
    private String telefono;
}
