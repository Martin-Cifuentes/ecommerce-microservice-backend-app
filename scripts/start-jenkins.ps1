# Script para iniciar Jenkins en Docker
# Uso: .\scripts\start-jenkins.ps1 [puerto]

param(
    [int]$Port = 8080,
    [int]$AgentPort = 50000
)

Write-Host "🚀 Iniciando Jenkins en Docker..." -ForegroundColor Cyan
Write-Host ""

# Verificar si el puerto está en uso
$portInUse = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
if ($portInUse) {
    Write-Host "⚠️  El puerto $Port está en uso" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Opciones:" -ForegroundColor Yellow
    Write-Host "  1. Usar un puerto diferente (ej: 9090)"
    Write-Host "  2. Detener el servicio que usa el puerto $Port"
    Write-Host ""
    
    $choice = Read-Host "¿Quieres usar el puerto 9090? (S/N)"
    if ($choice -eq "S" -or $choice -eq "s") {
        $Port = 9090
        Write-Host "✅ Usando puerto $Port" -ForegroundColor Green
    } else {
        Write-Host "❌ Cancelado" -ForegroundColor Red
        exit 1
    }
}

# Verificar si ya existe un contenedor Jenkins
$existingContainer = docker ps -a --filter "name=jenkins" --format "{{.Names}}"
if ($existingContainer) {
    Write-Host "⚠️  Ya existe un contenedor Jenkins" -ForegroundColor Yellow
    $action = Read-Host "¿Eliminar el contenedor existente y crear uno nuevo? (S/N)"
    if ($action -eq "S" -or $action -eq "s") {
        Write-Host "🛑 Deteniendo contenedor existente..." -ForegroundColor Yellow
        docker stop jenkins 2>$null
        docker rm jenkins 2>$null
        Write-Host "✅ Contenedor eliminado" -ForegroundColor Green
    } else {
        Write-Host "🔄 Iniciando contenedor existente..." -ForegroundColor Yellow
        docker start jenkins
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Jenkins iniciado" -ForegroundColor Green
            Write-Host ""
            Write-Host "📝 Para ver la contraseña inicial:" -ForegroundColor Cyan
            Write-Host "   docker logs jenkins" -ForegroundColor White
            Write-Host ""
            Write-Host "🌐 Accede a: http://localhost:$Port" -ForegroundColor Cyan
        }
        exit 0
    }
}

# Crear volumen si no existe
Write-Host "📦 Creando volumen para datos persistentes..." -ForegroundColor Cyan
docker volume create jenkins_home 2>$null

# Iniciar Jenkins
Write-Host "🚀 Iniciando Jenkins en puerto $Port..." -ForegroundColor Cyan
$dockerRun = "docker run -d --name jenkins -p ${Port}:8080 -p ${AgentPort}:50000 -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts"

Invoke-Expression $dockerRun

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ Jenkins iniciado exitosamente!" -ForegroundColor Green
    Write-Host ""
    Write-Host "⏳ Esperando que Jenkins inicie (esto puede tomar 30-60 segundos)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 5
    
    Write-Host ""
    Write-Host "📝 Contraseña inicial de Jenkins:" -ForegroundColor Cyan
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
    docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>$null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "   (La contraseña aparecerá en unos segundos. Ejecuta: docker logs jenkins)" -ForegroundColor Yellow
    }
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
    Write-Host ""
    Write-Host "🌐 Accede a Jenkins en:" -ForegroundColor Cyan
    Write-Host "   http://localhost:$Port" -ForegroundColor White -BackgroundColor DarkBlue
    Write-Host ""
    Write-Host "📋 Próximos pasos:" -ForegroundColor Cyan
    Write-Host "   1. Abre http://localhost:$Port en tu navegador"
    Write-Host "   2. Ingresa la contraseña mostrada arriba"
    Write-Host "   3. Instala los plugins sugeridos"
    Write-Host "   4. Configura Jenkins según GUIA-PRUEBAS-JENKINS.md"
    Write-Host ""
    Write-Host "💡 Para ver los logs:" -ForegroundColor Cyan
    Write-Host "   docker logs -f jenkins" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "❌ Error al iniciar Jenkins" -ForegroundColor Red
    Write-Host ""
    Write-Host "💡 Soluciones:" -ForegroundColor Yellow
    Write-Host "   - Verifica que Docker esté corriendo: docker ps"
    Write-Host "   - Verifica que el puerto $Port esté disponible"
    Write-Host "   - Prueba con otro puerto: .\scripts\start-jenkins.ps1 -Port 9090"
}



