package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU21 | END30: contacto de emergencia persistido y su identificador.
public class ContactoEmergenciaResponseDTO {
    private Long contactoId;
    private String nombreCompleto;
    private String relacion;
    private String telefono;
    private String correo;
}
