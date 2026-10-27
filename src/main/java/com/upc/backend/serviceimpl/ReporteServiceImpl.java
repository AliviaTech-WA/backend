package com.upc.backend.serviceimpl;
import com.upc.backend.dtos.ReporteGeneralDTO;
import com.upc.backend.entities.EventoWearable;
import com.upc.backend.entities.RegistroBienestar;
import com.upc.backend.entities.Seguimiento;
import com.upc.backend.exceptions.InvalidRequestException;
import com.upc.backend.exceptions.ResourceNotFoundException;
import com.upc.backend.repositories.EventoWearableRepositorio;
import com.upc.backend.repositories.PsicologoRepositorio;
import com.upc.backend.repositories.RegistroBienestarRepositorio;
import com.upc.backend.repositories.SeguimientoRepositorio;
import com.upc.backend.services.ReporteService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
// HU32 y HU33: calcula reportes generales y reutiliza esos datos para el PDF.
public class ReporteServiceImpl implements ReporteService {
    private final PsicologoRepositorio psicologoRepositorio;
    private final SeguimientoRepositorio seguimientoRepositorio;
    private final RegistroBienestarRepositorio registroRepositorio;
    private final EventoWearableRepositorio eventoRepositorio;

    public ReporteServiceImpl(PsicologoRepositorio psicologoRepositorio,
                              SeguimientoRepositorio seguimientoRepositorio,
                              RegistroBienestarRepositorio registroRepositorio,
                              EventoWearableRepositorio eventoRepositorio) {
        this.psicologoRepositorio = psicologoRepositorio;
        this.seguimientoRepositorio = seguimientoRepositorio;
        this.registroRepositorio = registroRepositorio;
        this.eventoRepositorio = eventoRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    // HU32 | END26: agrega actividad de los seguimientos activos dentro del periodo solicitado.
    public ReporteGeneralDTO consultar(Long psicologoId, LocalDate fechaDesde, LocalDate fechaHasta) {
        if (!psicologoRepositorio.existsById(psicologoId)) {
            throw new ResourceNotFoundException("No se encontró el psicólogo con ID " + psicologoId + ".");
        }
        LocalDate hasta = fechaHasta == null ? LocalDate.now() : fechaHasta;
        LocalDate desde = fechaDesde == null ? hasta.minusDays(27) : fechaDesde;
        if (hasta.isBefore(desde)) {
            throw new InvalidRequestException("fechaHasta no puede ser anterior a fechaDesde.");
        }
        OffsetDateTime desdeTs = desde.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime hastaTs = hasta.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC).minusNanos(1);
        List<Seguimiento> seguimientos = seguimientoRepositorio.findByIdPsicologo_IdAndFechaFinIsNull(psicologoId);
        List<PacienteDatos> pacientes = new ArrayList<>();
        for (Seguimiento seguimiento : seguimientos) {
            Long pacienteId = seguimiento.getIdPaciente().getId();
            List<RegistroBienestar> registros = registroRepositorio
                    .findByIdPaciente_IdAndFechaHoraBetweenOrderByFechaHoraDesc(pacienteId, desdeTs, hastaTs);
            List<EventoWearable> episodios = eventoRepositorio
                    .findByIdDispositivo_IdPaciente_IdAndFechaHoraInicioBetween(pacienteId, desdeTs, hastaTs)
                    .stream().filter(e -> "CONFIRMADO".equals(e.getEstadoConfirmacion())).toList();
            pacientes.add(new PacienteDatos(seguimiento, registros, episodios));
        }
        long registrosTotales = pacientes.stream().mapToLong(p -> p.registros().size()).sum();
        long episodiosTotales = pacientes.stream().mapToLong(p -> p.episodios().size()).sum();
        long conEpisodios = pacientes.stream().filter(p -> !p.episodios().isEmpty()).count();
        long conActividad = pacientes.stream().filter(p -> !p.registros().isEmpty() || !p.episodios().isEmpty()).count();
        double promedio = seguimientos.isEmpty() ? 0.0 : (double) registrosTotales / seguimientos.size();
        double porcentaje = seguimientos.isEmpty() ? 0.0 : conActividad * 100.0 / seguimientos.size();

        int semanas = Math.max(1, (int) Math.ceil((ChronoUnit.DAYS.between(desde, hasta) + 1) / 7.0));
        List<ReporteGeneralDTO.ActividadSemanalDTO> actividad = new ArrayList<>();
        List<ReporteGeneralDTO.PacientesActivosSemanaDTO> activosSemana = new ArrayList<>();
        for (int i = 0; i < semanas; i++) {
            LocalDate inicioSemana = desde.plusDays(i * 7L);
            LocalDate finSemana = inicioSemana.plusDays(6).isAfter(hasta) ? hasta : inicioSemana.plusDays(6);
            long registrosSemana = pacientes.stream().flatMap(p -> p.registros().stream())
                    .filter(r -> !r.getFechaHora().toLocalDate().isBefore(inicioSemana)
                            && !r.getFechaHora().toLocalDate().isAfter(finSemana)).count();
            long episodiosSemana = pacientes.stream().flatMap(p -> p.episodios().stream())
                    .filter(e -> !e.getFechaHoraInicio().toLocalDate().isBefore(inicioSemana)
                            && !e.getFechaHoraInicio().toLocalDate().isAfter(finSemana)).count();
            long activos = seguimientos.stream().filter(s -> !s.getFechaInicio().isAfter(finSemana)).count();
            actividad.add(new ReporteGeneralDTO.ActividadSemanalDTO(i + 1, registrosSemana, episodiosSemana));
            activosSemana.add(new ReporteGeneralDTO.PacientesActivosSemanaDTO(i + 1, activos));
        }

        List<ReporteGeneralDTO.PacienteActividadDTO> mayorActividad = pacientes.stream()
                .sorted(Comparator.comparingLong(PacienteDatos::totalActividad).reversed())
                .map(p -> new ReporteGeneralDTO.PacienteActividadDTO(
                        p.seguimiento().getIdPaciente().getId(),
                        p.nombreCompleto(), p.registros().size(), p.episodios().size()))
                .toList();
        return new ReporteGeneralDTO(seguimientos.size(), registrosTotales, episodiosTotales, promedio,
                actividad, mayorActividad, conEpisodios,
                new ReporteGeneralDTO.CoberturaDTO(conActividad, seguimientos.size(), porcentaje), activosSemana);
    }

