package com.upc.backend.services;

import com.upc.backend.dtos.ConsultaDispositivoAsignado;
import com.upc.backend.dtos.DatosWearableDTO;
import com.upc.backend.dtos.SincronizacionWearableRequestDTO;
import com.upc.backend.dtos.SincronizacionWearableResponseDTO;

// HU11, HU13, HU14, HU15, HU21 y HU26: contrato compartido para datos y gestión del wearable.
public interface DispositivoService {
    // HU11 y apoyo parcial a HU13 | END10: persiste la sincronización recibida.
    SincronizacionWearableResponseDTO sincronizar(Long dispositivoId, SincronizacionWearableRequestDTO request);
    // HU14: Consultar dispositivo
    ConsultaDispositivoAsignado consultarDispositivoAsignado(Long pacienteId);
    // HU15: Renombrar dispositivo
    ConsultaDispositivoAsignado renombrarDispositivoAsignado(String alias, Long pacienteId);
    // HU26 | END11: métricas, evolución de siete días y lecturas recientes.
    DatosWearableDTO consultarDatos(Long dispositivoId);
    // HU26 | END11: mismos datos a partir del paciente, usando su dispositivo activo.
    DatosWearableDTO consultarDatosPorPaciente(Long pacienteId);
}
