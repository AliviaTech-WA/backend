package com.upc.backend.security.services;

import com.upc.backend.entities.Usuario;
import com.upc.backend.repositories.UsuarioRepositorio;
import com.upc.backend.security.entities.UsuarioPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
// Infraestructura común de seguridad: adapta Usuario a UserDetails para HU04 y rutas protegidas.
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;

    public CustomUserDetailsService(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    // HU04: localiza al usuario por correo durante la autenticación.
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas."));
        return new UsuarioPrincipal(usuario);
    }
}
