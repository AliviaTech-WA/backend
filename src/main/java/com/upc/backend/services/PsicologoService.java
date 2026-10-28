package com.upc.backend.services;

import com.upc.backend.dtos.*;

// HU01 y HU34: contrato de registro y gestión del perfil profesional.
public interface PsicologoService {

    // HU01 | END01: registra usuario y perfil de psicólogo.
    PsicologoIdResponseDTO crear(CrearPsicologoRequestDTO request);

    // HU34 | END31: recupera el perfil profesional.
    PsicologoPerfilDTO buscarPerfilPorId(Long psicologoId);

    // HU34 | END32: actualiza los campos editables del perfil.
    PsicologoPerfilDTO actualizarPerfil(Long psicologoId, ActualizarPsicologoPerfilRequestDTO request);

    // HU02 | END24: crea al paciente pendiente y envía su invitación de activación.
    RegistrarPacienteResponseDTO registrarPaciente(Long psicologoId, RegistrarPacienteRequestDTO request);

}
