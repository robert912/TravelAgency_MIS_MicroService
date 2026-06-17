# Guía de Despliegue – TravelAgency Microservicios en Kubernetes

## Requisitos previos

Instala las siguientes herramientas si no las tienes:

| Herramienta | Versión mínima | Descarga |
|---|---|---|
| Java JDK | 21 | https://adoptium.net |
| Maven | 3.9 | https://maven.apache.org |
| Docker Desktop | Cualquier reciente | https://docker.com |
| Minikube | 1.32+ | https://minikube.sigs.k8s.io |
| kubectl | 1.28+ | https://kubernetes.io/docs/tasks/tools |

---

## Paso 1 – Iniciar Minikube

```bash
minikube start --memory=6144 --cpus=4
```

> **Importante:** Los microservicios y las bases de datos consumen bastante RAM.
> Se recomienda al menos 6 GB asignados a Minikube.

Verifica que funciona:
```bash
kubectl get nodes
```

---

## Paso 2 – Apuntar Docker al daemon de Minikube

Con este paso las imágenes que construyas quedan disponibles directamente
dentro del cluster **sin necesidad de un registry externo**.

**En PowerShell (Windows):**
```powershell
& minikube -p minikube docker-env --shell powershell | Invoke-Expression
```

**En Bash / Git Bash:**
```bash
eval $(minikube docker-env)
```

> Debes ejecutar este comando **en cada terminal nueva** que uses para hacer `docker build`.

---

## Paso 3 – Construir las imágenes Docker

Desde la raíz del proyecto `TravelAgency_MIS_MicroService/`, ejecuta los
siguientes comandos **en el orden indicado**:

```bash
# Infraestructura
docker build -t travel/config-server:latest  ./config-server
docker build -t travel/eureka-server:latest  ./eureka-server
docker build -t travel/api-gateway:latest    ./api-gateway

# Microservicios de negocio
docker build -t travel/m1-person-service:latest       ./m1-person-service
docker build -t travel/m2-package-service:latest      ./m2-package-service
docker build -t travel/m3-search-service:latest       ./m3-search-service
docker build -t travel/m4-reservation-service:latest  ./m4-reservation-service
docker build -t travel/m5-payment-service:latest      ./m5-payment-service
docker build -t travel/m6-confirmation-service:latest ./m6-confirmation-service
docker build -t travel/m7-report-service:latest       ./m7-report-service
```

Verifica que las imágenes están disponibles:
```bash
docker images | grep travel
```

---

## Paso 4 – Desplegar en Kubernetes

Aplica los manifests **en orden numérico** (el orden importa para las dependencias):

```bash
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-secrets.yaml
kubectl apply -f k8s/02-configmap.yaml
kubectl apply -f k8s/03-databases.yaml
kubectl apply -f k8s/04-config-server.yaml
kubectl apply -f k8s/05-eureka-server.yaml
kubectl apply -f k8s/06-microservices.yaml
kubectl apply -f k8s/07-api-gateway.yaml
```

O en un solo comando:
```bash
kubectl apply -f k8s/
```

---

## Paso 5 – Verificar que todo está corriendo

### Ver el estado de todos los pods:
```bash
kubectl get pods -n travel-agency
```

Espera hasta que todos los pods muestren `Running` con `READY 1/1`.
Esto puede tomar **2 a 5 minutos** la primera vez (las bases de datos tardan en iniciarse).

### Ver servicios:
```bash
kubectl get services -n travel-agency
```

### Ver logs de un servicio específico (útil para depurar):
```bash
kubectl logs -n travel-agency deployment/m1-person-service
kubectl logs -n travel-agency deployment/config-server
kubectl logs -n travel-agency deployment/eureka-server
```

### Verificar Eureka Dashboard:
```bash
minikube service eureka-server -n travel-agency --url
```
Abre esa URL en el navegador para ver todos los servicios registrados.

---

## Paso 6 – Obtener la URL del API Gateway

```bash
minikube service api-gateway -n travel-agency --url
```

Este comando imprime algo como: `http://192.168.49.2:30090`

**Esa es la URL base que debes configurar en el frontend** en lugar de
`http://localhost:8090`.

> La rúbrica prohíbe `port-forward`. El NodePort (30090) resuelve esto:
> el acceso es directo desde el navegador a través de la IP de Minikube.

---

## Paso 7 – Configurar el Frontend

En el proyecto frontend, actualiza la variable de URL del backend:

```
# Antes (desarrollo local):
VITE_API_URL=http://localhost:8090

# Después (Kubernetes):
VITE_API_URL=http://$(minikube ip):30090
```

Para obtener la IP de Minikube:
```bash
minikube ip
# Ejemplo: 192.168.49.2
```

---

## Verificación rápida de endpoints

```bash
# Reemplaza <IP> con el resultado de `minikube ip`
IP=$(minikube ip)

# Health del gateway
curl http://$IP:30090/actuator/health

# Listar paquetes activos (Épica 3)
curl http://$IP:30090/api/tour-packages/active

# Listar personas (Épica 1)
curl http://$IP:30090/api/persons/
```

---

## Referencia de puertos

| Servicio | Puerto interno | NodePort externo |
|---|---|---|
| API Gateway | 8090 | **30090** (acceso externo) |
| Eureka Server | 8761 | solo interno |
| Config Server | 8888 | solo interno |
| M1–M7 | dinámico (port=0) | solo interno (vía Eureka) |
| MySQL M1–M6 | 3306 | solo interno |

---

## Comandos útiles de mantenimiento

```bash
# Reiniciar un deployment (ej: si cambias el código)
kubectl rollout restart deployment/m1-person-service -n travel-agency

# Eliminar todo y volver a desplegar desde cero
kubectl delete namespace travel-agency
kubectl apply -f k8s/

# Ver uso de recursos
kubectl top pods -n travel-agency

# Acceder a la DB de M1 directamente (para inspección)
kubectl exec -it -n travel-agency deployment/mysql-m1 -- mysql -uroot -p12345678 db_m1_person
```

---

## Flujo de arranque (orden interno)

```
MySQL DBs  →  Config Server  →  Eureka Server  →  M1–M7  →  API Gateway
```

Los `initContainers` en M1, M2, M4, M5 y M6 esperan automáticamente
que sus bases de datos estén listas antes de arrancar.

---

## Rebuilding tras cambios de código

Si modificas el código de un microservicio:

```bash
# 1. Asegúrate de estar usando el Docker de Minikube
eval $(minikube docker-env)         # bash
# ó
& minikube -p minikube docker-env --shell powershell | Invoke-Expression  # PowerShell

# 2. Reconstruye solo la imagen afectada
docker build -t travel/m1-person-service:latest ./m1-person-service

# 3. Reinicia el deployment para que tome la nueva imagen
kubectl rollout restart deployment/m1-person-service -n travel-agency
```
