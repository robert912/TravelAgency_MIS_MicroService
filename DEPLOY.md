# Guía de Despliegue – TravelAgency Microservicios en Kubernetes

## Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Java JDK | 21 |
| Maven | 3.9 |
| Docker Desktop | Cualquier reciente |
| Minikube | 1.32+ |
| kubectl | 1.28+ |
| Cuenta Docker Hub | hub.docker.com (usuario: `robert912`) |

> **Driver recomendado:** Hyper-V (Windows 11 Pro). Con este driver `minikube ip`
> devuelve una IP **accesible desde el browser** sin necesidad de port-forward ni tunnel.

---

## Paso 1 – Iniciar sesión en Docker Hub

```bash
docker login
# Usuario: robert912
# Contraseña: (tu contraseña de Docker Hub)
```

---

## Paso 2 – Iniciar Minikube con driver Hyper-V

```bash
minikube start --driver=hyperv --memory=6144 --cpus=4
```

Verifica que funciona:
```bash
minikube status
kubectl get nodes
```

Obtén la IP de minikube (la usarás durante todo el proceso):
```bash
minikube ip
# Ejemplo: 172.23.252.25
```

> Guarda esa IP — es la dirección base para acceder al sistema desde el browser.

---

## Paso 3 – Construir las imágenes Docker

> Las imágenes se construyen con el **Docker local** (no el de minikube).
> Se subirán a Docker Hub y Kubernetes las descargará desde allí.

Desde la raíz del proyecto `TravelAgency_MIS_MicroService/`:

```bash
# Infraestructura
docker build -t robert912/config-server:latest   ./config-server
docker build -t robert912/eureka-server:latest   ./eureka-server
docker build -t robert912/api-gateway:latest     ./api-gateway

# Microservicios de negocio
docker build -t robert912/m1-person-service:latest       ./m1-person-service
docker build -t robert912/m2-package-service:latest      ./m2-package-service
docker build -t robert912/m3-search-service:latest       ./m3-search-service
docker build -t robert912/m4-reservation-service:latest  ./m4-reservation-service
docker build -t robert912/m5-payment-service:latest      ./m5-payment-service
docker build -t robert912/m6-confirmation-service:latest ./m6-confirmation-service
docker build -t robert912/m7-report-service:latest       ./m7-report-service
```

---

## Paso 4 – Construir el Frontend con la IP de Minikube

El frontend necesita saber la URL del API Gateway **en el momento del build**.
Usa la IP obtenida en el Paso 2:

```bash
# Reemplaza 172.28.80.1 con el resultado de `minikube ip`
MINIKUBE_IP=172.28.80.1

docker build \
  --build-arg VITE_API_URL=http://$MINIKUBE_IP:30090 \
  --build-arg VITE_AUTH_URL=http://$MINIKUBE_IP:30091 \
  -t robert912/frontend:latest \
  ./frontend

# Si en Windows PowerShell:
# $MINIKUBE_IP = "172.28.80.1"
# docker build --build-arg VITE_API_URL=http://$MINIKUBE_IP:30090 --build-arg VITE_AUTH_URL=http://$MINIKUBE_IP:30091 -t robert912/frontend:latest ./frontend
```

> Verifica que el `Dockerfile` del frontend acepta el build-arg `VITE_API_URL`.
> Si la URL está hardcodeada, edítala directamente en `.env.production` antes de hacer el build.

---

## Paso 5 – Subir todas las imágenes a Docker Hub

```bash
docker push robert912/config-server:latest
docker push robert912/eureka-server:latest
docker push robert912/api-gateway:latest
docker push robert912/m1-person-service:latest
docker push robert912/m2-package-service:latest
docker push robert912/m3-search-service:latest
docker push robert912/m4-reservation-service:latest
docker push robert912/m5-payment-service:latest
docker push robert912/m6-confirmation-service:latest
docker push robert912/m7-report-service:latest
docker push robert912/frontend:latest
docker push robert912/keycloak:latest
```

Verifica en hub.docker.com que los repositorios aparecen como públicos.

---

## Paso 6 – Desplegar en Kubernetes

Aplica los manifests en orden:

```bash
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-secrets.yaml
kubectl apply -f k8s/02-configmap.yaml
kubectl apply -f k8s/03-databases.yaml
kubectl apply -f k8s/04-config-server.yaml
kubectl apply -f k8s/05-eureka-server.yaml
kubectl apply -f k8s/06-microservices.yaml
kubectl apply -f k8s/07-api-gateway.yaml
kubectl apply -f k8s/08-keycloak.yaml
kubectl apply -f k8s/09-frontend.yaml
```

