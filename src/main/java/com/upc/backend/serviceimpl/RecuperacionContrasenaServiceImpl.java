package com.upc.backend.serviceimpl;

import com.upc.backend.dtos.MensajeResponseDTO;
import com.upc.backend.dtos.RestablecerContrasenaRequestDTO;
import com.upc.backend.dtos.SolicitarRecuperacionRequestDTO;
import com.upc.backend.dtos.SolicitarRecuperacionResponseDTO;
import com.upc.backend.dtos.ValidarCodigoRecuperacionRequestDTO;
import com.upc.backend.dtos.ValidarCodigoRecuperacionResponseDTO;
import com.upc.backend.entities.RecuperacionContrasena;
import com.upc.backend.entities.Usuario;
import com.upc.backend.exceptions.CorreoEnvioException;
import com.upc.backend.exceptions.InvalidRequestException;
import com.upc.backend.repositories.RecuperacionContrasenaRepositorio;
import com.upc.backend.repositories.UsuarioRepositorio;
import com.upc.backend.security.services.RecuperacionRateLimiter;
import com.upc.backend.services.CorreoService;
import com.upc.backend.services.RecuperacionContrasenaService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

@Service
// HU05 | END04-END06: administra en PostgreSQL el código y el token temporal de recuperación.
public class RecuperacionContrasenaServiceImpl implements RecuperacionContrasenaService {

    private static final String FASE_CODIGO = "CODIGO";
    private static final String FASE_RESTABLECIMIENTO = "RESTABLECIMIENTO";
    private static final int EXPIRACION_CODIGO_SEGUNDOS = 600;
    private static final int EXPIRACION_TOKEN_SEGUNDOS = 300;
    private static final int MAXIMO_INTENTOS = 5;
    private static final String MENSAJE_SOLICITUD =
            "Si la cuenta está habilitada, recibirás un código por correo";
    private static final String CODIGO_INVALIDO =
            "El código es inválido, venció o ya no puede utilizarse.";
    private static final String TOKEN_INVALIDO =
            "El token de restablecimiento es inválido, venció o ya fue utilizado.";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final RecuperacionContrasenaRepositorio recuperacionRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final CorreoService correoService;
    private final PasswordEncoder passwordEncoder;
    private final RecuperacionRateLimiter rateLimiter;

    public RecuperacionContrasenaServiceImpl(
            RecuperacionContrasenaRepositorio recuperacionRepositorio,
            UsuarioRepositorio usuarioRepositorio,
            CorreoService correoService,
            PasswordEncoder passwordEncoder,
            RecuperacionRateLimiter rateLimiter) {
        this.recuperacionRepositorio = recuperacionRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.correoService = correoService;
        this.passwordEncoder = passwordEncoder;
        this.rateLimiter = rateLimiter;
    }

    @Override
    @Transactional(noRollbackFor = CorreoEnvioException.class)
    // HU05 | END04: limita, reemplaza y envía una recuperación sin revelar cuentas inexistentes o pendientes.
    public SolicitarRecuperacionResponseDTO solicitar(SolicitarRecuperacionRequestDTO request, String direccionIp) {
        String correo = request.getCorreo().trim().toLowerCase(Locale.ROOT);
        rateLimiter.verificarYRegistrar(correo, direccionIp);

        UUID recuperacionId = UUID.randomUUID();
        Usuario usuario = usuarioRepositorio.findWithLockByCorreoIgnoreCase(correo).orElse(null);
        if (usuario == null || usuario.getFechaActivacion() == null) {
            return respuestaSolicitud(recuperacionId);
        }

        recuperacionRepositorio.deleteByIdUsuario_Id(usuario.getId());

        OffsetDateTime ahora = OffsetDateTime.now();
        String codigo = generarCodigoNumerico();
        RecuperacionContrasena recuperacion = new RecuperacionContrasena();
        recuperacion.setId(recuperacionId);
        recuperacion.setIdUsuario(usuario);
        recuperacion.setValorHash(passwordEncoder.encode(codigo));
        recuperacion.setFase(FASE_CODIGO);
        recuperacion.setFechaExpiracion(ahora.plusSeconds(EXPIRACION_CODIGO_SEGUNDOS));
        recuperacion.setIntentos((short) 0);
        recuperacionRepositorio.saveAndFlush(recuperacion);

        try {
            correoService.enviarCodigoRecuperacion(
                    usuario.getCorreo(), usuario.getNombres(), codigo);
        } catch (CorreoEnvioException ex) {
            recuperacionRepositorio.delete(recuperacion);
            recuperacionRepositorio.flush();
            throw ex;
        }
        return respuestaSolicitud(recuperacionId);
    }

