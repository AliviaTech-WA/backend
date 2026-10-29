package com.upc.backend.services;

import com.upc.backend.dtos.InicioPacienteDTO;

// Servicio compartido por las HU de inicio, eventos, notificaciones y perfil del paciente.
public interface PacienteService {
    // HU08 | END16: compone el resumen de inicio.
    InicioPacienteDTO consultarInicio(Long pacienteId);

}
