package com.upc.backend.serviceimpl;
import com.upc.backend.dtos.*;
import com.upc.backend.entities.*;
import com.upc.backend.exceptions.ConflictException;
import com.upc.backend.exceptions.CorreoEnvioException;
import com.upc.backend.exceptions.InvalidRequestException;
import com.upc.backend.exceptions.ResourceNotFoundException;
import com.upc.backend.repositories.*;
import com.upc.backend.services.CorreoService;
import com.upc.backend.services.PsicologoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
public class PsicologoServiceImpl implements PsicologoService {
    private static final String ROL_PSICOLOGO = "PSICOLOGO";
    private static final String ROL_PACIENTE = "PACIENTE";
    private static final String ESTADO_PENDIENTE = "PENDIENTE_ACTIVACION";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepositorio usuarioRepositorio;
    private final PsicologoRepositorio psicologoRepositorio;
    private final PacienteRepositorio pacienteRepositorio;
    private final SeguimientoRepositorio seguimientoRepositorio;
    private final DispositivoRepositorio dispositivoRepositorio;
    private final InvitacionActivacionRepositorio invitacionRepositorio;
    private final CorreoService correoService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final String frontendActivacionUrl;
    private final String terminosUrl;


    public PsicologoServiceImpl(UsuarioRepositorio usuarioRepositorio,
                                PsicologoRepositorio psicologoRepositorio,
                                PacienteRepositorio pacienteRepositorio,
                                SeguimientoRepositorio seguimientoRepositorio,
                                DispositivoRepositorio dispositivoRepositorio,
                                InvitacionActivacionRepositorio invitacionRepositorio,
                                CorreoService correoService,
                                ModelMapper modelMapper,
                                PasswordEncoder passwordEncoder,
                                @Value("${app.frontend.activacion-url}") String frontendActivacionUrl,
                                @Value("${app.consentimiento.terminos-url}") String terminosUrl) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.psicologoRepositorio = psicologoRepositorio;
        this.pacienteRepositorio = pacienteRepositorio;
        this.seguimientoRepositorio = seguimientoRepositorio;
        this.dispositivoRepositorio = dispositivoRepositorio;
        this.invitacionRepositorio = invitacionRepositorio;
        this.correoService = correoService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.frontendActivacionUrl = frontendActivacionUrl;
        this.terminosUrl = terminosUrl;
    }

    @Override
    @Transactional
    // HU01 | END01: crea primero el usuario y luego el perfil profesional con el mismo identificador.
    public PsicologoIdResponseDTO crear(CrearPsicologoRequestDTO request) {
        validarDuplicidad(request.getDni(), request.getCorreo(), null);
        if (psicologoRepositorio.existsByNumeroColegiatura(request.getNumeroColegiatura())) {
            throw new ConflictException("El número de colegiatura ya está registrado.");
        }

        Usuario usuario = modelMapper.map(request, Usuario.class);
        OffsetDateTime ahora = OffsetDateTime.now();
        usuario.setId(null);
        usuario.setRol(ROL_PSICOLOGO);
        usuario.setPasswordHash(passwordEncoder.encode(request.getContrasena()));
        usuario.setFechaCreacion(ahora);
        usuario.setFechaActivacion(ahora);
        usuario = usuarioRepositorio.save(usuario);

        Psicologo psicologo = modelMapper.map(request, Psicologo.class);
        psicologo.setId(null);
        psicologo.setUsuario(usuario);
        psicologoRepositorio.save(psicologo);

        return new PsicologoIdResponseDTO(usuario.getId());
    }

    @Override
    @Transactional(readOnly = true)
    // HU34 | END31: carga y convierte el perfil profesional solicitado.
    public PsicologoPerfilDTO buscarPerfilPorId(Long psicologoId) {
        return convertirPerfil(buscarPsicologo(psicologoId));
    }

    @Override
    @Transactional
    // HU34 | END32: modifica solo correo, teléfono y especialidad cuando fueron enviados.
    public PsicologoPerfilDTO actualizarPerfil(Long psicologoId, ActualizarPsicologoPerfilRequestDTO request) {
        validarCamposActualizacion(request);
        Psicologo psicologo = buscarPsicologo(psicologoId);
        Usuario usuario = psicologo.getUsuario();

        if (request.getCorreo() != null) {
            validarDuplicidad(null, request.getCorreo(), usuario.getId());
            usuario.setCorreo(request.getCorreo().trim());
        }
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono().trim());
        }
        if (request.getEspecialidad() != null) {
            psicologo.setEspecialidad(request.getEspecialidad().trim());
        }

        usuarioRepositorio.save(usuario);
        psicologoRepositorio.save(psicologo);
        return convertirPerfil(psicologo);
    }

    // HU34: centraliza la búsqueda del perfil y la respuesta 404 cuando no existe.
    private Psicologo buscarPsicologo(Long psicologoId) {
        return psicologoRepositorio.findById(psicologoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el psicólogo con ID " + psicologoId + "."));
    }

    // HU01 y HU34: combina los datos de Usuario y Psicologo en el DTO de perfil.
    private PsicologoPerfilDTO convertirPerfil(Psicologo psicologo) {
        PsicologoPerfilDTO response = modelMapper.map(psicologo.getUsuario(), PsicologoPerfilDTO.class);
        response.setPsicologoId(psicologo.getId());
        response.setNumeroColegiatura(psicologo.getNumeroColegiatura());
        response.setEspecialidad(psicologo.getEspecialidad());
        return response;
    }

    // HU01 y HU34: evita DNI o correo duplicados, excluyendo el usuario actual al editar.
    private void validarDuplicidad(String dni, String correo, Long usuarioExcluidoId) {
        boolean dniDuplicado = dni != null && usuarioRepositorio.existsByDni(dni);
        boolean correoDuplicado = correo != null && usuarioRepositorio.findByCorreoIgnoreCase(correo)
                .filter(usuario -> usuarioExcluidoId == null || !usuario.getId().equals(usuarioExcluidoId))
                .isPresent();
        boolean duplicado = dniDuplicado || correoDuplicado;
        if (duplicado) {
            throw new ConflictException("El DNI o correo ya está asociado a otra cuenta.");
        }
    }

    // HU34: exige al menos un cambio y rechaza valores vacíos.
    private void validarCamposActualizacion(ActualizarPsicologoPerfilRequestDTO request) {
        if (request.getCorreo() == null && request.getTelefono() == null && request.getEspecialidad() == null) {
            throw new InvalidRequestException("Debe enviar al menos un campo para actualizar.");
        }
        validarNoVacio(request.getCorreo(), "correo");
        validarNoVacio(request.getTelefono(), "telefono");
        validarNoVacio(request.getEspecialidad(), "especialidad");
    }
    // HU34: valida campos opcionales únicamente cuando están presentes.
    private void validarNoVacio(String valor, String campo) {
        if (valor != null && valor.isBlank()) {
            throw new InvalidRequestException("El campo " + campo + " no puede estar vacío.");
        }
    }

    @Override
    @Transactional(noRollbackFor = CorreoEnvioException.class)
    // HU02 | END24: crea usuario, paciente, seguimiento, dispositivo e invitación antes de enviar el correo.
    public RegistrarPacienteResponseDTO registrarPaciente(Long psicologoId, RegistrarPacienteRequestDTO request) {
        Psicologo psicologo = buscarPsicologo(psicologoId);
        String correo = request.getCorreo().trim().toLowerCase(Locale.ROOT);
        validarDuplicidad(request.getDni(), correo, null);

        OffsetDateTime ahora = OffsetDateTime.now();
        Usuario usuario = modelMapper.map(request, Usuario.class);
        usuario.setId(null);
        usuario.setCorreo(correo);
        usuario.setRol(ROL_PACIENTE);
        usuario.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        usuario.setFechaCreacion(ahora);
        usuario.setFechaActivacion(null);
        usuario = usuarioRepositorio.save(usuario);

        Paciente paciente = modelMapper.map(request, Paciente.class);
        paciente.setId(null);
        paciente.setUsuario(usuario);
        paciente = pacienteRepositorio.save(paciente);

        Seguimiento seguimiento = new Seguimiento();
        seguimiento.setIdPaciente(paciente);
        seguimiento.setIdPsicologo(psicologo);
        seguimiento.setFechaInicio(LocalDate.now());
        seguimiento = seguimientoRepositorio.save(seguimiento);

        Dispositivo dispositivo = modelMapper.map(request.getDispositivo(), Dispositivo.class);
        dispositivo.setId(null);
        dispositivo.setIdPaciente(paciente);
        dispositivo.setCodigoDispositivo(generarCodigoDispositivo());
        dispositivo.setEstado("VINCULADA");
        dispositivo.setFechaAsignacion(ahora);
        dispositivo = dispositivoRepositorio.save(dispositivo);

        String codigo = crearInvitacion(paciente, ahora);
        // Fuerza las restricciones de la base antes de comunicar un código al paciente.
        usuarioRepositorio.flush();
        pacienteRepositorio.flush();
        seguimientoRepositorio.flush();
        dispositivoRepositorio.flush();
        invitacionRepositorio.flush();

        try {
            enviarInvitacion(usuario, codigo);
        } catch (CorreoEnvioException ex) {
            throw new CorreoEnvioException(
                    ex.getMessage() + " Paciente pendiente creado con ID " + paciente.getId()
                            + ".",
                    ex);
        }
        return new RegistrarPacienteResponseDTO(
                paciente.getId(), seguimiento.getId(), dispositivo.getId(), ESTADO_PENDIENTE, true);
    }

    // HU02: guarda únicamente el hash BCrypt de un código aleatorio de seis dígitos y vigencia de 24 horas.
    private String crearInvitacion(Paciente paciente, OffsetDateTime ahora) {
        String codigo = String.format(Locale.ROOT, "%06d", RANDOM.nextInt(1_000_000));
        InvitacionActivacion invitacion = new InvitacionActivacion();
        invitacion.setIdPaciente(paciente);
        invitacion.setTokenHash(passwordEncoder.encode(codigo));
        invitacion.setFechaCreacion(ahora);
        invitacion.setFechaExpiracion(ahora.plusHours(24));
        invitacionRepositorio.save(invitacion);
        return codigo;
    }

    // HU02: construye el enlace configurable; el código se envía separado para no exponerlo en la URL.
    private void enviarInvitacion(Usuario usuario, String codigo) {
        String enlace = UriComponentsBuilder.fromUriString(frontendActivacionUrl)
                .queryParam("correo", usuario.getCorreo())
                .build()
                .encode()
                .toUriString();
        correoService.enviarInvitacionPaciente(
                usuario.getCorreo(), usuario.getNombres(), codigo, enlace, terminosUrl);
    }

    // HU02: genera el identificador técnico del wearable que no forma parte de la solicitud.
    private String generarCodigoDispositivo() {
        return "ALV-BAND-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase(Locale.ROOT);
    }
}
