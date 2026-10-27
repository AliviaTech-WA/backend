package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
// HU34 | END31 y END32: representación de consulta y respuesta del perfil profesional.
public class PsicologoPerfilDTO {
    private Long psicologoId;
    private String nombres;
    private String apellidos;
    private String dni;
    private String correo;
    private String telefono;
    private String numeroColegiatura;
    private String especialidad;
}
