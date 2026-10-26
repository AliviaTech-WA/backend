package com.upc.backend.security.services;

import com.upc.backend.dtos.MensajeResponseDTO;
import com.upc.backend.entities.Usuario;
import com.upc.backend.exceptions.InvalidRequestException;
import com.upc.backend.exceptions.ResourceNotFoundException;
import com.upc.backend.repositories.UsuarioRepositorio;
import com.upc.backend.security.dtos.CambiarContrasenaRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// HU06 | END07: servicio de seguridad para el cambio autenticado de contraseña.
public class UsuarioSecurityService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public UsuarioSecurityService(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    // HU06 | END07: verifica la contraseña actual y persiste el nuevo hash BCrypt.
    public MensajeResponseDTO cambiarContrasena(Long usuarioId, CambiarContrasenaRequestDTO request) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con ID " + usuarioId + "."));
        if (usuario.getPasswordHash() == null
                || !passwordEncoder.matches(request.getContrasenaActual(), usuario.getPasswordHash())) {
            throw new InvalidRequestException("La contraseña actual no es correcta.");
        }
        usuario.setPasswordHash(passwordEncoder.encode(request.getNuevaContrasena()));
        usuarioRepositorio.save(usuario);
        return new MensajeResponseDTO("La contraseña fue actualizada correctamente.");
    }
}
