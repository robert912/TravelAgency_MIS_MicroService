# Guía de Pruebas Locales con Postman — TravelAgency Microservices

## Prerequisitos

| Herramienta | Versión mínima |
|---|---|
| Java | 21 |
| Maven | 3.9+ |
| MySQL | 8.0 |
| Postman | Cualquiera |

---

## Configuración inicial

### 1. Solo necesitas tener MySQL corriendo

Las bases de datos se crean automáticamente gracias a `createDatabaseIfNotExist=true` que ya está en el config server. No necesitas ejecutar ningún script SQL previo.

> M3 (Search) y M7 (Report) no tienen base de datos propia.

---

### 2. Variables de entorno para arrancar cada servicio

Todos los servicios usan `server.port=0` (asignado por Eureka en producción).  
Para pruebas locales, sobreescribe el puerto y deshabilita Eureka y Config Server con estas variables al arrancar Maven:

```
-Dserver.port=<PUERTO>
-Dspring.cloud.config.enabled=false
-Deureka.client.enabled=false
-Dspring.datasource.url="jdbc:mysql://localhost:3306/<BASE_DATOS>?createDatabaseIfNotExist=true"
-Dspring.datasource.username=root
-Dspring.datasource.password=12345678
-Dspring.jpa.hibernate.ddl-auto=update
```

> **Seguridad JWT:** Todos los endpoints (salvo los públicos indicados) requieren un Bearer Token de Keycloak.  
> Para pruebas rápidas sin Keycloak, agrega también:  
> `-Dspring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration`  
> Esto deshabilita la validación JWT completamente.

---

### 3. Ports asignados para pruebas locales

| Servicio | Puerto local |
|---|---|
| m1-person-service | **8081** |
| m2-package-service | **8082** |
| m3-search-service | **8083** |
| m4-reservation-service | **8084** |
| m5-payment-service | **8085** |
| m6-confirmation-service | **8086** |
| m7-report-service | **8087** |

---

### 4. Importar colección en Postman

Crea un **Environment** en Postman con estas variables:

| Variable | Valor |
|---|---|
| `m1_url` | `http://localhost:8081` |
| `m2_url` | `http://localhost:8082` |
| `m3_url` | `http://localhost:8083` |
| `m4_url` | `http://localhost:8084` |
| `m5_url` | `http://localhost:8085` |
| `m6_url` | `http://localhost:8086` |
| `m7_url` | `http://localhost:8087` |
| `token` | *(tu JWT de Keycloak, si usas autenticación)* |

En cada request autenticado, agrega en **Authorization → Bearer Token**: `{{token}}`

---

## M1 — Person Service

### Arrancar el servicio

```bash
cd m1-person-service
mvn spring-boot:run \
  -Dserver.port=8081 \
  -Dspring.cloud.config.enabled=false \
  -Deureka.client.enabled=false \
  -Dspring.datasource.url="jdbc:mysql://localhost:3306/db_m1_person?createDatabaseIfNotExist=true" \
  -Dspring.datasource.username=root \
  -Dspring.datasource.password=12345678 \
  -Dspring.jpa.hibernate.ddl-auto=update
```

### Endpoints

#### Listar todas las personas
```
GET {{m1_url}}/api/persons/
```

#### Listar personas activas
```
GET {{m1_url}}/api/persons/active
```

#### Obtener persona por ID
```
GET {{m1_url}}/api/persons/1
```

#### Buscar persona por identificación o email
```
GET {{m1_url}}/api/persons/search?query=12345678
GET {{m1_url}}/api/persons/search?query=juan@email.com
```

#### Crear persona
```
POST {{m1_url}}/api/persons/
Content-Type: application/json

{
  "fullName": "Juan Pérez",
  "identification": "12345678",
  "email": "juan.perez@email.com",
  "phone": "+56912345678",
  "nationality": "Chilena",
  "active": 1
}
```

#### Actualizar persona
```
PUT {{m1_url}}/api/persons/
Content-Type: application/json

{
  "id": 1,
  "fullName": "Juan Pérez Actualizado",
  "identification": "12345678",
  "email": "juan.perez@email.com",
  "phone": "+56987654321",
  "nationality": "Chilena",
  "active": 1
}
```

