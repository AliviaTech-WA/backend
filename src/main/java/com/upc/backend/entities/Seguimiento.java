package com.upc.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "seguimiento")
// Entidad compartida por HU02, HU08, HU19-HU33 para vincular paciente y psicólogo.
public class Seguimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente idPaciente;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_psicologo", nullable = false)
    private Psicologo idPsicologo;

    @NotNull
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "fecha_ultima_consulta")
    private OffsetDateTime fechaUltimaConsulta;

}
