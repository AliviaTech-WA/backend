package com.upc.backend.serviceimpl;

import com.upc.backend.dtos.RegistrarBienestarRequestDTO;
import com.upc.backend.dtos.RegistroBienestarDetalleDTO;
import com.upc.backend.dtos.RegistroBienestarResponseDTO;
import com.upc.backend.entities.Paciente;
import com.upc.backend.entities.RegistroBienestar;
import com.upc.backend.exceptions.ResourceNotFoundException;
import com.upc.backend.repositories.PacienteRepositorio;
import com.upc.backend.repositories.RegistroBienestarRepositorio;
import com.upc.backend.services.RegistroBienestarService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.upc.backend.services.NotificacionService;

import java.time.OffsetDateTime;
import java.util.List;

@Service
// HU09, HU10 y HU25: implementa el registro y la consulta de bienestar con ModelMapper.
public class RegistroBienestarServiceImpl implements RegistroBienestarService {
    private final PacienteRepositorio pacienteRepositorio;
    private final RegistroBienestarRepositorio registroBienestarRepositorio;
    private final ModelMapper modelMapper;
    private final NotificacionService notificacionService;

    public RegistroBienestarServiceImpl(PacienteRepositorio pacienteRepositorio,
                                        RegistroBienestarRepositorio registroBienestarRepositorio,
                                        ModelMapper modelMapper,
                                        NotificacionService notificacionService) {
        this.pacienteRepositorio = pacienteRepositorio;
        this.registroBienestarRepositorio = registroBienestarRepositorio;
        this.modelMapper = modelMapper;
        this.notificacionService = notificacionService;
    }

    @Override
    @Transactional
    // HU09 | END08: asocia el registro al paciente y fija la fecha del servidor.
    public RegistroBienestarResponseDTO registrar(Long pacienteId, RegistrarBienestarRequestDTO request) {
        Paciente paciente = pacienteRepositorio.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el paciente con ID " + pacienteId + "."));

        RegistroBienestar registro = modelMapper.map(request, RegistroBienestar.class);
        registro.setId(null);
        registro.setIdPaciente(paciente);
        registro.setFechaHora(OffsetDateTime.now());
        registro = registroBienestarRepositorio.save(registro);

        //esta parte es para que el paciente reciba la notificación de que tiene un evento bienestar registrado
        //==============================
        String nombrePaciente = paciente.getUsuario().getNombres();

        notificacionService.crearNotificacion(
                pacienteId,
                "Nuevo registro de bienestar",
                "Hola " + nombrePaciente + " acabas de registrar un nuevo evento de bienestar. Por favor revisa tu historial para más detalles.",
                "REGISTRO_BIENESTAR"
        );
        //==============================

        RegistroBienestarResponseDTO response = modelMapper.map(registro, RegistroBienestarResponseDTO.class);
        response.setRegistroId(registro.getId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    // HU10 y HU25 | END09: normaliza el periodo, valida sus límites y devuelve el historial descendente.
    public List<RegistroBienestarDetalleDTO> listar(Long pacienteId,
                                                    OffsetDateTime fechaDesde,
                                                    OffsetDateTime fechaHasta) {
        if (!pacienteRepositorio.existsById(pacienteId)) {
            throw new ResourceNotFoundException("No se encontró el paciente con ID " + pacienteId + ".");
        }
        OffsetDateTime desde = fechaDesde == null ? OffsetDateTime.parse("1970-01-01T00:00:00Z") : fechaDesde;
        OffsetDateTime hasta = fechaHasta == null ? OffsetDateTime.now() : fechaHasta;
        if (hasta.isBefore(desde)) {
            throw new com.upc.backend.exceptions.InvalidRequestException("fechaHasta no puede ser anterior a fechaDesde.");
        }
        return registroBienestarRepositorio
                .findByIdPaciente_IdAndFechaHoraBetweenOrderByFechaHoraDesc(pacienteId, desde, hasta)
                .stream()
                .map(registro -> {
                    RegistroBienestarDetalleDTO dto = modelMapper.map(registro, RegistroBienestarDetalleDTO.class);
                    dto.setRegistroId(registro.getId());
                    return dto;
                })
                .toList();
    }
}