---

## M2 — Package Service

### Arrancar el servicio

```bash
cd m2-package-service
mvn spring-boot:run \
  -Dserver.port=8082 \
  -Dspring.cloud.config.enabled=false \
  -Deureka.client.enabled=false \
  -Dspring.datasource.url="jdbc:mysql://localhost:3306/db_m2_package?createDatabaseIfNotExist=true" \
  -Dspring.datasource.username=root \
  -Dspring.datasource.password=12345678 \
  -Dspring.jpa.hibernate.ddl-auto=update
```

> **⚠️ Orden de creación:** Para crear un paquete turístico necesitas tener creados primero una Temporada, Categoría y Tipo de Viaje.

### Endpoints — Categorías

#### Listar todas
```
GET {{m2_url}}/api/categories/
```
#### Listar activas
```
GET {{m2_url}}/api/categories/active
```
#### Obtener por ID
```
GET {{m2_url}}/api/categories/1
```
#### Crear
```
POST {{m2_url}}/api/categories/
Content-Type: application/json

{
  "name": "Aventura",
  "description": "Paquetes de aventura y deporte extremo",
  "active": 1
}
```
#### Actualizar
```
PUT {{m2_url}}/api/categories/
Content-Type: application/json

{
  "id": 1,
  "name": "Aventura Premium",
  "description": "Paquetes de aventura de lujo",
  "active": 1
}
```

---

### Endpoints — Temporadas (Seasons)

#### Crear temporada
```
POST {{m2_url}}/api/seasons/
Content-Type: application/json

{
  "name": "Verano 2025",
  "startDate": "2025-12-01",
  "endDate": "2026-03-31",
  "active": 1
}
```
#### Listar todas
```
GET {{m2_url}}/api/seasons/
```

---

### Endpoints — Tipos de Viaje

#### Crear tipo de viaje
```
POST {{m2_url}}/api/travel-types/
Content-Type: application/json

{
  "name": "Crucero",
  "description": "Viajes en crucero marítimo",
  "active": 1
}
```
#### Listar activos *(endpoint público — no requiere token)*
```
GET {{m2_url}}/api/travel-types/active
```

---

### Endpoints — Condiciones y Restricciones

#### Crear condición
```
POST {{m2_url}}/api/conditions/
Content-Type: application/json

{
  "name": "No incluye vuelos",
  "description": "El paquete no incluye pasajes aéreos",
  "active": 1
}
```

#### Crear restricción
```
POST {{m2_url}}/api/restrictions/
Content-Type: application/json

{
  "name": "Edad mínima 18 años",
  "description": "Se requiere ser mayor de edad",
  "active": 1
}
```

---

### Endpoints — Servicios incluidos

#### Crear servicio
```
POST {{m2_url}}/api/services/
Content-Type: application/json

{
  "name": "Desayuno buffet",
  "description": "Desayuno incluido todas las mañanas",
  "active": 1
}
```

---

### Endpoints — Paquetes Turísticos

#### Listar activos *(público — no requiere token)*
```
GET {{m2_url}}/api/tour-packages/active
```

#### Obtener por ID *(público — no requiere token)*
```
GET {{m2_url}}/api/tour-packages/1
```

#### Buscar / filtrar
```
GET {{m2_url}}/api/tour-packages/search?destination=Cancún
GET {{m2_url}}/api/tour-packages/search?minPrice=500&maxPrice=2000
GET {{m2_url}}/api/tour-packages/search?startDate=2025-12-01&endDate=2026-01-31
GET {{m2_url}}/api/tour-packages/search?travelTypeId=1
```

#### Crear paquete turístico
> Requiere que existan: `seasonId`, `categoryId`, `travelTypeId`

```
POST {{m2_url}}/api/tour-packages/
Content-Type: application/json

{
  "name": "Caribe Paradisíaco",
  "destination": "Cancún, México",
  "description": "7 noches en resort all-inclusive frente al mar",
  "startDate": "2025-12-15",
  "endDate": "2025-12-22",
  "price": 1500.00,
  "totalSlots": 20,
  "stars": 5,
  "imageUrl": "https://example.com/cancun.jpg",
  "status": "DISPONIBLE",
  "active": 1,
  "season": { "id": 1 },
  "category": { "id": 1 },
  "travelType": { "id": 1 }
}
```

