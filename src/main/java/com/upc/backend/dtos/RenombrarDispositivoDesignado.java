package com.upc.backend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RenombrarDispositivoDesignado {

    @Schema(description = "Nuevo alias del dispositivo", example = "Pulsera de Marta")
    @NotBlank(message = "El alias no puede estar vacío")
    @Size(max = 100, message = "El alias no puede superar 100 caracteres")
    private String alias;
}
