-- ============================================================
-- ALIVIATECH - DATOS INICIALES DE DEMOSTRACION
-- PostgreSQL / Spring Boot
-- 3 registros por tabla (6 usuarios: 3 psicologos + 3 pacientes)
-- ============================================================

-- 1. USUARIO
INSERT INTO public.usuario
(id_usuario, rol, dni, nombres, apellidos, correo, telefono,
 password_hash, fecha_creacion, fecha_activacion)
VALUES
    (1, 'PSICOLOGO', '70123451', 'Rafael', 'Mendoza',
     'rafael.demo@aliviatech.test', '987654301', '$2a$12$lH2ZeLf.g/hordmwknRkYOjIpYqiM.QpUbt9slui7vFXfIhr07ZNa',
     '2026-09-01 09:00:00-05', '2026-09-01 09:30:00-05'),
    (2, 'PSICOLOGO', '70123452', 'Lucia', 'Torres',
     'lucia.demo@aliviatech.test', '987654302', '$2a$12$lH2ZeLf.g/hordmwknRkYOjIpYqiM.QpUbt9slui7vFXfIhr07ZNa',
     '2026-09-01 10:00:00-05', '2026-09-01 10:30:00-05'),
    (3, 'PSICOLOGO', '70123453', 'Carlos', 'Ramirez',
     'carlos.demo@aliviatech.test', '987654303', '$2a$12$lH2ZeLf.g/hordmwknRkYOjIpYqiM.QpUbt9slui7vFXfIhr07ZNa',
     '2026-09-01 11:00:00-05', '2026-09-01 11:30:00-05'),
    (4, 'PACIENTE', '70123454', 'Camila', 'Flores',
     'camila.demo@aliviatech.test', '987654304', '$2a$12$lH2ZeLf.g/hordmwknRkYOjIpYqiM.QpUbt9slui7vFXfIhr07ZNa',
     '2026-09-02 09:00:00-05', '2026-09-02 09:30:00-05'),
    (5, 'PACIENTE', '70123455', 'Diego', 'Castillo',
     'diego.demo@aliviatech.test', '987654305', '$2a$12$lH2ZeLf.g/hordmwknRkYOjIpYqiM.QpUbt9slui7vFXfIhr07ZNa',
     '2026-09-02 10:00:00-05', '2026-09-02 10:30:00-05'),
    (6, 'PACIENTE', '70123456', 'Valeria', 'Rojas',
     'valeria.demo@aliviatech.test', '987654306', '$2a$12$lH2ZeLf.g/hordmwknRkYOjIpYqiM.QpUbt9slui7vFXfIhr07ZNa',
     '2026-09-02 11:00:00-05', '2026-09-02 11:30:00-05')
    ON CONFLICT DO NOTHING;

-- 2. PSICOLOGO
INSERT INTO public.psicologo
(id_psicologo, numero_colegiatura, especialidad)
VALUES
    (1, 'CPsP-10001', 'Psicologia clinica'),
    (2, 'CPsP-10002', 'Psicologia cognitivo-conductual'),
    (3, 'CPsP-10003', 'Psicologia de la salud')
    ON CONFLICT DO NOTHING;

-- 3. PACIENTE
INSERT INTO public.paciente
(id_paciente, fecha_nacimiento)
VALUES
    (4, '2002-05-15'),
    (5, '1999-08-20'),
    (6, '2001-12-10')
    ON CONFLICT DO NOTHING;

-- 4. SEGUIMIENTO
INSERT INTO public.seguimiento
(id_seguimiento, id_paciente, id_psicologo, fecha_inicio,
 fecha_fin, fecha_ultima_consulta)
VALUES
    (1, 4, 1, '2026-09-05', NULL, '2026-10-20 15:00:00-05'),
    (2, 5, 2, '2026-09-10', NULL, '2026-10-21 16:00:00-05'),
    (3, 6, 3, '2026-09-15', NULL, '2026-10-22 17:00:00-05')
    ON CONFLICT DO NOTHING;

-- 5. CONTACTO_EMERGENCIA
INSERT INTO public.contacto_emergencia
(id_contacto, id_paciente, nombre_completo, telefono, correo, relacion)
VALUES
    (1, 4, 'Maria Flores', '987650101',
     'maria.demo@aliviatech.test', 'Madre'),
    (2, 5, 'Ana Castillo', '987650102',
     'ana.demo@aliviatech.test', 'Hermana'),
    (3, 6, 'Pedro Rojas', '987650103',
     'pedro.demo@aliviatech.test', 'Padre')
    ON CONFLICT DO NOTHING;

-- 6. INVITACION_ACTIVACION
-- Tokens ficticios: no utilizables para activar cuentas reales.
INSERT INTO public.invitacion_activacion
(id_invitacion, id_paciente, token_hash, fecha_creacion,
 fecha_expiracion, fecha_uso)
