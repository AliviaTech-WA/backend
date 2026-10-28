package com.upc.backend.serviceimpl;

import org.springframework.stereotype.Service;

import com.upc.backend.dtos.ConsultaDispositivoAsignado;
import com.upc.backend.entities.Dispositivo;
import com.upc.backend.repositories.DispositivoRepositorio;
import com.upc.backend.services.DispositivoService;


@Service
public class DispositivoServiceImpl implements DispositivoService {

    private final DispositivoRepositorio dispositivoRepositorio;

    public DispositivoServiceImpl(
            DispositivoRepositorio dispositivoRepositorio) {
        this.dispositivoRepositorio = dispositivoRepositorio;
    }

     @Override
    public ConsultaDispositivoAsignado consultarDispositivoAsignado(Long pacienteId) {

        // 1. Buscar el dispositivo asignado al paciente
        Dispositivo dispositivo = dispositivoRepositorio
                .findFirstByIdPaciente_IdAndEstadoIgnoreCaseAndFechaRetiroIsNull(pacienteId, "ACTIVO")
                .orElseThrow(() -> new RuntimeException(
                        "El paciente no tiene un dispositivo asignado."
                ));

        // 2. Obtener el nombre completo del paciente
        String nombrePaciente = dispositivo.getIdPaciente()
                .getUsuario()
                .getNombres();

        // 3. Construir el DTO con los datos que necesita Angular
        ConsultaDispositivoAsignado dto = new ConsultaDispositivoAsignado();
        dto.setNombrePaciente(nombrePaciente);
        dto.setCodigoDispositivo(dispositivo.getCodigoDispositivo());
        dto.setModelo(dispositivo.getModelo());
        dto.setEstado(dispositivo.getEstado());
        dto.setAlias(dispositivo.getAlias());
        dto.setFechaAsignacion(dispositivo.getFechaAsignacion());
        dto.setFechaUltimaSincronizacion(
                dispositivo.getFechaUltimaSincronizacion()
        );

        // 4. Devolver el DTO
        return dto;
    }
}
