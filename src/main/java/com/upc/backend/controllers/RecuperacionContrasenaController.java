package com.upc.backend.controllers;

import com.upc.backend.dtos.MensajeResponseDTO;
import com.upc.backend.dtos.RestablecerContrasenaRequestDTO;
import com.upc.backend.dtos.SolicitarRecuperacionRequestDTO;
import com.upc.backend.dtos.SolicitarRecuperacionResponseDTO;
import com.upc.backend.dtos.ValidarCodigoRecuperacionRequestDTO;
import com.upc.backend.dtos.ValidarCodigoRecuperacionResponseDTO;
import com.upc.backend.services.RecuperacionContrasenaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recuperaciones-contrasena")
// HU05 | END04-END06: endpoints públicos para solicitar, validar y consumir una recuperación.
public class RecuperacionContrasenaController {

    private final RecuperacionContrasenaService recuperacionService;

    public RecuperacionContrasenaController(RecuperacionContrasenaService recuperacionService) {
        this.recuperacionService = recuperacionService;
    }

    @PostMapping
    // HU05 | END04: solicita o reemplaza el código aplicando límites por correo e IP remota.
    public ResponseEntity<SolicitarRecuperacionResponseDTO> solicitar(
            @Valid @RequestBody SolicitarRecuperacionRequestDTO request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(recuperacionService.solicitar(request, httpRequest.getRemoteAddr()));
    }

    @PostMapping("/validar-codigo")
    // HU05 | END05: valida el código y emite un token temporal de restablecimiento.
    public ResponseEntity<ValidarCodigoRecuperacionResponseDTO> validarCodigo(
            @Valid @RequestBody ValidarCodigoRecuperacionRequestDTO request) {
        return ResponseEntity.ok(recuperacionService.validarCodigo(request));
    }

    @PutMapping("/restablecer")
    // HU05 | END06: consume el token y reemplaza la contraseña con BCrypt.
    public ResponseEntity<MensajeResponseDTO> restablecer(
            @Valid @RequestBody RestablecerContrasenaRequestDTO request) {
        return ResponseEntity.ok(recuperacionService.restablecer(request));
    }
}