VALUES
    (1, 4, 'demo-token-hash-camila-001',
     '2026-09-02 09:00:00-05',
     '2026-09-03 09:00:00-05',
     '2026-09-02 09:30:00-05'),
    (2, 5, 'demo-token-hash-diego-002',
     '2026-09-02 10:00:00-05',
     '2026-09-03 10:00:00-05',
     '2026-09-02 10:30:00-05'),
    (3, 6, 'demo-token-hash-valeria-003',
     '2026-09-02 11:00:00-05',
     '2026-09-03 11:00:00-05',
     '2026-09-02 11:30:00-05')
    ON CONFLICT DO NOTHING;

-- 7. CONSENTIMIENTO
INSERT INTO public.consentimiento
(id_consentimiento, id_paciente, version_documento, fecha_aceptacion)
VALUES
    (1, 4, 'v1.0', '2026-09-02 09:35:00-05'),
    (2, 5, 'v1.0', '2026-09-02 10:35:00-05'),
    (3, 6, 'v1.0', '2026-09-02 11:35:00-05')
    ON CONFLICT DO NOTHING;

-- 8. DISPOSITIVO
INSERT INTO public.dispositivo
(id_dispositivo, id_paciente, codigo_dispositivo, modelo,
 estado, alias, fecha_asignacion, fecha_retiro,
 fecha_ultima_sincronizacion)
VALUES
    (1, 4, 'AT-WEAR-001', 'AliviaBand Simulada',
     'ACTIVO', 'Pulsera Camila',
     '2026-09-05 10:00:00-05', NULL,
     '2026-10-26 08:00:00-05'),
    (2, 5, 'AT-WEAR-002', 'AliviaBand Simulada',
     'ACTIVO', 'Pulsera Diego',
     '2026-09-10 10:00:00-05', NULL,
     '2026-10-26 08:00:00-05'),
    (3, 6, 'AT-WEAR-003', 'AliviaBand Simulada',
     'ACTIVO', 'Pulsera Valeria',
     '2026-09-15 10:00:00-05', NULL,
     '2026-10-26 08:00:00-05')
    ON CONFLICT DO NOTHING;

-- 9. LECTURA_WEARABLE
INSERT INTO public.lectura_wearable
(id_lectura, id_dispositivo, fecha_hora, frecuencia_cardiaca)
VALUES
    (1, 1, '2026-10-26 08:00:00-05', 72),
    (2, 2, '2026-10-26 08:00:00-05', 118),
    (3, 3, '2026-10-26 08:00:00-05', 85)
    ON CONFLICT DO NOTHING;

-- 10. RESUMEN_WEARABLE_DIARIO
INSERT INTO public.resumen_wearable_diario
(id_resumen, id_dispositivo, fecha, pasos, minutos_sueno)
VALUES
    (1, 1, '2026-10-25', 8450, 450),
    (2, 2, '2026-10-25', 5230, 360),
    (3, 3, '2026-10-25', 7120, 420)
    ON CONFLICT DO NOTHING;

-- 11. REGISTRO_BIENESTAR
INSERT INTO public.registro_bienestar
(id_registro, id_paciente, fecha_hora, nivel_ansiedad,
 estado_animo, comentario)
VALUES
    (1, 4, '2026-10-25 20:00:00-05', 3,
     'CALMADO', 'Hoy me senti tranquila durante el dia.'),
    (2, 5, '2026-10-25 21:00:00-05', 7,
     'ANSIOSO', 'Tuve nervios antes de una presentacion.'),
    (3, 6, '2026-10-25 22:00:00-05', 5,
     'TRISTE', 'Me senti desanimada durante la tarde.')
    ON CONFLICT DO NOTHING;

-- 12. EVENTO_WEARABLE
INSERT INTO public.evento_wearable
(id_evento, id_dispositivo, fecha_hora_inicio, fecha_hora_fin,
 descripcion_variacion, estado_confirmacion,
 fecha_respuesta, contexto_paciente)
VALUES
    (1, 1,
     '2026-10-24 15:00:00-05',
     '2026-10-24 15:15:00-05',
     'Frecuencia cardiaca simulada superior a 110 lpm.',
     'SI',
     '2026-10-24 15:30:00-05',
     'Estaba subiendo escaleras.'),
    (2, 2,
     '2026-10-26 08:00:00-05',
     '2026-10-26 08:15:00-05',
     'Lectura simulada de 118 lpm.',
     NULL,
     NULL, NULL),
    (3, 3,
     '2026-10-23 18:00:00-05',
     '2026-10-23 18:15:00-05',
     'Frecuencia cardiaca simulada superior a 110 lpm.',
     'SI',
     '2026-10-23 18:30:00-05',
     'Estaba realizando actividad fisica.')
    ON CONFLICT DO NOTHING;

-- 13. SESION
INSERT INTO public.sesion
(id_sesion, id_seguimiento, fecha_sesion, nota_interna,
 resumen_compartido, recomendacion_compartida, fecha_registro)
