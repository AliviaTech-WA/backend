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
@Table(name = "evento_wearable")
// HU12, HU13, HU16, HU17 y HU27 | END14-END15: variación detectada y respuesta del paciente.
public class EventoWearable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_dispositivo", nullable = false)
    private Dispositivo idDispositivo;

    @NotNull
    @Column(name = "fecha_hora_inicio", nullable = false)
    private OffsetDateTime fechaHoraInicio;

    @NotNull
    @Column(name = "fecha_hora_fin", nullable = false)
    private OffsetDateTime fechaHoraFin;

    @Column(name = "descripcion_variacion", length = Integer.MAX_VALUE)
    private String descripcionVariacion;

    @Size(max = 20)
    @NotNull
    @Column(name = "estado_confirmacion", nullable = false, length = 20)
    private String estadoConfirmacion;

    @Column(name = "fecha_respuesta")
    private OffsetDateTime fechaRespuesta;

    @Column(name = "contexto_paciente", length = Integer.MAX_VALUE)
    private String contextoPaciente;


}
