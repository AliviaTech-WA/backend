package com.upc.backend.services;

import com.upc.backend.dtos.DashboardPsicologoDTO;
import com.upc.backend.dtos.PacienteSeguimientoListaDTO;
import com.upc.backend.dtos.ResumenSeguimientoPacienteDTO;

import java.time.LocalDate;
import java.util.List;

// HU22-HU24 | END21-END23: contrato compartido de consultas del seguimiento.
public interface SeguimientoConsultaService {

    // HU22 | END21: indicadores del dashboard del psicólogo.
    DashboardPsicologoDTO consultarDashboard(Long psicologoId);

    // HU23 | END22: lista de pacientes con búsqueda, filtros y orden.
    List<PacienteSeguimientoListaDTO> listarPacientes(Long psicologoId,
                                                      String busqueda,
                                                      String novedad,
                                                      String orden,
                                                      LocalDate fechaEpisodioDesde,
                                                      LocalDate fechaEpisodioHasta);

    // HU24 | END23: resumen del paciente; registra la revisión del psicólogo.
    ResumenSeguimientoPacienteDTO consultarResumen(Long pacienteId);
}
