package com.upc.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "usuario")
// Entidad compartida por HU01, HU03-HU06, HU21 y HU34 para identidad, contacto y credenciales.
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Long id;

    @Size(max = 20)
    @NotNull
    @Column(name = "rol", nullable = false, length = 20)
    private String rol;

    @Size(max = 8)
    @NotNull
    @Column(name = "dni", nullable = false, length = 8)
    private String dni;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Size(max = 100)
    @NotNull
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Size(max = 254)
    @NotNull
    @Column(name = "correo", nullable = false, length = 254)
    private String correo;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 255)
    @Column(name = "password_hash")
    private String passwordHash;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @Column(name = "fecha_activacion")
    private OffsetDateTime fechaActivacion;


}
