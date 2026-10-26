package com.upc.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "contacto_emergencia")
// HU21 | END28 y END30: contacto único asociado al paciente.
public class ContactoEmergencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contacto", nullable = false)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente idPaciente;

    @Size(max = 200)
    @NotNull
    @Column(name = "nombre_completo", nullable = false, length = 200)
    private String nombreCompleto;

    @Size(max = 20)
    @NotNull
    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Size(max = 254)
    @Column(name = "correo", length = 254)
    private String correo;

    @Size(max = 50)
    @NotNull
    @Column(name = "relacion", nullable = false, length = 50)
    private String relacion;


}
