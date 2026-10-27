package com.upc.backend.controllers;
import com.upc.backend.dtos.ReporteGeneralDTO;
import com.upc.backend.services.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/psicologos/{psicologoId}/reportes")
// HU32 y HU33: consulta indicadores consolidados y permite exportarlos como PDF.
public class ReporteController {
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    @PreAuthorize("@autorizacionService.puedeAccederPsicologo(#psicologoId)")
    // HU32 | END26: obtiene el reporte general para el periodo solicitado.
    public ResponseEntity<ReporteGeneralDTO> consultar(
            @PathVariable Long psicologoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        return ResponseEntity.ok(reporteService.consultar(psicologoId, fechaDesde, fechaHasta));
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("@autorizacionService.puedeAccederPsicologo(#psicologoId)")
    // HU33 | END27: genera la descarga PDF usando el mismo periodo del reporte general.
    public ResponseEntity<byte[]> exportarPdf(
            @PathVariable Long psicologoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename("reporte-aliviatech.pdf").build());
        return ResponseEntity.ok().headers(headers)
                .body(reporteService.exportarPdf(psicologoId, fechaDesde, fechaHasta));
    }
}
