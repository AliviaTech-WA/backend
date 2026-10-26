package com.upc.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "invitacion_activacion")
// HU02 y HU03 | END24 y END02: conserva el hash, vigencia y consumo de cada invitación.
public class InvitacionActivacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_invitacion", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente idPaciente;

    @Size(max = 255)
    @NotNull
    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @NotNull
    @Column(name = "fecha_expiracion", nullable = false)
    private OffsetDateTime fechaExpiracion;

    @Column(name = "fecha_uso")
    private OffsetDateTime fechaUso;


}
