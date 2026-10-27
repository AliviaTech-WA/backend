package com.upc.backend.services;

import com.upc.backend.dtos.RegistrarSesionRequestDTO;
import com.upc.backend.dtos.SesionCreadaDTO;
import com.upc.backend.dtos.SesionResumenDTO;
import com.upc.backend.dtos.SesionDetalleDTO;
import java.util.List;

// HU19, HU20, HU28, HU29 y HU30: contrato compartido para sesiones de seguimiento.
public interface SesionService {
    // HU28 | END18: registra una sesión.
    SesionCreadaDTO registrar(Long pacienteId, RegistrarSesionRequestDTO request);
    // HU19 y HU29 | END19: lista sesiones del seguimiento activo.
    List<SesionResumenDTO> listar(Long pacienteId);
    // HU20 y HU30 | END20: consulta detalle con nota interna condicionada por rol.
    SesionDetalleDTO buscarDetalle(Long sesionId, boolean incluirNotaInterna);
}
