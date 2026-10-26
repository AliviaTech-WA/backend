package com.upc.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "recuperacion_contrasena")
// HU05 | END04-END06: código o token temporal persistido como hash e identificado mediante UUID.
public class RecuperacionContrasena {
    @Id
    @Column(name = "id_recuperacion", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario idUsuario;

    @Size(max = 255)
    @NotNull
    @Column(name = "valor_hash", nullable = false)
    private String valorHash;

    @Size(max = 20)
    @NotNull
    @Column(name = "fase", nullable = false, length = 20)
    private String fase;

    @NotNull
    @Column(name = "fecha_expiracion", nullable = false)
    private OffsetDateTime fechaExpiracion;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "intentos", nullable = false)
    private Short intentos;


}
