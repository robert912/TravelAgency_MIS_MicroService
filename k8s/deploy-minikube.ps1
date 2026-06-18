# ============================================================
#  deploy-minikube.ps1
#  Despliega toda la aplicación Travel Agency en minikube
#  Ejecutar desde la carpeta TravelAgency_MIS_MicroService:
#    cd TravelAgency_MIS_MicroService
#    .\k8s\deploy-minikube.ps1
# ============================================================

$ErrorActionPreference = "Stop"
$FRONTEND_DIR = "..\TravelAgency_MIS_frontend"

Write-Host "`n=== 1. Verificando minikube ===" -ForegroundColor Cyan
minikube status
if ($LASTEXITCODE -ne 0) {
    Write-Host "Iniciando minikube..." -ForegroundColor Yellow
    minikube start --memory=6144 --cpus=4
}

# Apuntar Docker al daemon de minikube (las imágenes se construyen dentro de minikube)
Write-Host "`n=== 2. Configurando Docker -> minikube ===" -ForegroundColor Cyan
& minikube -p minikube docker-env | Invoke-Expression

# Obtener IP de minikube
$MINIKUBE_IP = minikube ip
Write-Host "minikube IP: $MINIKUBE_IP" -ForegroundColor Green

# URLs externas
$API_URL      = "http://${MINIKUBE_IP}:30090"
$KEYCLOAK_URL = "http://${MINIKUBE_IP}:30091"

Write-Host "`n=== 3. Construyendo imágenes de microservicios ===" -ForegroundColor Cyan
docker compose build
Write-Host "Microservicios built ✓" -ForegroundColor Green

Write-Host "`n=== 4. Construyendo imagen de Keycloak (con realm embebido) ===" -ForegroundColor Cyan
docker build -t travel/keycloak:latest -f keycloak/Dockerfile keycloak/
Write-Host "Keycloak built ✓" -ForegroundColor Green

Write-Host "`n=== 5. Construyendo frontend con URLs de minikube ===" -ForegroundColor Cyan
Push-Location $FRONTEND_DIR
@"
VITE_API_URL=$API_URL
VITE_KEYCLOAK_URL=$KEYCLOAK_URL
VITE_KEYCLOAK_REALM=travel-realm
VITE_KEYCLOAK_CLIENT_ID=travel-frontend
"@ | Set-Content .env.production
Write-Host ".env.production escrito con IP $MINIKUBE_IP" -ForegroundColor Green
npm run build
Pop-Location
docker build -t travel/frontend:latest $FRONTEND_DIR
Write-Host "Frontend built ✓" -ForegroundColor Green

Write-Host "`n=== 6. Aplicando manifests de Kubernetes ===" -ForegroundColor Cyan
kubectl apply -f k8s/
Write-Host "Manifests aplicados ✓" -ForegroundColor Green

Write-Host "`n=== 7. Actualizando redirectUris de Keycloak en el realm ===" -ForegroundColor Yellow
Write-Host "IMPORTANTE: Una vez que Keycloak esté listo, entra a:"
Write-Host "  http://${MINIKUBE_IP}:30091  (admin / admin)"
Write-Host "  Realm: travel-realm -> Clients -> travel-frontend -> Settings"
Write-Host "  Agrega en 'Valid redirect URIs':  http://${MINIKUBE_IP}:30080/*"
Write-Host "  Agrega en 'Web origins':          http://${MINIKUBE_IP}:30080"

Write-Host "`n=== URLs de acceso ===" -ForegroundColor Green
Write-Host "  Frontend  : http://${MINIKUBE_IP}:30080"
Write-Host "  API Gateway: $API_URL"
Write-Host "  Keycloak  : $KEYCLOAK_URL"
Write-Host "  Eureka    : (interno, sin NodePort)"
Write-Host ""
Write-Host "Para ver el estado de los pods:"
Write-Host "  kubectl get pods -n travel-agency -w"
