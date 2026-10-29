package com.upc.backend.controllers;

import com.upc.backend.dtos.SincronizacionWearableRequestDTO;
import com.upc.backend.dtos.SincronizacionWearableResponseDTO;
import com.upc.backend.services.DispositivoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dispositivos")
// HU11, HU13 y HU26: recibe sincronizaciones y expone los datos consolidados del wearable.
public class DispositivoController {
    private final DispositivoService dispositivoService;

    public DispositivoController(DispositivoService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    @PostMapping("/{dispositivoId}/sincronizaciones")
    // HU11 y apoyo parcial a HU13 | END10: almacena la sincronización; la detección automática no está en esta cadena.
    public ResponseEntity<SincronizacionWearableResponseDTO> sincronizar(
            @PathVariable Long dispositivoId,
            @Valid @RequestBody SincronizacionWearableRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dispositivoService.sincronizar(dispositivoId, request));
    }
}