#### Actualizar paquete
```
PUT {{m2_url}}/api/tour-packages/
Content-Type: application/json

{
  "id": 1,
  "name": "Caribe Paradisíaco Actualizado",
  "destination": "Cancún, México",
  "startDate": "2025-12-15",
  "endDate": "2025-12-22",
  "price": 1350.00,
  "totalSlots": 25,
  "stars": 5,
  "status": "DISPONIBLE",
  "active": 1,
  "season": { "id": 1 },
  "category": { "id": 1 },
  "travelType": { "id": 1 }
}
```

#### Sincronizar condiciones de un paquete
```
PUT {{m2_url}}/api/tour-package-conditions/package/1/sync?userId=1
Content-Type: application/json

{
  "conditionIds": [1, 2]
}
```

#### Sincronizar restricciones de un paquete
```
PUT {{m2_url}}/api/tour-package-restrictions/package/1/sync?userId=1
Content-Type: application/json

{
  "restrictionIds": [1]
}
```

#### Sincronizar servicios de un paquete
```
PUT {{m2_url}}/api/tour-package-services/package/1/sync?userId=1
Content-Type: application/json

{
  "serviceIds": [1, 2, 3]
}
```

---

## M3 — Search Service

> M3 actúa como agregador: no tiene BD propia. Llama internamente a M2 y M4.  
> Para pruebas locales, M2 y M4 deben estar corriendo.

### Arrancar el servicio

```bash
cd m3-search-service
mvn spring-boot:run \
  -Dserver.port=8083 \
  -Dspring.cloud.config.enabled=false \
  -Deureka.client.enabled=false
```

### Endpoints

#### Listar paquetes activos *(público — no requiere token)*
```
GET {{m3_url}}/api/tour-packages/active
```

#### Buscar paquetes con filtros
```
GET {{m3_url}}/api/tour-packages/search?destination=Cancún
GET {{m3_url}}/api/tour-packages/search?minPrice=500&maxPrice=2000&startDate=2025-12-01
```

#### Ver disponibilidad de un paquete *(público)*
```
GET {{m3_url}}/api/tour-packages/1/availability
```

Respuesta esperada:
```json
{
  "packageId": 1,
  "totalSlots": 20,
  "confirmedPassengers": 5,
  "availableSlots": 15,
  "available": true
}
```

#### Verificar si hay cupos suficientes *(público)*
```
GET {{m3_url}}/api/tour-packages/1/availability/check?quantity=3
```

Respuesta esperada:
```json
{
  "available": true,
  "availableSlots": 15,
  "requestedQuantity": 3
}
```

---

## M4 — Reservation Service

> M4 llama a M1 (personas) y M2 (paquetes) para enriquecer las respuestas.  
> Para pruebas locales completas, M1 y M2 deben estar corriendo.

### Arrancar el servicio

```bash
cd m4-reservation-service
mvn spring-boot:run \
  -Dserver.port=8084 \
  -Dspring.cloud.config.enabled=false \
  -Deureka.client.enabled=false \
  -Dspring.datasource.url="jdbc:mysql://localhost:3306/db_m4_reservation?createDatabaseIfNotExist=true" \
  -Dspring.datasource.username=root \
  -Dspring.datasource.password=12345678 \
  -Dspring.jpa.hibernate.ddl-auto=update
```

### Endpoints

#### Listar todas las reservas
```
GET {{m4_url}}/api/reservations/
```

#### Obtener reserva por ID
```
GET {{m4_url}}/api/reservations/1
```

#### Obtener pasajeros de una reserva
```
GET {{m4_url}}/api/reservations/1/passengers
```

#### Obtener reservas de una persona
```
GET {{m4_url}}/api/reservations/person/1
```

#### Contar pasajeros confirmados de un paquete *(usado internamente por M3)*
```
GET {{m4_url}}/api/reservations/count-confirmed-passengers?packageId=1
```

