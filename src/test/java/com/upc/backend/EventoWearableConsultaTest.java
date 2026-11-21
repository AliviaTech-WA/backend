package com.upc.backend;

import com.upc.backend.dtos.EventoPacienteDTO;
import com.upc.backend.entities.Dispositivo;
import com.upc.backend.entities.EventoWearable;
import com.upc.backend.entities.Paciente;
import com.upc.backend.enums.EstadoConfirmacionEventos;
import com.upc.backend.repositories.EventoWearableRepositorio;
import com.upc.backend.repositories.LecturaWearableRepositorio;
import com.upc.backend.repositories.PacienteRepositorio;
import com.upc.backend.repositories.ResumenWearableDiarioRepositorio;
import com.upc.backend.security.entities.UsuarioPrincipal;
import com.upc.backend.security.services.AutorizacionService;
import com.upc.backend.serviceimpl.PacienteServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventoWearableConsultaTest {

    @Test
    void pacienteRecibePendientesConfirmadosYDenegados() {
        var servicio = new PacienteServiceImpl();
        var pacientes = mock(PacienteRepositorio.class);
        var eventos = mock(EventoWearableRepositorio.class);
        var lecturas = mock(LecturaWearableRepositorio.class);
        var resumenes = mock(ResumenWearableDiarioRepositorio.class);
        var autorizacion = mock(AutorizacionService.class);
        var principal = mock(UsuarioPrincipal.class);
        ReflectionTestUtils.setField(servicio, "pacienteRepositorio", pacientes);
        ReflectionTestUtils.setField(servicio, "eventoRepositorio", eventos);
        ReflectionTestUtils.setField(servicio, "lecturaRepositorio", lecturas);
        ReflectionTestUtils.setField(servicio, "resumenRepositorio", resumenes);
        ReflectionTestUtils.setField(servicio, "autorizacionService", autorizacion);
        when(pacientes.findById(1L)).thenReturn(Optional.of(new Paciente()));
        when(autorizacion.principalActual()).thenReturn(principal);
        when(principal.getRol()).thenReturn("PACIENTE");

        var pendiente = evento(10L, null);
        var confirmado = evento(11L, EstadoConfirmacionEventos.SI);
        var denegado = evento(12L, EstadoConfirmacionEventos.NO);
        when(eventos.findByIdDispositivo_IdPaciente_IdOrderByFechaHoraInicioDesc(1L))
                .thenReturn(List.of(pendiente, confirmado, denegado));
        when(lecturas.findByIdDispositivo_IdAndFechaHoraBetween(anyLong(), any(), any()))
                .thenReturn(List.of());

        List<EventoPacienteDTO> resultado = servicio.listarEventosWearable(1L);
        assertEquals(3, resultado.size());
        assertNull(resultado.get(0).getEstadoConfirmacion());
        assertEquals("SI", resultado.get(1).getEstadoConfirmacion());
        assertEquals("NO", resultado.get(2).getEstadoConfirmacion());
        assertEquals(15L, resultado.get(0).getDuracionMinutos());
        assertEquals(15L, resultado.get(0).getTiempoVariacion());
        assertEquals("contexto", resultado.get(0).getComentario());
        assertNull(resultado.get(0).getNivelActividad());
        verify(eventos, never()).findByIdDispositivo_IdPaciente_IdAndEstadoConfirmacionOrderByFechaHoraInicioDesc(
                anyLong(), any());
    }

    @Test
    void psicologoSoloConsultaConfirmados() {
        var servicio = new PacienteServiceImpl();
        var pacientes = mock(PacienteRepositorio.class);
        var eventos = mock(EventoWearableRepositorio.class);
        var lecturas = mock(LecturaWearableRepositorio.class);
        var autorizacion = mock(AutorizacionService.class);
        var principal = mock(UsuarioPrincipal.class);
        ReflectionTestUtils.setField(servicio, "pacienteRepositorio", pacientes);
        ReflectionTestUtils.setField(servicio, "eventoRepositorio", eventos);
        ReflectionTestUtils.setField(servicio, "lecturaRepositorio", lecturas);
        ReflectionTestUtils.setField(servicio, "resumenRepositorio", mock(ResumenWearableDiarioRepositorio.class));
        ReflectionTestUtils.setField(servicio, "autorizacionService", autorizacion);
        when(pacientes.findById(1L)).thenReturn(Optional.of(new Paciente()));
        when(autorizacion.principalActual()).thenReturn(principal);
        when(principal.getRol()).thenReturn("PSICOLOGO");
        when(eventos.findByIdDispositivo_IdPaciente_IdAndEstadoConfirmacionOrderByFechaHoraInicioDesc(
                1L, EstadoConfirmacionEventos.SI)).thenReturn(List.of(evento(11L, EstadoConfirmacionEventos.SI)));
        when(lecturas.findByIdDispositivo_IdAndFechaHoraBetween(anyLong(), any(), any()))
                .thenReturn(List.of());

        assertEquals(1, servicio.listarEventosWearable(1L).size());
        verify(eventos, never()).findByIdDispositivo_IdPaciente_IdOrderByFechaHoraInicioDesc(anyLong());
    }

    private EventoWearable evento(Long id, EstadoConfirmacionEventos estado) {
        var dispositivo = new Dispositivo();
        dispositivo.setId(2L);
        var evento = new EventoWearable();
        evento.setId(id);
        evento.setIdDispositivo(dispositivo);
        evento.setFechaHoraInicio(OffsetDateTime.parse("2026-11-20T10:00:00-05:00"));
        evento.setFechaHoraFin(OffsetDateTime.parse("2026-11-20T10:15:00-05:00"));
        evento.setEstadoConfirmacion(estado);
        evento.setContextoPaciente("contexto");
        return evento;
    }
}
