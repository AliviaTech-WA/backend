package com.upc.backend;

import com.upc.backend.entities.Dispositivo;
import com.upc.backend.entities.EventoWearable;
import com.upc.backend.entities.LecturaWearable;
import com.upc.backend.entities.Paciente;
import com.upc.backend.entities.Usuario;
import com.upc.backend.repositories.EventoWearableRepositorio;
import com.upc.backend.repositories.LecturaWearableRepositorio;
import com.upc.backend.serviceimpl.WearableEventoDetector;
import com.upc.backend.services.NotificacionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WearableEventoDetectorTest {

    @Test
    void altosConsecutivosSeExtiendenYLecturaNormalOBrechaInicianOtroEvento() {
        var lecturas = mock(LecturaWearableRepositorio.class);
        var eventos = mock(EventoWearableRepositorio.class);
        var notificaciones = mock(NotificacionService.class);
        when(eventos.save(any(EventoWearable.class))).thenAnswer(call -> call.getArgument(0));
        var detector = new WearableEventoDetector(lecturas, eventos, notificaciones);
        var inicio = OffsetDateTime.parse("2026-11-21T10:00:00-05:00");

        detector.procesar(dispositivo(), List.of(
                lectura(inicio, 120), lectura(inicio.plusMinutes(15), 121),
                lectura(inicio.plusMinutes(30), 110), lectura(inicio.plusMinutes(45), 130),
                lectura(inicio.plusMinutes(90), 140)));

        var captor = ArgumentCaptor.forClass(EventoWearable.class);
        verify(eventos, times(4)).save(captor.capture());
        var guardados = captor.getAllValues();
        assertSame(guardados.get(0), guardados.get(1));
        assertEquals(inicio.plusMinutes(30), guardados.get(0).getFechaHoraFin());
        assertEquals(inicio.plusMinutes(45), guardados.get(2).getFechaHoraInicio());
        assertEquals(inicio.plusMinutes(60), guardados.get(2).getFechaHoraFin());
        assertEquals(inicio.plusMinutes(90), guardados.get(3).getFechaHoraInicio());
        assertEquals(inicio.plusMinutes(105), guardados.get(3).getFechaHoraFin());
        verify(notificaciones, times(3)).crearNotificacion(any(), any(), any(), any());
    }

    @Test
    void continuaIntervaloNuevoPeroNoModificaEventoAntiguoDeDuracionCero() {
        var lecturas = mock(LecturaWearableRepositorio.class);
        var eventos = mock(EventoWearableRepositorio.class);
        var notificaciones = mock(NotificacionService.class);
        when(eventos.save(any(EventoWearable.class))).thenAnswer(call -> call.getArgument(0));
        var detector = new WearableEventoDetector(lecturas, eventos, notificaciones);
        var inicio = OffsetDateTime.parse("2026-11-21T10:00:00-05:00");
        when(lecturas.findFirstByIdDispositivo_IdAndFechaHoraBeforeOrderByFechaHoraDesc(2L, inicio.plusMinutes(15)))
                .thenReturn(Optional.of(lectura(inicio, 120)));
        var reciente = new EventoWearable();
        reciente.setFechaHoraInicio(inicio);
        reciente.setFechaHoraFin(inicio.plusMinutes(15));
        when(eventos.findFirstByIdDispositivo_IdOrderByFechaHoraInicioDesc(2L))
                .thenReturn(Optional.of(reciente));

        detector.procesar(dispositivo(), List.of(lectura(inicio.plusMinutes(15), 130)));
        assertEquals(inicio.plusMinutes(30), reciente.getFechaHoraFin());
        verify(notificaciones, never()).crearNotificacion(any(), any(), any(), any());

        var historico = new EventoWearable();
        historico.setFechaHoraInicio(inicio);
        historico.setFechaHoraFin(inicio);
        when(eventos.findFirstByIdDispositivo_IdOrderByFechaHoraInicioDesc(2L))
                .thenReturn(Optional.of(historico));
        detector.procesar(dispositivo(), List.of(lectura(inicio.plusMinutes(15), 130)));
        assertEquals(inicio, historico.getFechaHoraFin());
        verify(notificaciones, times(1)).crearNotificacion(any(), any(), any(), any());
    }

    private Dispositivo dispositivo() {
        var usuario = new Usuario();
        usuario.setNombres("Paciente");
        var paciente = new Paciente();
        paciente.setId(1L);
        paciente.setUsuario(usuario);
        var dispositivo = new Dispositivo();
        dispositivo.setId(2L);
        dispositivo.setIdPaciente(paciente);
        return dispositivo;
    }

    private LecturaWearable lectura(OffsetDateTime fechaHora, int frecuencia) {
        var lectura = new LecturaWearable();
        lectura.setFechaHora(fechaHora);
        lectura.setFrecuenciaCardiaca((short) frecuencia);
        return lectura;
    }
}
