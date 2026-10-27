package com.upc.backend.services;

import com.upc.backend.dtos.ReporteGeneralDTO;

import java.time.LocalDate;

// HU32 y HU33: contrato para consultar y exportar reportes generales.
public interface ReporteService {
    // HU32 | END26: calcula indicadores del periodo.
    ReporteGeneralDTO consultar(Long psicologoId, LocalDate fechaDesde, LocalDate fechaHasta);
}
