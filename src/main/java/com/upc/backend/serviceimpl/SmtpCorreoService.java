package com.upc.backend.serviceimpl;

import com.upc.backend.exceptions.CorreoEnvioException;
import com.upc.backend.services.CorreoService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
// Infraestructura de HU02: envía la invitación mediante el proveedor SMTP configurado.
public class SmtpCorreoService implements CorreoService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean habilitado;
    private final String remitente;
    private final String smtpHost;
    private final String smtpUsuario;

    public SmtpCorreoService(ObjectProvider<JavaMailSender> mailSenderProvider,
                             @Value("${app.mail.enabled:false}") boolean habilitado,
                             @Value("${app.mail.from:}") String remitente,
                             @Value("${spring.mail.host:}") String smtpHost,
                             @Value("${spring.mail.username:}") String smtpUsuario) {
        this.mailSenderProvider = mailSenderProvider;
        this.habilitado = habilitado;
        this.remitente = remitente;
        this.smtpHost = smtpHost;
        this.smtpUsuario = smtpUsuario;
    }

    @Override
    // HU02 | END24: comunica el código, el enlace de activación y los términos al paciente.
    public void enviarInvitacionPaciente(String destinatario,
                                         String nombres,
                                         String codigo,
                                         String enlaceActivacion,
                                         String enlaceTerminos) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (!habilitado || mailSender == null || !StringUtils.hasText(remitente)
                || !StringUtils.hasText(smtpHost) || !StringUtils.hasText(smtpUsuario)) {
            throw new CorreoEnvioException(
                    "La cuenta quedó pendiente, pero no se envió la invitación porque SMTP no está configurado.");
        }

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destinatario);
        mensaje.setSubject("Activa tu cuenta | AliviaTech");
        mensaje.setText("Hola, " + nombres + ".\n\n"
                + "Tu código de activación es: " + codigo + "\n"
                + "El código vence en 24 horas y solo puede utilizarse una vez.\n\n"
                + "Activa tu cuenta en: " + enlaceActivacion + "\n"
                + "Términos de tratamiento de datos: " + enlaceTerminos);

        try {
            mailSender.send(mensaje);
        } catch (MailException ex) {
            throw new CorreoEnvioException(
                    "La cuenta quedó pendiente, pero el proveedor SMTP no pudo enviar la invitación.", ex);
        }
    }

    @Override
    public void enviarCodigoRecuperacion(String destinatario, String nombres, String codigo) {
        throw new CorreoEnvioException("El envío de códigos de recuperación no está disponible.");
    }
}
