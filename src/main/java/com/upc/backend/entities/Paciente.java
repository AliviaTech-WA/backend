package com.upc.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "paciente")
// Entidad compartida por las HU de paciente; usa el mismo identificador que Usuario mediante MapsId.
public class Paciente {
    @Id
    @Column(name = "id_paciente", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Usuario usuario;

    @NotNull
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;


}
