package com.upc.backend.services;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

// HU31 | END25: contrato interno entre la agregación de datos y el modelo descriptivo.
public interface AnalisisIaGenerador {
    ResultadoGenerado generar(DatosAgregados datos);

    record DatosAgregados(String fuente,
                          OffsetDateTime periodoDesde,
                          OffsetDateTime periodoHasta,
                          ResumenRegistros registros,
                          ResumenWearable wearable,
                          List<ResumenDiario> serieDiaria) {
    }

    record ResumenRegistros(int total,
                            double ansiedadPromedio,
                            int ansiedadMaxima,
                            long registrosAnsiedadAlta,
                            Map<String, Long> estadosAnimo) {
    }

    record ResumenWearable(int lecturasCardiacas,
                           Double frecuenciaCardiacaPromedio,
                           Integer frecuenciaCardiacaMaxima,
                           int resumenesDiarios,
                           Double horasSuenoPromedio,
                           Double pasosPromedio,
                           int episodiosConfirmados,
                           int diasConEpisodios) {
    }

    record ResumenDiario(LocalDate fecha,
                         int registrosBienestar,
                         Double ansiedadPromedio,
                         int lecturasCardiacas,
                         Double frecuenciaCardiacaPromedio,
                         Integer frecuenciaCardiacaMaxima,
                         Double horasSueno,
                         Double pasos,
                         int episodiosConfirmados) {
    }

    record ResultadoGenerado(String resumen,
                             String coincidencias,
                             String puntosRevision) {
    }
}
