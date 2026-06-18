# Despliegue desde otro equipo (casa u otro PC)

> Usa esta guía cuando despliegas en un equipo diferente al del trabajo.
> La IP de Minikube cambia entre equipos, por eso hay que actualizar 3 archivos
> y reconstruir 2 imágenes (frontend y keycloak). Los microservicios del backend
> NO necesitan rebuild.

---

## Paso 1 – Requisitos (instalar si no están)

| Herramienta | Descarga |
|---|---|
| Docker Desktop | https://docker.com |
| Minikube | https://minikube.sigs.k8s.io |
| kubectl | https://kubernetes.io/docs/tasks/tools |
| Git | https://git-scm.com |

---

## Paso 2 – Clonar / actualizar el proyecto

```bash
# Si es la primera vez en este equipo:
git clone <url-del-repo> TravelAgency_MIS_MicroService
cd TravelAgency_MIS_MicroService

# Si ya lo tienes clonado:
git pull
```

---

## Paso 3 – Iniciar Minikube y obtener la IP

```powershell
# En PowerShell como Administrador:
minikube start --driver=hyperv --memory=4096 --cpus=4
minikube ip
```

> Anota la IP que aparece. Ejemplo: `172.19.48.1`
> En el resto de la guía se usa `<MINIKUBE_IP>` — reemplázala por tu IP real.

---

## Paso 4 – Actualizar la IP en el Frontend

Edita el archivo `.env` del proyecto frontend:

**Archivo:** `D:\Proyecto_Universidad\TravelAgency\TravelAgency_MIS_frontend\.env`

```env
VITE_API_URL=http://<MINIKUBE_IP>:30090
VITE_KEYCLOAK_URL=http://<MINIKUBE_IP>:30091
VITE_KEYCLOAK_REALM=travel-realm
VITE_KEYCLOAK_CLIENT_ID=travel-frontend
```

---

## Paso 5 – Actualizar la IP en el Realm de Keycloak

Edita el archivo:
`keycloak/travel-realm-realm.json`

Busca el bloque del cliente `travel-frontend` (cerca del final del archivo) y actualiza:

```json
"rootUrl": "http://<MINIKUBE_IP>:30080",
"adminUrl": "http://<MINIKUBE_IP>:30080",
"redirectUris": [
  "http://localhost:5173/*",
  "http://<MINIKUBE_IP>:30080/*"
],
"webOrigins": [
  "http://localhost:5173",
  "http://<MINIKUBE_IP>:30080"
],
```

---

## Paso 6 – Actualizar la IP en el YAML de Keycloak

Edita el archivo: `k8s/08-keycloak.yaml`

Busca y actualiza las variables de entorno:

```yaml
- name: KC_HOSTNAME_URL
  value: "http://<MINIKUBE_IP>:30091"
- name: KC_HOSTNAME_ADMIN_URL
  value: "http://<MINIKUBE_IP>:30091"
```

---

## Paso 7 – Reconstruir y subir Frontend + Keycloak

```bash
docker login
# Usuario: robert912

# Reconstruir frontend con la nueva IP
cd D:\Proyecto_Universidad\TravelAgency\TravelAgency_MIS_frontend
npm run build
docker build -t robert912/travelmicro-frontend:latest .
docker push robert912/travelmicro-frontend:latest

# Reconstruir keycloak con el realm actualizado
cd D:\Proyecto_Universidad\TravelAgency\TravelAgency_MIS_MicroService
docker build -t robert912/keycloak:latest ./keycloak
docker push robert912/keycloak:latest
```

> Los microservicios del backend (m1–m7, config-server, eureka-server, api-gateway)
> **NO necesitan rebuild** — sus imágenes ya están en Docker Hub y no dependen de la IP.

---

## Paso 8 – Desplegar en Kubernetes

```bash
kubectl apply -f k8s/
```

---

## Paso 9 – Verificar que todo está corriendo

```bash
kubectl get pods -n travel-agency
```

Espera hasta que todos los pods muestren `Running 1/1` (3–7 minutos).

---

## Paso 10 – Acceder a la aplicación

| Componente | URL |
|---|---|
| **Frontend** | `http://<MINIKUBE_IP>:30080` |
| **API Gateway** | `http://<MINIKUBE_IP>:30090` |
| **Keycloak** | `http://<MINIKUBE_IP>:30091` |

---

## Resumen de qué cambia entre equipos

| Qué | Archivo | Acción |
|---|---|---|
| URL del backend en el frontend | `TravelAgency_MIS_frontend/.env` | Editar IP |
| Redirect URIs de Keycloak | `keycloak/travel-realm-realm.json` | Editar IP |
| Hostname de Keycloak en K8s | `k8s/08-keycloak.yaml` | Editar IP |
| Imagen frontend | Docker Hub | Rebuild + push |
| Imagen keycloak | Docker Hub | Rebuild + push |
| Microservicios backend | Docker Hub | **Sin cambios** |

---

## Si Minikube ya estaba instalado con otro driver

```powershell
# En PowerShell como Administrador:
minikube delete --all --purge
minikube start --driver=hyperv --memory=4096 --cpus=4
```
