# ============================================================
#  deploy-minikube.ps1
#  1. Obtiene la IP de minikube
#  2. Actualiza archivos si la IP cambio
#  3. PARA minikube (libera RAM)
#  4. Hace build + push a Docker Hub
#  5. REINICIA minikube (misma IP)
#  6. Muestra comandos kubectl a ejecutar manualmente
#
#  Uso:
#    cd TravelAgency_MIS_MicroService
#    .\k8s\deploy-minikube.ps1
# ============================================================

$ErrorActionPreference = "Stop"
$BACKEND_DIR  = Split-Path $PSScriptRoot   # carpeta TravelAgency_MIS_MicroService
$FRONTEND_DIR = Resolve-Path (Join-Path $BACKEND_DIR "..\TravelAgency_MIS_frontend")

# ── 1. Obtener IP de minikube ─────────────────────────────────────────────────
Write-Host "`n=== 1. Obteniendo IP de minikube ===" -ForegroundColor Cyan
$NEW_IP = minikube ip 2>$null
if (-not $NEW_IP) {
    Write-Host "minikube no esta corriendo, iniciando..." -ForegroundColor Yellow
    minikube start
    $NEW_IP = minikube ip
}
Write-Host "IP actual: $NEW_IP" -ForegroundColor Green

# ── 2. Detectar IP anterior y actualizar archivos si cambio ──────────────────
$envFile = "$FRONTEND_DIR\.env"
$envContent = Get-Content $envFile -Raw
if ($envContent -match 'VITE_API_URL=http://([\d.]+):') {
    $OLD_IP = $matches[1]
} else {
    $OLD_IP = "NONE"
}

if ($OLD_IP -ne $NEW_IP) {
    Write-Host "`n=== 2. IP cambio ($OLD_IP -> $NEW_IP). Actualizando archivos ===" -ForegroundColor Yellow

    (Get-Content $envFile -Raw) -replace $OLD_IP, $NEW_IP |
        Set-Content $envFile -NoNewline
    Write-Host "  [OK] $envFile"

    $realmFile = "$BACKEND_DIR\keycloak\travel-realm-realm.json"
    (Get-Content $realmFile -Raw) -replace $OLD_IP, $NEW_IP |
        Set-Content $realmFile -NoNewline
    Write-Host "  [OK] $realmFile"

    $keycloakYaml = "$BACKEND_DIR\k8s\08-keycloak.yaml"
    (Get-Content $keycloakYaml -Raw) -replace $OLD_IP, $NEW_IP |
        Set-Content $keycloakYaml -NoNewline
    Write-Host "  [OK] $keycloakYaml"
} else {
    Write-Host "`n=== 2. IP sin cambios ($NEW_IP) ===" -ForegroundColor Green
}

# ── 3. Parar minikube para liberar RAM durante los builds ────────────────────
Write-Host "`n=== 3. Parando minikube para liberar RAM ===" -ForegroundColor Cyan
minikube stop
Write-Host "minikube detenido ✓" -ForegroundColor Green

# ── 4. Builds y push a Docker Hub ────────────────────────────────────────────
Write-Host "`n=== 4. Build + push frontend ===" -ForegroundColor Cyan
Set-Location $FRONTEND_DIR
npm run build
docker build -t robert912/travelmicro-frontend:latest .
docker push robert912/travelmicro-frontend:latest
Write-Host "Frontend pushed ✓" -ForegroundColor Green

Write-Host "`n=== 5. Build + push keycloak ===" -ForegroundColor Cyan
Set-Location $BACKEND_DIR
docker build -t robert912/keycloak:latest ./keycloak
docker push robert912/keycloak:latest
Write-Host "Keycloak pushed ✓" -ForegroundColor Green

# ── 5. Reiniciar minikube (recupera la misma IP) ──────────────────────────────
Write-Host "`n=== 6. Reiniciando minikube ===" -ForegroundColor Cyan
minikube start
Write-Host "minikube listo ✓" -ForegroundColor Green

# ── 6. Instrucciones finales ──────────────────────────────────────────────────
Write-Host "`n=== Listo. Ejecuta estos comandos para aplicar en Kubernetes: ===" -ForegroundColor Green
Write-Host ""
Write-Host "  kubectl apply -f k8s\" -ForegroundColor White
Write-Host "  kubectl rollout restart deployment/frontend deployment/keycloak -n travel-agency" -ForegroundColor White
Write-Host "  kubectl get pods -n travel-agency" -ForegroundColor White
Write-Host ""
Write-Host "URLs cuando los pods esten listos:"
Write-Host "  Frontend  : http://$NEW_IP`:30080"
Write-Host "  API GW    : http://$NEW_IP`:30090"
Write-Host "  Keycloak  : http://$NEW_IP`:30091"
