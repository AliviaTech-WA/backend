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
@Table(name = "dispositivo")
// HU11, HU14, HU15, HU21 y HU26 | END10-END13: wearable asignado al paciente.
public class Dispositivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dispositivo", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente idPaciente;

    @Size(max = 50)
    @NotNull
    @Column(name = "codigo_dispositivo", nullable = false, length = 50)
    private String codigoDispositivo;

    @Size(max = 100)
    @NotNull
    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @Size(max = 30)
    @NotNull
    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Size(max = 100)
    @Column(name = "alias", length = 100)
    private String alias;

    @NotNull
    @Column(name = "fecha_asignacion", nullable = false)
    private OffsetDateTime fechaAsignacion;

    @Column(name = "fecha_retiro")
    private OffsetDateTime fechaRetiro;

    @Column(name = "fecha_ultima_sincronizacion")
    private OffsetDateTime fechaUltimaSincronizacion;

}
