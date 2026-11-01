package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU21 | END28: contacto de emergencia incluido dentro del perfil consultado.
public class ContactoEmergenciaDTO {
    private String nombreCompleto;
    private String relacion;
    private String telefono;
    private String correo;
}
