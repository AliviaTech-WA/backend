package com.upc.backend.serviceimpl;

import com.upc.backend.dtos.ActivarPacienteRequestDTO;
import com.upc.backend.dtos.ActivarPacienteResponseDTO;
import com.upc.backend.entities.Consentimiento;
import com.upc.backend.entities.InvitacionActivacion;
import com.upc.backend.entities.Paciente;
import com.upc.backend.entities.Usuario;
import com.upc.backend.exceptions.InvalidRequestException;
import com.upc.backend.repositories.ConsentimientoRepositorio;
import com.upc.backend.repositories.InvitacionActivacionRepositorio;
import com.upc.backend.repositories.UsuarioRepositorio;
import com.upc.backend.services.ActivacionPacienteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Locale;

@Service
// HU03 | END02: valida y consume la invitación, activa la cuenta y registra el consentimiento.
public class ActivacionPacienteServiceImpl implements ActivacionPacienteService {

    private static final String ROL_PACIENTE = "PACIENTE";
    private static final String INVITACION_INVALIDA =
            "La invitación no es válida, venció o ya fue utilizada.";

    private final UsuarioRepositorio usuarioRepositorio;
    private final InvitacionActivacionRepositorio invitacionRepositorio;
    private final ConsentimientoRepositorio consentimientoRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final String versionConsentimiento;

    public ActivacionPacienteServiceImpl(
            UsuarioRepositorio usuarioRepositorio,
            InvitacionActivacionRepositorio invitacionRepositorio,
            ConsentimientoRepositorio consentimientoRepositorio,
            PasswordEncoder passwordEncoder,
            @Value("${app.consentimiento.version:1.0}") String versionConsentimiento) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.invitacionRepositorio = invitacionRepositorio;
        this.consentimientoRepositorio = consentimientoRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.versionConsentimiento = versionConsentimiento;
    }

    @Override
    @Transactional
    // HU03 | END02: aplica contraseña, activación, consentimiento y consumo como una unidad atómica.
    public ActivarPacienteResponseDTO activar(ActivarPacienteRequestDTO request) {
        if (!Boolean.TRUE.equals(request.getAceptaTratamientoDatos())) {
            throw new InvalidRequestException("Debe aceptar el tratamiento de datos para activar la cuenta.");
        }

        String correo = request.getCorreo().trim().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarioRepositorio.findByCorreoIgnoreCase(correo)
                .filter(item -> ROL_PACIENTE.equals(item.getRol()))
                .filter(item -> item.getFechaActivacion() == null)
                .orElseThrow(() -> new InvalidRequestException(INVITACION_INVALIDA));

        InvitacionActivacion invitacion = invitacionRepositorio
                .findFirstByIdPaciente_Usuario_CorreoIgnoreCaseAndFechaUsoIsNullOrderByFechaCreacionDesc(correo)
                .orElseThrow(() -> new InvalidRequestException(INVITACION_INVALIDA));

        OffsetDateTime ahora = OffsetDateTime.now();
        if (!invitacion.getFechaExpiracion().isAfter(ahora)
                || !passwordEncoder.matches(request.getCodigoActivacion(), invitacion.getTokenHash())) {
            throw new InvalidRequestException(INVITACION_INVALIDA);
        }

        Paciente paciente = invitacion.getIdPaciente();
        if (!paciente.getId().equals(usuario.getId())) {
            throw new InvalidRequestException(INVITACION_INVALIDA);
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getContrasena()));
        usuario.setFechaActivacion(ahora);
        invitacion.setFechaUso(ahora);

        Consentimiento consentimiento = new Consentimiento();
        consentimiento.setIdPaciente(paciente);
        consentimiento.setVersionDocumento(versionConsentimiento);
        consentimiento.setFechaAceptacion(ahora);

        usuarioRepositorio.save(usuario);
        invitacionRepositorio.save(invitacion);
        consentimientoRepositorio.save(consentimiento);
        return new ActivarPacienteResponseDTO(paciente.getId(), ahora);
    }
}
