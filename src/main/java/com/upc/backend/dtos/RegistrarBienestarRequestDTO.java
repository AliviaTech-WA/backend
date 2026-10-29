package com.upc.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
// HU09 | END08: nivel de ansiedad, estado de ánimo y comentario informado por el paciente.
public class RegistrarBienestarRequestDTO {
    @NotNull
    @jakarta.validation.constraints.Min(1)
    @jakarta.validation.constraints.Max(10)
    private Short nivelAnsiedad;

    @NotBlank
    @Size(max = 20)
    @jakarta.validation.constraints.Pattern(regexp = "TRANQUILO|NEUTRAL|TENSO|CANSADO")
    private String estadoAnimo;

    private String comentario;
}
