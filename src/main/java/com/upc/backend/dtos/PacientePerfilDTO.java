package com.upc.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU21 | END28: perfil personal con contacto de emergencia opcional.
public class PacientePerfilDTO {
    private Long pacienteId;
    private String nombres;
    private String apellidos;
    private String dni;
    private String correo;
    private String telefono;
    private LocalDate fechaNacimiento;
    private ContactoEmergenciaDTO contactoEmergencia;
}
