package com.upc.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "lectura_wearable")
// HU11, HU12, HU13 y HU26: medición cardíaca recibida desde el wearable.
public class LecturaWearable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lectura", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_dispositivo", nullable = false)
    private Dispositivo idDispositivo;

    @NotNull
    @Column(name = "fecha_hora", nullable = false)
    private OffsetDateTime fechaHora;

    @NotNull
    @Column(name = "frecuencia_cardiaca", nullable = false)
    private Short frecuenciaCardiaca;


}
