package com.upc.backend.serviceimpl;
import com.upc.backend.dtos.RegistrarSesionRequestDTO;
import com.upc.backend.dtos.SesionCreadaDTO;
import com.upc.backend.entities.Seguimiento;
import com.upc.backend.entities.Sesion;
import com.upc.backend.exceptions.ResourceNotFoundException;
import com.upc.backend.repositories.SeguimientoRepositorio;
import com.upc.backend.repositories.SesionRepositorio;
import com.upc.backend.services.SesionService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
@Service
// HU19, HU20, HU28, HU29 y HU30: implementa las operaciones de sesiones del seguimiento.
public class SesionServiceImpl implements SesionService {
    private final SeguimientoRepositorio seguimientoRepositorio;
    private final SesionRepositorio sesionRepositorio;
    private final ModelMapper modelMapper;

    public SesionServiceImpl(SeguimientoRepositorio seguimientoRepositorio,
                             SesionRepositorio sesionRepositorio,
                             ModelMapper modelMapper) {
        this.seguimientoRepositorio = seguimientoRepositorio;
        this.sesionRepositorio = sesionRepositorio;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    // HU28 | END18: vincula la sesión al seguimiento activo y registra la fecha de creación.
    public SesionCreadaDTO registrar(Long pacienteId, RegistrarSesionRequestDTO request) {
        Seguimiento seguimiento = buscarSeguimientoActivo(pacienteId);
        Sesion sesion = modelMapper.map(request, Sesion.class);
        sesion.setIdSeguimiento(seguimiento);
        sesion.setFechaRegistro(OffsetDateTime.now());
        sesion = sesionRepositorio.save(sesion);
        return new SesionCreadaDTO(sesion.getId(), sesion.getFechaSesion(), sesion.getFechaRegistro());
    }
    // HU19, HU28 y HU29: localiza el seguimiento vigente usado por las operaciones de sesión.
    private Seguimiento buscarSeguimientoActivo(Long pacienteId) {
        return seguimientoRepositorio.findFirstByIdPaciente_IdAndFechaFinIsNull(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("El paciente no tiene un seguimiento activo."));
    }

}
