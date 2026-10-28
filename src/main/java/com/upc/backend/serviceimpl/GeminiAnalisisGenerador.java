package com.upc.backend.serviceimpl;

import com.upc.backend.services.AnalisisIaGenerador;
import org.springframework.stereotype.Service;
import com.upc.backend.exceptions.AnalisisIaNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class GeminiAnalisisGenerador implements AnalisisIaGenerador{
    private static final Logger log = LoggerFactory.getLogger(GeminiAnalisisGenerador.class);
    private static final String CABECERA_API_KEY = "x-goog-api-key";
    private static final int MAXIMO_MENSAJE_LOG = 300;
    private static final Pattern CLAVE_GOOGLE = Pattern.compile("AIza[0-9A-Za-z_-]{20,}");
    private static final Pattern PARAMETRO_KEY = Pattern.compile("(?i)([?&]key=)[^&\\s]+");
    private static final Pattern ASIGNACION_CLAVE = Pattern.compile(
            "(?i)((?:api[_ -]?key|authorization|bearer)\\s*[:=]\\s*)[^,;\\s]+");
    private static final String INSTRUCCIONES = """
            Genera un análisis breve en español a partir exclusivamente de los datos agregados recibidos.
            Describe tendencias, variaciones y coincidencias temporales observables sin inferir causalidad.
            No identifiques personas ni supongas datos que no estén presentes.
            No realices ni sugieras diagnósticos clínicos, predicciones de crisis, tratamientos ni recomendaciones clínicas.
            En puntosRevision menciona únicamente aspectos de los datos que el psicólogo podría revisar.
            Si una relación no está respaldada por los agregados, indícalo explícitamente.
            """;
    private static final String AVISO_DESCRIPTIVO =
            "El resultado es descriptivo y no constituye diagnóstico, predicción ni recomendación clínica automática.";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiAnalisisGenerador(ObjectMapper objectMapper,
                                   @Value("${app.gemini.base-url:https://generativelanguage.googleapis.com/v1beta}")
                                   String baseUrl,
                                   @Value("${app.gemini.api-key:}") String apiKey,
                                   @Value("${app.gemini.model:gemini-3.5-flash-lite}") String model) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public ResultadoGenerado generar(DatosAgregados datos) {
        validarConfiguracion();
        String entradaAgregada = serializarDatos(datos);
        GeminiResponse response = solicitarAnalisis(entradaAgregada);
        return convertirResultado(extraerTexto(response));
    }

    private void validarConfiguracion() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new AnalisisIaNoDisponibleException(
                    "La integración de Gemini no está configurada. Define GEMINI_API_KEY.");
        }
        if (model == null || model.isBlank()) {
            throw new AnalisisIaNoDisponibleException(
                    "El modelo de Gemini no está configurado. Define GEMINI_MODEL.");
        }
    }

    private String serializarDatos(DatosAgregados datos) {
        try {
            return objectMapper.writeValueAsString(datos);
        } catch (Exception ex) {
            throw new AnalisisIaNoDisponibleException(
                    "No fue posible preparar los datos agregados para Gemini.", ex);
        }
    }

    // HU31 | END25: distingue límites de uso, errores remotos y fallos de conexión de Gemini.
    private GeminiResponse solicitarAnalisis(String entradaAgregada) {
        try {
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/models/{model}:generateContent")
                            .build(model))
                    .header(CABECERA_API_KEY, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(crearSolicitud(entradaAgregada))
                    .retrieve()
                    .body(GeminiResponse.class);
        } catch (RestClientResponseException ex) {
            registrarErrorHttp(ex);
            if (ex.getStatusCode().value() == 429) {
                throw new AnalisisIaNoDisponibleException(
                        "Gemini alcanzó el límite de uso. Intenta nuevamente más tarde.", ex);
            }
            throw new AnalisisIaNoDisponibleException(
                    "Gemini rechazó o no pudo procesar la solicitud de análisis.", ex);
        } catch (ResourceAccessException ex) {
            Throwable causa = ex.getMostSpecificCause();
            String detalle = causa == null ? ex.getClass().getSimpleName() : causa.getMessage();
            log.warn("HU31 | END25: fallo de conexión con Gemini: {}", sanearParaLog(detalle));
            throw new AnalisisIaNoDisponibleException(
                    "No fue posible conectarse con Gemini para generar el análisis.", ex);
        } catch (Exception ex) {
            log.warn("HU31 | END25: error inesperado al invocar Gemini: {}",
                    ex.getClass().getSimpleName());
            throw new AnalisisIaNoDisponibleException(
                    "No fue posible generar el análisis descriptivo con Gemini.", ex);
        }
    }

    // HU31 | END25: registra solo estado y mensaje saneado; nunca cabeceras, clave, solicitud ni datos.
    private void registrarErrorHttp(RestClientResponseException ex) {
        String estadoGemini = null;
        String mensajeGemini = null;
        try {
            GeminiErrorResponse respuesta = objectMapper.readValue(
                    ex.getResponseBodyAsString(), GeminiErrorResponse.class);
            if (respuesta != null && respuesta.error() != null) {
                estadoGemini = respuesta.error().status();
                mensajeGemini = respuesta.error().message();
            }
        } catch (Exception ignored) {
            // El cuerpo remoto puede no ser JSON; se usa únicamente el texto del estado HTTP.
        }

        String detalle = combinarDetalle(estadoGemini, mensajeGemini, ex.getStatusText());
        log.warn("HU31 | END25: Gemini respondió HTTP {}: {}",
                ex.getStatusCode().value(), sanearParaLog(detalle));
    }

    private String combinarDetalle(String estadoGemini, String mensajeGemini, String fallback) {
        boolean hayEstado = estadoGemini != null && !estadoGemini.isBlank();
        boolean hayMensaje = mensajeGemini != null && !mensajeGemini.isBlank();
        if (hayEstado && hayMensaje) {
            return estadoGemini + " - " + mensajeGemini;
        }
        if (hayMensaje) {
            return mensajeGemini;
        }
        if (hayEstado) {
            return estadoGemini;
        }
        return fallback == null || fallback.isBlank() ? "sin detalle remoto" : fallback;
    }

    private String sanearParaLog(String valor) {
        String seguro = valor == null ? "sin detalle remoto" : valor;
        seguro = CLAVE_GOOGLE.matcher(seguro).replaceAll("[CLAVE_REDACTADA]");
        seguro = PARAMETRO_KEY.matcher(seguro).replaceAll("$1[CLAVE_REDACTADA]");
        seguro = ASIGNACION_CLAVE.matcher(seguro).replaceAll("$1[CLAVE_REDACTADA]");
        seguro = seguro.replaceAll("[\\p{Cntrl}]", " ").replaceAll("\\s+", " ").trim();
        if (seguro.isBlank()) {
            return "sin detalle remoto";
        }
        return seguro.length() <= MAXIMO_MENSAJE_LOG
                ? seguro
                : seguro.substring(0, MAXIMO_MENSAJE_LOG) + "…";
    }

    private Map<String, Object> crearSolicitud(String entradaAgregada) {
        Map<String, Object> propiedadTexto = Map.of(
                "type", "STRING",
                "description", "Texto descriptivo breve, basado solo en los agregados recibidos.");
        Map<String, Object> esquema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "resumen", propiedadTexto,
                        "coincidencias", propiedadTexto,
                        "puntosRevision", propiedadTexto),
                "required", List.of("resumen", "coincidencias", "puntosRevision"));
        Map<String, Object> configuracionGeneracion = Map.of(
                "responseMimeType", "application/json",
                "responseSchema", esquema,
                "maxOutputTokens", 600);
        return Map.of(
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", INSTRUCCIONES))),
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of(
                                "text", "Datos agregados y anonimizados para analizar:\n" + entradaAgregada)))),
                "generationConfig", configuracionGeneracion);
    }

    private String extraerTexto(GeminiResponse response) {
        if (response == null || response.candidates() == null) {
            log.warn("HU31 | END25: Gemini devolvió una respuesta sin candidatos utilizables.");
            throw new AnalisisIaNoDisponibleException("Gemini devolvió una respuesta sin contenido.");
        }
        return response.candidates().stream()
                .filter(candidate -> candidate.content() != null && candidate.content().parts() != null)
                .flatMap(candidate -> candidate.content().parts().stream())
                .map(GeminiPart::text)
                .filter(text -> text != null && !text.isBlank())
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("HU31 | END25: Gemini devolvió candidatos sin texto utilizable.");
                    return new AnalisisIaNoDisponibleException(
                            "Gemini no devolvió un análisis descriptivo utilizable.");
                });
    }

    private ResultadoGenerado convertirResultado(String texto) {
        try {
            ResultadoGenerado resultado = objectMapper.readValue(texto, ResultadoGenerado.class);
            validarResultado(resultado);
            return new ResultadoGenerado(
                    resultado.resumen().trim(),
                    resultado.coincidencias().trim(),
                    agregarAviso(resultado.puntosRevision()));
        } catch (AnalisisIaNoDisponibleException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("HU31 | END25: Gemini devolvió una respuesta estructurada inválida: {}",
                    ex.getClass().getSimpleName());
            throw new AnalisisIaNoDisponibleException(
                    "Gemini devolvió una respuesta con formato inválido.", ex);
        }
    }

    private void validarResultado(ResultadoGenerado resultado) {
        if (resultado == null
                || esVacio(resultado.resumen())
                || esVacio(resultado.coincidencias())
                || esVacio(resultado.puntosRevision())) {
            throw new AnalisisIaNoDisponibleException(
                    "Gemini devolvió un análisis descriptivo incompleto.");
        }
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private String agregarAviso(String puntosRevision) {
        String puntos = puntosRevision.trim();
        if (puntos.contains(AVISO_DESCRIPTIVO)) {
            return puntos;
        }
        return puntos.endsWith(".")
                ? puntos + " " + AVISO_DESCRIPTIVO
                : puntos + ". " + AVISO_DESCRIPTIVO;
    }

    private record GeminiResponse(List<GeminiCandidate> candidates) {
    }

    private record GeminiCandidate(GeminiContent content, String finishReason) {
    }

    private record GeminiContent(List<GeminiPart> parts, String role) {
    }

    private record GeminiPart(String text) {
    }

    private record GeminiErrorResponse(GeminiError error) {
    }

    private record GeminiError(Integer code, String message, String status) {
    }
}
