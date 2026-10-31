package com.upc.backend.dtos;

import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponseDTO {
    private Long id;
    private String titulo;
    private String mensaje;
    private String tipo;
    private Boolean leido;
    private OffsetDateTime fechaCreacion;
}