package com.upc.backend.controllers;

import com.upc.backend.dtos.SincronizacionWearableRequestDTO;
import com.upc.backend.dtos.SincronizacionWearableResponseDTO;
import com.upc.backend.services.DispositivoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/dispositivos")
// HU11, HU13: recibe sincronizaciones y expone los datos consolidados del wearable.
public class DispositivoController {
    private final DispositivoService dispositivoService;

    public DispositivoController(DispositivoService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    @PostMapping("/{dispositivoId}/sincronizaciones")
    @PreAuthorize("@autorizacionService.puedeAccederDispositivo(#dispositivoId)")
    // HU11 | END10: recibe y almacena las lecturas del wearable.
    // HU13: procesa las lecturas para detectar variaciones superiores a 110 lpm.
    // Seguridad: permite sincronizar únicamente dispositivos autorizados.
    public ResponseEntity<SincronizacionWearableResponseDTO> sincronizar(@PathVariable Long dispositivoId, @Valid @RequestBody SincronizacionWearableRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dispositivoService.sincronizar(dispositivoId, request));
    }
}