    // HU32 y HU33: estructura auxiliar para agrupar actividad por seguimiento durante el cálculo.
    private record PacienteDatos(Seguimiento seguimiento,
                                 List<RegistroBienestar> registros,
                                 List<EventoWearable> episodios) {
        long totalActividad() {
            return registros.size() + episodios.size();
        }
        String nombreCompleto() {
            return seguimiento.getIdPaciente().getUsuario().getNombres() + " "
                    + seguimiento.getIdPaciente().getUsuario().getApellidos();
        }
    }

    @Override
    // HU33 | END27: genera un PDF a partir del resultado calculado por END26.
    public byte[] exportarPdf(Long psicologoId, LocalDate fechaDesde, LocalDate fechaHasta) {
        ReporteGeneralDTO reporte = consultar(psicologoId, fechaDesde, fechaHasta);
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                PDType1Font titulo = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font texto = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                float y = 730;
                y = escribir(content, titulo, 16, 60, y, "Reporte general de AliviaTech");
                y = escribir(content, texto, 11, 60, y - 12, "Pacientes activos: " + reporte.getPacientesActivos());
                y = escribir(content, texto, 11, 60, y, "Registros recibidos: " + reporte.getRegistrosRecibidos());
                y = escribir(content, texto, 11, 60, y, "Episodios confirmados: " + reporte.getEpisodiosConfirmados());
                y = escribir(content, texto, 11, 60, y, String.format("Promedio de registros por paciente: %.2f", reporte.getPromedioRegistrosPorPaciente()));
                y = escribir(content, texto, 11, 60, y, String.format("Cobertura: %.2f%%", reporte.getCobertura().getPorcentaje()));
                y = escribir(content, titulo, 13, 60, y - 12, "Actividad semanal");
                for (ReporteGeneralDTO.ActividadSemanalDTO semana : reporte.getActividadSemanal()) {
                    y = escribir(content, texto, 10, 70, y,
                            "Semana " + semana.getSemana() + ": " + semana.getRegistros()
                                    + " registros, " + semana.getEpisodios() + " episodios");
                }
            }
            document.save(output);
            return output.toByteArray();
        }
        catch (IOException ex) {
            throw new IllegalStateException("No se pudo generar el reporte PDF.", ex);
        }
    }

    // HU33: escribe una línea y devuelve la siguiente coordenada vertical del PDF.
    private float escribir(PDPageContentStream content, PDType1Font fuente, float tamano,
                           float x, float y, String texto) throws IOException {
        content.beginText();
        content.setFont(fuente, tamano);
        content.newLineAtOffset(x, y);
        content.showText(texto);
        content.endText();
        return y - 18;
    }
}
