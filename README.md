# Grosella_Backend

API REST de la **Aplicación web para la gestión de pedidos de la Pastelería Grosella** (Ayacucho).
Proyecto de titulación CIBERTEC, Grupo 02.

- Java 21, Spring Boot 3.5, Spring Data JPA, PostgreSQL
- Arquitectura en capas: `config`, `controller`, `dto`, `exception`, `model`, `repository`, `security`, `service`

## Ejecutar

1. Crear la base `grosella_pedidos` con los scripts de la carpeta BaseDatos.
2. Revisar puerto y contraseña de PostgreSQL en `src/main/resources/application.yml`
   (o con las variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`).
3. Levantar la API:

```bash
./mvnw spring-boot:run      # Windows: .\mvnw.cmd spring-boot:run
```

La API queda en `http://localhost:8080/api`.

## Ramas

- `main`: versión estable
- `jhonas_dev`: desarrollo de Jhonas
