package com.upc.backend.serviceimpl;

import com.upc.backend.exceptions.CorreoEnvioException;
import com.upc.backend.services.CorreoService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.UnsupportedEncodingException;

@Service
// Infraestructura común: centraliza el envío SMTP para Gmail y proveedores compatibles como SES.
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
// HU02 | END24: comunica la invitación con código de seis dígitos y vigencia de 24 horas.
    public void enviarInvitacionPaciente(
            String destinatario,
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

        try {

            MimeMessage mensaje = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente, "AliviaTech");
            helper.setTo(destinatario);
            helper.setSubject("Activa tu cuenta | AliviaTech");

            String html = crearHtmlInvitacion(
                    nombres,
                    codigo,
                    enlaceActivacion,
                    enlaceTerminos
            );

            helper.setText(html, true);

            mailSender.send(mensaje);

        } catch (MessagingException
                 | UnsupportedEncodingException
                 | MailException ex) {

            throw new CorreoEnvioException(
                    "La cuenta quedó pendiente, pero el proveedor SMTP no pudo enviar la invitación.",
                    ex
            );
        }
    }

    @Override
// HU05 | END04: envía por SMTP el código de recuperación con vigencia de diez minutos.
    public void enviarCodigoRecuperacion(
            String destinatario,
            String nombres,
            String codigo) {

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();

        if (!habilitado || mailSender == null || !StringUtils.hasText(remitente)
                || !StringUtils.hasText(smtpHost) || !StringUtils.hasText(smtpUsuario)) {

            throw new CorreoEnvioException(
                    "No se envió el código de recuperación porque SMTP no está configurado.");
        }

        try {

            MimeMessage mensaje = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente, "AliviaTech");
            helper.setTo(destinatario);
            helper.setSubject("Código para recuperar tu contraseña | AliviaTech");

            String html = crearHtmlRecuperacion(nombres, codigo);

            helper.setText(html, true);

            mailSender.send(mensaje);

        } catch (MessagingException |
                 UnsupportedEncodingException |
                 MailException ex) {

            throw new CorreoEnvioException(
                    "El proveedor SMTP no pudo enviar el código de recuperación.",
                    ex);
        }

    }

    private String crearHtmlInvitacion(
            String nombres,
            String codigo,
            String enlaceActivacion,
            String enlaceTerminos) {

        String nombreSeguro = HtmlUtils.htmlEscape(nombres);
        String codigoSeguro = HtmlUtils.htmlEscape(codigo);
        String activacionSegura = HtmlUtils.htmlEscape(enlaceActivacion);
        String terminosSeguros = HtmlUtils.htmlEscape(enlaceTerminos);

        String html = """
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport"
                      content="width=device-width, initial-scale=1.0">

                <title>Activa tu cuenta - AliviaTech</title>
            </head>

            <body style="
                margin:0;
                padding:0;
                background-color:#f4f7f8;
                font-family:Arial, Helvetica, sans-serif;
                color:#2f3a3d;
            ">

            <table role="presentation"
                   width="100%"
                   cellspacing="0"
                   cellpadding="0"
                   border="0"
                   style="
                       background-color:#f4f7f8;
                       padding:40px 15px;
                   ">

                <tr>
                    <td align="center">

                        <table role="presentation"
                               width="100%"
                               align="center"
                               cellspacing="0"
                               cellpadding="0"
                               border="0"
                               style="
                                   width:100%;
                                   max-width:600px;
                                   margin:0 auto;
                                   background-color:#ffffff;
                                   border-radius:18px;
                                   overflow:hidden;
                                   box-shadow:0 6px 22px rgba(0,0,0,0.08);
                               ">

                            <!-- ENCABEZADO -->
                            <tr>
                                <td align="center"
                                    style="
                                        background-color:#16B2B1;
                                        padding:32px 25px;
                                    ">

                                    <div style="
                                        font-size:28px;
                                        font-weight:700;
                                        color:#ffffff;
                                        letter-spacing:0.5px;
                                    ">
                                        AliviaTech
                                    </div>

                                    <div style="
                                        margin-top:6px;
                                        font-size:14px;
                                        color:#e9ffff;
                                    ">
                                        Bienestar y acompañamiento entre sesiones
                                    </div>

                                </td>
                            </tr>

                            <!-- CONTENIDO -->
                            <tr>
                                <td style="padding:40px 42px;">

                                    <h1 style="
                                        margin:0 0 20px 0;
                                        font-size:25px;
                                        line-height:1.3;
                                        color:#243437;
                                    ">
                                        Activa tu cuenta
                                    </h1>

                                    <p style="
                                        margin:0 0 18px 0;
                                        font-size:16px;
                                        line-height:1.6;
                                        color:#4c5b5e;
                                    ">
                                        Hola, <strong>{{NOMBRE}}</strong>.
                                    </p>

                                    <p style="
                                        margin:0 0 24px 0;
                                        font-size:16px;
                                        line-height:1.6;
                                        color:#4c5b5e;
                                    ">
                                        Has recibido una invitación para
                                        activar tu cuenta en AliviaTech.
                                        Utiliza el siguiente código para
                                        completar el proceso:
                                    </p>

                                    <!-- CÓDIGO -->
                                    <table role="presentation"
                                           width="100%"
                                           cellspacing="0"
                                           cellpadding="0"
                                           border="0">

                                        <tr>
                                            <td align="center"
                                                style="
                                                    background-color:#eefafa;
                                                    border:1px solid #c9eeee;
                                                    border-radius:14px;
                                                    padding:25px;
                                                ">

                                                <div style="
                                                    font-size:13px;
                                                    color:#657578;
                                                    margin-bottom:10px;
                                                    text-transform:uppercase;
                                                    letter-spacing:1px;
                                                ">
                                                    Tu código de activación
                                                </div>

                                                <div style="
                                                    font-size:38px;
                                                    line-height:1;
                                                    font-weight:700;
                                                    letter-spacing:9px;
                                                    color:#16B2B1;
                                                ">
                                                    {{CODIGO}}
                                                </div>

                                            </td>
                                        </tr>

                                    </table>

                                    <p style="
                                        margin:24px 0;
                                        font-size:14px;
                                        line-height:1.6;
                                        color:#667679;
                                    ">
                                        Este código vence en
                                        <strong>24 horas</strong>
                                        y solo puede utilizarse una vez.
                                    </p>

                                    <!-- BOTÓN -->
                                    <table role="presentation"
                                           width="100%"
                                           cellspacing="0"
                                           cellpadding="0"
                                           border="0">

                                        <tr>
                                            <td align="center"
                                                style="padding:8px 0 28px 0;">

                                                <a href="{{ENLACE_ACTIVACION}}"
                                                   style="
                                                       display:inline-block;
                                                       background-color:#16B2B1;
                                                       color:#ffffff;
                                                       text-decoration:none;
                                                       font-size:16px;
                                                       font-weight:600;
                                                       padding:14px 30px;
                                                       border-radius:10px;
                                                   ">
                                                    Activar mi cuenta
                                                </a>

                                            </td>
                                        </tr>

                                    </table>

                                    <!-- AVISO -->
                                    <table role="presentation"
                                           width="100%"
                                           cellspacing="0"
                                           cellpadding="0"
                                           border="0">

                                        <tr>
                                            <td style="
                                                background-color:#f7f9f9;
                                                border-left:4px solid #16B2B1;
                                                border-radius:8px;
                                                padding:16px 18px;
                                            ">

                                                <div style="
                                                    font-size:14px;
                                                    line-height:1.6;
                                                    color:#566568;
                                                ">
                                                    Antes de activar tu cuenta,
                                                    revisa los
                                                    <a href="{{ENLACE_TERMINOS}}"
                                                       style="
                                                           color:#16B2B1;
                                                           font-weight:600;
                                                           text-decoration:none;
                                                       ">
                                                        términos de tratamiento
                                                        de datos
                                                    </a>.
                                                </div>

                                            </td>
                                        </tr>

                                    </table>

                                    <p style="
                                        margin:24px 0 0 0;
                                        font-size:14px;
                                        line-height:1.6;
                                        color:#667679;
                                    ">
                                        Si no esperabas recibir esta invitación,
                                        puedes ignorar este correo.
                                    </p>

                                    <p style="
                                        margin:30px 0 0 0;
                                        font-size:15px;
                                        line-height:1.6;
                                        color:#4c5b5e;
                                    ">
                                        Saludos,<br>
                                        <strong>Equipo AliviaTech</strong>
                                    </p>

                                </td>
                            </tr>

                            <!-- FOOTER -->
                            <tr>
                                <td align="center"
                                    style="
                                        background-color:#f7f9f9;
                                        padding:25px 30px;
                                        border-top:1px solid #e8eeee;
                                    ">

                                    <p style="
                                        margin:0 0 7px 0;
                                        font-size:12px;
                                        color:#8a989a;
                                    ">
                                        Este correo fue enviado automáticamente.
                                        No respondas a este mensaje.
                                    </p>

                                    <p style="
                                        margin:0;
                                        font-size:12px;
                                        color:#a0aaac;
                                    ">
                                        © 2026 AliviaTech
                                    </p>

                                </td>
                            </tr>

                        </table>

                    </td>
                </tr>

            </table>

            </body>
            </html>
            """;

        return html
                .replace("{{NOMBRE}}", nombreSeguro)
                .replace("{{CODIGO}}", codigoSeguro)
                .replace("{{ENLACE_ACTIVACION}}", activacionSegura)
                .replace("{{ENLACE_TERMINOS}}", terminosSeguros);
    }

    private String crearHtmlRecuperacion(String nombres, String codigo) {

        String nombreSeguro = HtmlUtils.htmlEscape(nombres);
        String codigoSeguro = HtmlUtils.htmlEscape(codigo);

        String html = """
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Recuperación de contraseña - AliviaTech</title>
            </head>

            <body style="
                margin:0;
                padding:0;
                background-color:#f4f7f8;
                font-family:Arial, Helvetica, sans-serif;
                color:#2f3a3d;
            ">

            <table role="presentation"
                   width="100%"
                   cellspacing="0"
                   cellpadding="0"
                   border="0"
                   style="background-color:#f4f7f8; padding:40px 15px;">

                <tr>
                    <td align="center">

                        <table role="presentation"
                               width="100%"
                               cellspacing="0"
                               cellpadding="0"
                               border="0"
                               style="
                                    max-width:600px;
                                    background-color:#ffffff;
                                    border-radius:18px;
                                    overflow:hidden;
                                    box-shadow:0 6px 22px rgba(0,0,0,0.08);
                               ">

                            <tr>
                                <td align="center"
                                    style="
                                        background-color:#16B2B1;
                                        padding:32px 25px;
                                    ">

                                    <div style="
                                        font-size:28px;
                                        font-weight:700;
                                        color:#ffffff;
                                        letter-spacing:0.5px;
                                    ">
                                        AliviaTech
                                    </div>

                                    <div style="
                                        margin-top:6px;
                                        font-size:14px;
                                        color:#e9ffff;
                                    ">
                                        Bienestar y acompañamiento entre sesiones
                                    </div>

                                </td>
                            </tr>

                            <tr>
                                <td style="padding:40px 42px;">

                                    <h1 style="
                                        margin:0 0 20px 0;
                                        font-size:25px;
                                        color:#243437;
                                    ">
                                        Recupera tu contraseña
                                    </h1>

                                    <p style="
                                        font-size:16px;
                                        line-height:1.6;
                                        color:#4c5b5e;
                                    ">
                                        Hola, <strong>{{NOMBRE}}</strong>.
                                    </p>

                                    <p style="
                                        font-size:16px;
                                        line-height:1.6;
                                        color:#4c5b5e;
                                    ">
                                        Recibimos una solicitud para restablecer
                                        la contraseña de tu cuenta de AliviaTech.
                                        Utiliza el siguiente código para continuar:
                                    </p>

                                    <table role="presentation"
                                           width="100%"
                                           cellspacing="0"
                                           cellpadding="0"
                                           border="0">

                                        <tr>
                                            <td align="center"
                                                style="
                                                    background-color:#eefafa;
                                                    border:1px solid #c9eeee;
                                                    border-radius:14px;
                                                    padding:25px;
                                                ">

                                                <div style="
                                                    font-size:13px;
                                                    color:#657578;
                                                    margin-bottom:10px;
                                                ">
                                                    TU CÓDIGO DE VERIFICACIÓN
                                                </div>

                                                <div style="
                                                    font-size:38px;
                                                    font-weight:700;
                                                    letter-spacing:9px;
                                                    color:#16B2B1;
                                                ">
                                                    {{CODIGO}}
                                                </div>

                                            </td>
                                        </tr>

                                    </table>

                                    <p style="
                                        margin-top:25px;
                                        font-size:14px;
                                        line-height:1.6;
                                        color:#667679;
                                    ">
                                        Este código vence en
                                        <strong>10 minutos</strong>
                                        y solo puede utilizarse una vez.
                                    </p>

                                    <p style="
                                        margin-top:30px;
                                        font-size:15px;
                                        color:#4c5b5e;
                                    ">
                                        Saludos,<br>
                                        <strong>Equipo AliviaTech</strong>
                                    </p>

                                </td>
                            </tr>

                            <tr>
                                <td align="center"
                                    style="
                                        background-color:#f7f9f9;
                                        padding:25px 30px;
                                        border-top:1px solid #e8eeee;
                                    ">

                                    <p style="
                                        margin:0;
                                        font-size:12px;
                                        color:#8a989a;
                                    ">
                                        Este correo fue enviado automáticamente.
                                        No respondas a este mensaje.
                                    </p>

                                </td>
                            </tr>

                        </table>

                    </td>
                </tr>

            </table>

            </body>
            </html>
            """;

        return html
                .replace("{{NOMBRE}}", nombreSeguro)
                .replace("{{CODIGO}}", codigoSeguro);
    }
}
