package com.upc.backend.security.services;

import com.upc.backend.entities.Dispositivo;
import com.upc.backend.entities.EventoWearable;
import com.upc.backend.entities.Sesion;
import com.upc.backend.repositories.DispositivoRepositorio;
import com.upc.backend.repositories.EventoWearableRepositorio;
import com.upc.backend.repositories.SeguimientoRepositorio;
import com.upc.backend.repositories.SesionRepositorio;
import com.upc.backend.security.entities.UsuarioPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("autorizacionService")
// Infraestructura común: aplica autorización por identidad, rol y seguimiento a los endpoints protegidos.
public class AutorizacionService {

    private final SeguimientoRepositorio seguimientoRepositorio;
    private final DispositivoRepositorio dispositivoRepositorio;
    private final EventoWearableRepositorio eventoWearableRepositorio;
    private final SesionRepositorio sesionRepositorio;

    public AutorizacionService(SeguimientoRepositorio seguimientoRepositorio,
                               DispositivoRepositorio dispositivoRepositorio,
                               EventoWearableRepositorio eventoWearableRepositorio,
                               SesionRepositorio sesionRepositorio) {
        this.seguimientoRepositorio = seguimientoRepositorio;
        this.dispositivoRepositorio = dispositivoRepositorio;
        this.eventoWearableRepositorio = eventoWearableRepositorio;
        this.sesionRepositorio = sesionRepositorio;
    }

    // HU06, HU09, HU14, HU15, HU18 y HU21: comprueba que el recurso pertenezca al usuario autenticado.
    public boolean esMismoUsuario(Long usuarioId) {
        UsuarioPrincipal principal = principal();
        return principal != null && principal.getId().equals(usuarioId);
    }

    // HU02, HU32, HU33 y HU34: restringe operaciones al mismo psicólogo autenticado.
    public boolean puedeAccederPsicologo(Long psicologoId) {
        UsuarioPrincipal principal = principal();
        return principal != null && "PSICOLOGO".equals(principal.getRol())
                && principal.getId().equals(psicologoId);
    }

    // HU10, HU12, HU14, HU17, HU19, HU21 y HU25-HU30: permite al paciente o a su psicólogo responsable.
    public boolean puedeAccederPaciente(Long pacienteId) {
        UsuarioPrincipal principal = principal();
        if (principal == null) {
            return false;
        }
        if ("PACIENTE".equals(principal.getRol())) {
            return principal.getId().equals(pacienteId);
        }
        return "PSICOLOGO".equals(principal.getRol())
                && seguimientoRepositorio.existsByIdPaciente_IdAndIdPsicologo_IdAndFechaFinIsNull(
                pacienteId, principal.getId());
    }

    // HU26 | END11: autoriza el wearable a través de su paciente asignado.
    public boolean puedeAccederDispositivo(Long dispositivoId) {
        Dispositivo dispositivo = dispositivoRepositorio.findById(dispositivoId).orElse(null);
        return dispositivo != null && puedeAccederPaciente(dispositivo.getIdPaciente().getId());
    }

    // Infraestructura de eventos: autoriza por el paciente propietario del dispositivo.
    public boolean puedeAccederEvento(Long eventoId) {
        EventoWearable evento = eventoWearableRepositorio.findById(eventoId).orElse(null);
        return evento != null && puedeAccederPaciente(evento.getIdDispositivo().getIdPaciente().getId());
    }

    // HU16 | END14: exige que quien responde sea el paciente propietario del evento.
    public boolean esPacienteDelEvento(Long eventoId) {
        UsuarioPrincipal principal = principal();
        EventoWearable evento = eventoWearableRepositorio.findById(eventoId).orElse(null);
        return principal != null && "PACIENTE".equals(principal.getRol()) && evento != null
                && principal.getId().equals(evento.getIdDispositivo().getIdPaciente().getId());
    }

    // HU20 y HU30 | END20: autoriza la sesión mediante el seguimiento del paciente.
    public boolean puedeAccederSesion(Long sesionId) {
        Sesion sesion = sesionRepositorio.findById(sesionId).orElse(null);
        return sesion != null && puedeAccederPaciente(sesion.getIdSeguimiento().getIdPaciente().getId());
    }

    // Infraestructura común: expone el principal para adaptar respuestas según el rol.
    public UsuarioPrincipal principalActual() {
        return principal();
    }

    // Infraestructura común: obtiene el principal tipado desde el contexto de Spring Security.
    private UsuarioPrincipal principal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getPrincipal() instanceof UsuarioPrincipal principal
                ? principal : null;
    }
}
