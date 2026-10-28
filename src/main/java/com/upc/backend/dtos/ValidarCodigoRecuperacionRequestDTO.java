package com.upc.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// HU05 | END05: identifica la recuperación persistida y el código de seis dígitos recibido.
public class ValidarCodigoRecuperacionRequestDTO {

    @NotNull
    private UUID recuperacionId;

    @NotBlank
    @Pattern(regexp = "\\d{6}", message = "debe contener 6 dígitos")
    private String codigo;
}
