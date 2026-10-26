package com.upc.backend.repositories;

import com.upc.backend.entities.Usuario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// Repositorio compartido por HU01, HU04-HU06, HU21 y HU34 para identidad y credenciales.
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    // HU01, HU04, HU21 y HU34: localiza por correo sin distinguir mayúsculas.
    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    // Soporte de validación de unicidad de correo.
    boolean existsByCorreoIgnoreCase(String correo);

    // HU01: valida la unicidad del DNI al registrar al psicólogo.
    boolean existsByDni(String dni);

    // HU05 | END04: serializa solicitudes de recuperación para la misma cuenta.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Usuario> findWithLockByCorreoIgnoreCase(String correo);

    // HU05 | END06: mantiene el mismo orden de bloqueo al consumir el token y actualizar la contraseña.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Usuario> findWithLockById(Long usuarioId);
}