#### Crear reserva — con personId existente
```
POST {{m4_url}}/api/reservations/create?userId=1
Content-Type: application/json

{
  "personId": 1,
  "tourPackageId": 1,
  "passengers": 2,
  "specialRequests": "Habitación con vista al mar",
  "subtotal": 3000.00,
  "totalAmount": 2850.00,
  "discountAmount": 150.00,
  "passengersData": [
    {
      "personId": 2
    }
  ]
}
```

#### Crear reserva — persona nueva (sin personId)
```
POST {{m4_url}}/api/reservations/create?userId=1
Content-Type: application/json

{
  "identification": "87654321",
  "fullName": "María González",
  "email": "maria.gonzalez@email.com",
  "phone": "+56987654321",
  "nationality": "Chilena",
  "tourPackageId": 1,
  "passengers": 1,
  "specialRequests": "Dieta vegetariana",
  "subtotal": 1500.00,
  "totalAmount": 1500.00,
  "discountAmount": 0,
  "passengersData": []
}
```

#### Crear reserva — con descuentos
```
POST {{m4_url}}/api/reservations/create?userId=1
Content-Type: application/json

{
  "personId": 1,
  "tourPackageId": 1,
  "passengers": 2,
  "subtotal": 3000.00,
  "totalAmount": 2700.00,
  "discountAmount": 300.00,
  "discountsDetail": [
    {
      "name": "Descuento estudiante",
      "description": "10% descuento para estudiantes",
      "amount": 300.00
    }
  ],
  "passengersData": []
}
```

#### Cambiar estado de reserva
```
PUT {{m4_url}}/api/reservations/1/status?status=PAGADA&userId=1
```

Estados válidos y transiciones permitidas:
```
PENDIENTE → PAGADA
PENDIENTE → CANCELADA
PENDIENTE → EXPIRADA
PAGADA    → CANCELADA
CANCELADA → (ninguno)
EXPIRADA  → (ninguno)
```

#### Actualizar reserva
```
PUT {{m4_url}}/api/reservations/
Content-Type: application/json

{
  "id": 1,
  "status": "PAGADA",
  "active": 1,
  "modifiedByUserId": 1
}
```

---

## M5 — Payment Service

> M5 llama a M4 para verificar la reserva y actualizar su estado.  
> Para pruebas locales completas, M4 debe estar corriendo.

### Arrancar el servicio

```bash
cd m5-payment-service
mvn spring-boot:run \
  -Dserver.port=8085 \
  -Dspring.cloud.config.enabled=false \
  -Deureka.client.enabled=false \
  -Dspring.datasource.url="jdbc:mysql://localhost:3306/db_m5_payment?createDatabaseIfNotExist=true" \
  -Dspring.datasource.username=root \
  -Dspring.datasource.password=12345678 \
  -Dspring.jpa.hibernate.ddl-auto=update
```

### Endpoints

#### Procesar pago
> La reserva debe estar en estado `PENDIENTE` y no tener pago previo.

```
POST {{m5_url}}/api/payments/process
Content-Type: application/json

{
  "reservationId": 1,
  "cardNumber": "4111111111111111",
  "cardHolderName": "JUAN PEREZ",
  "cardExpiration": "12/27",
  "cardCvv": "123",
  "paymentMethod": "TARJETA_CREDITO",
  "userId": 1
}
```

Respuesta exitosa:
```json
{
  "id": 1,
  "reservationId": 1,
  "amount": 1500.00,
  "paymentMethod": "TARJETA_CREDITO",
  "cardNumber": "**** **** **** 1111",
  "cardExpiration": "12/27",
  "transactionId": "TXN-A1B2C3D4-1718300000000"
}
```

Errores comunes:
```
"Solo se pueden pagar reservas en estado PENDIENTE"  → reserva ya pagada/cancelada
"Esta reserva ya tiene un pago registrado"           → pago duplicado
"Numero de tarjeta invalido"                         → debe tener 16 dígitos
"CVV invalido"                                       → debe tener 3 dígitos
"Fecha de expiracion invalida"                       → formato MM/YY o MM/YYYY
"Nombre del titular es requerido"                    → campo vacío
```

#### Listar todos los pagos
```
GET {{m5_url}}/api/payments/
```

#### Obtener pago por ID
```
GET {{m5_url}}/api/payments/1
```

#### Obtener pago por ID de reserva
```
GET {{m5_url}}/api/payments/reservation/1
```

