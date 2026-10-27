package com.upc.backend.services;

import com.upc.backend.dtos.RegistrarSesionRequestDTO;
import com.upc.backend.dtos.SesionCreadaDTO;
import com.upc.backend.dtos.SesionResumenDTO;
import java.util.List;

// HU19, HU20, HU28, HU29 y HU30: contrato compartido para sesiones de seguimiento.
public interface SesionService {
    // HU28 | END18: registra una sesión.
    SesionCreadaDTO registrar(Long pacienteId, RegistrarSesionRequestDTO request);
    // HU19 y HU29 | END19: lista sesiones del seguimiento activo.
    List<SesionResumenDTO> listar(Long pacienteId);
}
