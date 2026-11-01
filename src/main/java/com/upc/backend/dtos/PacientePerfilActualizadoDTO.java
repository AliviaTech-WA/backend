package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
// HU21 | END29: confirma los datos personales editables resultantes.
public class PacientePerfilActualizadoDTO {

    private Long pacienteId;
    private String correo;
    private String telefono;
}
