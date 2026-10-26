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
@Table(name = "consentimiento")
// HU03 | END02: registra la versión de términos aceptada al activar la cuenta.
public class Consentimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consentimiento", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente idPaciente;

    @Size(max = 20)
    @NotNull
    @Column(name = "version_documento", nullable = false, length = 20)
    private String versionDocumento;

    @NotNull
    @Column(name = "fecha_aceptacion", nullable = false)
    private OffsetDateTime fechaAceptacion;


}