VALUES
    (1, 1, '2026-10-20 15:00:00-05',
     'Nota privada: revisar situaciones de estres academico.',
     'Se converso sobre situaciones cotidianas y bienestar.',
     'Continuar con las actividades acordadas en sesion.',
     '2026-10-20 16:00:00-05'),
    (2, 2, '2026-10-21 16:00:00-05',
     'Nota privada: explorar situaciones de ansiedad laboral.',
     'Se revisaron experiencias relacionadas con el trabajo.',
     'Registrar situaciones relevantes para la siguiente sesion.',
     '2026-10-21 17:00:00-05'),
    (3, 3, '2026-10-22 17:00:00-05',
     'Nota privada: revisar cambios en el estado de animo.',
     'Se converso sobre experiencias de la semana.',
     'Mantener el registro de bienestar acordado.',
     '2026-10-22 18:00:00-05')
    ON CONFLICT DO NOTHING;

-- 14. RECUPERACION_CONTRASENA
-- Registros ficticios expirados, solo para demostracion.
INSERT INTO public.recuperacion_contrasena
(id_recuperacion, id_usuario, valor_hash, fase,
 fecha_expiracion, intentos)
VALUES
    ('0b9e7a52-3c1d-4f68-9a0e-5d2c8b7f1a34',
     4, 'demo-hash-recuperacion-001', 'CODIGO',
     '2026-10-01 12:00:00-05', 0),
    ('7c4d2e91-8a6b-4b03-b1f5-9e3a0d6c2f78',
     5, 'demo-hash-recuperacion-002', 'CODIGO',
     '2026-10-01 13:00:00-05', 1),
    ('f1a3c5e7-2b4d-4968-8c0a-1e3b5d7f9a21',
     6, 'demo-hash-recuperacion-003', 'CODIGO',
     '2026-10-01 14:00:00-05', 0)
    ON CONFLICT DO NOTHING;

-- ============================================================
-- SINCRONIZACION DE SECUENCIAS IDENTITY
-- ============================================================

SELECT setval(
               pg_get_serial_sequence('public.usuario', 'id_usuario'),
               GREATEST(COALESCE((SELECT MAX(id_usuario) FROM public.usuario), 0), 1),
               EXISTS (SELECT 1 FROM public.usuario)
       );

SELECT setval(
               pg_get_serial_sequence('public.seguimiento', 'id_seguimiento'),
               GREATEST(COALESCE((SELECT MAX(id_seguimiento) FROM public.seguimiento), 0), 1),
               EXISTS (SELECT 1 FROM public.seguimiento)
       );

SELECT setval(
               pg_get_serial_sequence('public.contacto_emergencia', 'id_contacto'),
               GREATEST(COALESCE((SELECT MAX(id_contacto) FROM public.contacto_emergencia), 0), 1),
               EXISTS (SELECT 1 FROM public.contacto_emergencia)
       );

SELECT setval(
               pg_get_serial_sequence('public.invitacion_activacion', 'id_invitacion'),
               GREATEST(COALESCE((SELECT MAX(id_invitacion) FROM public.invitacion_activacion), 0), 1),
               EXISTS (SELECT 1 FROM public.invitacion_activacion)
       );

SELECT setval(
               pg_get_serial_sequence('public.consentimiento', 'id_consentimiento'),
               GREATEST(COALESCE((SELECT MAX(id_consentimiento) FROM public.consentimiento), 0), 1),
               EXISTS (SELECT 1 FROM public.consentimiento)
       );

SELECT setval(
               pg_get_serial_sequence('public.dispositivo', 'id_dispositivo'),
               GREATEST(COALESCE((SELECT MAX(id_dispositivo) FROM public.dispositivo), 0), 1),
               EXISTS (SELECT 1 FROM public.dispositivo)
       );

SELECT setval(
               pg_get_serial_sequence('public.lectura_wearable', 'id_lectura'),
               GREATEST(COALESCE((SELECT MAX(id_lectura) FROM public.lectura_wearable), 0), 1),
               EXISTS (SELECT 1 FROM public.lectura_wearable)
       );

SELECT setval(
               pg_get_serial_sequence('public.resumen_wearable_diario', 'id_resumen'),
               GREATEST(COALESCE((SELECT MAX(id_resumen) FROM public.resumen_wearable_diario), 0), 1),
               EXISTS (SELECT 1 FROM public.resumen_wearable_diario)
       );

SELECT setval(
               pg_get_serial_sequence('public.registro_bienestar', 'id_registro'),
               GREATEST(COALESCE((SELECT MAX(id_registro) FROM public.registro_bienestar), 0), 1),
               EXISTS (SELECT 1 FROM public.registro_bienestar)
       );

SELECT setval(
               pg_get_serial_sequence('public.evento_wearable', 'id_evento'),
               GREATEST(COALESCE((SELECT MAX(id_evento) FROM public.evento_wearable), 0), 1),
               EXISTS (SELECT 1 FROM public.evento_wearable)
       );

SELECT setval(
               pg_get_serial_sequence('public.sesion', 'id_sesion'),
               GREATEST(COALESCE((SELECT MAX(id_sesion) FROM public.sesion), 0), 1),
               EXISTS (SELECT 1 FROM public.sesion)
       );
