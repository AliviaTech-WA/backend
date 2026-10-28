package com.upc.backend.services;

import com.upc.backend.dtos.ActivarPacienteRequestDTO;
import com.upc.backend.dtos.ActivarPacienteResponseDTO;

// HU03 | END02: contrato público para completar una cuenta de paciente invitado.
public interface ActivacionPacienteService {

    ActivarPacienteResponseDTO activar(ActivarPacienteRequestDTO request);
}