---

## M6 — Confirmation Service

> M6 es una capa sobre M4: lee y actualiza reservas delegando en M4.  
> Para pruebas locales, M4 debe estar corriendo.

### Arrancar el servicio

```bash
cd m6-confirmation-service
mvn spring-boot:run \
  -Dserver.port=8086 \
  -Dspring.cloud.config.enabled=false \
  -Deureka.client.enabled=false \
  -Dspring.datasource.url="jdbc:mysql://localhost:3306/db_m6_confirmation?createDatabaseIfNotExist=true" \
  -Dspring.datasource.username=root \
  -Dspring.datasource.password=12345678 \
  -Dspring.jpa.hibernate.ddl-auto=update
```

### Endpoints

#### Listar todas las reservas
```
GET {{m6_url}}/api/confirmations/
```

#### Obtener reserva por ID
```
GET {{m6_url}}/api/confirmations/1
```

#### Obtener pasajeros de una reserva
```
GET {{m6_url}}/api/confirmations/1/passengers
```

#### Obtener reservas de una persona
```
GET {{m6_url}}/api/confirmations/person/1
```

#### Confirmar / cambiar estado de reserva
```
PUT {{m6_url}}/api/confirmations/1/status?status=PAGADA&userId=1
```

> Usa los mismos estados y transiciones que M4.

---

## M7 — Report Service

> M7 llama a M4 y M5 para construir los reportes.  
> Para pruebas locales, M4 y M5 deben estar corriendo.  
> **Todos los endpoints de M7 requieren rol `Admin` en el JWT.**

### Arrancar el servicio

```bash
cd m7-report-service
mvn spring-boot:run \
  -Dserver.port=8087 \
  -Dspring.cloud.config.enabled=false \
  -Deureka.client.enabled=false
```

### Endpoints

#### Reporte de ventas por período
```
GET {{m7_url}}/api/reports/sales?startDate=2025-01-01&endDate=2025-12-31
```

#### Ranking de paquetes más vendidos
```
GET {{m7_url}}/api/reports/package-ranking?startDate=2025-01-01&endDate=2025-12-31
```

---

## Flujo completo de prueba (happy path)

Sigue este orden para probar el ciclo completo del sistema:

```
1. [M2] POST /api/categories/          → crear categoría
2. [M2] POST /api/seasons/             → crear temporada
3. [M2] POST /api/travel-types/        → crear tipo de viaje
4. [M2] POST /api/tour-packages/       → crear paquete turístico
5. [M1] POST /api/persons/             → crear persona (cliente)
6. [M4] POST /api/reservations/create  → crear reserva
7. [M3] GET  /api/tour-packages/1/availability → verificar disponibilidad (slots -1)
8. [M5] POST /api/payments/process     → procesar pago (estado → PAGADA)
9. [M6] GET  /api/confirmations/1      → verificar confirmación
10.[M7] GET  /api/reports/sales        → ver reporte de ventas
```

---

## Resumen de endpoints públicos (sin token)

| Servicio | Endpoint |
|---|---|
| M2 | `GET /api/tour-packages/active` |
| M2 | `GET /api/tour-packages/{id}` |
| M2 | `GET /api/travel-types/active` |
| M3 | `GET /api/tour-packages/active` |
| M3 | `GET /api/tour-packages/{id}/availability` |
| M3 | `GET /api/tour-packages/{id}/availability/check` |
| Todos | `GET /swagger-ui/**` |
| Todos | `GET /v3/api-docs/**` |

---

## Notas importantes

- **RestTemplate vs Eureka local:** Cuando M3, M4, M5, M6 llaman internamente a otros servicios, usan nombres como `http://m1-person-service/...`. En local, estos nombres no resuelven. Para pruebas completamente aisladas (un solo microservicio a la vez), los endpoints que hacen llamadas internas retornarán datos enriquecidos vacíos (`person: null`, `tourPackage: null`) pero el resto funcionará correctamente.
- **`ddl-auto=update`:** Hibernate crea las tablas automáticamente la primera vez. No se necesita script SQL manual.
- **Swagger UI disponible** en cada servicio corriendo: `http://localhost:<PUERTO>/swagger-ui/index.html`
