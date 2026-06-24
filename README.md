# Microservicio Usuarios - PerfuLandia SPA

Este repositorio contiene el microservicio de **usuarios y seguridad** para el sistema PerfuLandia SPA.

El microservicio permite gestionar usuarios, roles, permisos, autenticación básica de login y cambio de contraseña. Está desarrollado con Java 21, Spring Boot, Maven, MySQL, JPA, Validation, HATEOAS, Swagger UI, JUnit, Mockito y JaCoCo.

## Tecnologías utilizadas

* Java 21
* Spring Boot
* Maven
* MySQL
* Spring Web
* Spring Data JPA
* Spring Validation
* Spring HATEOAS
* Springdoc OpenAPI / Swagger UI
* JUnit 5
* Mockito
* JaCoCo
* XAMPP / MySQL local

## Estructura principal del proyecto

```txt
src/main/java/cl/perfulandia/usuarios
├── controller
├── dto
├── model
├── repository
├── security
├── service
└── UsuariosApplication.java
```

```txt
src/test/java/cl/perfulandia/usuarios
├── controller
├── security
├── service
└── UsuariosApplicationTests.java
```

## Base de datos

El microservicio utiliza MySQL en el puerto 3306.

Base de datos utilizada:

```txt
perfulandia_usuarios_db
```

Antes de ejecutar el proyecto, se debe iniciar MySQL desde XAMPP o desde los servicios de Windows.

## Ejecución local

Para compilar el proyecto:

```powershell
.\mvnw.cmd clean compile
```

Para ejecutar el microservicio:

```powershell
.\mvnw.cmd spring-boot:run
```

El servicio queda disponible en:

```txt
http://localhost:8081
```

## Swagger UI

La documentación Swagger se encuentra en:

```txt
http://localhost:8081/swagger-ui/index.html
```

También puede probarse con:

```txt
http://localhost:8081/swagger-ui.html
```

La evidencia de Swagger se encuentra en:

```txt
docs/evidencias/05-swagger/01-swagger-ui-endpoints.png
```

## Pruebas unitarias

Para ejecutar las pruebas unitarias:

```powershell
.\mvnw.cmd clean test
```

Para ejecutar pruebas y generar reporte de cobertura:

```powershell
.\mvnw.cmd clean test jacoco:report
```

El reporte JaCoCo se genera en:

```txt
target/site/jacoco/index.html
```

## Cobertura de pruebas

La cobertura oficial del microservicio se mide con JaCoCo sobre las capas:

```txt
service
security
```

Estas capas contienen la lógica de negocio principal del microservicio.

Se excluyen de la medición principal:

```txt
controller
dto
model
repository
UsuariosApplication
```

Motivo de exclusión:

* `dto`: contiene objetos de transferencia de datos.
* `model`: contiene entidades JPA.
* `repository`: contiene interfaces de acceso a datos.
* `controller`: se valida funcionalmente mediante Swagger/Postman.
* `UsuariosApplication`: clase de arranque de Spring Boot.

La evidencia de cobertura se encuentra en:

```txt
docs/evidencias/04-cobertura/01-clean-test-build-success.png
docs/evidencias/04-cobertura/02-jacoco-reporte-general-98.png
```

## Endpoints principales

### Autenticación

```txt
POST /api/auth/login
```

### Usuarios

```txt
GET    /api/usuarios
GET    /api/usuarios/{id}
GET    /api/usuarios/estado/{estado}
POST   /api/usuarios
PUT    /api/usuarios/{id}
PUT    /api/usuarios/{id}/estado
PUT    /api/usuarios/{id}/rol
PUT    /api/usuarios/{id}/password
DELETE /api/usuarios/{id}
```

### Roles

```txt
GET    /api/roles
GET    /api/roles/{id}
POST   /api/roles
PUT    /api/roles/{id}
PUT    /api/roles/{id}/permisos/{idPermiso}
DELETE /api/roles/{id}
```

### Permisos

```txt
GET    /api/permisos
GET    /api/permisos/{id}
POST   /api/permisos
PUT    /api/permisos/{id}
DELETE /api/permisos/{id}
```

## Evidencias disponibles

```txt
docs/evidencias/04-cobertura
docs/evidencias/05-swagger
```

## Estado del microservicio

* Microservicio compila correctamente.
* Pruebas unitarias ejecutadas correctamente.
* Cobertura JaCoCo generada correctamente.
* Swagger UI habilitado correctamente.
* MySQL funcionando localmente mediante XAMPP.
* Proyecto versionado en GitHub.
