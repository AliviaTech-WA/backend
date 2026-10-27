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
// HU34 | END32: campos opcionales permitidos para actualizar el perfil profesional.
public class ActualizarPsicologoPerfilRequestDTO {
    @Email
    @Size(max = 254)
    private String correo;

    @Size(max = 20)
    private String telefono;

    @Size(max = 100)
    private String especialidad;
}
