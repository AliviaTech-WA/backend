package com.upc.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "sesion")
// HU19, HU20, HU28, HU29 y HU30 | END18-END20: sesión asociada al seguimiento.
public class Sesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_seguimiento", nullable = false)
    private Seguimiento idSeguimiento;

    @NotNull
    @Column(name = "fecha_sesion", nullable = false)
    private OffsetDateTime fechaSesion;

    @Column(name = "nota_interna", length = Integer.MAX_VALUE)
    private String notaInterna;

    @Column(name = "resumen_compartido", length = Integer.MAX_VALUE)
    private String resumenCompartido;

    @Column(name = "recomendacion_compartida", length = Integer.MAX_VALUE)
    private String recomendacionCompartida;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha_registro", nullable = false)
    private OffsetDateTime fechaRegistro;


}
