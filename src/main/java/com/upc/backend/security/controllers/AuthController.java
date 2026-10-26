package com.upc.backend.security.controllers;

import com.upc.backend.security.dtos.AuthRequestDTO;
import com.upc.backend.security.dtos.AuthResponseDTO;
import com.upc.backend.security.services.AutenticacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/autenticacion")
// HU04: expone el acceso autenticado y delega la emisión del JWT.
public class AuthController {

    private final AutenticacionService autenticacionService;

    public AuthController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    @PostMapping("/iniciar-sesion")
    // HU04 | END03: valida las credenciales e inicia la sesión lógica del usuario.
    public ResponseEntity<AuthResponseDTO> iniciarSesion(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(autenticacionService.autenticar(request));
    }
}
