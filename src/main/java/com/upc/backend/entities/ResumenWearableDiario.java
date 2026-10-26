package com.upc.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "resumen_wearable_diario")
// HU11, HU12, HU13 y HU26: resumen diario de pasos y sueño del wearable.
public class ResumenWearableDiario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resumen", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_dispositivo", nullable = false)
    private Dispositivo idDispositivo;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull
    @Column(name = "pasos", nullable = false)
    private Integer pasos;

    @NotNull
    @Column(name = "minutos_sueno", nullable = false)
    private Short minutosSueno;


}
