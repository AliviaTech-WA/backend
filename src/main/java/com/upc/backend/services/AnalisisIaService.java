package com.upc.backend.services;
import com.upc.backend.dtos.AnalisisIaResponseDTO;
import com.upc.backend.dtos.GenerarAnalisisIaRequestDTO;

// HU31 | END25: contrato para generar análisis de bienestar sin persistencia.
public interface AnalisisIaService {
    AnalisisIaResponseDTO generar(Long pacienteId, GenerarAnalisisIaRequestDTO request);
}