O en un solo comando:
```bash
kubectl apply -f k8s/
```

---

## Paso 7 – Verificar que todo está corriendo

```bash
kubectl get pods -n travel-agency
```

Espera hasta que todos los pods muestren `Running` con `READY 1/1`.
Esto puede tomar **3 a 7 minutos** la primera vez.

```bash
# Ver todos los servicios y sus puertos
kubectl get services -n travel-agency

# Logs de un servicio (útil para depurar)
kubectl logs -n travel-agency deployment/m1-person-service
kubectl logs -n travel-agency deployment/config-server
kubectl logs -n travel-agency deployment/eureka-server
```

---

## Paso 8 – Acceder a la aplicación

Con Hyper-V la IP de Minikube es **directamente accesible desde el browser** de Windows.

```bash
minikube ip
# Ejemplo: 172.28.80.1
```

| Componente | URL de acceso |
|---|---|
| **Frontend** | `http://172.28.80.1:30080` |
| **API Gateway** | `http://172.28.80.1:30090` |
| **Keycloak** | `http://172.28.80.1:30091` |
| **Eureka Dashboard** | solo interno (ClusterIP) |

> **Sin port-forward:** El acceso es directo mediante NodePort.
> `kubectl port-forward` **NO se usa** en ninguna parte de este despliegue.

---

## Verificación rápida de endpoints

```bash
MINIKUBE_IP=$(minikube ip)

# Health del gateway
curl http://$MINIKUBE_IP:30090/actuator/health

# Listar paquetes activos (Épica 3)
curl http://$MINIKUBE_IP:30090/api/tour-packages/active

# Listar personas (Épica 1)
curl http://$MINIKUBE_IP:30090/api/persons/
```

---

## Referencia de puertos (NodePort)

| Servicio | Puerto interno | NodePort externo |
|---|---|---|
| Frontend | 80 | **30080** |
| API Gateway | 8090 | **30090** |
| Keycloak | 9090 | **30091** |
| Eureka Server | 8761 | solo interno |
| Config Server | 8888 | solo interno |
| M1–M7 | dinámico (Eureka) | solo interno |
| MySQL M1–M6 | 3306 | solo interno |

---

## Comandos útiles de mantenimiento

```bash
# Ver estado de pods
kubectl get pods -n travel-agency

# Ver pods con IP asignada
kubectl get pods -n travel-agency -o wide

# Reiniciar un deployment (ej: si actualizas la imagen)
kubectl rollout restart deployment/m1-person-service -n travel-agency

# Acceder a la DB de M1 directamente (para inspección)
kubectl exec -it -n travel-agency deployment/mysql-m1 -- mysql -uroot -p12345678 db_m1_person

# Eliminar todo y redesplegar desde cero
kubectl delete namespace travel-agency
kubectl apply -f k8s/

# Ver uso de recursos
kubectl top pods -n travel-agency

# Minikube dashboard (interfaz gráfica)
minikube dashboard
```

---

## Rebuilding tras cambios de código

Si modificas el código de un microservicio:

```bash
# 1. Reconstruye la imagen con Docker local
docker build -t robert912/m1-person-service:latest ./m1-person-service

# 2. Sube la nueva imagen a Docker Hub
docker push robert912/m1-person-service:latest

# 3. Reinicia el deployment para que descargue la nueva imagen
kubectl rollout restart deployment/m1-person-service -n travel-agency
```

---

## Flujo de arranque interno (orden de dependencias)

```
MySQL DBs  →  Config Server  →  Eureka Server  →  M1–M7  →  API Gateway  →  Frontend
```

Los `initContainers` en M1, M2, M4, M5 y M6 esperan automáticamente
que sus bases de datos estén listas antes de arrancar.

---

## Troubleshooting

### Pod en CrashLoopBackOff
```bash
kubectl logs -n travel-agency deployment/<nombre-del-servicio> --previous
```

### Hyper-V no disponible
Si `minikube start --driver=hyperv` falla, verifica que Hyper-V esté habilitado:
```powershell
# En PowerShell como Administrador:
Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V -All
# Reinicia el sistema tras habilitarlo
```

### Imagen no encontrada (ErrImagePull)
```bash
# Verifica que la imagen existe en Docker Hub
docker pull robert912/m1-person-service:latest

# Si no existe, vuelve a hacer build + push
docker build -t robert912/m1-person-service:latest ./m1-person-service
docker push robert912/m1-person-service:latest
```
