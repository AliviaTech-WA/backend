package com.upc.backend.controllers;

import com.upc.backend.dtos.ActivarPacienteRequestDTO;
import com.upc.backend.dtos.ActivarPacienteResponseDTO;
import com.upc.backend.services.ActivacionPacienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/activaciones-pacientes")
// HU03 | END02: endpoint público para completar la cuenta del paciente invitado.
public class ActivacionPacienteController {

    private final ActivacionPacienteService activacionPacienteService;

    public ActivacionPacienteController(ActivacionPacienteService activacionPacienteService) {
        this.activacionPacienteService = activacionPacienteService;
    }

    @PostMapping
    // HU03 | END02: devuelve el paciente y la fecha cuando la invitación se consume correctamente.
    public ResponseEntity<ActivarPacienteResponseDTO> activar(
            @Valid @RequestBody ActivarPacienteRequestDTO request) {
        return ResponseEntity.ok(activacionPacienteService.activar(request));
    }
}
