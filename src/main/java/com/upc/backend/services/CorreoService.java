package com.upc.backend.services;

// Infraestructura común: contrato reutilizable para correos transaccionales del sistema.
public interface CorreoService {

    // HU02 | END24: envía el código, el enlace de activación y la referencia de términos.
    void enviarInvitacionPaciente(String destinatario,
                                  String nombres,
                                  String codigo,
                                  String enlaceActivacion,
                                  String enlaceTerminos);

    // HU05 | END04: envía el código temporal de recuperación a una cuenta habilitada.
    void enviarCodigoRecuperacion(String destinatario, String nombres, String codigo);
}
