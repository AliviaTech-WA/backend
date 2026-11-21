package com.upc.backend;

import com.upc.backend.dtos.ActualizarSesionRequestDTO;
import com.upc.backend.entities.Paciente;
import com.upc.backend.entities.Psicologo;
import com.upc.backend.entities.Seguimiento;
import com.upc.backend.entities.Sesion;
import com.upc.backend.entities.Usuario;
import com.upc.backend.exceptions.ResourceNotFoundException;
import com.upc.backend.repositories.DispositivoRepositorio;
import com.upc.backend.repositories.EventoWearableRepositorio;
import com.upc.backend.repositories.SeguimientoRepositorio;
import com.upc.backend.repositories.SesionRepositorio;
import com.upc.backend.security.entities.UsuarioPrincipal;
import com.upc.backend.security.services.AutorizacionService;
import com.upc.backend.serviceimpl.SesionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SesionModificacionTest {

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void putActualizaFechaYLimpiaCamposOpcionalesOmitidos() {
        var sesiones = mock(SesionRepositorio.class);
        var servicio = new SesionServiceImpl(mock(SeguimientoRepositorio.class), sesiones, mock(ModelMapper.class));
        var sesion = new Sesion();
        sesion.setId(5L);
        sesion.setFechaSesion(OffsetDateTime.parse("2026-11-20T10:00:00-05:00"));
        sesion.setFechaRegistro(OffsetDateTime.parse("2026-11-01T10:00:00-05:00"));
        sesion.setNotaInterna("nota anterior");
        sesion.setResumenCompartido("resumen anterior");
        sesion.setRecomendacionCompartida("recomendacion anterior");
        when(sesiones.findById(5L)).thenReturn(Optional.of(sesion));
        when(sesiones.save(sesion)).thenReturn(sesion);
        var request = new ActualizarSesionRequestDTO();
        request.setFechaSesion(OffsetDateTime.parse("2026-11-21T11:00:00-05:00"));
        request.setNotaInterna("nota nueva");

        var resultado = servicio.actualizar(5L, request);

        assertEquals(request.getFechaSesion(), resultado.getFechaSesion());
        assertEquals("nota nueva", resultado.getNotaInterna());
        assertNull(resultado.getResumenCompartido());
        assertNull(resultado.getRecomendacionCompartida());
        assertEquals(OffsetDateTime.parse("2026-11-01T10:00:00-05:00"), sesion.getFechaRegistro());
        verify(sesiones).save(sesion);
    }

    @Test
    void deleteEliminaYAmbasOperacionesReutilizanEl404() {
        var sesiones = mock(SesionRepositorio.class);
        var servicio = new SesionServiceImpl(mock(SeguimientoRepositorio.class), sesiones, mock(ModelMapper.class));
        var sesion = new Sesion();
        when(sesiones.findById(5L)).thenReturn(Optional.of(sesion));
        servicio.eliminar(5L);
        verify(sesiones).delete(sesion);

        assertThrows(ResourceNotFoundException.class, () -> servicio.eliminar(99L));
        assertThrows(ResourceNotFoundException.class,
                () -> servicio.actualizar(99L, new ActualizarSesionRequestDTO()));
    }

    @Test
    void soloPsicologoDelSeguimientoConAccesoVigentePuedeModificar() {
        var seguimientos = mock(SeguimientoRepositorio.class);
        var sesiones = mock(SesionRepositorio.class);
        var autorizacion = new AutorizacionService(seguimientos, mock(DispositivoRepositorio.class),
                mock(EventoWearableRepositorio.class), sesiones);
        var paciente = new Paciente();
        paciente.setId(7L);
        var psicologo = new Psicologo();
        psicologo.setId(3L);
        var seguimiento = new Seguimiento();
        seguimiento.setIdPaciente(paciente);
        seguimiento.setIdPsicologo(psicologo);
        var sesion = new Sesion();
        sesion.setIdSeguimiento(seguimiento);
        when(sesiones.findById(5L)).thenReturn(Optional.of(sesion));
        when(seguimientos.existsByIdPaciente_IdAndIdPsicologo_IdAndFechaFinIsNull(7L, 3L))
                .thenReturn(true);
        autenticar(3L, "PSICOLOGO");
        assertTrue(autorizacion.puedeModificarSesion(5L));
        assertTrue(autorizacion.puedeModificarSesion(99L)); // deja el 404 al servicio

        psicologo.setId(4L);
        assertFalse(autorizacion.puedeModificarSesion(5L));
        psicologo.setId(3L);
        when(seguimientos.existsByIdPaciente_IdAndIdPsicologo_IdAndFechaFinIsNull(7L, 3L))
                .thenReturn(false);
        assertFalse(autorizacion.puedeModificarSesion(5L));
        autenticar(7L, "PACIENTE");
        assertFalse(autorizacion.puedeModificarSesion(5L));
    }

    private void autenticar(Long id, String rol) {
        var usuario = new Usuario();
        usuario.setId(id);
        usuario.setRol(rol);
        var principal = new UsuarioPrincipal(usuario);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }
}
