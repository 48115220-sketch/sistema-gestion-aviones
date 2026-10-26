# Sistema de Gestión de aviones - Backend (Microservicios)

Sistema distribuido basado en arquitectura de microservicios desarrollado con Java, Spring Boot, Spring Cloud, Docker y persistencia en H2 Database. La seguridad del ecosistema está centralizada mediante autenticación y autorización con JWT.

---

## Arquitectura y Puertos

| Servicio | Puerto | Ruta Gateway | Descripción |
| :--- | :---: | :---: | :--- |
| **eureka-server** | `8761` | N/A | Servidor de descubrimiento y registro de servicios |
| **config-server** | `8888` | N/A | Servidor de configuración centralizada |
| **gateway-service** | `8080` | `/` | API Gateway de entrada y enrutamiento principal |
| **vuelos-service** | `8081` | `/vuelos/**` | Microservicio de gestión de vuelos |
| **aviones-service** | `8082` | `/aviones/**` | Microservicio de gestión de aviones |
| **personal-service** | `8083` | `personal/**` | Microservicio de gestión de personal |
| **auth-service** | `8084` | `/auth/**` | Microservicio de autenticación y emisión de JWT |

---

## Seguridad y Autenticación (JWT)

El sistema implementa un modelo de seguridad por capas con Spring Security y JWT:

1. **Autenticación (Login):** Petición `POST` al endpoint público `/auth/login` a través del API Gateway (`http://localhost:8080/auth/login`).
2. **Emisión de Token:** El microservicio `auth-service` (puerto 8084) valida credenciales y genera la firma JWT.
3. **Acceso a Recursos Protegidos:** Las peticiones hacia `/vuelos`, `/aviones` y `/personal` requieren la cabecera HTTP:
   ```text
   Authorization: Bearer <TU_TOKEN_JWT>
Endpoints Principales (API Gateway - Puerto 8080)
Autenticación (Público): POST http://localhost:8080/auth/login

Gestión de Alumnos (Protegido): http://localhost:8080/vuelos

Gestión de Cursos (Protegido): http://localhost:8080/aviones

Gestión de Personal / Admin (Protegido): http://localhost:8080/personal

Orden de Despliegue y Arranque
eureka-server: Registro de descubrimiento.

config-server: Carga de propiedades centralizadas desde el repositorio Git.

Servicios de Negocio y Seguridad (auth-service, personal-service, vuelos-service, aviones-service).

gateway-service: Enrutamiento de peticiones externas.

Nota: En la ejecución con Docker Compose, este flujo se gestiona automáticamente mediante depends_on y healthchecks.

Documentación de API (Swagger / OpenAPI)
Swagger Auth: http://localhost:8084/swagger-ui.html

Swagger Admin / Personal: http://localhost:8083/swagger-ui.html

Swagger Alumnos: http://localhost:8081/swagger-ui.html

Swagger Cursos: http://localhost:8082/swagger-ui.html

Despliegue con Docker Compose en Máquina Virtual (Linux / Ubuntu)
1. Clonar el repositorio en la Máquina Virtual:
Bash
git clone https://github.com/48115220-sketch/sistema-gestion-aviones
cd sistema-gestion-aviones
2. Levantar la infraestructura completa con Docker Compose:
Bash
docker compose up --build
