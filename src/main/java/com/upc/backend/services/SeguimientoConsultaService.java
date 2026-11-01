package com.upc.backend.services;

import com.upc.backend.dtos.DashboardPsicologoDTO;

// HU22-HU24 | END21-END23: contrato compartido de consultas del seguimiento.
public interface SeguimientoConsultaService {

    // HU22 | END21: indicadores del dashboard del psicólogo.
    DashboardPsicologoDTO consultarDashboard(Long psicologoId);
}
