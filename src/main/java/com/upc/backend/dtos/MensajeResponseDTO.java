package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU05-HU06 | END06-END07: respuesta textual reutilizable para confirmar cambios de contraseña.
public class MensajeResponseDTO {

    private String mensaje;
}
