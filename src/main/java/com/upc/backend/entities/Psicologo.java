package com.upc.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "psicologo")
// HU01 y HU34: perfil profesional asociado uno a uno con Usuario.
public class Psicologo {
    @Id
    @Column(name = "id_psicologo", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_psicologo", nullable = false)
    private Usuario usuario;

    @Size(max = 30)
    @NotNull
    @Column(name = "numero_colegiatura", nullable = false, length = 30)
    private String numeroColegiatura;

    @Size(max = 100)
    @NotNull
    @Column(name = "especialidad", nullable = false, length = 100)
    private String especialidad;
}
