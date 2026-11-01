package com.upc.backend.services;

import com.upc.backend.dtos.ActualizarPacientePerfilRequestDTO;
import com.upc.backend.dtos.ContactoEmergenciaResponseDTO;
import com.upc.backend.dtos.GuardarContactoEmergenciaRequestDTO;
import com.upc.backend.dtos.InicioPacienteDTO;
import com.upc.backend.dtos.PacientePerfilActualizadoDTO;
import com.upc.backend.dtos.PacientePerfilDTO;

// Servicio compartido por las HU de inicio, eventos, notificaciones y perfil del paciente.
public interface PacienteService {
    // HU08 | END16: compone el resumen de inicio.
    InicioPacienteDTO consultarInicio(Long pacienteId);

    // HU21 | END29: actualiza correo y teléfono del paciente.
    PacientePerfilActualizadoDTO actualizarPerfil(Long pacienteId, ActualizarPacientePerfilRequestDTO request);

    // HU21 | END28: consulta el perfil y contacto de emergencia.
    PacientePerfilDTO buscarPerfil(Long pacienteId);

    // HU21 | END30: crea o actualiza el contacto único.
    ContactoEmergenciaResponseDTO guardarContacto(Long pacienteId, GuardarContactoEmergenciaRequestDTO request);

}
