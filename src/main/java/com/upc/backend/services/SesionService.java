package com.upc.backend.services;

import com.upc.backend.dtos.RegistrarSesionRequestDTO;
import com.upc.backend.dtos.SesionCreadaDTO;

// HU19, HU20, HU28, HU29 y HU30: contrato compartido para sesiones de seguimiento.
public interface SesionService {
    // HU28 | END18: registra una sesión.
    SesionCreadaDTO registrar(Long pacienteId, RegistrarSesionRequestDTO request);
}
