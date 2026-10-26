package com.upc.backend.security.services;

import com.upc.backend.entities.Usuario;
import com.upc.backend.repositories.UsuarioRepositorio;
import com.upc.backend.security.dtos.AuthRequestDTO;
import com.upc.backend.security.dtos.AuthResponseDTO;
import com.upc.backend.security.entities.UsuarioPrincipal;
import com.upc.backend.security.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
// HU04 | END03: servicio de autenticación y emisión de JWT; infraestructura común de acceso.
public class AutenticacionService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepositorio usuarioRepositorio;
    private final JwtUtil jwtUtil;

    public AutenticacionService(AuthenticationManager authenticationManager,
                                UsuarioRepositorio usuarioRepositorio,
                                JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepositorio = usuarioRepositorio;
        this.jwtUtil = jwtUtil;
    }

    // HU04 | END03: autentica credenciales y construye la respuesta con identidad, rol y token.
    public AuthResponseDTO autenticar(AuthRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getContrasena()));
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        Usuario usuario = usuarioRepositorio.findById(principal.getId()).orElseThrow();
        return new AuthResponseDTO(usuario.getId(), usuario.getRol(), jwtUtil.generateToken(principal));
    }
}
