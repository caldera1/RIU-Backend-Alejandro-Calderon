# Hotel Availability Search API

Servicio Spring Boot que gestiona búsquedas de disponibilidad hotelera. Recibe búsquedas via REST, las publica en Kafka, las consume y persiste en PostgreSQL, y permite consultar cuántas veces se repitió una búsqueda idéntica.

## Stack

- Java 21 con virtual threads
- Spring Boot 3.4.4
- Apache Kafka (KRaft)
- JaCoCo (cobertura >= 80%)
- Testcontainers + H2 para tests
- Swagger / OpenAPI
- PostgreSQL 17 (el challenge especifica Oracle; se usa PostgreSQL porque la capa de persistencia es agnóstica al motor via JPA/Hibernate, permitiendo migrar cambiando solo el driver y el dialect)

## Arquitectura

Hexagonal (ports & adapters), paquete único sin módulos Maven:

```
com.sling.hotel/
├── domain/model/          Modelos de dominio (records inmutables)
├── domain/port/in/        Casos de uso
├── domain/port/out/       Puertos de salida (repositorio, publisher)
├── application/service/   Implementación de casos de uso
└── infrastructure/
    ├── adapter/in/rest/   Controller REST
    ├── adapter/in/kafka/  Consumer Kafka
    ├── adapter/out/kafka/ Producer Kafka
    ├── adapter/out/persistence/ JPA adapter
    └── config/            Configuraciones Spring
```

## Endpoints

### POST /search

Registra una búsqueda de disponibilidad.

**Request:**
```json
{
  "hotelId": "1234aBc",
  "checkIn": "29/12/2023",
  "checkOut": "31/12/2023",
  "ages": [30, 29, 1, 3]
}
```

**Response (200):**
```json
{
  "searchId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Validaciones:**
- `hotelId`: obligatorio, no vacío
- `checkIn`, `checkOut`: obligatorios, formato `dd/MM/yyyy`
- `checkIn` debe ser anterior a `checkOut`
- `ages`: obligatorio, al menos un elemento

### GET /count?searchId={id}

Devuelve cuántas búsquedas idénticas se realizaron. El orden de `ages` influye en el conteo.

**Response (200):**
```json
{
  "searchId": "550e8400-e29b-41d4-a716-446655440000",
  "search": {
    "hotelId": "1234aBc",
    "checkIn": "29/12/2023",
    "checkOut": "31/12/2023",
    "ages": [30, 29, 1, 3]
  },
  "count": 3
}
```

**Response (404):** si el `searchId` no existe.

## Ejecución con Docker

**Nota sobre base de datos**: el challenge original especifica Oracle. Se optó por PostgreSQL por compatibilidad de entorno. La implementación JPA es completamente agnóstica al motor — migrar a Oracle requiere únicamente agregar el driver `ojdbc` en `pom.xml` y actualizar el dialect en `application.yml`.

```bash
docker compose up --build
```

Levanta la aplicación, PostgreSQL y Kafka. La app queda disponible en `http://localhost:8080`.

## Ejecución local (sin Docker)

Requiere Java 21, PostgreSQL y Kafka corriendo en localhost.

```bash
./mvnw spring-boot:run
```

## Tests

```bash
./mvnw test
```

Los tests de integración usan Testcontainers (requiere Docker corriendo).

## Cobertura

JaCoCo está configurado con un mínimo de 80% en líneas, branches, métodos e instrucciones. El reporte se genera en `target/site/jacoco/index.html`.

```bash
./mvnw test jacoco:report
```

## Swagger UI

Disponible en `http://localhost:8080/swagger-ui/index.html` con la app corriendo.
