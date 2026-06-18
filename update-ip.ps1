# update-ip.ps1
# Ejecutar en PowerShell como Administrador desde la raiz del proyecto backend
# Uso: .\update-ip.ps1

$BACKEND_DIR  = $PSScriptRoot
$FRONTEND_DIR = Resolve-Path (Join-Path $PSScriptRoot "..\TravelAgency_MIS_frontend")

# 1. Obtener IP actual de minikube
Write-Host "Obteniendo IP de minikube..." -ForegroundColor Cyan
$NEW_IP = minikube ip
if (-not $NEW_IP) { Write-Host "ERROR: No se pudo obtener la IP de minikube. Verifica que este corriendo." -ForegroundColor Red; exit 1 }
Write-Host "IP actual: $NEW_IP" -ForegroundColor Green

# 2. Detectar IP anterior en el .env del frontend
$envFile = "$FRONTEND_DIR\.env"
$envContent = Get-Content $envFile -Raw
if ($envContent -match 'VITE_API_URL=http://([\d.]+):') {
    $OLD_IP = $matches[1]
} else {
    $OLD_IP = "NONE"
}

if ($OLD_IP -eq $NEW_IP) {
    Write-Host "La IP no cambio ($NEW_IP). No se requiere rebuild." -ForegroundColor Yellow
    exit 0
}

Write-Host "IP anterior: $OLD_IP  ->  IP nueva: $NEW_IP" -ForegroundColor Yellow

# 3. Actualizar los 3 archivos
Write-Host "`nActualizando archivos..." -ForegroundColor Cyan

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

# 4. Rebuild frontend
Write-Host "`nReconstruyendo frontend..." -ForegroundColor Cyan
Set-Location $FRONTEND_DIR
npm run build
docker build -t robert912/travelmicro-frontend:latest .
docker push robert912/travelmicro-frontend:latest

# 5. Rebuild keycloak
Write-Host "`nReconstruyendo keycloak..." -ForegroundColor Cyan
Set-Location $BACKEND_DIR
docker build -t robert912/keycloak:latest ./keycloak
docker push robert912/keycloak:latest

# 6. Aplicar yamls
Write-Host "`nAplicando manifests en Kubernetes..." -ForegroundColor Cyan
kubectl apply -f "$BACKEND_DIR\k8s\"

Write-Host "`nListo. Accede en:" -ForegroundColor Green
Write-Host "  Frontend : http://$NEW_IP`:30080"
Write-Host "  API GW   : http://$NEW_IP`:30090"
Write-Host "  Keycloak : http://$NEW_IP`:30091"
