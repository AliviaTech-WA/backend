package com.upc.backend.services;
import com.upc.backend.dtos.ConsultaDispositivoAsignado;

public interface DispositivoService {
    
    ConsultaDispositivoAsignado consultarDispositivoAsignado(Long pacienteId);
}
