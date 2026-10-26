package com.upc.backend.security.entities;

import com.upc.backend.entities.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Infraestructura común: adapta Usuario al principal autenticado usado por Spring Security.
public class UsuarioPrincipal implements UserDetails {

    private final Long id;
    private final String correo;
    private final String passwordHash;
    private final String rol;
    private final boolean habilitado;

    public UsuarioPrincipal(Usuario usuario) {
        this.id = usuario.getId();
        this.correo = usuario.getCorreo();
        this.passwordHash = usuario.getPasswordHash();
        this.rol = usuario.getRol();
        this.habilitado = !"PACIENTE".equals(usuario.getRol()) || usuario.getFechaActivacion() != null;
    }

    public Long getId() {
        return id;
    }

    public String getRol() {
        return rol;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override
    public String getPassword() {
        return passwordHash == null ? "" : passwordHash;
    }

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    // HU02 y HU03: impide autenticar al paciente mientras fecha_activacion permanezca nula.
    public boolean isEnabled() {
        return habilitado;
    }

}
