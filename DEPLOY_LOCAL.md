# Guía de Despliegue – TravelAgency Local

---

## Para levantar todo:
```
cd TravelAgency_MIS_MicroService
docker compose up --build
```

| Servicio | URL |
|---|---|
| **Frontend** | http://localhost:5173 |
| **API Gateway** | http://localhost:8090 |
| **Keycloak** | http://localhost:9090 |
| **Eureka** | http://localhost:8761 |

URLs una vez levantado:

Usuarios de prueba:

| Usuario | Password | Rol |
|---|---|---|
|admin|Admin1234|Admin (acceso total)|
|user|user123|User (solo reservas)|

