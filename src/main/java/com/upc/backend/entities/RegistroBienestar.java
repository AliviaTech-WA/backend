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
@Table(name = "registro_bienestar")
// HU09, HU10 y HU25 | END08-END09: registro voluntario de bienestar del paciente.
public class RegistroBienestar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente idPaciente;

    @NotNull
    @Column(name = "fecha_hora", nullable = false)
    private OffsetDateTime fechaHora;

    @NotNull
    @Column(name = "nivel_ansiedad", nullable = false)
    private Short nivelAnsiedad;

    @Size(max = 20)
    @NotNull
    @Column(name = "estado_animo", nullable = false, length = 20)
    private String estadoAnimo;

    @Column(name = "comentario", length = Integer.MAX_VALUE)
    private String comentario;


}