    @Override
    @Transactional(noRollbackFor = InvalidRequestException.class)
    // HU05 | END05: bloquea la fila y conserva los intentos fallidos aunque responda HTTP 400.
    public ValidarCodigoRecuperacionResponseDTO validarCodigo(ValidarCodigoRecuperacionRequestDTO request) {
        RecuperacionContrasena recuperacion = recuperacionRepositorio
                .findWithLockById(request.getRecuperacionId())
                .orElseThrow(() -> new InvalidRequestException(CODIGO_INVALIDO));

        OffsetDateTime ahora = OffsetDateTime.now();
        if (!FASE_CODIGO.equals(recuperacion.getFase())
                || !recuperacion.getFechaExpiracion().isAfter(ahora)
                || recuperacion.getIntentos() >= MAXIMO_INTENTOS) {
            throw new InvalidRequestException(CODIGO_INVALIDO);
        }

        if (!passwordEncoder.matches(request.getCodigo(), recuperacion.getValorHash())) {
            short intentos = (short) Math.min(MAXIMO_INTENTOS, recuperacion.getIntentos() + 1);
            recuperacion.setIntentos(intentos);
            recuperacionRepositorio.save(recuperacion);
            throw new InvalidRequestException(CODIGO_INVALIDO);
        }

        String secreto = generarSecreto();
        recuperacion.setValorHash(passwordEncoder.encode(secreto));
        recuperacion.setFase(FASE_RESTABLECIMIENTO);
        recuperacion.setFechaExpiracion(ahora.plusSeconds(EXPIRACION_TOKEN_SEGUNDOS));
        recuperacion.setIntentos((short) 0);
        recuperacionRepositorio.save(recuperacion);

        return new ValidarCodigoRecuperacionResponseDTO(
                recuperacion.getId() + "." + secreto, EXPIRACION_TOKEN_SEGUNDOS);
    }

    @Override
    @Transactional
    // HU05 | END06: valida y consume el token mientras actualiza la contraseña en una sola transacción.
    public MensajeResponseDTO restablecer(RestablecerContrasenaRequestDTO request) {
        TokenRestablecimiento token = parsearToken(request.getTokenRestablecimiento());

        Long usuarioId = recuperacionRepositorio.findUsuarioIdById(token.recuperacionId())
                .orElseThrow(() -> new InvalidRequestException(TOKEN_INVALIDO));

        Usuario usuario = usuarioRepositorio.findWithLockById(usuarioId)
                .orElseThrow(() -> new InvalidRequestException(TOKEN_INVALIDO));
        RecuperacionContrasena recuperacion = recuperacionRepositorio
                .findWithLockById(token.recuperacionId())
                .orElseThrow(() -> new InvalidRequestException(TOKEN_INVALIDO));

        OffsetDateTime ahora = OffsetDateTime.now();
        if (!FASE_RESTABLECIMIENTO.equals(recuperacion.getFase())
                || !recuperacion.getFechaExpiracion().isAfter(ahora)
                || !passwordEncoder.matches(token.secreto(), recuperacion.getValorHash())
                || !recuperacion.getIdUsuario().getId().equals(usuario.getId())) {
            throw new InvalidRequestException(TOKEN_INVALIDO);
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getNuevaContrasena()));
        usuarioRepositorio.save(usuario);
        recuperacionRepositorio.delete(recuperacion);
        recuperacionRepositorio.flush();
        return new MensajeResponseDTO("La contraseña fue actualizada correctamente.");
    }

    // HU05 | END04: mantiene idéntica la respuesta para cuentas válidas, inexistentes o pendientes.
    private SolicitarRecuperacionResponseDTO respuestaSolicitud(UUID recuperacionId) {
        return new SolicitarRecuperacionResponseDTO(
                recuperacionId, MENSAJE_SOLICITUD, EXPIRACION_CODIGO_SEGUNDOS);
    }

    // HU05 | END04: genera seis dígitos con SecureRandom y permite ceros iniciales.
    private String generarCodigoNumerico() {
        return String.format(Locale.ROOT, "%06d", RANDOM.nextInt(1_000_000));
    }

    // HU05 | END05: genera 32 bytes y los representa en Base64 URL sin relleno.
    private String generarSecreto() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // HU05 | END06: separa el UUID del secreto sin aceptar tokens incompletos.
    private TokenRestablecimiento parsearToken(String valor) {
        int separador = valor.indexOf('.');
        if (separador <= 0 || separador == valor.length() - 1
                || valor.indexOf('.', separador + 1) >= 0) {
            throw new InvalidRequestException(TOKEN_INVALIDO);
        }
        try {
            return new TokenRestablecimiento(
                    UUID.fromString(valor.substring(0, separador)), valor.substring(separador + 1));
        } catch (IllegalArgumentException ex) {
            throw new InvalidRequestException(TOKEN_INVALIDO);
        }
    }

    private record TokenRestablecimiento(UUID recuperacionId, String secreto) {
    }
}
