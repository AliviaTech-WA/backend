package com.upc.backend.services;

import com.upc.backend.dtos.MensajeResponseDTO;
import com.upc.backend.dtos.RestablecerContrasenaRequestDTO;
import com.upc.backend.dtos.SolicitarRecuperacionRequestDTO;
import com.upc.backend.dtos.SolicitarRecuperacionResponseDTO;
import com.upc.backend.dtos.ValidarCodigoRecuperacionRequestDTO;
import com.upc.backend.dtos.ValidarCodigoRecuperacionResponseDTO;

// HU05 | END04-END06: contrato del flujo público de recuperación de contraseña.
public interface RecuperacionContrasenaService {

    SolicitarRecuperacionResponseDTO solicitar(
            SolicitarRecuperacionRequestDTO request, String direccionIp);

    ValidarCodigoRecuperacionResponseDTO validarCodigo(
            ValidarCodigoRecuperacionRequestDTO request);

    MensajeResponseDTO restablecer(RestablecerContrasenaRequestDTO request);
}
