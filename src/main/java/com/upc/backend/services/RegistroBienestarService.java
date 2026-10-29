package com.upc.backend.services;

import com.upc.backend.dtos.RegistrarBienestarRequestDTO;
import com.upc.backend.dtos.RegistroBienestarResponseDTO;

import com.upc.backend.dtos.RegistroBienestarDetalleDTO;
import java.time.OffsetDateTime;
import java.util.List;

public interface RegistroBienestarService {
    // HU09 | END08: crea un registro voluntario de bienestar.
    RegistroBienestarResponseDTO registrar(Long pacienteId, RegistrarBienestarRequestDTO request);

    // HU10 y HU25 | END09: lista registros dentro de un periodo opcional.
    List<RegistroBienestarDetalleDTO> listar(Long pacienteId, OffsetDateTime fechaDesde, OffsetDateTime fechaHasta);
}
