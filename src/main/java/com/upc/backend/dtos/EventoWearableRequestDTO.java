package com.upc.backend.dtos;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import com.upc.backend.enums.EstadoConfirmacionEventos;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoWearableRequestDTO {

    @NotNull(message = "El estado de confirmación es obligatorio")
    private EstadoConfirmacionEventos estadoConfirmacion;

    private String contexto; // Comentario opcional donde el paciente explica qué hacía
}
