# backend
Repositorio que contiene el código fuente del backend de la aplicación web, construido con el framework Spring Boot.

## Docker Compose local

1. Copia `.env.example` a `.env` y configura al menos `DB_PASSWORD` y `JWT_SECRET` con valores propios. Ajusta `DB_USER` si lo necesitas. Mantén `.env` solo en tu equipo. El correo SMTP y Gemini son opcionales; configura sus variables si vas a usar esas funciones.
2. Ejecuta `docker compose config --quiet` y `docker compose up -d --build`.
3. Revisa el estado con `docker compose ps` y los logs con `docker compose logs api db`.
4. Usa `http://localhost:8080` como URL base de la API en Postman.
5. Detén los servicios con `docker compose down`. El volumen nombrado `postgres_data` conserva los datos.

Compose configura `DB_URL` como `jdbc:postgresql://db:5432/BD_AliviaTech` y comparte `DB_USER` y `DB_PASSWORD` con PostgreSQL. La base de datos no publica un puerto en el host.
